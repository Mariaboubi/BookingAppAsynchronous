package gr.aueb.bookingapp.backend.entities;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a hotel with its associated data.
 * This class includes attributes like hotel name, number of people it can accommodate,
 * star rating, area, price, along with functionalities to manage reservations and available dates.
 */
public class Hotel {
    // Counter for the id of the hotel
    private static final AtomicInteger id_counter = new AtomicInteger(0);
    protected final int id; // The id of the hotel
    private final String hotelName; // Name of the hotel
    private final int numPeople; // Number of people it can accommodate
    private final String area; // The area where the hotel is located
    private double stars; // The star rating of the hotel
    private int numReviews; // Number of reviews
    private final String hotelImage; // Path to the hotel's image
    private final Double price; // The price of the hotel
    private final int manager_id; // ID of the hotel's manager
    private final List<String> availableDates; // List of available dates

    // A map to hold reservations with client IDs and their corresponding booking dates
    private final Map<Integer, List<String>> reservations;

    /**
     * Constructs a Hotel object with the specified details.
     *
     * @param roomName The name of the hotel.
     * @param numPeople The number of people it can accommodate.
     * @param area The area where the hotel is located.
     * @param stars The star rating of the hotel.
     * @param numReviews The number of reviews.
     * @param hotelImage The path to the hotel's image.
     * @param price The price of the hotel.
     * @param availableDates The list of available dates.
     * @param manager_id The ID of the hotel's manager.
     */
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

    /* Setters and getters */

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

    public String getHotelName() {
        return hotelName;
    }

    /**
     * Converts the available dates to a formatted string.
     *
     * @return A string representing the available dates.
     */
    public String getAvailableDatesAsString() {
        StringBuilder dates = new StringBuilder();
        for (String date : availableDates) {
            date = date.replace("[", "").replace("]", "");
            dates.append(date).append("\n");
        }
        return dates.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return " hotelName='" + hotelName + '\'' +
                ", number of people ='" + numPeople + '\'' +
                ", area='" + area + '\'' +
                ", stars=" + stars +
                ", number Of reviews=" + numReviews +
                ", availableDates=" + availableDates + '\'' +
                ", price=" + price + '\'' +
                "reservations=" + reservations;
    }

}
