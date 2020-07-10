package com.example.TCSS450GROUP1.ui.weather;

import android.graphics.drawable.Icon;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentWeatherPostBinding;



import java.util.List;

/**
 * @author Matthew Molina
 */
public class WeatherRecyclerViewAdapter extends
        RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder> {

    private final List<WeatherPost> mWeatherPosts;

    public WeatherRecyclerViewAdapter(List<WeatherPost> items) {
        this.mWeatherPosts = items;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_weather_post, parent, false));    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.setWeatherPost(mWeatherPosts.get(position));
    }

    @Override
    public int getItemCount() {
        return mWeatherPosts.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the WeatherPost Recycler View.
     */
    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentWeatherPostBinding binding;

        public WeatherViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentWeatherPostBinding.bind(view);
        }

        void setWeatherPost(final WeatherPost post) {
            binding.textCity.setText(post.getCityName());
            binding.textDescription.setText(post.getWeatherDescription());
            binding.textHour.setText(post.getTime());
            binding.textTemp.setText(post.getTemperature());

        }}
}
