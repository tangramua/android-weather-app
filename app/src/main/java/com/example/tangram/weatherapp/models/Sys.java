package com.example.tangram.weatherapp.models;

import java.io.Serializable;

public class Sys implements Serializable {

    private String pod;

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }
}
