package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class WalkerSuccessModel implements Serializable {
    @SerializedName("success")
    public Boolean success;

    @SerializedName("walkers")
    public List<CurrentLatLongModel> walkers = null;

    @SerializedName("error")
    public Boolean error;

    @SerializedName("error_code")
    public Integer error_code;


}

