package com.example.andrew.mymapapp;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.StringPrepParseException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        String loc = searchBarText.getText().toString().toLowerCase();
        if (loc.equals("balboa park")) {
            LatLng balboa = new LatLng(32.730831, -117.142586);
            mMap.addMarker(new MarkerOptions().position(balboa).title("Balboa Park"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(balboa));
        }
        if (loc.equals("airport")) {
            LatLng airport = new LatLng(32.7338006, -117.193303792);
            mMap.addMarker(new MarkerOptions().position(airport).title("San Diego Airport"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(airport));
        }
        if (loc.equals("seaworld")) {
            LatLng seaworld = new LatLng(32.7648, -117.2266);
            mMap.addMarker(new MarkerOptions().position(seaworld).title("Seaworld"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(seaworld));
        }
        if (loc.equals("zoo")) {
            LatLng zoo = new LatLng(32.7347483943, -117.150943196);
            mMap.addMarker(new MarkerOptions().position(zoo).title("San Diego Zoo"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(zoo));
        }

    }

   /* @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_PERMISSIONS){
            Log.d(TAG, "Failed Permission check 1");
            Log.d(TAG, Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.GET_PERMISSIONS ){
            Log.d(TAG, "Failed Permission check 2");
            Log.d(TAG, Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
        mMap.setMyLocationEnabled(true);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onLocationChanged(Location location) {

        handleNewLocation(location);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }*/

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
                Log.d(TAG, "locationListenerGPS: Location Provider is availabe");
                Toast.makeText(MapsActivity.this, "Location provider availabe", Toast.LENGTH_SHORT);
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

}
