package org.lantern.http;

import org.lantern.LanternService;

public interface IJettyLauncher extends LanternService {

    public abstract void start(int port);

}