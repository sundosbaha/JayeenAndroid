
package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

public class RequestLocationModel {

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
    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("time")
    public Double mTime;
    @SerializedName("unit")
    public String mUnit;


    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

}
