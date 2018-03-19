package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mukesh on 1/3/17.
 */

public class IncomingRequest implements Serializable {
    @SerializedName("datetime")
    public String mDatetime;
    @SerializedName("later")
    public Integer mLater;
    @SerializedName("request_data")
    public RequestData mRequestData;
    @SerializedName("request_id")
    public Integer mRequestId;
    @SerializedName("gmap_url")
    public String gmap_url;


    @SerializedName("request_services")
    public String mRequestServices;

    @SerializedName("time_left_to_respond")
    public Long mTimeLeftToRespond;
    @SerializedName("type")
    public List<Type> mType;
    @SerializedName("pickup_address")
    public String pickupAddress;
    @SerializedName("drop_address")
    public String dropAddress;
//    @SerializedName("estimation")
//    public int estimation;
    public class Type {

        @SerializedName("name")
        public String mName;
        @SerializedName("price")
        public Double mPrice;

    }

    public class RequestData {

        @SerializedName("dog")
        public List<Object> mDog;
        @SerializedName("owner")
        public Owner mOwner;
        @SerializedName("estimation")
        public Estimation estimation;
        @SerializedName("payment_mode")
        public Integer mPaymentMode;

    }

    public class Owner {

        @SerializedName("address")
        public String mAddress;
        @SerializedName("dest_latitude")
        public Double mDestLatitude;
        @SerializedName("dest_longitude")
        public Double mDestLongitude;
        @SerializedName("latitude")
        public Double mLatitude;
        @SerializedName("longitude")
        public Double mLongitude;
        @SerializedName("name")
        public String mName;
        @SerializedName("num_rating")
        public Float mNumRating;
        @SerializedName("payment_type")
        public Integer mPaymentType;
        @SerializedName("phone")
        public String mPhone;
        @SerializedName("picture")
        public String mPicture;
        @SerializedName("rating")
        public Float mRating;

        @SerializedName("d_latitude")
        public Double mDest_Latitude;
        @SerializedName("d_longitude")
        public Double mDest_Longitude;
    }
    public class Estimation {
        @SerializedName("drop_address")
        public String drop_address;
        @SerializedName("pickup_address")
        public String pickup_address;
        @SerializedName("gmap_url")
        public String mapURL;
        @SerializedName("estimation")
        public float estimation;
    }
}