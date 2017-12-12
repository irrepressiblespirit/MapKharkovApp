package com.skibnev.mapkharkovapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by user on 28.12.2015.
 */
public class HttpRequestDirecrions extends AsyncTask<Double,Void,Void> {

    public HttpRequestDirecrions(FragmentActivity frg) {
        activity=frg;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        int currentInd=-1;
        int ind=0;
        double min=1000.0;
        for (double d: RoutesPrices){
            currentInd++;
            if (min>d){
                min=d;
                ind=currentInd;
            }
        }
        System.out.println("min price: " + min);
        System.out.println("index of routes with min price: " + ind);
        Path p=new Path();
        ProgramDatas.getInstance().setPoints(p.findCoord(result, ind));
        //googleMap.addPolyline(new PolylineOptions().addAll(ProgramDatas.getInstance().getPoints()));
        Intent intent=new Intent("com.skibnev.intent.action.info");
        //intent.putExtra("from","point1");
        //intent.putExtra("to","point2");
        intent.putExtra("inf",p.getTransport()+" стоимость/PRICE: "+String.valueOf(min));
        activity.startActivity(intent);
    }

    private SumArrayList<Double> StepsPrices;
    private ArrayList<Double> RoutesPrices;
    private JSONObject result=null;
    Thread thrd;
    private ArrayDeque<Thread> t;
    String number;
    private FragmentActivity activity;

    @Override
    protected Void doInBackground(Double... params) {
        StepsPrices=new SumArrayList<Double>();
        RoutesPrices=new ArrayList<Double>();
        t=new ArrayDeque<Thread>(1);
        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+params[0].toString()+","+params[1].toString()+"&destination="+params[2].toString()+","+params[3].toString()+"&mode=transit&transit_mode=bus|rail&travel_mode=TRANSIT&alternatives=true&sensor=true&key=AIzaSyCJYAjERoDs2AWSftsBo9_k8AuOkfShENo";
        //String url="https://maps.googleapis.com/maps/api/directions/json?origin="+"улица Героев Труда, Харьков, Харьковская область, Украина"+"&destination="+"улица Чкалова, Харьков, Харьковская область, Украина"+"&mode=transit&transit_mode=bus|tram|subway&travel_mode=transit&key=AIzaSyCJYAjERoDs2AWSftsBo9_k8AuOkfShENo";
        RestTemplate restTemplate=new RestTemplate();
        try{
            String res=restTemplate.getForObject(url,String.class);
            //Log.i("ReqDir", res);
            System.out.println(res);
            result=new JSONObject(res);
            JSONArray routeArray=result.getJSONArray("routes");
            System.out.println("Count of routes: "+routeArray.length());
            for (int i=0;i<routeArray.length();i++){
            JSONObject route=routeArray.getJSONObject(i);
            JSONArray legs=route.getJSONArray("legs");
            Log.i("ReqDir","count of legs: "+legs.length());
                JSONObject leg=legs.getJSONObject(0);
                JSONArray steps=leg.getJSONArray("steps");
                Log.i("ReqDir", "count of steps: " + steps.length());
                for (int j=0;j<steps.length();j++){
                    JSONObject step=steps.getJSONObject(j);
                    if (!step.has("transit_details")){
                        continue;
                    }else {
                        JSONObject info=step.getJSONObject("transit_details");
                        JSONObject line=info.getJSONObject("line");
                        String transport_type=line.getJSONObject("vehicle").getString("type");
                        System.out.println(transport_type);
                        switch (transport_type){
                            case "SHARE_TAXI":
                                number=line.getString("short_name");
                                //System.out.println(number);
                                thrd= new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        StepsPrices.add(new CalcRoutePrice().calcToBus("http://gortransport.kharkov.ua/bus/routes/",number));
                                        StepsPrices.sum();
                                    }
                                });
                                thrd.start();
                                t.add(thrd);
                                thrd=null;
                                //StepsPrices.add(this.calcToBus("http://gortransport.kharkov.ua/bus/routes/", this.convertStringToBusNumber(number)));
                                break;
                            case "SUBWAY":
                                thrd= new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        StepsPrices.add(new CalcRoutePrice().findPrice("http://gortransport.kharkov.ua/subway/index.html",false));
                                        StepsPrices.sum();
                                    }
                                });
                                thrd.start();
                                t.add(thrd);
                                thrd=null;
                                //StepsPrices.add(this.findPrice("http://gortransport.kharkov.ua/subway/index.html", false));
                                break;
                            case "TRAM":
                                thrd= new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        StepsPrices.add(new CalcRoutePrice().findPrice("http://gortransport.kharkov.ua/tram/index.html",false));
                                        StepsPrices.sum();
                                    }
                                });
                                thrd.start();
                                t.add(thrd);
                                thrd=null;
                                //StepsPrices.add(this.findPrice("http://gortransport.kharkov.ua/tram/index.html", false));
                                break;
                        }
                    }
                }
                while (t.size()!=0){
                    Thread tmp=t.pollFirst();
                    if (tmp.isAlive()){
                        t.addLast(tmp);
                    }
                }
                //Double dArray[]=new Double[StepsPrices.size()];
                //dArray=StepsPrices.toArray(dArray);
                //double sum=0;
                //for (double d:dArray){
                    //System.out.println(String.valueOf(d));
                    //sum+=d;
                //}
                //System.out.println("Route price: "+String.valueOf(sum));
                RoutesPrices.add(StepsPrices.getSumInArray());
                //StepsPrices.removeAll(StepsPrices);
                StepsPrices.clearSumOfArray();
            }
        }catch (HttpClientErrorException e){
            Log.e("ReqDir",e.getStatusCode().toString());
            Log.e("ReqDir",e.getResponseBodyAsString());
        } catch (JSONException e) {
            Log.e("ReqDirJSON", e.toString());
        }
        return null;
    }

}
