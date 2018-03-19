package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pandian on 20/3/17.
 */

public class InstantJobModel implements Serializable {
    @SerializedName("success")
    public Boolean success;
    @SerializedName("requests")
    public Requests requests;

    public class Requests {
        @SerializedName("id")
        public Integer id;
    }
    @SerializedName("error_code")
    public Integer error_code;
    @SerializedName("error")
    public String error;
}