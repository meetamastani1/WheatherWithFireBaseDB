package com.weather.firebaseauth.model;

import java.util.ArrayList;

public class FiveDayResponse {
    public String cod;
    public int message;
    public int cnt;
    public ArrayList<WeatherListData> list;

    @Override
    public String toString() {
        return "FiveDayResponse{" +
                "cod='" + cod + '\'' +
                ", message=" + message +
                ", cnt=" + cnt +
                ", list=" + list +
                '}';
    }
}