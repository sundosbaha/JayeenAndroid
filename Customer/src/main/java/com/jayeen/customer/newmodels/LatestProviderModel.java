
package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LatestProviderModel implements Serializable{

    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("walkers")
    public List<Walker> mWalkers=null;
    @SerializedName("error")
    public Boolean error;
    @SerializedName("error_code")
    public Boolean error_code;
    public class Current implements Serializable{
        @SerializedName("latitude")
        public Double mLatitude;
        @SerializedName("longitude")
        public Double mLongitude;
    }

    public class Walker implements Serializable{
        @SerializedName("bearing")
        public Long mBearing;
        @SerializedName("current")
        public Current mCurrent;
        @SerializedName("id")
        public Integer mId;
        @SerializedName("is_near")
        public Boolean mIsNear;
        @SerializedName("type")
        public Integer mType;
//        @SerializedName("is_selected")
//        public Long is_selected;
    }
}
