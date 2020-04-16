package com.example.martinruiz.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class CityWeatherStorage {
    private static CityWeatherStorage instance;
    private CityWeatherStorage() {
        cityWeatherList = new ArrayList<>();
    }
    public static CityWeatherStorage getInstance(){
        if (instance == null) instance = new CityWeatherStorage();
        return instance;
    }



   private List<CityWeather> cityWeatherList;



    public synchronized List<CityWeather> getCityWeatherList() {
        return cityWeatherList;
    }

    public synchronized void setCityWeatherList(List<CityWeather> cityWeatherList) {
        this.cityWeatherList = cityWeatherList;
    }
}
