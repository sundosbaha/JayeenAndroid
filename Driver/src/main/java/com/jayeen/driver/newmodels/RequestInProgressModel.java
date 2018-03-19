
package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

public class RequestInProgressModel {

    @SerializedName("is_approved")
    public Integer mIsApproved;
    @SerializedName("is_approved_txt")
    public String mIsApprovedTxt;
    @SerializedName("is_available")
    public Integer mIsAvailable;
    @SerializedName("request_id")
    public Integer mRequestId;
    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("error")
    public String error;
    @SerializedName("error_code")
    public Integer error_code;

}
