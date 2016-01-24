package com.example.yomac_000.rsrpechhulp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import adapters.PopupAdapter;

public class BreakDownOnMaps extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest =  new LocationRequest();
    double currentLatitude;
    double currentLongitude;
    LatLng latLng;
    GoogleMap gMap;
    Geocoder geoCoder;
    List<Address> addresses;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down_on_maps);
        buildApi();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = (Button) findViewById(R.id.LinkToPopUp);

        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try
                {
                    showDialog("0631293494");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    public void showDialog(final String phone) throws Exception
    {
        MyTouchListener touchListener = new MyTouchListener();
        AlertDialog.Builder builder = new AlertDialog.Builder(BreakDownOnMaps.this);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_text);

        builder.setPositiveButton(R.string.btn_call_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);// (Intent.ACTION_CALL);

                callIntent.setData(Uri.parse("tel:" + phone));

                startActivity(callIntent);

                dialog.dismiss();
            }
        });
        
        builder.setNegativeButton(R.string.btn_call_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
        //builder.show();
    }

    public class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case 1:
                    //do stuff for button 1
                    break;
                case 2:
                    //do stuff for button 2
                    break;
                case 3:
                    //do stuff for button 3
                    break;
                case 4:
                    //do stuff for button 4
                    break;
            }
            return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    private void buildApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void handleNewLocation(Location loc) {
        currentLatitude = loc.getLatitude();
        currentLongitude = loc.getLongitude();
        latLng = new LatLng(currentLatitude, currentLongitude);
        System.out.println("handleNewLocation ");
        geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geoCoder.getFromLocation(currentLatitude, currentLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        String zipCode = addresses.get(0).getPostalCode();
        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();

        Marker marker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Uw Locatie:")
                .snippet(address + ", " + zipCode + "\n" +
                        city + ", " + country + "\n" +
                        "\n" +
                        "Onthoud deze locatie voor het "+ "\n" +
                        "telefoongesprek.")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.map_marker)));
        gMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        marker.showInfoWindow();
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
        else {
            handleNewLocation(location);
        };
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("currentLatitude : " + currentLatitude);
        System.out.println("currentLongitude : " + currentLongitude);
        latLng = new LatLng(currentLatitude, currentLongitude);
        setgMap(googleMap);
        if(currentLatitude != 0 || currentLongitude != 0) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");
            googleMap.addMarker(options);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public GoogleMap getgMap() {
        return gMap;
    }

    public void setgMap(GoogleMap gMap) {
        this.gMap = gMap;
    }
}
