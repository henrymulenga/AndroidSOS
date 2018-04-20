package com.henrymulenga.androidsos.models;

public class UsefulEmergencyNumber {
    private String usefulEmergencyNumber;
    private String name;
    private String number;
    private String country;

    public UsefulEmergencyNumber() {
    }

    public String getUsefulEmergencyNumber() {
        return usefulEmergencyNumber;
    }

    public void setUsefulEmergencyNumber(String usefulEmergencyNumber) {
        this.usefulEmergencyNumber = usefulEmergencyNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "UsefulEmergencyNumber{" +
                "usefulEmergencyNumber='" + usefulEmergencyNumber + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
