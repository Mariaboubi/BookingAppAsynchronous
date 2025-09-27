package org.aueb.reducer;

import org.aueb.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Reducer class aggregates results from multiple worker nodes and processes them.
 * It listens for connections from worker nodes, receives their computed data, and combines it into a final result.
 */
public class Reducer {
    private ServerSocket serverSocket; // Server socket to accept connections from worker nodes
    private final int numWorkers; // Total number of worker nodes expected to connect
    private final AtomicInteger completedWorkers = new AtomicInteger(0); // Counter to keep track of completed worker nodes
    private final List<String> aggregatedResults = Collections.synchronizedList(new ArrayList<>()); // List to store aggregated results from worker nodes
    private static final Logger logger = LoggerFactory.getLogger(Reducer.class); // Logger for the Reducer class

    public static void main(String[] args) {
        int port = 7000;
        int numWorkers = Constants.NUM_WORKER_NODES;

        Reducer reducer = new Reducer(numWorkers);
        reducer.openServer(port);
    }


    /**
     * Constructor for the Reducer class.
     *
     * @param numWorkers Total number of worker nodes expected to connect.
     */
    public Reducer(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    /**
     * Opens a server socket and listens for connections from worker nodes.
     * For each connection, a new thread is created to handle the connection.
     * @param port Port number to listen on.
     */
    void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Reducer started on port " + port);

            while (true) {
                Socket workerConnection = serverSocket.accept(); // Accept connection from worker node
                // Create a new thread to handle the connection
                Runnable r = new ReducerConnectionHandler(workerConnection, aggregatedResults, numWorkers, completedWorkers);
                Thread reducerThread = new Thread(r);
                reducerThread.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                serverSocket.close(); // Ensure the server socket is properly closed
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}