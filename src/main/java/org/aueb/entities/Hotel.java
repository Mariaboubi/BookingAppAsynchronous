package org.aueb.entities;

import org.json.simple.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a hotel with its associated data.
 * This class includes attributes like hotel name, number of people it can accommodate,
 * star rating, area, price,..., along with functionalities to manage reservations and available dates.
 */
public class Hotel {
    // counter for the id of the hotel
    private static final AtomicInteger id_counter = new AtomicInteger(0);
    protected final int id; // the id of the hotel
    private final String hotelName; // name of the hotel
    private final int numPeople; // number of people
    private final String area; // the area of the hotel
    private double stars; // the stars of the hotel
    private int numReviews; // number of reviews
    private final String hotelImage; // the path of the room image
    private final Double price; // the price of the hotel
    private final int manager_id; // the manager of the hotel
    private final List<String> availableDates; // available dates

    // A map to hold reservations with client IDs and their corresponding booking dates
    private Map<Integer, List<String>> reservations;

    public Hotel(String roomName, int numPeople, String area, double stars, int numReviews, String hotelImage, Double price, List<String> availableDates, int manager_id) {
        this.hotelName = roomName;
        this.numPeople = numPeople;
        this.area = area;
        this.stars = stars;
        this.numReviews = numReviews;
        this.hotelImage = hotelImage;
        this.price = price;
        this.manager_id = manager_id;
        this.id = id_counter.incrementAndGet();
        this.availableDates = availableDates;
        reservations = new HashMap<>();
    }

    /* setters and getters */
    public void setStars(double stars) {
        this.stars = stars;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public String getArea() {
        return area;
    }

    public Double getPrice() {
        return price;
    }

    public double getStars() {
        return stars;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public String getRoomImage() {
        return hotelImage;
    }

    public int getManagerId() {
        return manager_id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public List<String> getAvailableDates() {
        return availableDates;
    }

    public Map<Integer,List<String>> getReservations() {
        return reservations;
    }

    public void setReservations(Map<Integer,List<String>> reservations){
        this.reservations=reservations;
    }

    public void addAvailableDates(String availableDates) {
        this.availableDates.add(availableDates);
    }

    @Override
    public String toString() {
        return
                " hotelName='" + hotelName + '\'' +
                        ", number of people ='" + numPeople + '\'' +
                        ", area='" + area + '\'' +
                        ", stars=" + stars +
                        ", number Of reviews=" + numReviews +
                        ", availableDates=" + availableDates + '\'' +
                        ", price=" + price+ '\'' +
                "reservations=" + reservations ;

    }

    /**
     * Converts the hotel object to a JSON object.
     * @return The JSON object representing the hotel.
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hotelName", hotelName);
        jsonObject.put("numPeople", numPeople);
        jsonObject.put("area", area);
        jsonObject.put("stars", stars);
        jsonObject.put("numReviews", numReviews);
        jsonObject.put("hotelImage", hotelImage);
        jsonObject.put("price", price);
        jsonObject.put("availableDates", availableDates);
        jsonObject.put("manager_id", manager_id);

        JSONObject jsonReservations = new JSONObject();
        for (Map.Entry<Integer, List<String>> entry : reservations.entrySet()) {
            jsonReservations.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        jsonObject.put("reservations", jsonReservations);
        return jsonObject;
    }

    /**
     * Adds a reservation to the hotel's reservation list.
     * @param client The client ID
     * @param dates The dates to reserve
     */
    public void addReservations(Integer client,String dates) {
        if(reservations.containsKey(client)) {
            reservations.get(client).add(dates);
        } else {
            reservations.put(client, List.of(dates));
        }
    }

}
