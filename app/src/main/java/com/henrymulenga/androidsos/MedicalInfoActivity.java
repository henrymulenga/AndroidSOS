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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.henrymulenga.androidsos.models.Hospital;
import com.henrymulenga.androidsos.models.MedicalInformation;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = MedicalInfoActivity.class.getSimpleName();

    // UI references
    private View mUserInfoView;
    private Button btnSaveMedicalInfo;
    private EditText etAllergies;
    private Spinner spinnerHospitals, spinnerBloodTypes;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbMedicalInfo, dbHospitals;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();


    private List<Hospital> locations = new ArrayList<Hospital>();
    private List<Hospital> updatedLocations = new ArrayList<Hospital>();
    private MedicalInformation medicalInformation = new MedicalInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkFirebaseConnection();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUserEmail);
        txtUserEmail.setText(firebaseUser.getEmail());

        /*etHospitalName = (EditText) findViewById(R.id.text_preferred_hospital);
        etBloodType = (EditText) findViewById(R.id.text_blood_type);*/

        etAllergies = (EditText) findViewById(R.id.text_allergies);
        spinnerHospitals = (Spinner)findViewById(R.id.spinner_hospitals);

        spinnerBloodTypes = (Spinner)findViewById(R.id.spinner_blood_types);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.blood_type_array,
                        android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerBloodTypes.setAdapter(staticAdapter);


        btnSaveMedicalInfo = (Button) findViewById(R.id.button_save_medical_info);
        btnSaveMedicalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMedicalInfo();
            }
        });

        getFirebaseDatabases();

        initializeHospitalSpinner(locations);

        //set a listener for the hospital selected
        /*spinnerHospitals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                etHospitalName.setText(locations.get(index).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/

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
            startActivity(new Intent(MedicalInfoActivity.this,MapActivity.class));
            finish();
        } else if (id == R.id.nav_user_info) {
            startActivity(new Intent(MedicalInfoActivity.this,RegistrationActivity.class));
            finish();
        } else if (id == R.id.nav_medical) {
            //do nothing
        }else if (id == R.id.nav_emergency) {
            startActivity(new Intent(MedicalInfoActivity.this,EmergencyContactsActivity.class));
            finish();
        } else if (id == R.id.nav_hospitals) {
            startActivity(new Intent(MedicalInfoActivity.this,HospitalActivity.class));
            finish();
        } else if (id == R.id.nav_useful) {

        } else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(MedicalInfoActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    startActivity(new Intent(MedicalInfoActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void getFirebaseDatabases(){
        Log.d(TAG, "getFirebaseDatabases() called");

        //load the required database
        mFirebaseInstance = firebaseDataUtilities.getDatabase();

        dbMedicalInfo = mFirebaseInstance.getReference("users-medical-info/" + firebaseUser.getUid());
        dbMedicalInfo.keepSynced(true);

        dbHospitals = mFirebaseInstance.getReference("hospitals");
        dbHospitals.keepSynced(true);

        //listeners
        dbMedicalInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "dbMedicalInfo:onDataChange: ");

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if (snapshot != null){
                        MedicalInformation medicalInformation = snapshot.getValue(MedicalInformation.class);

                        if (medicalInformation != null){
                            System.out.println(medicalInformation);
                            //update the text fields
                            etAllergies.setText(medicalInformation.getAllergies());
                            spinnerHospitals.setSelection(getIndex(spinnerHospitals, medicalInformation.getPreferredHospitalName()));
                            spinnerBloodTypes.setSelection(getIndex(spinnerBloodTypes, medicalInformation.getBloodType()));

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                initializeHospitalSpinner(updatedLocations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //to get the spinner set to correct value
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public void initializeHospitalSpinner(List<Hospital> data){
        //deepcopy
        locations = new ArrayList<>();
        locations = (ArrayList<Hospital>) data;

        List<String> list = new ArrayList<String>();

        for (Hospital hospital: locations){
            list.add(hospital.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospitals.setAdapter(dataAdapter);

        spinnerHospitals.setSelection(getIndex(spinnerHospitals, medicalInformation.getPreferredHospitalName()));


    }

    private void updateMedicalInfo(){
        // Reset errors.
        etAllergies.setError(null);

        // Store values at the time of the login attempt.
        String allergies = etAllergies.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(allergies)) {
            etAllergies.setError(getString(R.string.error_field_required));
            focusView = etAllergies;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            medicalInformation = new MedicalInformation();

            medicalInformation.setPreferredHospitalName(spinnerHospitals.getSelectedItem().toString());
            medicalInformation.setBloodType(spinnerBloodTypes.getSelectedItem().toString());

            medicalInformation.setAllergies(allergies);
            medicalInformation.setUserId(firebaseUser.getUid());
            System.out.println("Saving " + medicalInformation);

            String key = dbMedicalInfo.child(firebaseUser.getUid()).push().getKey();

            Map<String, Object> postValues = medicalInformation.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, postValues);

            dbMedicalInfo.setValue(childUpdates);

            //inform user of save
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            // set title
            alertDialogBuilder.setTitle(R.string.app_name);
            // set dialog message
            alertDialogBuilder
                    .setMessage(R.string.dialog_details_updated)
                    .setCancelable(false)
                    .setPositiveButton("Proceed",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            startActivity(new Intent(MedicalInfoActivity.this,MapActivity.class));
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

    }
}
