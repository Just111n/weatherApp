package com.example.weatherapp;

public class WeatherModel {
    private String location;
    private String temperature;
    private String minTemperature;
    private String maxTemperature;
    private String weatherDescription;
    private String weatherMain;

    private WeatherModel(Builder builder) {
        this.location = builder.location;
        this.temperature = builder.temperature;
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature;
        this.weatherDescription = builder.weatherDescription;
        this.weatherMain = builder.weatherMain;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public String getMaxTemperature() {
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
        private String temperature;
        private String minTemperature;
        private String maxTemperature;
        private String weatherDescription;
        private String weatherMain;

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setTemperature(String temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setMinTemperature(String minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        public Builder setMaxTemperature(String maxTemperature) {
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
}
