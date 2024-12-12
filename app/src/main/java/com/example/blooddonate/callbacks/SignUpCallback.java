package com.example.blooddonate.callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface SignUpCallback {
    void onSuccess(FirebaseUser user);
    void onFailure(Exception exception);
}
