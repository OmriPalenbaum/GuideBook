package com.example.guidebook;

import android.net.Uri;

import java.io.Serializable;
import java.sql.Blob;
//holds boulder data
public class Boulder implements Serializable{
    private String name;
    private String address;
    private String rating;
    private boolean isActive;
    private boolean isDone;
    private byte[] imageBytes;

    //Constructor
    public Boulder(String name, String address, String rating, int isActive, int isDone, byte[] imageBytes) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.isActive = (isActive == 1); //if input for isActive is 1, set to true. else, set to false
        this.isDone = (isDone == 1); //if input for isDone is 1, set to true. else, set to false
        this.imageBytes = imageBytes;
    }

    //Getters and Setters
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

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
