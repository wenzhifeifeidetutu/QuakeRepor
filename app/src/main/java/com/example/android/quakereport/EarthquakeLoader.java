package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017/4/2.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String myUrl;
    public EarthquakeLoader(Context context , String url) {
        super(context);
        myUrl = url;
    }


    @Override
    protected void onStartLoading(){
        forceLoad();
    }


    @Override
    public List<Earthquake> loadInBackground( ) {

        if (myUrl == null){
            return null;
        }

        List<Earthquake> result  = Query.fetchEarthquakeData(myUrl);

        return result;
    }
}
