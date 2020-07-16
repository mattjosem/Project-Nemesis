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
    private FragmentWeatherListBinding binding;

    private UserInfoViewModel mUserModel;

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



        mModel= new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        mModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                String Lat = String.valueOf(location.getLatitude());
                String Lon = String.valueOf(location.getLongitude());
                Log.i("LAT INNER", Lat);
                Log.i("LON INNER", Lon);
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