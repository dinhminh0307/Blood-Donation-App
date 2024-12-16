package com.example.blooddonate.services;

import android.util.Log;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public void updateField(String siteId, String fieldName, Object value) {
        firestore.collection("bloodDonationSites")
                .document(siteId)
                .update(fieldName, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Field " + fieldName + " updated successfully for site: " + siteId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating field " + fieldName + " for site: " + siteId, e);
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

    public BloodDonationSite findDonationSiteById(String siteId) {
        BloodDonationSite site = new BloodDonationSite();
        firestore.collection("bloodDonationSites")
                .document(siteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        site.setBloodDonationSite(Objects.requireNonNull(documentSnapshot.toObject(BloodDonationSite.class)));
                    } else {
                        Log.d("Firestore", "No donation site found with ID: " + siteId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding donation site", e);
                });
        return  site;
    }

    public void findAllSites(DataFetchCallback<BloodDonationSite> callback) {
        firestore.collection("bloodDonationSites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BloodDonationSite> sites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        BloodDonationSite site = doc.toObject(BloodDonationSite.class);
                        if (site != null) {
                            sites.add(site);
                        }
                    }
                    // Notify success with the fetched data
                    callback.onSuccess(sites);
                })
                .addOnFailureListener(e -> {
                    // Notify failure
                    callback.onFailure(e);
                });
    }

    public void getSiteIdByModel(BloodDonationSite siteModel, DataFetchCallback<String> callback) {
        firestore.collection("bloodDonationSites")
                .whereEqualTo("name", siteModel.getName()) // Match site name
                .whereEqualTo("location", siteModel.getLocation()) // Match site location
                .whereEqualTo("date", siteModel.getDate()) // Match site date
                .whereEqualTo("bloodTypes", siteModel.getBloodTypes()) // Match blood types
                .whereEqualTo("units", siteModel.getUnits()) // Match units
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Get the first matching document
                        String siteId = querySnapshot.getDocuments().get(0).getId();
                        List<String> returnedData = new ArrayList<>();
                        returnedData.add(siteId);
                        callback.onSuccess(returnedData);
                    } else {
                        callback.onFailure(new Exception("No matching site found."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding site UID", e);
                    callback.onFailure(e);
                });
    }


}
