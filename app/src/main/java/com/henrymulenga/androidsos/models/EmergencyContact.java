package com.henrymulenga.androidsos.models;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContact {
    private String contactId;
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String phoneNumber;
    private String userId;

    public EmergencyContact() {
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "contactId='" + contactId + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("contactId",contactId);
        result.put("lastName",lastName);
        result.put("firstName",firstName);
        result.put("emailAddress",emailAddress);
        result.put("phoneNumber",phoneNumber);
        result.put("userId",userId);
        return result;
    }
}
