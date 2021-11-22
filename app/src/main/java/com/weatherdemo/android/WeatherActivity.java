package com.weatherdemo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherdemo.android.gson.AQI;
import com.weatherdemo.android.gson.CurrentWeather;
import com.weatherdemo.android.gson.Daily;
import com.weatherdemo.android.gson.Weather;
import com.weatherdemo.android.util.HttpUtil;
import com.weatherdemo.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pmText;
    private LinearLayout forecastLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //bind the view
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pmText = findViewById(R.id.pm_text);
        //start cache
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        if (weatherString != null) {
            //read cache
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //query weather when no cache
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            showPresentWeather(weatherId);
            requestWeather(weatherId);
            requestAQI(weatherId);
            weatherLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param weatherId
     */
    private void requestAQI(final String weatherId) {
        String AQIUrl = "https://devapi.qweather.com/v7/air/now?location=" + weatherId + "&key=0de86a5ebdde49719b8a809d4cafacbe";
        Log.i("***", "requestAQI: "+AQIUrl);
        HttpUtil.sendOkHttpRequest(AQIUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取空气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                AQI aqi = Utility.handleAQIResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aqiText.setText(aqi.AQINow.aqi);
                        pmText.setText(aqi.AQINow.pm2p5);
                    }
                });
            }
        });
    }

    /**
     * query weather info by weather_id
     *
     * @param weatherId
     */
    private void requestWeather(final String weatherId) {
        String weatherUrl = "https://devapi.qweather.com/v7/weather/7d?location=" + weatherId + "&key=0de86a5ebdde49719b8a809d4cafacbe";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "200".equals(weather.code)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showPresentWeather(final String weatherId){
        String currentWeatherUrl = "https://devapi.qweather.com/v7/weather/now?location=" + weatherId + "&key=0de86a5ebdde49719b8a809d4cafacbe";
        HttpUtil.sendOkHttpRequest(currentWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取当前天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                CurrentWeather currentWeather = Utility.handleCurrentWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assert currentWeather != null;
                        degreeText.setText(currentWeather.now.temp+"℃");
                        titleUpdateTime.setText(currentWeather.updateTime.substring(11,16));
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        titleCity.setText(getIntent().getStringExtra("countryName"));
        forecastLayout.removeAllViews();
        for (Daily daily : weather.daily) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(daily.fxDate.substring(5));
            infoText.setText(daily.textDay);
            maxText.setText(daily.tempMax);
            minText.setText(daily.tempMin);
            forecastLayout.addView(view);
        }
    }
}