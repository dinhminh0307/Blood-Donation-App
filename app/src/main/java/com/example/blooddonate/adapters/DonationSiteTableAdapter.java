package com.example.blooddonate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.callbacks.GetUserCallback;
import com.example.blooddonate.controllers.UserController;
import com.example.blooddonate.models.BloodDonationSite;
import com.example.blooddonate.models.User;

import java.util.List;

public class DonationSiteTableAdapter extends RecyclerView.Adapter<DonationSiteTableAdapter.ViewHolder> {

    private final List<BloodDonationSite> sites;
    private final Context context;

    private UserController userController;

    public DonationSiteTableAdapter(Context context, List<BloodDonationSite> sites) {
        this.context = context;
        this.sites = sites;
        this.userController = new UserController();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.site_table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodDonationSite site = sites.get(position);
        holder.name.setText(site.getName());
        userController.getUserByUID(site.getOwner(), new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                holder.owner.setText(user.getName());
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });


        holder.date.setText(site.getDate());
        holder.bloodTypes.setText(site.getBloodTypes());
        holder.registers.setText(String.valueOf(site.getRegisters().size()));
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, owner, date, bloodTypes, registers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name);
            owner = itemView.findViewById(R.id.row_owner);
            date = itemView.findViewById(R.id.row_date);
            bloodTypes = itemView.findViewById(R.id.row_blood_types);
            registers = itemView.findViewById(R.id.row_registers);
        }
    }
}
