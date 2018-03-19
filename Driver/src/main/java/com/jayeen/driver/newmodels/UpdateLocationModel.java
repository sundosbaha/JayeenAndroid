package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pandian on 27/4/17.
 */

public class UpdateLocationModel implements Serializable {

    @SerializedName("is_active")
    public Integer mIsActive;
    @SerializedName("is_active_txt")
    public String mIsActiveTxt;
    @SerializedName("is_approved")
    public Integer mIsApproved;
    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("dest_latitude")
    public Double mDestLatitude;
    @SerializedName("dest_longitude")
    public Double mDestLongitude;
    @SerializedName("distance")
    public float mDistance;
    @SerializedName("is_cancelled")
    public Integer mIsCancelled;
    @SerializedName("payment_type")
    public Integer mPaymentType;
    @SerializedName("time")
    public Double mTime;
    @SerializedName("unit")
    public String mUnit;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

}
