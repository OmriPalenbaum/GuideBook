package com.example.guidebook;

public class Boulder {
    private final String name;
    private final String address;
    private final String rating;
    private String isActive;

    public Boulder(String name, String address, String rating, String isActive) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }

    public String getIsActive() {
        return isActive;
    }
}
