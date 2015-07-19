package com.example.atv684.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public static final String TAG = ForecastFragment.class.getSimpleName();

    public static final String FORECAST_STRING_KEY = "forecastString";

    ArrayAdapter<String> mForecastAdapter;

    ListView mListView;

    public int daysForecasted = 7;

    public String mZip;

    WeatherDataParser weatherDataParser;

    public ShareActionProvider mShareActionProvider;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        String[] forecastStrings = {
            "Today - Sunny - 88/63",
            "Tomorrow - Foggy - 70/43", "Weds - Cloudy - 72/47",
            "Thurs - Rainy - 83/24",
            "Fri - Foggy - 77/53",
            "Sat - Sunny - 77/58"
        };

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mZip = preferences.getString(getResources().getString(R.string.pref_location_key), getResources().getString(R.string
            .pref_location_default));

        sendWeatherRequest(mZip);

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        weatherDataParser = new WeatherDataParser();

        ArrayList<String> forecastList = new ArrayList<String>(Arrays.asList(forecastStrings));


        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id
            .list_item_forecast_textview, forecastList);

        mListView = (ListView) view.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(FORECAST_STRING_KEY, mForecastAdapter.getItem
                    (position));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));

        mShareActionProvider.setShareIntent(getDefaultShareIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mZip = preferences.getString(getResources().getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            new FetchWeatherTask().execute(mZip);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.action_map){

            Uri geo = Uri.parse("geo:0,0?q=" + mZip);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geo);

            if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        else if(item.getItemId() == R.id.action_share){
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            mShareActionProvider.setShareIntent(getDefaultShareIntent());
        }

        return super.onOptionsItemSelected(item);
    }

    public Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(0) + " #SunshineApp");
        return intent;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void sendWeatherRequest(String zip) {
        (new FetchWeatherTask()).execute(zip);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        public String taskSendWeatherRequest(String zip) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=19806&mode=json&units=metric&cnt=7");

                final String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";

                Uri.Builder builder = new Uri.Builder();

                Uri uri = Uri.parse(BASE_FORECAST_URL).buildUpon().appendQueryParameter("q", zip)
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", Integer.valueOf(daysForecasted).toString())
                    .build();

                url = new URL(uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.e("adf", "null input stream");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.e("adsf", "buffer length = 0");
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (Exception e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return forecastJsonStr;
        }

        @Override
        protected String[] doInBackground(String... params) {

            String weatherDataString = taskSendWeatherRequest(params[0]);

            try {
                String[] weatherData = weatherDataParser.getWeatherDataFromJson(weatherDataString, daysForecasted);

                for (String s : weatherData) {
                    Log.e(TAG, s);
                }

                return weatherData;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            Log.e(TAG, "onPostExecute");
            ArrayList<String> weatherArrayList = new ArrayList<String>(Arrays.asList(result));

            for (String s : weatherArrayList) {
                Log.e(TAG, "weather = " + s);
            }

            if (mForecastAdapter == null) {
                mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,
                    weatherArrayList );
            } else {
                mForecastAdapter.clear();
                mForecastAdapter.addAll(weatherArrayList);
                mForecastAdapter.notifyDataSetChanged();
            }
        }
    }
}
