package com.henrymulenga.androidsos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.henrymulenga.androidsos.models.User;
import com.henrymulenga.androidsos.utilities.FirebaseDataUtilities;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Debugging TAG for Log
    private static final String TAG = RegistrationActivity.class.getSimpleName();

    // UI references
    private Spinner spinner;
    private View mUserInfoView;
    private Button btnSaveUserInfo;
    private EditText etFirstName, etLastName,etEmail, etPhone, etAge;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbUserInfo;
    FirebaseDataUtilities firebaseDataUtilities = new FirebaseDataUtilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkFirebaseConnection();
        //firebaseAuth = FirebaseAuth.getInstance();
        //System.out.println(firebaseAuth.getCurrentUser().getEmail());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUserEmail);
        txtUserEmail.setText(firebaseUser.getEmail());

        spinner = (Spinner) findViewById(R.id.spinner_gender);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.gender_array,
                        android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(staticAdapter);

        etFirstName = (EditText) findViewById(R.id.text_firstname);
        etLastName = (EditText) findViewById(R.id.text_lastname);
        etEmail = (EditText) findViewById(R.id.text_email);
        etPhone = (EditText) findViewById(R.id.text_phone_number);
        etAge = (EditText) findViewById(R.id.text_age);

        btnSaveUserInfo = (Button) findViewById(R.id.button_save_user_info);
        btnSaveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
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
            startActivity(new Intent(RegistrationActivity.this,MapActivity.class));
            finish();
        } else if (id == R.id.nav_user_info) {
            //do nothing

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_exit) {
            auth.signOut();
            // launch login activity
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Attempts to save the firebaseUser details specified on the form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual save attempt is made.
     */
    private void attemptSaveUserDetails() {

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
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void getFirebaseDatabases(){
        Log.d(TAG, "getFirebaseDatabases() called");

        //load the required database
        mFirebaseInstance = firebaseDataUtilities.getDatabase();

        dbUserInfo = mFirebaseInstance.getReference("users/" + firebaseUser.getUid());
        dbUserInfo.keepSynced(true);

        //listeners
        dbUserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "dbUserInfo:onDataChange: ");
                //clear updated devices
                //updatedUserInfo = new ArrayList<User>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if (snapshot != null){
                        User user = snapshot.getValue(User.class);

                        if (user != null){
                            System.out.println(user);
                            //update the text fields
                            etFirstName.setText(user.getFirstName());
                            etLastName.setText(user.getFirstName());
                            etAge.setText(String.valueOf(user.getAge()));
                            etEmail.setText(user.getEmailAddress());
                            etPhone.setText(user.getPhoneNumber());
                            spinner.setSelection(getIndex(spinner, user.getGender()));
                            etFirstName.setText(user.getFirstName());
                        }
                    }


                    //updatedUserDevices.add(firebaseUser);
                }

                //System.out.println("User Devices Size : " +updatedUserDevices.size()  );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //to get the spinner set to correct gender
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }


    private void updateUserInfo(){
        // Reset errors.
        etEmail.setError(null);

        // Store values at the time of the login attempt.
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String eMail = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        int age = Integer.parseInt(etAge.getText().toString());

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
            // Show a progress spinner, and kick off a background task to
            // perform the firebaseUser login attempt.
            //showProgress(true);

            //trashedProduct.setFirebaseKey(key);
            //create firebaseUser
            User user = new User();
            user.setUserId(firebaseUser.getUid());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phone);
            user.setEmailAddress(eMail);
            user.setAge(age);
            user.setGender(spinner.getSelectedItem().toString());
            System.out.println("Saving " + user);

            String key = dbUserInfo.child(firebaseUser.getUid()).push().getKey();
            //dbUserInfo.setValue(user);
            Map<String, Object> postValues = user.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, postValues);
            //dbUserInfo.updateChildren(childUpdates);
            dbUserInfo.setValue(childUpdates);
            //Log.v(TAG, trashedProduct.getGtin() + " added to Cart");
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
