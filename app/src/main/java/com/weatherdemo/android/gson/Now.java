package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("obsTime")
    @Expose
    public String obsTime;
    @SerializedName("temp")
    @Expose
    public String temp;
    @SerializedName("feelsLike")
    @Expose
    public String feelsLike;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("wind360")
    @Expose
    public String wind360;
    @SerializedName("windDir")
    @Expose
    public String windDir;
    @SerializedName("windScale")
    @Expose
    public String windScale;
    @SerializedName("windSpeed")
    @Expose
    public String windSpeed;
    @SerializedName("humidity")
    @Expose
    public String humidity;
    @SerializedName("precip")
    @Expose
    public String precip;
    @SerializedName("pressure")
    @Expose
    public String pressure;
    @SerializedName("vis")
    @Expose
    public String vis;
    @SerializedName("cloud")
    @Expose
    public String cloud;
    @SerializedName("dew")
    @Expose
    public String dew;

}
