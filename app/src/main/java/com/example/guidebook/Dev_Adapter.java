package com.example.guidebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Dev_Adapter extends RecyclerView.Adapter<Dev_Adapter.BoulderViewHolder> {

    private List<Boulder> boulderList;
    private DatabaseHelper dbHelper;
    private Dev_Adapter.OnItemClickListener listener; // Listener for item clicks

    // Constructor
    public Dev_Adapter(List<Boulder> boulderList, Dev_Adapter.OnItemClickListener listener) {
        this.boulderList = boulderList;
        this.listener = listener;
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
            holder.imageView.setImageResource(R.drawable.empty_camera); // Default image if BLOB is null
        }

        // Set the click listener on the itemView to set the Boulder Is_Active to true
        holder.itemView.setOnClickListener(v -> {
            Boulder boulder = boulderList.get(position);
            dbHelper = new DatabaseHelper(v.getContext());
            boulder.setIsActive(true); // Set Is_Active to true

            // Update the boulder's is_active status in the database
            dbHelper.updateBoulderStatus(boulder.getName());

            // Log the update
            Log.d("Dev_Adapter", "Boulder " + boulder.getName() + " set to active");

            // shows a toast message
            Toast.makeText(v.getContext(), boulder.getName() + " is now active", Toast.LENGTH_SHORT).show();
            this.notifyDataSetChanged();
        });
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
    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Boulder boulder, int position);

    }
}
