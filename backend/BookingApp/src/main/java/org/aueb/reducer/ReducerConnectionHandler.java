package org.aueb.reducer;

import org.aueb.util.Constants;
import org.aueb.util.JSONUtils;
import org.aueb.util.Response;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ReducerConnectionHandler class is responsible for handling connections from worker nodes.
 * It receives data from worker nodes, aggregates it, and sends it back to the Master.
 */
public class ReducerConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReducerConnectionHandler.class); // Logger for the ReducerConnectionHandler class
    private DataInputStream inputStream; // Input stream to read data from the worker node
    private Socket connection; // Socket connection to the worker node
    private final int totalWorkers; // Total number of worker nodes expected to connect
    private final AtomicInteger completedWorkers; // Counter to keep track of completed worker nodes
    private final List<String> aggregatedResults; // List to store aggregated results from worker nodes


    /**
     * Constructor for ReducerConnectionHandler.
     *
     * @param connection        Socket connection to the worker node.
     * @param aggregatedResults List to store results from workers.
     * @param totalWorkers      Total number of workers that must complete before aggregation is done.
     * @param completedWorkers  Counter for tracking completed workers.
     */
    public ReducerConnectionHandler(
            Socket connection,
            List<String> aggregatedResults,
            int totalWorkers,
            AtomicInteger completedWorkers) {
        this.connection = connection;
        this.totalWorkers = totalWorkers;
        this.completedWorkers = completedWorkers;
        this.aggregatedResults = aggregatedResults;
        try {
            // Create input stream for the worker connection
            inputStream = SocketUtils.createDataInputStream(connection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main execution method for the thread, handling all logic to receive data,
     * aggregate it, and coordinate with other instances.
     */
    public void run() {
        try {
            logger.info("New client connected: " + this.connection.getRemoteSocketAddress());
            serve();
        } catch (IOException ignored) {
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (connection != null && !connection.isClosed()) connection.close();
            logger.info("Connection from worker closed successfully.");
        } catch (Exception e) {
            logger.error("Error closing connection: ", e);
        }
    }

    /**
     * Method to serve the worker node connection, receiving data and aggregating it.
     */
    private void serve() throws IOException {
        while (true) {
            try {
                Response workerResponse = Response.fromJSONString(SocketUtils.safeReceive(inputStream));

                synchronized (aggregatedResults) {

                    aggregatedResults.add(workerResponse.getBody().toJSONString());
                }

                int localCompleted = completedWorkers.incrementAndGet();

                if (localCompleted < totalWorkers) {
                    synchronized (completedWorkers) {
                        // Wait for the last thread only if not all workers are done
                        while (completedWorkers.get() < totalWorkers) {
                            completedWorkers.wait(); // Wait for other workers to complete
                        }
                    }
                } else {
                    synchronized (completedWorkers) {
                        completedWorkers.notifyAll();  // Notify all waiting threads if this is the last worker
                    }
                }
                if (localCompleted == totalWorkers) {
                    JSONObject finalResults;

                    // Reservations by area get aggregated as a map
                    if(workerResponse.getBody().get("option").equals("4") && workerResponse.getBody().get("user_role").equals("Manager")){


                        finalResults = mergeWorkerResultsMap();
                        Response aggregatedResponse;

                        assert finalResults.get("results") != null; // Ensure the 'result' key exists in the final results



                        if (((JSONObject) finalResults.get("results")).isEmpty()) {
                            aggregatedResponse = new Response(workerResponse.getSessionId(), "", Response.Status.NOT_FOUND, "No results found", null);
                        } else {

                            aggregatedResponse = new Response(workerResponse.getSessionId(), "", Response.Status.SUCCESS, "Found results", finalResults);
                        }

                        sendResultsToMaster(aggregatedResponse);
                    }else{
                        finalResults = mergeWorkerResults();
                        Response aggregatedResponse;


                        assert finalResults.get("results") != null; // Ensure the 'results' key exists in the final results

                        if (((JSONArray) finalResults.get("results")).isEmpty()) {

                            aggregatedResponse = new Response(workerResponse.getSessionId(), "", Response.Status.NOT_FOUND, "No results found", null);

                        } else {
                            aggregatedResponse = new Response(workerResponse.getSessionId(), "", Response.Status.SUCCESS, "Found results", finalResults);
                        }

                        sendResultsToMaster(aggregatedResponse);// Send the results to the Master
                    }


                    completedWorkers.set(0);
                }
            } catch (InterruptedException e) {
                logger.error("Error waiting for other workers", e);
            }
        }
    }

    /**
     * Merges results from multiple workers into a single JSON object for reservations by area.
     *
     * @return A JSON object containing the merged results.
     */
    private JSONObject mergeWorkerResultsMap() {

        // Create a JSON object to store the merged results
        JSONObject mergedResults = new JSONObject();
        Map<String, Integer> totalReservationsByArea = new HashMap<>();

        synchronized (aggregatedResults) {
            // Iterate over each result string in the aggregated results list
            for (String result : aggregatedResults) {
                // Parse the JSON string to a JSONObject
                JSONObject workerData = JSONUtils.parseJSONString(result);

                // Extract the 'result' object from the parsed JSON object
                JSONObject workerResults = (JSONObject) workerData.get("result");

                // Check if the workerResults object is not null and iterate over it
                if (workerResults != null) {
                    for (Object key : workerResults.keySet()) {
                        String area = (String) key;
                        int count = ((Long) workerResults.get(area)).intValue();

                        // Aggregate the results by adding counts from different workers
                        totalReservationsByArea.put(area, totalReservationsByArea.getOrDefault(area, 0) + count);
                    }
                }
            }
        }
        // Convert the totalReservationsByArea map to a JSONObject
        JSONObject result = new JSONObject();
        result.putAll(totalReservationsByArea);

        // Assign the consolidated results object to the 'result' key in the final JSON object
        mergedResults.put("results", result);


        // Return the JSON object containing all merged results
        return mergedResults;

    }

    /**
     * Merges results from multiple workers into a single JSON array.
     *
     * @return A JSON object containing the merged results.
     */
    private JSONObject mergeWorkerResults() {
        // Create a JSON object to store the merged results
        JSONObject mergedResults = new JSONObject();
        JSONArray mergedResultsArray = new JSONArray();

        synchronized (aggregatedResults) {
            // Iterate over each result string in the aggregated results list
            for (String result : aggregatedResults) {
                // Parse the JSON string to a JSONObject
                JSONObject workerData = JSONUtils.parseJSONString(result);
                // Extract the 'result' field from the parsed JSON object
                Object resultField = workerData.get("result");

                if (resultField instanceof JSONArray) {
                    // If the result field is a JSONArray, add all its elements to mergedResultsArray
                    mergedResultsArray.addAll((JSONArray) resultField);
                } else if (resultField instanceof JSONObject) {
                    // If the result field is a JSONObject, add it to mergedResultsArray
                    mergedResultsArray.add(resultField);
                } else {
                    // Handle the case where resultField is neither JSONArray nor JSONObject (optional)
                    // Log or throw an exception as needed
                    System.err.println("Unexpected result type: " + resultField.getClass().getName());
                }
            }
        }

        // Assign the consolidated results array to the 'results' key in the final JSON object
        mergedResults.put("results", mergedResultsArray);

        // Return the JSON object containing all merged results
        return mergedResults;
    }




    /**
     * Sends the aggregated results to the master server.
     *
     * @param aggregatedResponse The aggregated response to send.
     */
    private void sendResultsToMaster(Response aggregatedResponse) {
        Socket masterSocket = null;
        DataOutputStream outputStream = null;
        try {
            // Connect to the master's reducer response port
            masterSocket = new Socket("localhost", Constants.REDUCER_TO_MASTER_PORT);
            outputStream = new DataOutputStream(masterSocket.getOutputStream());
            // Send the merged results as a string

            SocketUtils.safeSend(outputStream, aggregatedResponse.toJSONString());

        } catch (IOException e) {
            logger.error("Error sending results to Master", e);
        } finally {
            // Clean up resources
            try {
                if (outputStream != null) outputStream.close();
                if (masterSocket != null) masterSocket.close();
                aggregatedResults.clear();
            } catch (IOException ex) {
                logger.error("Error closing resources", ex);
            }
        }
    }

}
