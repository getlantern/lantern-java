package org.lantern.endpoints;

import java.io.IOException;
import java.util.List;

import org.lantern.state.ClientFriend;

public interface IFriendApi {

    /**
     * This method lists all the entities inserted in datastore. It uses HTTP
     * GET method.
     * 
     * @return List of all entities persisted.
     * @throws IOException If there's an error making the call to the server.
     */
    public abstract List<ClientFriend> listFriends() throws IOException;

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     * 
     * @param id The primary key of the java bean.
     * @return The entity with primary key id.
     * @throws IOException If there's an error making the call to the server.
     */
    public abstract ClientFriend getFriend(long id) throws IOException;

    /**
     * This inserts the entity into App Engine datastore. It uses HTTP POST
     * method.
     * 
     * @param task The entity to be inserted.
     * @return The inserted entity.
     * @throws IOException If there's an error making the call to the server.
     */
    public abstract ClientFriend insertFriend(ClientFriend friend)
            throws IOException;

    /**
     * This method is used for updating a entity. It uses HTTP PUT method.
     * 
     * @param friend The entity to be updated.
     * @return The updated entity.
     * @throws IOException If there's an error making the call to the server.
     */
    public abstract ClientFriend updateFriend(ClientFriend friend)
            throws IOException;

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     * 
     * @param id The primary key of the entity to be deleted.
     * @return The deleted entity.
     * @throws IOException If there's an error making the call to the server.
     */
    public abstract void removeFriend(long id) throws IOException;

}