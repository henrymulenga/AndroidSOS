package com.henrymulenga.androidsos.models;

public class MedicalInformation {
    private String medicalInfoId;
    private  String userId;
    private String preferredHospitalId;
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

    @Override
    public String toString() {
        return "MedicalInformation{" +
                "medicalInfoId='" + medicalInfoId + '\'' +
                ", userId='" + userId + '\'' +
                ", preferredHospitalId='" + preferredHospitalId + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", allergies='" + allergies + '\'' +
                '}';
    }
}
