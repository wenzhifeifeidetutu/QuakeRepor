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

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private EarthquakeAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // Find a reference to the {@link ListView} in the layout

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = mAdapter.getItem(position);
                Uri earthQuakeUri = Uri.parse(earthquake.getUri());

                Intent intent = new Intent(Intent.ACTION_VIEW, earthQuakeUri );

                startActivity(intent);
            }
        });


        EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeAsyncTask();
        earthquakeAsyncTask.execute(USGS_REQUEST_URL);



    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {
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
            if (/*data != null && data.isEmpty()*/ data != null && !data.isEmpty()){
                mAdapter.addAll(data);
            }


        }
    }


}
