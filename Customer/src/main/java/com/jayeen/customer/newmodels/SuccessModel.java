package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;
import com.jayeen.customer.models.ApplicationPages;
import com.jayeen.customer.utils.Const;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev-Kalai on 12/26/2016.
 */

public class SuccessModel implements Serializable {
    @SerializedName("success")
    public Boolean success;

    @SerializedName("error")
    public String error;

    @SerializedName("error_code")
    public String error_code;

    @SerializedName(Const.Params.INFORMATIONS)
    public List<ApplicationPages> applicationPages;

}
