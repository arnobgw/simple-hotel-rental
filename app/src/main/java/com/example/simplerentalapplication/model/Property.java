package com.example.simplerentalapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Property implements Serializable {

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("propertyType")
    public String propertyType;
    @SerializedName("leaseType")
    public String leaseType;
    @SerializedName("address")
    public String address;
    @SerializedName("contactPerson")
    public String contactPerson;
    @SerializedName("contactPhone")
    public String contactPhone;
    @SerializedName("userName")
    public String userName;
    @SerializedName("leaseStartDate")
    public long leaseStartDate;
    @SerializedName("leaseEndDate")
    public long leaseEndDate;
    @SerializedName("cost")
    public String cost;
    @SerializedName("timeUnit")
    public String timeUnit;
    @SerializedName("pictureUrls")
    public List<String> pictureUrls;

    public Property(){}

    public Property(String name, String propertyType, String leaseType, String address, String contactPerson, String contactPhone, String cost, String timeUnit, List<String> pictureUrls) {
        this.name = name;
        this.propertyType = propertyType;
        this.leaseType = leaseType;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.cost = cost;
        this.timeUnit = timeUnit;
        this.pictureUrls = pictureUrls;
    }
}
