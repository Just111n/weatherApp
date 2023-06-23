package com.example.weatherapp;

import java.text.DecimalFormat;

public class WeatherModel {
    private String location;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;
    private String weatherDescription;
    private String weatherMain;

    private WeatherModel(Builder builder) {
        this.location = builder.location;
        setTemperature(builder.temperature); // Convert Kelvin to Celsius
        setMinTemperature(builder.minTemperature); // Convert Kelvin to Celsius
        setMaxTemperature(builder.maxTemperature); // Convert Kelvin to Celsius
        this.weatherDescription = builder.weatherDescription;
        this.weatherMain = builder.weatherMain;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTemperature(double temperature) {
        this.temperature = kelvinToCelsius(temperature);
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = kelvinToCelsius(minTemperature);
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = kelvinToCelsius(maxTemperature);
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public static class Builder {
        private String location;
        private double temperature;
        private double minTemperature;
        private double maxTemperature;
        private String weatherDescription;
        private String weatherMain;

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setTemperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setMinTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        public Builder setMaxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
            return this;
        }

        public Builder setWeatherDescription(String weatherDescription) {
            this.weatherDescription = weatherDescription;
            return this;
        }

        public Builder setWeatherMain(String weatherMain) {
            this.weatherMain = weatherMain;
            return this;
        }

        public WeatherModel build() {
            return new WeatherModel(this);
        }
    }

    public static double kelvinToCelsius(double kelvinTemperature) {
        double celsius = kelvinTemperature - 273.15;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return Double.parseDouble(decimalFormat.format(celsius));
    }
}
