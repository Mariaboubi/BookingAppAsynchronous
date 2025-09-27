package org.aueb.master;

import org.aueb.entities.*;
import org.aueb.util.*;
import org.aueb.worker.WorkerInfo;
import org.aueb.worker.WorkerUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Handles network communication for a single client session in a hotel booking system.
 * This handler is responsible for processing client requests, performing user authentication,
 * and dispatching requests to the appropriate worker nodes.
 */
public class MasterConnectionHandler implements Runnable {
    private DataInputStream inputStream; // Input stream to read data from the client
    private DataOutputStream outputStream; // Output stream to send data to the client
    private final Session clientSession; // Client session object
    private final Logger logger = LoggerFactory.getLogger(MasterConnectionHandler.class); // Logger for the MasterConnectionHandler class
    private User user; // Authenticated user object
    private final List<User> users; // List of users in the system
    private final LinkedHashMap<Integer, WorkerInfo> workerInfoMap; // Map of worker nodes available for processing requests

    public MasterConnectionHandler(
            Session clientSession,
            List<User> users,
            ArrayList<Integer> workerPorts) {

        this.clientSession = clientSession;
        this.users = Collections.synchronizedList(users);
        this.workerInfoMap = new LinkedHashMap<>();
        WorkerUtils.connectToWorkers(workerPorts, workerInfoMap);
        initializeStreams();
    }

    /* Initializes data input and output streams for client communication.*/
    private void initializeStreams() {
        try {
            inputStream = SocketUtils.createDataInputStream(clientSession.getSocket());
            outputStream = SocketUtils.createDataOutputStream(clientSession.getSocket());
        } catch (RuntimeException e) {
            logger.error("Error initializing streams: ", e);
            closeConnection(); // Close connection if streams initialization fails
        }
    }

    /**
     * Closes all I/O streams and the client socket to release resources.
     */
    private void closeConnection() {
        try {
            String remoteAddress = clientSession.getSocket().getRemoteSocketAddress().toString();
            // Attempt to close resources associated with worker nodes
            for (WorkerInfo workerinfo : workerInfoMap.values()) {
                workerinfo.getInputStream().close();
                workerinfo.getOutputStream().close();
                workerinfo.getSocket().close();
            }
            // Close client connection streams and socket
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (!clientSession.getSocket().isClosed())
                clientSession.getSocket().close();
            logger.info("Connection from " + remoteAddress + " closed successfully.");
        } catch (Exception e) {
            logger.error("Error closing connection: ", e);
        }
    }

    /* Main thread execution point, handles client authentication and session management. */
    public void run() {
        logger.info("New client connected: " + this.clientSession.getSocket().getRemoteSocketAddress());
        try {
            while (true) {
                this.user = authenticate(); // Authenticate the user
                serve();  // Starts serving the client after authentication.
            }
        } catch (RuntimeException e) {
            logger.error("Error serving client: ", e);
        } catch (IOException ignored) {
        } finally {
            closeConnection(); // Ensures connection is closed after operation.
        }
    }

    /**
     * Continuously processes requests from the client until the session ends.
     */
    public void serve() throws IOException {
        while (true) {
            Request request = receiveRequest();
            if(handleRequest(request) == -1){
                break;
            };
        }
    }

    /**
     * Receives a single request from the client and converts it into a Request object.
     *
     * @return The received request as a Request object.
     */
    public Request receiveRequest() throws IOException {
        String request = SocketUtils.safeReceive(this.inputStream);
        JSONObject clientRequestJson = JSONUtils.parseJSONString(request);

        return new Request(clientSession.getId(), clientRequestJson.get("type").toString(), clientRequestJson);
    }

    /**
     * Routes the client's request to the appropriate handler depending on the user's role and the request type.
     *
     * @param request The request to handle.
     */
    public int handleRequest(Request request) throws IOException {
        request.getBody().put("user_role", user.getClass().getSimpleName());
        request.getBody().put("user_id", user.getId());

        if (Objects.equals(request.getType(), "-1")) {
            SocketUtils.safeSend(outputStream, new Response(clientSession.getId(), "-1", Response.Status.SUCCESS, "", new JSONObject()).toJSONString());
            return -1;
        }
        if (user instanceof Manager) {
            handleManagerRequest(request); // Handle manager requests
        } else if (user instanceof Client) {
            handleClientRequest(request); // Handle client requests
        } else {
            throw new RuntimeException("Unknown user role");
        }
        return 0;
    }

    /**
     * Handles requests specific to hotel managers.
     * Managers can add hotels, update hotel information, or view reservations.
     *
     * @param request The request object containing all necessary data.
     */
    private void handleManagerRequest(Request request) throws IOException {
        long worker_id;
        String response;
        switch (request.getType()) {
            // Add hotel or
            case "1":
                // Add available dates
            case "2":
                String name = request.getBody().get("hotelName").toString();
                /* Select the worker node to handle the request */
                worker_id = WorkerUtils.selectWorker(name, workerInfoMap);

                /* Send the request to the selected worker */
                SocketUtils.safeSend(
                        workerInfoMap.get((int) worker_id).getOutputStream(),
                        request.toJSONString()
                );

                response = SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());

                /* Update the local JSON storage based on the response */
                updateJsonForManager(response, request.getBody());

                /* Send response to client */
                SocketUtils.safeSend(outputStream, response);

                break;
            // Show reservations
            case "3":
            case "4": // Reservations by area
                /* Send the request to all workers */
                for (long id : workerInfoMap.keySet()) {
                    SocketUtils.safeSend(
                            workerInfoMap.get((int) id).getOutputStream(),
                            request.toJSONString()
                    );
                }
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
    }

    /**
     * Handles client requests by directing them to the appropriate method based on user type and request type.
     *
     * @param request the JSON object containing the client's request
     */
    private void handleClientRequest(Request request) throws IOException {
        long worker_id;

        switch (request.getType()) {
            // Search hotels
            case "1":
                logger.info(request.toString());
                /* Send the request to all workers*/
                for (long id : workerInfoMap.keySet()) {
                    SocketUtils.safeSend(
                            workerInfoMap.get((int) id).getOutputStream(),
                            request.toJSONString()
                    );
                }
                break;
            // Reserve hotel
            case "2":
                String hotel_Name = request.getBody().get("hotelName").toString();
                /* Select the worker node to handle the request */
                worker_id = WorkerUtils.selectWorker(hotel_Name, workerInfoMap);

                /* Send the request to the selected worker */
                SocketUtils.safeSend(
                        workerInfoMap.get((int) worker_id).getOutputStream(),
                        request.toJSONString()
                );

                String reserveResponse = SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());

                /* Update the local JSON storage based on the response */
                updateJsonForClient(reserveResponse, request.getBody());

                /* Send the response back to the client */
                SocketUtils.safeSend(outputStream, reserveResponse);
                break;
            // Rate hotel
            case "3":
                String hotelName = request.getBody().get("hotelName").toString();
                /* Select the worker node to handle the request */
                worker_id = WorkerUtils.selectWorker(hotelName, workerInfoMap);

                /* Send the request to the selected worker */
                SocketUtils.safeSend(
                        workerInfoMap.get((int) worker_id).getOutputStream(),
                        request.toJSONString()
                );

                String rateResponse = SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());

                /* Update the local JSON storage based on the response */
                updateJsonForClient(rateResponse, request.getBody());

                /* Send the response back to the client */
                SocketUtils.safeSend(outputStream, rateResponse);
                break;

            case "4": // Return all the available hotels
                for (long id : workerInfoMap.keySet()) {
                    SocketUtils.safeSend(
                            workerInfoMap.get((int) id).getOutputStream(),
                            request.toJSONString()
                    );
                }
                break;
            case "5": // Fetch client reservations
                Map<Hotel, List<String>> reservations = ((Client) this.user).getReservations();

                JSONObject response = new JSONObject();
                JSONArray reservationsArray = new JSONArray();

                for (Map.Entry<Hotel, List<String>> entry : reservations.entrySet()) {
                    Hotel hotel = entry.getKey();
                    List<String> reservationDates = entry.getValue();

                    JSONObject hotelJSON = hotel.toJson();

                    JSONArray datesArray = new JSONArray();
                    datesArray.addAll(reservationDates);
                    hotelJSON.put("reservations", datesArray);

                    reservationsArray.add(hotelJSON);
                }
                response.put("reservations", reservationsArray);

                logger.info("Reservations: " + response.toString());

                Response res = new Response(clientSession.getId(), request.getType(), Response.Status.SUCCESS, "", response);
                SocketUtils.safeSend(outputStream, res.toJSONString());
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }

    }

    /**
     * Updates the local JSON storage based on the response received from the worker.
     * It processes the response for two types of manager requests: adding a hotel and updating available dates.
     *
     * @param response the JSON formatted string response from the worker
     * @param request  the original JSON request sent to the worker
     */
    private void updateJsonForManager(String response, JSONObject request) {

        JSONObject jsonObject_response = JSONUtils.parseJSONString(response);
        // Extract the type and status from the response
        String type = jsonObject_response.get("type").toString();
        String answer = jsonObject_response.get("status").toString();

        /* Check the type of request and the status of the response */
        if (type.equals("1") && answer.equals("SUCCESS")) { // Add hotel success
            try {
                request.remove("type");
                request.remove("user_role");

                String get_user_id = request.get("user_id").toString();
                request.remove("user_id");

                request.put("manager_id", get_user_id);

                request.put("availableDates", new JSONArray());
                request.put("reservations", new JSONObject());

                // Read the JSON file
                Object obj = JSONReaderWriter.readJsonFile("bin/hotel.json");
                JSONObject jsonObject = (JSONObject) obj;

                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
                // Add the new hotel to the hotels array
                hotelsArray.add(request);

                // Write the updated JSON object to the file
                JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals("2") && answer.equals("SUCCESS")) { // Add available dates success
            try {
                String hotelName = request.get("hotelName").toString();  // Get the hotel name from the request
                JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH);
                // Get the hotels array from the JSON object
                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
                // Iterate through the hotels array
                for (Object hotelObj : hotelsArray) {
                    JSONObject hotel = (JSONObject) hotelObj;
                    String currentHotelName = (String) hotel.get("hotelName");
                    if (currentHotelName.equals(hotelName)) {
                        JSONObject body = (JSONObject) jsonObject_response.get("body");
                        // Update the available dates for the hotel
                        hotel.put("availableDates", body.get("availableDates"));
                        JSONReaderWriter.writeJsonFile(jsonObject, Constants.JSON_FILE_PATH); // Write the updated JSON object to the file
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates client-specific JSON data based on responses, specifically for rating updates and reservation confirmations.
     *
     * @param response the JSON formatted response data from a worker
     * @param request  the original request JSON from the client
     */
    private void updateJsonForClient(String response, JSONObject request) {
        JSONObject jsonObject_response = JSONUtils.parseJSONString(response);

        // Extract the type and status from the response
        String type = jsonObject_response.get("type").toString();
        String answer = jsonObject_response.get("status").toString();

        if (type.equals("2") && answer.equals("SUCCESS")) { // Reserve hotel success
            try {
                JSONObject json_response = JSONUtils.parseJSONString(response);
                String hotelName = request.get("hotelName").toString(); // Get the hotel name from the request
                JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json");
                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels"); // Get the hotels array from the JSON object

                for (Object hotelObj : hotelsArray) {
                    JSONObject hotel = (JSONObject) hotelObj;
                    String currentHotelName = (String) hotel.get("hotelName");
                    if (currentHotelName.equals(hotelName)) {
                        JSONObject body = (JSONObject) json_response.get("body");
                        // hotel found, return the hotel's JSONObject or handle as needed
                        hotel.put("availableDates", body.get("availableDates"));
                        hotel.put("reservations", body.get("reservations"));
                        // Write the updated JSON object to the file
                        JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json");

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals("3") && answer.equals("SUCCESS")) { // Rate hotel success
            try {
                JSONObject json_response = JSONUtils.parseJSONString(response); // Parse the response JSON
                String hotelName = request.get("hotelName").toString(); // Get the hotel name from the request
                JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json");
                // Get the hotels array from the JSON object
                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

                for (Object hotelObj : hotelsArray) {
                    JSONObject hotel = (JSONObject) hotelObj;
                    String currentHotelName = (String) hotel.get("hotelName");
                    if (currentHotelName.equals(hotelName)) {
                        JSONObject body = (JSONObject) json_response.get("body");
                        // hotel found, return the hotel's JSONObject or handle as needed
                        hotel.put("stars", body.get("updatedStars"));
                        hotel.put("numReviews", body.get("updatedReviews"));
                        // Write the updated JSON object to the file
                        JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json");

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Handles user authentication by processing login or registration requests until a valid user is authenticated.
     *
     * @return the authenticated User object
     * @throws IOException if there is an input/output error during the process
     */
    public User authenticate() throws IOException {
        User user;

        while (true) {
            /* Receive the request from the client */
            String request = SocketUtils.safeReceive(inputStream);


            JSONObject jsonObject = JSONUtils.parseJSONString(request);

            String type = (String) Objects.requireNonNull(jsonObject).get("type");
            String result;
            Response.Status status;

            /* Authenticate the user */
            switch (type) {
                case "login":
                    user = login(jsonObject);
                    result = (user != null) ? "authenticated" : "not authenticated";
                    break;
                case "register":
                    user = register(jsonObject);
                    result = "registered";
                    break;
                default:
                    throw new RuntimeException("Unknown request type");
            }
            /* Log the results */
            if (user != null) {
                logger.info("User authenticated");
                status = Response.Status.SUCCESS;
            } else {
                logger.info("User not authenticated");
                status = Response.Status.UNSUCCESSFUL;
            }
            /* Send the result back to the client */
            JSONObject answer = new JSONObject();
            answer.put("result", result);
            if (result.equals("authenticated") || result.equals("registered")) {
                answer.put("user_role", user.getClass().getSimpleName());
            }

            Response response = new Response(clientSession.getId(), type, status, "", answer);
            SocketUtils.safeSend(outputStream, response.toJSONString());

            /* Proceed only if the user is authenticated */
            if (user != null) {
                return user;
            }
        }

    }

    /**
     * Attempts to authenticate a user based on the provided username and password.
     *
     * @param jsonObject the JSON object containing the username and password
     * @return the authenticated User object if credentials match, otherwise null
     */
    private User login(JSONObject jsonObject) {

        String username = (String) jsonObject.get("username");
        String password = (String) jsonObject.get("password");

        synchronized (users) {
            for (User user : users) {
                if (username.equals(user.getUserName()) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;


    }

    /**
     * Registers a new user with the provided details and adds them to the user list.
     *
     * @param jsonObject the JSON object containing user details
     * @return the newly registered User object
     * @throws RuntimeException if the user role is unknown
     */
    private User register(JSONObject jsonObject) {
        User user;
        String username = (String) jsonObject.get("username");
        String name = (String) jsonObject.get("name");
        String lastname = (String) jsonObject.get("lastname");
        String password = (String) jsonObject.get("password");
        String user_role = (String) jsonObject.get("user_role");

        if (user_role.equalsIgnoreCase("Manager")) {
            user = new Manager(name, lastname, username, password);
        } else if (user_role.equalsIgnoreCase("Client")) {
            user = new Client(name, lastname, username, password);
        } else {
            throw new RuntimeException("Unknown user user role");
        }
        /* Synchronized block to avoid concurrent modification of the users list */
        synchronized (users) {
            users.add(user);
        }
        return user;
    }

}


