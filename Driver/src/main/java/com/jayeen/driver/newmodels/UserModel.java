package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mukesh on 12/20/16.
 */
@DatabaseTable(tableName = "User")
public class UserModel implements Serializable {

    @DatabaseField(columnName = "rowid", generatedId = true, canBeNull = false) //id denotes this field as primary key
    private int rowId;

    @DatabaseField(columnName = "user_id", canBeNull = false)
    @SerializedName("id")
    public Integer id;

    @DatabaseField(columnName = "first_name", canBeNull = false)
    @SerializedName("first_name")
    public String firstName;

    @DatabaseField(columnName = "last_name", canBeNull = false)
    @SerializedName("last_name")
    public String lastName;

    @DatabaseField(columnName = "contact", canBeNull = false)
    @SerializedName("phone")
    public String phone;

    @DatabaseField(columnName = "email", canBeNull = false)
    @SerializedName("email")
    public String email;

    @SerializedName("is_otp")
    public boolean isOtp;

    @DatabaseField(columnName = "picture", canBeNull = false)
    @SerializedName("picture")
    public String picture;

    @DatabaseField(columnName = "bio")
    @SerializedName("bio")
    public String bio;

    @DatabaseField(columnName = "address")
    @SerializedName("address")
    public String address;


    @SerializedName("state")
    public String state;

    @SerializedName("country")
    public String country;

    @DatabaseField(columnName = "zipcode")
    @SerializedName("zipcode")
    public String zipcode;
    @DatabaseField(columnName = "loginBy", canBeNull = false)
    @SerializedName("login_by")
    public String loginBy;
    @DatabaseField(columnName = "socialUniqueId", canBeNull = false)
    @SerializedName("social_unique_id")
    public String socialUniqueId;

    @DatabaseField(columnName = "deviceToken", canBeNull = false)
    @SerializedName("device_token")
    public String deviceToken;

    @SerializedName("device_type")
    public String deviceType;

    @SerializedName("otp_status")
    public Boolean otpStatus;

    @SerializedName("timezone")
    public String timezone;

    @DatabaseField(columnName = "token", canBeNull = false)
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