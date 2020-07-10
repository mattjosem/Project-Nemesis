package com.example.TCSS450GROUP1.ui.weather;

import java.io.Serializable;

/**
 * @author Matthew Molina
 */
public class WeatherPost implements Serializable {

    private final String mCityName;
    private final String mTemperature;
    private final String mWeatherDescription;
    private final String mTime;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     * @author Matthew Molain
     */
    public static class Builder {
        private String mCityName = "";
        private String mTemperature = "";
        private String mWeatherDescription = "";
        private String mTime = "";


        /**
         * Constructs a new Builder.
         *
         * @param Temperature the temperature for this location
         * @param WeatherDescription the description of the weather
         */
        public Builder(String Temperature, String WeatherDescription) {
            this.mTemperature = Temperature;
            this.mWeatherDescription = WeatherDescription;
        }

        /**
         * Add an optional time for the weather.
         *
         * @param Time an optional time, used for hourly weather
         * @return the Builder of this BlogPost
         */
        public Builder addTime(final String Time) {
            mTime = Time;
            return this;
        }


        /**
         * Add an optional city name for the weather.
         *
         * @param cityName an optional city name
         * @return the Builder of this BlogPost
         */
        public Builder addCityName(final String cityName) {
            mCityName = cityName;
            return this;
        }

        public WeatherPost build() {
            return new WeatherPost(this);
        }

    }

    private WeatherPost(final Builder builder) {
        this.mCityName = builder.mCityName;
        this.mTemperature = builder.mTemperature;
        this.mWeatherDescription = builder.mWeatherDescription;
        this.mTime = builder.mTime;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public String getTime() { return mTime;
    }

}
