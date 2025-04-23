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

    private List<Boulder> boulderList;

    // Constructor
    public Item_Adapter(List<Boulder> boulderList) {
        this.boulderList = boulderList;
    }

    @NonNull
    @Override
    public BoulderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false); // Use the XML created earlier
        return new BoulderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BoulderViewHolder holder, int position) {
        Boulder currentBoulder = boulderList.get(position);

        // Set name, address, and rating
        holder.nameTextView.setText(currentBoulder.getName());
        holder.addressTextView.setText(currentBoulder.getAddress() + "\uD83D\uDCCD");
        holder.ratingTextView.setText(currentBoulder.getRating() + "⭐");

        // Convert byte[] to Bitmap and set it to ImageView
        byte[] imageBytes = currentBoulder.getImageBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            // Log the issue and set default image
            Log.d("Item_Adapter", "No image data available for Boulder: " + currentBoulder.getName());
            holder.imageView.setImageResource(R.drawable.icon_empty_camera); // Default image if BLOB is null
        }

        // Set icon visibility based on whether the Boulder is completed or not
        if (currentBoulder.getIsDone()) {
            holder.doneIcon.setVisibility(View.VISIBLE); // Show the icon if the boulder is completed
        } else {
            holder.doneIcon.setVisibility(View.GONE); // Hide the icon if not completed
        }

        // Set the click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            Boulder boulder = boulderList.get(position);

            // Create an instance of the fragment with Boulder details
            BoulderFragment fragment = BoulderFragment.newInstance(
                    boulder.getName(),
                    boulder.getAddress(),
                    boulder.getRating(),
                    boulder.getIsDone(),
                    boulder.getImageBytes()
            );

            fragment.setOnBoulderUpdatedListener((boulderName, isDone) -> {
                for (Boulder b : boulderList) {
                    if (b.getName().equals(boulderName)) {
                        b.setIsDone(isDone);
                        break;
                    }
                }
                this.notifyDataSetChanged();
            });

            // Begin the fragment transaction
            FragmentTransaction transaction = ((AppCompatActivity) v.getContext())
                    .getSupportFragmentManager()
                    .beginTransaction();

            // Add the fragment (instead of replace) if you want to stack it on top of the RecyclerView
            transaction.add(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null); // Optional: allows back navigation

            // Commit the transaction to make the fragment appear
            transaction.commit();

            // Get the RecyclerView reference from the parent activity or fragment
            RecyclerView recyclerView = ((AppCompatActivity) v.getContext()).findViewById(R.id.recyclerView); // Ensure this matches your RecyclerView's ID
            Spinner spinner = ((AppCompatActivity) v.getContext()).findViewById(R.id.spinnerSort);
            CheckBox checkBox = ((AppCompatActivity) v.getContext()).findViewById(R.id.checkboxShowDone);
            // Set the RecyclerView visibility to GONE
            if (recyclerView != null && spinner != null && checkBox != null) {
                recyclerView.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }
            // Get the FrameLayout reference
            FrameLayout frameLayout = ((AppCompatActivity) v.getContext()).findViewById(R.id.fragmentContainer); // Ensure this matches your FrameLayout's ID
            // Set the FrameLayout visibility to VISIBLE
            if (frameLayout != null) {
                frameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boulderList.size();
    }

    // ViewHolder class
    public static class BoulderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView;
        ImageView imageView, doneIcon;

        public BoulderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);       // Reference to Name TextView
            addressTextView = itemView.findViewById(R.id.itemAddress); // Reference to Address TextView
            ratingTextView = itemView.findViewById(R.id.itemRating);  // Reference to Rating TextView
            imageView = itemView.findViewById(R.id.itemImage);        // Reference to ImageView
            doneIcon = itemView.findViewById(R.id.itemDoneIcon);      // Reference to DoneIcon
        }
    }
    public void updateList(List<Boulder> newList) {
        boulderList.clear();
        boulderList.addAll(newList);
        notifyDataSetChanged();
    }
}