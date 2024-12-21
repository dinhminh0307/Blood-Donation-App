package com.example.blooddonate.services;

import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.callbacks.LoginCallback;
import com.example.blooddonate.helpers.FirebaseHelper;
import com.example.blooddonate.models.User;

public class AdminService {
    FirebaseHelper firebaseHelper;

    public AdminService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public void adminLogin(String email, String password, LoginCallback cb) {
        firebaseHelper.adminLogin(email, password, cb);
    }
}
