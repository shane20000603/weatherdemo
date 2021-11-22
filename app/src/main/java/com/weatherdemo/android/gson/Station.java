package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("pubTime")
    @Expose
    public String pubTime;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("aqi")
    @Expose
    public String aqi;
    @SerializedName("level")
    @Expose
    public String level;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("primary")
    @Expose
    public String primary;
    @SerializedName("pm10")
    @Expose
    public String pm10;
    @SerializedName("pm2p5")
    @Expose
    public String pm2p5;
    @SerializedName("no2")
    @Expose
    public String no2;
    @SerializedName("so2")
    @Expose
    public String so2;
    @SerializedName("co")
    @Expose
    public String co;
    @SerializedName("o3")
    @Expose
    public String o3;

}
