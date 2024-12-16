package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.DataFetchCallback;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.services.DonationSiteService;

import java.util.List;
import java.util.Map;

public class DonationSitesController {
    DonationSiteService donationSiteService;

    public DonationSitesController() {
        this.donationSiteService = new DonationSiteService();
    }

    public void addSites(String siteId, BloodDonationSite bloodDonationSite) {
        this.donationSiteService.addDonationSiteWithCustomID(siteId, bloodDonationSite);
    }

    public void findAllSites(DataFetchCallback<BloodDonationSite> callback) {
        this.donationSiteService.findAllSites(callback);
    }

    public void getSiteUiD(BloodDonationSite bloodDonationSite, DataFetchCallback<String> callback) {
        this.donationSiteService.getSiteIdByModel(bloodDonationSite,callback);
    }

    public  void updateDonationSite(String siteId, String fieldName, Object value) {
        this.donationSiteService.updateField(siteId, fieldName, value);
    }
}
