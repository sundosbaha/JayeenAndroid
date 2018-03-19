package com.jayeen.customer.newmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev-Kalai on 12/27/2016.
 */

public class CardsModel implements Serializable {


    @SerializedName("success")
    public Boolean success;

    @SerializedName("error")
    public String erro;

    @SerializedName("error_code")
    public Integer errorCode;

    @SerializedName("payments")
    public List<Payment> payments = null;

    public class Payment implements Serializable {

        @SerializedName("id")
        public Integer id;

        @SerializedName("customer_id")
        public String customerId;

        @SerializedName("last_four")
        public String lastFour;

        @SerializedName("card_token")
        public String cardToken;

        @SerializedName("card_type")
        public String cardType;

        @SerializedName("card_id")
        public String cardId;

        @SerializedName("is_default")
        public Boolean isDefault;
    }

    @Expose
    public Wallet wallet_maintain;

    //    Wallet
    public class Wallet implements Serializable {
        @Expose
        public Boolean success;

        @Expose
        public String error;
        @Expose
        public String error_code;
        @Expose
        public String total_balance;
        @Expose
        public String total_balance_amount;
        @Expose
        public String total_added_amount;
        @Expose
        public String total_spent_amount;
    }

}
