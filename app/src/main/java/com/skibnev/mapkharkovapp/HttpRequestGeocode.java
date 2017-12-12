package com.skibnev.mapkharkovapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

/**
 * Created by user on 21.12.2015.
 */
public class HttpRequestGeocode extends AsyncTask<String,Void,Void> {

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng())));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng()), googleMap.getMaxZoomLevel() - 4));
        httpRequestDirecrions=new HttpRequestDirecrions(this.frgAct);
        httpRequestDirecrions.execute(ProgramDatas.getInstance().getStartLat(),ProgramDatas.getInstance().getStartLng(),ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng());
    }

    public HttpRequestGeocode(FragmentActivity act) {
        frgAct=act;
    }



    private HttpRequestDirecrions httpRequestDirecrions;
    private FragmentActivity frgAct;

    @Override
    protected Void doInBackground(String... params) {
        String url="https://maps.googleapis.com/maps/api/geocode/json?address="+params[0]+"&language=ru&sensor=true&components=country:UA|administrative_area:Kharkiv&key=AIzaSyCJYAjERoDs2AWSftsBo9_k8AuOkfShENo";
        //Log.i("ReqGeo",url);
        RestTemplate restTemplate=new RestTemplate();
        String res=restTemplate.getForObject(url,String.class);
        if (res!=null){
            try {
                JSONObject jsonObject=new JSONObject(res);
                //Log.i("ReqGeo",res);
                Log.i("ReqGeo", "before get array results and this 0 element");
                JSONArray result=jsonObject.getJSONArray("results");
                if (result==null){
                    Log.e("ReqGeo","array result is null");
                }
                Log.i("ReqGeo","before get object geometry");
                JSONObject geometry=result.getJSONObject(0).getJSONObject("geometry");
                if (geometry==null){
                    Log.e("ReqGeo","array geometry is null");
                }
                Log.i("ReqGeo","before get object location");
                JSONObject location=geometry.getJSONObject("location");
                if (location==null){
                    Log.e("ReqGeo","object location is null");
                }
                ProgramDatas.getInstance().setEndLat(location.getDouble("lat"));
                ProgramDatas.getInstance().setEndLng(location.getDouble("lng"));
            } catch (JSONException e) {
                Log.e("ReqGeo",e.toString());
            }
        }else {
            Log.e("ReqGeo","res is null !");
        }
        return null;
    }
}
