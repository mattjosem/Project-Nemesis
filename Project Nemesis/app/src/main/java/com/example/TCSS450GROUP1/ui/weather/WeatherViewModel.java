package com.example.TCSS450GROUP1.ui.weather;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.TCSS450GROUP1.io.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Matthew Molina
 */
public class    WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponseCurrent;
    private MutableLiveData<JSONObject> mResponseFiveDay;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mResponseCurrent = new MutableLiveData<>();
        mResponseCurrent.setValue(new JSONObject());
        mResponseFiveDay = new MutableLiveData<>();
        mResponseFiveDay.setValue(new JSONObject());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponseCurrent.observe(owner, observer);
        mResponseFiveDay.observe(owner, observer);
    }

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
     * @author Matthew Molina
     */
    public void connectToWeatherCurrent() {
        try {
            Log.i("Made it:", "HERE ONE");

            String urlCurrentDay = "http://team1-database.herokuapp.com/weather";

            Request request = new JsonObjectRequest(
                    Request.Method.GET,
                    urlCurrentDay,
                    null, //no body for this get request
                    mResponseCurrent::setValue,
                    this::handleErrorCurrent);

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

    private void handleErrorMultiple(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponseFiveDay.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponseFiveDay.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * @author Matthew Molina
     */
    public void connectToWeatherMultiple() {
        try {
            Log.i("Made it:", "HERE TWO");

            String urlFiveDay = "https://team1-database.herokuapp.com/weather/forecast";

            Request request = new JsonObjectRequest(
                    Request.Method.GET,
                    urlFiveDay,
                    null, //no body for this get request
                    mResponseFiveDay::setValue,
                    this::handleErrorMultiple);

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
}
