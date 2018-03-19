package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class WalkerDetail implements Serializable {
    @SerializedName("unique_id")
    public Integer uniqueId;

    @SerializedName("id")
    public Integer id;
    @SerializedName("bearing")
    public String bearing;
    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("picture")
    public String picture;

    @SerializedName("bio")
    public String bio;

    @SerializedName("latitude")
    public Float latitude;

    @SerializedName("longitude")
    public Float longitude;

    @SerializedName("type")
    public Integer type;

    @SerializedName("car_model")
    public String carModel;

    @SerializedName("car_number")
    public String carNumber;

    @SerializedName("rating")
    public Float rating;

    @SerializedName("num_rating")
    public Integer numRating;
}
