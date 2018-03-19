
package com.jayeen.driver.newmodels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetRequestModel {
    @SerializedName("success")
    public Boolean mSuccess;

    @SerializedName("is_approved")
    public Integer mIsApproved;
    @SerializedName("is_approved_txt")
    public String mIsApprovedTxt;

    @SerializedName("is_available")
    public Integer mIsAvailable;

    @SerializedName("incoming_requests")
    public List<IncomingRequest> mIncomingRequests;

    @SerializedName("error")
    public String error;

    @SerializedName("error_code")
    public Integer error_code;

    //    Second Type of Request
    @SerializedName("request")
    public Request request;
    @SerializedName("bill")
    public Bill bill = null;
//    public List<Object> bill = null;


    public class OwnerRequest {

        @SerializedName("name")

        public String name;
        @SerializedName("picture")

        public String picture;
        @SerializedName("phone")

        public String phone;
        @SerializedName("address")

        public String address;
        @SerializedName("latitude")

        public Double latitude;
        @SerializedName("longitude")

        public Double longitude;
        @SerializedName("owner_dist_lat")

        public Double ownerDistLat;
        @SerializedName("owner_dist_long")

        public Double ownerDistLong;
        @SerializedName("dest_latitude")

        public Double destLatitude;
        @SerializedName("dest_longitude")

        public Double destLongitude;
        @SerializedName("rating")

        public Float rating;
        @SerializedName("num_rating")

        public Float numRating;

    }

    public class Request {

        @SerializedName("is_walker_started")

        public Integer isWalkerStarted;
        @SerializedName("is_walker_arrived")

        public Integer isWalkerArrived;
        @SerializedName("is_started")

        public Integer isStarted;
        @SerializedName("is_completed")

        public Integer isCompleted;
        @SerializedName("is_dog_rated")

        public Integer isDogRated;
        @SerializedName("is_cancelled")

        public Integer isCancelled;
        @SerializedName("dest_latitude")

        public Double destLatitude;
        @SerializedName("dest_longitude")

        public Double destLongitude;
        @SerializedName("accepted_time")

        public String acceptedTime;
        @SerializedName("payment_mode")

        public Integer paymentMode;
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("owner")

        public OwnerRequest owner;
        @SerializedName("dog")

        public List<Object> dog = null;

        @SerializedName("bill")
        public Bill bill;
        
        @SerializedName("request_type")
        public String request_type;
        //        @SerializedName("card_details")
//
//        public String cardDetails;
        @SerializedName("charge_details")

        public ChargeDetails chargeDetails;

        @SerializedName("start_time")

        public String start_time;
        @SerializedName("unit")

        public String unit;
    }

    public class ChargeDetails {

        @SerializedName("unit")

        public String unit;
        @SerializedName("base_distance")

        public Double baseDistance;
        @SerializedName("base_price")

        public Double basePrice;
        @SerializedName("distance_price")

        public Double distancePrice;
        @SerializedName("price_per_unit_time")

        public Double pricePerUnitTime;
        @SerializedName("total")

        public Double total;
        @SerializedName("is_paid")

        public Integer isPaid;

    }

    public class Bill {

        @SerializedName("distance")
        public String distance;

        @SerializedName("unit")
        public String unit;

        @SerializedName("time")
        public Double time;

        @SerializedName("base_distance")
        public Double baseDistance;

        @SerializedName("base_price")
        public Double basePrice;

        @SerializedName("distance_cost")
        public Double distanceCost;

        @SerializedName("time_cost")
        public Double timeCost;

        @SerializedName("tips")
        public String tips;

        @SerializedName("waiting_price")
        public Double waiting_price;

        @SerializedName("walker")
        public Walker walker;
        @SerializedName("admin")

        public Admin admin;
        @SerializedName("currency")
        public String currency;

        @SerializedName("total")
        public Double total;

        @SerializedName("main_total")
        public Double mainTotal;

        @SerializedName("actual_total")
        public Double actualTotal;

        @SerializedName("referral_bonus")
        public Double referralBonus;

        @SerializedName("promo_bonus")
        public Double promoBonus;

        @SerializedName("payment_type")
        public Integer paymentType;

        @SerializedName("is_paid")
        public Integer isPaid;

    }

    public class Walker {

        @SerializedName("email")
        public String email;

        @SerializedName("amount")
        public Double amount;

    }

    public class Admin {

        @SerializedName("email")
        public String email;

        @SerializedName("amount")
        public Double amount;

    }

}
