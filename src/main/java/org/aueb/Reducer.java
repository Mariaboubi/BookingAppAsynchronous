package org.aueb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Reducer {
    private ServerSocket serverSocket;

    private static final Logger logger = LoggerFactory.getLogger(Reducer.class);

    public static void main(String[] args) {
        int port = 7000;

        Reducer reducer = new Reducer();
        reducer.openServer(port);
    }


    void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

            logger.info("Reducer started on port " + port);
            while (true) {
                Socket connection = serverSocket.accept();
                Thread t = new ReducerConnectionHandler(connection);
                t.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
