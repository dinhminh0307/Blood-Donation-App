package com.example.blooddonate.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BloodDonationSite implements Parcelable {
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
    public BloodDonationSite(String name, String location, String date, String bloodTypes, double units, String owner, List<String> registers) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.bloodTypes = bloodTypes;
        this.units = units;
        this.owner = owner;
        this.registers = registers;
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
        return this.registers;
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
        this.registers = site.getRegisters();
    }

    // Parcelable implementation
    protected BloodDonationSite(Parcel in) {
        name = in.readString();
        location = in.readString();
        date = in.readString();
        owner = in.readString();
        bloodTypes = in.readString();
        units = in.readDouble();
        registers = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(owner);
        dest.writeString(bloodTypes);
        dest.writeDouble(units);
        dest.writeStringList(registers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BloodDonationSite> CREATOR = new Parcelable.Creator<BloodDonationSite>() {
        @Override
        public BloodDonationSite createFromParcel(Parcel in) {
            return new BloodDonationSite(in);
        }

        @Override
        public BloodDonationSite[] newArray(int size) {
            return new BloodDonationSite[size];
        }
    };
}
