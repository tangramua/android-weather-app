package com.example.tangram.weatherapp.roomdb;

import android.arch.persistence.room.TypeConverter;

import com.example.tangram.weatherapp.models.City;
import com.example.tangram.weatherapp.models.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CityWeatherTypeConverter {

    private Gson gson;

    public CityWeatherTypeConverter() {
        gson = new Gson();
    }

    @TypeConverter
    public String fromWeeklyWeather(List<Weather> weatherList){
        return gson.toJson(weatherList);
    }

    @TypeConverter
    public List<Weather> toWeeklyWeather(String json){
        ArrayList<Weather> weatherList = gson.fromJson(json, new TypeToken<List<Weather>>() {
        }.getType());

        return weatherList;
    }

    @TypeConverter
    public String fromCity(City city){
        return gson.toJson(city);
    }

    @TypeConverter
    public City toCity(String json){
        return gson.fromJson(json, City.class);
    }

}
