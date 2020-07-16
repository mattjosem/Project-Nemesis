package com.example.TCSS450GROUP1.ui.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

//import com.example.TCSS450GROUP1.databinding.FragmentWeatherBinding;
import com.example.TCSS450GROUP1.databinding.FragmentWeatherListBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;


/**
 * @author Matthew Molina
 */

public class WeatherListFragment extends Fragment {

    /** The location view model, used for the Google Maps option. **/
    private LocationViewModel mModel;

    /** The weather list view model, used to get weather information from database. **/
    private WeatherListViewModel mWeatherModel;

    /** Binding of the main weather fragment, used to pass arguments from Google Maps fragment. **/
    private FragmentWeatherListBinding binding;

    /** The UserInfoViewModel, used in this case to get the user's JWT. **/
    private UserInfoViewModel mUserModel;


    /**
     * Necessary empty constructor.
     */
    public WeatherListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherListBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherListViewModel.class);

        // Used to update recycler views with up-to-date information based on the user's inputs
        mWeatherModel.addWeatherListObserver(getViewLifecycleOwner(), weatherList -> {
            if (!weatherList.isEmpty()) {
                WeatherRecyclerViewAdapter currentRecyclerViewAdapter =
                        new WeatherRecyclerViewAdapter(weatherList.subList(0, 1));
                binding.listCurrentWeather.setAdapter(currentRecyclerViewAdapter);
                if (weatherList.size() > 1) {
                    WeatherRecyclerViewAdapter fiveDayRecyclerViewAdapter =
                            new WeatherRecyclerViewAdapter(weatherList.subList(25, 30));
                    binding.listFiveDay.setVisibility(View.VISIBLE);
                    binding.listFiveDay.setAdapter(fiveDayRecyclerViewAdapter);

                    WeatherRecyclerViewAdapter tfHourRecyclerViewAdapter =
                            new WeatherRecyclerViewAdapter(weatherList.subList(1, 25));
                    binding.list24Hour.setVisibility(View.VISIBLE);
                    binding.list24Hour.setAdapter(tfHourRecyclerViewAdapter);
                } else {
                    binding.listFiveDay.setVisibility(View.INVISIBLE);
                    binding.list24Hour.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Used to connect with the Google Maps fragment from user input as well as
        // the initial location from the phone if user gives access
        mModel= new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        mModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                String Lat = String.valueOf(location.getLatitude());
                String Lon = String.valueOf(location.getLongitude());
                mWeatherModel.connectToWeather(Lat, Lon, mUserModel.getJWT());
            }
        });

        binding.zipCodeButton.setOnClickListener(button -> {
                    String zip = binding.requestedZipCode.getText().toString();
                    if (checkZipcode(zip)) {
                        mWeatherModel.connectToWeatherZip(zip, mUserModel.getJWT());
                    } else {
                        binding.requestedZipCode.setError("Not a valid Zip Code!");
                    }
                });

        binding.mapFloatingButton.setOnClickListener(button ->
                        Navigation.findNavController(getView())
                                .navigate(WeatherListFragmentDirections
                                        .actionNavigationWeatherToMapFragment()));

        WeatherListFragmentArgs args = WeatherListFragmentArgs.fromBundle(getArguments());
        String argLat = args.getLatitude();
        String argLon = args.getLongitude();
        if (!argLat.equals("") && !argLon.equals("")) {
            Log.i("ARGLAT AND ARGLON ARE EMPTY", argLat + " " + argLon);
            mWeatherModel.connectToWeather(argLat, argLon, mUserModel.getJWT());
        }
    }


    /**
     * Checks that zip code is valid, checks for five digits, not perfect but without accessing
     * database with all available zip codes this is the best way.
     * @param zip the zip code
     * @return true or false if zip code is valid, in this case a 5 digit integer
     */
    private boolean checkZipcode(String zip) {
        boolean flag = false;
        if (zip.length() == 5) {
            try {
                //Strictly used to check if input is valid 5 digit number
                Integer intZip = Integer.parseInt(zip);

                //if it gets here then flag gets set to true, anyway else it is false
                flag = true;
                Log.i("THE ZIP CODE", String.valueOf(intZip));

                //At this point this is the best I can do with zip checking
                //without utilizing a database with valid US Zips
                //I check the length of the string for 5 digits, check for a
                //leading 0, and parseint which means it should return a valid int

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}