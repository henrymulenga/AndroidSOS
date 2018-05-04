package com.henrymulenga.androidsos;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.henrymulenga.androidsos.models.Hospital;
import com.henrymulenga.androidsos.models.Incident;
import com.henrymulenga.androidsos.models.User;
import com.henrymulenga.androidsos.utilities.CurrentLocation;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;
import com.henrymulenga.androidsos.utilities.OnConnectedListener;
import com.henrymulenga.androidsos.utilities.OnLocationChangedListener;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.PermissionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henry Mulenga
 *
 * This class shows a Google map of the location and the firebaseUser position relative to medical facilities.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/03/2018.
 *
 * Loosely based on the works of Kapil on How to send SMS programmatically ? – Android Tutorial
 * Accessed at https://www.androidtutorialpoint.com/basics/send-sms-programmatically-android-tutorial/
 * Accessed on 15/04/2018
 */

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, PermissionCallback, OnConnectedListener, OnLocationChangedListener,NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = MapActivity.class.getSimpleName();

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

    // Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbHospitals,dbEmergencyContactInfo, dbUserIncidents;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();
    private String incidentKey;
    private String incidentResult;

    //Hospitals
    private List<Hospital> locations = new ArrayList<Hospital>();
    private List<Hospital> updatedLocations = new ArrayList<Hospital>();
    private User emergencyContact;
    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkFirebaseConnection();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check if we have permission to use camera and GPS sensor
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheckFineLocation == -1) {
            new AskPermission.Builder(this)
                    .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
                    .setCallback(this)
                    .request(REQUEST_PERMISSIONS);
        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Start GPS
        startGPS();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creates the sms dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MapActivity.this);
                // set title
                alertDialogBuilder.setTitle(R.string.app_name);

                if (emergencyContact != null) {

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("You are about to ask for help from " + emergencyContact.getFirstName() + " " + emergencyContact.getLastName() + ", This will send your location as well.")
                            .setCancelable(false)
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //send SMS
                                    String locationUrl = "";

                                        if (mLastKnownLocation != null) {
                                            locationUrl = "https://www.google.com/maps/search/?api=1&query=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
                                        }
                                        String message = "Help me. I am at \n" + locationUrl + "\n";
                                        SmsManager sms = SmsManager.getDefault();
                                        // if message length is too long messages are divided
                                        List<String> messages = sms.divideMessage(message);
                                        for (String msg : messages) {

                                            PendingIntent sentIntent = PendingIntent.getBroadcast(MapActivity.this, 0, new Intent("SMS_SENT"), 0);
                                            PendingIntent deliveredIntent = PendingIntent.getBroadcast(MapActivity.this, 0, new Intent("SMS_DELIVERED"), 0);
                                            sms.sendTextMessage(emergencyContact.getPhoneNumber(), null, msg, sentIntent, deliveredIntent);

                                        }


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }else{
                    // no emergency contact
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("You have no emergency contact registered, please create one. ")
                            .setCancelable(false)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //System.out.println(firebaseAuth.getCurrentUser().getEmail());
        //TextView txtUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        //txtUserEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        TextView txtUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUserEmail);
        txtUserEmail.setText(firebaseUser.getEmail());

        getFirebaseDatabases();
    }

    public void onResume() {

        super.onResume();

        //creates the sms dialog
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapActivity.this);
        // set title
        alertDialogBuilder.setTitle(R.string.app_name);

        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                incidentResult = "Msg Sent Unknown Error";

                incidentKey = dbUserIncidents.child(firebaseUser.getUid()).push().getKey();

                Incident incident = new Incident();
                incident.setLatitude(mLastKnownLocation.getLatitude());
                incident.setLongitude(mLastKnownLocation.getLongitude());
                incident.setAccuracy(mLastKnownLocation.getAccuracy());
                incident.setUserId(firebaseUser.getUid());
                incident.setIncidentId(incidentKey);

                Map<String, Object> postValues = incident.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(incidentKey, postValues);

                dbUserIncidents.setValue(childUpdates);


                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        incidentResult = "Message Sent Successfully !!";

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        incidentResult = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        incidentResult = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        incidentResult = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        incidentResult = "Error : Radio is off";
                        break;
                    default:
                        break;
                }

            }
        };

        deliveredStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                incidentResult = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        incidentResult = "Your message was delivered successfully";
                        //inform user of successful delivery

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(incidentResult)
                                .setCancelable(false)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }

            }
        };

        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the firebaseUser will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the firebaseUser has
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


    }

    /**
     * Updates the map's UI settings based on whether the firebaseUser has granted location permission.
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
        }

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
        }
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
            ;

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
        refreshMap();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkFirebaseConnection(){
        Log.v(TAG,"checkFirebaseConnection");
        //get firebase authorization instance and current firebaseUser
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //verify if firebaseUser is still active
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // firebaseUser auth state is changed - firebaseUser is null
                    // launch login activity
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            //do nothing
        } else if (id == R.id.nav_user_info) {
            startActivity(new Intent(MapActivity.this,RegistrationActivity.class));

        } else if (id == R.id.nav_medical) {
            startActivity(new Intent(MapActivity.this,MedicalInfoActivity.class));

        } else if (id == R.id.nav_emergency) {
            startActivity(new Intent(MapActivity.this,EmergencyContactsActivity.class));

        } else if (id == R.id.nav_hospitals) {
            startActivity(new Intent(MapActivity.this,HospitalActivity.class));

        } else if (id == R.id.nav_useful) {
            startActivity(new Intent(MapActivity.this,UsefulInformationActivity.class));

        }else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(MapActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getFirebaseDatabases(){
        Log.d(TAG, "getFirebaseDatabases() called");

        //load the required database
        mFirebaseInstance = firebaseDataUtilities.getDatabase();

        dbHospitals = mFirebaseInstance.getReference("hospitals");
        dbHospitals.keepSynced(true);

        dbEmergencyContactInfo = mFirebaseInstance.getReference("users-emergency-contacts/" + firebaseUser.getUid());
        dbEmergencyContactInfo.keepSynced(true);

        dbUserIncidents = mFirebaseInstance.getReference("users-incidents/" + firebaseUser.getUid());
        dbUserIncidents.keepSynced(true);
        //listeners
        dbHospitals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG,"checkFirebaseConnection.onDataChange");
                //clear updated
                updatedLocations = new ArrayList<Hospital>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Hospital location = snapshot.getValue(Hospital.class);
                    System.out.println(location);
                    updatedLocations.add(location);
                }
                initializeDataBind(updatedLocations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbEmergencyContactInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "dbEmergencyContactInfo:onDataChange: ");

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if (snapshot != null){
                        emergencyContact = snapshot.getValue(User.class);

                        if (emergencyContact != null){
                            System.out.println(emergencyContact);

                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void initializeDataBind(List<Hospital> data){
        //deepcopy
        locations = new ArrayList<>();
        locations = (ArrayList<Hospital>) data;
        //ApplicationDataStore.getInstance().setLocations(locations);
        refreshMap();
    }

    public void refreshMap(){
        System.out.println("MapActivity.refreshMap for " + locations.size() + " hospitals");
        // Clearing previous markers
        mMap.clear();
        //drawing firebase locations on map
        for (Hospital location : locations) {
            LatLng marker = new LatLng(location.getLatitude(), location.getLongitude());

            Location markerLocation = new Location("SELF");
            markerLocation.setLatitude(marker.latitude);
            markerLocation.setLongitude(marker.longitude);

            float distanceTo = 0;
            if (mLastKnownLocation != null){

                distanceTo = mLastKnownLocation.distanceTo(markerLocation);
            }

            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(location.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .snippet(distanceTo + "m away"))
                    .setTag(location);
        }
    }
}
