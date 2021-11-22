package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeather {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("updateTime")
    @Expose
    public String updateTime;
    @SerializedName("fxLink")
    @Expose
    public String fxLink;
    @SerializedName("now")
    @Expose
    public Now now;
    @SerializedName("refer")
    @Expose
    public Refer refer;

}
