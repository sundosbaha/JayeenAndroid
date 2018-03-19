package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/24/2016.
 */

public class ProfileUpdatedSuccess implements Serializable {
    @SerializedName("success")
    public Boolean success;

    @SerializedName("user")
    public UserModel user;
    @SerializedName("error")
    public String error;

    @SerializedName("error_code")
    public String error_code;

}
