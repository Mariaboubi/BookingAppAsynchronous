package org.aueb.master;

import org.aueb.entities.*;
import org.aueb.util.Constants;
import org.aueb.util.JSONReaderWriter;
import org.aueb.util.SocketUtils;
import org.aueb.worker.WorkerInfo;
import org.aueb.worker.WorkerUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * The Master class acts as the central server in a distributed hotel reservation system.
 * It listens for connections from clients (consoles) and the reducer, and handles incoming data.
 * The Master is responsible for managing hotels, users, and sessions, as well as distributing hotels to worker nodes.
 */
public class Master {

    private final ArrayList<Hotel> hotels; // List to store all hotels available in the system
    private final Logger logger = LoggerFactory.getLogger(Master.class); // Logger for the Master class
    private final List<User> users; // List to store all users in the system
    private final ArrayList<Integer> workerPorts = new ArrayList<>(Arrays.asList(6001, 6002, 6003)); // List to store the ports of the worker nodes
    private ServerSocket masterServerSocket; // ServerSocket for the Master
    private ServerSocket reducerResponseSocket; // ServerSocket for the Reducer
    private Map<Long, Session> sessions = new LinkedHashMap<>(); // Map to store all active sessions

    /**
     * Constructor for the Master class.
     */
    public Master() {
        this.users = new ArrayList<>();
        this.hotels = new ArrayList<>();
    }

    public static void main(String[] args) {
        Master server = new Master();
        server.runServer();
    }

    /**
     * Initializes and runs the server by setting up data and opening server sockets.
     */
    private void runServer() {
        setupInitialData();
        openServer();
    }

    /**
     * Sets up initial data for the application by loading users, hotels and establishing connections to worker nodes.
     */
    private void setupInitialData() {
        initializeUsers();
        readHotels();
        associateHotelsWithManagers();
        clientReservation();
        LinkedHashMap<Integer, WorkerInfo> workerInfoMap = new LinkedHashMap<>(); // Map to store worker node information
        WorkerUtils.connectToWorkers(this.workerPorts, workerInfoMap);
        distributeHotels(workerInfoMap);
    }

    /**
     * Opens server sockets and listens for connections from clients and the reducer.
     */
    private void openServer() {
        try {
            masterServerSocket = new ServerSocket(Constants.MASTER_PORT);  // Existing connection handling for Consoles
            reducerResponseSocket = new ServerSocket(Constants.REDUCER_TO_MASTER_PORT);  // Dedicated port for Reducer

            // Handle Console connections
            new Thread(this::handleConsoleConnections).start();

            // Handle Reducer connections
            new Thread(this::handleReducerConnections).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handles incoming connections from the reducer.
     */
    private void handleReducerConnections() {
        while (true) {
            try {
                Socket clientConnection = reducerResponseSocket.accept();
                Runnable r = new ReducerDataHandler(clientConnection, sessions); // Handle incoming data from the reducer
                new Thread(r).start(); // Start a new thread to handle the reducer session

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }

    /**
     * Handles incoming connections from clients (consoles).
     */
    private void handleConsoleConnections() {
        while (true) {
            try {
                Socket clientConnection = masterServerSocket.accept();
                Session clientSession = new Session(clientConnection); // Create a new session for the client
                sessions.put(clientSession.getId(), clientSession); // Add the session to the active sessions map
                Runnable r = new MasterConnectionHandler(clientSession, users, workerPorts); // Handle incoming data from the client
                new Thread(r).start(); // Start a new thread to handle the client session
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Initializes predefined users
     */
    private void initializeUsers() {
        /* Initialize some managers and clients */
        users.add(new Manager("maria", "boubi", "mariab", "1111"));
        users.add(new Manager("eleni", "zanou", "eleni", "1234"));
        users.add(new Manager("mariaSam", "samara", "samaraM", "maria111"));
        users.add(new Client("georgos", "tsibo", "sambo", "2003"));
        users.add(new Client("giannis", "papadopoulos", "giannis_p", "giannis1"));

    }

    /**
     * Distributes hotels to worker nodes based on a hash function.
     *
     * @param workerInfoMap Map containing worker node information.
     */
    private void distributeHotels(LinkedHashMap<Integer, WorkerInfo> workerInfoMap) {
        LinkedHashMap<Long, JSONObject> hotelsMap = new LinkedHashMap<>();
        for (int worker_id = 0; worker_id < Constants.NUM_WORKER_NODES; worker_id++) {
            // Create a JSON object for each worker node
            JSONObject workerHotels = new JSONObject();
            workerHotels.put("hotels", new JSONArray());
            hotelsMap.put((long) worker_id, workerHotels);
        }
        for (Hotel hotel : hotels) {
            // Select a worker node based on the hotel name
            Long worker_id = WorkerUtils.selectWorker(hotel.getHotelName(), workerInfoMap);
            JSONObject workerHotelJson = hotelsMap.get(worker_id);

            JSONArray hotelsArray = (JSONArray) workerHotelJson.get("hotels");

            hotelsArray.add(hotel.toJson()); // Add the hotel to the worker's list of hotels

        }
        // Send the hotels to the respective worker nodes
        for (int worker_id = 0; worker_id < Constants.NUM_WORKER_NODES; worker_id++) {
            JSONObject workerHotelJson = hotelsMap.get((long) worker_id);
            SocketUtils.safeSend(
                    workerInfoMap.get(worker_id).getOutputStream(),
                    workerHotelJson.toJSONString()
            );
        }
    }

    /**
     * Reads hotel data from a JSON file and populates the hotels list.
     */
    private void readHotels() {
        try {
            JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH);
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;

                // Use default values if keys are missing or values are null
                Object managerIdObj = hotel.getOrDefault("manager_id", "0");  // Default to "0" if not found

                // Safely convert to int, handling possible Number or String types
                int manager_id = 0;
                if (managerIdObj instanceof Number) {
                    manager_id = ((Number) managerIdObj).intValue();
                } else if (managerIdObj instanceof String) {
                    try {
                        manager_id = Integer.parseInt((String) managerIdObj);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in manager_id: " + managerIdObj);
                        manager_id = 0; // Default to 0 or handle appropriately
                    }
                }
                int numReviews = hotel.get("numReviews") == null ? 0 : ((Long) hotel.get("numReviews")).intValue();
                int numPeople = hotel.get("numPeople") == null ? 0 : ((Long) hotel.get("numPeople")).intValue();
                double stars = hotel.get("stars") == null ? 0.0 : ((Number) hotel.get("stars")).doubleValue();
                double price = hotel.get("price") == null ? 0.0 : ((Number) hotel.get("price")).doubleValue();
                String hotelName = (String) hotel.getOrDefault("hotelName", "Unknown");
                String area = (String) hotel.getOrDefault("area", "Unknown");
                String roomImage = (String) hotel.getOrDefault("hotelImage", "default.png");


                JSONArray availableDatesObj = (JSONArray) hotel.get("availableDates");
                List<String> dates = new ArrayList<>();
                if (availableDatesObj != null) {
                    availableDatesObj.forEach(date -> dates.add((String) date));
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

                // Create a new Hotel object and add it to the hotels list
                Hotel newHotel = new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, dates, manager_id);
                newHotel.setReservations(reservations);
                hotels.add(newHotel);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Associates hotels with their respective managers.
     */
    public void associateHotelsWithManagers() {
        // Create a map of manager IDs to Manager objects for efficient lookup
        Map<Integer, Manager> managerMap = new HashMap<>();
        for (User user : users) {
            if (user instanceof Manager) {
                managerMap.put(user.getId(), (Manager) user);
            }
        }
        // Iterate over all hotels and add them to the list of hotels managed by the respective manager
        for (Hotel hotel : hotels) {
            int manager_id = hotel.getManagerId();

            Manager manager = managerMap.get(manager_id);
            if (manager != null) {
                manager.addHotelsManaged(hotel);
            }
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find.
     * @return The User object if found, otherwise null.
     */
    public User findUserById(int id){
        for(User user:users){
            if(user.getId()==id){
                return user;
            }
        }
        return null;
    }

    /**
     * Finds a hotel by its name.
     *
     * @param name The name of the hotel to find.
     * @return The Hotel object if found, otherwise null.
     */
    public Hotel findHotelByName(String name){
        for(Hotel hotel:hotels){
            if(hotel.getHotelName().equals(name)){
                return hotel;
            }
        }
        return null;
    }

    /**
     * Associates client reservations with hotels.
     */
    public void clientReservation(){
        try {
            JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH);
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;
                String HotelName= (String) hotel.get("hotelName");
                Hotel hotelObject= findHotelByName(HotelName);
                Map<Integer,List<String>> reservations= (Map<Integer, List<String>>) hotel.get("reservations");
                for (Map.Entry<Integer, List<String>> entry : reservations.entrySet()) {
                    int userId = Integer.parseInt(String.valueOf(entry.getKey()));
                    Client client = (Client) findUserById(userId);
                    client.addReservations(hotelObject, entry.getValue().toString());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}