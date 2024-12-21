package com.example.blooddonate.controllers;

import com.example.blooddonate.models.Notification;
import com.example.blooddonate.services.NotificationService;

public class NotificationController {
    NotificationService notificationService;

    public NotificationController() {
        this.notificationService = new NotificationService();
    }

    public void addNotification(Notification notification) {
        notificationService.addNotification(notification);
    }

    public void updateNotificationField(Notification notification, String fieldName, Object value) {
        notificationService.updateNotificationField(notification, fieldName, value);
    }
}
