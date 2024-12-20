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
import android.widget.Toast;

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
        holder.blood_type.setText(site.getBloodTypes());

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
        Button downloadButton = dialogView.findViewById(R.id.download_button); // New button

        // Set site information
        siteName.setText(site.getName());
        String details = String.format("Location: %s\nDate: %s\nUnits: %.1f\nBlood Type: %s",
                site.getLocation(), site.getDate(), site.getUnits(), site.getBloodTypes());
        siteDetails.setText(details);

        // Populate register table
        List<String> registers = site.getRegisters();
        final int[] idx = {0};
        for (String r : registers) {
            Log.d("BloodDonationSiteAdapter", "Registered: " + r);
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
                    nameCol.setText(registerName); // For simplicity, showing the register name
                    row.addView(nameCol);

                    // Add the row to the table
                    registerTable.addView(row);
                    idx[0]++;
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d("DonationSiteAdapter", "Error fetching user: " + exception.getMessage());
                }
            });
        }

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Download Button Click Listener
        downloadButton.setOnClickListener(v -> {
            StringBuilder csvData = new StringBuilder("Index,Name\n");
            idx[0] = 0; // Reset index
            for (String r : registers) {
                userController.getUserByUID(r, new GetUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        String registerName = user.getName();
                        csvData.append(idx[0] + 1).append(",").append(registerName).append("\n");
                        idx[0]++;

                        if (idx[0] == registers.size()) { // When all users are fetched
                            saveCSVToFile(csvData.toString(), site.getName() + "_registers.csv");
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d("DonationSiteAdapter", "Error fetching user for download: " + exception.getMessage());
                    }
                });
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Save CSV Data to File
    private void saveCSVToFile(String csvData, String fileName) {
        try {
            // Get the public Downloads directory
            java.io.File downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs(); // Create the Downloads directory if it doesn't exist
            }

            // Create the file in the Downloads directory
            java.io.File file = new java.io.File(downloadsDir, fileName);

            // Write CSV data to the file
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(csvData.getBytes());
            fos.flush();
            fos.close();

            Log.d("DonationSiteAdapter", "CSV File saved at: " + file.getAbsolutePath());
            Toast.makeText(context, "CSV saved to Downloads: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Notify the system to make the file accessible
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(android.net.Uri.fromFile(file));
            context.sendBroadcast(intent);

            // Prompt the user to open the file
            android.net.Uri fileUri = android.net.Uri.fromFile(file);
            Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
            openFileIntent.setDataAndType(fileUri, "text/csv");
            openFileIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there's an app to handle CSV files
            if (openFileIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(openFileIntent);
            } else {
                Toast.makeText(context, "No app found to open CSV files", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("DonationSiteAdapter", "Error saving CSV", e);
            Toast.makeText(context, "Failed to save CSV", Toast.LENGTH_SHORT).show();
        }
    }






    @Override
    public int getItemCount() {
        return filteredSites.size();
    }

    // Method to filter out sites owned by the current user
    public void updateData(List<BloodDonationSite> newSites, boolean mySite) {
        this.sites = new ArrayList<>(newSites);
        this.filteredSites = new ArrayList<>(newSites); // Update filtered list to match new data
        isMySite = mySite;

        notifyDataSetChanged(); // Notify RecyclerView about data change
    }

    // Method to filter sites based on the search query
    public void filterByQuery(String query) {
        if (query.isEmpty()) {
            // If query is empty, reset to show all sites
            filteredSites = new ArrayList<>(sites);
        } else {
            // Filter the sites whose name contains the query
            List<BloodDonationSite> tempFiltered = new ArrayList<>();
            for (BloodDonationSite site : sites) {
                if (site.getName().toLowerCase().contains(query)) {
                    tempFiltered.add(site);
                }
            }
            filteredSites = tempFiltered;
        }

        // Notify RecyclerView about the changes
        notifyDataSetChanged();
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location, blood_type;
        public Button requestSiteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.site_name);
            location = itemView.findViewById(R.id.site_location);
            requestSiteButton = itemView.findViewById(R.id.site_request_button);
            blood_type = itemView.findViewById(R.id.blood_type);
        }
    }
}
