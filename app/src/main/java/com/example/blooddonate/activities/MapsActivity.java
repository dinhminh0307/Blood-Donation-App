package com.example.blooddonate.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.models.BloodDonationSite;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private String selectedAddress;
    private boolean isInteractive = false;

    private List<BloodDonationSite> sites = new ArrayList<>();

    private DonationSitesController donationSitesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Check if the map should allow marking locations
        isInteractive = getIntent().getBooleanExtra("isInteractive", false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        donationSitesController = new DonationSitesController();

        // Hide Confirm Button by default
        Button confirmButton = findViewById(R.id.confirm_location_button);
        confirmButton.setVisibility(isInteractive ? View.VISIBLE : View.GONE);
    }

    /**
     * Geocode an address to obtain its latitude and longitude.
     * @param address The address to geocode.
     * @return A LatLng object representing the latitude and longitude, or null if geocoding fails.
     */
    private LatLng geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Log.e("Geocoding", "No results found for address: " + address);
            }
        } catch (IOException e) {
            Log.e("Geocoding", "Error geocoding address: " + address, e);
        }
        return null;
    }

    private void getAllSiteMarker() {
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                sites.addAll(data);
                printAllSiteLocation();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MapsActivity", "Fail to add site " + e);
            }
        });
    }

    private void printAllSiteLocation() {
        // Geocode each site's address and add a marker
        for (BloodDonationSite site : sites) {
            Log.d("Print Site", "Sites: " + site.getLocation());
            LatLng latLng = geocodeAddress(site.getLocation());
            if (latLng != null) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(site.getName()));
            }
        }

        // Move camera to the first site's location if available
        if (!sites.isEmpty()) {
            LatLng firstLocation = geocodeAddress(sites.get(0).getLocation());
            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10)); // Zoom level 10
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getAllSiteMarker(); // fetch site data

        if (isInteractive) {
            // Allow user to mark locations only if isInteractive is true
            mMap.setOnMapClickListener(latLng -> {
                // Clear previous markers
                mMap.clear();

                // Add a marker on the clicked location
                selectedLatLng = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

                // Get the address for the selected location
                selectedAddress = getAddressFromLatLng(latLng);

                // Show Confirm Button
                Button confirmButton = findViewById(R.id.confirm_location_button);
                confirmButton.setVisibility(View.VISIBLE);
            });
        }
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0); // Return full address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }

    // Handle Confirm Button Click
    public void confirmLocation(View view) {
        if (selectedLatLng != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_location", selectedAddress);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
        }
    }
}


