package com.example.mymap.weatherapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("weather")
    List<Weather> mWeather;

    @SerializedName("main")
    WeatherMain mMain;

    public List<Weather> getmWeather() {
        return mWeather;
    }

    public void setmWeather(List<Weather> mWeather) {
        this.mWeather = mWeather;
    }

    public WeatherMain getmMain() {
        return mMain;
    }

    public void setmMain(WeatherMain mMain) {
        this.mMain = mMain;
    }
}
