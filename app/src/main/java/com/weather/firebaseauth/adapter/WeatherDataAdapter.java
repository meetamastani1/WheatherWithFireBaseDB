package com.weather.firebaseauth.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.weather.firebaseauth.databinding.ItemDailyWeatherBinding;
import com.weather.firebaseauth.model.WeatherListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherDataAdapter extends RecyclerView.Adapter<WeatherDataAdapter.ViewHolder> {

    private ArrayList<WeatherListData> weatherListData;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public WeatherDataAdapter() {
        weatherListData = new ArrayList<>();
    }

    public void addAllData(ArrayList<WeatherListData> data) {
        weatherListData.clear();
        this.weatherListData = data;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDailyWeatherBinding itemDailyWeatherBinding = ItemDailyWeatherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemDailyWeatherBinding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.itemDailyWeatherBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view,position);
            }
        });

        holder.itemDailyWeatherBinding.txtDay.setText(weatherListData.get(position).dt_txt);
        holder.itemDailyWeatherBinding.txtTemp.setText("Temprature: " + String.valueOf(weatherListData.get(position).main.temp));
        holder.itemDailyWeatherBinding.txtHumidity.setText("Humidity: " + String.valueOf(weatherListData.get(position).main.humidity));
        holder.itemDailyWeatherBinding.txtWeather.setText("Weather: " + weatherListData.get(position).weather.get(0).main);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return weatherListData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemDailyWeatherBinding itemDailyWeatherBinding;

        ViewHolder(ItemDailyWeatherBinding itemView) {
            super(itemView.getRoot());
            this.itemDailyWeatherBinding = itemView;

        }

        @Override
        public void onClick(View view) {

        }
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}