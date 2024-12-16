package com.example.blooddonate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.activities.RequestActivity;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.services.SearchEngineService;

import java.util.ArrayList;
import java.util.List;

public class BloodDonationSiteAdapter extends RecyclerView.Adapter<BloodDonationSiteAdapter.ViewHolder> {
    private List<BloodDonationSite> sites; // Main list of all sites
    private List<BloodDonationSite> filteredSites; // Filtered list for display
    private Context context;

    private SearchEngineService searchEngineService;

    public BloodDonationSiteAdapter(Context context, List<BloodDonationSite> sites) {
        this.context = context;
        this.sites = sites;
        this.filteredSites = new ArrayList<>(sites); // Initialize filtered list
        searchEngineService = new SearchEngineService();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.site_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BloodDonationSite site = filteredSites.get(position);
        holder.name.setText(site.getName());
        holder.location.setText(site.getLocation());

        holder.requestSiteButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestActivity.class);
            intent.putExtra("site_name", site.getName());
            intent.putExtra("site_location", site.getLocation());
            intent.putExtra("site_data", site);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredSites.size();
    }

    // Method to filter by first letter
    public void filterByFirstLetter(String letter) {
        filteredSites = searchEngineService.searchDonationSites(sites,letter);
        notifyDataSetChanged(); // Notify RecyclerView about data change
    }

    // Method to update data in the adapter
    public void updateData(List<BloodDonationSite> newSites) {
        this.sites = new ArrayList<>(newSites);
        this.filteredSites = new ArrayList<>(newSites); // Reset filtered list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location;
        public Button requestSiteButton;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.site_name);
            location = itemView.findViewById(R.id.site_location);
            requestSiteButton = itemView.findViewById(R.id.site_request_button); // Add this line
        }
    }
}




