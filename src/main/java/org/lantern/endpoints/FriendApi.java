package org.lantern.endpoints;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.lantern.JsonUtils;
import org.lantern.LanternClientConstants;
import org.lantern.LanternUtils;
import org.lantern.oauth.OauthUtils;
import org.lantern.state.ClientFriend;
import org.lantern.state.ClientFriends;
import org.lantern.state.Friend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * API for accessing the remote friends endpoint on the controller.
 */
public class FriendApi implements IFriendApi {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final String BASE = 
        LanternClientConstants.CONTROLLER_URL + "/_ah/api/friend/v1/friend/";
    
    /**
     * We store this separately because it does internal caching that can
     * speed things up on subsequent calls, or so they say.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    private final OauthUtils oauth;
    
    @Inject
    public FriendApi(final OauthUtils oauth) {
        this.oauth = oauth;
    }
    
    /* (non-Javadoc)
     * @see org.lantern.endpoints.IFriendApi#listFriends()
     */
    @Override
    public List<ClientFriend> listFriends() throws IOException {
        if (LanternUtils.isFallbackProxy()) {
            log.debug("Ignoring friends call from fallback");
            return Collections.emptyList();
        }
        final String url = BASE+"list";

        final String all = this.oauth.getRequest(url);
        final ClientFriends friends;
        try {
            friends = mapper.readValue(all, ClientFriends.class);
        } catch (final JsonParseException e) {
            log.error("Could not parse json in body: "+all, e);
            throw new IOException("Could not parse friends!", e);
        }
        
        final List<ClientFriend> list = friends.getItems();
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /* (non-Javadoc)
     * @see org.lantern.endpoints.IFriendApi#getFriend(long)
     */
    @Override
    public ClientFriend getFriend(final long id) throws IOException {
        if (LanternUtils.isFallbackProxy()) {
            log.debug("Ignoring friends call from fallback");
            return null;
        }
        final String url = BASE+"get/"+id;
        final String content = this.oauth.getRequest(url);
        try {
            final ClientFriend read =
                    mapper.readValue(content, ClientFriend.class);
            return read;
        } catch (final JsonParseException e) {
            log.error("Could not parse friend in body: "+content, e);
            throw new IOException("Could not parse friends!", e);
        }
    }

    /* (non-Javadoc)
     * @see org.lantern.endpoints.IFriendApi#insertFriend(org.lantern.state.ClientFriend)
     */
    @Override
    public ClientFriend insertFriend(final ClientFriend friend)
            throws IOException {
        if (LanternUtils.isFallbackProxy()) {
            log.debug("Ignoring friends call from fallback");
            return friend;
        }
        log.debug("Inserting friend: {}", friend);
        final String url = BASE+"insert";
        return post(url, friend);
    }

    /* (non-Javadoc)
     * @see org.lantern.endpoints.IFriendApi#updateFriend(org.lantern.state.ClientFriend)
     */
    @Override
    public ClientFriend updateFriend(final ClientFriend friend) throws IOException {
        if (LanternUtils.isFallbackProxy()) {
            log.debug("Ignoring friends call from fallback");
            return friend;
        }
        log.debug("Updating friend: {}", friend);
        final String url = BASE+"update";
        return post(url, friend);
    }

    private ClientFriend post(final String url, final Friend friend) 
            throws IOException {
        final String json = JsonUtils.jsonify(friend);
        final String content = this.oauth.postRequest(url, json);
        try {
            final ClientFriend read = mapper.readValue(content, ClientFriend.class);
            return read;
        } catch (final JsonParseException e) {
            log.error("Could not parse friend in body: "+content, e);
            throw new IOException("Could not parse friends!", e);
        }
    }

    /* (non-Javadoc)
     * @see org.lantern.endpoints.IFriendApi#removeFriend(long)
     */
    @Override
    public void removeFriend(final long id) throws IOException {
        if (LanternUtils.isFallbackProxy()) {
            log.debug("Ignoring friends call from fallback");
            return;
        }
        final String url = BASE+"remove/"+id;
        
        // The responses to this simply return no entity body (204 No Content).
        this.oauth.deleteRequest(url);
    }

}