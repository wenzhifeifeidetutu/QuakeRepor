/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.LoaderManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.content.Loader;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    //private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private TextView emptyTextView;

    private ProgressBar waitProgrssBar;

    //指定Loader的id
     private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // Find a reference to the {@link ListView} in the layout

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        waitProgrssBar = (ProgressBar)findViewById(R.id.wait_data);

        emptyTextView = (TextView)findViewById(R.id.empty_text);
        earthquakeListView.setEmptyView(emptyTextView);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        if (earthquakeListView != null) {
            earthquakeListView.setAdapter(mAdapter);
        }



        if (earthquakeListView != null) {
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Earthquake earthquake = mAdapter.getItem(position);
                    Uri earthQuakeUri = null;
                    if (earthquake != null) {
                        earthQuakeUri = Uri.parse(earthquake.getUri());
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, earthQuakeUri );

                    startActivity(intent);
                }
            });
        }


        /*EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeAsyncTask();
        earthquakeAsyncTask.execute(USGS_REQUEST_URL);*/
        //改用LoaderManager管理
        LoaderManager loaderManager = getLoaderManager();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetWorkInfo = connectivityManager.getActiveNetworkInfo();
        if (activityNetWorkInfo != null && activityNetWorkInfo.isConnectedOrConnecting()){
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }else {
            emptyTextView.setText("No Internet Connection!!");
            waitProgrssBar.setVisibility(View.GONE);
        }




    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader:  is called" );

        //构造偏好设置Uri
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.setting_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        );

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        //使用Uri.Builder构建Uri对象
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);


        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Earthquake>> loader, List<Earthquake> data) {
        waitProgrssBar.setVisibility(View.GONE);
        emptyTextView.setText("NO EarthQuake Found!!");


        mAdapter.clear();
        Log.d(LOG_TAG, "onLoadFinished: is called");

        if (data != null && !data.isEmpty()) {
           mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Earthquake>> loader) {
        Log.d(LOG_TAG, "onLoaderReset: is called");
        mAdapter.clear();
    }



    /*private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {
        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null){
                return null;
            }

            List<Earthquake> result  = Query.fetchEarthquakeData(urls[0]);
//            System.out.println("777777"+result.isEmpty());
            return result;
        }

        @Override
        protected  void onPostExecute(List<Earthquake> data ){
            //清除地震之前的适配器
            mAdapter.clear();
//            Log.d(LOG_TAG, "操你麻痹bug在哪里"+data.isEmpty());
            //添加data到listView中
            if (*//*data != null && data.isEmpty()*//* data != null && !data.isEmpty()){
                mAdapter.addAll(data);
            }


        }
    }*/

            //设置菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}
