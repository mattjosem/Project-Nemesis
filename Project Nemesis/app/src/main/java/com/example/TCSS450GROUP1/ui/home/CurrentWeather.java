package com.example.TCSS450GROUP1.ui.home;

import java.io.Serializable;

class CurrentWeather implements Serializable {
    private final String mWeatherName;
    private final double temp;
    public CurrentWeather(String weather, Double temp ) {

        this.mWeatherName = weather;
        this.temp = temp;
    }
    public String getWeatherName( ){
        return this.mWeatherName;
    }
    public Double getTemp() {
        return this.temp;
    }
}
