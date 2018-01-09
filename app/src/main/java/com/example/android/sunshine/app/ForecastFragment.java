/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.service.SunshineService;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final int FORECAST_LOADER = 0;
    private ListView mListView ;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private boolean mUseTodayLayout ;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private ForecastAdapter mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG,"onCreate");
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(LOG_TAG,"oncreateOptionsMenu - inflate forecastfragement menu " + menu.size());
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(LOG_TAG,"onOptionsitemSelected ");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.e(LOG_TAG,"onOptionsItemSelected id = " + id);
//        if (id == R.id.action_refresh) {
//            Log.e(LOG_TAG,"onOptionsItemSelected calling update weather ");
//            updateWeather();
//            return true;
//        }
        Log.e(LOG_TAG,"onOptionsItemSelected " + id + " ridactionmap " + R.id.action_map);
        if (id == R.id.action_map) {
            openPreferredLocationInMap() ;
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
Log.e(LOG_TAG,"oncreateView......................................................................................" + container);
        // The CursorAdapter will take data from our cursor and populate the ListView.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        Log.e(LOG_TAG,"oncreateView mForecastAdapter" + mForecastAdapter);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.e(LOG_TAG,"oncreateView rootView " + rootView);
        // Get a reference to the ListView, and attach this adapter to it.
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
//        listView.setAdapter(mForecastAdapter);
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);
        Log.e(LOG_TAG,"oncreateView rootView " + rootView);
        Log.e(LOG_TAG,"oncreateView mForecastAdapter" + mForecastAdapter);
        // We'll call our MainActivity
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Log.e(LOG_TAG,"listView.setOnItemClickListener");
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                //    Intent intent = new Intent(getActivity(), DetailActivity.class)
                //            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                //                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                //            ));
                //    startActivity(intent);
                    Log.e(LOG_TAG,"listView.setOnItemClickListener cursor not null");
                    ((Callback)  getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,cursor.getLong(COL_WEATHER_DATE)
                            ));
                }
                //mposition = position ;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mForecastAdapter.setmUseTodayLayout(mUseTodayLayout);
        Log.e(LOG_TAG,"oncreateView rootView " + rootView);
//        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;
    }
    public void onSaveInstanceState(Bundle outState) {
        //When tables rotate, the currently selceted list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
    public interface Callback {
        /*
        A callback interface that all activities containing this fragment must
        implement.  This mechanism allows activities to be notified of item
        selections.
         */

        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(LOG_TAG,"onactivitycreated just before initLoader.......................................................................... " + FORECAST_LOADER );
        updateWeather(); // THIS IS BEING FORCED HERE
        Log.e(LOG_TAG,"onactivitycreated just before initLoader " + FORECAST_LOADER  + " but after FORCED GET WEATHER ");
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        // This is called from MainActivity onResume method
        Log.e(LOG_TAG,"onLocationWeather update weather restartForecastLoader ");
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }
    public void updateWeatherFromOutside() {
        updateWeather();
    }
    private void updateWeather() {

        Log.e(LOG_TAG,"updateWeather - SunshineService.LOCATION_QUERY_EXTRA "+ SunshineService.LOCATION_QUERY_EXTRA );
        //Log.e(LOG_TAG,"updateWeather - calls FWT.execute with location " );
  //      FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
/*        Intent alarmIntent = new Intent(getActivity(),SunshineService.AlarmReceiver.class);
        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));
        Log.e(LOG_TAG,"updateWeather - calls SunshineService innerclass AlarmReceiver with location "+ SunshineService.LOCATION_QUERY_EXTRA );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                0,
                alarmIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+500,
                pendingIntent);

*/
  //      weatherTask.execute(location);
  //      Intent intent = new Intent(getActivity(), SunshineService.class);
 //       intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA,
 //               Utility.getPreferredLocation(getActivity()));

   //     getActivity().startService(intent);
        SunshineSyncAdapter.syncImmediately(getActivity());
    }
    private void openPreferredLocationInMap() {
        Log.e(LOG_TAG, "openPreferredLocationInMap"  + COL_COORD_LAT);

        //Using the URI scheme for showing a location in a map.  This super-handy
        // intent is detailed in the Common Intents page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        if (null != mForecastAdapter) {
            Cursor cursor = mForecastAdapter.getCursor();
            if (null != cursor) {
                cursor.moveToPosition(0);
                Log.e(LOG_TAG, "openPreferredLocationInMap  cursor "  + cursor.getCount());
                Log.e(LOG_TAG, "openPreferredLocationInMap  cursor.getString() "  + cursor.getString(COL_COORD_LAT));
                String posLat = cursor.getString(COL_COORD_LAT);
                String posLong = cursor.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getActivity().getPackageManager())!=null){
                    startActivity(intent);
                } else {
                    Log.e(LOG_TAG,"Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
     //   updateWeather() ; // <---------------TAKE THIS OUT
        String locationSetting = Utility.getPreferredLocation(getActivity());
Log.e(LOG_TAG,"oncreateLoader " + locationSetting);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
Log.e(LOG_TAG,"oncreateLoader URI " + weatherForLocationUri);
Log.e(LOG_TAG,"oncreateLoader  return new CursorLoader");

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);

        Log.e(LOG_TAG, "oncreateLoader cursor loader " + cursorLoader  + " toString " + cursorLoader.toString());
        if (cursorLoader==null) {Log.e(LOG_TAG,"oncreateLoader cursor loader is null ");}
        return  cursorLoader ;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG,"onLoadFinished - adapterSwapCursor cursor.getCount() " + cursor.getCount());
        mForecastAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION){
            // If we do not need to restart the loader,and there is a desired position to
            // restore, do so now
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG,"onLoaderReset - adeaptere swap cursor null");
        mForecastAdapter.swapCursor(null);
    }
    public void setUseTodayLayout(boolean useTodayLayout){
        Log.e(LOG_TAG,"setUseTodayLayout");
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setmUseTodayLayout(mUseTodayLayout);
        }
    }
}
