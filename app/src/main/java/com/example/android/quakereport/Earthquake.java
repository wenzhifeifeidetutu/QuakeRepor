package com.example.android.quakereport;

import java.net.URI;

/**
 * Created by Administrator on 2017/3/10.
 */

public class Earthquake {
    private String magnitude ;

    private String location ;

    private String time;

    private String uri;



    public Earthquake(String magnitude, String location, String time, String uri){
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.uri = uri;
    }

    public String getMagnitude(){
        return magnitude;
    }

    public String getLocation(){
        return location;
    }

    public String getUri() {
        return uri;
    }
    public String getTime(){
        return time;
    }
}
