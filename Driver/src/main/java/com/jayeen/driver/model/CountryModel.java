package com.jayeen.driver.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryModel implements Serializable{
    @SerializedName("name")
    public String name;
    @SerializedName("alpha-2")
    public String alpha_2;
    @SerializedName("country-code")
    public String country_code;
    @SerializedName("phone-code")
    public String phone_code;


    @Override
    public String toString() {
        return phone_code + " " + name;
    }
}