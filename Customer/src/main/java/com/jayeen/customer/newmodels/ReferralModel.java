package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/27/2016.
 */

public class ReferralModel implements Serializable {


    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("referral_code")
    public String referralCode;

    @SerializedName("total_referrals")
    public String totalRefferal;

    @SerializedName("amount_earned")
    public String amountEarned;

    @SerializedName("amount_spent")
    public String amountSpent;

    @SerializedName("balance_amount")
    public String balanceAmount;


}
