package org.aueb;

import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.entities.Manager;
import org.aueb.entities.User;
import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorkerConnectionHandler extends Thread {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket connection;
    private final Logger logger = LoggerFactory.getLogger(WorkerConnectionHandler.class);
    private final List<Hotel> hotels;
    private final long id;

    public WorkerConnectionHandler(
            Socket connection, List<Hotel> hotels, long id) {
        this.connection = connection;
        this.hotels = hotels;
        logger.info("WorkerConnectionHandler constructor"+hotels);
        this.id = id;

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
        logger.info("Handling request: " + request.toJSONString());

        JSONObject hotels = (JSONObject) request.get("hotels");
        for(Object hotel: hotels.keySet()) {
            logger.info("Hotel: " + hotel);
        }


       String user_role = (String) request.get("user_role");
       String requestType = (String) request.get("type");
       logger.info("User role: " + user_role);
       logger.info("Request type: " + requestType);

       if(Objects.equals(user_role, "Manager")) {
            logger.info("Manager");
            handleManagerRequest(request);
       }
//       else if(Objects.equals(user_role, "Client")){
//            logger.info("Client");
//            //handleClientRequest(request);
//       } else {
//            throw new RuntimeException("Unknown user role");
//       }
//        if (user_role.equalsIgnoreCase("manager")) {
//            handleManagerRequest(request);
//        } else if (user_role.equalsIgnoreCase("client")) {
//            //handleClientRequest(request);
//        } else {
//            throw new RuntimeException("Unknown user role");
//        }

    }

    private void handleManagerRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        String requestType = (String) request.get("type");

        logger.info("Handling manager request: " + requestType);
        switch (requestType) {
            case "1": // Add hotel or
                hotels.add(new Hotel(
                        (String) request.get("hotelName"),
                        ((Long) request.get("numPeople")).intValue(),
                        (String) request.get("area"),
                        ((Number) request.get("stars")).doubleValue(),
                        ((Long) request.get("numReviews")).intValue(),
                        (String) request.get("roomImage"),
                        ((Number) request.get("price")).doubleValue(),
                        (String) request.get("availableDates"),
                        ((Long) request.get("user_id")).intValue()
                ));
                response.put("option", "1");
                response.put("Success", true);
                logger.info("hotels" + hotels.toString());
                break;
            case "2": // add available dates
                boolean foundHotel = false;
                String hotelName= request.get("hotelName").toString();
                String availableDates= request.get("availableDates").toString();
                logger.info(hotels.toString());
                for (Hotel hotel: hotels) {
                    if ((hotel.getHotelName()).equals(hotelName)) {
                        if (!hotel.getAvailableDates().equals(" ")) {
                            String newAvailableDate = hotel.getAvailableDates() + " , " + availableDates;

                        }
                        foundHotel=true;
                        hotel.setAvailableDates(availableDates);
                        break;
                    }
                }
                response.put("option", "2");
                response.put("foundHotel", foundHotel);
                break;
            case "3":   // Show reservations
                List<Hotel> reservationsByManager= new ArrayList<>();
                List<Hotel> hotelsByManager= new ArrayList<>();
                int manager_id = (int) request.get("user_id");
                for (Hotel hotel: hotels){
                    if(hotel.getManagerId()==manager_id){
                        hotelsByManager.add(hotel);
                    }
                }
                for (Hotel hotel : hotelsByManager) {
                    Map<Client, String> reservations = hotel.getReservations();
                    if (!reservations.isEmpty()) {
                        reservationsByManager.add(hotel);
                    }
                }

                response.put("option", "3");
                response.put("reservations", reservationsByManager);

                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
        SocketUtils.safeSend(outputStream,response.toJSONString());


    }

    private void receiveHotels(String hotelsJsonString) {
        JSONObject jsonObject = JSONUtils.parseJSONString(hotelsJsonString);
        JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

        if (!hotelsArray.isEmpty()) {
            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;
                logger.info("Hotel: " + hotel.toString());
                int manager_id = ((Long) hotel.get("manager_id")).intValue();
                int numPeople = ((Long) hotel.get("numPeople")).intValue();
                int numReviews = ((Long) hotel.get("numReviews")).intValue();
                double stars = ((Number) hotel.get("stars")).doubleValue();
                double price = ((Number) hotel.get("price")).doubleValue();
                String hotelName = hotel.get("hotelName").toString();
                String area = hotel.get("area").toString();
                String roomImage = hotel.get("hotelImage").toString();
                String availableDate = (String) hotel.get("availableDates");

                this.hotels.add(new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, availableDate, manager_id));
            }
        }
    }
//    private void handleClientRequest(JSONObject request, String requestType) {
//        long worker_id;
//        switch (requestType) {
//            case "1":
//            case "2":
//                String name = request.get("hotelName").toString();
//                worker_id = selectWorker(name);
//                logger.info("Worker_id" + worker_id);
//                logger.info(request.toJSONString());
//                SocketUtils.safeSend(
//                        workerInfoMap.get((int) worker_id).getOutputStream(),
//                        request.toJSONString()
//                );
//                break;
//            case "3":
//                for (Hotel hotel : ((Manager) user).getHotelsManaged()) {
//                    worker_id = selectWorker(hotel.getHotelName());
//                    logger.info("Worker_id" + worker_id);
//                    SocketUtils.safeSend(
//                            workerInfoMap.get((int) worker_id).getOutputStream(),
//                            request.toJSONString()
//                    );
//                }
//                break;
//            default:
//                throw new RuntimeException("Unknown request type");
//        }
//    }
//



//    private void handleManagerRequest(JSONObject request, String requestType) {
//        long worker_id;
//        switch (requestType) {
//            case "1": // Add hotel or
//            case "2": // add available dates
//                String name = request.get("hotelName").toString();
//                worker_id = selectWorker(name);
//                logger.info("Worker_id" + worker_id);
//                SocketUtils.safeSend(
//                        workerInfoMap.get((int) worker_id).getOutputStream(),
//                        request.toJSONString()
//                );
//
//                break;
//            case "3":   // Show reservations
//                for (Hotel hotel : hotels) {
//                    worker_id = selectWorker(hotel.getHotelName());
//                    logger.info("Worker_id" + worker_id);
//                    SocketUtils.safeSend(
//                            workerInfoMap.get((int) worker_id).getOutputStream(),
//                            request.toJSONString()
//                    );
//                }
//                break;
//            default:
//                throw new RuntimeException("Unknown request type");
//        }
//    }


//    public void run() {
//        try {
//            // Accept the connection from master
//            Socket connection = serverSocket.accept();
//
//            // Create the worker's input and output streams
//            inputStream = SocketUtils.createDataInputStream(connection);
//            outputStream = SocketUtils.createDataOutputStream(connection);
//
//            logger.info("Master connected : " + connection.getRemoteSocketAddress());
//
//            // Receive hotels from master
//            String hotelsRequest = SocketUtils.safeReceive(inputStream);
//            receiveHotels(hotelsRequest);
//
//
//            String request = SocketUtils.safeReceive(inputStream);
//            JSONObject jsonObject = (JSONObject) parser.parse(request);
//            String identity = jsonObject.get("user_role").toString();
//            if (identity.equals("Manager")) {
//                logger.info("Managerrrr");
//                String option = jsonObject.get("").toString();
//                if (option.equals("1")) {
//                    addHotelToManager(jsonObject);
//                } else if (option.equals("2")) {
//                    addAvailableDates(jsonObject);
//                }
//            }
//
//            logger.info(request);
//            logger.info("Received request from master, worker_id: " + id);
//
//        } catch (IOException e) {
//            System.out.println("Error accepting client connection: " + e.getMessage());
//            e.printStackTrace();
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }

}