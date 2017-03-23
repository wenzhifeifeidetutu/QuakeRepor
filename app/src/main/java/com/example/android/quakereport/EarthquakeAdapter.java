package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter( Context context, List<Earthquake> earthquakeList) {
        super(context, 0, earthquakeList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.eaethquake_list, parent,false);
        }
        //得到earthiquake对象
        Earthquake earthquake = getItem(position);
        //得到textView对象
        TextView magnitudeText = (TextView) listItemView.findViewById(R.id.magnitude);
        TextView locationTextView = (TextView)listItemView.findViewById(R.id.location);
        TextView timeTextView = (TextView)listItemView.findViewById(R.id.time);
        TextView locationOfset = (TextView)listItemView.findViewById(R.id.location_offset);
        TextView timeMouthTextView = (TextView)listItemView.findViewById(R.id.date);

        //设置对象text的值
        magnitudeText.setText(earthquake.getMagnitude()+"");
        String location = earthquake.getLocation();
        //进行拆分location
        String[] text = location.split("of");
        if (text[0].equals(location)){
            locationOfset.setText("Near the");
            locationTextView.setText(text[0]);
        }else {
            locationOfset.setText(text[0]);
            locationTextView.setText(text[1]);
        }

        //进行拆分time
        String time = earthquake.getTime();
        String[] timeString = time.split(" ");
        timeMouthTextView.setText(timeString[0]+" "+timeString[1]);
        timeTextView.setText(timeString[2]);
        //增加震级圆圈的正确显示
        //从textView中获取背景 GradientDrawable

        GradientDrawable magnitudeCircle = (GradientDrawable)magnitudeText.getBackground();
        //将string 转换为double
        double color = Double.parseDouble(earthquake.getMagnitude());

        int magnitudeColor = getMagnitudeColor(color);

        magnitudeCircle.setColor(magnitudeColor);

        return listItemView;
    }

    //调整圆圈里的颜色
    public int getMagnitudeColor(Double magnitude){

        int magnitudeColorResourceId;
        //将double转为整数
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor){
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId =R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId =R.color.magnitude10plus;
                break;
        }
        //将颜色id转换成颜色值使用ContextCompat.getColor（）
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
