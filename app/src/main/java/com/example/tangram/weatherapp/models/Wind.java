package com.example.tangram.weatherapp.models;

import java.io.Serializable;

public class Wind implements Serializable {
    private float speed, deg;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }
}
