package com.example.TCSS450GROUP1.ui.weather;


import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.TCSS450GROUP1.io.RequestQueueSingleton;
import com.example.TCSS450GROUP1.ui.connections.Contacts;
import com.example.TCSS450GROUP1.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Matthew Molina
 */
public class WeatherListViewModel extends AndroidViewModel {

    private MutableLiveData<List<WeatherPost>> mWeatherPostsHourly;
    private MutableLiveData<List<WeatherPost>> mWeatherPostsFiveDay;
    private MutableLiveData<Location> mLocation;

    private MutableLiveData<JSONObject> mResponseCurrent;
    private MutableLiveData<JSONObject> mResponseFiveDay;


    public WeatherListViewModel(@NonNull Application application) {
        super(application);
        mResponseCurrent = new MutableLiveData<>();
        mResponseCurrent.setValue(new JSONObject());
        mResponseFiveDay = new MutableLiveData<>();
        mResponseFiveDay.setValue(new JSONObject());
        mWeatherPostsHourly = new MutableLiveData<>();
        mWeatherPostsHourly.setValue(new ArrayList<>());
        mWeatherPostsFiveDay = new MutableLiveData<>();
        mWeatherPostsFiveDay.setValue(new ArrayList<>());
        mLocation = new MediatorLiveData<>();
    }


    public void addWeatherListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<WeatherPost>> observer) {
        mWeatherPostsHourly.observe(owner, observer);
        mWeatherPostsFiveDay.observe(owner, observer);
    }


    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponseCurrent.observe(owner, observer);
        mResponseFiveDay.observe(owner, observer);
    }


    public void addLocationObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super Location> observer) {
        mLocation.observe(owner, observer);
    }


    public void setLocation(final Location location) {
        if (!location.equals(mLocation.getValue())) {
            mLocation.setValue(location);
        }
    }


    public Location getCurrentLocation() {
        return new Location(mLocation.getValue());
    }

    /**
     * handles possible error with database call
     * @param error with database call.
     */
    private void handleErrorCurrent(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponseCurrent.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponseCurrent.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * handles the result from the database call
     * @param result the result from the database call.
     */
    private void handleResult(final JSONObject result) {
        Log.i("THE RESULT!!!!!!!!!!!!!!", result.toString());
        if (!(result.has("current")
                && result.has("hourly")
                && result.has("forecast"))) {
            throw new IllegalStateException("Unexpected response in WeatherListViewModel: " + result);
        }
        try {
//            ArrayList<WeatherPost> wPosts = new ArrayList<>();

            //For the Current
            //To get the message json

            // THERE ARE 50 HOURS IN THE HOURLY BTW //
            String current = result.getString("current");
            Log.i("CURRENT FROM PHONE LOCATION", current);
            // gets the 3 split into an array, starts at 1 for some reason
            String[] splits = current.split("dt: |temp: |Weather:");

            Log.i("current split", String.valueOf(splits.length));

            String currentTemp = kelvinToFar(splits[2].trim());
            String currentDescription = splits[3].trim();
            String currentTime = unixToDay(splits[1].trim(), 0);

            // Clears the data so that multiple clicks onto weather does not produce
            // a recyclerview of 30 different clicks data
            mWeatherPostsHourly.getValue().clear();

            mWeatherPostsHourly.getValue().add(new WeatherPost.Builder(currentTemp, currentDescription)
                    //TIME IS THE CARD TITLE
                                    .addTime("CURRENT WEATHER")
                                    .addCityName(currentTime)
                                    .build());

            String hourly = result.getString("hourly");
            String[] hourlysplits = hourly.split("dt: |temp: |Weather:", 150);

            int dt = 1;
            int tmp = 2;
            int dsc = 3;
            while (dt < 71) {
                String hourlytmp = kelvinToFar(hourlysplits[tmp].trim());
                String  hourlydsc= hourlysplits[dsc].trim();
                String hourlydt = unixToNormal(hourlysplits[dt].trim());

                mWeatherPostsHourly.getValue().add(new WeatherPost.Builder(hourlytmp.trim(), hourlydsc.trim())
                        .addTime("24-HOUR WEATHER")
                        .addCityName(hourlydt)
                        .build());
                dt += 3;
                tmp += 3;
                dsc += 3;
            }

            String fiveday = result.getString("forecast");
            Log.i("fd", fiveday);
            String[] fivedaysplits = fiveday.split("dt: |temp: |Weather:", 20);

            int fd_dt = 1;
            int fd_tmp = 2;
            int fd_dsc = 3;
            int daysAdded = 0;
            while (fd_dt < 14) {
                String fivedaytmp = kelvinToFar(fivedaysplits[fd_tmp]).trim();
                String  fivedaydsc = (fivedaysplits[fd_dsc]).trim();
                String fivedaydt = unixToDay(fivedaysplits[fd_dt].trim(), daysAdded);

                mWeatherPostsHourly.getValue().add(new WeatherPost.Builder(fivedaytmp, fivedaydsc)
                        .addTime("FIVE DAY WEATHER")
                        .addCityName(fivedaydt)
                        .build());
                fd_dt += 3;
                fd_tmp += 3;
                fd_dsc += 3;
                daysAdded += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mWeatherPostsHourly.setValue(mWeatherPostsHourly.getValue());
        mWeatherPostsFiveDay.setValue(mWeatherPostsFiveDay.getValue());



    }


    /**
     * Connects using a lat and long.
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     */
    public void connectToWeather(String lat, String lon, String jwt) {
        String url = "https://team1-database.herokuapp.com/weather/" + lat + "/" + lon;
        connectWithUrl(url, jwt);
    }


    /**
     * Connect using a zipcode.
     * @param zip the zipcode of the location.
     */
    public void connectToWeatherZip(String zip, String jwt) {

        Log.i("BUTTON PUSHED", zip);
//        String zip = binding.requestedZipCode.toString();
        // Check if has only 5 characters

        String url = "https://team1-database.herokuapp.com/zip/" + zip;
        checkZip(url, jwt);
    }


    public void checkZip(String url, String jwt) {
        try {
            Log.i("THE URL", url);

            Request request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null, //no body for this get request
                    this::handleResultZip,
                    this::handleErrorCurrent) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    // add headers <key,value>
                    headers.put("Authorization", jwt);
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10_000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Instantiate the RequestQueue and add the request to the queue
            RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                    .addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleResultZip(JSONObject result) {
        Log.i("THE ZIP JSON", result.toString());
        if (!result.has("current")) {
            throw new IllegalStateException(
                    "Unexpected response in WeatherListViewModel: " + result);
        }
        try {
            String current = result.getString("current");
            Log.i("CURRENT", current);

            // gets the 3 split into an array, starts at 1 for some reason
            String[] splits = current.split("Weather: |temp: |dt: |Name:");


            Log.i("current split", String.valueOf(splits.length));
            for (int i = 0; i < splits.length; i++)  {
                Log.i("FUCKING FUK" + String.valueOf(i), splits[i]);
            }

            // Weird instance where the endpoint returns two "Weather", so this checks for that and
            // formats accordingly.

            String currentTemp;
            String currentDescription;
            //String currentTime;
            String currentCity;

            // four spaces, for some reason 0 index is nothing but counts as part of length???
            if (splits.length == 5) {
                currentTemp = kelvinToFar(splits[2].trim());
                currentDescription = splits[1].trim();
                //currentTime = unixToDay(splits[3].trim(), 0);
                currentCity = splits[4];

                // This is for when it adds the weird extra weather
            } else {
                currentTemp = kelvinToFar(splits[3].trim());
                currentDescription = splits[1].trim();
                //currentTime = unixToDay(splits[4].trim(), 0);
                currentCity = splits[5];
            }

            // Clears the data so that multiple clicks onto weather does not produce
            // a recyclerview of 30 different clicks data
            mWeatherPostsHourly.getValue().clear();

            mWeatherPostsHourly.getValue().add(new WeatherPost.Builder(currentTemp, currentDescription)
                    //TIME IS THE CARD TITLE
                    .addTime("CURRENT WEATHER")
                    .addCityName(currentCity)
                    .build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWeatherPostsHourly.setValue(mWeatherPostsHourly.getValue());
    }


    /**
         * @author Matthew Molina
         */
    private void connectWithUrl(String url, String jwt) {
        //try {
//            Log.i("Made it:", "HERE ONE");
//            double double_lat = mLocation.getValue().getLatitude();
//            double double_lon = mLocation.getValue().getLongitude();
//            String lat = String.valueOf(double_lat);
//            String lon = String.valueOf(double_lon);
//            Log.i("LAT AND LONG", lat + " " + lon);


            //String urlCurrentDay = "http://team1-database.herokuapp.com/weather/" + lat + "/" + lon;

            Request request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null, //no body for this get request
                    this::handleResult,
                    this::handleErrorCurrent) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    // add headers <key,value>
                    headers.put("Authorization", jwt);
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10_000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Instantiate the RequestQueue and add the request to the queue
            RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                    .addToRequestQueue(request);

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * Converts Kelvin to Fahrenheit
     * @param theTemp the kelvin temp in string format
     * @return the temperature in fahrenheit and string format
     */
    private String kelvinToFar(final String theTemp) {
        Float kelvin = Float.parseFloat(theTemp);
        //If 9.0 and 5.0 not stated as doubles then they will use int division and result in 1
        double temp = (kelvin * (9.0/5.0)) - 459.67;
        long longTemp =  Math.round(temp);
        // Apparently doing below converts the long to a string, maybe
        // because it is a primitive??
        return longTemp + "°";
    }


    private String unixToNormal(final String theDT) {
        Log.i("THE DT", theDT);
        Date expiry = new Date(Long.parseLong(theDT) * 1000);
        String exp = expiry.toString();
        //Log.i("UNIX TIME!!!!!", exp);
        DateFormat hourMinSec = new SimpleDateFormat("HH:mm");
        return hourMinSec.format(expiry);
    }


    private String unixToDay(final String theDT, final int daysAdded) {
        DateFormat df = new SimpleDateFormat("EE MMM dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysAdded);
        return df.format(cal.getTime());
    }

}
