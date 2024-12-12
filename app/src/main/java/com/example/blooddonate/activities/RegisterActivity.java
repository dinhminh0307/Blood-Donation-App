package com.example.blooddonate.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.SignUpResultCallback;
import com.example.blooddonate.controllers.SignupController;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText fullNameInput;

    EditText emailInput;

    EditText phoneNumInput;

    EditText passwordInput;

    EditText confirmPasswordInput;

    Button signUpBtn;

    SignupController signupController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        signupController = new SignupController();

        onSignUpBtnClicked();
    }

    private void onSignUpBtnClicked() {
        signUpBtn = findViewById(R.id.create_account_button);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneNumInput = findViewById(R.id.phone_number_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String phoneNumber = phoneNumInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confrimPassword = confirmPasswordInput.getText().toString();


                // handle validation input
                if(fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()||
                    password.isEmpty() || confrimPassword.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(confrimPassword)) {
                    Toast.makeText(RegisterActivity.this, "Wrong confirm passowrd", Toast.LENGTH_SHORT).show();
                    return;
                }
//                FirebaseUser checkUser;
                signupController.signup(email, password, fullName, phoneNumber, new SignUpResultCallback() {

                    @Override
                    public void onSignupSuccess(FirebaseUser user) {
                        Toast.makeText(RegisterActivity.this, "Register successfully " + user.getEmail(), Toast.LENGTH_SHORT).show();
//                        checkUser = user;
                    }

                    @Override
                    public void onSignupFailure(Exception exception) {
                        Toast.makeText(RegisterActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
    }
}