package org.aueb.worker;

import org.aueb.entities.Hotel;
import org.aueb.util.JSONUtils;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Worker class functions as a server node in a distributed system handling specific tasks like managing hotel data.
 * Each worker maintains a subset of the entire dataset (hotels in this instance) and processes requests pertaining to its data.
 */
public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class); // Logger for the Worker class
    private final ArrayList<Hotel> hotels; // List of hotels managed by this worker
    private ServerSocket serverSocket; // Server socket to listen for connections from the Master
    private final long id; // Unique ID of the worker

    /**
     * Constructs a new Worker with a unique ID.
     *
     * @param id The unique ID of the worker.
     */
    public Worker(int id) {
        this.id = id;
        hotels = new ArrayList<>();
    }

    public static void main(String[] args) {
        /* Check if the correct number of arguments is provided */
        if (args.length < 2) {
            System.out.println("Usage: java Worker <worker ID> <port number>");
            return;
        }
        /* Parse the worker ID and port number from the command line arguments */
        int workerId = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        logger.info("Initializing Worker " + workerId + " on port " + port);

        Worker worker = new Worker(workerId);
        worker.openServer(port);
    }

    /**
     * Opens a server socket to listen for incoming connections from the master node,
     * initializes resources, and handles client requests in a loop.
     * @param port The port number on which this worker will listen for incoming connections.
     */
    void openServer(int port) {
        try {
            /* Create a server socket to listen for connections from the Master */
            serverSocket = new ServerSocket(port);

            // Initial connection to receive hotel data from the master.
            Socket connection = serverSocket.accept();
            getInitialHotels(receiveRequest(SocketUtils.createDataInputStream(connection)));
            connection.close();

            /* Continuously listen for incoming connections from the Master */
            while (true) {
                connection = serverSocket.accept();  // Accept incoming connection
                Runnable r = new WorkerConnectionHandler(connection, hotels, id); // Create a new WorkerConnectionHandler
                Thread workerThread = new Thread(r); // Create a new thread to handle the connection
                workerThread.start(); // Start the thread
            }
        } catch (IOException ignored) {
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Receives a request from the Master and parses it into a JSONObject.
     *
     * @param inputStream DataInputStream to read the request from.
     * @return JSONObject containing the request.
     * @throws IOException If an I/O error occurs.
     */
    public JSONObject receiveRequest(DataInputStream inputStream) throws IOException {
        String request = SocketUtils.safeReceive(inputStream);
        return JSONUtils.parseJSONString(request);
    }

    /**
     * Parses the initial hotels from the request and adds them to the hotels list.
     *
     * @param request JSONObject containing the initial hotels.
     */
    private void getInitialHotels(JSONObject request) {
        JSONArray hotelsArray = (JSONArray) request.get("hotels");

        if (hotelsArray != null && !hotelsArray.isEmpty()) {
            for (Object o : hotelsArray) {
                JSONObject hotel = (JSONObject) o;

                int manager_id = ((Long) hotel.get("manager_id")).intValue();
                int numPeople = ((Long) hotel.get("numPeople")).intValue();
                int numReviews = ((Long) hotel.get("numReviews")).intValue();
                double stars = ((Number) hotel.get("stars")).doubleValue();
                double price = ((Number) hotel.get("price")).doubleValue();
                String hotelName = hotel.get("hotelName").toString();
                String area = hotel.get("area").toString();
                String roomImage = hotel.get("hotelImage").toString();

                JSONArray jsonAvailableDates = (JSONArray) hotel.get("availableDates");
                List<String> availableDates = new ArrayList<>();
                if (jsonAvailableDates != null) {
                    for (Object dateObj : jsonAvailableDates) {
                        availableDates.add((String) dateObj);
                    }
                }

                JSONObject reservationsObj = (JSONObject) hotel.get("reservations");
                Map<Integer, List<String>> reservations = new HashMap<>();
                if (reservationsObj != null) {
                    for (Object key : reservationsObj.keySet()) {
                        JSONArray datesArray = (JSONArray) reservationsObj.get(key);
                        List<String> dateList = new ArrayList<>();
                        if (datesArray != null) {
                            datesArray.forEach(date -> dateList.add((String) date));
                        }
                        reservations.put(Integer.parseInt((String) key), dateList);
                    }
                }

                // Create a new hotel object and add it to the list of hotels
                Hotel newhotel = new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, availableDates, manager_id);
                newhotel.setReservations(reservations);
                this.hotels.add(newhotel);

            }
        }
    }
}

