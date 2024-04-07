package org.aueb.consoles;

import org.aueb.entities.User;
import org.aueb.util.SocketUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Console {
    private final Scanner scanner = new Scanner(System.in);
    private final Socket clientSocket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final JSONParser parser = new JSONParser();

    public Console(String host, int port) throws IOException {
        this.clientSocket = SocketUtils.createSocket(host, port);
        this.inputStream = SocketUtils.createDataInputStream(this.clientSocket);
        this.outputStream = SocketUtils.createDataOutputStream(this.clientSocket);
    }
    public void loginMenu() throws Exception {
            System.out.print("Give me the username: ");
            String username = scanner.nextLine();

            System.out.print("Give me the password: ");
            String password = scanner.nextLine();

            String response = null;
            try {

                JSONObject request = new JSONObject();
                request.put("type", "login");
                request.put("username", username);
                request.put("password", password);

                // Sends the login request to the server
                SocketUtils.safeSend(this.outputStream, request.toJSONString());

                // Receives the answer from server
                response = SocketUtils.safeReceive(this.inputStream);

                System.out.println(" Response : " + response);

                JSONObject jsonObject_response = (JSONObject) parser.parse(response);
                String string_response = (String) jsonObject_response.get("result");


                if (string_response.equals("not authenticated")) {
                    System.out.println("Username or password are incorrect.");

                    while (true) {
                        System.out.println("1. Try again");
                        System.out.println("2. Create an account");
                        System.out.print("> ");

                        String option = scanner.nextLine();
                        if (option.equals("1")) {
                            break;
                        }
                        if (option.equals("2")) {
                            registerMenu();
                            break;
                        } else {
                            System.out.println("Invalid option");
                        }
                    }


                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            directToConsole(response);

    }

    private void registerMenu() throws Exception {
        User user;

        System.out.print("Give me your name: ");
        String name = scanner.nextLine();

        System.out.print("Give me your last name: ");
        String lastname = scanner.nextLine();

        System.out.print("Create a username: ");
        String username = scanner.nextLine();

        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        String identity;
        while (true) {
            System.out.print("Do you want to be a Manager or Client? ");
            identity = scanner.nextLine();

            if (identity.equalsIgnoreCase("Manager") || identity.equalsIgnoreCase("Client")) {
                break;
            }else{
                System.out.println("Invalid option");
            }
        }

        JSONObject register = new JSONObject();
        register.put("type", "register");
        register.put("name", name);
        register.put("lastname", lastname);
        register.put("username", username);
        register.put("password", password);
        register.put("identity", identity);

        // Sends to Master the register data
        SocketUtils.safeSend(this.outputStream, register.toJSONString());

        // Receives the answer from server
        String response = SocketUtils.safeReceive(this.inputStream);
        directToConsole(response);
    }

    public void directToConsole(String response) throws Exception {
        JSONObject jsonObject_response = (JSONObject) parser.parse(response);
        String user_type = (String) jsonObject_response.get("user_type");
        String string_result = (String) jsonObject_response.get("result");
        System.out.println(string_result +" as a " + user_type);

        if (user_type.equals("Manager")) {
            System.out.println("Manager Console");
            this.ManagerConsole();

        } else {
            System.out.println("Client Console");
            this.clientConsole();
        }
    }

    public void clientConsole(){
        while (true) {
            System.out.println("1.Choose a filter: ");
            System.out.println("2.Book a hotel: ");
            System.out.println("3.Rate a hotel (1-5): ");
            System.out.println("4. Exit");
            int option = Integer.parseInt(scanner.nextLine());
            if (option == 1) {
                this.filterHotel();
            } else if (option == 2) {
                this.makeReservation();
            } else if (option == 3) {
                this.rateHotel();
            }else if(option==4) {
                exit(0);
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    public void makeReservation() {
        System.out.println("Do you want to filter the hotels? Y/N ");
        String filter = scanner.nextLine();
        System.out.println("Give the name of the hotel which you want reserve ");
        String hotelName = scanner.nextLine();
        System.out.println("Give the dates which you want reserve with this format: YYYY/MM/DD - YYYY/MM/DD ");
        String dates = scanner.nextLine();

        JSONObject reservation = new JSONObject();
        reservation.put("option", 2);
        reservation.put("filter", filter);
        reservation.put("hotelName", hotelName);
        reservation.put("Available dates", dates);

        SocketUtils.safeSend(this.outputStream, reservation.toJSONString());
    }

    public void rateHotel(){

        System.out.println("Give the name of the hotel you want to rate");
        String hotelName = scanner.nextLine().trim();
        System.out.println("Give your rating(1-5): ");
        double newRating = Double.parseDouble(scanner.nextLine());
        JSONObject rate = new JSONObject();

        rate.put("option", 3);
        rate.put("hotelName", hotelName);
        rate.put("newRating", newRating);

        SocketUtils.safeSend(this.outputStream, rate.toJSONString());
    }

    private void filterHotel() {

        System.out.println("Choose one or multiple filters seperated by comma");
        System.out.println("1.Area");
        System.out.println("2.Available dates");
        System.out.println("3.Number of persons");
        System.out.println("4.Price");
        System.out.println("5.Stars");

        String option = scanner.nextLine();
        String[] options = option.split(",");

        JSONObject filter = new JSONObject();
        filter.put("option", 1);
        for(int i=0;i<options.length ;i++){
            filter.put("filter_"+ i, options[i]);
        }
        SocketUtils.safeSend(this.outputStream, filter.toJSONString());

    }

    public void ManagerConsole() throws Exception {
        while (true) {
            System.out.println("1. Add a room: ");
            System.out.println("2. Add available dates for your hotel: ");
            System.out.println("3. Show your reservations: ");
            System.out.println("4. Exit");

            String optionInput= scanner.nextLine();
            int option = Integer.parseInt(optionInput);

            if (option == 1) {
                this.addHotel();
            } else if (option == 2) {
                this.addAvailableDates();
            } else if (option == 3) {
                this.showReservations();
            } else if (option == 4) {
                exit(0);
            } else {
                System.out.println("Invalid option.");
            }

        }
    }

    public void showReservations() throws IOException {
        JSONObject showReservations = new JSONObject();
        showReservations.put("option", 3);
        SocketUtils.safeSend(this.outputStream, showReservations.toJSONString());
    }

    public void  addAvailableDates() throws Exception {
        System.out.print("Give the name of the hotel you want to add available dates: ");
        String hotelName = scanner.nextLine().trim();
        System.out.print("Give the available dates with this format: YYYY/MM/DD - YYYY/MM/DD, YYYY/MM/DD - YYYY/MM/DD,... ");
        String availableDates = scanner.nextLine();
        JSONObject addDates = new JSONObject();

        addDates.put("option", 2);
        addDates.put("hotelName", hotelName);
        addDates.put("availableDates", availableDates);

        SocketUtils.safeSend(this.outputStream, addDates.toJSONString());
    }


    public void addHotel() {

        System.out.print("Give the name of the hotel: ");
        String hotelName = scanner.nextLine();

        System.out.print("Give the number of the people: ");
        String noOfPersons = scanner.nextLine();

        System.out.print("Give the area of the hotel: ");
        String area = scanner.nextLine();

        double stars;
        while (true) {
            System.out.print("Give the rate of the hotel (1-5): ");
            stars = Double.parseDouble(scanner.nextLine());
            if (stars >= 1. && stars <= 5.) {
                break;
            }
            else {
                System.out.println("Invalid option.");
            }
        }

        System.out.print("Give the number of the reviews: ");
        int noOfReviews = Integer.parseInt(scanner.nextLine());

        System.out.print("Give the path of the image: ");
        String hotelImage = scanner.nextLine();

        System.out.print("Give the price of the hotel: ");
        Double price = Double.parseDouble(scanner.nextLine());

        JSONObject newHotel = new JSONObject();

        newHotel.put("option", 1);
        newHotel.put("hotelName", hotelName);
        newHotel.put("noOfPersons", noOfPersons);
        newHotel.put("price" ,price);
        newHotel.put ("area", area);
        newHotel.put("stars", stars);
        newHotel.put("noOfReviews",noOfReviews);
        newHotel.put("hotelImage",hotelImage);

        SocketUtils.safeSend(this.outputStream, newHotel.toJSONString());
    }


    public void authenticate() throws Exception {
        System.out.print("Do you have an account? (Yes/No) ");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
            loginMenu();
        }
        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
           registerMenu();
        }

    }

    public void start_app() throws Exception {
        /* Authenticate user (login or register) */
        User user = null;
        try {
            System.out.println("My address is " + clientSocket.getLocalSocketAddress());
            System.out.println("Connected to: " + clientSocket.getRemoteSocketAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            do {
                authenticate();
            } while (true);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) throws Exception {
        Console console = new Console("localhost", 8000);

        console.start_app();
    }

}
