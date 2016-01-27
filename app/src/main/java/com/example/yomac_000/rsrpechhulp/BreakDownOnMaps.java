package com.example.yomac_000.rsrpechhulp;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import utils.MyLocationListener;

public class BreakDownOnMaps extends FragmentActivity implements
        OnMapReadyCallback {
    double currentLatitude;
    double currentLongitude;
    LatLng latLng;
    GoogleMap gMap;
    private MyLocationListener myLocationListener;
    ArrayList<Location> locationList = new ArrayList<Location>();

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down_on_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myLocationListener = new MyLocationListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myLocationListener.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocationListener.connect();
    }

    public void handleNewLocation(Location loc) {
        currentLatitude = loc.getLatitude();
        currentLongitude = loc.getLongitude();
        latLng = new LatLng(currentLatitude, currentLongitude);
        System.out.println("handleNewLocation ");


        if (gMap != null) {

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");

            System.out.println("null");
            gMap.addMarker(options);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        } else{
            locationList.add(loc);
            System.out.println("f null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("currentLatitude : " + currentLatitude);
        System.out.println("currentLongitude : " + currentLongitude);
        latLng = new LatLng(currentLatitude, currentLongitude);

        System.out.println("locationList");
        for (int i = 0; i < locationList.size(); i++) {
            System.out.println("een lat lcoatie " + locationList.get(i).getLatitude());
        }


        if(googleMap != null) {
            setgMap(googleMap);
            if(currentLatitude != 0 || currentLongitude != 0) {
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("I am here!");
                googleMap.addMarker(options);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                System.out.println("currentLatitude OR currentLongitude is NULL");
            }
        } else {
            System.out.println("googleMap is NULL");
        }

    }

    public GoogleMap getgMap() {
        return gMap;
    }

    public void setgMap(GoogleMap gMap) {
        this.gMap = gMap;
    }
}
