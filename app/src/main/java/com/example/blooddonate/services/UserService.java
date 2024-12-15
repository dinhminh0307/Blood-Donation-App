package com.example.blooddonate.services;

import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.helpers.FirebaseHelper;
import com.example.blooddonate.models.User;

public class UserService {
    FirebaseHelper firebaseHelper;

    public UserService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public void getCurrentUser(GetUserCallback callback) {
        firebaseHelper.getCurrentUser(callback);
    }

    public String getUserUID() {
        return firebaseHelper.getUserId();
    }

    public void signOut() {
        firebaseHelper.signOut();
    }
}
