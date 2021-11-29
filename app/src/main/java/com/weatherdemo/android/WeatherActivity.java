package com.weatherdemo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weatherdemo.android.gson.AQI;
import com.weatherdemo.android.gson.CurrentWeather;
import com.weatherdemo.android.gson.Daily;
import com.weatherdemo.android.gson.Weather;
import com.weatherdemo.android.service.AutoUpdateService;
import com.weatherdemo.android.util.HttpUtil;
import com.weatherdemo.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "***WeatherActivity";

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pmText;
    private LinearLayout forecastLayout;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;

    private String weatherId;
    private String countryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        //set transparent
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_weather);
        //bind the view
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pmText = findViewById(R.id.pm_text);
        swipeRefresh = findViewById(R.id.swipe_fresh);
        swipeRefresh.setColorSchemeResources(R.color.design_default_color_primary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //start cache
        SharedPreferences prefs = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS);
        //get 3 basic info
        String currentWeatherResponseCache = prefs.getString("current_weather_response",null);
        String aqiResponseCache = prefs.getString("aqi_response",null);
        String weatherResponseCache = prefs.getString("weather", null);
        Log.i(TAG, "onCreate: cwr "+currentWeatherResponseCache);
        Log.i(TAG, "onCreate: ar "+aqiResponseCache);
        Log.i(TAG, "onCreate: wr "+weatherResponseCache);


        String bingPic = prefs.getString("bing_pic",null);
        weatherId = prefs.getString("weather_id",null);
        countryName = prefs.getString("countryName",null);
        Log.i(TAG, "onCreate: cn "+countryName);
        Log.i(TAG, "onCreate: wid "+weatherId);

        if(bingPic != null) Glide.with(this).load(bingPic).into(bingPicImg);
        else {
            loadBingPic();
        }

        if (weatherResponseCache != null && currentWeatherResponseCache != null && aqiResponseCache != null && countryName != null) {
            Log.i(TAG, "onCreate: cache");
            //read cache
            Log.i(TAG, "onCreate: "+"read cache,weatherId = "+weatherId);
            Log.i(TAG, "onCreate: "+"read cache,countryName = "+countryName);
            Log.i(TAG, "onCreate: "+"read cache,weatherResponse = "+weatherResponseCache);

            showTitleName(countryName);
            Weather weather = Utility.handleWeatherResponse(weatherResponseCache);
            AQI aqi = Utility.handleAQIResponse(aqiResponseCache);
            CurrentWeather currentWeather = Utility.handleCurrentWeatherResponse(currentWeatherResponseCache);
            showCacheInfo(weather,aqi,currentWeather);
        } else {
            Log.i(TAG, "onCreate: no cache");
            //query weather when no cache
            weatherId = getIntent().getStringExtra("weather_id");
            countryName = getIntent().getStringExtra("countryName");
            SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
            editor.putString("weather_id",weatherId);
            editor.putString("countryName",countryName);
            editor.apply();
            showTitleName(countryName);
            queryAndShowWeatherInfo(weatherId);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                titleCity.setText(countryName);
                queryAndShowWeatherInfo(weatherId);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void resetCountry(String countryName,String weatherId){
        this.countryName = countryName;
        this.weatherId = weatherId;
        SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
        editor.putString("countryName",countryName);
        editor.putString("weather_id",weatherId);
        editor.apply();
        showTitleName(countryName);
        queryAndShowWeatherInfo(weatherId);
    }

    /**
     * show all weather info by weather id
     * @param weatherId
     */
    private void queryAndShowWeatherInfo(String weatherId){
        weatherLayout.setVisibility(View.INVISIBLE);
        loadBingPic();
        requestCurrentWeather(weatherId);
        requestWeather(weatherId);
        requestAQI(weatherId);
        weatherLayout.setVisibility(View.VISIBLE);
    }


    /**
     * load background pic
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     *
     * @param weatherId
     */
    private void requestAQI(final String weatherId) {
        String AQIUrl = "https://devapi.qweather.com/v7/air/now?location=" + weatherId + "&key=" + Utility.key;
        Log.i(TAG, "requestAQI: "+AQIUrl);
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
                SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                editor.putString("aqi_response",responseText);
                editor.apply();
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
        String weatherUrl = "https://devapi.qweather.com/v7/weather/7d?location=" + weatherId + "&key=" + Utility.key;
        Log.i(TAG, "requestWeather: "+weatherUrl);
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
                            SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
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

    private void requestCurrentWeather(final String weatherId){
        String currentWeatherUrl = "https://devapi.qweather.com/v7/weather/now?location=" + weatherId + "&key=" + Utility.key;
        Log.i(TAG, "requestCurrentWeather: "+currentWeatherUrl);
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
                SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                editor.putString("current_weather_response",responseText);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        degreeText.setText(currentWeather.now.temp+"℃");
                        titleUpdateTime.setText(currentWeather.updateTime.substring(11,16));
                        weatherInfoText.setText(currentWeather.now.text);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
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

    private void showCacheInfo(Weather weather,AQI aqi,CurrentWeather currentWeather){
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showWeatherInfo(weather);
                aqiText.setText(aqi.AQINow.aqi);
                pmText.setText(aqi.AQINow.pm2p5);
                degreeText.setText(currentWeather.now.temp+"℃");
                titleUpdateTime.setText(currentWeather.updateTime.substring(11,16));
                weatherInfoText.setText(currentWeather.now.text);
            }
        });
    }

    private void showTitleName(String countryName){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleCity.setText(countryName);
            }
        });
    }
}