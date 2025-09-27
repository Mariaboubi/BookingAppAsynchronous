package org.aueb.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final String name;
    private final String lastName;
    private final String userName;
    private final String password;
    protected final int id;
    private static final AtomicInteger id_counter = new AtomicInteger(0);

    /**
     * Represents a generic user in the system. This class is intended to be a base class for more specific types of users,
     * such as clients or managers.
     */
    public User(String name, String lastName, String userName, String password) {
        this.name = name;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.id = id_counter.incrementAndGet();
    }
    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() { return password;}
}
