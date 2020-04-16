package com.example.martinruiz.myapplication.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.martinruiz.myapplication.models.CityWeather;

import java.util.List;

@Dao
public interface CityWeatherDao {

    @Query("SELECT * FROM cityweather")
    List<CityWeather> getAll();

    @Query("DELETE FROM cityweather")
    void clear();

    @Insert
    void insert(List<CityWeather> cityWeatherList);

    @Update
    void update(CityWeather cityWeather);

    @Delete
    void delete(CityWeather cityWeather);
}
