package com.example.guidebook;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BoulderFragment extends Fragment {

    private String name;
    private String address;
    private String rating;
    private byte[] imageBytes; // Byte array for the image

    // Static factory method to create a new instance of the fragment with arguments
    public static BoulderFragment newInstance(String name, String address, String rating, byte[] imageBytes) {
        BoulderFragment fragment = new BoulderFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("address", address);
        args.putString("rating", rating);
        args.putByteArray("imageBytes", imageBytes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            rating = getArguments().getString("rating");
            imageBytes = getArguments().getByteArray("imageBytes");
        }
        Log.d("BoulderFragment", "Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boulder, container, false);

        Log.d("BoulderFragment", "Fragment onCreateView");

        // Initialize UI components and set values
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewRating = view.findViewById(R.id.textViewRating);
        ImageView imageViewBoulder = view.findViewById(R.id.imageViewBoulder);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        textViewName.setText(name);
        textViewAddress.setText("Address: " + address);
        textViewRating.setText("Rating: " + rating);

        // Convert byte array to Bitmap and set it to the ImageView
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageViewBoulder.setImageBitmap(bitmap);
        } else {
            imageViewBoulder.setImageResource(R.drawable.no_image_icon); // Fallback image
        }

        buttonBack.setOnClickListener(v -> {
            // Get the RecyclerView reference from the parent activity
            RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
            // Set the RecyclerView visibility to VISIBLE
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            // Get the FrameLayout reference
            FrameLayout frameLayout = getActivity().findViewById(R.id.fragmentContainer);
            // Set the FrameLayout visibility to GONE
            if (frameLayout != null) {
                frameLayout.setVisibility(View.GONE);
            }

            // Pop the fragment from the back stack
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}