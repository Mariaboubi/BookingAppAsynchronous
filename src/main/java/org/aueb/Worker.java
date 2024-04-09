package org.aueb;

import org.aueb.entities.Hotel;
import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private final ArrayList<Hotel> hotels;
    private final JSONParser parser = new JSONParser();
    private ServerSocket serverSocket;
    private Socket connection;
    private long id;
    private int port;

    public Worker(int id, int port) {
        this.id = id;
        this.port = port;
        hotels = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        int numWorkerNodes = Constants.NUM_WORKER_NODES;
        int port = 6000;

        for (int i = 0; i < numWorkerNodes; i++) {
//            port += 1;
            int finalPort = port + 1;
            port += 1;
            int finalI = i;
            Worker worker = new Worker(finalI, finalPort);
            worker.openServer(finalPort);

        }
    }
    void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                connection = serverSocket.accept();
                Thread t = new WorkerConnectionHandler(connection, hotels, id);
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



//    private void addAvailableDates(JSONObject jsonObject) {
//    }

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
    }

    private void addHotelToManager(JSONObject jsonObject) throws IOException {
        int manager_id = ((Long) jsonObject.get("id")).intValue();
        String hotelName = jsonObject.get("hotelName").toString();
        int numPeople = ((Long) jsonObject.get("numPeople")).intValue();
        int numReviews = ((Long) jsonObject.get("numReviews")).intValue();
        String area = jsonObject.get("area").toString();
        double stars = ((Number) jsonObject.get("stars")).doubleValue();
        String roomImage = jsonObject.get("hotelImage").toString();
        double price = ((Number) jsonObject.get("price")).doubleValue();


        Hotel newHotel = new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, " ", manager_id);
        addHotel(newHotel);
        //objectOutputStream.writeObject(newHotel);
    }
}

