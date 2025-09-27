package org.aueb.util;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Utility class providing static methods to simplify common socket and I/O operations.
 * This includes safely sending and receiving messages, and creating data streams from sockets.
 */
public class SocketUtils {

    /**
     * Sends a message safely through a DataOutputStream.
     * Ensures that the message is written and flushed without needing to handle IOExceptions externally.
     *
     * @param out The DataOutputStream through which the message will be sent.
     * @param message The String message to send.
     * @throws RuntimeException If an IOException occurs during writing to the output stream.
     */
    public static void safeSend(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Receives a UTF-encoded string message from a DataInputStream safely.
     * This method abstracts the IOException handling, simplifying method calls.
     *
     * @param in The DataInputStream from which to read the message.
     * @return The received String message.
     * @throws IOException If an error occurs during reading from the input stream.
     */
    public static String safeReceive(DataInputStream in) throws IOException {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Creates a DataInputStream from a given Socket's input stream.
     * This method handles the IOException internally, throwing a RuntimeException if an error occurs.
     *
     * @param socket The Socket from which the input stream is created.
     * @return A new DataInputStream object.
     * @throws RuntimeException If an IOException occurs when retrieving the input stream from the socket.
     */
    public static DataInputStream createDataInputStream(Socket socket) {
        try {
            return new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a DataOutputStream from a given Socket's output stream.
     * Similar to createDataInputStream, it handles IOExceptions internally.
     *
     * @param socket The Socket from which the output stream is created.
     * @return A new DataOutputStream object.
     * @throws RuntimeException If an IOException occurs when retrieving the output stream from the socket.
     */
    public static DataOutputStream createDataOutputStream(Socket socket) {
        try {
            return new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns a new Socket connected to the specified host and port.
     * Handles various exceptions by throwing a RuntimeException, simplifying error handling for the caller.
     *
     * @param host The hostname to connect to.
     * @param port The port number on the host to connect to.
     * @return A new Socket connected to the specified host and port.
     * @throws RuntimeException If an IOException, NoSuchElementException, or IllegalArgumentException occurs during socket creation.
     */
    public static Socket createSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (IOException | NoSuchElementException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

}
