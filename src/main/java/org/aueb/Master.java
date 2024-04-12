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
    private final Logger logger = LoggerFactory.getLogger(Master.class);
    private final Map<Integer, WorkerInfo> workerInfoMap;
    private List<User> users;
    private ServerSocket providerSocket;
    private Socket connection = null;
    private int numWorkerNodes;

    private final List<Integer> workerPorts = Arrays.asList(6001, 6002, 6003);

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

        manager = new Manager("eleni", "zanou", "eleni", "1234");
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

        for (int i = 0; i < workerPorts.size(); i++) {
            logger.info("Connecting to worker " + i + " on port " + workerPorts.get(i));
            int workerPort = workerPorts.get(i); // Corresponding port for the worker
            Socket workerSocket = SocketUtils.createSocket("localhost", workerPort);
            DataInputStream dis = SocketUtils.createDataInputStream(workerSocket);
            DataOutputStream dos = SocketUtils.createDataOutputStream(workerSocket);
            workerInfoMap.put(i, new WorkerInfo(i, workerSocket, dis, dos));
        }
    }

    private void readHotels() {
        try {
            JSONObject jsonObject = JSONReaderWriter.readJsonFile(Constants.JSON_FILE_PATH);
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");

            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;

                // Use default values if keys are missing or values are null
                int manager_id = hotel.get("manager_id") == null ? 0 : ((Long) hotel.get("manager_id")).intValue();
                int numReviews = hotel.get("numReviews") == null ? 0 : ((Long) hotel.get("numReviews")).intValue();
                int numPeople = hotel.get("numPeople") == null ? 0 : ((Long) hotel.get("numPeople")).intValue();
                double stars = hotel.get("stars") == null ? 0.0 : ((Number) hotel.get("stars")).doubleValue();
                double price = hotel.get("price") == null ? 0.0 : ((Number) hotel.get("price")).doubleValue();
                String hotelName = (String) hotel.getOrDefault("hotelName", "Unknown");
                String area = (String) hotel.getOrDefault("area", "Unknown");
                String roomImage = (String) hotel.getOrDefault("hotelImage", "default.png");

                List<String> availableDates = new ArrayList<>();
                Object availableDatesObj = hotel.get("availableDates");

                if (availableDatesObj instanceof JSONArray) {
                    JSONArray jsonAvailableDates = (JSONArray) availableDatesObj;
                    for (Object dateObj : jsonAvailableDates) {
                        availableDates.add((String) dateObj);
                    }
                }

                hotels.add(new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, availableDates, manager_id));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Consider more specific error handling depending on your application's needs
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
