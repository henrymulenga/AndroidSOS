package com.henrymulenga.androidsos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.henrymulenga.androidsos.adapters.HospitalListAdapter;
import com.henrymulenga.androidsos.models.Hospital;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;

import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = HospitalActivity.class.getSimpleName();

    // Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbHospitals;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();

    //Hospitals
    private HospitalListAdapter arrayAdapter;
    private List<Hospital> locations = new ArrayList<Hospital>();
    private List<Hospital> updatedLocations = new ArrayList<Hospital>();
    private ListView listView;
    private Hospital selectedHospital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list);

        checkFirebaseConnection();
        getFirebaseDatabases();

        // initialization of the list
        initializeAdapter(locations);

        //List Listener

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the hosptial selected by the user
                selectedHospital = new Hospital();
                selectedHospital = locations.get(position);

                //inform user of save
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HospitalActivity.this);
                // set title
                alertDialogBuilder.setTitle(R.string.app_name);
                // set dialog message
                alertDialogBuilder
                        .setMessage("Would you like to make " + selectedHospital.getName() + " your preferred hospital.")
                        .setCancelable(false)
                        .setPositiveButton("Proceed",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                startActivity(new Intent(HospitalActivity.this,MapActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Return",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
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
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void initializeAdapter(List<Hospital> data){

        //deepcopy
        locations = new ArrayList<>();
        locations = (ArrayList<Hospital>) data;

        // reinitialize and set the list adapter
        arrayAdapter = new HospitalListAdapter(getApplicationContext(), locations);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);
        arrayAdapter.notifyDataSetChanged();
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
                    startActivity(new Intent(HospitalActivity.this, LoginActivity.class));
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
            startActivity(new Intent(HospitalActivity.this,MapActivity.class));
            finish();
        } else if (id == R.id.nav_user_info) {
            startActivity(new Intent(HospitalActivity.this,RegistrationActivity.class));
            finish();
        } else if (id == R.id.nav_medical) {
            startActivity(new Intent(HospitalActivity.this,MedicalInfoActivity.class));
            finish();
        } else if (id == R.id.nav_emergency) {
            startActivity(new Intent(HospitalActivity.this,EmergencyContactsActivity.class));
            finish();
        } else if (id == R.id.nav_hospitals) {
            //do nothing
        } else if (id == R.id.nav_useful) {

        } else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(HospitalActivity.this, LoginActivity.class));
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
                initializeAdapter(updatedLocations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
