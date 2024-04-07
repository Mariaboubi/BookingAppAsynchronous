package org.aueb;

import org.aueb.entities.Hotel;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread {
    private final ArrayList<Hotel> assignedHotels;
    private final JSONParser parser = new JSONParser();
    private final ServerSocket serverSocket;

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private DataInputStream inputStream;

    private DataOutputStream outputStream;

    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;
    private int id;


    private int port;



    public Worker(int id, int port) throws IOException {
        this.id = id;
        this.port = port;
        serverSocket = new ServerSocket(port);
        assignedHotels = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
    public void run() {
        try {
            // Accept a new client connection
            Socket clientSocket = serverSocket.accept();

            // Create input and output streams for the client
            inputStream = SocketUtils.createDataInputStream(clientSocket);
            outputStream = SocketUtils.createDataOutputStream(clientSocket);

            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());


            logger.info("New client connected: " + clientSocket.getRemoteSocketAddress());

            String request = SocketUtils.safeReceive(inputStream);
            JSONObject jsonObject = (JSONObject) parser.parse(request);
            String identity= jsonObject.get("identity").toString();
            if (identity.equals("Manager")) {
                logger.info("Managerrrr");
                String option = jsonObject.get("").toString();
                if (option.equals("1")) {
                    addHotelToManager(jsonObject);
                } else if (option.equals("2")) {
                    addAvailableDates(jsonObject);
                }
            }

            logger.info(request);
            logger.info("Received request from master, worker_id: " + id);

        } catch (IOException e) {
            System.out.println("Error accepting client connection: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void addAvailableDates(JSONObject jsonObject) {
    }

    public void addHotel(Hotel hotel) {
        assignedHotels.add(hotel);
    }

    private void addHotelToManager(JSONObject jsonObject) throws IOException {
        int manager_id = ((Long) jsonObject.get("id")).intValue();
        String hotelName = jsonObject.get("hotelName").toString();
        String noOfPersons =jsonObject.get("noOfPersons").toString();
        int noOfReviews = ((Long) jsonObject.get("noOfReviews")).intValue();
        String area = jsonObject.get("area").toString();
        double stars = ((Number) jsonObject.get("stars")).doubleValue();
        String roomImage = jsonObject.get("hotelImage").toString();
        double price = ((Number) jsonObject.get("price")).doubleValue();


        Hotel newHotel= new Hotel(hotelName,noOfPersons,area,stars,noOfReviews,roomImage,price," ",manager_id);
        addHotel(newHotel);
        objectOutputStream.writeObject(newHotel);
    }

    public static void main(String[] args) throws IOException {
////        int numWorkerNodes = 3;
////        for (int i = 0; i < numWorkerNodes; i++) {
////            int port = 8001 + i;
////            Worker worker = new Worker(port);
////            worker.start(); // Correctly start the thread
////        }
    }
}

