package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AndroidDev-Kalai on 12/26/2016.
 */

public class HistoryModel implements Serializable {

    @SerializedName("success")
    public Boolean success;

    @SerializedName("requests")
    public ArrayList<Request> requests = null;

    public class Request implements Serializable{

        @SerializedName("start_lat")
        public String startLat;

        @SerializedName("start_long")
        public String startLong;

        @SerializedName("end_lat")
        public String endLat;

        @SerializedName("end_long")
        public String endLong;

        @SerializedName("map_url")
        public String mapUrl;

        @SerializedName("id")
        public Integer id;

        @SerializedName("date")
        public String date;

        @SerializedName("distance")
        public String distance;

        @SerializedName("unit")
        public String unit;

        @SerializedName("time")
        public String time;

        @SerializedName("promo_discount")
        public Integer promoDiscount;

        @SerializedName("distance_price")
        public String distancePrice;
        @SerializedName("waiting_price")
        public String waiting_price;

        @SerializedName("price_per_unit_time")
        public String pricePerUnitTime;

        @SerializedName("base_price")
        public String basePrice;

        @SerializedName("distance_cost")
        public String distanceCost;

        @SerializedName("time_cost")
        public String timeCost;

        @SerializedName("setbase_distance")
        public String setbaseDistance;

        @SerializedName("total")
        public String total;

        @SerializedName("actual_total")
        public Double actualTotal;

        @SerializedName("type")
        public String type;

        @SerializedName("type_icon")
        public String typeIcon;

        @SerializedName("walker")
        public Walker walker;

        @SerializedName("main_total")
        public Double mainTotal;

        @SerializedName("referral_bonus")
        public String referralBonus;

        @SerializedName("promo_bonus")
        public String promoBonus;

        @SerializedName("payment_type")
        public Integer paymentType;

        @SerializedName("is_paid")
        public Integer isPaid;

        @SerializedName("promo_id")
        public Integer promoId;

        @SerializedName("promo_code")
        public String promoCode;

        @SerializedName("currency")
        public String currency;

    }
    public class Walker implements Serializable{

        @SerializedName("rating")
        public String rating;

        @SerializedName("first_name")
        public String firstName;

        @SerializedName("last_name")
        public String lastName;

        @SerializedName("phone")
        public String phone;

        @SerializedName("email")
        public String email;

        @SerializedName("picture")
        public String picture;

        @SerializedName("bio")
        public String bio;

        @SerializedName("type")
        public String type;

    }

}

