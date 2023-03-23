package com.weather.firebaseauth.apiservices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weather.firebaseauth.model.FiveDayResponse;
import com.weather.firebaseauth.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {

    private WeatherService bookSearchService;
    private MutableLiveData<FiveDayResponse> fiveDayResponseLiveData;

    public WeatherRepository() {
        fiveDayResponseLiveData = new MutableLiveData<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        bookSearchService = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService.class);

    }

    public void getWeatherData(String cityname, String apiKey) {
        bookSearchService.getFiveDaysWeather(cityname, apiKey)
                .enqueue(new Callback<FiveDayResponse>() {
                    @Override
                    public void onResponse(Call<FiveDayResponse> call, Response<FiveDayResponse> response) {
                        if (response.body() != null) {
                            fiveDayResponseLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDayResponse> call, Throwable t) {
                        fiveDayResponseLiveData.postValue(null);
                    }
                });
    }

    public LiveData<FiveDayResponse> getFiveDayResponseLiveData() {
        return fiveDayResponseLiveData;
    }
}