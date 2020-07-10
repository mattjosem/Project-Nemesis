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

    private LocationViewModel mModel;
    private WeatherListViewModel mWeatherModel;
    private WeatherListViewModel mWeatherModelMultiple;
    private FragmentWeatherListBinding binding;

    private UserInfoViewModel mUserModel;
    //Hardcoded to Puyallup Lat Long, will update with location updates
    private String Lat = "47.1854";
    private String Lon = "-122.2929";

    public WeatherListFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mWeatherModelCurrent = new ViewModelProvider(getActivity()).get(WeatherListViewModel.class);
//        mWeatherModelMultiple = new ViewModelProvider(getActivity()).get(WeatherListViewModel.class);
//    }

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

        //mWeatherModelMultiple.connectToWeatherMultiple();
//        mWeatherModelCurrent.addResponseObserver(getViewLifecycleOwner(),
//                this::observeWeatherCurrent);
        //WeatherModelMultiple.addResponseObserver(getViewLifecycleOwner(),
        //        this::observeWeatherMultiple);
        mWeatherModel.addWeatherListObserver(getViewLifecycleOwner(), weatherList -> {
            if (!weatherList.isEmpty()) {
                WeatherRecyclerViewAdapter wRecyclerViewAdapter = new WeatherRecyclerViewAdapter(weatherList);
                binding.listWeather.setAdapter(wRecyclerViewAdapter);
            }
        });



        mModel= new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        mModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            this.Lat = String.valueOf(location.getLatitude());
            this.Lon = String.valueOf(location.getLongitude());
            if(location != null) {
                Lat = String.valueOf(location.getLatitude());
                Lon = String.valueOf(location.getLongitude());
                Log.i("LAT INNER", Lat);
                Log.i("LON INNER", Lon);
                mWeatherModel.connectToWeather(Lat, Lon, mUserModel.getJWT());
                getLatLong(Lat, Lon);
            }
        });
        Log.i("LAT OUTER", Lat);
        Log.i("LON OUTER", Lon);

        binding.zipCodeButton.setOnClickListener(button -> {
                    String zip = binding.requestedZipCode.getText().toString();
                    //binding.zipCodeButton.setText(zip);
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
        String argLon = args.getLatitude();
        if (!argLat.equals("") && !argLon.equals("")) {
            mWeatherModel.connectToWeather(argLat, argLon, mUserModel.getJWT());
        }
//        mWeatherModelCurrent.connectToWeatherCurrent(Lat, Lon);


        //mWeatherModelCurrent.connectToWeatherCurrent(Lat, Lon);


    }

    private boolean checkZipcode(String zip) {
        boolean flag = false;
        if (zip.length() == 5) {
            try {

                //Strictly used to check if input is valid 5 digit number
                Integer intZip = Integer.parseInt(zip);

                //if it gets here then flag gets set to true, anyway else it is false
                flag = true;
                Log.i("THE INTZIP", String.valueOf(intZip));

                //At this point this is the best I can do with zip checking
                //without utilizing a database with valid US Zips
                //I check the length of the string for 5 digits, check for a
                //leading 0, and parseint which means it should return a valid int

            } catch (Exception e) {
                e.printStackTrace();
                //binding.requestedZipCode.setError("Not a Valid Zip");
                //Log.i("Error", e.toString());
            }
        }
        return flag;
    }

    private void getLatLong(String lat, String lon) {
        Lat = lat;
        Lon = lon;
    }
}