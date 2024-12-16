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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.adapters.SiteCardAdapter;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private SiteCardAdapter adapter;
    private List<BloodDonationSite> sites = new ArrayList<>();
    private boolean isInteractive = false;

    private LatLng selectedLatLng;
    private String selectedAddress;

    private Button confirmButton;

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        isInteractive = getIntent().getBooleanExtra("isInteractive", false);
        userController = new UserController();
        // RecyclerView Setup
        recyclerView = findViewById(R.id.site_list_recycler);
        if (!isInteractive) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new SiteCardAdapter(this, sites, this::moveToMarker);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView in interactive mode
        }

        // Confirm Button
        confirmButton = findViewById(R.id.confirm_location_button);
        confirmButton.setVisibility(isInteractive ? View.VISIBLE : View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng vietnam = new LatLng(14.0583, 108.2772);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vietnam, 6));

        if (isInteractive) {
            setupInteractiveMode();
        } else {
            fetchAllSites();
        }
    }

    private void setupInteractiveMode() {
        // Allow user to add marker by clicking on the map
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear(); // Clear previous markers
            selectedLatLng = latLng;
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedAddress = getAddressFromLatLng(latLng);
        });

        confirmButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", selectedAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllSites() {
        DonationSitesController donationSitesController = new DonationSitesController();
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                sites.clear();
                for(BloodDonationSite tmp : data) {
                    if(!tmp.getOwner().equals(userController.getUserId())) {
                        sites.add(tmp);
                    }
                }

                adapter.notifyDataSetChanged();
                addMarkersToMap();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MapsActivity.this, "Failed to load sites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkersToMap() {
        for (BloodDonationSite site : sites) {
            LatLng latLng = geocodeAddress(site.getLocation());
            if (latLng != null) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(site.getName()));
            }
        }
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }

    private LatLng geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            Log.e("Geocoding", "Error: " + e.getMessage());
        }
        return null;
    }

    private void moveToMarker(int position) {
        BloodDonationSite site = sites.get(position);
        LatLng latLng = geocodeAddress(site.getLocation());
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        }
    }
}

