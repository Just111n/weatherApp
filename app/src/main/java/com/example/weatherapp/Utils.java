package com.example.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    private static final String GET = "GET";

    private static InputStream getInputStream (URL url) {
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private static String convertStreamToString(InputStream inputStream) throws IOException{
        BufferedReader reader;
        String outString;
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        if (buffer.length() == 0) {
            return null;
        }
        outString = buffer.toString();
        return outString;
    }

    private static String getJson(URL url) throws IOException {
        String json;
        json = convertStreamToString(getInputStream(url));
        return json;
    }

    private static URL buildURL(double latitude, double longitude) {
        final String apiKey = "936ce59a8d9cd81dd85d0f31b05815a9";
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendQueryParameter("lat", String.valueOf(latitude))
                .appendQueryParameter("lon", String.valueOf(longitude))
                .appendQueryParameter("appid", apiKey);
        Uri uri = builder.build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getWeatherInfoFromApi(double latitude, double longitude) throws IOException, JSONException {
        URL url = buildURL(latitude, longitude);
        return getJson(url);
    }

}


