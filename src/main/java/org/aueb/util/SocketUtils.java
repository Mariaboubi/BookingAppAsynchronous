package org.aueb.util;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class SocketUtils {
    public static void safeSend(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String safeReceive(DataInputStream in) {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataInputStream createDataInputStream(Socket socket) {
        try {
            return new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static DataOutputStream createDataOutputStream(Socket socket) {
        try {
            return new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Socket createSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (IOException | NoSuchElementException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

}
