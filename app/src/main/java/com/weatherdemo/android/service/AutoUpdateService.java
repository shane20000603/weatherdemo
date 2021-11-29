package com.weatherdemo.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.weatherdemo.android.gson.AQI;
import com.weatherdemo.android.gson.CurrentWeather;
import com.weatherdemo.android.gson.Weather;
import com.weatherdemo.android.util.HttpUtil;
import com.weatherdemo.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG = "*****Service";
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int an8Hour = 2 * 10 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + an8Hour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        Log.i(TAG, "updateBingPic: started");
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                editor.putString("bing_pic",response.body().string());
                editor.apply();
            }
        });
    }

    private void updateWeather() {
        Log.i(TAG, "updateWeather: started");
        SharedPreferences prefs = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS);
        String countryName = prefs.getString("countryName",null);
        String weatherId = prefs.getString("weather_id",null);
        String weather = prefs.getString("weather", null);
        String aqi = prefs.getString("aqi_response",null);
        String currentWeather = prefs.getString("current_weather_response",null);
        if (countryName != null && weatherId != null && weather != null && aqi != null && currentWeather != null){
            String weatherUrl = "https://devapi.qweather.com/v7/weather/7d?location=" + weatherId + "&key=" + Utility.key;
            String currentWeatherUrl = "https://devapi.qweather.com/v7/weather/now?location=" + weatherId + "&key=" + Utility.key;
            String AQIUrl = "https://devapi.qweather.com/v7/air/now?location=" + weatherId + "&key=" + Utility.key;
            Log.i(TAG, "updateWeather: "+weatherUrl);
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Log.i(TAG, "onResponse: "+ responseText);
                    Weather weatherResponse = Utility.handleWeatherResponse(responseText);
                    if( weatherResponse != null && weatherResponse.code.equals("200") ){
                        SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
            HttpUtil.sendOkHttpRequest(currentWeatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    CurrentWeather currentWeatherResponse = Utility.handleCurrentWeatherResponse(responseText);
                    Log.i(TAG, "onResponse: "+ responseText);
                    if( currentWeatherResponse != null && currentWeatherResponse.code.equals("200") ){
                        SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                        editor.putString("current_weather_response", responseText);
                        editor.apply();
                    }
                }
            });
            HttpUtil.sendOkHttpRequest(AQIUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    AQI aqiResponse = Utility.handleAQIResponse(responseText);
                    Log.i(TAG, "onResponse: "+responseText);
                    if( aqiResponse != null && aqiResponse.code.equals("200") ){
                        SharedPreferences.Editor editor = getSharedPreferences("cache",Context.MODE_MULTI_PROCESS).edit();
                        editor.putString("aqi_response", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
}