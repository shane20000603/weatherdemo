package com.weatherdemo.android.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.weatherdemo.android.db.City;
import com.weatherdemo.android.db.Country;
import com.weatherdemo.android.db.Province;
import com.weatherdemo.android.gson.AQI;
import com.weatherdemo.android.gson.CurrentWeather;
import com.weatherdemo.android.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
    parse and handle the province data from server api
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    Province province = new Province();
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     parse and handle the city data from server api
     */
    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    City city = new City();
                    JSONObject cityObject = allCities.getJSONObject(i);
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     parse and handle the country data from server api
     */
    public static boolean handleCountryResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i = 0; i < allCountries.length(); i++) {
                    Country country = new Country();
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    country.setCountryName(countryObject.getString("name"));
                    country.setWeatherId(countryObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * parse current weather
     * @param response
     * @return
     */
    public static CurrentWeather handleCurrentWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String currentWeatherContent = jsonObject.toString();
            return new Gson().fromJson(currentWeatherContent,CurrentWeather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse the return json into weather object
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String weatherContent = jsonObject.toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    public static AQI handleAQIResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            String AQIContent = jsonObject.toString();
            return new Gson().fromJson(AQIContent, AQI.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
