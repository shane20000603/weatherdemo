package com.weatherdemo.android.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Refer {

    @SerializedName("sources")
    @Expose
    public List<String> sources = null;
    @SerializedName("license")
    @Expose
    public List<String> license = null;

}
