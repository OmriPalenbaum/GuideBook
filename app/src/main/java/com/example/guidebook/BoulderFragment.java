package com.example.guidebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class BoulderFragment extends Fragment {

    private static final String APIKEY = "20deb2d30fc03075f6b90c285858e376";
    private static final String UNITS = "metric";
    private String name;
    private String address;
    private String rating;
    private boolean isDone;
    private byte[] imageBytes; // Byte array for the image
    private OnBoulderUpdatedListener listener;
    public void setOnBoulderUpdatedListener(OnBoulderUpdatedListener listener) {
        this.listener = listener;
    }

    // Static factory method to create a new instance of the fragment with arguments
    public static BoulderFragment newInstance(String name, String address, String rating, boolean isDone, byte[] imageBytes) {
        BoulderFragment fragment = new BoulderFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("address", address);
        args.putString("rating", rating);
        args.putBoolean("isDone", isDone);
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
            isDone = getArguments().getBoolean("isDone", false);
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
        ImageButton imageButtonNav = view.findViewById(R.id.imageButtonNav);
        TextView textViewWeather1 = view.findViewById(R.id.textViewWeather1);
        TextView textViewWeather2 = view.findViewById(R.id.textViewWeather2);
        ImageView imageViewWeather = view.findViewById(R.id.imageViewWeather);
        ImageView imageBoulder = view.findViewById(R.id.imageViewBoulder);
        ImageButton icon = view.findViewById(R.id.buttonClimberDone);
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        TextView textViewClimbedHint = view.findViewById(R.id.textViewClimbedHint);

        textViewName.setText(name);
        textViewAddress.setText("Address: " + address);
        textViewRating.setText("Rating: " + rating);

        // Convert byte array to Bitmap and set it to the ImageButton
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageBoulder.setImageBitmap(bitmap);
        } else {
            imageBoulder.setImageResource(R.drawable.icon_no_image); // Fallback image
        }

        // Set the icon with transparency based on the initial state of isDone
        setBoulderIcon(icon, isDone);

        // Set the text based on the initial state of isDone
        textViewClimbedHint.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

        // Set onClickListener for the icon to toggle isDone state
        icon.setOnClickListener(v -> {
            isDone = !isDone;
            setBoulderIcon(icon, isDone);

            // Update database
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            dbHelper.updateIsDone(name, isDone);

            // Update text visibility
            textViewClimbedHint.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

            // Notify listener
            if (listener != null) {
                listener.onBoulderUpdated(name, isDone);
            }
        });


        //Sets the listener om the imageButtonNav
        imageButtonNav.setOnClickListener(v -> openGoogleMapsNavigation(address));

        //Create an instance of the WeatherApiService
        WeatherApiService weatherApi = RetrofitClient.getClient().create(WeatherApiService.class);

        //Call the getWeather method to fetch weather data
        weatherApi.getWeather(address, APIKEY, UNITS).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                // Check if the response is successful and contains data
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the weather data from the response
                    WeatherResponse weather = response.body();

                    String weatherInfo = "Temperature: " + weather.getMain().getTemp() + "°C";
                    textViewWeather1.setText(weatherInfo);

                    weatherInfo =  "Humidity: " + weather.getMain().getHumidity() + "%\n" +
                            "Description: " + weather.getWeather().get(0).getDescription();
                    textViewWeather2.setText(weatherInfo);

                    // Set the weather image to clear skies by default. If the temperature is less than 15°C, change the weather image to rainy
                    imageViewWeather.setImageResource(R.drawable.icon_clear_skies);
                    if (weather.getMain().getTemp() < 15) {
                        imageViewWeather.setImageResource(R.drawable.icon_rainy);
                    }
                } else {
                    // If the response is not successful or contains no data, display an error message in textViewWeather1
                    textViewWeather1.setText("address wasn't found");
                }
            }

            //Handles failure
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewWeather1.setText("Error: " + t.getMessage());
            }
        });

        //Sets listener on buttonBack
        buttonBack.setOnClickListener(v -> {
            // Get the RecyclerView reference from the parent activity
            RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
            Spinner spinner = ((AppCompatActivity) v.getContext()).findViewById(R.id.spinnerSort);
            CheckBox checkBox = ((AppCompatActivity) v.getContext()).findViewById(R.id.checkboxShowDone);
            // Set the RecyclerView visibility to VISIBLE
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
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

    // Method to set the boulder icon based on whether it's marked as "done"
    private void setBoulderIcon(ImageButton imageButton, boolean isDone) {
        if (isDone) {
            imageButton.setAlpha(1f); // Set the icon to fully visible (color full)
        } else {
            imageButton.setAlpha(0.5f); // Set the icon to 50% transparent
        }
    }

    //Method to call the Google Maps intent
    private void openGoogleMapsNavigation(String locationAddress) {
        // Encode the location name and Creates a URI for Google Maps navigation using the encoded destination
        String destination = Uri.encode(locationAddress + " עיר, Israel");
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);

        // Create an intent to open Google Maps with the navigation URI
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if Google Maps is installed. if it is, start navigation, otherwise show an error message
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps app not installed!", Toast.LENGTH_SHORT).show();
        }
    }

    //Interface for the listener
    public interface OnBoulderUpdatedListener {
        void onBoulderUpdated(String boulderName, boolean isDone);
    }

}