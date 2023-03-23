package com.weather.firebaseauth.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.weather.firebaseauth.apiservices.WeatherRepository;
import com.weather.firebaseauth.model.FiveDayResponse;
import com.weather.firebaseauth.utils.Constants;

public class BookSearchViewModel extends AndroidViewModel {
    private WeatherRepository weatherRepository;
    private LiveData<FiveDayResponse> volumesResponseLiveData;

    public BookSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        weatherRepository = new WeatherRepository();
        volumesResponseLiveData = weatherRepository.getFiveDayResponseLiveData();
    }

    public void searchVolumes(String city) {
        weatherRepository.getWeatherData(city, Constants.API_KEY);
    }

    public LiveData<FiveDayResponse> getVolumesResponseLiveData() {
        return volumesResponseLiveData;
    }
}