package com.example.blooddonate.services;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.callbacks.LoginCallback;
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

    public void login(String email, String password, LoginCallback cb) {
        firebaseHelper.login(email,password,cb);
    }

    public void updateUserField(String siteId, String fieldName, Object val) {
        firebaseHelper.updateUserField(siteId, fieldName, val);
    }

    public void getUserByUID(String id, GetUserCallback cb) {
        firebaseHelper.getUserByUID(id, cb);
    }

    public void getAllUsers(DataFetchCallback<User> cb) {
        firebaseHelper.findAllUser(cb);
    }
}
