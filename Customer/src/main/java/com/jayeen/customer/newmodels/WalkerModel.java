package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class WalkerModel implements Serializable {

    @SerializedName("id")
    public Integer id;

    @SerializedName("current")
    public List<CurrentLatLongModel> current;

    @SerializedName("bearing")
    public Integer bearing;

    @SerializedName("type")
    public Integer type;

    @SerializedName("is_near")
    public Boolean isNear;
}
