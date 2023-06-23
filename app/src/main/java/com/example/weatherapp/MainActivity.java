package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "WEATHERTEST";
    private static final String JSON_KEY = "JSON_KEY" ;
    private static final String TIME_UPDATED_KEY = "TIME_UPDATED_KEY" ;
    private SharedPreferences mPreferences;
    private final String PREFERENCES_FILE_KEY = "com.example.android.mainsharedprefs";
    public static final String RATE_KEY = "Rate_Key";

    private LocationManager locationManager;
    private TextView locationTextView;

    private TextView timeUpdatedTextView;

    private WeatherModel weather;
    private TextView mainWeatherTextView;
    private TextView weatherDescTextView;
    private TextView tempTextView;

    private TextView coordTextView;
    TextView maxTempTextView;
    TextView minTempTextView;

    final String ERROR_NO_NETWORK = "No Network";
    final String RESULTS = "results", ERROR = "error", CODE = "code", MESSAGE = "message";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);

        timeUpdatedTextView = findViewById(R.id.timeTextView);
        coordTextView = findViewById(R.id.coordinatesTextView);
        locationTextView = findViewById(R.id.locationTextView);
        maxTempTextView = findViewById(R.id.maxTempTextView);
        minTempTextView = findViewById(R.id.minTempTextView);
        tempTextView = findViewById(R.id.tempTextView);
        mainWeatherTextView = findViewById(R.id.mainWeatherTextView);
        weatherDescTextView = findViewById(R.id.weatherDescTextView);


        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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


        // TODO store when exit app
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(RATE_KEY, weather.getLocation()).toString();
        preferencesEditor.apply();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location updates here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        WeatherModel weather;

        // Display the location in the TextView
        coordTextView.setText("Coordinates: " + latitude + ", " + longitude);


        if ( Utils.isNetworkAvailable(this)){
            weather = getWeatherInfo(latitude, longitude);
            timeUpdatedTextView.setText("Real Time");


        }else{

            // TODO store time when network is removed
            Toast.makeText(this, ERROR_NO_NETWORK,
                    Toast.LENGTH_LONG).show();

            mPreferences = getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
            String jsonStored = mPreferences.getString(JSON_KEY,
                    getString(R.string.default_json_string));
            String timeStored = mPreferences.getString(TIME_UPDATED_KEY,
                    getString(R.string.default_time));
            timeUpdatedTextView.setText(timeStored);

            /*** app is already been used, or app is used for the first time **/
            Log.d(TAG,"no network");
            try {
                WeatherModel weatherStored = getWeatherInfo(jsonStored);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

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

    private WeatherModel getWeatherInfo(double lat, double lon) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());




        executor.execute(() -> {
            try {
                String mainWeather = "";
                String weatherDescription = "";


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
                    mainWeather = weatherObject.getString("main");
                    weatherDescription = weatherObject.getString("description");

                }

                // Get the main object
                JSONObject mainObject = jsonObject.getJSONObject("main");

                // Extract the temperature details
                double temperature = mainObject.getDouble("temp");
                double minTemperature = mainObject.getDouble("temp_min");
                double maxTemperature = mainObject.getDouble("temp_max");


                // Get the location name
                String locationName = jsonObject.getString("name");


                 weather = new WeatherModel.Builder()
                        .setLocation(locationName)
                        .setTemperature(temperature)
                        .setMinTemperature(minTemperature)
                        .setMaxTemperature(maxTemperature)
                        .setWeatherDescription(weatherDescription)
                        .setWeatherMain(mainWeather)
                        .build();

                handler.post(() -> {
                    mainWeatherTextView.setText(weather.getWeatherMain());
                    weatherDescTextView.setText(weather.getWeatherDescription());
                    tempTextView.setText(String.valueOf(weather.getTemperature()));
                    minTempTextView.setText(String.valueOf(weather.getMinTemperature()));
                    maxTempTextView.setText(String.valueOf(weather.getMaxTemperature()));
                    locationTextView.setText(weather.getLocation());
                });

                // storing json whenever location changes
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putString(JSON_KEY,jsonResult);


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                String date = dateFormat.format(calendar.getTime());
                preferencesEditor.putString(TIME_UPDATED_KEY,date);
                preferencesEditor.apply();





            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Log.d(TAG, "catch"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return weather;
    }

    private WeatherModel getWeatherInfo(String jsonString) throws JSONException {

            String mainWeather = "";
            String weatherDescription = "";


            JSONObject jsonObject = new JSONObject(jsonString);


            // Get the weather array
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            if (weatherArray.length() > 0) {
                // Get the first weather object
                JSONObject weatherObject = weatherArray.getJSONObject(0);

                // Extract the weather details
                mainWeather = weatherObject.getString("main");
                weatherDescription = weatherObject.getString("description");

            }

            // Get the main object
            JSONObject mainObject = jsonObject.getJSONObject("main");

            // Extract the temperature details
            double temperature = mainObject.getDouble("temp");
            double minTemperature = mainObject.getDouble("temp_min");
            double maxTemperature = mainObject.getDouble("temp_max");


            // Get the location name
            String locationName = jsonObject.getString("name");


            weather = new WeatherModel.Builder()
                    .setLocation(locationName)
                    .setTemperature(temperature)
                    .setMinTemperature(minTemperature)
                    .setMaxTemperature(maxTemperature)
                    .setWeatherDescription(weatherDescription)
                    .setWeatherMain(mainWeather)
                    .build();


                mainWeatherTextView.setText(weather.getWeatherMain());
                weatherDescTextView.setText(weather.getWeatherDescription());
                tempTextView.setText(String.valueOf(weather.getTemperature()));
                minTempTextView.setText(String.valueOf(weather.getMinTemperature()));
                maxTempTextView.setText(String.valueOf(weather.getMaxTemperature()));
                locationTextView.setText(weather.getLocation());

        return weather;
    }












}
