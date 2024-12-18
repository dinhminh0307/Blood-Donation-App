package com.example.blooddonate.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.callbacks.LoginCallback;
import com.example.blooddonate.callbacks.SignUpCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "Firebase Helper";

    private static String userId = "";

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

    public void login(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onSuccess(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public String getUserId() {
        return this.userId;
    }

    public void getCurrentUser(GetUserCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                Log.d(TAG, "User retrieved: " + user.getName());
                                callback.onSuccess(user);
                            } else {
                                callback.onFailure(new Exception("Failed to parse User object."));
                            }
                        } else {
                            callback.onFailure(new Exception("User document does not exist."));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error retrieving user document", e);
                        callback.onFailure(e);
                    });
        } else {
            callback.onFailure(new Exception("No authenticated user found."));
        }
    }

    public void getUserByUID(String uid, GetUserCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            Log.d(TAG, "User retrieved by UID: " + user.getName());
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(new Exception("Failed to parse User object."));
                        }
                    } else {
                        callback.onFailure(new Exception("User document does not exist for UID: " + uid));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving user document by UID", e);
                    callback.onFailure(e);
                });
    }

    public void updateUserField(String siteId, String fieldName, Object value) {
        db.collection("users")
                .document(siteId)
                .update(fieldName, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Field " + fieldName + " updated successfully for site: " + siteId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating field " + fieldName + " for site: " + siteId, e);
                });
    }

    public void findAllUser(DataFetchCallback<User> callback) {
        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    // Notify success with the fetched data
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    // Notify failure
                    callback.onFailure(e);
                });
    }

    public void signOut() {
        mAuth.signOut();
    }
}
