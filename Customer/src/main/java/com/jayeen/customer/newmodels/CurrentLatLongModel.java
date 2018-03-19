package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class CurrentLatLongModel implements Serializable {

    @SerializedName("latitude")
    public Float latitude;

    @SerializedName("longitude")
    public Float longitude;
}
