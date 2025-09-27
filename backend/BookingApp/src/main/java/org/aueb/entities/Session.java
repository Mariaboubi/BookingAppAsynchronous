package org.aueb.entities;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a session for a connected client.
 * Each session is identified by a unique ID and is associated with a specific socket connection.
 */
public class Session  {
    private static final AtomicInteger id_counter = new AtomicInteger(0); // counter for session ids
    private final long id; // session id
    private final Socket socket; // socket of the session

    /**
     * Constructs a new Session with a unique ID and associates it with a specific socket.
     * @param socket The socket that is linked to this session. It is used to communicate with the client.
     */
    public Session(Socket socket) {
        this.id = id_counter.incrementAndGet();
        this.socket = socket;
    }

    public long getId() {return id;}

    public Socket getSocket() {
        return socket;
    }
}
