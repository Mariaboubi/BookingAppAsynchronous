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
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Master extends Thread {
    private final ServerSocket serverSocket;
    private final Map<Integer, Worker> worker_map;
    private final ArrayList<User> users;
    private final ArrayList<Hotel> hotels;
    private final JSONParser parser = new JSONParser();
    private final Logger logger = LoggerFactory.getLogger(Master.class);
    private static final String JSON_FILE_PATH = "bin/hotel.json";

     private DataInputStream inputStream1;

     private DataOutputStream outputStream;

    private DataInputStream inputWorkerStream;

    private DataOutputStream outputWorkerStream;

    private ObjectInputStream in;

    private ObjectOutputStream out;

     private Map<Integer,Integer> MapWorkerPort;

     private int numWorkerNodes;



    public Master(int numWorkerNodes, int port) throws IOException {
        this.users = new ArrayList<>();
        this.hotels = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        this.worker_map = new HashMap<>();
        this.MapWorkerPort= new HashMap<>();
        this.numWorkerNodes=numWorkerNodes;
    }

    private void initializeWorkers(int numWorkerNodes) throws IOException {

        for (int i = 0; i < numWorkerNodes; i++) {
            int port = 5001 + i;

            Worker worker = new Worker(i, port);
            worker_map.put(i, worker);
            worker.start(); // Correctly start the thread
        }

        for (Hotel hotel : hotels) {
            Worker worker = selectWorker(hotel.getHotelName()); // Assuming getName() returns the name of the hotel
            worker.addHotel(hotel); // Add the hotel to the selected worker
        }
    }

    private void connectToWorkers(int numWorkerNodes) throws IOException {
        for (int i = 0; i < numWorkerNodes; i++) {
            int port = 5001 + i;
            MapWorkerPort.put(i, port);
        }

        MapWorkerPort.forEach((workerId, port) -> {

            logger.info("Connecting to Worker " + workerId + " on port " + port);
            try {
                Socket workerSocket = new Socket("localhost", port); // Connect to the worker
                System.out.println("Connected to Worker "  + " on port " + port);
                this.inputWorkerStream = SocketUtils.createDataInputStream(workerSocket);
                this.outputWorkerStream = SocketUtils.createDataOutputStream(workerSocket);

                out = new ObjectOutputStream(workerSocket.getOutputStream());
                in = new ObjectInputStream(workerSocket.getInputStream());


                // Here, you'd communicate with the worker, for example, sending tasks or receiving updates

                //workerSocket.close();
            } catch (IOException e) {
                System.err.println("Could not connect to Worker "  + " on port " + port);
            }
        });
    }
    public void run() {


        while (true) {
            try {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();

                // Create input and output streams for the client
                inputStream1 = SocketUtils.createDataInputStream(clientSocket);
                outputStream = SocketUtils.createDataOutputStream(clientSocket);


                logger.info("New client connected: " + clientSocket.getRemoteSocketAddress());

                User user = findTheUser();

                String option = SocketUtils.safeReceive(inputStream1);
                JSONObject jsonObject = (JSONObject) parser.parse(option);


                logger.info("Received request: " + jsonObject.toJSONString());
                String type =  jsonObject.get("option").toString();
                logger.info (type);
                jsonObject.put("identity", user.getClass().getSimpleName());
                jsonObject.put("id", user.getId());

                if(user instanceof Manager){

                    if(type.equals("1") || type.equals("2") ) {
                        String name = jsonObject.get("hotelName").toString();
                        Worker selectedWorker= selectWorker(name);
                        logger.info("Worker_id"+ selectedWorker.getId() );
                        SocketUtils.safeSend(this.outputWorkerStream, jsonObject.toJSONString());
                    }else{
                        for(Hotel hotel: ((Manager) user).getHotelsManaged()){
                            Worker selectedWorker= selectWorker(hotel.getHotelName());
                            logger.info("Worker_id"+ selectedWorker.getId() );
                            SocketUtils.safeSend(this.outputWorkerStream, jsonObject.toJSONString());
                        }
                    }
                } else if (user instanceof Client) {
                    if(type.equals("2") || type.equals("3") ) {
                        String name = jsonObject.get("hotelName").toString();
                        Worker selectedWorker= selectWorker(name);
                        logger.info("Worker_id"+ selectedWorker.getId() );
                        SocketUtils.safeSend(this.outputWorkerStream, jsonObject.toJSONString());
                    }else{
                        for(Hotel hotel:hotels){
                            Worker selectedWorker= selectWorker(hotel.getHotelName());
                            logger.info("Worker_id"+ selectedWorker.getId() );
                            SocketUtils.safeSend(this.outputWorkerStream, jsonObject.toJSONString());
                        }
                    }
                }
                Hotel hotel= (Hotel) in.readObject();
                hotel.toString();

                //logger.info(option);

            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public User findTheUser() throws ParseException {
       User user = null;

                while (true) {

                    System.out.println("Waiting for request");
                    String request = SocketUtils.safeReceive(inputStream1);
                    JSONObject jsonObject = (JSONObject) parser.parse(request);

                    logger.info("Received request: " + request);

                    String type = (String) jsonObject.get("type");
                    String result=null;
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
                            if(user!=null){
                                logger.info("registered");
                                result = "registered";
                            }
                    }
                    String user_type;
                    JSONObject answer = new JSONObject();
                    if(result.equals("not authenticated")){
                        user_type = " ";

                    } else {
                        assert user != null;
                        user_type = user.getClass().getSimpleName();
                    }


                    answer.put("result",result );
                    answer.put("user_type",user_type);
                    SocketUtils.safeSend(outputStream, answer.toJSONString());

                    return user;

                }


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
            //managerConsole.setManager((Manager) user);
            return user;

        } else if (identity.equals("Client")) {
            user = new Client(name, lastname, username, password);
            users.add(user);
            return user;
        }
        return null;
    }

    private User login(JSONObject jsonObject) {



            String username = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");

            logger.info(username);
            logger.info(password);
            for (User user : users) {
                if (username.equals(user.getUserName()) && user.getPassword().equals(password)) {
                    //managerConsole.setManager((Manager)user);
                    logger.info("User found");
                    return user;
                }
            }
            return  null;


    }
    public Worker selectWorker(String roomName) {
        int workerId = hashFunction(roomName) % numWorkerNodes;
        return worker_map.get(workerId);
    }

    private int hashFunction(String roomName) {
        return roomName.hashCode();
    }


    private void readHotels() {

        try {
            JSONObject jsonObject = JSONReaderWriter.readJsonFile(JSON_FILE_PATH);
            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
            //Map<Integer, Manager> managerMap = createManagerMap(users);

            // Iterate through the hotels array
            for (Object hotelObj : hotelsArray) {
                JSONObject hotel = (JSONObject) hotelObj;
                int id = ((Long) hotel.get("id")).intValue();
                int manager_id = ((Long) hotel.get("manager_id")).intValue();
                String hotelName = (String) hotel.get("hotelName");
                String noOfPersons = (String) hotel.get("noOfPersons");
                int noOfReviews = ((Long) hotel.get("noOfReviews")).intValue();
                String area = (String) hotel.get("area");
                double stars = ((Number) hotel.get("stars")).doubleValue();
                String roomImage = (String) hotel.get("hotelImage");
                double price = ((Number) hotel.get("price")).doubleValue();
                String availableDate = (String) hotel.get("availableDates");

                Hotel newHotel = new Hotel(hotelName, noOfPersons, area, stars, noOfReviews, roomImage, price, availableDate, manager_id);
                //System.out.println(newHotel.getHotelName());
                hotels.add(newHotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void matchManagerHotel() {
        Map<Integer, Manager> managerMap = createManagerMap(users);

        for (Hotel hotel : hotels) {
            int manager_id = hotel.getManager();

            Manager manager = managerMap.get(manager_id);
            if (manager != null) {
                manager.addHotelsManaged(hotel);
            }

        }
    }
    private Map<Integer, Manager> createManagerMap(ArrayList<User> users) {
        Map<Integer, Manager> map = new HashMap<>();
        for (User user : users) {
            if (user instanceof Manager) {
                map.put(user.getId(), (Manager) user); // Correctly use manager.getId()
            }
        }
        return map;
    }


    private void initializeManagers() {
        Manager manager;
        manager = new Manager("maria", "boubi", "mariab", "1234");
        //managers.add(manager);
        users.add(manager);
        manager = new Manager("eleni", "zanou", "zanou", "4444");

        //managers.add(manager);
        users.add(manager);

        manager = new Manager("mariaSam", "samara", "samaraM", "maria111");
        //managers.add(manager);
        users.add(manager);
    }

    private void initializeClients() {
        Client client = new Client("georgos", "tsibo", "sambo", "2003");
        users.add(client);
    }

    public static void main(String[] args) throws IOException {
        int port = 8000;
        int numWorkers = 3;
        Master master = new Master(numWorkers, port);
        master.initializeManagers();
        master.initializeClients();
        master.readHotels();
        master.matchManagerHotel();
        master.initializeWorkers(numWorkers);
        master.connectToWorkers(numWorkers);
        master.start();
    }

}
