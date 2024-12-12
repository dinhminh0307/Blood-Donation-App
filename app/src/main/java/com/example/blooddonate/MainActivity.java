package com.example.blooddonate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.activities.HomePageActivity;
import com.example.blooddonate.activities.LoginActivity;
import com.example.blooddonate.activities.RegisterActivity;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.User;

public class MainActivity extends AppCompatActivity {

    Button createAccountBtn;
    Button loginBtn;

    UserController userController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        userController = new UserController();
        onRegisterClicked();
        onLoginButtonClicked();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in and update UI accordingly.
        userController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("Login", "User retrieved: " + user.getName());
                if (user != null) {
                    // Navigate to ProfileActivity
                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish(); // Optional: Finish MainActivity to prevent going back
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Login", "Error: " + e.getMessage());
                // Remain in the current activity (MainActivity)
            }
        });
    }

    private void onRegisterClicked() {
        createAccountBtn = findViewById(R.id.create_account_button);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onLoginButtonClicked() {
        loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}