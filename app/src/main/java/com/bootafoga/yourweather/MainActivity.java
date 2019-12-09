package com.bootafoga.yourweather;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=7e03471b84df8649aace3fb0e94453d4&lang=ru&units=metric"; //Не забудьте ввести свой APPID после '='

    private EditText editTextCity;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewShowWeather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        String url = String.format(WEATHER_URL, city);
        DownloadWeatherTask task = new DownloadWeatherTask();
        task.execute(url);
    }

    public class DownloadWeatherTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpsURLConnection httpsURLConnection = null;
            StringBuilder res = new StringBuilder();
            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection)url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    res.append(line);
                    line = bufferedReader.readLine();
                }
                return res.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (url != null){
                    httpsURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                textViewWeather.setText(String.format("Город: %s,\nПогода: %s,\nНа улице: %s", city, temp, description));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

