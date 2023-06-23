package com.example.weatherapp;

import static com.example.weatherapp.Utils.kelvinToCelsius;


// Builder Design Pattern
public class WeatherModel {
    private String location;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;
    private String weatherDescription;
    private String weatherMain;

    private WeatherModel(Builder builder) {
        this.location = builder.location;
        this.temperature = builder.temperature;
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature ;
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
        this.temperature = temperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
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
            this.temperature = kelvinToCelsius(temperature);
            return this;
        }

        public Builder setMinTemperature(double minTemperature) {
            this.minTemperature = kelvinToCelsius(minTemperature);
            return this;
        }

        public Builder setMaxTemperature(double maxTemperature) {
            this.maxTemperature = kelvinToCelsius(maxTemperature);
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


}
