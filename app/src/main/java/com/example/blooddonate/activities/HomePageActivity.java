package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.adapters.NotificationsAdapter;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.NotificationController;
import com.example.blooddonate.models.Notification;
import com.example.blooddonate.models.User;
import com.example.blooddonate.services.ThreadService;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    ImageView findSiteButton;
    TextView siteMap;

    ImageView campaginAdd, profile;

    User user;

    List<Notification> notifications = new ArrayList<>();

    View notificationDot ; // Notification dot
    ImageView notificationIcon;

    private NotificationController notificationController;


    private ThreadService threadService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        notificationController = new NotificationController();

        notificationComponentInit();

        notificationFetching();

        setUser();
        onFindSiteBtnClicked();
        onSiteMapClicked();
        onCampaignClicked();
        onProfileClicked();
        onNotificationClicked();
    }

    private void notificationComponentInit() {
        notificationDot = findViewById(R.id.notification_dot);
        notificationIcon = findViewById(R.id.notification_icon);
    }

    private void notificationFetching() {
        threadService = new ThreadService();
        threadService.startPolling(new DataFetchCallback<Notification>() {
            private void run() {
                // Update UI or notify user about new notifications
                boolean openDot = false;
                for (Notification tmp : notifications) {
                    if (!tmp.getIsRead()) {
                        openDot = true;
                        break;
                    }
                }
                showNotificationDot(openDot); // Show the dot if there are new notifications
            }

            @Override
            public void onSuccess(List<Notification> data) {
                Log.d("HomePage Activity", "Success");
                notifications.clear();
                notifications.addAll(data);
                runOnUiThread(this::run);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HomePageActivity", "Error fetching notifications: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadService.stopPolling();
    }


    public void setUser() {
        user = (User) getIntent().getSerializableExtra("user");
    }

    private void onFindSiteBtnClicked() {
        findSiteButton =findViewById(R.id.nav_find_donor);
        findSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, FindSiteActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void onSiteMapClicked() {
        siteMap = findViewById(R.id.nearby_donation_sites);
        siteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MapsActivity.class);
                intent.putExtra("user", user);

                startActivity(intent);
            }
        });
    }

    private void onCampaignClicked() {
        campaginAdd = findViewById(R.id.campaigns_icon);
        campaginAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AddSiteActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }


    private void onProfileClicked() {
        profile = findViewById(R.id.nav_profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onNotificationClicked() {
        notificationIcon.setOnClickListener(v -> {
            showNotificationsDialog();
            showNotificationDot(false); // Hide the dot when the user opens the notification dialog
            for(Notification tmp : notifications) {
                notificationController.updateNotificationField(tmp, "isRead", true);
            }
        });
    }

    private void showNotificationDot(boolean show) {
        if (show) {
            notificationDot.setVisibility(View.VISIBLE);
        } else {
            notificationDot.setVisibility(View.GONE);
        }
    }

    private void showNotificationsDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notification, null);

        // Initialize RecyclerView
        RecyclerView recyclerView = dialogView.findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data for notifications

        // Set adapter
        NotificationsAdapter adapter = new NotificationsAdapter(this, notifications);
        recyclerView.setAdapter(adapter);

        // Build and show the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}