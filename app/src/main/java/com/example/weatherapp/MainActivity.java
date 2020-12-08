package com.example.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;

    public void checkWeather(View view) {
        try {
            DownloadData data = new DownloadData();
            String encodedCity = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            data.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find the weather!", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadData extends AsyncTask<String, Void, String> {
        String result = "";
        @Override
        protected String doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1) {
                    char currentData = (char) data;
                    result += currentData;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                String weather = object.getString("weather");
                JSONArray array = new JSONArray(weather);
                String message = "";

                for(int i=0; i<array.length(); i++) {
                    JSONObject part = array.getJSONObject(i);
                    String main = part.getString("main");
                    String description = part.getString("description");

                    if(!main.equals("") && !description.equals("")) {
                        message += main + " : " + description + "\r\n";
                    }
//                    Log.i("Main ", part.getString("main"));
//                    Log.i("Description ", part.getString("description"));
                }
                if(!message.equals("")) {
                    textView.setText(message);
                } else {
                    Toast.makeText(MainActivity.this, "Could not find the weather!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Exception ", " Failed");
                Toast.makeText(MainActivity.this, "Could not find the weather!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.answer);
        editText = findViewById(R.id.randomCity);
    }
}
