package com.example.blooddonate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.activities.RequestActivity;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;
import com.example.blooddonate.services.SearchEngineService;

import java.util.ArrayList;
import java.util.List;

public class BloodDonationSiteAdapter extends RecyclerView.Adapter<BloodDonationSiteAdapter.ViewHolder> {
    private List<BloodDonationSite> sites; // Main list of all sites
    private List<BloodDonationSite> filteredSites; // Filtered list for display
    private Context context;

    private String currentUserId; // Holds the current user's UID

    private boolean isMySite = false;

    private UserController userController;


    private SearchEngineService searchEngineService;

    public BloodDonationSiteAdapter(Context context, List<BloodDonationSite> sites, String currentUserId) {
        this.context = context;
        this.sites = sites;
        this.filteredSites = new ArrayList<>(sites); // Initialize filtered list
        this.currentUserId = currentUserId;
        searchEngineService = new SearchEngineService();
        userController = new UserController();
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

        if(isMySite) {
            holder.requestSiteButton.setText("View Donation Site");
        } else {
            holder.requestSiteButton.setText("REQUEST SITE");
        }

        holder.requestSiteButton.setOnClickListener(v -> {
            if (site.getOwner().equals(currentUserId)) {
                // Show the modal dialog for the user's own site
                showSiteInfoDialog(site);
            } else {
                // Navigate to the RequestActivity for other sites
                Intent intent = new Intent(context, RequestActivity.class);
                intent.putExtra("site_name", site.getName());
                intent.putExtra("site_location", site.getLocation());
                intent.putExtra("site_data", site);
                context.startActivity(intent);
            }
        });
    }

    private void showSiteInfoDialog(BloodDonationSite site) {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_site_info, null);

        // Initialize dialog components
        TextView siteName = dialogView.findViewById(R.id.site_name);
        TextView siteDetails = dialogView.findViewById(R.id.site_details);
        LinearLayout registerTable = dialogView.findViewById(R.id.register_table);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        // Set site information
        siteName.setText(site.getName());
        String details = String.format("Location: %s\nDate: %s\nUnits: %.1f\nBlood Type: %s",
                site.getLocation(), site.getDate(), site.getUnits(), site.getBloodTypes());
        siteDetails.setText(details);

        // Populate register table
        List<String> registers = site.getRegisters();
        final int[] idx = {0};
        for(String r: registers) {
            Log.d("BloodDonataionSiteAdapter", "Registered: " + r);
            userController.getUserByUID(r, new GetUserCallback() {
                @Override
                public void onSuccess(User user) {


                    String registerName = user.getName();

                    // Create a row for each register
                    LinearLayout row = new LinearLayout(context);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding(8, 8, 8, 8);

                    // Register column
                    TextView registerCol = new TextView(context);
                    registerCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    registerCol.setText(String.valueOf(idx[0] + 1)); // Register index
                    row.addView(registerCol);

                    // Name column
                    TextView nameCol = new TextView(context);
                    nameCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    nameCol.setText(registerName); // For simplicity, showing the register ID (or map to name if available)
                    row.addView(nameCol);

                    // Add the row to the table
                    registerTable.addView(row);
                    idx[0]++;
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d("Donationsite Adapter", "Error get user site: " + exception.getMessage());
                }
            });
        }

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return filteredSites.size();
    }

    // Method to filter out sites owned by the current user
    public void updateData(List<BloodDonationSite> newSites, boolean mySite) {
        this.sites = new ArrayList<>(newSites);
        // Filter out sites where the owner matches the current user
        this.filteredSites = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            this.filteredSites.add(site);
        }

        isMySite = mySite;

        notifyDataSetChanged(); // Notify RecyclerView about data change
    }

    // Filter method (e.g., by first letter)
    public void filterByFirstLetter(String letter) {
        List<BloodDonationSite> tempFiltered = new ArrayList<>();
        for (BloodDonationSite site : sites) {
            if (!site.getOwner().equals(currentUserId) &&
                    site.getName().toLowerCase().startsWith(letter.toLowerCase())) {
                tempFiltered.add(site);
            }
        }
        filteredSites = tempFiltered;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location;
        public Button requestSiteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.site_name);
            location = itemView.findViewById(R.id.site_location);
            requestSiteButton = itemView.findViewById(R.id.site_request_button);
        }
    }
}
