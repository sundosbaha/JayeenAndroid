
package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderListModel {

    @SerializedName("success")
    public Boolean Success;

    @SerializedName("error")
    public Boolean error;

    @SerializedName("error_code")
    public Boolean error_code;

    @SerializedName("walker_list")
    public List<WalkerList> mWalkerList;

    public class WalkerList {

        @SerializedName("address")
        public String mAddress;
        @SerializedName("bearing")
        public Long mBearing;
        @SerializedName("bio")
        public String mBio;
        @SerializedName("car_model")
        public String mCarModel;
        @SerializedName("car_number")
        public String mCarNumber;
        @SerializedName("country")
        public String mCountry;
        @SerializedName("email")
        public String mEmail;
        @SerializedName("first_name")
        public String mFirstName;
        @SerializedName("id")
        public long mId;
        @SerializedName("last_name")
        public String mLastName;
        @SerializedName("latitude")
        public Double mLatitude;
        @SerializedName("longitude")
        public Double mLongitude;
        @SerializedName("phone")
        public String mPhone;
        @SerializedName("state")
        public String mState;
        @SerializedName("type")
        public Integer mType;
        @SerializedName("zipcode")
        public String mZipcode;


    }

}
