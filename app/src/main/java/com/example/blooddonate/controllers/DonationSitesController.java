package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.services.DonationSiteService;

import java.util.List;

public class DonationSitesController {
    DonationSiteService donationSiteService;

    public DonationSitesController() {
        this.donationSiteService = new DonationSiteService();
    }

    public void addSites(BloodDonationSite bloodDonationSite) {
        this.donationSiteService.addDonationSite(bloodDonationSite);
    }

    public void findAllSites(DataFetchCallback<BloodDonationSite> callback) {
        this.donationSiteService.findAllSites(callback);
    }
}
