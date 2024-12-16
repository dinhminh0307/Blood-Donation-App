package com.example.blooddonate.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.activities.RequestActivity;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.List;

public class SiteCardAdapter extends RecyclerView.Adapter<SiteCardAdapter.ViewHolder> {
    private List<BloodDonationSite> sites;
    private OnSiteCardClickListener listener;
    private int highlightedPosition = -1;

    private Context context;

    public SiteCardAdapter(Context context, List<BloodDonationSite> sites, OnSiteCardClickListener listener) {
        this.sites = sites;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.site_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BloodDonationSite site = sites.get(position);
        holder.siteName.setText(site.getName());
        holder.siteLocation.setText(site.getLocation());

        // Dynamically determine if this item is highlighted
        holder.itemView.setBackgroundColor(holder.getAdapterPosition() == highlightedPosition ? Color.LTGRAY : Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                listener.onCardClick(clickedPosition);
                updateHighlightedItem(clickedPosition);
            }
        });

        holder.requestSiteButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestActivity.class);
            intent.putExtra("site_name", site.getName());
            intent.putExtra("site_location", site.getLocation());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    // Dynamically update highlighted item
    public void updateHighlightedItem(int position) {
        int previous = highlightedPosition;
        highlightedPosition = position;

        // Notify only the affected items
        if (previous != RecyclerView.NO_POSITION) {
            notifyItemChanged(previous);
        }
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteName, siteLocation;
        public Button requestSiteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.site_name);
            siteLocation = itemView.findViewById(R.id.site_location);
            requestSiteButton = itemView.findViewById(R.id.site_request_button);
        }
    }

    public interface OnSiteCardClickListener {
        void onCardClick(int position);
    }
}

