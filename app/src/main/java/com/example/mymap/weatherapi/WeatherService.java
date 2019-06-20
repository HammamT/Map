package com.example.mymap.weatherapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("/data/2.5/weather")
    Call<WeatherResponse> get(
            @Query("APPID") String token,
            @Query("lat") String latitude,
            @Query("lon") String longtitude
    );
}
