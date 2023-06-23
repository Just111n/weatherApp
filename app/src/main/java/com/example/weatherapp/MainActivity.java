package com.example.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TextView locationTextView;

    private WeatherModel weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);
        TextView maxTempTextView = findViewById(R.id.maxTempTextView);
        TextView minTempTextView = findViewById(R.id.minTempTextView);
        TextView tempTextView = findViewById(R.id.tempTextView);
        TextView mainWeatherTextView = findViewById(R.id.mainWeatherTextView);
        TextView weatherDescTextView = findViewById(R.id.weatherDescTextView);


        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        weather = new WeatherModel.Builder()
                .setTemperature("25°C")
                .setMinTemperature("20°C")
                .setMaxTemperature("30°C")
                .setWeatherDescription("Cloudy")
                .setWeatherMain("Clouds")
                .build();


        maxTempTextView.setText(weather.getMaxTemperature());
        minTempTextView.setText(weather.getMinTemperature());
        tempTextView.setText(weather.getTemperature());
        mainWeatherTextView.setText(weather.getWeatherMain());
        weatherDescTextView.setText(weather.getWeatherDescription());





    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove location updates
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location updates here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Display the location in the TextView
        locationTextView.setText("Location: " + latitude + ", " + longitude);


        String coordinates = String.format("%.6f, %.6f", latitude, longitude);


        weather.setLocation(coordinates);
        Toast.makeText(this, weather.getLocation(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle when a location provider is enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle when a location provider is disabled
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle changes in the status of the location provider
    }
}
