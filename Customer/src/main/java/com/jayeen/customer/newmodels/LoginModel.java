package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mukesh on 12/20/16.
 */

public class LoginModel implements Serializable {

    @SerializedName("clientToken")
    public String clientToken;

    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("user")
    public UserModel user;

}