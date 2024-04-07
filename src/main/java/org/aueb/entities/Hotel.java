package org.aueb.entities;

import java.util.HashMap;
import java.util.Map;

public class Hotel {
    //private static final AtomicInteger id_counter = new AtomicInteger(0);
    //protected final int id;
    private String hotelName; // name of the hotel
    private String noOfPeople; // number of people
    private String area; // the area of the hotel
    private double stars; // the stars of the hotel
    private int noOfReviews; // number of reviews
    private String hotelImage; // the path of the room image
    private String availableDates; // available dates

    private final Map<Client,String> reservations ;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    private Double price;

    private int manager_id; // the manager of the hotel

    //public Manager manager;


//    public Hotel(String roomName, String noOfPeople, String area, double stars, int noOfReviews, String hotelImage, Double price,String availableDates,Manager manager) {
//        this.hotelName=roomName;
//        this.noOfPeople= noOfPeople;
//        this.area=area;
//        this.stars=stars;
//        this.noOfReviews=noOfReviews;
//        this.hotelImage=hotelImage;
//        this.price = price;
//        this.manager=manager;
//        //this.id = id_counter.incrementAndGet();
//        //System.out.println("Created hotel with id: " + this.id);
//    }
    public Hotel(String roomName, String noOfPeople, String area, double stars, int noOfReviews, String hotelImage, Double price,String availableDates,int manager_id) {
        this.hotelName=roomName;
        this.noOfPeople= noOfPeople;
        this.area=area;
        this.stars=stars;
        this.noOfReviews=noOfReviews;
        this.hotelImage=hotelImage;
        this.price = price;
        this.manager_id=manager_id;
        //this.id = id;
        this.availableDates= availableDates;
        reservations= new HashMap<>();

    }

    // Setters
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setNoOfPeople(String noOfPersons) {
        this.noOfPeople = noOfPersons;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
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
    public String getNoOfPeople() {
        return noOfPeople;
    }

    public String getArea() {
        return area;
    }

    public double getStars() {
        return stars;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public String getRoomImage() {
        return hotelImage;
    }

    public int getManager() {
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
                        ", number of people ='" + noOfPeople + '\'' +
                        ", area='" + area + '\'' +
                        ", stars=" + stars +
                        ", number Of reviews=" + noOfReviews +
                        ", availableDates='" + availableDates + '\'' +
                        ", price=" + price ;
    }

    public void addReservations(Client client,String dates){
        this.reservations.put(client,dates);
    }
}
