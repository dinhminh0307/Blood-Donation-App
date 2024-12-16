package com.example.blooddonate.activities;

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
import com.example.blooddonate.services.UserService;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;

    Button loginBtn;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        userService = new UserService();

        onLoginButtonClicked();
    }

    private void onLoginButtonClicked() {
        loginBtn = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    return;
                }

                userService.login(email, password, new LoginCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Log.d("Login Activity", "Signin successfully");
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d("Login Activity", "Signin failed " + exception.getMessage());
                    }
                });
            }
        });
    }
}