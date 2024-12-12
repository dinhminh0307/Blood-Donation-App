package com.example.blooddonate.services;

import android.util.Log;

import com.example.blooddonate.callbacks.SignUpCallback;
import com.example.blooddonate.callbacks.SignUpResultCallback;
import com.example.blooddonate.helpers.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpService {
    FirebaseHelper firebaseHelper;

    public SignUpService() {
        this.firebaseHelper = new FirebaseHelper();
    }

    public void signup(String email, String password, String name, String phoneNumber, SignUpResultCallback callback) {
        firebaseHelper.SignUp(email, password, name, phoneNumber, new SignUpCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d("SignUpService", "Callback onSuccess invoked");
                if (callback != null) {
                    callback.onSignupSuccess(user);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("SignUpService", "Callback onFailure invoked");
                if (callback != null) {
                    callback.onSignupFailure(exception);
                }
            }
        });
    }
}
