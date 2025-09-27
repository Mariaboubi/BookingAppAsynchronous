package org.aueb.console;

import org.aueb.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The Console class implements an application that interacts with a server for various hotel management tasks
 * through a text-based user interface.
 */
public class Console {
    private final Scanner scanner = new Scanner(System.in); // Scanner to read user input
    private final Socket clientSocket; // Socket to connect to the server
    private final DataInputStream inputStream; // Input stream to read data from the server
    private final DataOutputStream outputStream; // Output stream to send data to the server

    /**
     * Constructor to create a new Console instance.
     * Connects to a server at the specified host and port.
     * @param host The host name of the server.
     * @param port The port number of the server.
     */
    public Console(String host, int port) {
        this.clientSocket = SocketUtils.createSocket(host, port);
        this.inputStream = SocketUtils.createDataInputStream(this.clientSocket);
        this.outputStream = SocketUtils.createDataOutputStream(this.clientSocket);
    }

    public static void main(String[] args) {
        /* Create a new Console instance and start the application */
        Console console = new Console("localhost", 8000);

        console.start_app();
    }

    /*
    *  Closes the connection with the server and exits the application
    *  */
    private void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        exit(0);
    }

    /**
     * Handles the user login process by repeatedly prompting for username and password
     * until successful authentication or an error occurs. It constructs a JSON object
     * to send login credentials to the server and waits for a response to confirm authentication.
     * If authentication fails, it informs the user and prompts to try again.
    */
    private void loginMenu() {

        while (true) {
            System.out.print("Give me the username: ");
            String username = this.readUserInput();

            System.out.print("Give me the password: ");
            String password = this.readUserInput();

            try {
                /* Creates a JSON object with the login request data */
                JSONObject request = new JSONObject();
                request.put("type", "login");
                request.put("username", username);
                request.put("password", password);

                /* Sends the login request to the server */
                SocketUtils.safeSend(this.outputStream, request.toJSONString());

                /* Receives the answer from server */
                Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));

                JSONObject body = response.getBody();
                String string_response = (String) body.get("result");

                /* if the user is not authenticated, then it will prompt the user to enter the credentials again */
                if (string_response.equals("authenticated")) {
                    directToConsole(body);
                    return;
                } else {
                    System.out.println("Wrong credentials");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // the response will always be not null
    }

    /**
     * Provides a registration interface where users can enter personal information and choose to be a Manager or a Client.
     * @throws Exception If there are I/O errors during communication with the server.
     */
    private void registerMenu() throws Exception {
        System.out.print("Give me your name: ");
        String name = this.readUserInput();

        System.out.print("Give me your last name: ");
        String lastname = this.readUserInput();

        System.out.print("Create a username: ");
        String username = this.readUserInput();

        System.out.print("Create a password: ");
        String password = this.readUserInput();

        String user_role;
        /* Prompt the user to choose between Manager and Client */
        while (true) {
            System.out.print("Do you want to be a Manager or Client? ");
            user_role = this.readUserInput();

            if (user_role.equalsIgnoreCase("Manager") || user_role.equalsIgnoreCase("Client")) {
                break;
            } else {
                System.out.println("Invalid option");
                System.out.println();
            }
        }

        /* Creates a JSON object with the register request data */
        JSONObject registerRequest = new JSONObject();
        registerRequest.put("type", "register");
        registerRequest.put("name", name);
        registerRequest.put("lastname", lastname);
        registerRequest.put("username", username);
        registerRequest.put("password", password);
        registerRequest.put("user_role", user_role);

        /* Sends to Master the register data */
        SocketUtils.safeSend(this.outputStream, registerRequest.toJSONString());

        /* Receives the answer from server */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
        JSONObject registerResponse = response.getBody();
        directToConsole(registerResponse);
    }


    /**
     * Directs the user to the appropriate console interface based on their role.
     * This method decides whether to initiate the Manager's console or the Client's console
     * based on the user role defined in the provided JSON body from the server response.
     *
     * @param body The JSON object containing the response data from the server,
     *             which includes the user's role and the result of the previous request.
     * @throws Exception if there is an error during the execution of the console methods,
     *                   which could include IOException or NumberFormatException among others.
     */
    private void directToConsole(JSONObject body) throws Exception {
        String user_role = (String) body.get("user_role");

        System.out.println();

        if (user_role.equalsIgnoreCase("Manager")) {
            System.out.println("Manager Console");
            this.ManagerConsole();

        } else if (user_role.equalsIgnoreCase("Client")){
            System.out.println("Client Console");
            this.clientConsole();
        }
    }

    /**
     * Displays options to the client and handles client commands.
     * @throws IOException If network errors occur.
     */
    private void clientConsole() throws IOException {
        while (true) {
            System.out.println("1.Choose a filter: ");
            System.out.println("2.Book a hotel: ");
            System.out.println("3.Rate a hotel (1-5): ");
            System.out.println("4. Exit");
            System.out.print("> ");
            String optionInput = this.readUserInput();

            int option;
            try {
                option = Integer.parseInt(optionInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                System.out.println();
                continue;  // Skip to the next iteration of the loop
            }

            switch (option) {
                case 1:
                    this.filterHotel(); // Filter hotels based on user preferences
                    break;
                case 2:
                    JSONObject request= new JSONObject();
                    request.put("type","4");
                    SocketUtils.safeSend(this.outputStream, request.toJSONString());
                    Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
                    JSONObject body = response.getBody();
                    if (body != null) {
                        JSONArray hotels = (JSONArray) body.get("results");

                        System.out.println();
                        System.out.println("Results: ");
                        System.out.println();
                        // print all hotels
                        for (Object hotel : hotels) {
                            JSONObject hotelObj = (JSONObject) hotel;
                            hotelObj.remove("manager_id");

                            System.out.println("Hotel: " + hotelObj.get("hotelName"));
                            System.out.println("Area: " + hotelObj.get("area"));
                            System.out.println("Rating: " + hotelObj.get("stars"));
                            System.out.println("Price: $" + hotelObj.get("price"));
                            System.out.println("Number of people: " + hotelObj.get("numPeople"));
                            System.out.println(hotelObj.get("hotelImage"));
                            System.out.println("Available Dates: " + hotelObj.get("availableDates"));
                            System.out.println("Number of Reviews: " + hotelObj.get("numReviews"));
                            System.out.println("-------------------------------------------------");
                        }
                    }
                    else {
                        System.out.println(response.getMessage());
                        System.out.println();
                    }
                    this.makeReservation(); // Make a reservation
                    break;
                case 3:
                    JSONObject rateRequest= new JSONObject();
                    rateRequest.put("type","5");

                    SocketUtils.safeSend(this.outputStream, rateRequest.toJSONString());
                    Response rateResponse = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
                    JSONObject rateBody = rateResponse.getBody();

                    if(rateBody!=null) {
                        Map<String, List<String>> reservations = (Map<String, List<String>>) rateBody.get("reservations");
                        for (Map.Entry<String, List<String>> entry : reservations.entrySet()) {
                            System.out.println("Hotel: " + entry.getKey());
                            System.out.println("Reservations: " + entry.getValue());
                            System.out.println("---------------");
                        }
                    }

                    this.rateHotel(); // Rate a hotel
                    break;
                case 4:
                    closeConnection();
                    return;  // Exit the method after closing the connection
                default:
                    // Invalid option
                    System.out.println("Invalid option. Please choose from 1 to 4.");
                    System.out.println();
                    break;
            }
        }
    }

    /**
     * Displays options to the manager and handles manager commands.
     * @throws Exception If network or parsing errors occur.
     */
    private void ManagerConsole() throws Exception {
        while (true) {
            System.out.println("1. Add a room: ");
            System.out.println("2. Add available dates for your hotel: ");
            System.out.println("3. Show your reservations: ");
            System.out.println("4. Exit");
            System.out.print("> ");

            String optionInput = this.readUserInput();

            /* Handling non-integer input to prevent NumberFormatException */
            int option;
            try {
                option = Integer.parseInt(optionInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue; // Continue to the next iteration of the loop
            }

            switch (option) {
                case 1:
                    this.addHotel(); // Add a hotel
                    break;
                case 2:
                    this.addAvailableDates(); // Add available dates for a hotel
                    break;
                case 3:
                    this.showReservations(); // Show reservations
                    break;
                case 4:
                    closeConnection();
                    return; // Exit the method to stop the console
                default:
                    // Invalid option
                    System.out.println("Invalid option. Please choose from 1 to 4.");
                    break;
            }
        }
    }

    /* Client actions */

    /**
     * Handles making a hotel reservation for a user.
     * Prompts the user to input the hotel they want to book and the date of their visit,
     * then sends a reservation request to the server and handles the response.
     *
     * @throws IOException If an I/O error occurs while sending/receiving data.
     */
    private void makeReservation() throws IOException {
        System.out.print("Which hotel do you want to book? ");
        String hotelName = this.readUserInput();
        System.out.print("When are you visiting: (YYYY-MM-DD - YYYY-MM-DD ) ");
        String answer = DateProcessing.checkDates(false);

        /* Send request to server */
        JSONObject reservationRequest = new JSONObject();
        reservationRequest.put("type", "2");
        reservationRequest.put("hotelName", hotelName);
        reservationRequest.put("Reservation dates", answer);

        SocketUtils.safeSend(this.outputStream, reservationRequest.toJSONString());

        /* Handle response */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));

        System.out.println();
        System.out.println(response.getMessage());
        System.out.println();
    }

    /**
     * Allows a user to rate a hotel.
     * Requests input for the hotel name and desired rating, then sends this data to the server.
     *
     * @throws IOException If an I/O error occurs during data transmission.
     */
    private void rateHotel() throws IOException {
        System.out.print("Which hotel do you want to rate: ");
        String hotelName = this.readUserInput();

        System.out.print("Choose between 1 and 5 stars: ");
        double newRating = Double.parseDouble(scanner.nextLine());

        /* Send request to server */
        JSONObject rate = new JSONObject();

        rate.put("type", "3");
        rate.put("hotelName", hotelName);
        rate.put("newRating", newRating);

        SocketUtils.safeSend(this.outputStream, rate.toJSONString());

        /* Handle response */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));

        System.out.println();
        System.out.println(response.getMessage());
        System.out.println();
    }

    /**
     * Filters hotels based on user-specified criteria such as area, dates, price, etc.
     * Collects filter parameters from the user and sends them to the server for processing.
     *
     * @throws IOException If there is an error communicating with the server.
     */
    private void filterHotel() throws IOException {

        System.out.println("Choose one or multiple filters seperated by comma");
        System.out.println("1.Area");
        System.out.println("2.Available dates");
        System.out.println("3.Number of people");
        System.out.println("4.Price");
        System.out.println("5.Stars");
        System.out.print("> ");

        String option = this.readUserInput();
        String[] options = option.split(",");

        JSONObject filter = new JSONObject();
        JSONArray selectedFilters = new JSONArray();

        for (String optionStr : options) {
            int filterOption = Integer.parseInt(optionStr.trim());
            switch (filterOption) {
                case 1:
                    System.out.print("Enter the area you're interested in: ");
                    String area = this.readUserInput();

                    selectedFilters.add("1");
                    filter.put("area", area);
                    break;

                case 2:
                    System.out.print("Enter the date you're interested in (YYYY-MM-DD): ");
                    String date = this.readUserInput();

                    selectedFilters.add("2");
                    filter.put("date", date);
                    break;

                case 3:
                    System.out.print("Choose the number of persons: ");
                    String numPeopleStr = this.readUserInput();
                    int numPeople = Integer.parseInt(numPeopleStr);

                    selectedFilters.add("3");
                    filter.put("numPeople", numPeople);
                    break;
                case 4:
                    String priceStr;
                    double price = 0;
                    do {
                        try {
                            System.out.print("Choose the maximum price per night: ");
                            priceStr = this.readUserInput();
                            price = Double.parseDouble(priceStr);
                        } catch (NumberFormatException e) {
                            System.out.print("Price must be a number. ");
                            continue;
                        }
                        if (price <= 0) {
                            System.out.print("Price must be a positive number. ");
                        }
                    } while (price <= 0);

                    selectedFilters.add("4");
                    System.out.println(price);
                    filter.put("price", price);
                    break;

                case 5:
                    String starsStr;
                    double stars = 0;
                    do {
                        System.out.println("test");
                        try {
                            System.out.print("Choose the stars of the hotel (1-5): ");
                            starsStr = this.readUserInput();
                            stars = Double.parseDouble(starsStr);
                        } catch (NumberFormatException e) {
                            System.out.print("Stars must be a number. ");
                            continue;
                        }
                        if (stars < 1 || stars > 5) {
                            System.out.print("Invalid value. ");
                        }
                    } while (stars < 1 || stars > 5);

                    selectedFilters.add("5");
                    filter.put("stars", stars);


                    break;
                default:
                    System.out.println("Invalid option. Please choose from 1 to 5.");
                    break;
            }
        }


        filter.put("type", "1");

        filter.put("filters", selectedFilters);


        SocketUtils.safeSend(this.outputStream, filter.toJSONString());

        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));

        JSONObject body = response.getBody();
        if (body != null) {
            JSONArray hotels = (JSONArray) body.get("results");

            System.out.println();
            System.out.println("Results: ");
            System.out.println();
            // print all hotels
            for (Object hotel : hotels) {
                JSONObject hotelObj = (JSONObject) hotel;
                hotelObj.remove("manager_id");

                System.out.println("Hotel: " + hotelObj.get("hotelName"));
                System.out.println("Area: " + hotelObj.get("area"));
                System.out.println("Rating: " + hotelObj.get("stars"));
                System.out.println("Price: $" + hotelObj.get("price"));
                System.out.println("Number of people: " + hotelObj.get("numPeople"));
                System.out.println(hotelObj.get("hotelImage"));
                System.out.println("Available Dates: " + hotelObj.get("availableDates"));
                System.out.println("Number of Reviews: " + hotelObj.get("numReviews"));
                System.out.println("-------------------------------------------------");
            }
        }
        else {
            System.out.println(response.getMessage());
            System.out.println();
        }
    }

    /* Manager actions */

    /**
     * Gathers input from the user to add a new hotel and sends the data to the server.
     * Handles the server response to inform the user of the success or failure of the hotel addition.
     * @throws IOException if there is a problem with network communication
     */
    private void addHotel() throws IOException {

        System.out.print("Give the name of the hotel: ");
        String hotelName = this.readUserInput();

        System.out.print("Give the number of the people: ");
        String numPeopleStr = this.readUserInput();
        int numPeople = Integer.parseInt(numPeopleStr);

        System.out.print("Give the area of the hotel: ");
        String area = this.readUserInput();

        System.out.print("Give the price of the hotel: ");
        String priceStr = this.readUserInput();
        double price = Double.parseDouble(priceStr);

        System.out.print("Give the path of the image: ");
        String hotelImage = this.readUserInput();

        /* Send request to server */
        JSONObject newHotel = new JSONObject();

        newHotel.put("type", "1");
        newHotel.put("hotelName", hotelName);
        newHotel.put("numPeople", numPeople);
        newHotel.put("price", price);
        newHotel.put("area", area);
        newHotel.put("stars", 0);
        newHotel.put("numReviews", 0);
        newHotel.put("hotelImage", hotelImage);

        SocketUtils.safeSend(outputStream, newHotel.toJSONString());

        /* Handle response */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
        System.out.println();
        System.out.println(response.getMessage());
        System.out.println();

    }

    /* Adds available dates for the hotel */
    private void addAvailableDates() throws IOException {
        System.out.print("Give the name of the hotel you want to add available dates: ");
        String hotelName = this.readUserInput();
        System.out.print("Give the available dates with this format (YYYY-MM-DD - YYYY-MM-DD,  YYYY-MM-DD - YYYY-MM-DD, ...): ");
        String availableDates = DateProcessing.checkDates(true);
        JSONObject addDates = new JSONObject();

        /* Send request to server */
        addDates.put("type", "2");
        addDates.put("hotelName", hotelName);
        addDates.put("availableDates", availableDates);

        SocketUtils.safeSend(this.outputStream, addDates.toJSONString());

        /* Handle response */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
        System.out.println();
        System.out.println(response.getMessage());
        System.out.println();
    }

    /*
    * Shows the reservations of the manager */
    private void showReservations() throws IOException {
        /* Prepare a JSON request object with the type identifier for fetching reservations */
        JSONObject request = new JSONObject();
        request.put("type", "3");
        SocketUtils.safeSend(this.outputStream, request.toJSONString());

        /* Receive and parse the JSON response from the server */
        Response response = Response.fromJSONString(SocketUtils.safeReceive(this.inputStream));
        System.out.println();
        if (response.getStatus().equals(Response.Status.SUCCESS)) {
            JSONObject body = response.getBody();
            JSONArray results = (JSONArray) body.get("results");
            /* Iterate through each result representing reservations per hotel */
            for (Object result : results) {

                JSONObject resultObj = (JSONObject) result;
                System.out.println("Hotel Name: " + resultObj.get("hotelName"));
                System.out.println();
                JSONObject reservations = (JSONObject) resultObj.get("reservations");

                /* Display each reservation's client ID and period */
                for (Object key : reservations.keySet()) {
                    System.out.println("Client ID: " + key);
                    System.out.println("Reservation Period: " + reservations.get(key));
                }
            }
        } else {
            System.out.println("Message: " + response.getMessage()); // Display the error message
        }
        System.out.println();
    }


    /* Reads the user input */
    private String readUserInput() {
        String input = this.scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            closeConnection();
        }
        return input;
    }

    /* Handles the login and register process */
    private void authenticate() throws Exception {
        System.out.print("Do you have an account? (Yes/No) ");
        String answer = this.readUserInput();

        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
            loginMenu();
        }
        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
            registerMenu();
        }

    }

    /* Starts the application */
    private void start_app() {
        System.out.println("Welcome to the Booking App. You can exit by typing 'exit' at any time.");
        System.out.println();
        try {
            System.out.println("My address is " + clientSocket.getLocalSocketAddress());
            System.out.println("Connected to: " + clientSocket.getRemoteSocketAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            do {
                authenticate(); // Handles the login and register process
            } while (true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}