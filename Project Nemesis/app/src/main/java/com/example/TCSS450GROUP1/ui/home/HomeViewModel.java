package com.example.TCSS450GROUP1.ui.home;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Joseph Rushford
 */
public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<Location> mLocation;
    private MutableLiveData<JSONObject> mResponse;
    private String mEmail;
    public HomeViewModel(@NonNull Application application) {
        super(application);

        mLocation = new MediatorLiveData<>();
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

    public void addLocationObserver(@NonNull LifecycleOwner owner,
                    @NonNull Observer<? super Location> observer) {
        mLocation.observe(owner, observer);
    }
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    public void setLocation(final Location location) {
        if (!location.equals(mLocation.getValue())) {
            mLocation.setValue(location);
        }
    }

    public Location getCurrentLocation() {
        return new Location(mLocation.getValue());
    }

    public void handleResult(final JSONObject result) {
        Log.i("weather", result.toString());
        if(!result.has("current")) {
            throw new IllegalStateException("Unexpected response in HomeViewModel" + result);
        }
        try {
            JSONObject jsonCurrent = result.getJSONObject("current");
            Log.i("array", jsonCurrent.toString());
           // JSONObject weather = new JSONObject((jsonCurrent.getString(0)));
            //JSONObject temp = new JSONObject((jsonCurrent.getString(1)));
            mResponse.setValue(jsonCurrent);
           // Log.i("CURRENT WEATHER", mResponse.getValue().getWeatherName() + mResponse.getValue().getTemp().toString());
        } catch(Exception e) {
            e.printStackTrace();
        }




    }



    private void handleError(VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }
}
