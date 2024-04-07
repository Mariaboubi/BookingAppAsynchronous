package org.aueb.entities;

import java.util.ArrayList;
import java.util.Scanner;

public class Client extends User {
    private ArrayList<Hotel> hotels = new ArrayList<Hotel>();
    private final Scanner scanner = new Scanner(System.in);
    public Client(String name, String lastName, String userName, String password) {
        super(name, lastName, userName, password);
    }
}





