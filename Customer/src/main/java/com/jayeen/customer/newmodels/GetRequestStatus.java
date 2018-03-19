package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mukesh on 12/29/16.
 */

public class GetRequestStatus {
    @SerializedName("success")
    
    public Boolean success;





    @SerializedName("unique_id")
    
    public Integer uniqueId;
    @SerializedName("status")
    
    public Integer status;
    @SerializedName("is_referral_active")
    
    public String isReferralActive;
    @SerializedName("is_referral_active_txt")
    
    public String isReferralActiveTxt;
    @SerializedName("is_promo_active")
    
    public String isPromoActive;
    @SerializedName("is_promo_active_txt")
    
    public String isPromoActiveTxt;
    @SerializedName("confirmed_walker")
    
    public Integer confirmedWalker;
    @SerializedName("is_walker_started")
    
    public Integer isWalkerStarted;
    @SerializedName("is_walker_arrived")
    
    public Integer isWalkerArrived;
    @SerializedName("is_walk_started")
    
    public Integer isWalkStarted;
    @SerializedName("is_completed")
    
    public Integer isCompleted;
    @SerializedName("is_walker_rated")
    
    public Integer isWalkerRated;
    @SerializedName("is_cancelled")
    
    public Integer isCancelled;
    @SerializedName("dest_latitude")
    
    public Integer destLatitude;
    @SerializedName("dest_longitude")
    
    public Integer destLongitude;
    @SerializedName("promo_id")
    
    public Integer promoId;
    @SerializedName("promo_code")
    
    public String promoCode;
    @SerializedName("walker")
    
    public Walker walker;
    @SerializedName("bill")
    
    public String bill;
    @SerializedName("owner")
    
    public Owner owner;
    @SerializedName("card_details")
    
    public String cardDetails;
    @SerializedName("charge_details")
    
    public ChargeDetails chargeDetails;
    @SerializedName("error_code")
    
    public Integer errorCode;
    @SerializedName("error")
    
    public String error;
    public class Owner {

        @SerializedName("owner_lat")
        
        public Double ownerLat;
        @SerializedName("owner_long")
        
        public Double ownerLong;
        @SerializedName("owner_dist_lat")
        
        public Integer ownerDistLat;
        @SerializedName("owner_dist_long")
        
        public Integer ownerDistLong;
        @SerializedName("payment_type")
        
        public Integer paymentType;
        @SerializedName("default_card")
        
        public Integer defaultCard;
        @SerializedName("dest_latitude")
        
        public Integer destLatitude;
        @SerializedName("dest_longitude")
        
        public Integer destLongitude;
        @SerializedName("referral_code")
        
        public String referralCode;
        @SerializedName("is_referee")
        
        public Integer isReferee;
        @SerializedName("promo_count")
        
        public Integer promoCount;

    }
    public class Walker {

        @SerializedName("unique_id")
        
        public Integer uniqueId;
        @SerializedName("id")
        
        public Integer id;
        @SerializedName("first_name")
        
        public String firstName;
        @SerializedName("last_name")
        
        public String lastName;
        @SerializedName("phone")
        
        public String phone;
        @SerializedName("bio")
        
        public String bio;
        @SerializedName("picture")
        
        public String picture;
        @SerializedName("latitude")
        
        public Double latitude;
        @SerializedName("longitude")
        
        public Double longitude;
        @SerializedName("type")
        
        public Integer type;
        @SerializedName("car_model")
        
        public String carModel;
        @SerializedName("car_number")
        
        public String carNumber;
        @SerializedName("bearing")
        
        public Integer bearing;
        @SerializedName("rating")
        
        public Integer rating;
        @SerializedName("num_rating")
        
        public Integer numRating;

    }
    public class ChargeDetails {

        @SerializedName("unit")
        
        public String unit;
        @SerializedName("base_distance")
        
        public Integer baseDistance;
        @SerializedName("base_price")
        
        public Integer basePrice;
        @SerializedName("distance_price")
        
        public Double distancePrice;
        @SerializedName("price_per_unit_time")
        
        public Double pricePerUnitTime;
        @SerializedName("total")
        
        public Integer total;
        @SerializedName("is_paid")
        
        public Integer isPaid;

    }
}
