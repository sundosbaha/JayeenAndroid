package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/24/2016.
 */

public class RequestProgressModel implements Serializable {
    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("request_id")
    public Integer requestId;
}
