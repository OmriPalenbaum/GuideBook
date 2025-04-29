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

// RecyclerView Adapter for development purposes (editing and activating boulders)
public class Dev_Adapter extends RecyclerView.Adapter<Dev_Adapter.BoulderViewHolder> {

    private List<Boulder> boulderList; // List of boulders to display
    private DatabaseHelper dbHelper;   // Database helper for updating entries

    // Constructor
    public Dev_Adapter(List<Boulder> boulderList) {
        this.boulderList = boulderList;
    }

    @NonNull
    @Override
    public BoulderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new BoulderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoulderViewHolder holder, int position) {
        Boulder currentBoulder = boulderList.get(position);

        // Set name, address, and rating in the item
        holder.nameTextView.setText(currentBoulder.getName());
        holder.addressTextView.setText(currentBoulder.getAddress() + "\uD83D\uDCCD"); // Add location pin emoji
        holder.ratingTextView.setText(currentBoulder.getRating() + "⭐");

        // Load and display image
        byte[] imageBytes = currentBoulder.getImageBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            // No image available — use default icon
            Log.d("Dev_Adapter", "No image data available for Boulder: " + currentBoulder.getName());
            holder.imageView.setImageResource(R.drawable.icon_empty_camera);
        }

        // Handle item clicks
        holder.itemView.setOnClickListener(v -> showOptionsDialog(v, position));
    }

    @Override
    public int getItemCount() {
        return boulderList.size();
    }

    // ViewHolder for Boulder item
    public static class BoulderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView;
        ImageView imageView;

        public BoulderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);
            addressTextView = itemView.findViewById(R.id.itemAddress);
            ratingTextView = itemView.findViewById(R.id.itemRating);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }

    // Show dialog to choose between editing or activating/deactivating the boulder
    private void showOptionsDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);
        dbHelper = new DatabaseHelper(view.getContext());

        CharSequence[] options = {"Edit Boulder Details", "Activate/Deactivate Boulder"};

        new AlertDialog.Builder(view.getContext())
                .setTitle("Choose Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditOptionsDialog(view, position); // Editing details
                    } else {
                        showConfirmationDialog(view, position); // Toggling activation
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Show dialog to choose which field to edit (name, address, or rating)
    private void showEditOptionsDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);
        CharSequence[] editOptions = {"Edit Name", "Edit Address", "Edit Rating"};

        new AlertDialog.Builder(view.getContext())
                .setTitle("Edit " + boulder.getName())
                .setItems(editOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditDialog(view, position, "name", boulder.getName());
                            break;
                        case 1:
                            showEditDialog(view, position, "address", boulder.getAddress());
                            break;
                        case 2:
                            showEditDialog(view, position, "rating", boulder.getRating());
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Show input dialog to edit a specific field
    private void showEditDialog(View view, int position, String field, String currentValue) {
        Boulder boulder = boulderList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit " + field.substring(0, 1).toUpperCase() + field.substring(1));

        final EditText input = new EditText(view.getContext()); // Input field
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentValue);
        builder.setView(input);

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

    // Update a field (name, address, or rating) both in memory and in the database
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

        notifyItemChanged(position); // Refresh the item in RecyclerView
    }

    // Show confirmation dialog to activate/deactivate a boulder
    private void showConfirmationDialog(View view, int position) {
        Boulder boulder = boulderList.get(position);

        new AlertDialog.Builder(view.getContext())
                .setTitle("Confirm Action")
                .setMessage("Are you sure you want to " + (boulder.getIsActive() ? "deactivate" : "activate") + " " + boulder.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean newStatus = !boulder.getIsActive();
                    boulder.setIsActive(newStatus);

                    // Update database accordingly
                    if (newStatus) {
                        dbHelper.updateBoulderStatus1(boulder.getName());
                        Log.d("Dev_Adapter", "Boulder " + boulder.getName() + " set to active");
                        Toast.makeText(view.getContext(), boulder.getName() + " is now active", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.updateBoulderStatus0(boulder.getName());
                        Log.d("Dev_Adapter", "Boulder " + boulder.getName() + " set to inActive");
                        Toast.makeText(view.getContext(), boulder.getName() + " is now inActive", Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(position); // Refresh after status change
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}