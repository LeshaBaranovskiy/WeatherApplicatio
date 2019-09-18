package com.example.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private static TextView textViewName;

    private String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        editTextName = findViewById(R.id.editTextTownName);
        textViewName = findViewById(R.id.textViewName);
    }

    public void ShowWeather(View view) {
        String cityOrIndex = editTextName.getText().toString();
        url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=58137882965053f2f69a49799e47a31a&units=metric&lang=ru", cityOrIndex);
        FindWeather findWeather = new FindWeather();
        findWeather.execute(url);
        textViewName.setText("");

        //спрятать клавиатуру после нажатия на кнопку
        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private static class FindWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject main = jsonObject.getJSONObject("main");
                String desc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String temp = main.getString("temp");
                String name = jsonObject.getString("name");
                String weather = String.format("Температура в городе : %s %s по Цельсию.\n Погода: %s \nХорошего дня :)", name, temp, desc);
                textViewName.setText(weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
