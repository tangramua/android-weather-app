package com.example.tangram.weatherapp.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.tangram.weatherapp.models.CityWeather;

@Database(entities = {CityWeather.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityWeatherDao cityWeatherDao();
}
