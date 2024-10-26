package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;  // Input field for city name
    private TextView textViewWeather;  // TextView to display the weather
    private final String API_KEY = "e42b13d83cb7f4ea786befb4985dc496";  // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the UI components to code
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
        Button buttonFetchWeather = findViewById(R.id.buttonFetchWeather);

        // Set an OnClickListener for the button
        buttonFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();  // Hide the keyboard after input
                String city = editTextCity.getText().toString().trim();  // Get the input city name

                if (!city.isEmpty()) {
                    new FetchWeatherTask().execute(city);  // Fetch weather data for the entered city
                } else {
                    textViewWeather.setText("Please enter a city name.");  // Show error if input is empty
                }
            }
        });
    }

    // Method to hide the keyboard after clicking the button
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextCity.getWindowToken(), 0);
    }

    // AsyncTask to perform the network request in the background
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cityName = params[0];  // Get the city name from input
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    cityName + "&appid=" + API_KEY + "&units=metric";  // API URL

            try {
                URL url = new URL(urlString);  // Create a URL object
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();  // Open connection
                connection.setRequestMethod("GET");  // Set the request method to GET

                // Read the response from the API
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);  // Append each line to the response
                }

                reader.close();  // Close the reader
                return response.toString();  // Return the full response

            } catch (IOException e) {
                e.printStackTrace();  // Handle any network errors
                return null;  // Return null if there's an error
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {  // If the response is not null
                try {
                    JSONObject jsonObject = new JSONObject(result);  // Parse JSON
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temp = main.getDouble("temp");  // Get the temperature
                    String weatherInfo = "Temperature: " + temp + "°C";  // Format the weather info
                    textViewWeather.setText(weatherInfo);  // Display the temperature
                } catch (JSONException e) {
                    e.printStackTrace();  // Handle JSON parsing errors
                    textViewWeather.setText("Error parsing weather data.");
                }
            } else {
                textViewWeather.setText("Failed to get weather data. Please try again.");
            }
        }
    }
}

