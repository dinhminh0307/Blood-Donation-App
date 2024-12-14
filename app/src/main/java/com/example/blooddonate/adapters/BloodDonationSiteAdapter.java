package com.example.blooddonate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.List;

public class BloodDonationSiteAdapter extends RecyclerView.Adapter<BloodDonationSiteAdapter.ViewHolder> {
    private List<BloodDonationSite> sites;
    private Context context;

    public BloodDonationSiteAdapter(Context context, List<BloodDonationSite> sites) {
        this.context = context;
        this.sites = sites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.site_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BloodDonationSite site = sites.get(position);

        if (site != null) {
            holder.bloodType.setText(site.getBloodTypes() != null ? site.getBloodTypes() : "N/A");
            holder.bloodUnits.setText(site.getUnits() > 0 ? site.getUnits() + " Unit" : "No Units");
            holder.siteName.setText(site.getName() != null ? site.getName() : "Unknown Site");
            holder.siteLocation.setText(site.getLocation() != null ? site.getLocation() : "Unknown Location");
        } else {
            Log.e("Adapter", "Site data is null at position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return sites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bloodType, bloodUnits, siteName, siteLocation, siteStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            bloodType = itemView.findViewById(R.id.blood_type);
            bloodUnits = itemView.findViewById(R.id.blood_units);
            siteName = itemView.findViewById(R.id.site_name);
            siteLocation = itemView.findViewById(R.id.site_location);
            siteStatus = itemView.findViewById(R.id.site_status);
        }
    }
}

