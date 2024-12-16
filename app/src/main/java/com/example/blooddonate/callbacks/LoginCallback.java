package com.example.blooddonate.callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface LoginCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(Exception exception);
}
