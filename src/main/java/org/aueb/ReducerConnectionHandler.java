package org.aueb;

import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReducerConnectionHandler extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(ReducerConnectionHandler.class);
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket connection;
    public ReducerConnectionHandler(Socket connection){
        this.connection = connection;
        try {
            // Create input and output streams for the client
            inputStream = SocketUtils.createDataInputStream(connection);
            outputStream = SocketUtils.createDataOutputStream(connection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            logger.info("New client connected: " + this.connection.getRemoteSocketAddress());
            serve();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close(); // Ensure the connection is properly closed
                }
            } catch (IOException e) {
                logger.error("Error closing connection", e);
            }
        }
    }
    public void serve() {
        while (true) {
            JSONArray request = receiveRequest();
        }
    }
    public JSONArray receiveRequest() {
        String request = SocketUtils.safeReceive(this.inputStream);
        logger.info("Received request: " + request);

        return JSONUtils.parseJSONArray(request);
    }
}
