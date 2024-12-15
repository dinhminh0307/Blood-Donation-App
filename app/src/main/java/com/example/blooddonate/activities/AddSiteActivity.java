package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;

import java.util.HashMap;
import java.util.Map;

public class AddSiteActivity extends AppCompatActivity {
    DonationSitesController donationSitesController;

    UserController userController;

    EditText siteName;
    EditText unitInput, locationInput;

    EditText dateInput;
    RadioGroup timeRadio;

    Button addSiteBtn;

    Spinner bloodGroupSpinner;

    ImageView mapIcon;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_site);
        user = (User) getIntent().getSerializableExtra("user");
        userController = new UserController();
        donationSitesController = new DonationSitesController();
        setUpSpinner();
        onMapSelection();
        onAddSiteButtonClicked();
    }

    private void setUpSpinner() {
        bloodGroupSpinner = findViewById(R.id.blood_group_spinner);
        // set up blood group adpter
        ArrayAdapter<CharSequence> bloodGroupAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

    }

    private void onMapSelection() {
        mapIcon = findViewById(R.id.map_icon);// Handle map icon click
        mapIcon.setOnClickListener(v -> {
            // Navigate to MapsActivity to mark the location
            Intent intent = new Intent(AddSiteActivity.this, MapsActivity.class);
            intent.putExtra("isInteractive", true); // Enable marking locations
            startActivityForResult(intent, 1001); // Request code 1001
        });

    }
    // Handle the result from MapsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            // Get the selected location from MapsActivity
            String selectedLocation = data.getStringExtra("selected_location");

            // Update the location input field
            EditText locationInput = findViewById(R.id.location_input);
            locationInput.setText(selectedLocation);
        }
    }


    private void onAddSiteButtonClicked() {
        siteName = findViewById(R.id.site_name_input);
        bloodGroupSpinner = findViewById(R.id.blood_group_spinner);
        addSiteBtn = findViewById(R.id.add_site_button);
        unitInput = findViewById(R.id.units_input);
        locationInput = findViewById(R.id.location_input);
        dateInput = findViewById(R.id.date_input);

        addSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = siteName.getText().toString();
                String location = locationInput.getText().toString();
                String date = dateInput.getText().toString();
                int unit = Integer.parseInt(unitInput.getText().toString());
                String selectedBloodGroup = bloodGroupSpinner.getSelectedItem().toString();

                BloodDonationSite site = new BloodDonationSite(name, location, date, selectedBloodGroup, unit, userController.getUserId());
                donationSitesController.addSites(site);
                finish();
            }
        });

    }
}