package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "WEATHERTEST";
    private static final String JSON_KEY = "JSON_KEY";
    private static final String TIME_UPDATED_KEY = "TIME_UPDATED_KEY";
    private final String PREFERENCES_FILE_KEY = "com.example.android.mainsharedprefs";

    private SharedPreferences mPreferences;

    private LocationManager locationManager;
    private TextView locationTextView;
    private TextView timeUpdatedTextView;
    private TextView mainWeatherTextView;
    private TextView weatherDescTextView;
    private TextView tempTextView;
    private TextView latTextView,lonTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;

    final String ERROR_NO_NETWORK = "No Network";
    final String ERROR = "error";
    final String MESSAGE = "message";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        timeUpdatedTextView = findViewById(R.id.timeTextView);
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        locationTextView = findViewById(R.id.locationTextView);
        maxTempTextView = findViewById(R.id.maxTempTextView);
        minTempTextView = findViewById(R.id.minTempTextView);
        tempTextView = findViewById(R.id.tempTextView);
        mainWeatherTextView = findViewById(R.id.mainWeatherTextView);
        weatherDescTextView = findViewById(R.id.weatherDescTextView);

        Log.d(TAG,"onCreate started");
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

        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove location updates
        locationManager.removeUpdates(this);

        Log.d(TAG,"onPause");
    }

    @Override
    public void onLocationChanged(Location location) {

        // Get current location coordinates
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Display Coordinates
        displayCoord(latitude,longitude);


        if ( Utils.isNetworkAvailable(this)){

            timeUpdatedTextView.setText(R.string.real_time);
            fetchWeatherInfo(latitude, longitude);

        }else{
            Toast.makeText(this, ERROR_NO_NETWORK,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG,"onLocationChanged, no network");

            loadStoredData();
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

    private void setWeatherData(WeatherModel weather) {
        weatherDescTextView.setText(weather.getWeatherDescription());
        tempTextView.setText(String.valueOf(weather.getTemperature()));
        minTempTextView.setText(String.valueOf(weather.getMinTemperature()));
        maxTempTextView.setText(String.valueOf(weather.getMaxTemperature()));
        mainWeatherTextView.setText(weather.getWeatherMain());
        locationTextView.setText(weather.getLocation());
    }

    private void fetchWeatherInfo(double lat, double lon) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String jsonResult = Utils.getWeatherInfoFromApi(lat, lon);
                if (jsonResult == null) {
                    handler.post(() -> Log.d(TAG, "JSON is null"));
                    return;
                }

                JSONObject jsonObject = new JSONObject(jsonResult);

                if (jsonObject.has(ERROR)) {
                    JSONObject errorObject = jsonObject.getJSONObject(ERROR);
                    String errorMessage = errorObject.getString(MESSAGE);
                    handler.post(() ->
                            Toast.makeText(MainActivity.this, "Error Message: " + errorMessage, Toast.LENGTH_SHORT).show());
                    return;
                }

                WeatherModel weather = parseWeatherJson(jsonObject);

                handler.post(() -> {
                    setWeatherData(weather);
                    storeWeatherData(jsonResult);
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Log.d(TAG, "IOException occurred"));
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(() -> Log.d(TAG, "JSONException occurred"));
            }
        });
    }

    private WeatherModel parseWeatherJson(JSONObject jsonObject) throws JSONException {
        // Parse weather data from the JSON object and return a WeatherModel object
        String mainWeather = "";
        String weatherDescription = "";

        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        if (weatherArray.length() > 0) {
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            mainWeather = weatherObject.getString("main");
            weatherDescription = weatherObject.getString("description");
        }

        JSONObject mainObject = jsonObject.getJSONObject("main");
        double temperature = mainObject.getDouble("temp");
        double minTemperature = mainObject.getDouble("temp_min");
        double maxTemperature = mainObject.getDouble("temp_max");

        String locationName = jsonObject.getString("name");

        return new WeatherModel.Builder()
                .setLocation(locationName)
                .setTemperature(temperature)
                .setMinTemperature(minTemperature)
                .setMaxTemperature(maxTemperature)
                .setWeatherDescription(weatherDescription)
                .setWeatherMain(mainWeather)
                .build();
    }

    private void storeWeatherData(String jsonResult) {

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(JSON_KEY, jsonResult);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());
        preferencesEditor.putString(TIME_UPDATED_KEY, date);
        preferencesEditor.apply();

        Log.d(TAG,"store weather data method ");
        Log.d(TAG,"json and time is being saved ");
    }

    private void displayCoord(double lat, double lon) {

        latTextView.setText(String.valueOf(lat));
        lonTextView.setText(String.valueOf(lon));
    }


    private void loadStoredData() {
        mPreferences = getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        String timeStored = mPreferences.getString(TIME_UPDATED_KEY, getString(R.string.default_time));
        String jsonStored = mPreferences.getString(JSON_KEY, getString(R.string.default_json_string));

        timeUpdatedTextView.setText(timeStored);

        try {
            JSONObject jsonObject = new JSONObject(jsonStored);
            WeatherModel weatherStored = parseWeatherJson(jsonObject);
            setWeatherData(weatherStored);
        } catch (JSONException e) {
            Log.d(TAG, "Unable to read jsonStored");
        }
    }















}
