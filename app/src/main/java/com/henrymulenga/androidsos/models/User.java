package com.henrymulenga.androidsos.models;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String phoneNumber;
    private Integer age;
    private String gender;
    public double latitude;
    public double longitude;
    private double accuracy;
    public String timeLastSeen;

    public User() {
        accuracy = 0.00;
        latitude = 0.00;
        longitude = 0.00;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timeLastSeen = timestamp.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getTime() {
        return timeLastSeen;
    }

    public void setTime(String time) {
        this.timeLastSeen = time;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
               // ", latitude=" + latitude +
               // ", longitude=" + longitude +
                ///", accuracy=" + accuracy +
                ", timeLastSeen='" + timeLastSeen + '\'' +
                '}';
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("lastName",lastName);
        result.put("firstName",firstName);
        result.put("emailAddress",emailAddress);
        result.put("phoneNumber",phoneNumber);
        result.put("age",age);
        result.put("timeLastSeen",timeLastSeen);
        result.put("gender",gender);
        result.put("latitude",latitude);
        result.put("longitude",longitude);
        result.put("accuracy",accuracy);
        return result;
    }
}

