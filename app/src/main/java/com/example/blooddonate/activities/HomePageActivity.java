package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.models.User;

public class HomePageActivity extends AppCompatActivity {
    ImageView findSiteButton;
    TextView siteMap;

    ImageView campaginAdd;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        setUser();
        onFindSiteBtnClicked();
        onSiteMapClicked();
        onCampaignClicked();
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
}