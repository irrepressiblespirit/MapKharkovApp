package com.skibnev.mapkharkovapp;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
//import android.view.LayoutInflater;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 09.12.2015.
 */
public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //if (convertView==null){
            //LayoutInflater layoutInflater=LayoutInflater.from(mContext);
            //convertView=layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1,parent,false);
        //}
        View row=super.getView(position, convertView, parent);

        AutocompletePrediction item=getItem(position);
        TextView textView1=(TextView) row.findViewById(android.R.id.text1);
        //TextView textView2=(TextView) row.findViewById(android.R.id.text2);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        //textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView1.setText(item.getFullText(StyleItalic));
        //textView2.setText(item.getFullText(StyleItalic));
        //((TextView) row.findViewById(android.R.id.text1)).setText(item.getPrimaryText(StyleItalic).toString());
        //((TextView) row.findViewById(android.R.id.text2)).setText(item.getSecondaryText(StyleItalic).toString());
        return row;
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction){
                    return ((AutocompletePrediction)resultValue).getFullText(null);
                }else{
                    return super.convertResultToString(resultValue);
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                if (constraint!=null){
                   mResultList=getAutocomplete(constraint);
                    if (mResultList!=null){
                        results.values=mResultList;
                        results.count=mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results!=null && results.count>0){
                    notifyDataSetChanged();
                }else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

      private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint){

          AutocompletePredictionBuffer autocompletePredictions=null;

          if (googleApiClient.isConnected()){
              Log.i("PAA","googleApiClient is connected");

              PendingResult<AutocompletePredictionBuffer> res= Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(), bounds, null);

              autocompletePredictions=res.await(60, TimeUnit.SECONDS);

              final Status status=autocompletePredictions.getStatus();

              if (!status.isSuccess()){
                  Log.e("PAA","Error status"+status.toString());
                  autocompletePredictions.release();
                  return null;
              }
             Log.i("PAA","Query completed");

              return DataBufferUtils.freezeAndClose(autocompletePredictions);

          }else {
              Log.e("PAA","googleApiClient is not connected");
              return null;
          }
      }

    public PlaceAutocompleteAdapter(Context context, GoogleApiClient apiClient, LatLngBounds b) {
        super(context, android.R.layout.simple_dropdown_item_1line, android.R.id.text1);
        //mContext=context;
        if (apiClient==null){
            Log.i("PAA","GoogleApi is null");
        }
        googleApiClient=apiClient;
        if (b==null){
            Log.i("PAA","LatLngBounds is null");
        }
        bounds=b;
        StyleItalic=new StyleSpan(Typeface.ITALIC);
    }

    private GoogleApiClient googleApiClient;
    private LatLngBounds bounds;
    private ArrayList<AutocompletePrediction> mResultList;
    private CharacterStyle StyleItalic;
    //private final Context mContext;
}
