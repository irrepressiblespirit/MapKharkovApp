package com.skibnev.mapkharkovapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.location.LocationProvider;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
//import android.util.Log;

//import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
    }

    private GoogleMap mMap;
    public LocationManager locationManager;
    private FragmentActivity frg;
    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView autocompleteView;
    private static final LatLngBounds BOUNDS_KHARKOV = new LatLngBounds(new LatLng(49.877136, 36.084597), new LatLng(50.101688, 36.405332));
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        frg = this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, 0, this).addOnConnectionFailedListener(this).addConnectionCallbacks(this).addApi(Places.GEO_DATA_API).build();
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);
        autocompleteView.setOnItemClickListener(autocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_KHARKOV);
        autocompleteView.setAdapter(mAdapter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            autocompleteView.setText(item.getFullText(STYLE_BOLD));
            HttpRequestGeocode httpRequestGeocode = new HttpRequestGeocode(frg);
            httpRequestGeocode.execute(item.getFullText(STYLE_BOLD).toString());
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new TouchOnMapListener(mMap));
        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.e("MapsAct", "In onMapReady");
            return;
        }
        Log.i("MapsAct", "In onMapReady");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);

    }

    private LocationListener locationListener = new LocationListener() {
        //private boolean flag=false;
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                Log.e("MapsActivity", "location is null !!!");
                return;
            }
            System.out.println(location.getLatitude());
            System.out.println(location.getLongitude());
            Log.e("MapsAct", "Before if in onLocationChanged method");
            if (ProgramDatas.getInstance().getEndLat() == 0 && ProgramDatas.getInstance().getEndLng() == 0) {
                Log.e("MapsAct", "In if method onLocationChanged");
                //flag=true;
                mMap.clear();
                ProgramDatas.getInstance().setStartLat(location.getLatitude());
                ProgramDatas.getInstance().setStartLng(location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(new LatLng(ProgramDatas.getInstance().getStartLat(),ProgramDatas.getInstance().getStartLng() )));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ProgramDatas.getInstance().getStartLat(),ProgramDatas.getInstance().getStartLng()), mMap.getMaxZoomLevel() - 4));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    Log.e("MapsAct", "In onMapReady");
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
             }else if (ProgramDatas.getInstance().getPoints()==null){
                 mMap.addMarker(new MarkerOptions().position(new LatLng(ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng())));
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng()), mMap.getMaxZoomLevel() - 4));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    Log.e("MapsAct", "In onMapReady");
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
             }else{
                 mMap.addMarker(new MarkerOptions().position(new LatLng(ProgramDatas.getInstance().getStartLat(), ProgramDatas.getInstance().getStartLng())));
                 mMap.addMarker(new MarkerOptions().position(new LatLng(ProgramDatas.getInstance().getEndLat(), ProgramDatas.getInstance().getEndLng())));
                 LatLngBounds.Builder latlngBuilder=new LatLngBounds.Builder();
                 for (LatLng p:ProgramDatas.getInstance().getPoints()){
                     latlngBuilder.include(p);
                 }
                 mMap.addPolyline(new PolylineOptions().addAll(ProgramDatas.getInstance().getPoints()));
                 int size=getResources().getDisplayMetrics().widthPixels;
                 LatLngBounds latLngBounds=latlngBuilder.build();
                 CameraUpdate track=CameraUpdateFactory.newLatLngBounds(latLngBounds,size,size,25);
                 mMap.moveCamera(track);
             }
         }

         @Override
         public void onStatusChanged(String provider, int status, Bundle extras) {
             Log.e("MapsAct","In onStatusChanged");
         }

         @Override
         public void onProviderEnabled(String provider) {
             if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 // TODO: Consider calling
                 //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                 // here to request the missing permissions, and then overriding
                 //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                 //                                          int[] grantResults)
                 // to handle the case where the user grants the permission. See the documentation
                 // for Activity#requestPermissions for more details.
                 Log.e("MapsAct","In onProviderEn");
                 return;
             }
             Log.e("MapsAct","In onProviderEn before if");
             locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         }

         @Override
         public void onProviderDisabled(String provider) {
             if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 // TODO: Consider calling
                 //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                 // here to request the missing permissions, and then overriding
                 //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                 //                                          int[] grantResults)
                 // to handle the case where the user grants the permission. See the documentation
                 // for Activity#requestPermissions for more details.
                 Log.e("MapsAct","In onProviderDes");
                 return;
             }
             Log.e("MapsAct", "In onProviderDes before if");
             locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

         }
     };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("ERR",connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("INF", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
