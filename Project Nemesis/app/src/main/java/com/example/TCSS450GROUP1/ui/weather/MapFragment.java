package com.example.TCSS450GROUP1.ui.weather;

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
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentMapBinding;
import com.example.TCSS450GROUP1.ui.weather.LocationViewModel;

/**
 * @author Matthew Molina
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private LocationViewModel mModel;
    private GoogleMap mMap;
    private String mapLat;
    private String mapLon;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentMapBinding binding = FragmentMapBinding.bind(getView());

        mModel = new ViewModelProvider(getActivity()).get(LocationViewModel.class);
//        mModel.addLocationObserver(getViewLifecycleOwner(), location ->
//                binding.mapSearchButton.setText("Search");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);

        //String mapLat = String.valueOf(mModel.getCurrentLocation().getLatitude());
        //String mapLon = String.valueOf(mModel.getCurrentLocation().getLongitude());
        binding.mapSearchButton.setOnClickListener(button -> {
                if (mapLat == null || mapLon == null) {
                    mapLat = "";
                    mapLon = "";
                }
                Navigation.findNavController(getView())
                        .navigate(MapFragmentDirections
                                .actionMapFragmentToNavigationWeather(mapLat, mapLon));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);

                final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
                //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
            }
        });
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        mMap.clear();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mapLat = String.valueOf(latLng.latitude);
        mapLon = String.valueOf(latLng.longitude);


        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
    }
}

