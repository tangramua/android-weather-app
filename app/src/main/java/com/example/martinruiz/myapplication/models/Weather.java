package com.example.martinruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable{
    @SerializedName("dt")
    private int date;
    private Main main;
    @SerializedName("weather")
    private List<WeatherDetails> weatherDetails;
   private Clouds clouds;
   private Wind wind;
   private Sys sys;
   private String dt_txt;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<WeatherDetails> getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(List<WeatherDetails> weatherDetails) {
        this.weatherDetails = weatherDetails;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

}
