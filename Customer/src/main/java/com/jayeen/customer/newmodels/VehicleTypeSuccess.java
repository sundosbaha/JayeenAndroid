package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class VehicleTypeSuccess implements Serializable {
    @SerializedName("success")

    public Boolean success;

    @SerializedName("error")

    public String error;
    @SerializedName("error_code")

    public int error_code;
    @SerializedName("types")

    public ArrayList<VehicleTypeModel> types = null;

}
