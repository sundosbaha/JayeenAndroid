package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/28/2016.
 */
@DatabaseTable(tableName = "User")
public class DriverModel implements Serializable {

    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public String error;

    @SerializedName("error_code")
    public String error_code;


    @SerializedName("login_by")
    public String login_by;

    @SerializedName("social_unique_id")
    public String social_unique_id;


    @DatabaseField(columnName = "rowid", generatedId = true, canBeNull = false) //id denotes this field as primary key
    private int rowId;

    @DatabaseField(columnName = "user_id", canBeNull = false)
    @SerializedName("id")
    public Integer id;

    @DatabaseField(columnName = "first_name", canBeNull = false)
    @SerializedName("first_name")
    public String firstName;

    @DatabaseField(columnName = "last_name", canBeNull = false)
    @SerializedName("last_name")
    public String lastName;

    @DatabaseField(columnName = "contact", canBeNull = false)
    @SerializedName("phone")
    public String phone;

    @DatabaseField(columnName = "email", canBeNull = false)
    @SerializedName("email")
    public String email;


    @DatabaseField(columnName = "picture", canBeNull = false)
    @SerializedName("picture")
    public String picture;

    @DatabaseField(columnName = "bio")
    @SerializedName("bio")
    public String bio;

    @DatabaseField(columnName = "address")
    @SerializedName("address")
    public String address;


    @SerializedName("state")
    public String state;

    @SerializedName("country")
    public String country;

    @DatabaseField(columnName = "zipcode")
    @SerializedName("zipcode")
    public String zipcode;


    @DatabaseField(columnName = "deviceToken", canBeNull = false)
    @SerializedName("device_token")
    public String deviceToken;

    @SerializedName("device_type")
    public String deviceType;

    @SerializedName("is_approved")
    public Boolean isApproved;
    @DatabaseField(columnName = "car_model", canBeNull = false)
    @SerializedName("car_model")
    public String carModel;
    @DatabaseField(columnName = "car_number", canBeNull = false)
    @SerializedName("car_number")
    public String carNumber;

    @SerializedName("is_approved_txt")
    public String isApprovedTxt;

    @SerializedName("is_available")
    public Boolean isAvailable;

    @SerializedName("timezone")
    public String timezone;

    @SerializedName("type")
    public Integer type;

    @DatabaseField(columnName = "token", canBeNull = false)
    @SerializedName("token")
    public String token;

}
