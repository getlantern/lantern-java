package org.lantern.http;

public interface IJettyLauncher {

    public abstract void start();

    public abstract void start(int port);

    public abstract void stop();

}