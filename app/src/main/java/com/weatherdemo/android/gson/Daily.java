package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Daily {

    @SerializedName("fxDate")
    @Expose
    public String fxDate;
    @SerializedName("sunrise")
    @Expose
    public String sunrise;
    @SerializedName("sunset")
    @Expose
    public String sunset;
    @SerializedName("moonrise")
    @Expose
    public String moonrise;
    @SerializedName("moonset")
    @Expose
    public String moonset;
    @SerializedName("moonPhase")
    @Expose
    public String moonPhase;
    @SerializedName("moonPhaseIcon")
    @Expose
    public String moonPhaseIcon;
    @SerializedName("tempMax")
    @Expose
    public String tempMax;
    @SerializedName("tempMin")
    @Expose
    public String tempMin;
    @SerializedName("iconDay")
    @Expose
    public String iconDay;
    @SerializedName("textDay")
    @Expose
    public String textDay;
    @SerializedName("iconNight")
    @Expose
    public String iconNight;
    @SerializedName("textNight")
    @Expose
    public String textNight;
    @SerializedName("wind360Day")
    @Expose
    public String wind360Day;
    @SerializedName("windDirDay")
    @Expose
    public String windDirDay;
    @SerializedName("windScaleDay")
    @Expose
    public String windScaleDay;
    @SerializedName("windSpeedDay")
    @Expose
    public String windSpeedDay;
    @SerializedName("wind360Night")
    @Expose
    public String wind360Night;
    @SerializedName("windDirNight")
    @Expose
    public String windDirNight;
    @SerializedName("windScaleNight")
    @Expose
    public String windScaleNight;
    @SerializedName("windSpeedNight")
    @Expose
    public String windSpeedNight;
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
    @SerializedName("uvIndex")
    @Expose
    public String uvIndex;

}