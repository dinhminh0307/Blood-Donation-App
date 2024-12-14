package com.example.blooddonate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blooddonate.R;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.HashMap;
import java.util.Map;

public class AddSiteActivity extends AppCompatActivity {
    DonationSitesController donationSitesController;

    EditText siteName;
    EditText unitInput;

    EditText dateInput;
    RadioGroup timeRadio;

    Button addSiteBtn;

    Spinner bloodGroupSpinner, genderSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_site);
        donationSitesController = new DonationSitesController();
        setUpSpinner();
        onAddSiteButtonClicked();
    }

    private void setUpSpinner() {
        bloodGroupSpinner = findViewById(R.id.blood_group_spinner);
        genderSpinner = findViewById(R.id.gender_spinner);
        // set up blood group adpter
        ArrayAdapter<CharSequence> bloodGroupAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

        // setup gender dapter
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void onAddSiteButtonClicked() {
        siteName = findViewById(R.id.site_name_input);
        bloodGroupSpinner = findViewById(R.id.blood_group_spinner);
        genderSpinner = findViewById(R.id.gender_spinner);
        addSiteBtn = findViewById(R.id.add_site_button);
        unitInput = findViewById(R.id.units_input);
        dateInput = findViewById(R.id.date_input);

        addSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = siteName.getText().toString();
                String location = "HN";
                String date = dateInput.getText().toString();
                int unit = Integer.parseInt(unitInput.getText().toString());
                String selectedBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                String selectedGender = genderSpinner.getSelectedItem().toString();
                Map<String, Integer> bloodType = new HashMap<>();
                bloodType.put(selectedBloodGroup, unit);

                BloodDonationSite site = new BloodDonationSite(name, location, date, bloodType, 0, 0);
                donationSitesController.addSites(site);
            }
        });

    }
}