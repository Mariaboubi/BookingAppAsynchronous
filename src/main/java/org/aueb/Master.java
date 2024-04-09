package org.aueb;

import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.entities.Manager;
import org.aueb.entities.User;
import org.aueb.util.JSONReaderWriter;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {

    private final ArrayList<Hotel> hotels;
    private final JSONParser parser = new JSONParser();
    private final Logger logger = LoggerFactory.getLogger(Master.class);
    private final Map<Integer, WorkerInfo> workerInfoMap;
    private List<User> users;
    private ServerSocket providerSocket;
    private Socket connection = null;
    private int numWorkerNodes;

    public Master(int numWorkerNodes) {
        this.users = new ArrayList<>();
        this.hotels = new ArrayList<>();
        this.workerInfoMap = new LinkedHashMap<>();
        this.numWorkerNodes = numWorkerNodes;
    }

    public static void main(String[] args) {
        int port = 8000;
        int numWorkers = Constants.NUM_WORKER_NODES;

        Master server = new Master(numWorkers);
        server.initializeManagers();
        server.initializeClients();
        server.readHotels();
        server.associateHotelsWithManagers();
        server.connectToWorkers();
        server.sendHotelsToWorkers();
        server.openServer(port);
    }

    void openServer(int port) {
        try {
            providerSocket = new ServerSocket(port);

            while (true) {
                connection = providerSocket.accept();

                Thread t = new MasterConnectionHandler(connection, users, hotels, numWorkerNodes, workerInfoMap);
                t.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void initializeManagers() {
        Manager manager;

        manager = new Manager("maria", "boubi", "mariab", "1234");
        users.add(manager);

        manager = new Manager("eleni", "zanou", "zanou", "4444");
        users.add(manager);

        manager = new Manager("mariaSam", "samara", "samaraM", "maria111");
        users.add(manager);
    }

    private void initializeClients() {
        Client client = new Client("georgos", "tsibo", "sambo", "2003");
        users.add(client);
    }

    private int hashFunction(String roomName) {
        return roomName.hashCode();
    }

    public Long selectWorker(String roomName) {
        int workerId = Math.abs(hashFunction(roomName)) % Constants.NUM_WORKER_NODES;
        logger.info("Room name: " + roomName + " Worker id: " + workerId + " hashcode: " + hashFunction(roomName));

        return workerInfoMap.get(workerId).getWorker_id();
    }

    private void sendHotelsToWorkers() {
        LinkedHashMap<Long, JSONObject> hotelsMap = new LinkedHashMap<>();
        for (int worker_id = 0; worker_id < Constants.NUM_WORKER_NODES; worker_id++) {
            JSONObject workerHotels = new JSONObject();
            workerHotels.put("hotels", new JSONArray());
            hotelsMap.put((long) worker_id, workerHotels);
        }
        for (Hotel hotel : hotels) {
            Long worker_id = selectWorker(hotel.getHotelName());
            JSONObject workerHotelJson = hotelsMap.get(worker_id);

            JSONArray hotelsArray = (JSONArray) workerHotelJson.get("hotels");

            hotelsArray.add(hotel.toJson());

        }

        for (int worker_id = 0; worker_id < Constants.NUM_WORKER_NODES; worker_id++) {
            JSONObject workerHotelJson = hotelsMap.get((long) worker_id);
            SocketUtils.safeSend(
                    workerInfoMap.get(worker_id).getOutputStream(),
                    workerHotelJson.toJSONString()
            );
        }
    }

    private void connectToWorkers() {
        int port = 6000;
        int num_workers = Constants.NUM_WORKER_NODES;
        for (int worker_id = 0; worker_id < num_workers; worker_id++) {

            int finalPort = port + 1;
            port += 1;
            logger.info("Port: " + port);
            int finalWorker_id = worker_id;
            logger.info("Connecting to worker " + worker_id + " on port " + finalPort);
            Socket workerSocket = SocketUtils.createSocket("localhost", finalPort);
            try {
                workerSocket.setSoTimeout(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WorkerInfo workerInfo = new WorkerInfo(
                    finalWorker_id,
                    workerSocket,
                    SocketUtils.createDataInputStream(workerSocket),
                    SocketUtils.createDataOutputStream(workerSocket));

            workerInfoMap.put(finalWorker_id, workerInfo);


        }
    }


    private void readHotels() {
        try {
            JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH);
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

            // Iterate through the hotels array
            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;
                int manager_id = ((Long) hotel.get("manager_id")).intValue();
                int numReviews = ((Long) hotel.get("numReviews")).intValue();
                int numPeople = ((Long) hotel.get("numPeople")).intValue();
                double stars = ((Number) hotel.get("stars")).doubleValue();
                double price = ((Number) hotel.get("price")).doubleValue();
                String hotelName = (String) hotel.get("hotelName");
                String area = (String) hotel.get("area");
                String roomImage = (String) hotel.get("hotelImage");
                String availableDate = (String) hotel.get("availableDates");

                hotels.add(new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, availableDate, manager_id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void associateHotelsWithManagers() {
        Map<Integer, Manager> managerMap = new HashMap<>();
        for (User user : users) {
            if (user instanceof Manager) {
                managerMap.put(user.getId(), (Manager) user); // Correctly use manager.getId()
            }
        }
        for (Hotel hotel : hotels) {
            int manager_id = hotel.getManagerId();

            Manager manager = managerMap.get(manager_id);
            if (manager != null) {
                manager.addHotelsManaged(hotel);
            }
        }
    }
}
