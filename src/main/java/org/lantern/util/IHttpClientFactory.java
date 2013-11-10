package org.lantern.util;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

public interface IHttpClientFactory {

    /**
     * Returns a proxied client if we have access to a proxy in get mode.
     * 
     * @return The proxied {@link HttpClient} if available in get mode, 
     * otherwise an unproxied client.
     * @throws IOException If we could not obtain a proxied client.
     */
    public abstract HttpClient newClient() throws IOException;

    public abstract HttpClient newDirectClient();

    public abstract HttpHost newProxyBlocking() throws InterruptedException;

    public abstract HttpClient newClient(HttpHost proxy, boolean addProxy);

}