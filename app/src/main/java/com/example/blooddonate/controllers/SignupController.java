package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.SignUpResultCallback;
import com.example.blooddonate.services.SignUpService;

public class SignupController {
    SignUpService signUpService;

    public SignupController() {
        this.signUpService = new SignUpService();
    }

    public void signup(String email, String password, String name, String phoneNumber, SignUpResultCallback callback)
    {
        this.signUpService.signup(email, password, name, phoneNumber, callback);
    }
}
