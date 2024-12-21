package com.example.blooddonate.services;

import android.util.Log;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadService {
    private final UserController userController;
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler;
    private final List<Notification> returnNotifications = new ArrayList<>();

    public ThreadService() {
        this.userController = new UserController();
        this.notificationService = new NotificationService();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startPolling(DataFetchCallback<Notification> callback) {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                notificationService.findNotificationsByOwnerUID(userController.getUserId(), new DataFetchCallback<Notification>() {
                    @Override
                    public void onSuccess(List<Notification> data) {
                        synchronized (returnNotifications) {
                            returnNotifications.clear();
                            for (Notification tmp : data) {
                                returnNotifications.add(tmp);
                            }
                            if (!returnNotifications.isEmpty()) {
                                callback.onSuccess(new ArrayList<>(returnNotifications)); // Pass a copy to avoid shared state issues
                            } else {
                                callback.onFailure(new Exception("Data is empty"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Notification", "Unable to get notification from Firebase: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Log.e("ThreadService", "Exception in polling task", e);
            }
        }, 0, 10, TimeUnit.SECONDS); // Initial delay = 0, delay = 10 seconds
    }

    public void stopPolling() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}


