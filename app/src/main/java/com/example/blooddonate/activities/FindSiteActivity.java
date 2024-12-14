package com.example.blooddonate.activities;

import android.os.Bundle;
import android.util.Log;

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
import com.example.blooddonate.models.BloodDonationSite;

import java.util.ArrayList;
import java.util.List;

public class FindSiteActivity extends AppCompatActivity {
    RecyclerView siteRecycleView;

    DonationSitesController donationSitesController;

    BloodDonationSiteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_site);

        donationSitesController = new DonationSitesController();


        siteRecycleView = findViewById(R.id.recycler_view);
        siteRecycleView.setLayoutManager(new LinearLayoutManager(this));

        List<BloodDonationSite> sites = new ArrayList<>();
        adapter = new BloodDonationSiteAdapter(this, sites);
        // Attach the adapter to the RecyclerView
        siteRecycleView.setAdapter(adapter);
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                sites.clear();
                sites.addAll(data);

                for (BloodDonationSite tmp: sites) {
                    Log.d("Firestore", "Successfully fetch" + tmp.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error fetching donation sites", e);
            }
        });

    }


}