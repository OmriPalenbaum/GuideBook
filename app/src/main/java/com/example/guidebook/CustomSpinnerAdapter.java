package com.example.guidebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] options;
    private final int[] icons;

    public CustomSpinnerAdapter(Context context, String[] options, int[] icons) {
        super(context, 0, options);
        this.context = context;
        this.options = options;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This is the view that shows when the spinner is closed
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_closed, parent, false);
        }

        TextView text = convertView.findViewById(R.id.spinnerText);
        ImageView icon = convertView.findViewById(R.id.spinnerIcon);

        text.setText(options[position]);
        icon.setImageResource(icons[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // This is the view that shows in the dropdown list
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_open, parent, false);
        }

        TextView text = convertView.findViewById(R.id.spinnerText);
        ImageView icon = convertView.findViewById(R.id.spinnerIcon);

        text.setText(options[position]);
        icon.setImageResource(icons[position]);

        return convertView;
    }
}
