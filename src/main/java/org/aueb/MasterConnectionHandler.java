package org.aueb;


import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.entities.Manager;
import org.aueb.entities.User;
import org.aueb.util.JSONReaderWriter;
import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class MasterConnectionHandler extends Thread {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket connection;
    private final Logger logger = LoggerFactory.getLogger(MasterConnectionHandler.class);
    private User user;
    private final List<User> users;
    private final List<Hotel> hotels;
    private final int numWorkerNodes;
    private final Map<Integer, WorkerInfo> workerInfoMap;

    public MasterConnectionHandler(
            Socket connection,
            List<User> users,
            List<Hotel> hotels,
            int numWorkerNodes,
            Map<Integer, WorkerInfo> workerInfoMap) {
        this.connection = connection;
        this.users = users;
        this.hotels = hotels;
        this.numWorkerNodes = numWorkerNodes;
        this.workerInfoMap = workerInfoMap;
        initializeStreams();
    }
    private void initializeStreams() {
        try {
            inputStream = SocketUtils.createDataInputStream(connection);
            outputStream = SocketUtils.createDataOutputStream(connection);
        } catch (RuntimeException e) {
            logger.error("Error initializing streams: ", e);
            closeConnection(); // Close connection if streams initialization fails
        }
    }
    private void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (connection != null && !connection.isClosed()) connection.close();
            logger.info("Connection closed successfully.");
        } catch (Exception e) {
            logger.error("Error closing connection: ", e);
        }
    }

    public void run() {
        logger.info("New client connected: " + this.connection.getRemoteSocketAddress());
        try {
            this.user = authenticate();
            serve();
        } catch (Exception e) {
            logger.error("Error handling client request: ", e);
        } finally {
            closeConnection();
        }
    }

    public void serve() {
        while (true) {
            JSONObject request = receiveRequest();
            handleRequest(request);
        }
    }

    public JSONObject receiveRequest() {
        String request = SocketUtils.safeReceive(this.inputStream);
        logger.info("Received request: " + request);
        return JSONUtils.parseJSONString(request);
    }

    public void handleRequest(JSONObject request) {
        String requestType = (String) request.get("type");

        request.put("user_role", user.getClass().getSimpleName());
        request.put("user_id", user.getId());

        if (user instanceof Manager) {
            handleManagerRequest(request, requestType);
        } else if (user instanceof Client) {
            handleClientRequest(request, requestType);
        } else {
            throw new RuntimeException("Unknown user role");
        }

    }

    private void handleManagerRequest(JSONObject request, String requestType) {
        long worker_id;
        String response;
        switch (requestType) {
            case "1": // Add hotel or
            case "2": // add available dates
                String name = request.get("hotelName").toString();
                worker_id = selectWorker(name);

                logger.info("Worker_id" + worker_id);

                SocketUtils.safeSend(
                        workerInfoMap.get((int) worker_id).getOutputStream(),
                        request.toJSONString()
                );

                response = SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());
                logger.info("Response" + response);

                updateJsonForManager(response,request);

                // Send response to client
                SocketUtils.safeSend(outputStream,response);

                break;
            case "3":   // Show reservations
                for (Hotel hotel : hotels) {
                    worker_id = selectWorker(hotel.getHotelName());
                    logger.info("Worker_id" + worker_id);
                    SocketUtils.safeSend(
                            workerInfoMap.get((int) worker_id).getOutputStream(),
                            request.toJSONString()
                    );
                    response = SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());
                }

                break;
            default:
                throw new RuntimeException("Unknown request type");
        }



    }

    private void updateJsonForManager(String response, JSONObject request) {

        JSONObject jsonObject_response = JSONUtils.parseJSONString(response);
        String type = jsonObject_response.get("type").toString();
        String answer = jsonObject_response.get("status").toString();

        if (type.equals("1") && answer.equals("SUCCESS")) {
            try {
                request.remove("type");
                request.remove("user_role");

                String get_user_id = request.get("user_id").toString();
                request.remove("user_id");

                request.put("manager_id", get_user_id);
                request.put("availableDates", new JSONArray());

                JSONParser parser = new JSONParser();
                //Object obj = parser.parse(new FileReader("bin/hotel.json"));
                Object obj = JSONReaderWriter.readJsonFile("bin/hotel.json");
                JSONObject jsonObject = (JSONObject) obj;

                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

                hotelsArray.add(request);

                JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals("2") && answer.equals("SUCCESS")) {
            try {
                JSONObject json_response = JSONUtils.parseJSONString(response);
                String hotelName = request.get("hotelName").toString();
                JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH); // This method needs to be implemented
                JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
                // Iterate through the hotels array
                for (Object hotelObj : hotelsArray) {
                    JSONObject hotel = (JSONObject) hotelObj;
                    String currentHotelName = (String) hotel.get("hotelName");
                    if (currentHotelName.equals(hotelName)) {
                        JSONObject body= (JSONObject) json_response.get("body");
                        hotel.put("availableDates", body.get("availableDates"));
                        JSONReaderWriter.writeJsonFile(jsonObject, Constants.JSON_FILE_PATH); // This method needs to be implemented

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



private synchronized void  handleClientRequest(JSONObject request, String requestType) {
    long worker_id;
    String response;
    switch (requestType) {
        case "1":
            logger.info(request.toJSONString());
            for(long id : workerInfoMap.keySet()){
                SocketUtils.safeSend(
                        workerInfoMap.get((int) id).getOutputStream(),
                        request.toJSONString()
                );
//                response = SocketUtils.safeReceive(workerInfoMap.get((int) id).getInputStream());
//                logger.info("Response" + response);
//                updateJsonForClient(response,request);
//                SocketUtils.safeSend(outputStream,response);
            }
        case "2":


            break;
        case "3":
            String hotelName = request.get("hotelName").toString();
            worker_id = selectWorker(hotelName);
            logger.info("Worker_id" + worker_id);
            SocketUtils.safeSend(
                    workerInfoMap.get((int) worker_id).getOutputStream(),
                    request.toJSONString()
            );
            response=SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());
            logger.info("Response" + response);
            updateJsonForClient(response,request);

            SocketUtils.safeSend(outputStream,response);
            break;
        default:
            throw new RuntimeException("Unknown request type");
    }

}

private void updateJsonForClient(String response, JSONObject request) {
    JSONObject jsonObject_response = JSONUtils.parseJSONString(response);
    String type = jsonObject_response.get("type").toString();
    if(type.equals("3")){
        try {
            JSONObject json_response = JSONUtils.parseJSONString(response);
            String hotelName = request.get("hotelName").toString();
            JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json"); // This method needs to be implemented
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
            Double updatedstars = (Double) json_response.get("updatedStars");

            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;
                String currentHotelName = (String) hotel.get("hotelName");
                if (currentHotelName.equals(hotelName)) {
                    JSONObject body= (JSONObject) json_response.get("body");
                    // hotel found, return the hotel's JSONObject or handle as needed
                    hotel.put("stars", body.get("updatedStars"));
                    hotel.put("numReviews", body.get("updatedReviews"));
                    JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json"); // This method needs to be implemented

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


private int hashFunction(String roomName) {
    return roomName.hashCode();
}

public Long selectWorker(String roomName) {
    int workerId = Math.abs(hashFunction(roomName)) % numWorkerNodes;
    logger.info("Room name: " + roomName + " Worker id: " + workerId + " hashcode: " + hashFunction(roomName));

    return workerInfoMap.get(workerId).getWorker_id();
}

public User authenticate() {
    User user = null;

    while (true) {

        String request = SocketUtils.safeReceive(inputStream);
        JSONObject jsonObject = JSONUtils.parseJSONString(request);
        logger.info("Received request: " + request);

        String type = (String) jsonObject.get("type");
        String result = null;
        switch (type) {
            case "login":

                user = login(jsonObject);
                if (user != null) {
                    logger.info("authenticated");
                    result = "authenticated";

                } else {
                    logger.info("not authenticated");
                    result = "not authenticated";

                }
                break;
            case "register":
                user = register(jsonObject);
                if (user != null) {
                    logger.info("registered");
                    result = "registered";
                }
        }
        String user_type;
        JSONObject answer = new JSONObject();
        if (result.equals("not authenticated")) {
            user_type = " ";

        } else {
            assert user != null;
            user_type = user.getClass().getSimpleName();
        }


        answer.put("result", result);
        answer.put("user_type", user_type);

        SocketUtils.safeSend(outputStream, answer.toJSONString());

        return user;

    }

}

private User login(JSONObject jsonObject) {

    String username = (String) jsonObject.get("username");
    String password = (String) jsonObject.get("password");

    logger.info(username);
    logger.info(password);
    for (User user : users) {
        if (username.equals(user.getUserName()) && user.getPassword().equals(password)) {
            logger.info("User found");
            return user;
        }
    }
    return null;


}

private User register(JSONObject jsonObject) {
    User user;
    String username = (String) jsonObject.get("username");
    String name = (String) jsonObject.get("name");
    String lastname = (String) jsonObject.get("lastname");
    String password = (String) jsonObject.get("password");
    String identity = (String) jsonObject.get("identity");

    if (identity.equals("Manager")) {
        logger.info("Manager registration");
        user = new Manager(name, lastname, username, password);
        users.add(user);
        return user;

    } else if (identity.equals("Client")) {
        user = new Client(name, lastname, username, password);
        users.add(user);
        return user;
    }
    return null;
}

}



