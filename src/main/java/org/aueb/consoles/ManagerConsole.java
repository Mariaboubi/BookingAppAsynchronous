//package org.aueb.consoles;
//
//import org.aueb.entities.Manager;
//import org.json.simple.JSONObject;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Scanner;
//
//import static java.lang.System.exit;
//
//public class ManagerConsole extends Thread{
//    private final Scanner scanner = new Scanner(System.in);
//    public Socket socket =null;
//    public ObjectOutputStream out= null;
//    public ObjectInputStream in= null;
//
//
//    private Manager manager;
//
//    public ManagerConsole() {
//    }
//
//    public void setManager(Manager manager) {
//        this.manager = manager;
//    }
//    public void run(){
//
//        try{
//
//            socket = new Socket("localhost", 4321);
//            System.out.println("dfgdgdfgdg");
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());
//
//            System.out.println("Connected to server.");
//
//            this.console();
//
//        }catch (UnknownHostException unknownHost) {
//            System.err.println("You are trying to connect to an unknown host!");
//        } catch (Exception ioException) {
//            ioException.printStackTrace();
//        } finally {
//            try {
//                if (in != null) in.close();
//                if (out != null) out.close();
//                if (socket != null) socket.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }
//    public void console() throws Exception {
//        while (true) {
//            System.out.println("1. Add a room: ");
//            System.out.println("2. Add available dates for your hotel: ");
//            System.out.println("3. Show your reservations: ");
//            System.out.println("4. Show your reservations by area" +
//                    " for a specific period of time: ");
//            System.out.println("5. Exit");
//            String optionInput= scanner.nextLine();
//            int option = parseOption(optionInput);
//            if (option == 1) {
//                this.addHotel();
//            } else if (option == 2) {
//                this.addAvailableDates();
//            } else if (option == 3) {
//                this.showReservations();
//            } else if (option == 5) {
//                closeConnection();
//                exit(0);
//            } else {
//                System.out.println("Invalid option.");
//            }
//        }
//    }
//
//
//
//    public void addHotel() throws IOException {
//        // Inputs
//        System.out.print("Give the name of the hotel: ");
//        String hotelName = scanner.nextLine();
//
//        System.out.print("Give the number of the people: ");
//        String noOfPersons = scanner.nextLine();
//
//        System.out.print("Give the area of the hotel: ");
//        String area = scanner.nextLine();
//
//
//        double stars;
//        while (true) {
//            System.out.print("Give the rate of the hotel (1-5): ");
//            stars = Double.parseDouble(scanner.nextLine());
//            if (stars >= 1. && stars <= 5.) {
//                break;
//            }
//            else {
//                System.out.println("Invalid option.");
//            }
//        }
//
//        System.out.print("Give the number of the reviews: ");
//        int numReviews = Integer.parseInt(scanner.nextLine());
//
//        System.out.print("Give the path of the image: ");
//        String hotelImage = scanner.nextLine();
//
//        System.out.print("Give the price of the hotel: ");
//        Double price = Double.parseDouble(scanner.nextLine());
//
//        //Hotel hotel = new Hotel(hotelName, noOfPersons, area, stars, numReviews, hotelImage, price, " " ,manager);
//
//        // Add the new hotel into the list hotelsManaged
//        //manager.getHotelsManaged().add(hotel);
//
//        // Create a new JSONObject for the hotel
//        JSONObject newHotel = new JSONObject();
//
//        //newHotel.put("id", hotel.getId());
//        newHotel.put("option", 1);
//        newHotel.put("hotelName", hotelName);
//        newHotel.put("noOfPersons", noOfPersons);
//        newHotel.put("price" ,price);
//        newHotel.put ("area", area);
//        newHotel.put("stars", stars);
//        newHotel.put("numReviews",numReviews);
//        newHotel.put("hotelImage",hotelImage);
//        newHotel.put("availableDates", " ");
//        newHotel.put("manager_id", manager.getId());
//
//        sendDataToMaster(newHotel);
////
////        try {
////            JSONParser parser = new JSONParser();
////            Object obj = parser.parse(new FileReader("bin/hotel.json"));
////            JSONObject jsonObject = (JSONObject) obj;
////
////            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
////
////            hotelsArray.add(newHotel);
////
////            JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//    public void  addAvailableDates() throws Exception {
//        System.out.print("Give the name of the hotel you want to add available dates: ");
//        String hotelName = scanner.nextLine().trim();
//        System.out.print("Give the available dates with this format: YYYY/MM/DD - YYYY/MM/DD, YYYY/MM/DD - YYYY/MM/DD,... ");
//        String availableDates = scanner.nextLine();
//        JSONObject addDates = new JSONObject();
//
//        //newHotel.put("id", hotel.getId());
//        addDates.put("option", 2);
//        addDates.put("hotelName", hotelName);
//        addDates.put("availableDates", availableDates);
//        addDates.put("manager" ,manager);
//
//        this.sendDataToMaster(addDates);
//
////        System.out.print("Give the name of the hotel you want to add available dates: ");
////        String hotelName = scanner.nextLine().trim();
////        boolean hotelFound = false;
////
////        String availableDates = null;
////
////        for (Hotel hotel : manager.getHotelsManaged()) {
////
////            if ((hotel.getHotelName()).equals(hotelName)) {
////                //Input
////                System.out.print("Give the available dates with this format: YYYY/MM/DD - YYYY/MM/DD, YYYY/MM/DD - YYYY/MM/DD,... ");
////                availableDates = scanner.nextLine();
////
//////                hotel.setAvailableDates(availableDates);
//////                System.out.println("Updated dates: " + hotel.getAvailableDates());
//////                hotelFound = true;
//////                break;
////            }
////        }
////
////        if (!hotelFound) {
////            System.out.println("Hotel not found.");
////            return false;
////        }
////
////        try {
////            JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json"); // This method needs to be implemented
////            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
////
////            // Iterate through the hotels array
////            for (Object hotelObj : hotelsArray) {
////                JSONObject hotel = (JSONObject) hotelObj;
////                String currentHotelName = (String) hotel.get("hotelName");
////                if (currentHotelName.equals(hotelName)) {
////                    // hotel found, return the hotel's JSONObject or handle as needed
////                    hotel.put("availableDates", availableDates);
////                    JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json"); // This method needs to be implemented
////                    return true; // Successfully updated
////                }
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return false; // Return null if no hotel is found
//
//    }
//
//    public void showReservations() throws IOException {
//        JSONObject showReservations = new JSONObject();
//        showReservations.put("option", 3);
//        showReservations.put("manager" ,manager);
//
//        this.sendDataToMaster(showReservations);
//
////        System.out.println("Reservations for your managed hotels: ");
////        for (Hotel hotel : manager.getHotelsManaged()) {
////            //System.out.println("Hotel: " + hotel.getHotelName());
////            Map<Client, String> reservations = hotel.getReservations();
////            if (!reservations.isEmpty()) {
////                System.out.println("Hotel: " + hotel.getHotelName());
////                for (Map.Entry<Client, String> entry : reservations.entrySet())
////                    System.out.println("Client: " + entry.getKey().getName() + " Reservation dates: " + entry.getValue());
////            }
////        }
//    }
//
//    private void closeConnection() throws IOException {
//        in.close();
//        out.close();
//        socket.close();
//        System.out.println("Connection to Master closed.");
//    }
//
//    private void sendDataToMaster(JSONObject data) throws IOException {
//        out.writeUTF(data.toJSONString());
//        out.flush();
//        System.out.println("Data sent to Master: " + data);
//
//        String response = in.readLine();
//        System.out.println("Response from Master: " + response);
//    }
//
//    private int parseOption(String input) {
//        try {
//            return Integer.parseInt(input);
//        } catch (NumberFormatException e) {
//            return -1; // Invalid number
//        }
//    }
//}
