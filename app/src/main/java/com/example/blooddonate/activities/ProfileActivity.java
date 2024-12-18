package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.blooddonate.MainActivity;
import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.User;

public class ProfileActivity extends AppCompatActivity {

    private ImageView menuIcon;

    private UserController userController;

    private TextView userName;

    private User user;

    private ImageView homeNav, profileNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        userController = new UserController();
        user = (User) getIntent().getSerializableExtra("user");
        // Initialize the menu icon
        menuIcon = findViewById(R.id.menuId);

        profileInfoSetUp();
        // Set click listener for the menu icon
        menuIcon.setOnClickListener(this::showPopupMenu);
        onNavClicked();
    }

    private void profileInfoSetUp() {
        userController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                userName = findViewById(R.id.userName);
                userName.setText(user.getName());
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });

    }

    private void showPopupMenu(View anchor) {
        // Create a PopupMenu anchored to the menu icon
        PopupMenu popupMenu = new PopupMenu(this, anchor);

        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);

        // Show the popup menu
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit) {
            Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show();
            // Add logic for Edit action
            return true;
        } else if (itemId == R.id.menu_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            // Add logic for Settings action
            return true;
        } else if (itemId == R.id.menu_logout) {
            userController.signOut();
            // Clear the activity stack and navigate to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack
            startActivity(intent);
            finish(); // Finish current activity
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            // Add logic for Logout action
            return true;
        } else {
            return false;
        }
    }

    private  void onNavClicked() {
        homeNav = findViewById(R.id.home_nav);
        profileNav = findViewById(R.id.profile_nav);

        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

}
