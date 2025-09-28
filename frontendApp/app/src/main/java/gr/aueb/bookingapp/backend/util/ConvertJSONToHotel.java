package gr.aueb.bookingapp.backend.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.aueb.bookingapp.backend.entities.Hotel;

public class ConvertJSONToHotel {

    /**
     * Initializes a Hotel object from a JSON object.
     *
     * @param hotel The JSON object containing hotel data.
     * @return A Hotel object initialized with the data from the JSON object.
     */
    public static Hotel initializeHotel(JSONObject hotel) {
        // Extract available dates from the JSON object
        JSONArray availableDatesObj = (JSONArray) hotel.get("availableDates");
        List<String> dates = new ArrayList<>();
        if (availableDatesObj != null) {
            availableDatesObj.forEach(date -> dates.add((String) date));
        }

        // Extract and parse manager ID from the JSON object
        Object managerIdObj = hotel.getOrDefault("manager_id", "0");
        int manager_id = 0;
        if (managerIdObj instanceof Number) {
            manager_id = ((Number) managerIdObj).intValue();
        } else if (managerIdObj instanceof String) {
            try {
                manager_id = Integer.parseInt((String) managerIdObj);
            } catch (NumberFormatException e) {
                manager_id = 0; // Default to 0 or handle appropriately
            }
        }

        // Extract other hotel attributes from the JSON object
        int numReviews = hotel.get("numReviews") == null ? 0 : ((Long) hotel.get("numReviews")).intValue();
        int numPeople = hotel.get("numPeople") == null ? 0 : ((Long) hotel.get("numPeople")).intValue();
        double stars = hotel.get("stars") == null ? 0.0 : ((Number) hotel.get("stars")).doubleValue();
        double price = hotel.get("price") == null ? 0.0 : ((Number) hotel.get("price")).doubleValue();
        String hotelName = (String) hotel.getOrDefault("hotelName", "Unknown");
        String area = (String) hotel.getOrDefault("area", "Unknown");
        String roomImage = (String) hotel.getOrDefault("hotelImage", "default.png");

        // Create and return a new Hotel object with the extracted data
        Hotel newHotel = new Hotel(hotelName, numPeople, area, stars, numReviews, roomImage, price, dates, manager_id);

        return newHotel;
    }
}
