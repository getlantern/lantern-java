package org.lantern.oauth;

import java.io.IOException;

import org.apache.http.client.HttpClient;

import com.google.api.client.auth.oauth2.TokenResponse;

public interface IOauthUtils {

    /**
     * Obtains the oauth tokens. Note the refresh token should already be
     * set when this is called. This will attempt to obtain the tokens directly
     * and will then use a proxy if necessary.
     * 
     * @return The tokens.
     * @throws IOException If we cannot access the tokens either directory or
     * through a fallback proxy.
     */
    public abstract TokenResponse oauthTokens() throws IOException;

    public abstract TokenResponse oauthTokens(HttpClient httpClient,
            String refresh)
            throws IOException;

    public abstract String postRequest(String endpoint, String json)
            throws IOException;

    public abstract String getRequest(String endpoint) throws IOException;

    public abstract String deleteRequest(String endpoint) throws IOException;

    public abstract String accessToken(HttpClient httpClient)
            throws IOException;

}