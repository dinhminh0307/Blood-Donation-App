package com.example.blooddonate.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.blooddonate.callbacks.SignUpCallback;
import com.example.blooddonate.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "Firebase Helper";

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void SignUp(String email, String password, String name, String phoneNumber, SignUpCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Create a User object
                                User user = new User(name, phoneNumber, email);

                                // Save the User object to Firestore
                                db.collection("users").document(firebaseUser.getUid())
                                        .set(user)
                                        .addOnCompleteListener(saveTask -> {
                                            Log.d(TAG, "execute task");
                                            if (saveTask.isSuccessful()) {
                                                Log.d(TAG, "User profile saved to Firestore");
                                                callback.onSuccess(firebaseUser);
                                            } else {
                                                Log.w(TAG, "Failed to save user profile", saveTask.getException());
                                                callback.onFailure(saveTask.getException());
                                            }
                                        });
                            } else {
                                callback.onFailure(new Exception("User is null after successful registration"));
                            }
                        } else {
                            // Sign up failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }
}
