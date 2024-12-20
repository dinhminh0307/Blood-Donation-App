package com.example.blooddonate.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.blooddonate.R;
import com.example.blooddonate.models.BloodDonationSite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {

    private String selectedCity = "";
    private String selectedBloodType = "";
    private String selectedDate = "";

    private FilterDialogListener listener;

    private List<BloodDonationSite> listBloodDonationSite = new ArrayList<>();

    public FilterDialogFragment(List<BloodDonationSite> listBloodDonationSite) {
        this.listBloodDonationSite = listBloodDonationSite;
    }

    public interface FilterDialogListener {
        void onFilterApplied(String city, String bloodType, String date);
    }

    public void setFilterDialogListener(FilterDialogListener listener) {
        this.listener = listener;
    }

    // Method to set previous filter selections
    public void setPreviousFilters(String city, String bloodType, String date) {
        this.selectedCity = city;
        this.selectedBloodType = bloodType;
        this.selectedDate = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);

        // City EditText
        EditText cityEditText = view.findViewById(R.id.city_edit_text);
        cityEditText.setText(selectedCity); // Set the previously selected city

        // Blood Type Spinner
        Spinner bloodTypeSpinner = view.findViewById(R.id.blood_type_spinner);
        ArrayAdapter<CharSequence> bloodTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_types_array, android.R.layout.simple_spinner_item);
        bloodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypeAdapter);

        // Set the previously selected blood type
        if (!selectedBloodType.isEmpty()) {
            int spinnerPosition = bloodTypeAdapter.getPosition(selectedBloodType);
            bloodTypeSpinner.setSelection(spinnerPosition);
        }

        // Date Picker Button
        Button dateButton = view.findViewById(R.id.date_button);
        dateButton.setText(selectedDate.isEmpty() ? "Pick a Date" : selectedDate); // Set the previously selected date
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateButton.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Apply Button
        Button applyButton = view.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(v -> {
            selectedCity = cityEditText.getText().toString().trim();
            selectedBloodType = bloodTypeSpinner.getSelectedItem().toString();

            if (listener != null) {
                listener.onFilterApplied(selectedCity, selectedBloodType, selectedDate);
            }
            dismiss();
        });

        // Cancel Button
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        // Clear Filter Button
        Button clearFilterButton = view.findViewById(R.id.clear_filter_button);
        clearFilterButton.setOnClickListener(v -> {
            // Reset all filter selections
            cityEditText.setText("");
            bloodTypeSpinner.setSelection(0);
            selectedDate = "";
            dateButton.setText("Pick a Date");

            // Notify listener to reset the adapter to original sites
            if (listener != null) {
                listener.onFilterApplied("", "", "");
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set dialog to fill larger portion of the screen
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
