package com.example.blooddonate.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.activities.AddSiteActivity;
import com.example.blooddonate.activities.FindSiteActivity;
import com.example.blooddonate.activities.MapsActivity;
import com.example.blooddonate.activities.RequestActivity;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;
import com.example.blooddonate.services.SearchEngineService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodDonationSiteAdapter extends RecyclerView.Adapter<BloodDonationSiteAdapter.ViewHolder> {
    private List<BloodDonationSite> sites; // Main list of all sites
    private List<BloodDonationSite> filteredSites; // Filtered list for display
    private Context context;

    private String currentUserId; // Holds the current user's UID

    private boolean isMySite = false;

    private UserController userController;


    private SearchEngineService searchEngineService;

    String pickedDate = "";

    EditText locationInput;

    DonationSitesController donationSitesController;




    private ActivityResultLauncher<Intent> mapResultLauncher;

    public BloodDonationSiteAdapter(Context context, List<BloodDonationSite> sites, String currentUserId, ActivityResultLauncher<Intent> mapResultLauncher) {
        this.context = context;
        this.sites = sites;
        this.filteredSites = new ArrayList<>(sites); // Initialize filtered list
        this.currentUserId = currentUserId;
        this.mapResultLauncher = mapResultLauncher; // Store the launcher
        searchEngineService = new SearchEngineService();
        userController = new UserController();
        donationSitesController = new DonationSitesController();
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

    private void onDateIconClicked(View dialogView) {
        ImageView dateIcon = dialogView.findViewById(R.id.edit_calendar_icon);
        EditText dateInput = dialogView.findViewById(R.id.edit_date_input);

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
                        context,
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



    private void onMapSelection(View dialogView) {
        ImageView mapIcon = dialogView.findViewById(R.id.edit_map_icon);
        mapIcon.setOnClickListener(v -> {
            // Launch the MapsActivity using the launcher
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("isInteractive", true); // Enable location selection
            mapResultLauncher.launch(intent); // Use the launcher
        });
    }

    public void updateSelectedLocation(String location) {
        // Update the relevant location field in the UI or the data object
        Log.d("BloodDonationSiteAdapter", "Updated location: " + location);
        locationInput.setText(location);
        // Example: Update a field or notify the dataset
        // You can trigger this to update the RecyclerView
    }


    private void showEditSiteDialog(BloodDonationSite site) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.edit_site_dialog, null);

        // set new site
        BloodDonationSite savedSite = new BloodDonationSite();
        savedSite.setBloodDonationSite(site);

        // Initialize fields
        EditText siteNameInput = dialogView.findViewById(R.id.edit_site_name_input);
        Spinner bloodGroupSpinner = dialogView.findViewById(R.id.edit_blood_group_spinner);
        EditText unitsInput = dialogView.findViewById(R.id.edit_units_input);
        EditText dateInput = dialogView.findViewById(R.id.edit_date_input);
        locationInput = dialogView.findViewById(R.id.edit_location_input);

        // Pre-fill data
        siteNameInput.setText(site.getName());
        // TODO: Set spinner value for blood group
        unitsInput.setText(String.valueOf(site.getUnits()));
        dateInput.setText(site.getDate());
        locationInput.setText(site.getLocation());

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // edit proccess
        onDateIconClicked(dialogView);
        onMapSelection(dialogView);


        // Set up buttons
        dialogView.findViewById(R.id.save_button).setOnClickListener(v -> {
            // Save changes
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("name", siteNameInput.getText().toString());
            updatedFields.put("location", locationInput.getText().toString());
            updatedFields.put("units", Double.parseDouble(unitsInput.getText().toString()));
            updatedFields.put("date", dateInput.getText().toString());

            donationSitesController.updateDonationSiteByModel(site, updatedFields, new DataFetchCallback<BloodDonationSite>() {
                @Override
                public void onSuccess(List<BloodDonationSite> data) {
                    if (data == null || data.isEmpty()) {
                        Log.e("BloodDonationSiteAdapter", "Received null or empty data in onSuccess");

                        dialog.dismiss();
                        return;
                    }

                    // Update the site in the local list
                    int index = sites.indexOf(site);
                    if (index != -1) {
                        sites.set(index, data.get(0));
                        notifyItemChanged(index); // Notify RecyclerView of the change
                    }

                    Toast.makeText(context, "Site updated successfully", Toast.LENGTH_SHORT).show();

                    // Explicitly refresh data in the activity
                    if (context instanceof FindSiteActivity) {
                        ((FindSiteActivity) context).fetchSites();
                    }

                    dialog.dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("Blood Donation Adapter", "Failed to update site" + e.getMessage());
                    Toast.makeText(context, "Failed to update site", Toast.LENGTH_SHORT).show();
                }
            });
        });


        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
        ImageView editSite = dialogView.findViewById(R.id.edit_site_info_icon);

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

        // edit icon
        editSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditSiteDialog(site);
            }
        });

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

    // Method to filter out sdoubleites owned by the current user
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
