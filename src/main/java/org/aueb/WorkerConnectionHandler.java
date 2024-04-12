package org.aueb;

import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.util.JSONUtils;
import org.aueb.util.Response;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WorkerConnectionHandler extends Thread {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket connection;

    private DataInputStream inReducer;
    private DataOutputStream outReducer;

    private final Logger logger = LoggerFactory.getLogger(WorkerConnectionHandler.class);
    private final List<Hotel> hotels;
    private final long id;

    public WorkerConnectionHandler(
            Socket connection, List<Hotel> hotels, long id, DataInputStream inReducer, DataOutputStream outReducer) {
        this.connection = connection;
        this.hotels = Collections.synchronizedList(new ArrayList<>());
        this.id = id;
        this.inReducer = inReducer;
        this.outReducer = outReducer;


        try {
            // Create input and output streams for the client
            inputStream = SocketUtils.createDataInputStream(connection);
            outputStream = SocketUtils.createDataOutputStream(connection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            logger.info("New client connected: " + this.connection.getRemoteSocketAddress());
            serve();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close(); // Ensure the connection is properly closed
                }
            } catch (IOException e) {
                logger.error("Error closing connection", e);
            }
        }
    }


    public void serve() {
        while (true) {
            JSONObject request = receiveRequest();
            handleRequest(request);
        }
    }

    public JSONObject receiveRequest() {
        String request = SocketUtils.safeReceive(this.inputStream);

        return JSONUtils.parseJSONString(request);
    }

    public void handleRequest(JSONObject request) {
        logger.info("Handling request: " + request.toJSONString());

        receiveHotels(request);

        String user_role = (String) request.get("user_role");

        logger.info("User role: " + user_role);
        logger.info("Request type: " + request.get("type"));

        if (Objects.equals(user_role, "Manager")) {
            logger.info("Manager");
            handleManagerRequest(request);
        }
       else if(Objects.equals(user_role, "Client")) {
            logger.info("Client");
            handleClientRequest(request);
       }
    }
    private void handleManagerRequest(JSONObject request) {
        JSONObject response;

        String requestType = (String) request.get("type");

        logger.info("Handling manager request: " + requestType);

        switch (requestType) {
            case "1": // Add hotel to manager
                response = addHotelToManager(request);

                break;
            case "2": // Add available dates
                response = addAvailableDatesToHotel(request);

                break;
            case "3":   // Show reservations
                response = showReservations(request);

                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
        // Send the response back to master
        SocketUtils.safeSend(outputStream, response.toJSONString());

    }

    private void handleClientRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        String requestType = (String) request.get("type");

        switch (requestType) {
            case "1":
                List<JSONObject> filteredHotels = filterHotels(request);
                JSONArray hotelsArray = new JSONArray();
                for(JSONObject hotel: filteredHotels){
                    hotelsArray.add(hotel);
                }
                SocketUtils.safeSend(outReducer,hotelsArray.toJSONString());
            case "2":
                break;
            case "3":
                response = rateHotel(request);
                SocketUtils.safeSend(outputStream,response.toJSONString());
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
//        SocketUtils.safeSend(outputStream,response.toJSONString());
    }

    private List<JSONObject> filterHotels(JSONObject request) {

        logger.info("Filtering hotels");
        logger.info("Request: " + request.toJSONString());
        String areaFilter = (String) request.get("area");
        String dateFilter = (String) request.get("date");
        Long numPeopleFilter = (Long) request.get("numPeople");
        Double priceFilter = (Double) request.get("price");
        Double starsFilter = (Double) request.get("stars");

        List<JSONObject> filteredHotelsJson = hotels.stream()
                .filter(hotel -> (areaFilter == null || hotel.getArea().equalsIgnoreCase(areaFilter)))
                .filter(hotel -> (dateFilter == null || hotel.getAvailableDates().contains(dateFilter)))
                .filter(hotel -> (numPeopleFilter == null || hotel.getNumPeople() >= numPeopleFilter.intValue()))
                .filter(hotel -> (priceFilter == null || hotel.getPrice() <= priceFilter))
                .filter(hotel -> (starsFilter == null || hotel.getStars() >= starsFilter))
                .map(Hotel::toJson)  // Assuming a method toJsonObject() in Hotel class
                .collect(Collectors.toList());
        return filteredHotelsJson;
    }

    private synchronized JSONObject addHotelToManager(JSONObject request) {
        String hotelName = (String) request.get("hotelName");
        int numPeople = ((Long) request.get("numPeople")).intValue();
        String area = (String) request.get("area");
        double stars = ((Number) request.get("stars")).doubleValue();
        int numReviews = ((Long) request.get("numReviews")).intValue();
        String roomImage = (String) request.get("roomImage");
        double price = ((Number) request.get("price")).doubleValue();
        JSONArray jsonAvailableDates = (JSONArray) request.get("availableDates");
        List<String> availableDates = new ArrayList<>();

        // Converting JSONArray to List<String>
        if (jsonAvailableDates != null) {
            jsonAvailableDates.forEach(date -> availableDates.add((String) date));
        }

        int userId = ((Long) request.get("user_id")).intValue();

        // Adding a new hotel with the extracted and converted attributes
        synchronized (hotels) {
            hotels.add(new Hotel(
                    hotelName,
                    numPeople,
                    area,
                    stars,
                    numReviews,
                    roomImage,
                    price,
                    availableDates, // Pass the converted List<String>
                    userId
            ));
        }


        return Response.create("1",
                Response.Status.SUCCESS,
                "Hotel added successfully",
                null
        );
    }
    private synchronized JSONObject addAvailableDatesToHotel(JSONObject request) {
        boolean foundHotel = false;
        String hotelName = request.get("hotelName").toString();
        String availableDates = request.get("availableDates").toString();
        logger.info(hotels.toString());
        Hotel selectedHotel=null;
        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                if ((hotel.getHotelName()).equals(hotelName)) {
                    foundHotel = true;
                    hotel.addAvailableDates(availableDates);
                    selectedHotel= hotel;
                    break;

                }
            }
        }


        if (foundHotel) {
            JSONObject body = new JSONObject();
            body.put("availableDates",selectedHotel.getAvailableDates());
            return Response.create("2",Response.Status.SUCCESS, "Date added successfully",body );
        } else {
            return Response.create("2",Response.Status.NOT_FOUND, "Hotel not found", null);
        }
    }
    public synchronized JSONObject showReservations(JSONObject request) {
        // Extract hotels managed by the specified manager and that have reservations
        List<Hotel> reservationsByManager = new ArrayList<>();
        List<Hotel> hotelsByManager = new ArrayList<>();
        int manager_id = (int) request.get("user_id");
        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                if (hotel.getManagerId() == manager_id) {
                    hotelsByManager.add(hotel);
                }
            }
            for (Hotel hotel : hotelsByManager) {
                Map<Client, String> reservations = hotel.getReservations();
                if (!reservations.isEmpty()) {
                    reservationsByManager.add(hotel);
                }
            }
        }

        JSONObject body = new JSONObject();
        JSONArray hotelsJsonArray = hotelsToJsonArray(reservationsByManager);
        body.put("hotels", hotelsJsonArray);

        return Response.create("3",
                Response.Status.SUCCESS,
                "Reservations retrieved successfully",
                body
        );
    }

    private synchronized JSONObject rateHotel(JSONObject request) {
        String hotelName = request.get("hotelName").toString();
        boolean foundHotel = false;
        BigDecimal updatedStars = BigDecimal.ZERO;
        int updatedReviews= 0;

        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                if (hotel.getHotelName().equalsIgnoreCase(hotelName)) {
                    BigDecimal newRating = BigDecimal.valueOf(((Number) request.get("newRating")).doubleValue());
                    BigDecimal previousStars = BigDecimal.valueOf(hotel.getStars());
                    int previousReviews = hotel.getNumReviews();

                    // Calculating the new average
                    updatedStars = previousStars.multiply(BigDecimal.valueOf(previousReviews))
                            .add(newRating)
                            .divide(BigDecimal.valueOf(previousReviews + 1), 2, RoundingMode.HALF_UP);

                    updatedReviews = previousReviews + 1;

                    logger.info("Updated stars: " + updatedStars);
                    // Updating the hotel object, assuming setStars and setNumReviews are thread-safe or adequately synchronized
                    hotel.setStars(updatedStars.doubleValue());
                    hotel.setNumReviews(updatedReviews);
                    foundHotel = true;
                    break;
                }
            }
        }

        if (!foundHotel) {
            return Response.create("error", Response.Status.NOT_FOUND, "Hotel not found", null);
        }

        JSONObject body = new JSONObject();
        body.put("updatedStars", updatedStars.doubleValue());
        body.put("updatedReviews", updatedReviews);

        return Response.create("3", Response.Status.SUCCESS, "Hotel rated successfully", body);
    }
    private JSONArray hotelsToJsonArray(List<Hotel> hotels) {
        JSONArray hotelsJsonArray = new JSONArray();
        for (Hotel hotel : hotels) {
            JSONObject hotelJson = new JSONObject();
            hotelJson.put("hotelName", hotel.getHotelName());
            hotelJson.put("numPeople", hotel.getNumPeople());
            hotelJson.put("area", hotel.getArea());
            hotelJson.put("stars", hotel.getStars());
            hotelJson.put("numReviews", hotel.getNumReviews());
            hotelJson.put("roomImage", hotel.getRoomImage());
            hotelJson.put("price", hotel.getPrice());
            hotelJson.put("availableDates", hotel.getAvailableDates());
            hotelJson.put("managerId", hotel.getManagerId());
            hotelsJsonArray.add(hotelJson);
        }
        return hotelsJsonArray;
    }
    private void receiveHotels(JSONObject request) {
        JSONArray hotelsArray = (JSONArray) request.get("hotels");
        logger.info("Receive Hotels", hotelsArray);

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
                this.hotels.add(new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, availableDates, manager_id));
            }
        }
    }
}