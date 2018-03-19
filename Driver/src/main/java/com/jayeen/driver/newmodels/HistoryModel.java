
package com.jayeen.driver.newmodels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class HistoryModel {

    @SerializedName("requests")
    public List<Request> mRequests;

    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("error_code")
    public int error_code;
    @SerializedName("error")
    public String error;

}
