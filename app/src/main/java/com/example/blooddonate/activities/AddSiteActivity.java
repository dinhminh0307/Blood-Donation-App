package com.example.blooddonate.activities;

import android.content.Intent;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    ImageView mapIcon, dateIcon;

    User user;

    String pickedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_site);
        user = (User) getIntent().getSerializableExtra("user");
        userController = new UserController();
        donationSitesController = new DonationSitesController();

        // init component
        componentInit();
        setUpSpinner();
        onMapSelection();
        onAddSiteButtonClicked();
        onDateIconClicked();
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

    private void componentInit() {
        siteName = findViewById(R.id.site_name_input);
        bloodGroupSpinner = findViewById(R.id.blood_group_spinner);
        addSiteBtn = findViewById(R.id.add_site_button);
        unitInput = findViewById(R.id.units_input);
        locationInput = findViewById(R.id.location_input);
        dateInput = findViewById(R.id.date_input);
    }

    private void onAddSiteButtonClicked() {


        addSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = siteName.getText().toString();
                String location = locationInput.getText().toString();
                String date = dateInput.getText().toString();
                int unit = Integer.parseInt(unitInput.getText().toString());
                String selectedBloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                List<String> registers = new ArrayList<>();
                String siteId = "S" + userController.getUserId();
                BloodDonationSite site = new BloodDonationSite(name, location, date, selectedBloodGroup, unit, userController.getUserId(), registers);
                donationSitesController.addSites(site);
                finish();
            }
        });

    }

    private void onDateIconClicked() {
        dateIcon = findViewById(R.id.calendar_icon);

        dateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        AddSiteActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                pickedDate = Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
                                dateInput.setText(pickedDate);
                            }

                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();



            }
        });
    }
}