package com.henrymulenga.androidsos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import com.henrymulenga.androidsos.adapters.UsefulNumbersListAdapter;
import com.henrymulenga.androidsos.models.UsefulEmergencyNumber;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;

import java.util.ArrayList;
import java.util.List;

public class UsefulInformationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = UsefulInformationActivity.class.getSimpleName();

    // Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbUsefulNumbers;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();

    //Useful Numbers
    private UsefulNumbersListAdapter arrayAdapter;
    private List<UsefulEmergencyNumber> locations = new ArrayList<UsefulEmergencyNumber>();
    private List<UsefulEmergencyNumber> updatedNumbers = new ArrayList<UsefulEmergencyNumber>();
    private ListView listView;
    private UsefulEmergencyNumber usefulEmergencyNumber;


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
                // get the usefule number selected by the user
                usefulEmergencyNumber = new UsefulEmergencyNumber();
                usefulEmergencyNumber = locations.get(position);

                //inform user of save
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        UsefulInformationActivity.this);
                // set title
                alertDialogBuilder.setTitle(R.string.app_name);
                // set dialog message
                alertDialogBuilder
                        .setMessage("Would you like to call " + usefulEmergencyNumber.getName())
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + usefulEmergencyNumber.getNumber()));
                                if (ActivityCompat.checkSelfPermission(UsefulInformationActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                startActivity(intent);
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

    private void initializeAdapter(List<UsefulEmergencyNumber> data){

        //deepcopy
        locations = new ArrayList<>();
        locations = (ArrayList<UsefulEmergencyNumber>) data;

        // reinitialize and set the list adapter
        arrayAdapter = new UsefulNumbersListAdapter(getApplicationContext(), locations);
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
                    startActivity(new Intent(UsefulInformationActivity.this, LoginActivity.class));
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
            startActivity(new Intent(UsefulInformationActivity.this,MapActivity.class));
            finish();
        } else if (id == R.id.nav_user_info) {
            startActivity(new Intent(UsefulInformationActivity.this,RegistrationActivity.class));
            finish();
        } else if (id == R.id.nav_medical) {
            startActivity(new Intent(UsefulInformationActivity.this,MedicalInfoActivity.class));
            finish();
        } else if (id == R.id.nav_emergency) {
            startActivity(new Intent(UsefulInformationActivity.this,EmergencyContactsActivity.class));
            finish();
        } else if (id == R.id.nav_hospitals) {
            startActivity(new Intent(UsefulInformationActivity.this,HospitalActivity.class));
            finish();
        } else if (id == R.id.nav_useful) {
            //do nothing
        } else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(UsefulInformationActivity.this, LoginActivity.class));
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

        dbUsefulNumbers = mFirebaseInstance.getReference("useful-numbers");
        dbUsefulNumbers.keepSynced(true);

        dbUsefulNumbers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG,"checkFirebaseConnection.onDataChange");
                //clear updated
                updatedNumbers = new ArrayList<UsefulEmergencyNumber>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    UsefulEmergencyNumber number = snapshot.getValue(UsefulEmergencyNumber.class);
                    System.out.println(number);
                    updatedNumbers.add(number);
                }
                initializeAdapter(updatedNumbers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
