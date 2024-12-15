package com.example.blooddonate.services;

import com.example.blooddonate.models.BloodDonationSite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchEngineService {
    private Map<String, BloodDonationSite> bloodDonationSiteMap = new HashMap<>();

    public SearchEngineService() {}

    public List<BloodDonationSite> searchDonationSites(List<BloodDonationSite> sites, String letter) {
        List<BloodDonationSite> filteredSites = new ArrayList<>();
        if (letter.isEmpty()) {
            // Show all sites if input is empty
            filteredSites.addAll(sites);
        } else {
            // Filter by first letter of the name
            for (BloodDonationSite site : sites) {
                if (site.getName().toLowerCase().startsWith(letter.toLowerCase())) {
                    filteredSites.add(site);
                }
            }
        }
        return  filteredSites;
    }

}
