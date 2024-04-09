//package org.aueb;
//
//import org.aueb.entities.Client;
//import org.aueb.entities.Hotel;
//import org.aueb.entities.Manager;
//import org.aueb.entities.User;
//import org.aueb.util.JSONReaderWriter;
//import org.aueb.util.SocketUtils;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//
//public class Master extends Thread {
//    private final ServerSocket serverSocket;
//    private final ArrayList<User> users;
//    private final ArrayList<Hotel> hotels;
//    private final JSONParser parser = new JSONParser();
//    private final Logger logger = LoggerFactory.getLogger(Master.class);
//    private static final String JSON_FILE_PATH = "bin/hotel.json";
//
//    private DataInputStream inputStream1;
//
//    private DataOutputStream outputStream;
//
//    private DataInputStream inputWorkerStream;
//
//    private DataOutputStream outputWorkerStream;
//
//    private ObjectInputStream in;
//
//    private ObjectOutputStream out;
//
//    private final Map<Integer, WorkerInfo> workerInfoMap;
//
//    private int numWorkerNodes;
//
//
//    public Master(int numWorkerNodes, int port) throws IOException {
//        this.users = new ArrayList<>();
//        this.hotels = new ArrayList<>();
//        this.workerInfoMap = new LinkedHashMap<>();
//        this.numWorkerNodes = numWorkerNodes;
//    }
//
////    private void initializeWorkers(int numWorkerNodes) throws IOException {
////
////        for (int i = 0; i < numWorkerNodes; i++) {
////            int port = 5001 + i;
////
////            Worker worker = new Worker(i, port);
////            worker_map.put(i, worker);
////            worker.start(); // Correctly start the thread
////        }
////
////        for (Hotel hotel : hotels) {
////            Worker worker = selectWorker(hotel.getHotelName()); // Assuming getName() returns the name of the hotel
////            worker.addHotel(hotel); // Add the hotel to the selected worker
////        }
////    }
//
////    private void connectToWorkers(int numWorkerNodes) throws IOException {
////        for (int i = 0; i < numWorkerNodes; i++) {
////            int port = 5001 + i;
////            MapWorkerPort.put(i, port);
////        }
////
////        MapWorkerPort.forEach((workerId, port) -> {
////
////            logger.info("Connecting to Worker " + workerId + " on port " + port);
////            try {
////                Socket workerSocket = new Socket("localhost", port); // Connect to the worker
////                System.out.println("Connected to Worker "  + " on port " + port);
////                this.inputWorkerStream = SocketUtils.createDataInputStream(workerSocket);
////                this.outputWorkerStream = SocketUtils.createDataOutputStream(workerSocket);
////
//////                in = new ObjectInputStream(workerSocket.getInputStream());
//////                out = new ObjectOutputStream(workerSocket.getOutputStream());
////
////
////                // Here, you'd communicate with the worker, for example, sending tasks or receiving updates
////
////                //workerSocket.close();
////            } catch (IOException e) {
////                System.err.println("Could not connect to Worker "  + " on port " + port);
////            }
////        });
////    }
//
//
//    //public User findTheUser() throws ParseException {
////        User user = null;
////
////        while (true) {
////
////            System.out.println("Waiting for request");
////            String request = SocketUtils.safeReceive(inputStream1);
////            JSONObject jsonObject = (JSONObject) parser.parse(request);
////
////            logger.info("Received request: " + request);
////
////            String type = (String) jsonObject.get("type");
////            String result = null;
////            switch (type) {
////                case "login":
////
////                    user = login(jsonObject);
////                    if (user != null) {
////                        logger.info("authenticated");
////                        result = "authenticated";
////
////                    } else {
////                        logger.info("not authenticated");
////                        result = "not authenticated";
////
////                    }
////                    break;
////                case "register":
////                    user = register(jsonObject);
////                    if (user != null) {
////                        logger.info("registered");
////                        result = "registered";
////                    }
////            }
////            String user_type;
////            JSONObject answer = new JSONObject();
////            if (result.equals("not authenticated")) {
////                user_type = " ";
////
////            } else {
////                assert user != null;
////                user_type = user.getClass().getSimpleName();
////            }
////
////
////            answer.put("result", result);
////            answer.put("user_type", user_type);
////            SocketUtils.safeSend(outputStream, answer.toJSONString());
////
////            return user;
////
////        }
////
////
////    }
//
//    public void run() {
//        while (true) {
//            try {
//                // Accept a new client connection
//                Socket clientSocket = serverSocket.accept();
//
//                // Create input and output streams for the client
//                inputStream1 = SocketUtils.createDataInputStream(clientSocket);
//                outputStream = SocketUtils.createDataOutputStream(clientSocket);
//
//
//                logger.info("New client connected: " + clientSocket.getRemoteSocketAddress());
//
//                User user = findTheUser();
//
//                String request = SocketUtils.safeReceive(inputStream1);
//                JSONObject jsonObject = (JSONObject) parser.parse(request);
//
//
//                logger.info("Received request: " + jsonObject.toJSONString());
//                String request_type = jsonObject.get("option").toString();
//                logger.info(request_type);
//                jsonObject.put("identity", user.getClass().getSimpleName());
//                jsonObject.put("id", user.getId());
//
//                if (user instanceof Manager) {
//
//                    if (request_type.equals("1") || request_type.equals("2")) {  // Add hotel or add available dates
//                        String name = jsonObject.get("hotelName").toString();
//                        Long worker_id = selectWorker(name);
//                        logger.info("Worker_id" + worker_id);
//                        SocketUtils.safeSend(
//                                workerInfoMap.get(worker_id).getOutputStream(),
//                                jsonObject.toJSONString()
//                        );
//                    } else {  // Show reservations
//                        for (Hotel hotel : ((Manager) user).getHotelsManaged()) {
//                            Long worker_id = selectWorker(hotel.getHotelName());
//                            logger.info("Worker_id" + worker_id);
//                            SocketUtils.safeSend(
//                                    workerInfoMap.get(worker_id).getOutputStream(),
//                                    jsonObject.toJSONString()
//                            );
//                        }
//                    }
//                } else if (user instanceof Client) {
//                    if (request_type.equals("2") || request_type.equals("3")) {  // Reserve or rate hotel
//                        String name = jsonObject.get("hotelName").toString();
//                        Long worker_id = selectWorker(name);
//                        logger.info("Worker_id" + worker_id);
//                        SocketUtils.safeSend(
//                                workerInfoMap.get(worker_id).getOutputStream(),
//                                jsonObject.toJSONString()
//                        );
//                    } else {  // Search hotels by filters
//                        for (Hotel hotel : hotels) {
//                            Long worker_id = selectWorker(hotel.getHotelName());
//                            logger.info("Worker_id" + worker_id);
//                            SocketUtils.safeSend(
//                                    workerInfoMap.get(worker_id).getOutputStream(),
//                                    jsonObject.toJSONString()
//                            );
//                        }
//                    }
//                }
//                Hotel hotel = (Hotel) in.readObject();
//                hotel.toString();
//
//                //logger.info(option);
//
//            } catch (IOException e) {
//                System.out.println("Error accepting client connection: " + e.getMessage());
//                e.printStackTrace();
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//
//    public static void main(String[] args) throws IOException {
//        int port = 8000;
//        int numWorkers = 1;
//        Master master = new Master(numWorkers, port);
////        master.initializeManagers();
////        master.initializeClients();
////        master.readHotels();
////        master.matchManagerHotel();
////
////        master.connectToWorkers();
////        master.sendHotelsToWorkers();
////
////        master.start();
//    }
//
//}
