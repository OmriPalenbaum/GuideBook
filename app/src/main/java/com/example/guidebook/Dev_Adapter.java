package com.example.guidebook;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Dev_Adapter extends RecyclerView.Adapter<Dev_Adapter.BoulderViewHolder> {

    private List<Boulder> boulderList;
    private DatabaseHelper dbHelper;

    // Constructor
    public Dev_Adapter(List<Boulder> boulderList) {
        this.boulderList = boulderList;
    }

    @NonNull
    @Override
    public BoulderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false); // Uses the same layout as Item_Adapter
        return new BoulderViewHolder(view);
    }

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
            Log.d("Dev_Adapter", "No image data available for Boulder: " + currentBoulder.getName());
            holder.imageView.setImageResource(R.drawable.icon_empty_camera); // Default image if BLOB is null
        }

        // Set click listener to show options dialog
        holder.itemView.setOnClickListener(v -> showOptionsDialog(v, position));
    }

    @Override
    public int getItemCount() {
        return boulderList.size();
    }

    // ViewHolder class
    public static class BoulderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView;
        ImageView imageView;

        public BoulderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);       // Reference to Name TextView
            addressTextView = itemView.findViewById(R.id.itemAddress); // Reference to Address TextView
            ratingTextView = itemView.findViewById(R.id.itemRating);  // Reference to Rating TextView
            imageView = itemView.findViewById(R.id.itemImage);        // Reference to ImageView
        }
    }

    private void showOptionsDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);
        dbHelper = new DatabaseHelper(view.getContext());

        CharSequence[] options = {"Edit Boulder Details", "Activate/Deactivate Boulder"};

        new AlertDialog.Builder(view.getContext())
                .setTitle("Choose Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit Boulder Details
                        showEditOptionsDialog(view, position);
                    } else {
                        // Activate/Deactivate (old functionality)
                        showConfirmationDialog(view, position);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditOptionsDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);
        CharSequence[] editOptions = {"Edit Name", "Edit Address", "Edit Rating"};

        new AlertDialog.Builder(view.getContext())
                .setTitle("Edit " + boulder.getName())
                .setItems(editOptions, (dialog, which) -> {
                    // Handle edit option selection
                    switch (which) {
                        case 0: // Edit Name
                            showEditDialog(view, position, "name", boulder.getName());
                            break;
                        case 1: // Edit Address
                            showEditDialog(view, position, "address", boulder.getAddress());
                            break;
                        case 2: // Edit Rating
                            showEditDialog(view, position, "rating", boulder.getRating());
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditDialog(View view, int position, String field, String currentValue) {
        Boulder boulder = boulderList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit " + field.substring(0, 1).toUpperCase() + field.substring(1));

        // Set up the input
        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentValue);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                updateBoulderField(view, boulder, field, newValue, position);
            } else {
                Toast.makeText(view.getContext(), field + " cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateBoulderField(View view, Boulder boulder, String field, String newValue, int position) {
        switch (field) {
            case "name":
                String oldName = boulder.getName();
                boulder.setName(newValue);
                dbHelper.updateBoulderName(oldName, newValue);
                Log.d("Dev_Adapter", "Boulder name changed from " + oldName + " to " + newValue);
                break;
            case "address":
                boulder.setAddress(newValue);
                dbHelper.updateBoulderAddress(boulder.getName(), newValue);
                Log.d("Dev_Adapter", "Boulder address updated for " + boulder.getName());
                break;
            case "rating":
                boulder.setRating(newValue);
                dbHelper.updateBoulderRating(boulder.getName(), newValue);
                Log.d("Dev_Adapter", "Boulder rating updated for " + boulder.getName());
                break;
        }

        Toast.makeText(view.getContext(),
                field.substring(0, 1).toUpperCase() + field.substring(1) + " updated successfully",
                Toast.LENGTH_SHORT).show();

        notifyItemChanged(position);
    }

    private void showConfirmationDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);

        new AlertDialog.Builder(view.getContext())
                .setTitle("Confirm Action")
                .setMessage("Are you sure you want to " + (boulder.getIsActive() ? "deactivate" : "activate") + " " + boulder.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Toggle isActive status
                    boolean newStatus = !boulder.getIsActive();
                    boulder.setIsActive(newStatus);

                    // Update database
                    if (newStatus) {
                        dbHelper.updateBoulderStatus1(boulder.getName());
                        Log.d("Dev_Adapter", "Boulder " + boulder.getName() + " set to active");
                        Toast.makeText(view.getContext(), boulder.getName() + " is now active", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.updateBoulderStatus0(boulder.getName());
                        Log.d("Dev_Adapter", "Boulder " + boulder.getName() + " set to inActive");
                        Toast.makeText(view.getContext(), boulder.getName() + " is now inActive", Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(position);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}