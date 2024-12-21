package com.example.blooddonate.services;

import android.util.Log;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private FirebaseFirestore firestore;

    public NotificationService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addNotification(Notification notification) {
        // Add the data to the "bloodDonationSites" collection
        firestore.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding notification", e);
                });
    }

    public void findNotificationsByOwnerUID(String ownerUID, DataFetchCallback<Notification> callback) {
        firestore.collection("notifications") // Replace with your Firestore collection name
                .whereEqualTo("ownerUID", ownerUID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Notification notification = doc.toObject(Notification.class);
                        if (notification != null) {
                            notifications.add(notification);
                        }
                    }
                    callback.onSuccess(notifications);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void updateNotificationField(Notification notification, String fieldName, Object value) {
        firestore.collection("notifications")
                .whereEqualTo("ownerUID", notification.getOwnerUID())
                .whereEqualTo("message", notification.getMessage())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Update the matching document
                            firestore.collection("notifications")
                                    .document(document.getId())
                                    .update(fieldName, value)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Field " + fieldName + " updated successfully.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error updating field: " + e.getMessage(), e);
                                    });
                        }
                    } else {
                        Log.d("Firestore", "No matching document found for the given notification object.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding matching document: " + e.getMessage(), e);
                });
    }
}
