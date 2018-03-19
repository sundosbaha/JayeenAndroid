package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mukesh on 12/20/16.
 */

public class UserModel implements Serializable {

    @SerializedName("id")
    public Integer id;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("email")
    public String email;

    @SerializedName("is_otp")
    public boolean isOtp;

    @SerializedName("picture")
    public String picture;

    @SerializedName("bio")
    public String bio;

    @SerializedName("address")
    public String address;


    @SerializedName("state")
    public String state;

    @SerializedName("country")
    public String country;

    @SerializedName("zipcode")
    public String zipcode;

    @SerializedName("login_by")
    public String loginBy;

    @SerializedName("social_unique_id")
    public String socialUniqueId;

    @SerializedName("device_token")
    public String deviceToken;

    @SerializedName("device_type")
    public String deviceType;

    @SerializedName("otp_status")
    public Boolean otpStatus;

    @SerializedName("timezone")
    public String timezone;

    @SerializedName("token")
    public String token;

    @SerializedName("referral_code")
    public String referralCode;

    @SerializedName("is_referee")
    public Integer isReferee;

    @SerializedName("promo_count")
    public Integer promoCount;

    @SerializedName("is_referral_active")
    public String isReferralActive;

    @SerializedName("is_referral_active_txt")
    public String isReferralActiveTxt;

    @SerializedName("is_promo_active")
    public String isPromoActive;

    @SerializedName("is_promo_active_txt")
    public String isPromoActiveTxt;

}