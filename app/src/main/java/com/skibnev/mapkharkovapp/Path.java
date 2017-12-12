package com.skibnev.mapkharkovapp;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 27.01.2016.
 */
public class Path{
    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    private String transport="";

    public ArrayList<LatLng> findCoord(JSONObject res,int ind){

        ArrayList<LatLng> coord=new ArrayList<LatLng>();

        try {
            JSONArray routeArray=res.getJSONArray("routes");
            JSONObject route=routeArray.getJSONObject(ind);
            JSONArray legs=route.getJSONArray("legs");
            JSONObject leg=legs.getJSONObject(0);
            JSONArray steps=leg.getJSONArray("steps");
            for (int i=0;i<steps.length();i++){
                JSONObject step=steps.getJSONObject(i);
                if (step.has("transit_details")) {
                    JSONObject info = step.getJSONObject("transit_details");
                    JSONObject line = info.getJSONObject("line");
                    String transport_type = line.getJSONObject("vehicle").getString("type");
                    switch (transport_type) {
                        case "SHARE_TAXI":
                            String tr = "маршрутное_такси/SHARE_TAXI" + " № " + line.getString("short_name");
                            transport += tr;
                            break;
                        case "SUBWAY":
                            String t = "метро/SUBWAY" + " TO " + info.getJSONObject("arrival_stop").getString("name") + "FROM " + info.getJSONObject("departure_stop").getString("name");
                            transport += t;
                            break;
                        case "TRAM":
                            String tmp = "трамвай/TRAM" + " № " + line.getString("short_name");
                            transport += tmp;
                            break;
                    }
                }
                if (step.has("steps")) {
                    JSONArray inStep = step.getJSONArray("steps");
                    for (int j=0;j<inStep.length();j++){
                        JSONObject in=inStep.getJSONObject(j);
                        JSONObject polyline = in.getJSONObject("polyline");
                        coord.addAll(PolyUtil.decode(polyline.getString("points")));
                    }
                }else {
                    JSONObject polyline = step.getJSONObject("polyline");
                    coord.addAll(PolyUtil.decode(polyline.getString("points")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coord;
    }
}
