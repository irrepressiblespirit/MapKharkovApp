package com.skibnev.mapkharkovapp;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by user on 30.01.2016.
 */
public class ProgramDatas extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public ProgramDatas() {

    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    private static ProgramDatas instance;

    public static ProgramDatas getInstance(){
        return instance;
    }
    private double startLat=0;
    private double startLng=0;
    private double endLat=0;
    private double endLng=0;
    private ArrayList<LatLng> points=null;
}
