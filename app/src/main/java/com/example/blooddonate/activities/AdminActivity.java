package com.example.blooddonate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.MainActivity;
import com.example.blooddonate.R;
import com.example.blooddonate.adapters.DonationSiteTableAdapter;
import com.example.blooddonate.adapters.DonorTableAdapter;
import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.AdminController;
import com.example.blooddonate.controllers.DonationSitesController;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationSiteTableAdapter siteAdapter;
    private DonorTableAdapter donorAdapter;

    private List<BloodDonationSite> sites = new ArrayList<>(); // Initialize the list
    private List<BloodDonationSite> currentPageData = new ArrayList<>();
    private List<User> donors = new ArrayList<>();

    private int donorCurrentPage = 0; // Track the current page for donors
    private static final int DONORS_ROWS_PER_PAGE = 5; // Number of rows per page for donors
    private List<User> donorCurrentPageData = new ArrayList<>(); // Data for the current donor page

    private int currentPage = 0;
    private static final int ROWS_PER_PAGE = 5;

    private boolean showingSites = true; // Track which table is being displayed

    private DonationSitesController donationSitesController;
    private UserController userController;

    private AdminController adminController;

    private ImageView listDonorImage, listSitesImage;

    private TextView tableIndicator, userName;

    ImageView menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        donationSitesController = new DonationSitesController();
        userController = new UserController();
        adminController = new AdminController();

        listDonorImage = findViewById(R.id.donors_info_img);
        listSitesImage = findViewById(R.id.number_sites_id);
        tableIndicator = findViewById(R.id.table_indicator);
        userName = findViewById(R.id.userName);
        menuId = findViewById(R.id.menuId);


        recyclerView = findViewById(R.id.sites_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button previousButton = findViewById(R.id.previous_page_button);
        Button nextButton = findViewById(R.id.next_page_button);

        previousButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateSitesRecyclerView();
            }
        });

        nextButton.setOnClickListener(v -> {
            if ((currentPage + 1) * ROWS_PER_PAGE < sites.size()) {
                currentPage++;
                updateSitesRecyclerView();
            }
        });

        setupPaginationButtons();

        onListDonorsClicked();
        onListSitesClicked();
        getAdminData();

        getSitesFromServer(); // Fetch sites on startup
        getDonorsFromServer(); // Fetch donors in the background
        onMenuClicked();
    }

    private void onMenuClicked() {
        menuId.setOnClickListener(v -> {
            // Create a PopupMenu
            PopupMenu popupMenu = new PopupMenu(AdminActivity.this, menuId);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    Log.d("AdminActivity", "Edit clicked");
                    // Handle edit action
                    return true;

                } else if (item.getItemId() == R.id.menu_logout) {
                    Log.d("AdminActivity", "Logout clicked");
                    handleLogout();
                    return true;

                } else if (item.getItemId() == R.id.menu_settings) {
                    Log.d("AdminActivity", "Settings clicked");
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });
    }


    private void handleLogout() {
        // Handle logout logic here (e.g., clear session, navigate to login screen)
        userController.signOut();
        // Clear the activity stack and navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack
        startActivity(intent);
        finish(); // Finish current activity
        Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
    }

    private void getAdminData() {
        // display name
        adminController.getCurrentAdmin(new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                userName.setText(user.getName());
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });

    }

    private void updateSitesRecyclerView() {
        if (sites == null || sites.isEmpty()) {
            return; // Avoid NullPointerException or trying to update empty data
        }

        int start = currentPage * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, sites.size()); // Limit rows per page to 5
        currentPageData.clear();
        currentPageData.addAll(sites.subList(start, end)); // Only add rows within this range

        if (siteAdapter == null) {
            siteAdapter = new DonationSiteTableAdapter(this, currentPageData);
            recyclerView.setAdapter(siteAdapter);
        } else {
            siteAdapter.notifyDataSetChanged(); // Notify adapter of data changes
        }
    }

    private void getSitesFromServer() {
        donationSitesController.findAllSites(new DataFetchCallback<BloodDonationSite>() {
            @Override
            public void onSuccess(List<BloodDonationSite> data) {
                if (data != null) {
                    sites.clear();
                    sites.addAll(data);
                    runOnUiThread(AdminActivity.this::updateSitesRecyclerView); // Ensure UI updates on the main thread
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Admin Activity", "Error fetching sites: " + e.getMessage());
            }
        });
    }

    private void getDonorsFromServer() {
        userController.getAllUsers(new DataFetchCallback<User>() {
            @Override
            public void onSuccess(List<User> data) {
                if (data != null) {
                    donors.clear();
                    donors.addAll(data);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("AdminActivity", "Error fetching donors: " + e.getMessage());
            }
        });
    }

    // Update this method to handle donor pagination
    private void updateDonorsRecyclerView() {
        if (donors == null || donors.isEmpty()) {
            return; // Avoid NullPointerException or trying to update empty data
        }

        int start = donorCurrentPage * DONORS_ROWS_PER_PAGE;
        int end = Math.min(start + DONORS_ROWS_PER_PAGE, donors.size()); // Limit rows per page to 5
        donorCurrentPageData.clear();
        donorCurrentPageData.addAll(donors.subList(start, end)); // Only add rows within this range

        if (donorAdapter == null) {
            donorAdapter = new DonorTableAdapter(this, donorCurrentPageData);
            recyclerView.setAdapter(donorAdapter);
        } else {
            recyclerView.setAdapter(donorAdapter); // Ensure the correct adapter is set
            donorAdapter.notifyDataSetChanged(); // Notify adapter of data changes
        }
    }



    // Modify button click listeners to handle donor pagination
    private void setupPaginationButtons() {
        Button previousButton = findViewById(R.id.previous_page_button);
        Button nextButton = findViewById(R.id.next_page_button);

        previousButton.setOnClickListener(v -> {
            if (showingSites) {
                if (currentPage > 0) {
                    currentPage--;
                    updateSitesRecyclerView();
                }
            } else {
                if (donorCurrentPage > 0) {
                    donorCurrentPage--;
                    updateDonorsRecyclerView();
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            if (showingSites) {
                if ((currentPage + 1) * ROWS_PER_PAGE < sites.size()) {
                    currentPage++;
                    updateSitesRecyclerView();
                }
            } else {
                if ((donorCurrentPage + 1) * DONORS_ROWS_PER_PAGE < donors.size()) {
                    donorCurrentPage++;
                    updateDonorsRecyclerView();
                }
            }
        });
    }


    private void onListDonorsClicked() {
        listDonorImage.setOnClickListener(v -> {
            if (!showingSites) return; // Already showing donors, no action needed
            showingSites = false; // Switch to showing donors

            Log.d("Admin Activity", "Switching to donor table");
            tableIndicator.setText("Donors table");

            // Show donors header and hide sites header
            findViewById(R.id.donors_header).setVisibility(View.VISIBLE);
            findViewById(R.id.sites_header).setVisibility(View.GONE);

            donorCurrentPage = 0; // Reset donor pagination to the first page

            // Fetch updated donor data from the server
            userController.getAllUsers(new DataFetchCallback<User>() {
                @Override
                public void onSuccess(List<User> data) {
                    if (data != null) {
                        donors.clear();
                        donors.addAll(data);
                        donorCurrentPageData.clear();
                        int start = donorCurrentPage * DONORS_ROWS_PER_PAGE;
                        int end = Math.min(start + DONORS_ROWS_PER_PAGE, donors.size());
                        donorCurrentPageData.addAll(donors.subList(start, end));

                        // Explicitly set the donor adapter
                        donorAdapter = new DonorTableAdapter(AdminActivity.this, donorCurrentPageData);
                        recyclerView.setAdapter(donorAdapter);
                        donorAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("AdminActivity", "Error fetching donors: " + e.getMessage());
                }
            });
        });
    }



    private void onListSitesClicked() {
        listSitesImage.setOnClickListener(v -> {
            if (showingSites) return; // Already showing sites, no action needed

            showingSites = true; // Switch to showing sites
            Log.d("Admin Activity", "Switching to site table");
            tableIndicator.setText("Blood Donation Sites table");

            // Show sites header and hide donors header
            findViewById(R.id.sites_header).setVisibility(View.VISIBLE);
            findViewById(R.id.donors_header).setVisibility(View.GONE);

            currentPage = 0; // Reset site pagination to the first page

            currentPageData.clear();
            int start = currentPage * ROWS_PER_PAGE;
            int end = Math.min(start + ROWS_PER_PAGE, sites.size());
            currentPageData.addAll(sites.subList(start, end));

            // Explicitly set the site adapter
            siteAdapter = new DonationSiteTableAdapter(this, currentPageData);
            recyclerView.setAdapter(siteAdapter);
            siteAdapter.notifyDataSetChanged();
        });
    }




}

