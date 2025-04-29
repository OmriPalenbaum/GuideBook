package com.example.guidebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

// Custom adapter for displaying a spinner with both text and icons
public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] options; // Array of text options for the spinner
    private final int[] icons; // Array of icon resource IDs corresponding to the options

    // Constructor to initialize the adapter with context, options, and icons
    public CustomSpinnerAdapter(Context context, String[] options, int[] icons) {
        super(context, 0, options);
        this.context = context;
        this.options = options;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Returns the view displayed in the spinner when it is closed
        if (convertView == null) {
            // Inflate the custom layout for the closed spinner
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_closed, parent, false);
        }

        // Find and set the text and icon for the current spinner item
        TextView text = convertView.findViewById(R.id.spinnerText);
        ImageView icon = convertView.findViewById(R.id.spinnerIcon);

        text.setText(options[position]);
        icon.setImageResource(icons[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Returns the view displayed for each item in the dropdown list
        if (convertView == null) {
            // Inflate the custom layout for the dropdown items
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_open, parent, false);
        }

        // Find and set the text and icon for the dropdown item
        TextView text = convertView.findViewById(R.id.spinnerText);
        ImageView icon = convertView.findViewById(R.id.spinnerIcon);

        text.setText(options[position]);
        icon.setImageResource(icons[position]);

        return convertView;
    }
}