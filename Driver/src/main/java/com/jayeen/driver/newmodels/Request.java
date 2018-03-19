package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mukesh on 1/3/17.
 */

public class Request {

    @SerializedName("admin")
    public Admin mAdmin;

    @SerializedName("waiting_price")
    public double waiting_price;
    @SerializedName("admin_per_payment")
    public Double mAdminPerPayment;
    @SerializedName("base_distance")
    public Double mBaseDistance;
    @SerializedName("base_price")
    public Double mBasePrice;
    @SerializedName("currency")
    public String mCurrency;
    @SerializedName("date")
    public String mDate;
    @SerializedName("distance")
    public String mDistance;
    @SerializedName("distance_cost")
    public Double mDistanceCost;
    @SerializedName("driver_per_payment")
    public Double mDriverPerPayment;
    @SerializedName("end_lat")
    public String mEndLat;
    @SerializedName("end_long")
    public String mEndLong;
    @SerializedName("id")
    public Integer mId;
    @SerializedName("is_paid")
    public Integer mIsPaid;
    @SerializedName("main_total")
    public Double mMainTotal;
    @SerializedName("map_url")
    public String mMapUrl;
    @SerializedName("owner")
    public Owner mOwner;
    @SerializedName("payment_type")
    public Integer mPaymentType;
    @SerializedName("promo_bonus")
    public Double mPromoBonus;
    @SerializedName("promo_code")
    public String mPromoCode;
    @SerializedName("promo_id")
    public Integer mPromoId;
    @SerializedName("referral_bonus")
    public Double mReferralBonus;
    @SerializedName("start_lat")
    public String mStartLat;
    @SerializedName("start_long")
    public String mStartLong;
    @SerializedName("time")
    public Double mTime;
    @SerializedName("time_cost")
    public Double mTimeCost;
    @SerializedName("total")
    public Double mTotal;
    @SerializedName("unit")
    public String mUnit;
    @SerializedName("walker")
    public Walker mWalker;
    @SerializedName("price_per_unit_time")
    public Double price_per_unit_time;
    @SerializedName("distance_price")
    public Double price_per_unit_distance;
    public class Walker {

        @SerializedName("amount")
        public String mAmount;
        @SerializedName("email")
        public String mEmail;

    }

    public class Admin {

        @SerializedName("amount")
        public String mAmount;
        @SerializedName("email")
        public String mEmail;

    }

    public class Owner {

        @SerializedName("first_name")
        public String mFirst_name;
        @SerializedName("last_name")
        public String last_name;
        @SerializedName("phone")
        public String mPhone;
        @SerializedName("email")
        public String email;
        @SerializedName("picture")
        public String mPicture;
        @SerializedName("bio")
        public String bio;
        @SerializedName("payment_opt")
        public Integer payment_opt;

    }

}