package com.example.blooddonate.activities;

import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.adapters.BloodDonationSiteAdapter;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.dialogs.FilterDialogFragment;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;

import java.util.ArrayList;
import java.util.List;

public class FindSiteActivity extends AppCompatActivity {
    RecyclerView siteRecycleView;

    DonationSitesController donationSitesController;

    UserController userController;

    BloodDonationSiteAdapter adapter;

    Button searchButton;

    ImageView filter, homeNav, profileNav;

    EditText searchEditText;

    List<BloodDonationSite> sites;

    TextView tabMySites, tabOtherSites;

    String currentUserId;

    private String previousCity = "";
    private String previousBloodType = "";
    private String previousDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_site);

        // Initialize data and UI
        donationSitesController = new DonationSitesController();
        userController = new UserController();
        currentUserId = userController.getUserId();
        sites = new ArrayList<>();

        siteRecycleView = findViewById(R.id.recycler_view);
        searchButton = findViewById(R.id.search_button);
        searchEditText = findViewById(R.id.search_edit_text);
        filter = findViewById(R.id.filter_icon);

        tabMySites = findViewById(R.id.tab_my_sites);
        tabOtherSites = findViewById(R.id.tab_other_sites);

        // Initialize adapter
        initAdapter();

        // Fetch data and display
        fetchSites();

        // Set up filter and search logic
        onFilterClicked();
        onSearchInputChanged();
        onTabClicked(); // Handle tab clicks
        onNavClicked();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-fetch the current user
        userController.getCurrentUser(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("FindSiteActivity", "Current user fetched: " + user.getName());
                currentUserId = userController.getUserId(); // Update current user ID

                // Fetch and display sites
                fetchSites();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FindSiteActivity", "Failed to fetch current user", e);
            }
        });
    }

    private void initAdapter() {
        // Initialize the adapter with an empty list
        ActivityResultLauncher<Intent> mapResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Extract the location from the result
                        String selectedLocation = result.getData().getStringExtra("selected_location");
                        Log.d("FindSiteActivity", "Selected Location: " + selectedLocation);

                        // Update the UI or the data in the adapter
                        adapter.updateSelectedLocation(selectedLocation);
                    }
                }
        );

        adapter = new BloodDonationSiteAdapter(this, sites, currentUserId, mapResultLauncher);
        siteRecycleView.setLayoutManager(new LinearLayoutManager(this));
        siteRecycleView.setAdapter(adapter);
    }

    public void fetchSites() {
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                // Clear and update the sites list
                sites.clear();
                sites.addAll(data);

                // Show the currently selected tab's data
                if (tabMySites.getCurrentTextColor() == getResources().getColor(android.R.color.black)) {
                    showMySites();
                } else {
                    showOtherSites();
                }

                Log.d("Firestore", "Fetched sites: " + sites.size());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error fetching donation sites", e);
            }
        });
    }


    private void onFilterClicked() {
        filter.setOnClickListener(v -> {
            // Show the FilterDialogFragment
            FilterDialogFragment filterDialog = new FilterDialogFragment(sites);
            filterDialog.setPreviousFilters(previousCity, previousBloodType, previousDate); // Pass previous selections
            filterDialog.setFilterDialogListener((city, bloodType, date) -> {
                // Save the selected filters
                previousCity = city;
                previousBloodType = bloodType;
                previousDate = date;

                if (city.isEmpty() && bloodType.isEmpty() && date.isEmpty()) {
                    // Reset to show all sites
                    adapter.updateData(sites, false);
                } else {
                    // Filter logic
                    List<BloodDonationSite> filteredSites = new ArrayList<>();
                    for (BloodDonationSite site : sites) {
                        boolean matchesCity = city.isEmpty() || site.getLocation().toLowerCase().contains(city.toLowerCase());
                        boolean matchesBloodType = bloodType.isEmpty() || site.getBloodTypes().equalsIgnoreCase(bloodType);
                        boolean matchesDate = date.isEmpty() || site.getDate().equalsIgnoreCase(date);

                        if (matchesCity && matchesBloodType && matchesDate) {
                            filteredSites.add(site);
                        }
                    }
                    adapter.updateData(filteredSites, false);
                }
            });
            filterDialog.show(getSupportFragmentManager(), "FilterDialog");
        });
    }



    private void onSearchInputChanged() {
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the sites based on the search query
                String query = s.toString().trim().toLowerCase();
                adapter.filterByQuery(query);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text is changed
            }
        });
    }


    private void onTabClicked() {
        tabMySites.setOnClickListener(v -> showMySites());
        tabOtherSites.setOnClickListener(v -> showOtherSites());
    }

    private void showMySites() {
        // Update tab UI
        tabMySites.setTextColor(getResources().getColor(android.R.color.black));
        tabOtherSites.setTextColor(getResources().getColor(android.R.color.darker_gray));

        tabMySites.setBackgroundColor(getResources().getColor(android.R.color.white));
        tabOtherSites.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Filter sites owned by the current user
        List<BloodDonationSite> mySites = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            if (site.getOwner() != null && site.getOwner().equals(currentUserId)) {
                mySites.add(site);
            }
        }

        Log.d("FindSiteActivity", "My Sites Count: " + mySites.size());
        adapter.updateData(mySites, true);
    }

    private void showOtherSites() {
        // Update tab UI
        tabMySites.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tabOtherSites.setTextColor(getResources().getColor(android.R.color.black));

        tabMySites.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabOtherSites.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Filter sites not owned by the current user
        List<BloodDonationSite> otherSites = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            if (!site.getOwner().equals(currentUserId) && !site.getRegisters().contains(currentUserId)) {
                otherSites.add(site);
                Log.d("FindSiteActivity", "Data owner UID: " + site.getOwner() + "My UID " + currentUserId);
            }
        }

        Log.d("FindSiteActivity", "Other Sites Count: " + otherSites.size());
        adapter.updateData(otherSites, false);
    }



    private  void onNavClicked() {
        homeNav = findViewById(R.id.home_nav);
        profileNav = findViewById(R.id.profile_nav);

        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindSiteActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userController.getUserByUID(currentUserId, new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(FindSiteActivity.this, ProfileActivity.class);
                        intent.putExtra("user", user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
            }
        });
    }

}


