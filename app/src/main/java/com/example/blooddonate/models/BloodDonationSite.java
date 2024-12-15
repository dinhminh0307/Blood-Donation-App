package com.example.blooddonate.models;

import java.util.List;
import java.util.Map;

public class BloodDonationSite {
    private String name;
    private String location;
    private String date;

    private String owner;

    private String bloodTypes; // Blood types and counts
    private double units; // Total blood units collected

    private List<String> registers;

    // Default constructor (required for Firestore)
    public BloodDonationSite() {
    }

    // Constructor with parameters
    public BloodDonationSite(String name, String location, String date, String bloodTypes, double units, String owner) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.bloodTypes = bloodTypes;
        this.units = units;
        this.owner = owner;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
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

    public void setRegisters(List<String> registers) {
        this.registers = registers;
    }

    public List<String> getRegisters() {
        return  this.registers;
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

    public void setBloodDonationSite(BloodDonationSite site) {
        this.bloodTypes = site.getBloodTypes();
        this.date = site.getDate();
        this.owner = site.getOwner();
        this.units = site.getUnits();
        this.location = site.getLocation();
        this.name = site.getName();
    }

    // Method to calculate the total number of blood units based on blood types

}
