package com.example.blooddonate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonate.R;
import com.example.blooddonate.models.User;

import java.util.List;

public class DonorTableAdapter extends RecyclerView.Adapter<DonorTableAdapter.ViewHolder> {

    private final List<User> donors;
    private final Context context;

    public DonorTableAdapter(Context context, List<User> donors) {
        this.context = context;
        this.donors = donors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donor_table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User donor = donors.get(position);
        holder.name.setText(donor.getName());
        holder.phoneNumber.setText(donor.getPhoneNumber());
        holder.email.setText(donor.getEmail());
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name);
            phoneNumber = itemView.findViewById(R.id.row_phone_number);
            email = itemView.findViewById(R.id.row_email);
        }
    }
}
