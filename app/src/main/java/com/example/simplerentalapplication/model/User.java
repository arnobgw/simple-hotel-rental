package com.example.simplerentalapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("userName")
    public String userName;
    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public User(String userName, String name, String email, String password) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}