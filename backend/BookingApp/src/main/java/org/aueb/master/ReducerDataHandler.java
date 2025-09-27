package org.aueb.master;
import org.aueb.entities.*;
import org.aueb.util.Constants;
import org.aueb.util.Response;
import org.aueb.worker.WorkerInfo;
import org.aueb.util.JSONReaderWriter;
import org.aueb.util.SocketUtils;
import org.aueb.worker.WorkerUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;


/**
 * Handles incoming data from a Reducer in a distributed system setup.
 * It is responsible for processing the results from the reducer, identifying the correct session based on the session ID provided in the response,
 * and forwarding the processed data to the appropriate client session.
 */
public class ReducerDataHandler implements Runnable {
    private Socket reducerSocket;
    Map<Long, Session> sessions;
    Logger logger = LoggerFactory.getLogger(ReducerDataHandler.class);

    /**
     * Constructor for the ReducerDataHandler class.
     * @param reducerSocket The socket connected to the reducer to receive data.
     * @param sessions A map of sessions that allows the handler to forward the received data to the correct client based on session ID.
     */
    public ReducerDataHandler(Socket reducerSocket, Map<Long, Session> sessions) {
        this.reducerSocket = reducerSocket;
        this.sessions = sessions;
    }

    public void run() {
        try {

            DataInputStream reducerInput = new DataInputStream(reducerSocket.getInputStream()) ;

                String results = reducerInput.readUTF();  // Read the aggregated results from the reducer
                Response response = Response.fromJSONString(results);

                // Retrieve the client socket from the sessions map using the session ID from the response
                Socket clientSocket = sessions.get(response.getSessionId()).getSocket();

                // Create a data output stream associated with the client's socket
                DataOutputStream clientOut = SocketUtils.createDataOutputStream(clientSocket);

                // If the output stream is not null, send the response as a JSON string to the client
            if (clientOut != null) {
                SocketUtils.safeSend(clientOut, response.toJSONString());
            }

        } catch (IOException e) {
            System.out.println("Error in DataFromReducerHandler: " + e.getMessage());


        } finally {
            try {
                if (reducerSocket != null) reducerSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing reducer socket: " + e.getMessage());
            }
        }
    }
}