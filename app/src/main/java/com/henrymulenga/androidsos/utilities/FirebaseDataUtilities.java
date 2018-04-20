package com.henrymulenga.androidsos.utilities;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Henry Mulenga.
 *
 * This class manages the connection to the Firebase database
 */

public class FirebaseDataUtilities {

    private static FirebaseDatabase mFirebaseInstance;

    public FirebaseDatabase getDatabase(){
        if (mFirebaseInstance  == null){
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseInstance.setPersistenceEnabled(true);
        }
        return mFirebaseInstance;
    }
}
