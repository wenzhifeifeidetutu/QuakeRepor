package com.example.android.quakereport;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/3/12.
 */

public final class Query {
    public static final String LOG_TAG =Query.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link Query} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private Query() {
    }

    private static URL creteUrl(String urlOfString) {
        URL url =null;
        try{
            url = new URL(urlOfString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }




    //从http返回string中
    // 解析json数据
    private static List<Earthquake> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //创建list
        List<Earthquake> eathquakeList = new ArrayList<>();

        //提取json对象
        try{
            JSONObject baseObject = new JSONObject(jsonResponse);
            JSONArray featuresArray = baseObject.getJSONArray("features");
            Date dateObject;
            DecimalFormat decimalFormatMag = new DecimalFormat("0.0");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD,yyyy h:mm a", Locale.ENGLISH);

            //把每一个地震放到list里面
            for (int i = 0; i<featuresArray.length(); i++){
                JSONObject containObject = featuresArray.getJSONObject(i);
                JSONObject propertiesObject = containObject.getJSONObject("properties");
                String place = propertiesObject.getString("place");
                Double mag = propertiesObject.getDouble("mag");
                String url  = propertiesObject.getString("url");
                long time = propertiesObject.getLong("time");
                String magFormat = decimalFormatMag.format(mag);

                dateObject = new Date(time);
                String dateToDisplay = dateFormat.format(dateObject);

                Earthquake earthquakeContain = new Earthquake(magFormat, place,dateToDisplay, url);
                eathquakeList.add(earthquakeContain);
                //System.out.println(magFormat+" "+ place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return eathquakeList;
    }

    //进行http请求

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponseString = "";
        //如果url为空直接返回空的json
        if (url == null){
            return jsonResponseString;
        }

        //开始建立http请求
        HttpURLConnection  httpUrlConnection = null;
        InputStream inputStream = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setReadTimeout(10000);
            httpUrlConnection.setConnectTimeout(15000);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();
//            System.out.println("connect!!!!");

            if (httpUrlConnection.getResponseCode() == 200 ){
                inputStream = httpUrlConnection.getInputStream();
                //用StringBuild来建立string对象
                jsonResponseString = readFromSream(inputStream);
//                System.out.println("connect!!!! ok");
//                System.out.println(jsonResponseString+"666666666");
            }else {
                Log.e(LOG_TAG, "Error: "+ httpUrlConnection.getResponseCode());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (httpUrlConnection != null){
                httpUrlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponseString;

    }

    private static String readFromSream(InputStream inputStream) throws IOException {
        //采用StringBuilder来进行流处理
        StringBuilder output = new StringBuilder();
        if (inputStream !=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line !=null){
                output.append(line);
                line  =reader.readLine();
            }
        }

        return output.toString();

    }


    //得到地震数据
    public static  List<Earthquake> fetchEarthquakeData(String requeseUrl) {
        Log.d(LOG_TAG, "fetchEarthquakeData: is called ");
        //创建URL对象
        URL url = creteUrl(requeseUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Earthquake> earthquakeList = extractFeatureFromJson(jsonResponse);
        return earthquakeList;
    }



}
