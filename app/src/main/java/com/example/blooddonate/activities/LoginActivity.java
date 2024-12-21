package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.LoginCallback;
import com.example.blooddonate.controllers.AdminController;
import com.example.blooddonate.services.UserService;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;

    Button loginBtn;

    UserService userService;

    AdminController adminController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        userService = new UserService();
        adminController = new AdminController();

        onLoginButtonClicked();
    }

    private void onLoginButtonClicked() {
        loginBtn = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Log.d("Login Activity", "Email or password is empty");
                    return;
                }

                // First, try to log in as an admin
                adminController.adminLogin(email, password, new LoginCallback() {
                    @Override
                    public void onSuccess(FirebaseUser admin) {
                        Log.d("Login Activity", "Admin login successful");
                        // Redirect to Admin Dashboard
                        // Use an intent to start an admin-specific activity
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d("Login Activity", "Admin login failed: " + exception.getMessage());

                        // If not an admin, attempt user login
                        userService.login(email, password, new LoginCallback() {
                            @Override
                            public void onSuccess(FirebaseUser user) {
                                Log.d("Login Activity", "User login successful");
                                // Redirect to User Dashboard
                                // Use an intent to start a user-specific activity
                                finish();
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Log.d("Login Activity", "User login failed: " + exception.getMessage());
                            }
                        });
                    }
                });
            }
        });
    }

}