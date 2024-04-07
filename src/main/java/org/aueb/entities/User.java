package org.aueb.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private String name;
    private String lastName;
    private String userName;
    private String password;
    protected final int id;
    private static final AtomicInteger id_counter = new AtomicInteger(0);



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
    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() { return password;}
    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
