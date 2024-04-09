package org.aueb;

import org.aueb.consoles.Console;
import org.aueb.entities.Client;
import org.aueb.entities.Hotel;
import org.aueb.entities.Manager;
import org.aueb.entities.User;

import java.util.ArrayList;

public class Main {
    //private static final List<Manager> managers = new ArrayList<>();
    private static final String JSON_FILE_PATH = "bin/hotel.json";

    private static final ArrayList<Hotel> hotels = new ArrayList<>(); // A list of the hotels

    private static final ArrayList<User> users = new ArrayList<>();

    private static Client client;


    public static void main(String[] args) throws Exception {
//        Console console = new Console(users, hotels);
//
//        initializeManagers();
//        initializeClients();
//        // matchManagerHotel();
//        console.start_app();
        //System.out.println("Main hotels: "+ hotels.size());
//        client.clientConsole(hotels);
        //chooseΙdentity();

        //manager1.addRoom();
//        System.out.println("Manager " + managers.get(0).getName());
//        (managers.get(2)).addAvailableDates();
    }





//    private static void matchManagerHotel() {
//
//        try {
//            JSONObject jsonObject = JSONReaderWriter.readJsonFile(JSON_FILE_PATH);
//            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
//            Map<Integer, Manager> managerMap = createManagerMap(users);
//
//            // Iterate through the hotels array
//            for (Object hotelObj : hotelsArray) {
//                JSONObject hotel = (JSONObject) hotelObj;
//                int id = ((Long) hotel.get("id")).intValue();
//                int manager_id = ((Long) hotel.get("manager_id")).intValue();
//                Manager manager = managerMap.get(manager_id);
//                String hotelName  = (String) hotel.get("hotelName");
//                String noOfPersons= (String) hotel.get("noOfPersons");
//                int numReviews = ((Long) hotel.get("numReviews")).intValue();
//                String area= (String) hotel.get("area");
//                double stars = ((Number) hotel.get("stars")).doubleValue();
//                String roomImage  = (String) hotel.get("hotelImage");
//                double price = ((Number) hotel.get("price")).doubleValue();
//                String availableDate  = (String) hotel.get("availableDates");
//
//                Hotel newHotel= new Hotel( hotelName,noOfPersons,area,stars,numReviews,roomImage,price,availableDate,manager);
//                if(newHotel.getHotelName().equals("MyPortoVilla")){
//                    newHotel.addReservations(client,"2024/07/03 - 2024/07/09");
//                }
//                //System.out.println(newHotel.getHotelName());
//                hotels.add(newHotel);
//            }
//
//            for (Hotel hotel : hotels) {
//                Manager manager = hotel.getManager();
//                if (manager != null) {
//                    manager.addHotelsManaged(hotel);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    private static Map<Integer, Manager> createManagerMap(List<User> users) {
//        Map<Integer, Manager> map = new HashMap<>();
//        for (User user : users) {
//            if (user instanceof Manager) {
//                map.put(user.getId(), (Manager) user); // Correctly use manager.getId()
//            }
//        }
//        return map;
//    }
}