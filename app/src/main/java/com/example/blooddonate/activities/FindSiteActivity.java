package com.example.blooddonate.activities;

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
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.dialogs.FilterDialogFragment;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.ArrayList;
import java.util.List;

public class FindSiteActivity extends AppCompatActivity {
    RecyclerView siteRecycleView;

    DonationSitesController donationSitesController;

    UserController userController;

    BloodDonationSiteAdapter adapter;

    Button searchButton;

    ImageView filter;

    EditText searchEditText;

    List<BloodDonationSite> sites;

    TextView tabMySites, tabOtherSites;

    String currentUserId;

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
    }

    private void initAdapter() {
        // Initialize the adapter with an empty list
        adapter = new BloodDonationSiteAdapter(this, sites, currentUserId);
        siteRecycleView.setLayoutManager(new LinearLayoutManager(this));
        siteRecycleView.setAdapter(adapter);
    }

    private void fetchSites() {
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                // Update the main data list
                sites.clear();
                sites.addAll(data);

                // Initially show "Other Sites"
                showOtherSites();

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
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.setFilterDialogListener((city, bloodType, date) -> {
                // Apply filter logic if required
                Log.d("Filters", "City: " + city + ", Blood Type: " + bloodType + ", Date: " + date);
                // You can add more filtering logic here if needed
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
                // Apply the filter as the user types
                adapter.filterByFirstLetter(s.toString().trim());
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
        tabMySites.setTextColor(getResources().getColor(android.R.color.black)); // Selected tab color
        tabOtherSites.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Unselected tab color

        tabMySites.setBackgroundColor(getResources().getColor(android.R.color.white)); // Selected tab background
        tabOtherSites.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Unselected tab background

        // Filter sites owned by the current user
        List<BloodDonationSite> mySites = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            if (site.getOwner().equals(currentUserId)) {
                mySites.add(site);
            }
        }
        adapter.updateData(mySites);
    }

    private void showOtherSites() {
        // Update tab UI
        tabMySites.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Unselected tab color
        tabOtherSites.setTextColor(getResources().getColor(android.R.color.black)); // Selected tab color

        tabMySites.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Unselected tab background
        tabOtherSites.setBackgroundColor(getResources().getColor(android.R.color.white)); // Selected tab background

        // Filter sites not owned by the current user
        List<BloodDonationSite> otherSites = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            if (!site.getOwner().equals(currentUserId)) {
                otherSites.add(site);
            }
        }
        adapter.updateData(otherSites);
    }

}


