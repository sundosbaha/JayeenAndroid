package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class RideNowSuccess implements Serializable {
    @SerializedName("success")
    public Boolean success;

    @SerializedName("unique_id")
    public Integer uniqueId;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("is_referral_active")
    public Boolean isReferralActive;

    @SerializedName("is_referral_active_txt")
    public String isReferralActiveTxt;

    @SerializedName("error")
    public String error;

    @SerializedName("is_promo_active")
    public Boolean isPromoActive;

    @SerializedName("is_promo_active_txt")

    public String isPromoActiveTxt;
    @SerializedName("request_id")

    public Integer requestId;
    @SerializedName("walker")

    public WalkerDetail walker;
}
