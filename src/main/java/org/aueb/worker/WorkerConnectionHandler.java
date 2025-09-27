package org.aueb.worker;

import org.aueb.entities.Hotel;
import org.aueb.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class WorkerConnectionHandler implements Runnable {
    private DataInputStream inputStream; // Stream to read data from the master
    private DataOutputStream outputStream; // Stream to send data to the master
    private Socket masterThreadConnection; // Connection to the master
    private Socket reducerSocket; // Connection to the reducer
    private DataOutputStream outReducer; // Stream to send data to the reducer

    private final Logger logger = LoggerFactory.getLogger(WorkerConnectionHandler.class); // Logger for the WorkerConnectionHandler class
    private final List<Hotel> hotels; // List of hotels managed by this worker
    private final long id; // Identifier for the worker

    /**
     * Constructor to initialize the WorkerConnectionHandler with necessary streams and information.
     * @param masterThreadConnection the socket connection to the master
     * @param hotels list of hotels that this worker will manage
     * @param id identifier for this worker node
     */
    public WorkerConnectionHandler(
            Socket masterThreadConnection, List<Hotel> hotels, long id) {
        this.masterThreadConnection = masterThreadConnection;
        this.hotels = Collections.synchronizedList(hotels); // Synchronized list to manage concurrent access
        this.id = id;
        this.reducerSocket = SocketUtils.createSocket("localhost", Constants.REDUCER_PORT);
        this.outReducer = SocketUtils.createDataOutputStream(reducerSocket);

        initializeStreams();
    }

    /**
     * Initializes the data streams over the socket connection to the master.
     */
    private void initializeStreams() {
        try {
            // Create input and output streams for the client
            inputStream = SocketUtils.createDataInputStream(masterThreadConnection);
            outputStream = SocketUtils.createDataOutputStream(masterThreadConnection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures all connections and streams are closed properly to free up resources.
     */
    private void closeConnection() {
        try {
            // close reducer connection
            if (outReducer != null) outReducer.close();
            if (reducerSocket != null && !reducerSocket.isClosed()) reducerSocket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (masterThreadConnection != null && !masterThreadConnection.isClosed()) masterThreadConnection.close();
            logger.info("Connection from master closed successfully.");
        } catch (Exception e) {
            logger.error("Error closing connection: ", e);
        }
    }

    /**
     * Main run method that handles the incoming requests and processes them.
     */
    public void run() {

        logger.info("New connection with master: " + this.masterThreadConnection.getRemoteSocketAddress());
        try {
            serve();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException ignored) {
        } finally {
            closeConnection();
        }
    }


    /**
     * Continuous service loop to handle requests from the master.
     */
    public void serve() throws ParseException, IOException {
        while (true) {
            Request request = receiveRequest();
            handleRequest(request);
        }
    }

    /**
     * Receives and parses JSON requests from the master.
     * @return JSONObject representing the request
     * @throws IOException if an error occurs in reading the input stream
     */
    public Request receiveRequest() throws IOException {
        String request = SocketUtils.safeReceive(this.inputStream);

        return Request.fromJSONString(request);
    }

    /**
     * Handles the incoming request by parsing the JSON object and determining the type of request.
     * @param request JSONObject representing the request
     */
    public void handleRequest(Request request) throws ParseException {

        String user_role = (String) request.getBody().get("user_role");

        if (Objects.equals(user_role, "Manager")) {
            handleManagerRequest(request); // Handle manager requests
        } else if (Objects.equals(user_role, "Client")) {
            handleClientRequest(request); // Handle client requests
        }
    }

    /**
     * Handles requests specific to managers such as adding hotels, setting available dates.
     * @param request JSONObject representing the manager's request
     */
    private void handleManagerRequest(Request request) throws ParseException {
        Response response;

        String requestType = request.getType();

        switch (requestType) {
            case "1": // Add hotel to manager
                response = addHotelToManager(request);
                SocketUtils.safeSend(outputStream, response.toJSONString());
                break;
            case "2": // Add available dates
                response = addAvailableDatesToHotel(request);
                SocketUtils.safeSend(outputStream, response.toJSONString());
                break;
            case "3":   // Show reservations
                response = showReservations(request);
                SocketUtils.safeSend(outReducer, response.toJSONString());
                return;
            case "4":  // Reservations by area
                response = reservationByArea(request);
                SocketUtils.safeSend(outReducer, response.toJSONString());
                return;
            default:
                throw new RuntimeException("Unknown request type");
        }
    }



    /**
     * Handles requests specific to clients such as booking hotels, rating hotels.
     * @param request JSONObject representing the client's request
     */
    private void handleClientRequest(Request request) throws ParseException {
        JSONObject requestBody = request.getBody();
        Response response;
        String requestType = request.getType();


        switch (requestType) {
            case "1": // Search hotels
                List<JSONObject> filteredHotels = filterHotels(request);
                JSONArray hotelsArray = new JSONArray();
                for (JSONObject hotel : filteredHotels) {
                    hotelsArray.add(hotel);
                }
                /* Send the filtered hotels to the reducer */
                JSONObject rootObject = new JSONObject();
                rootObject.put("result", hotelsArray);  // Storing the JSONArray under the userId key
                rootObject.put("option", "1");
                rootObject.put("user_role", "Client");
                Response workerResponse = new Response(request.getSessionId(), "1", Response.Status.SUCCESS, "Search completed", rootObject);
                // Convert the entire JSONObject to a string and send it
                SocketUtils.safeSend(outReducer, workerResponse.toJSONString());
                break;
            case "2":

                response = makeReservation(request);
                SocketUtils.safeSend(outputStream, response.toJSONString());
                break;
            case "3":
                response = rateHotel(request);
                SocketUtils.safeSend(outputStream, response.toJSONString());
                break;
                // Return all available hotels
            case "4":
                List<JSONObject> hotels = this.hotels.stream().map(Hotel::toJson).toList();
                JSONArray hotelsArray_book = new JSONArray();
                hotelsArray_book.addAll(hotels);

                JSONObject obj = new JSONObject();
                obj.put("result", hotelsArray_book);  // Storing the JSONArray under the userId key
                obj.put("option", "4");
                obj.put("user_role", "Client");

                logger.info("Sending hotels to reducer" + obj.toJSONString());
                Response workerResponse_book = new Response(request.getSessionId(), "4", Response.Status.SUCCESS, "Search completed", obj);
                // Convert the entire JSONObject to a string and send it
                SocketUtils.safeSend(outReducer, workerResponse_book.toJSONString());
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
    }

    /**
     * Handles reservation requests by checking available dates and making reservations accordingly.
     * Note: This is a stubbed method and the actual implementation details have been commented out for clarity.
     *
     * @param request Contains all necessary information to process the reservation, including hotelName and dates.
     * @return A JSON object indicating whether the reservation was successful or not, along with the new available dates.
     */
    private Response makeReservation(Request request) throws ParseException {
        JSONObject requestBody = request.getBody();
        String hotelName = requestBody.get("hotelName").toString();
        String request_date = requestBody.get("Reservation dates").toString();
        String[] dates = request_date.split(" - ");
        boolean reservation = false;
        String foundDate = null;
        Hotel chooseHotel = null;

        synchronized (hotels) {

            for (Hotel hotel : hotels) {
                if (hotel.getHotelName().equalsIgnoreCase(hotelName)) {
                    chooseHotel = hotel;
                    break;
                }
            }
            if (chooseHotel != null) {
                if (!chooseHotel.getAvailableDates().isEmpty()) {
                    String[] date_X = null;
                    for (String date : chooseHotel.getAvailableDates()) {

                        date_X = date.split(" - ");
                        if (DateProcessing.compareDates(date_X[0], dates[0]) && DateProcessing.compareDates(dates[1], date_X[1])) {
                            reservation = true;
                            foundDate = date;
                            break;
                        }

                    }
                    if (reservation) {
                        int userId = ((Long) requestBody.get("user_id")).intValue();
                        chooseHotel.addReservations(userId, request_date);
                        if (!dates[0].equals(date_X[0])) {
                            String firstAvailableDate = date_X[0] + " - " + DateProcessing.subtractOneDay(dates[0]);
                            chooseHotel.addAvailableDates(firstAvailableDate);
                        }
                        String lastavailabledate = dates[1] + " - " + date_X[1];
                        chooseHotel.getAvailableDates().remove(foundDate);
                        chooseHotel.addAvailableDates(lastavailabledate);

                    }
                }
            }
        }
        if (reservation) {
            JSONObject body = new JSONObject();
            JSONArray jsonAvailableDates = new JSONArray();

            jsonAvailableDates.addAll(chooseHotel.getAvailableDates());
            body.put("availableDates", jsonAvailableDates);

            // Convert reservations Map to JSONObject
            JSONObject jsonReservations = new JSONObject();
            jsonReservations.putAll(chooseHotel.getReservations());
            body.put("reservations", jsonReservations);

            return new Response( request.getSessionId(),"2", Response.Status.SUCCESS, "The reservation was successful", body);
        } else {
            if (chooseHotel == null) {
                return new Response( request.getSessionId(), "2", Response.Status.NOT_FOUND, "Hotel not found", null);
            } else {
                return new Response( request.getSessionId(), "2", Response.Status.UNSUCCESSFUL, "There are not available dates ", null);
            }
        }
    }

    /**
     * Filters hotels based on various criteria such as area, date, number of people, price, and star rating.
     *
     * @param request JSON object containing filter criteria.
     * @return A list of JSON objects representing hotels that match the filter criteria.
     */
    private List<JSONObject> filterHotels(Request request) {
        JSONObject requestBody = request.getBody();

        String areaFilter = (String) requestBody.get("area");
        String dateFilter = (String) requestBody.get("date");
        Long numPeopleFilter = (Long) requestBody.get("numPeople");
        Double priceFilter = (Double) requestBody.get("price");
        Double starsFilter = (Double) requestBody.get("stars");

        return hotels.stream()
                .filter(hotel -> (areaFilter == null || hotel.getArea().equalsIgnoreCase(areaFilter)))
                .filter(hotel -> (dateFilter == null || hotel.getAvailableDates().contains(dateFilter)))
                .filter(hotel -> (numPeopleFilter == null || hotel.getNumPeople() >= numPeopleFilter.intValue()))
                .filter(hotel -> (priceFilter == null || hotel.getPrice() <= priceFilter))
                .filter(hotel -> (starsFilter == null || hotel.getStars() >= starsFilter))
                .map(Hotel::toJson)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new hotel under a manager's management based on the provided data in the request.
     *
     * @param request JSON object containing hotel data such as name, number of people, area, etc.
     * @return A JSON object indicating the success status of the operation.
     */
    private Response addHotelToManager(Request request) {
        JSONObject requestBody = request.getBody();

        String hotelName = (String) requestBody.get("hotelName");
        int numPeople = ((Long) requestBody.get("numPeople")).intValue();
        String area = (String) requestBody.get("area");
        double stars = ((Number) requestBody.get("stars")).doubleValue();
        int numReviews = ((Long) requestBody.get("numReviews")).intValue();
        String roomImage = (String) requestBody.get("roomImage");
        double price = ((Number) requestBody.get("price")).doubleValue();
        List<String> availableDates = new ArrayList<>();
        Map<Integer,List<String>> reservations = new HashMap<>();



        int userId = ((Long) requestBody.get("user_id")).intValue();

        // Adding a new hotel with the extracted and converted attributes
        synchronized (hotels) {

            Hotel hotel = new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage,price, availableDates,userId);
            hotel.setReservations(reservations);
            hotels.add(hotel);

        }


        return new Response(request.getSessionId(),
                "1",
                Response.Status.SUCCESS,
                "Hotel added successfully",
                null
        );
    }

    /**
     * Adds available dates to an existing hotel managed by a manager.
     *
     * @param request JSON object containing the hotel's name and the new dates to add.
     * @return A JSON object indicating whether the operation was successful or not.
     */
    private  Response addAvailableDatesToHotel(Request request) {
        boolean foundHotel = false;
        JSONObject requestBody = request.getBody();
        String hotelName = requestBody.get("hotelName").toString(); // Extract hotel name
        String availableDates = requestBody.get("availableDates").toString(); // Extract available dates
        int manager_id = ((Number) requestBody.get("user_id")).intValue(); // Extract manager ID

        Hotel selectedHotel = null;
        synchronized (hotels) {
            for (Hotel hotel : hotels) { // Iterate through the list of hotels
                int hotel_manager = hotel.getManagerId();
                if ((hotel.getHotelName()).equals(hotelName) ) { // Check if the hotel name matches the requested hotel
                    if (hotel_manager != manager_id) { // Check if the manager is authorized to add dates to this hotel
                        return new Response(request.getSessionId(), "2", Response.Status.UNSUCCESSFUL, "You are not authorized to add dates to this hotel", null);
                    }
                    foundHotel = true;
                    hotel.addAvailableDates(availableDates);
                    selectedHotel = hotel;
                    break;

                }
            }
        }
        if (foundHotel) {
            JSONObject body = new JSONObject();
            body.put("availableDates", selectedHotel.getAvailableDates());
            return new Response(request.getSessionId() , "2",Response.Status.SUCCESS, "Date added successfully", body);
        } else {
            return new Response(request.getSessionId(), "2", Response.Status.NOT_FOUND, "Hotel not found", null);
        }
    }

    /**
     * Handles reservation requests by area and returns the results to the reducer.
     * @param request JSON object containing the period for which reservations are requested.
     * @return A JSON object with the reservation counts by area.
     */
    private Response reservationByArea(Request request) throws ParseException {
        JSONObject requestBody = request.getBody();
        String period= requestBody.get("Period").toString();
        String[] periodSplit = period.split(" - ");
        Map<Hotel,Integer> reservationsHotels= new HashMap<>();
        Map<String, Integer> reservationsByAreaMap;
        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                int count=0;
                Map<Integer, List<String>> reservations= hotel.getReservations();
                for (Map.Entry<Integer, List<String>> entry : reservations.entrySet()) {
                    List<String> reservationDates= entry.getValue();
                    for(String dates:reservationDates){
                        String[] date = dates.split(" - ");
                        if (DateProcessing.compareDates(periodSplit[0], date[0]) && DateProcessing.compareDates(date[1], periodSplit[1])) {
                            count++;
                        }
                    }
                }
                if(count!=0){
                    reservationsHotels.put(hotel,count);
                }

            }

            reservationsByAreaMap = calculateReservationsByArea(reservationsHotels);

        }
        JSONObject body = new JSONObject();

        JSONObject result = new JSONObject();
        for (Map.Entry<String, Integer> entry : reservationsByAreaMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        body.put("option","4");
        body.put("user_role", "Manager");
        body.put("result", result);

        return new Response(
                request.getSessionId(), "4",
                Response.Status.SUCCESS,
                "Reservations retrieved successfully",
                body
        );
    }

    /**
     * Calculates the number of reservations by area.
     * @param reservationsHotels Map of hotels and their respective reservation counts.
     * @return Map of areas and their respective reservation counts.
     */
    public static Map<String, Integer> calculateReservationsByArea(Map<Hotel, Integer> reservationsHotels) {
        Map<String, Integer> reservationsByArea = new HashMap<>();

        for (Map.Entry<Hotel, Integer> entry : reservationsHotels.entrySet()) {
            Hotel hotel = entry.getKey();
            Integer reservations = entry.getValue();
            String area = hotel.getArea();

            reservationsByArea.put(area, reservationsByArea.getOrDefault(area, 0) + reservations);
        }

        return reservationsByArea;
    }

    /**
     * Retrieves all reservations for hotels managed by a specific manager.
     *
     * @param request JSON object containing the manager's user ID.
     * @return A JSON object listing all hotels and their reservations under the specified manager.
     */
    public  Response showReservations(Request request) {
        // Extract hotels managed by the specified manager and that have reservations
        JSONObject requestBody = request.getBody();
        List<Hotel> reservationsByManager = new ArrayList<>();
        List<Hotel> hotelsByManager = new ArrayList<>();

        int manager_id = ((Number) requestBody.get("user_id")).intValue();

        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                if (Integer.valueOf(hotel.getManagerId()).equals(manager_id)) {
                    hotelsByManager.add(hotel);
                }
            }

            for (Hotel hotel : hotelsByManager) {
                Map<Integer, List<String>> reservations = hotel.getReservations();
                if (!reservations.isEmpty()) {
                    reservationsByManager.add(hotel);
                }
            }
        }
        // Prepare and return the response containing the reservations.
        JSONObject body = new JSONObject();
        JSONArray hotelsJsonArray = hotelsToJsonArray(reservationsByManager);
        body.put("option","3");
        body.put("user_role", "Manager");
        body.put("result", hotelsJsonArray);

        return new Response(
                request.getSessionId(), "3",
                Response.Status.SUCCESS,
                "Reservations retrieved successfully",
                body
        );
    }

    /**
     * Updates the rating of a specified hotel based on new guest reviews.
     * Calculates the new average rating and updates the hotel's record.
     *
     * @param request JSON object containing the hotel name and new rating provided by a guest.
     * @return A JSON object indicating the success status of the update operation and the new ratings.
     */
    private Response rateHotel(Request request) {
        JSONObject requestBody = request.getBody();
        String hotelName = requestBody.get("hotelName").toString();
        boolean foundHotel = false;
        BigDecimal updatedStars = BigDecimal.ZERO;
        int updatedReviews = 0;

        synchronized (hotels) {
            for (Hotel hotel : hotels) {
                if (hotel.getHotelName().equalsIgnoreCase(hotelName)) {
                    BigDecimal newRating = BigDecimal.valueOf(((Number) requestBody.get("newRating")).doubleValue());
                    BigDecimal previousStars = BigDecimal.valueOf(hotel.getStars());
                    int previousReviews = hotel.getNumReviews();

                    // Calculating the new average
                    updatedStars = previousStars.multiply(BigDecimal.valueOf(previousReviews))
                            .add(newRating)
                            .divide(BigDecimal.valueOf(previousReviews + 1), 3, RoundingMode.HALF_UP);

                    updatedReviews = previousReviews + 1;

                    // Updating the hotel object
                    hotel.setStars(updatedStars.doubleValue());
                    hotel.setNumReviews(updatedReviews);

                    foundHotel = true;
                    break;
                }
            }
        }

        if (!foundHotel) {
            return new Response(request.getSessionId(), "error", Response.Status.NOT_FOUND, "Hotel not found", null);
        }

        JSONObject body = new JSONObject();
        body.put("updatedStars", updatedStars.doubleValue());
        body.put("updatedReviews", updatedReviews);

        return new Response(request.getSessionId(), "success", Response.Status.SUCCESS, "Hotel rated successfully", body);
    }



    /**
     * Converts a list of Hotel objects into a JSON array format. This is useful for serializing the
     * list of hotels to JSON format for network transmission or storage.
     *
     * @param hotels List of Hotel objects to be converted.
     * @return JSONArray containing the serialized JSON representation of hotels.
     */
    private JSONArray hotelsToJsonArray(List<Hotel> hotels) {
        JSONArray hotelsJsonArray = new JSONArray();
        for (Hotel hotel : hotels) {
            JSONObject hotelJson = new JSONObject();
            hotelJson.put("hotelName", hotel.getHotelName());
            hotelJson.put("numPeople", hotel.getNumPeople());
            hotelJson.put("area", hotel.getArea());
            hotelJson.put("stars", hotel.getStars());
            hotelJson.put("numReviews", hotel.getNumReviews());
            hotelJson.put("hotelImage", hotel.getRoomImage());
            hotelJson.put("price", hotel.getPrice());
            hotelJson.put("availableDates", hotel.getAvailableDates());
            hotelJson.put("managerId", hotel.getManagerId());
            hotelJson.put("reservations", hotel.getReservations());
            hotelsJsonArray.add(hotelJson);
        }
        return hotelsJsonArray;
    }
}
