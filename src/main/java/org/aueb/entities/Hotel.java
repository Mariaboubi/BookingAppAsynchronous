package org.aueb.entities;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Hotel {
    private static final AtomicInteger id_counter = new AtomicInteger(0);
    protected final int id;
    private String hotelName; // name of the hotel
    private int numPeople; // number of people
    private String area; // the area of the hotel
    private double stars; // the stars of the hotel
    private int numReviews; // number of reviews
    private String hotelImage; // the path of the room image
    private String availableDates; // available dates

    private final Map<Client, String> reservations;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    private Double price;

    private int manager_id; // the manager of the hotel

    public Hotel(String roomName, int numPeople, String area, double stars, int numReviews, String hotelImage, Double price, String availableDates, int manager_id) {
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

    // Setters
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public void setRoomImage(String roomImage) {
        this.hotelImage = roomImage;
    }

    public void setAvailableDates(String availableDates) {
        this.availableDates = availableDates;
    }

    public void setManager(int manager_id) {
        this.manager_id = manager_id;
    }

    // Getters
    public int getNumPeople() {
        return numPeople;
    }

    public String getArea() {
        return area;
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

    public String getAvailableDates() {
        return availableDates;
    }
//    //public int getId() {
//        return id;
//    }

    public Map<Client, String> getReservations() {
        return reservations;
    }

    @Override
    public String toString() {
        return
                " hotelName='" + hotelName + '\'' +
                        ", number of people ='" + numPeople + '\'' +
                        ", area='" + area + '\'' +
                        ", stars=" + stars +
                        ", number Of reviews=" + numReviews +
                        ", availableDates='" + availableDates + '\'' +
                        ", price=" + price;
    }

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
        return jsonObject;
    }

    public void addReservations(Client client, String dates) {
        this.reservations.put(client, dates);
    }
}
