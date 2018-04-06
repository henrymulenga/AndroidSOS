package com.henrymulenga.androidsos.utilities;

import android.graphics.Bitmap;

import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.Marker;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henry Mulenga.
 */

public class ApplicationDataStore {

    private static ApplicationDataStore _selfInstance = null;
    private LocationSettingsResult locationSettingsResult;
    private Bitmap screenshot;
    private Marker marker;
    //private CsgLocation selectedLocation;
    //public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    //private List<CsgLocation> locations = new ArrayList<CsgLocation>();
    //private List<CsgLocation> userLocations = new ArrayList<CsgLocation>();
    //private List<CsgUserLocation> userPositions = new ArrayList<CsgUserLocation>();
    private int mapRange;

    /**
     * Private constructor to prevent further instantiation
     */
    private ApplicationDataStore() {
        screenshot = null;
        marker = null;
        //selectedLocation = null;
        /*locations = new ArrayList<CsgLocation>();
        userLocations = new ArrayList<CsgLocation>();
        userPositions = new ArrayList<CsgUserLocation>();*/
        mapRange = 300;
        locationSettingsResult = null;
    }
    /**
     * Factory method to get the instance of this class. This method ensures
     * that this class will have one and only one instance at any point of
     * time. This is the only way to get the instance of this class. No other
     * way will be made available to the programmer to instantiate this class.
     *
     * @return the object of this class.
     */
    public static ApplicationDataStore getInstance() {
        if (_selfInstance == null) {
            _selfInstance = new ApplicationDataStore();
        }
        return _selfInstance;
    }

    public Bitmap getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(Bitmap screenshot) {
        this.screenshot = screenshot;
    }

    public Marker getMarker() {
        return marker;
    }

    /*public CsgLocation getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(CsgLocation selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public List<CsgLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<CsgLocation> locations) {
        this.locations = locations;
    }

    public List<CsgLocation> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(List<CsgLocation> userLocations) {
        this.userLocations = userLocations;
    }

    public void addUserLocation(CsgLocation location){
        this.userLocations.add(location);
    }

    public List<CsgUserLocation> getUserPositions() {
        return userPositions;
    }

    public void setUserPositions(List<CsgUserLocation> userPositions) {
        this.userPositions = userPositions;
    }*/

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public int getMapRange() {
        return mapRange;
    }

    public void setMapRange(int mapRange) {
        this.mapRange = mapRange;
    }

    public LocationSettingsResult getLocationSettingResult() {
        return locationSettingsResult;
    }

    public void setLocationSettingsResult(LocationSettingsResult locationSettingsResult) {
        this.locationSettingsResult = locationSettingsResult;
    }

}
