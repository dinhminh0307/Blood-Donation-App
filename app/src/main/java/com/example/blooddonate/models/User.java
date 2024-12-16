package com.example.blooddonate.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String name;
    private String phoneNumber;
    private String email;

    private List<String> listRegistered = new ArrayList<>();
    private List<String> listOwnSites = new ArrayList<>();

    // Default constructor (required for Firestore)
    public User() {}

    // Constructor
    public User(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Constructor with listRegistered and listOwnSites
    public User(String name, String phoneNumber, String email, List<String> listRegistered, List<String> listOwnSites) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.listRegistered = listRegistered;
        this.listOwnSites = listOwnSites;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getListRegistered() {
        return listRegistered;
    }

    public void setListRegistered(List<String> listRegistered) {
        this.listRegistered = listRegistered;
    }

    public List<String> getListOwnSites() {
        return listOwnSites;
    }

    public void setListOwnSites(List<String> listOwnSites) {
        this.listOwnSites = listOwnSites;
    }

    public void setUser(User currentUser) {
        this.email = currentUser.getEmail();
        this.phoneNumber = currentUser.getPhoneNumber();
        this.name = currentUser.getName();
        this.listRegistered = currentUser.getListRegistered();
        this.listOwnSites = currentUser.getListOwnSites();
    }

    // Parcelable implementation
    protected User(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        listRegistered = in.createStringArrayList();
        listOwnSites = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeStringList(listRegistered);
        dest.writeStringList(listOwnSites);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
