package com.weather.firebaseauth.model;

import java.util.ArrayList;

public class WeatherListData {
    public int dt;
    public WeatherMain main;
    public ArrayList<Weather> weather;
    public int visibility;
    public double pop;
    public String dt_txt;

    @Override
    public String toString() {
        return "List{" +
                "dt=" + dt +
                ", main=" + main +
                ", weather=" + weather +
                ", visibility=" + visibility +
                ", pop=" + pop +
                ", dt_txt='" + dt_txt + '\'' +
                '}';
    }
}