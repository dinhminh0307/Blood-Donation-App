package com.example.blooddonate.services;

import android.util.Log;

import com.example.blooddonate.models.BloodDonationSite;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonationSiteService {
    private FirebaseFirestore firestore;

    public DonationSiteService() {
        // Initialize Firestore instance
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Method to add a donation site to Firestore
    public void addDonationSite(BloodDonationSite donationSite) {
        // Add the data to the "bloodDonationSites" collection
        firestore.collection("bloodDonationSites")
                .add(donationSite)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Donation site added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding donation site", e);
                });
    }

    // Method to add a donation site with a custom ID
    public void addDonationSiteWithCustomID(String siteId, BloodDonationSite donationSite) {
        // Add the data to the "bloodDonationSites" collection with a custom ID
        firestore.collection("bloodDonationSites")
                .document(siteId)
                .set(donationSite)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Donation site added with custom ID: " + siteId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding donation site with custom ID", e);
                });
    }

    // Example method to update an existing donation site
    public void updateDonationSite(String siteId, Map<String, Object> updates) {
        firestore.collection("bloodDonationSites")
                .document(siteId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Donation site updated successfully: " + siteId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating donation site", e);
                });
    }

    // Example method to delete a donation site
    public void deleteDonationSite(String siteId) {
        firestore.collection("bloodDonationSites")
                .document(siteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Donation site deleted successfully: " + siteId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting donation site", e);
                });
    }
}
