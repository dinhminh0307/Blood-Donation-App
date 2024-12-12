package com.example.blooddonate.callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface SignUpResultCallback {
    void onSignupSuccess(FirebaseUser user);
    void onSignupFailure(Exception exception);
}
