package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.services.UserService;

public class UserController {
    UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public void getCurrentUser(GetUserCallback callback) {
        this.userService.getCurrentUser(callback);
    }

    public String getUserId() {
        return userService.getUserUID();
    }
}
