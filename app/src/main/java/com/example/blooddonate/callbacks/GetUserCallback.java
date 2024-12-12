package com.example.blooddonate.callbacks;

import com.example.blooddonate.models.User;
import com.google.firebase.auth.FirebaseUser;

public interface GetUserCallback {
    void onSuccess(User user);
    void onFailure(Exception exception);
}
