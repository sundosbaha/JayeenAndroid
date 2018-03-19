package com.jayeen.customer.newmodels;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/26/2016.
 */

public class RequestLocation implements Serializable {

//    {"success":true,"latitude":11.01456605,"longitude":76.98154838,"bearing":0,"distance":"0","time":0,"unit":"kms"}
    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("bearing")
    public Double bearing;

    @SerializedName("distance")
    public String distance;

    @SerializedName("time")
    public String time;

    @SerializedName("unit")
    public String unit;

    public LatLng latlong(){
       return new LatLng(latitude,longitude);
    }
}
