package com.example.atv684.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    TextView weatherDataTextView;

    String weatherData;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weatherData = getActivity().getIntent().getStringExtra(ForecastFragment.FORECAST_STRING_KEY);

        weatherDataTextView = (TextView)view.findViewById(R.id.weather_detail);

        weatherDataTextView.setText(weatherData);
    }
}
