package com.skibnev.mapkharkovapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by user on 21.12.2015.
 */
public class TouchOnMapListener implements GoogleMap.OnMapClickListener {

    public TouchOnMapListener(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private GoogleMap googleMap;

    @Override
    public void onMapClick(LatLng latLng) {
        this.googleMap.addMarker(new MarkerOptions().position(latLng));
    }
}
