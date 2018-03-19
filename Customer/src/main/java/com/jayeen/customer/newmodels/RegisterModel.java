package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/21/2016.
 */

public class RegisterModel implements Serializable {

    @SerializedName("success")
    public Boolean success;

    @SerializedName("error")
    public String erro;

    @SerializedName("error_code")
    public Integer errorCode;

    @SerializedName("user")
    public UserModel user;
}
