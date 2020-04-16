package com.example.tangram.weatherapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.tangram.weatherapp.roomdb.CityWeatherTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MartinRuiz on 8/19/2017.
 */
@Entity
@TypeConverters({CityWeatherTypeConverter.class})
public class CityWeather implements Serializable{

    @SerializedName("list")
    private List<Weather> weeklyWeather;

    @PrimaryKey(autoGenerate = false)
    @NonNull private City city;


    public List<Weather> getWeeklyWeather() {
        return weeklyWeather;
    }

    public void setWeeklyWeather(List<Weather> weeklyWeather) {
        this.weeklyWeather = weeklyWeather;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}


