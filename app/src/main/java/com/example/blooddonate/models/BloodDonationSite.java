package com.example.blooddonate.models;

import java.util.Map;

public class BloodDonationSite {
    private String name;
    private String location;
    private String date;
    private Map<String, Integer> bloodTypes; // Blood types and counts
    private double units; // Total blood units collected
    private int registeredCount; // Number of registered donors or attendees

    // Default constructor (required for Firestore)
    public BloodDonationSite() {
    }

    // Constructor with parameters
    public BloodDonationSite(String name, String location, String date, Map<String, Integer> bloodTypes, double units, int registeredCount) {
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

    public Map<String, Integer> getBloodTypes() {
        return bloodTypes;
    }

    public void setBloodTypes(Map<String, Integer> bloodTypes) {
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

    // Method to calculate the total number of blood units based on blood types
    public double calculateTotalUnits() {
        if (bloodTypes == null || bloodTypes.isEmpty()) {
            return 0.0;
        }
        double totalUnits = 0.0;
        for (int count : bloodTypes.values()) {
            totalUnits += count;
        }
        return totalUnits;
    }

    // Override toString() for easy debugging/logging
    @Override
    public String toString() {
        return "BloodDonationSite{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", bloodTypes=" + bloodTypes +
                ", units=" + units +
                ", registeredCount=" + registeredCount +
                '}';
    }

    // Method to add blood to a specific blood type
    public void addBlood(String bloodType, int quantity) {
        if (bloodTypes == null) {
            throw new IllegalStateException("Blood types map is not initialized");
        }
        bloodTypes.put(bloodType, bloodTypes.getOrDefault(bloodType, 0) + quantity);
        units = calculateTotalUnits(); // Recalculate units after adding
    }
}
