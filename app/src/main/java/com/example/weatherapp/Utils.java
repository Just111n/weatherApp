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

    private static URL buildURL(String medName) {
        String scheme = "https";
        final String authority = "api.fda.gov";
        final String path = "drug";
        final String subPath = "label.json";
        final String apiKey = "aMchNi8ksgXCpONFYXBszSboK641yncpEWPA7v8g";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(scheme)
                .authority(authority)
                .appendPath(path)
                .appendPath(subPath)
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("search", "openfda.generic_name:" + Uri.encode(medName));
        Uri uri = builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            assert false;
        }

        return url;
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getMedInfoFromApi(String medName) throws IOException, JSONException {
        URL url = buildURL(medName);
        return getJson(url);
    }
}


