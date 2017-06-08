package com.example.andrew.mymapapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.StringPrepParseException;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private EditText searchBarText;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private final static long MIN_TIME_IN_UPDATES = 1000 * 15 * 1;
    private final static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private Location myLocation;
    private final static int MY_LOC_ZOOM_FACTOR = 17;
    private boolean tracking = false;
    private Button trackingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "what");
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        */
    }

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng birth = new LatLng(32.7157, -117.1611);
        mMap.addMarker(new MarkerOptions().position(birth).title("Marker in Place of Birth"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birth));
    }

    public void changeMapType(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void pointsOfInterest(View v) {
        searchBarText = (EditText) findViewById(R.id.searchText);
        String loc = searchBarText.getText().toString();
        Geocoder geo = new Geocoder(this,Locale.US);
        List<Address> addressList = null;
        try{
            Log.d(TAG, "pointsOfInterest: Getting address");
            if(!loc.equals("")) {
                if (myLocation != null) {
                    addressList = geo.getFromLocationName(loc, 500
                            , myLocation.getLatitude() - .08333333
                            , myLocation.getLongitude() - .08333333,
                            myLocation.getLatitude() + .08333333,
                            myLocation.getLongitude() + .08333333);
                    for (int i = 0; i < addressList.size(); i++) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(addressList.get(i).getLatitude(),
                                        addressList.get(i).getLongitude()))
                                .title(loc));
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLng
                                        (new LatLng(addressList.get(i).getLatitude(),
                                                addressList.get(i).getLongitude())));
                    }
                    Log.d(TAG, "pointsOfInterest: Successful");
                } else if (myLocation == null) {
                    Log.d(TAG, "pointsOfInterest: Tracking needs to be turned on");
                    Toast.makeText(this, "Turn on Tracking", Toast.LENGTH_SHORT).show();
                }
            }
            else if(loc.equals("")){
                Log.d(TAG,"pointsOfInterest: No input");
                Toast.makeText(this, "Put a location into the input field", Toast.LENGTH_SHORT).show();
            }
        }
        catch(IOException ioException){
            Log.d(TAG, "pointsOfInterest: Error getting location", ioException);
        }
        catch(IllegalArgumentException illegalArgumentException){
            for(int i=0;i<addressList.size();i++){
                Log.d(TAG, "pointsOfInterest: Latitude "+addressList.get(i).getLatitude() + " Longitude " + addressList.get(i).getLongitude(), illegalArgumentException);
            }

        }

    }

    public void getLocation(View v) {

        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            //get GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Log.d(TAG, "getLocation: GPS enabled");
            }
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Log.d(TAG, "getLocation: Network enabled");
            }
            if(tracking==false){
                tracking=true;
                Toast.makeText(this,"Tracking Enabled",Toast.LENGTH_SHORT).show();
               // trackingButton.setText("Tracking On");
            }
            else if(tracking==true){
                trackingButton = (Button) findViewById(R.id.button4);
                Toast.makeText(this,"Tracking disabled",Toast.LENGTH_SHORT).show();
                tracking=false;
                isGPSEnabled=false;
                isNetworkEnabled=false;
                locationManager.removeUpdates(locationListenerNetwork);
                locationManager.removeUpdates(locationListenerGPS);
            }
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d(TAG, "getLocation: No provider is enabled");
            } else {

                this.canGetLocation = true;

                if (isGPSEnabled) {
                    Log.d(TAG, "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_IN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGPS);

                    Log.d(TAG, "getLocation: GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }

                if (isNetworkEnabled) {
                    Log.d(TAG, "getLocation: Network enabled - requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_IN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);

                    Log.d(TAG, "getLocation: Network update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }



            }

        } catch (Exception e) {
            Log.d(TAG, "Caught exception in getLocation");
            e.printStackTrace();
        }

    }


    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //output is Log.d and Toast that GPS is enabled
            Log.d(TAG, "locationListenerGPS: onLocationChanged: enabled");
            Toast.makeText(MapsActivity.this, "GPS is enabled", Toast.LENGTH_SHORT);

            //Drop a marker on map - create a method called dropMarker
            dropGPSMarker(LocationManager.GPS_PROVIDER);

            //Remove the network location updates
            locationManager.removeUpdates(locationListenerNetwork);
            isNetworkEnabled=false;
            isGPSEnabled=true;

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output is Log.d and Toast that GPS is enabled and working
            Log.d(TAG, "locationListenerGPS: onLocationChanged: enabled");
            Toast.makeText(MapsActivity.this, "GPS is enabled", Toast.LENGTH_SHORT);

            //setup a switch statement to check the status input parameter
            //case LocationProvider.AVAILABLE --> output message to Log.d and Toast
            if (status == LocationProvider.AVAILABLE) {
                Log.d(TAG, "locationListenerGPS: Location Provider is available");
                Toast.makeText(MapsActivity.this, "Location provider available", Toast.LENGTH_SHORT);
                isGPSEnabled=true;
                isNetworkEnabled=false;
            }
            //case LocationProvider.OUT_OF_SERVICE --> request updates from NETWORK_PROVIDER
            //case LocationProvider.TEMPORARILY_UNAVAILABLE --> request updates from NETWORK_PROVIDER
            else if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_IN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        locationListenerNetwork);
                isNetworkEnabled=true;
                isGPSEnabled = false;
            }
            //case default --> request updates from NETWORK_PROVIDER
            else {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_IN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        locationListenerNetwork);
                isNetworkEnabled=true;
                isGPSEnabled = false;
            }

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //output is Log.d and Toast that Network is enabled
            Log.d(TAG, "locationListenerNetwork: onLocationChanged: Network enabled");
            Toast.makeText(MapsActivity.this, "Network is enabled", Toast.LENGTH_SHORT);

            //Drop a marker on map - create a method called dropMarker
            dropNetworkMarker(LocationManager.NETWORK_PROVIDER);

            //Relaunch the network provider request (requestLocationUpdates (NETWORK_PROVIDER))
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_IN_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerNetwork);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output is Log.d and Toast that NETWORK is enabled and working
            Log.d(TAG, "locationListenerNetwork: onStatusChanged: Network is enabled and working");
            Toast.makeText(MapsActivity.this, "Network is enabled and working", Toast.LENGTH_SHORT);

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void dropGPSMarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);

        }
        if(myLocation == null){
            //Display a message via Log.d and/or Toast
            Log.d(TAG, "myLocation is null");
            Toast.makeText(MapsActivity.this, "myLocation is null", Toast.LENGTH_SHORT).show();
        }
        else{

            //Get the location
            userLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

            //Display a message with the lat/long
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation,MY_LOC_ZOOM_FACTOR);

            //Drop the actual marker on the map
            //If using circles, reference Android Circle class
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation)
                    .radius(5)
                    .strokeColor(Color.GREEN)
                    .strokeWidth(3));

            mMap.animateCamera(update);
        }


    }
    public void dropNetworkMarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);

        }
        if (myLocation == null) {
            //Display a message via Log.d and/or Toast
            Log.d(TAG, "myLocation is null");
            Toast.makeText(MapsActivity.this, "myLocation is null", Toast.LENGTH_SHORT).show();
        } else {

            //Get the location
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //Display a message with the lat/long
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);

            //Drop the actual marker on the map
            //If using circles, reference Android Circle class
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation)
                    .radius(5)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(3));

            mMap.animateCamera(update);
        }
    }
    public void clearMap(View v) {

        mMap.clear();

    }

    public void pointsNearby(View v){
        Geocoder finder = new Geocoder(this, Locale.getDefault());
        ArrayList addresses = new ArrayList();
    }

}
