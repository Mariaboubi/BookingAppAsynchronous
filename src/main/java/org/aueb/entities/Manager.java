package org.aueb.entities;

import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Manager extends User {
    private final Scanner scanner = new Scanner(System.in);
    private List<Hotel> hotelsManaged; // the hotels that manages
    private final JSONParser parser = new JSONParser();

    public Manager(String name, String lastName, String userName, String password) {
        super(name, lastName, userName, password);
        this.hotelsManaged = new ArrayList<>();
    }

    public void addHotelsManaged(Hotel hotel) {
        this.hotelsManaged.add(hotel);
    }

    public List<Hotel> getHotelsManaged() {
        return hotelsManaged;
    }

    public void setHotelsManaged(List<Hotel> hotelsManaged) {
        this.hotelsManaged = hotelsManaged;
    }
}


