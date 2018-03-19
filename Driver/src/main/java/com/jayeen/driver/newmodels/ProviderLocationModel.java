
package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

public class ProviderLocationModel {

    @SerializedName("is_active")
    public Integer mIsActive;
    @SerializedName("is_active_txt")
    public String mIsActiveTxt;
    @SerializedName("is_approved")
    public Integer mIsApproved;
    @SerializedName("success")
    public Boolean mSuccess;


    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

}
