package org.lantern.state;

import java.io.IOException;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.jackson.map.annotate.JsonView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.lantern.annotation.Keep;
import org.lantern.state.Model.Run;

/**
 * Class containing data about the users system.
 */
@Keep
public class SystemData {

    private final String os;
    private long bytesFree;
    private final long memory = 50; // TODO: make this work again
    private double[] screenSize;
    
    public SystemData() {
        
        if (SystemUtils.IS_OS_MAC_OSX) {
            os = "osx";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            os = "windows";
        } else {
            os = "ubuntu";
        }
        try {
            bytesFree = FileSystemUtils.freeSpaceKb() * 1024;
        } catch (final IOException e) {
            bytesFree = 1000000000L;
        }
//        final OperatingSystemMXBean operatingSystemMXBean = 
//            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        memory = operatingSystemMXBean.getTotalPhysicalMemorySize();
    }
    
    @JsonView({Run.class})
    public String getLang() {
        return SystemUtils.USER_LANGUAGE;
    }
    
    @JsonView({Run.class})
    public String getOs() {
        return os;
    }

    @JsonView({Run.class})
    public String getVersion() {
        return SystemUtils.OS_VERSION;
    }

    @JsonView({Run.class})
    public String getArch() {
        return SystemUtils.OS_ARCH;
    }

    @JsonView({Run.class})
    public long getBytesFree() {
        return bytesFree;
    }

    @JsonView({Run.class})
    public long getMemory() {
        return memory;
    }

    @JsonView({Run.class})
    public String getJava() {
        return SystemUtils.JAVA_VERSION;
    }

    @JsonView({Run.class})
    public double[] getScreenSize() {
        if (this.screenSize != null) {
            return this.screenSize;
        }
        final double[] ss = new double[2];
        try {
            Monitor primary = new Display().getPrimaryMonitor();
            Rectangle bounds = primary.getBounds();
            ss[0] = bounds.width;
            ss[1] = bounds.height;
            this.screenSize = ss;
        } catch (final Exception e) {
            // We might not be able to get the screen size if we're running
            // in headless mode, for example.
            this.screenSize = new double[2];
        }
        return this.screenSize;
    }
}
