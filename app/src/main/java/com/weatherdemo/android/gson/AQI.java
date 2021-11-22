package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AQI {

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
    public AQINow AQINow;
    @SerializedName("station")
    @Expose
    public List<Station> station = null;
    @SerializedName("refer")
    @Expose
    public Refer refer;

}
