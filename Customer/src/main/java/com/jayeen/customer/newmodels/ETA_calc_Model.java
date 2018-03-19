package com.jayeen.customer.newmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by naveen on 16/10/17.
 */

public class ETA_calc_Model implements Serializable {




    @Expose
    public boolean success;

    @Expose
    public String error;

    @SerializedName("cal_total")
    public String cal_total;

    @SerializedName("total_duration")
    public String total_duration;

    @SerializedName("total_distance")
    public String total_distance;

}
