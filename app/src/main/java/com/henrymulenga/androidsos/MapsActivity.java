package com.henrymulenga.androidsos;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.henrymulenga.androidsos.utilities.CurrentLocation;
import com.henrymulenga.androidsos.utilities.OnConnectedListener;
import com.henrymulenga.androidsos.utilities.OnLocationChangedListener;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.PermissionCallback;

/**
 * Created by Henry Mulenga
 *
 * This class shows a Google map of the location and the user position relative to medical facilities.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/03/2018.
*/
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, PermissionCallback, OnConnectedListener, OnLocationChangedListener {

    // Debugging TAG for Log
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private CurrentLocation myCurrentLocation;

    // Default Location if No GPS is UTH Lusaka -15.431637, 28.313821;
    private final LatLng mDefaultLocation = new LatLng(-15.431637, 28.313821);

    //constants
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_PERMISSIONS = 20;
    private static final int WORLD_ZOOM = 1;
    private static final int STREET_ZOOM = 15;

    // Variables
    private Location mLastKnownLocation;
    private LatLng mCoordinate;
    private CameraPosition mCameraPosition;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "CAMERA_POSITION";
    private static final String KEY_LOCATION = "LOCATION";

    // Flags
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check if we have permission to use camera and GPS sensor
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheckFineLocation == -1) {
            new AskPermission.Builder(this)
                    .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    .setCallback(this)
                    .request(REQUEST_PERMISSIONS);
        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //checkFirebaseConnection();

        // Start GPS
        startGPS();
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

        // Adding top padding to accommodate for toolbar
        mMap.setPadding(0, 200, 0, 200);

        // Turning on the My Location layer and the related control on the map.
        updateLocationUI();

        // Getting the current location of the device and setting the position of the map.
        getDeviceLocation();

        // Add a marker in Sydney and move the camera
        //-15.4309956,28.3049162
        /*LatLng sydney = new LatLng(-15.431637, 28.313821);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Lusaka"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        Log.d(TAG, "updateLocationUI");
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } /*else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }*/

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /*
     * Gets the devices location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }/* else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }*/
        /*
         * Getting the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            // getting the last location
            mLastKnownLocation = myCurrentLocation.getLastLocation();
        }

        // Setting the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            // Getting the users location co-ordinates
            mCoordinate = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            // Animating the Cameras movement to position
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mCoordinate, STREET_ZOOM);
            mMap.animateCamera(yourLocation, 5000, null);

            // Looking up POIs
            //new PoiLookup().execute();

        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, WORLD_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
    /**
     * Starts the Google Maps API Client
     */
    private void startGPS() {
        myCurrentLocation = new CurrentLocation(this, this, getApplicationContext());
        myCurrentLocation.buildGoogleApiClient(getApplicationContext());
        myCurrentLocation.start();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        startGPS();
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        String errorMessage = "The application has been unable to get permission to use the GPS sensors and thus we not be able to work.";
        //openDialog(errorMessage);
        Log.d(TAG,errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

    }

    /**
     * Handling the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle bundle) {
        // Building the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Handling suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG,"location changed = " + location);
        //System.out.println("altitude: " + location.getAltitude());

        // store it off for use when we need it
        mLastKnownLocation = location;
        //lastLatitude = location.getLatitude();
        //lastLongitude = location.getLongitude();
        //locationData = "["+String.valueOf(lastLatitude)+","+String.valueOf(lastLongitude)+"]";
        //toolbar.setTitle("GPS: "+  String.valueOf(mLastKnownLocation.getAccuracy())+ "meters");

        //check if settings have changed and refresh view
        /*if (prefManager.isSettingsChangedStatus()){
            if (mMap != null){
                refreshMap(mCoordinate);
            }
            //clear flag
            prefManager.setSettingsChangedStatus(false);
            //upload user changed settings
            uploadUserInteraction("Change of Parameters");
        }*/
        //user POI location awareness monitoring
        //proximityCheck();

        //upload user location
        //uploadUserLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
