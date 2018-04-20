package com.henrymulenga.androidsos.models;

import java.sql.Timestamp;
import java.util.List;

public class Incident {
    private String incidentId;
    public double latitude;
    public double longitude;
    private double accuracy;
    private Timestamp timestamp;
    public String time;
    private String userId;
    private List<NotificationContactRequest> notificationContactRequests;
    private boolean active;
    private boolean falseAlarm;

    public Incident() {
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<NotificationContactRequest> getNotificationContactRequests() {
        return notificationContactRequests;
    }

    public void setNotificationContactRequests(List<NotificationContactRequest> notificationContactRequests) {
        this.notificationContactRequests = notificationContactRequests;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFalseAlarm() {
        return falseAlarm;
    }

    public void setFalseAlarm(boolean falseAlarm) {
        this.falseAlarm = falseAlarm;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "incidentId='" + incidentId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", timestamp=" + timestamp +
                ", time='" + time + '\'' +
                ", userId='" + userId + '\'' +
                ", notificationContactRequests=" + notificationContactRequests +
                ", active=" + active +
                ", falseAlarm=" + falseAlarm +
                '}';
    }
}
