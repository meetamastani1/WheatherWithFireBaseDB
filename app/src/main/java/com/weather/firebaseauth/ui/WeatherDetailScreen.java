package com.weather.firebaseauth.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.weather.firebaseauth.databinding.ActivityWeatherDetailBinding;
import com.weather.firebaseauth.model.WeatherListData;

public class WeatherDetailScreen extends AppCompatActivity {

    private ActivityWeatherDetailBinding activityWeatherDetailBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWeatherDetailBinding=ActivityWeatherDetailBinding.inflate(getLayoutInflater());
        View view=activityWeatherDetailBinding.getRoot();
        setContentView(view);

        Gson gson = new Gson();
        WeatherListData ob = gson.fromJson(getIntent().getStringExtra("myjson"), WeatherListData.class);
        activityWeatherDetailBinding.txtWeatherDetail.setText(ob.toString());
    }

}
