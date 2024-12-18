package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.callbacks.LoginCallback;
import com.example.blooddonate.models.User;
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

    public void signOut() {
        userService.signOut();
    }

    public void updateUserField(String siteId, String fieldName, Object val) {
        userService.updateUserField(siteId, fieldName, val);
    }

    public void login(String email, String password, LoginCallback cb) {
        userService.login(email, password, cb);
    }

    public void getUserByUID(String id, GetUserCallback cb) {
        userService.getUserByUID(id, cb);
    }

    public void getAllUsers(DataFetchCallback<User> cb) {
        userService.getAllUsers(cb);
    }
}
