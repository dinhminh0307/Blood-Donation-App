package com.example.blooddonate.models;

public class Notification {
    private String ownerUID;
    private String message;

    private  boolean isRead = false;

    // Constructor
    public Notification(String ownerUID, String message) {
        this.ownerUID = ownerUID;
        this.message = message;
    }

    // Default Constructor
    public Notification() {
    }

    // Getter for ownerUID
    public String getOwnerUID() {
        return ownerUID;
    }

    // Setter for ownerUID
    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}
