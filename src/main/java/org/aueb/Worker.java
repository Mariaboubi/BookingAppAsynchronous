package org.aueb;

import org.aueb.entities.Hotel;
import org.aueb.util.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private final ArrayList<Hotel> hotels;
    private ServerSocket serverSocket;
    private final long id;

    private  Socket reducerSocket;
    private  DataInputStream inReducer ;
    private DataOutputStream outReducer;



    public Worker(int id, int port) {
        this.id = id;
        hotels = new ArrayList<>();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Worker <worker ID> <port number>");
            return;
        }
        int workerId = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        logger.info("Initializing Worker " + workerId + " on port " + port);
        Worker worker = new Worker(workerId, port);
        worker.connectToReducer();
        worker.openServer(port);
    }

    void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket connection = serverSocket.accept();
                Thread t = new WorkerConnectionHandler(connection, hotels, id, inReducer, outReducer);
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

    public long getId() {
        return id;
    }

    private void connectToReducer() {
        int reducerPort = 7000;
        reducerSocket = SocketUtils.createSocket("localhost", reducerPort);
        inReducer = SocketUtils.createDataInputStream(reducerSocket);
        outReducer = SocketUtils.createDataOutputStream(reducerSocket);

    }
}

