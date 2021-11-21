package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("updateTime")
    @Expose
    public String updateTime;
    @SerializedName("fxLink")
    @Expose
    public String fxLink;
    @SerializedName("daily")
    @Expose
    public List<Daily> daily = null;
    @SerializedName("refer")
    @Expose
    public Refer refer;

}