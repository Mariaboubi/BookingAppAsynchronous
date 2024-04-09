package org.aueb;

import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.entities.Manager;
import org.aueb.entities.User;
import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONObject;
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
        try {
            // Create input and output streams for the client
            inputStream = SocketUtils.createDataInputStream(connection);
            outputStream = SocketUtils.createDataOutputStream(connection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        logger.info("New client connected: " + this.connection.getRemoteSocketAddress());

        this.user = authenticate();
        serve();


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

    private void handleClientRequest(JSONObject request, String requestType) {
        long worker_id;
        String response;
        switch (requestType) {
            case "1":
            case "2":
                String name = request.get("hotelName").toString();
                worker_id = selectWorker(name);
                logger.info("Worker_id" + worker_id);
                logger.info(request.toJSONString());
                SocketUtils.safeSend(
                        workerInfoMap.get((int) worker_id).getOutputStream(),
                        request.toJSONString()
                );

                break;
            case "3":
                for (Hotel hotel : ((Manager) user).getHotelsManaged()) {
                    worker_id = selectWorker(hotel.getHotelName());
                    logger.info("Worker_id" + worker_id);
                    SocketUtils.safeSend(
                            workerInfoMap.get((int) worker_id).getOutputStream(),
                            request.toJSONString()
                    );

                }
                break;
            default:
                throw new RuntimeException("Unknown request type");
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
                response=SocketUtils.safeReceive(workerInfoMap.get((int) worker_id).getInputStream());
                logger.info("Response" + response);


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

            System.out.println("Waiting for request");
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