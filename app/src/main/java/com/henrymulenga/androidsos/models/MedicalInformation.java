package com.henrymulenga.androidsos.models;

import java.util.HashMap;
import java.util.Map;

public class MedicalInformation {
    private String medicalInfoId;
    private  String userId;
    private String preferredHospitalId;
    private String preferredHospitalName;
    private String bloodType;
    private String allergies;

    public MedicalInformation() {
    }

    public String getMedicalInfoId() {
        return medicalInfoId;
    }

    public void setMedicalInfoId(String medicalInfoId) {
        this.medicalInfoId = medicalInfoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPreferredHospitalId() {
        return preferredHospitalId;
    }

    public void setPreferredHospitalId(String preferredHospitalId) {
        this.preferredHospitalId = preferredHospitalId;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getPreferredHospitalName() {
        return preferredHospitalName;
    }

    public void setPreferredHospitalName(String preferredHospitalName) {
        this.preferredHospitalName = preferredHospitalName;
    }

    @Override
    public String toString() {
        return "MedicalInformation{" +
                "medicalInfoId='" + medicalInfoId + '\'' +
                ", userId='" + userId + '\'' +
                ", preferredHospitalId='" + preferredHospitalId + '\'' +
                ", preferredHospitalName='" + preferredHospitalName + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", allergies='" + allergies + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("medicalInfoId",medicalInfoId);
        result.put("preferredHospitalId",preferredHospitalId);
        result.put("preferredHospitalName",preferredHospitalName);
        result.put("bloodType",bloodType);
        result.put("allergies",allergies);
        result.put("userId",userId);
        return result;
    }
}
