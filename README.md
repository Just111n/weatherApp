
# Weather Forecast App

The Weather Forecast App is a simple application that displays the current weather forecast using the OpenWeatherMap free weather API. It retrieves weather data for the device's current location and displays relevant information in a card view. It also includes a list of predefined cities and their current weather conditions.

## Features

- Display the current weather forecast for the device's current location.
- Display location and relevant weather information in a card view.
- Retrieve weather data by sending an HTTP request to the OpenWeatherMap API.
- Save the response for offline use and display it when there is no internet connection.
- Background network call to update weather data periodically.

## API Key

To use the OpenWeatherMap API, you need to obtain an API key. You can sign up for a free API key at [OpenWeatherMap](http://openweathermap.org/API) and replace `<YOUR_API_KEY>` in Utils.js with your actual API key in the code.

## Predefined Cities (KIV)

The app also displays the current weather conditions for the following cities:

1. New York
2. Singapore
3. Mumbai
4. Delhi
5. Sydney
6. Melbourne

## Setup

1. Clone the repository:

    ```bash
   git clone https://github.com/Just111n/weatherApp.git
   
  
2. Open the project in Android Studio.

3. Replace apiKey with your actual OpenWeatherMap API key in the following files: app/src/main/java/com/example/weatherapp/Utils.java
4. Build and run the app on your device or emulator.

## Usage
Upon launching the app, the current weather forecast for your device's current location will be displayed in a card view.
If there is no internet connection, the app will display the previously saved weather data along with the timestamp when the data was received.
The app will perform a background network call to update the weather data periodically.
Dependencies
The app uses the following dependencies:


Thank you for reviewing the Weather Forecast App. If you have any further questions or feedback, please don't hesitate to contact me.
