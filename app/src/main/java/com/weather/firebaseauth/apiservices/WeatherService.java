package com.weather.firebaseauth.apiservices;


import com.weather.firebaseauth.model.FiveDayResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    /**
     * Get five days weather forecast.
     *
     * @param q     String name of city
     * @param appId String api key
     * @return instance of {@link FiveDayResponse}
     */
    @GET("forecast")
    Call<FiveDayResponse> getFiveDaysWeather(
            @Query("q") String cityname,
            @Query("appid") String appId
    );
}