package org.aueb.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a manager in the hotel management system.
 * A manager is responsible for managing one or more hotels.
 */
public class Manager extends User {
    private final List<Hotel> hotelsManaged; // List to store the hotels managed by this manager

    public Manager(String name, String lastName, String userName, String password) {
        super(name, lastName, userName, password);
        this.hotelsManaged = new ArrayList<>(); // Initialize the list of hotels managed by this manager
    }

    /**
     * Adds a hotel to the list of hotels managed by this manager.
     * @param hotel The hotel to be managed by this manager.
     */
    public void addHotelsManaged(Hotel hotel) {
        this.hotelsManaged.add(hotel);
    }
}


