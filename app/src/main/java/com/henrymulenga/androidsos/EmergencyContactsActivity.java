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
import com.henrymulenga.androidsos.models.EmergencyContact;
import com.henrymulenga.androidsos.models.User;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContactsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = EmergencyContactsActivity.class.getSimpleName();

    // UI references
    private View mUserInfoView;
    private Button btnSaveUserInfo;
    private EditText etFirstName, etLastName,etEmail, etPhone;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbEmergencyContactInfo;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
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

        etFirstName = (EditText) findViewById(R.id.text_firstname);
        etLastName = (EditText) findViewById(R.id.text_lastname);
        etEmail = (EditText) findViewById(R.id.text_email);
        etPhone = (EditText) findViewById(R.id.text_phone_number);

        btnSaveUserInfo = (Button) findViewById(R.id.button_save_emergency_info);
        btnSaveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmergencyContactInfo();
            }
        });

        getFirebaseDatabases();

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
            startActivity(new Intent(EmergencyContactsActivity.this,MapActivity.class));
            finish();
        } else if (id == R.id.nav_user_info) {
            startActivity(new Intent(EmergencyContactsActivity.this,RegistrationActivity.class));
            finish();
        } else if (id == R.id.nav_medical) {

        }else if (id == R.id.nav_emergency) {
            //do nothing
        } else if (id == R.id.nav_hospitals) {

        } else if (id == R.id.nav_useful) {

        } else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(EmergencyContactsActivity.this, LoginActivity.class));
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
                    startActivity(new Intent(EmergencyContactsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void getFirebaseDatabases(){
        Log.d(TAG, "getFirebaseDatabases() called");

        //load the required database
        mFirebaseInstance = firebaseDataUtilities.getDatabase();

        dbEmergencyContactInfo = mFirebaseInstance.getReference("users-emergency-contacts/" + firebaseUser.getUid());
        dbEmergencyContactInfo.keepSynced(true);

        //listeners
        dbEmergencyContactInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "dbEmergencyContactInfo:onDataChange: ");

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if (snapshot != null){
                        User user = snapshot.getValue(User.class);

                        if (user != null){
                            System.out.println(user);
                            //update the text fields
                            etFirstName.setText(user.getFirstName());
                            etLastName.setText(user.getLastName());
                            etEmail.setText(user.getEmailAddress());
                            etPhone.setText(user.getPhoneNumber());
                            etFirstName.setText(user.getFirstName());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateEmergencyContactInfo(){
        // Reset errors.
        etEmail.setError(null);

        // Store values at the time of the login attempt.
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String eMail = etEmail.getText().toString();
        String phone = etPhone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(eMail)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(eMail)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            EmergencyContact emergencyContact = new EmergencyContact();
            //emergencyContact.setUserId(firebaseUser.getUid());
            emergencyContact.setFirstName(firstName);
            emergencyContact.setLastName(lastName);
            emergencyContact.setPhoneNumber(phone);
            emergencyContact.setEmailAddress(eMail);
            emergencyContact.setUserId(firebaseUser.getUid());
            System.out.println("Saving " + emergencyContact);

            String key = dbEmergencyContactInfo.child(firebaseUser.getUid()).push().getKey();

            Map<String, Object> postValues = emergencyContact.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, postValues);

            dbEmergencyContactInfo.setValue(childUpdates);

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
                            startActivity(new Intent(EmergencyContactsActivity.this,MapActivity.class));
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
