package org.lantern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.lantern.http.JettyLauncher;
import org.lantern.state.StaticSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks for an existing Lantern version and stops it.
 */
public class LanternChecker {

    private final Logger LOG = 
            LoggerFactory.getLogger(LanternChecker.class);

    /**
     * We use this to obtain a read and write exclusive lock on the file
     * indicating Lantern is running.
     */
    private RandomAccessFile randomAccessFile;
    private FileChannel fileChannel;
    
    private final AtomicBoolean releasingLock = 
            new AtomicBoolean(false);

    private final AtomicReference<FileLock> atomicLockRef = 
            new AtomicReference<FileLock>();

    public void stopExistingLantern() {
        addShutdownHook();
        configureLockFile();
        if (shouldStopExisting()) {
            stopExisting();
        }
    }
    
    private boolean shouldStopExisting() {
        final boolean shouldStopExisting;

        FileLock tempLock = grabLock();
        if (tempLock == null) {
            shouldStopExisting = true;
        } else {
            shouldStopExisting = false;
            atomicLockRef.set(tempLock);
        }
        return shouldStopExisting;
    }
    
    private void addShutdownHook() {
        final Runnable shutDownRunner = new Runnable() {
            @Override
            public void run() {
                synchronized (releasingLock) {
                    System.out.println("Launcher caught shutdown call!!!\n\n\n"
                            + "***********************");
                    releasingLock.set(true);
                    final FileLock lock = atomicLockRef.get();
                    if (lock != null) {
                        try {
                            System.out.println("Releasing lock...");
                            lock.release();
                        } catch (final IOException e) {
                            LOG.error("Could not release lock?", e);
                        }
                    }
                    try {
                        fileChannel.close();
                    } catch (final IOException e) {
                        LOG.debug("Exception closing channel", e);
                    }
                    try {
                        randomAccessFile.close();
                    } catch (final IOException e) {
                        LOG.debug("Exception closing RAF", e);
                    }
                }
            }
        };
        final Thread hook = new Thread(shutDownRunner,
                "Lantern-Shutdown-Lock-File-Thread");
        Runtime.getRuntime().addShutdownHook(hook);
    }
    
    private void configureLockFile() {
        final File instanceLock = new File(LanternClientConstants.CONFIG_DIR,
                "lantern-running.lck");
        LOG.debug("Lock file is: {}", instanceLock);
        if (!instanceLock.isFile()) {
            LOG.debug("No existing process file");
            try {
                instanceLock.createNewFile();
            } catch (final IOException e) {
                LOG.warn("Could not touch process running flag file", e);
            }
        }
        try {
            randomAccessFile = new RandomAccessFile(instanceLock, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (final FileNotFoundException e) {
            // This should never happen.
            LOG.error("FNFE on file we've checked for?", e);
        }
    }
    
    private FileLock grabLock() {
        try {
            final FileLock lock = fileChannel.tryLock();
            LOG.debug("Returning lock: {}", lock);
            return lock;
        } catch (final OverlappingFileLockException e) {
            LOG.debug("Overlapping file lock", e);
        } catch (final ClosedChannelException e) {
            LOG.error("Closed channel?", e);
        } catch (final NonWritableChannelException e) {
            LOG.error("Closed channel?", e);
        } catch (final IOException e) {
            LOG.error("IO Error getting lock?", e);
        }
        return null;
    }

    private void stopExisting() {
        LOG.debug("Sending message to existing app.");
        final Runnable runner = new Runnable() {

            @Override
            public void run() {
                Response response = null;
                try {
                    response = Request
                        .Post(StaticSettings.getLocalEndpoint()+"/"+JettyLauncher.apiPath()+"/close")
                        .connectTimeout(4000)
                        .socketTimeout(8000)
                        .execute();
                    final HttpResponse httpResponse = response.returnResponse();
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        LOG.info("Closed peer");
                        return;
                    } else {
                        LOG.error("Unable to shutdown other Lantern version: {}", 
                            EntityUtils.toString(httpResponse.getEntity()));
                    }
                } catch (final IOException e) {
                    LOG.error("Exception trying to register peer", e);
                } finally {
                    if (response != null) {
                        response.discardContent();
                    }
                }
            }
        };
        final Thread t = new Thread(runner, "Stop-Lantern-Thread");
        t.setDaemon(true);
        t.start();
        
    }
}
