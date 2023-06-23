package com.example.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "YourClass";

    private LocationManager locationManager;
    private TextView locationTextView;

    private WeatherModel weather;
    private TextView mainWeatherTextView;
    private TextView weatherDescTextView;

    final String ERROR_NO_NETWORK = "No Network";
    final String RESULTS = "results", ERROR = "error", CODE = "code", MESSAGE = "message";
    final String INDICATIONS_AND_USAGE = "indications_and_usage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);
        TextView maxTempTextView = findViewById(R.id.maxTempTextView);
        TextView minTempTextView = findViewById(R.id.minTempTextView);
        TextView tempTextView = findViewById(R.id.tempTextView);
        mainWeatherTextView = findViewById(R.id.mainWeatherTextView);
        weatherDescTextView = findViewById(R.id.weatherDescTextView);


        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        weather = new WeatherModel.Builder()
                .setTemperature("25°C")
                .setMinTemperature("20°C")
                .setMaxTemperature("30°C")
                .setWeatherDescription("LAVA IS FALLING")
                .setWeatherMain("ASH IN THE SKY")
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

        if ( Utils.isNetworkAvailable(this)){
            getWeatherInfo(latitude, longitude);
        }else{
            Toast.makeText(this, ERROR_NO_NETWORK,
                    Toast.LENGTH_LONG).show();
        }

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

    private void getWeatherInfo(double lat, double lon) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String jsonResult = Utils.getWeatherInfoFromApi(lat,lon);
                if (jsonResult == null) {
                    handler.post(() -> Log.d(TAG, "JSON is Null"));
                    return;
                }
                JSONObject jsonObject = new JSONObject(jsonResult);
                if (jsonObject.has(ERROR)) {
                    JSONObject errorObject = jsonObject.getJSONObject(ERROR);
                    String errorCode = errorObject.getString(CODE);
                    String errorMessage = errorObject.getString(MESSAGE);
                    Toast.makeText(MainActivity.this, "error Message:" + errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the weather array
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                if (weatherArray.length() > 0) {
                    // Get the first weather object
                    JSONObject weatherObject = weatherArray.getJSONObject(0);

                    // Extract the weather details
                    String mainWeather = weatherObject.getString("main");
                    String weatherDescription = weatherObject.getString("description");

                    // Set the weather details to the respective TextViews
                    handler.post(() -> {
                        mainWeatherTextView.setText(mainWeather);
                        weatherDescTextView.setText(weatherDescription);
                    });
                }

                String purpose = getString(R.string.purpose_not_available);

//                if (firstResult.has("main")) {
//                    Log.d(TAG,"if statement is running");
//                    JSONArray purposeArray = firstResult.getJSONArray(INDICATIONS_AND_USAGE);
//                    purpose = purposeArray.getString(0);
//                }

                String finalPurpose = purpose;
                handler.post(() -> Log.d(TAG, finalPurpose));

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Log.d(TAG, "catch"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }





}
