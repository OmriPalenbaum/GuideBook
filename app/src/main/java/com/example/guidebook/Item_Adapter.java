package com.example.guidebook;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Item_Adapter extends RecyclerView.Adapter<Item_Adapter.BoulderViewHolder> {

    private List<Boulder> boulderList; // List of all Boulder objects to display

    // Constructor: receives the list of Boulders
    public Item_Adapter(List<Boulder> boulderList) {
        this.boulderList = boulderList;
    }

    @NonNull
    @Override
    public BoulderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each RecyclerView item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new BoulderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BoulderViewHolder holder, int position) {
        Boulder currentBoulder = boulderList.get(position); // Get the Boulder object for the current position

        // Set the Boulder name
        holder.nameTextView.setText(currentBoulder.getName());

        // Set the address with a location emoji 📍
        holder.addressTextView.setText(currentBoulder.getAddress() + "\uD83D\uDCCD");

        // Set the rating with a star emoji ⭐
        holder.ratingTextView.setText(currentBoulder.getRating() + "⭐");

        // Convert image bytes to a Bitmap and display it
        byte[] imageBytes = currentBoulder.getImageBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageView.setImageBitmap(bitmap); // Set the image if available
        } else {
            // Log the issue and display a default placeholder image
            Log.d("Item_Adapter", "No image data available for Boulder: " + currentBoulder.getName());
            holder.imageView.setImageResource(R.drawable.icon_empty_camera);
        }

        // Show or hide the "done" icon depending on the Boulder status
        if (currentBoulder.getIsDone()) {
            holder.doneIcon.setVisibility(View.VISIBLE);
        } else {
            holder.doneIcon.setVisibility(View.GONE);
        }

        // Handle click events on each item
        holder.itemView.setOnClickListener(v -> {
            Boulder boulder = boulderList.get(position); // Get the clicked Boulder

            // Create a new instance of BoulderFragment, passing the Boulder details
            BoulderFragment fragment = BoulderFragment.newInstance(
                    boulder.getName(),
                    boulder.getAddress(),
                    boulder.getRating(),
                    boulder.getIsDone(),
                    boulder.getImageBytes()
            );

            // Set a listener to update the Boulder status after changes inside the fragment
            fragment.setOnBoulderUpdatedListener((boulderName, isDone) -> {
                for (Boulder b : boulderList) {
                    if (b.getName().equals(boulderName)) {
                        b.setIsDone(isDone); // Update the Boulder status
                        break;
                    }
                }
                this.notifyDataSetChanged(); // Refresh the list after update
            });

            // Start a fragment transaction to display the BoulderFragment
            FragmentTransaction transaction = ((AppCompatActivity) v.getContext())
                    .getSupportFragmentManager()
                    .beginTransaction();

            // Add the fragment to the screen
            transaction.add(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null); // Allow user to navigate back
            transaction.commit(); // Commit the transaction

            // Hide the RecyclerView, Spinner, and Checkbox while the fragment is open
            RecyclerView recyclerView = ((AppCompatActivity) v.getContext()).findViewById(R.id.recyclerView);
            Spinner spinner = ((AppCompatActivity) v.getContext()).findViewById(R.id.spinnerSort);
            CheckBox checkBox = ((AppCompatActivity) v.getContext()).findViewById(R.id.checkboxShowDone);

            if (recyclerView != null && spinner != null && checkBox != null) {
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }

            // Show the FrameLayout that will hold the fragment
            FrameLayout frameLayout = ((AppCompatActivity) v.getContext()).findViewById(R.id.fragmentContainer);
            if (frameLayout != null) {
                frameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boulderList.size(); // Return the total number of items to be displayed
    }

    // ViewHolder class to hold references to the views inside each item
    public static class BoulderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView;
        ImageView imageView, doneIcon;

        public BoulderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);       // Reference to the Boulder name TextView
            addressTextView = itemView.findViewById(R.id.itemAddress); // Reference to the address TextView
            ratingTextView = itemView.findViewById(R.id.itemRating);   // Reference to the rating TextView
            imageView = itemView.findViewById(R.id.itemImage);         // Reference to the main image of the Boulder
            doneIcon = itemView.findViewById(R.id.itemDoneIcon);       // Reference to the "done" checkmark icon
        }
    }

    // Method to update the current list of Boulders and refresh the RecyclerView
    public void updateList(List<Boulder> newList) {
        boulderList.clear();
        boulderList.addAll(newList);
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }
}