package com.example.guidebook;

import android.net.Uri;

import java.io.Serializable;
import java.sql.Blob;

public class Boulder implements Serializable{
    private String name;
    private String address;
    private String rating;
    private boolean isActive;
    private byte[] imageBytes;

    public Boulder(String name, String address, String rating, int isActive, byte[] imageBytes) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.isActive = (isActive == 1); //if input for isActive is 1, set to true. else, set to false
        this.imageBytes = imageBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
