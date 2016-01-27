package com.example.yomac_000.rsrpechhulp;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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
    private double currentLatitude;
    private double currentLongitude;
    private LatLng latLng;
    private GoogleMap gMap;
    private Geocoder geoCoder;
    private List<Address> addresses;
    private Button button;
    private View vLinkToRehabMenu;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down_on_maps);
        final LocationManager manager = (LocationManager) getSystemService( BreakDownOnMaps.this.LOCATION_SERVICE );
        //Returns false if there isn't a internet connection on the device
        if(!isOnline()) buildAlertMessageNoInternet();

        //Returns false if GPS isn't enabled on the device
        if ( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) buildAlertMessageNoGps();

        //Initializing the GoogleApiClient object
        buildApi();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Through calling getMapAsync() we are calling the onMapReady() through a callback
        mapFragment.getMapAsync(this);

        vLinkToRehabMenu = findViewById(R.id.tvLinkToRehabMenu);
        button = (Button) findViewById(R.id.LinkToPopUp);

        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.tvLinkToRehabMenu:
                        intent = new Intent(getApplicationContext(),
                                RehabilitationMenu.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.LinkToPopUp:
                        // custom dialog
                        final Dialog dialog = new Dialog(BreakDownOnMaps.this);
                        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog);
                        Button dialogBtnCancel, dialogBtnConfirm;
                        dialogBtnCancel = (Button) dialog.findViewById(R.id.dialog_cancel);
                        dialogBtnConfirm =  (Button) dialog.findViewById(R.id.dialog_ok);

                        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
                            public void onClick(View v) {
                                switch(v.getId()) {
                                    case R.id.tvLinkToRehabMenu:
                                        intent = new Intent(getApplicationContext(),
                                                RehabilitationMenu.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case R.id.dialog_cancel:
                                        dialog.dismiss();
                                        break;
                                    case R.id.dialog_ok:
                                        try {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + "0631293494"));
                                            if (ContextCompat.checkSelfPermission(BreakDownOnMaps.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for Activity#requestPermissions for more details.
                                                return;
                                            }
                                            startActivity(callIntent);
                                        } catch (ActivityNotFoundException activityException) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Het bellen is mislukt", Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                        break;
                                }
                            }
                        };

                        dialogBtnCancel.setOnClickListener(myOnlyhandler);
                        dialogBtnConfirm.setOnClickListener(myOnlyhandler);

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawableResource(R.color.lightgreen);


                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();
                        // Setting the dialog window at the bottom of the screen
                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);

                        dialog.show();
                        break;
                }
            }
        };
        vLinkToRehabMenu.setOnClickListener(myOnlyhandler);
        button.setOnClickListener(myOnlyhandler);
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getApplicationContext(),
                RehabilitationMenu.class);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(BreakDownOnMaps.this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uw GPS staat momenteel uit wilt u deze aanzetten?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("U heeft geen internetverbinding wilt u verbinding maken?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        doKeepDialog(alert);
    }

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
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
        geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            //Initializing the Address list with address, postal code, city etc.
            addresses = geoCoder.getFromLocation(currentLatitude, currentLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isOnline()) {
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
            //Setting the info window with my own implementation
            gMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            marker.showInfoWindow();
        }
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

    public void setgMap(GoogleMap gMap) {
        this.gMap = gMap;
    }
}
