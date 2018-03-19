package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/28/2016.
 */

public class LoginModel implements Serializable {

    @SerializedName("success")
    public Boolean success;

    @SerializedName("error")
    public String erro;

    @SerializedName("error_code")
    public Integer errorCode;

    @SerializedName("is_active")
    public Integer is_active;

    @SerializedName("driver")
    public DriverModel driver;

    @SerializedName("wallet")
    public WalletModel wallet;
}
