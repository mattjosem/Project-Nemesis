package com.example.TCSS450GROUP1.ui.weather;

import java.io.Serializable;

/**
 * @author Matthew Molina
 */
public class WeatherPost implements Serializable {

    private final String mDayTime;
    private final String mTemperature;
    private final String mWeatherDescription;
    private final String mTypeDay;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     * @author Matthew Molain
     */
    public static class Builder {
        private String mDayTime = "";
        private String mTemperature = "";
        private String mWeatherDescription = "";
        private String mTypeDay = "";


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
         * @param typeDay an optional time, used for hourly weather
         * @return the Builder of this BlogPost
         */
        public Builder addTypeDay(final String typeDay) {
            mTypeDay = typeDay;
            return this;
        }


        /**
         * Add an optional city name for the weather.
         *
         * @param dayTime an optional city name
         * @return the Builder of this BlogPost
         */
        public Builder addDayTime(final String dayTime) {
            mDayTime = dayTime;
            return this;
        }

        public WeatherPost build() {
            return new WeatherPost(this);
        }

    }

    private WeatherPost(final Builder builder) {
        this.mDayTime = builder.mDayTime;
        this.mTemperature = builder.mTemperature;
        this.mWeatherDescription = builder.mWeatherDescription;
        this.mTypeDay = builder.mTypeDay;
    }

    public String getDayTime() { return mDayTime; }

    public String getTemperature() {
        return mTemperature;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public String getTypeDay() { return mTypeDay;
    }

}
