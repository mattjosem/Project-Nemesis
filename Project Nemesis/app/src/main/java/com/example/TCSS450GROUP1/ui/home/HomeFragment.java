package com.example.TCSS450GROUP1.ui.home;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.TCSS450GROUP1.databinding.FragmentHomeBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;
import com.example.TCSS450GROUP1.ui.chat.ChatViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Joseph Rushford
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    private static final int HARD_CODED_CHAT_ID = 1;
    private ChatViewModel mChatModel;
    private UserInfoViewModel mUserModel;
    private HomeViewModel mModel;

    private CurrentWeatherViewModel mCurrentModel;
    private Double latD;
    private Double longD;
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mModel = provider.get(HomeViewModel.class);


        mCurrentModel = provider.get(CurrentWeatherViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());

        binding.textEmailHome.setText(mUserModel.getEmail());
       // mCurrentModel.connect(mUserModel.getJWT(), lat, longD);
        mModel.addLocationObserver(getViewLifecycleOwner(), this::observeCurrentLocation);



    }

    private void observeCurrentLocation(Location location) {
        Double latitude = mModel.getCurrentLocation().getLatitude();
        Double longitude = mModel.getCurrentLocation().getLongitude();
        mCurrentModel.connect(mUserModel.getJWT(), latitude, longitude );
        mCurrentModel.addResponseObserver(getViewLifecycleOwner(), this::observeCurrentWeather);
     //   binding.weatherHome.setText(latitude);
      //  binding.weatherTypeHome.setText(longitude);
    }

    private void observeCurrentWeather(JSONObject response) {
        Log.i("length response" , response.toString());
        Log.i("length response" , String.valueOf(response.length()));
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.homeCity.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    getCurrentWeather(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void getCurrentWeather(JSONObject response) throws JSONException {

       // JSONObject jsonMessage = new JSONObject(response.getString("current"));
        String current = response.getString("current");
        String[] splits = current.split("dt: |temp: |Weather:");

            Log.i("current", splits[0]);

        Log.i("current", response.getString("current"));
        binding.weatherHome.setText(splits[3]);
        Float kev = Float.valueOf(splits[2]);
        String temp = String.valueOf(kelvinToFar(kev));
         binding.currentTemperatureHome.setText(temp);

    }



    public long kelvinToFar(Float theInt) {
        //If 9.0 and 5.0 not stated as doubles then they will use int division and result in 1
        double temp = (theInt * (9.0/5.0)) - 459.67;
        return Math.round(temp);
    }


}
