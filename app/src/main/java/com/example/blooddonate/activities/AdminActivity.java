package com.example.blooddonate.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.adapters.DonationSiteTableAdapter;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationSiteTableAdapter adapter;
    private List<BloodDonationSite> sites = new ArrayList<>(); // Initialize the list
    private List<BloodDonationSite> currentPageData = new ArrayList<>();
    private int currentPage = 0;
    private static final int ROWS_PER_PAGE = 5;

    private DonationSitesController donationSitesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        donationSitesController = new DonationSitesController();

        recyclerView = findViewById(R.id.sites_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button previousButton = findViewById(R.id.previous_page_button);
        Button nextButton = findViewById(R.id.next_page_button);

        previousButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateRecyclerView();
            }
        });

        nextButton.setOnClickListener(v -> {
            if ((currentPage + 1) * ROWS_PER_PAGE < sites.size()) {
                currentPage++;
                updateRecyclerView();
            }
        });

        getSitesFromServer();
    }

    private void updateRecyclerView() {
        if (sites == null || sites.isEmpty()) {
            return; // Avoid NullPointerException or trying to update empty data
        }

        int start = currentPage * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, sites.size());
        currentPageData.clear();
        currentPageData.addAll(sites.subList(start, end));

        if (adapter == null) {
            adapter = new DonationSiteTableAdapter(this, currentPageData);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(); // Notify adapter of data changes
        }
    }

    private void getSitesFromServer() {
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                for(BloodDonationSite tmp : data) {
                    Log.d("Admin Activity", "Site:" + tmp.getName());
                }
                if (data != null) {
                    sites.clear();
                    sites.addAll(data);
                    runOnUiThread(AdminActivity.this::updateRecyclerView); // Ensure UI updates on the main thread
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure (e.g., show a toast or log the error)
                Log.d("Admin Activity", "Error: " + e.getMessage());
            }
        });
    }
}
