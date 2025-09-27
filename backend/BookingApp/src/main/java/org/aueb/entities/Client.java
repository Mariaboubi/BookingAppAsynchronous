package org.aueb.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a client user in the system.
 * This class extends the User class, inheriting its basic user functionality and attributes.
 * It is designed to encapsulate the attributes specific to a client user.
 */
public class Client extends User {

    private Map<Hotel, List<String>> reservations;
    /**

     Constructor for creating a new Client instance.
     Initializes a new client with the provided user details.*
     @param name     The first name of the client.
     @param lastName The last name of the client.
     @param userName The username for the client's login.
     @param password The password for the client's login.
     */
    public Client(String name, String lastName, String userName, String password) {
        super(name, lastName, userName, password);
        reservations = new HashMap<>();}

    /**
     * Retrieves the reservations made by the client.
     *
     * @return A map of hotels and their corresponding reservation dates.
     */
    public Map<Hotel, List<String>> getReservations() {
        return reservations;}

    /**
     * Adds a reservation for a specific hotel and date.
     * If the hotel already has reservations, the date is added to the existing list.
     * Otherwise, a new entry is created for the hotel with the given date.
     *
     * @param hotel The hotel for which the reservation is being made.
     * @param dates The dates of the reservation.
     */
    public void addReservations(Hotel hotel,String dates) {
        if(reservations.containsKey(hotel)) {
            reservations.get(hotel).add(dates);
        } else {
            reservations.put(hotel, Collections.singletonList(dates));
        }
    }
}