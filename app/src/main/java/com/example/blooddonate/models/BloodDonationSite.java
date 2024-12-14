package com.example.blooddonate.models;

import java.util.Map;

public class BloodDonationSite {
    private String name;
    private String location;
    private String date;
    private String bloodTypes; // Blood types and counts
    private double units; // Total blood units collected
    private int registeredCount; // Number of registered donors or attendees

    // Default constructor (required for Firestore)
    public BloodDonationSite() {
    }

    // Constructor with parameters
    public BloodDonationSite(String name, String location, String date, String bloodTypes, double units, int registeredCount) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.bloodTypes = bloodTypes;
        this.units = units;
        this.registeredCount = registeredCount;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBloodTypes() {
        return bloodTypes;
    }

    public void setBloodTypes(String bloodTypes) {
        this.bloodTypes = bloodTypes;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public void setBloodDonationSite(BloodDonationSite site) {
        this.bloodTypes = site.getBloodTypes();
        this.date = site.getDate();
        this.registeredCount = site.getRegisteredCount();
        this.units = site.getUnits();
        this.location = site.getLocation();
        this.name = site.getName();
    }

    // Method to calculate the total number of blood units based on blood types

}
