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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

public class MainActivity extends AppCompatActivity
    // added lessen 5 handle list item click
    implements ForecastFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane ;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG,"x1");
        mLocation = Utility.getPreferredLocation(this);
        Log.e(LOG_TAG,"x2");
        setContentView(R.layout.activity_main);  // <-- causes FF OnCreate & onCreateView to be called, then cascades into onActivityCreated, oncreateloader, onCreateOptionsMenu,onLoadFinishSwapCursor
        Log.e(LOG_TAG,"x3");
//        ForecastFragment ff = new ForecastFragment();
//        ff.updateWeatherFromOutside();
        if (findViewById(R.id.weather_detail_container) != null) {
            Log.e(LOG_TAG,"oncreate1");
            // detail container view will be present only in the large screen layouts
            // if this view is present, then the activity should be in the two pane mode
            mTwoPane = true;
            // in two-pane mode, show detail view of activty by
            // adding or replacing the detail fragment using a
            // fragment transaction
            if (savedInstanceState == null) {
                Log.e(LOG_TAG,"oncreate2 - savedInstanceSTate = null ");
//                Log.e(LOG_TAG,"oncreate2");
//                FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(this);
//               String location = Utility.getPreferredLocation(this);
//                Log.e(LOG_TAG,"updateWeather - calls FWT.execute with main activity " + location);
//                fetchWeatherTask.execute(location);

                getSupportFragmentManager().beginTransaction()
                        //                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        //.add(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Log.e(LOG_TAG,"oncreate3");
            mTwoPane = false ;
            getSupportActionBar().setElevation(0f) ; // prevents casting of shadow in 1 pane more
        }
        Log.e(LOG_TAG,"call ForecastFragment using getSupportfragmentManageer - findFragmentById R.id.fragment-forecast");
        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);
        Log.e(LOG_TAG,"init sunshineSyncAdapter");
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG_TAG,"onCreateOptionsMenu - inflates menu " + menu.toString() + " size " + menu.size());
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Log.e(LOG_TAG,"onCreateOptionsMenu - inflates menu " + menu.toString() + " size after inflater " + menu.size());
        return true;
    }



    //public boolean onOptionsItemSelected(MenuItem item) {

    public void onItemSelected(Uri contentUri) {
        Log.e(LOG_TAG,"onITemSelected ");
        if (mTwoPane) {
            Log.e(LOG_TAG,"onITemSelected twoPane true / new DetailFragment");
            Bundle args = new Bundle() ;
            args.putParcelable(DetailFragment.DETAIL_URI,contentUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container,detailFragment,DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(LOG_TAG,"onITemSelected twoPane false / DetailActivityClass setData contentURI " + contentUri);
            Intent intent = new Intent(this,DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
}
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
Log.e(LOG_TAG,"onOptionsItemSelected");
        int id = item.getItemId();
        Log.e(LOG_TAG,"onOptionsItemSelected value of id " + id);
        Log.e(LOG_TAG,"onOptionsItemSelected value of id " + R.id.action_settings);
        Log.e(LOG_TAG,"onOptionsItemSelected value of id " + R.id.action_map);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e(LOG_TAG,"onOptionsItemSelected actionSettings go to SettingsFragment");
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
//            startActivity(new Intent(this, SettingsFragment.class));
            startActivity(settingsIntent);
//            ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.fragment_forecast));
//            forecastFragment.setUseTodayLayout(!mTwoPane);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.weather_detail_container,detailFragment,DETAILFRAGMENT_TAG)
//                    .commit();

            return true;
        }

 //       if (id == R.id.action_map) {
 //           Log.e(LOG_TAG,"onOptionsItemSelected actionMap go to openPreferredLocationInMap");
 //           openPreferredLocationInMap();
 //           return true;
 //       }
        Log.e(LOG_TAG,"onOptionsitemSelected " + " bottom of method " + super.onOptionsItemSelected(item));
        return super.onOptionsItemSelected(item);
    }

 /*   private void openPreferredLocationInMap() {
        Log.e(LOG_TAG,"openPreferredLocationINMap");
        String location = Utility.getPreferredLocation(this);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }
*/
    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        Log.e(LOG_TAG,"On resume location " + location + " mLocation " + mLocation);
        if (location != null && !location.equals(mLocation)) {
            Log.e(LOG_TAG,"On resume location not null & does not equal mLocation - ergo locationchange? new ForecastFragment");
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            //ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if ( null != ff ) {
                Log.e(LOG_TAG,"On resume forecast fragment not null exec onLocationChange");
                ff.onLocationChanged();
            }
            Log.e(LOG_TAG,"On resume find detail fragment");
            DetailFragment detailFragment =
                    (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != detailFragment) {
                Log.e(LOG_TAG,"On resume detail fragment not null exec onlocationchange");
                detailFragment.onLocationChanged(location) ;


            }
            mLocation = location;
        }
    }
    protected void onStart() { super.onStart() ; Log.e(LOG_TAG,"onStart");}
    protected void onPause() { super.onPause() ; Log.e(LOG_TAG,"onPause");}
    protected void onStop() { super.onStop() ; Log.e(LOG_TAG,"onStop");}
    protected void onDestroy() { super.onDestroy() ; Log.e(LOG_TAG,"onDestroy");}
    protected void onRestart() { super.onRestart() ; Log.e(LOG_TAG,"onRestart");}
}
