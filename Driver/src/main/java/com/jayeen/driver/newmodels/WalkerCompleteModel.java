
package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class WalkerCompleteModel implements Serializable {


    @SerializedName("success")

    public Boolean success;
    @SerializedName("total")

    public Double total;
    @SerializedName("error")

    public String error;
    @SerializedName("error_code")
    public int error_code;
    @SerializedName("currency")

    public String currency;
    @SerializedName("is_paid")

    public Integer isPaid;
    @SerializedName("request_id")

    public String requestId;
    @SerializedName("status")

    public Integer status;
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
    @SerializedName("walker")

    public Walker walker;
    @SerializedName("payment_mode")

    public Integer paymentMode;
    @SerializedName("bill")

    public Bill bill;
    @SerializedName("owner")

    public Owner owner;
    @SerializedName("payment_option")

    public Integer paymentOption;
    @SerializedName("request")

    public Request request;

    public class Admin implements Serializable {

        @SerializedName("email")

        public String email;
        @SerializedName("amount")

        public Double amount;

    }

    public class Admin_ implements Serializable {

        @SerializedName("email")

        public String email;
        @SerializedName("amount")

        public Double amount;

    }

    public class Bill implements Serializable {

        @SerializedName("payment_mode")

        public Integer paymentMode;
        @SerializedName("distance")

        public String distance;
        @SerializedName("unit")

        public String unit;
        @SerializedName("time")

        public Double time;
        @SerializedName("base_price")

        public Double basePrice;
        @SerializedName("distance_cost")

        public Double distanceCost;
        @SerializedName("time_cost")

        public Double timeCost;
        @SerializedName("walker")

        public Walker_ walker;
        @SerializedName("admin")

        public Admin admin;
        @SerializedName("currency")

        public String currency;
        @SerializedName("actual_total")

        public Double actualTotal;
        @SerializedName("total")

        public Double total;
        @SerializedName("is_paid")

        public Integer isPaid;
        @SerializedName("promo_discount")

        public Double promoDiscount;
        @SerializedName("main_total")

        public Double mainTotal;
        @SerializedName("referral_bonus")

        public Double referralBonus;
        @SerializedName("promo_bonus")

        public Double promoBonus;
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("type")

        public List<Type> type = null;

    }

    public class Bill_ implements Serializable {

        @SerializedName("payment_mode")

        public Integer paymentMode;
        @SerializedName("distance")

        public String distance;
        @SerializedName("unit")

        public String unit;
        @SerializedName("time")

        public Double time;
        @SerializedName("base_price")

        public Double basePrice;
        @SerializedName("distance_cost")

        public Double distanceCost;
        @SerializedName("time_cost")

        public Double timeCost;
        @SerializedName("walker")

        public Walker__ walker;

        @SerializedName("tips")
        public String tips;

        @SerializedName("waiting_price")
        public Double waiting_price;

        @SerializedName("admin")

        public Admin_ admin;
        @SerializedName("currency")

        public String currency;
        @SerializedName("actual_total")

        public Double actualTotal;
        @SerializedName("total")

        public Double total;
        @SerializedName("is_paid")

        public Integer isPaid;
        @SerializedName("promo_discount")

        public Double promoDiscount;
        @SerializedName("main_total")

        public Double mainTotal;
        @SerializedName("referral_bonus")

        public Double referralBonus;
        @SerializedName("promo_bonus")

        public Double promoBonus;
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("type")

        public List<Type_> type = null;

    }

    public class ChargeDetails implements Serializable {

        @SerializedName("unit")

        public String unit;
        @SerializedName("admin_per_payment")

        public Double adminPerPayment;
        @SerializedName("driver_per_payment")

        public Double driverPerPayment;
        @SerializedName("base_price")

        public Double basePrice;
        @SerializedName("distance_price")

        public Double distancePrice;
        @SerializedName("price_per_unit_time")

        public Double pricePerUnitTime;
        @SerializedName("total")

        public Double total;
        @SerializedName("is_paid")

        public Double isPaid;

    }

    public class Owner implements Serializable {

        @SerializedName("name")

        public String name;
        @SerializedName("picture")

        public String picture;
        @SerializedName("phone")

        public String phone;
        @SerializedName("address")

        public String address;
        @SerializedName("bio")

        public String bio;
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
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("rating")

        public Float rating;
        @SerializedName("num_rating")

        public Float numRating;
        @SerializedName("start_location")

        public String startLocation;
        @SerializedName("end_location")

        public String endLocation;

    }

    public class Owner_ implements Serializable {

        @SerializedName("name")

        public String name;
        @SerializedName("picture")

        public String picture;
        @SerializedName("phone")

        public String phone;
        @SerializedName("address")

        public String address;
        @SerializedName("bio")

        public String bio;
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
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("rating")

        public Float rating;
        @SerializedName("num_rating")

        public Float numRating;
        @SerializedName("start_location")

        public String startLocation;
        @SerializedName("end_location")

        public String endLocation;

    }

    public class Request implements Serializable {

        @SerializedName("request_id")

        public String requestId;
        @SerializedName("status")

        public Integer status;
        @SerializedName("confirmed_walker")

        public Integer confirmedWalker;
        @SerializedName("is_walker_started")

        public Integer isWalkerStarted;
        @SerializedName("is_walker_arrived")

        public Integer isWalkerArrived;
        @SerializedName("is_started")

        public Integer isStarted;
        @SerializedName("is_walk_started")

        public Integer isWalkStarted;
        @SerializedName("is_completed")

        public Integer isCompleted;
        @SerializedName("is_dog_rated")

        public Integer isDogRated;
        @SerializedName("is_cancelled")

        public Integer isCancelled;
        @SerializedName("is_walker_rated")

        public Integer isWalkerRated;
        @SerializedName("dest_latitude")

        public Double destLatitude;
        @SerializedName("dest_longitude")

        public Double destLongitude;
        @SerializedName("accepted_time")

        public String acceptedTime;
        @SerializedName("payment_type")

        public Integer paymentType;
        @SerializedName("distance")

        public String distance;
        @SerializedName("unit")

        public String unit;
        @SerializedName("end_time")

        public String endTime;
        @SerializedName("owner")

        public Owner_ owner;
        @SerializedName("dog")

        public List<Object> dog = null;
        @SerializedName("bill")

        public Bill_ bill;
        @SerializedName("charge_details")

        public ChargeDetails chargeDetails;
        @SerializedName("payment_option")

        public Integer paymentOption;

    }

    public class Type implements Serializable {

        @SerializedName("name")

        public String name;
        @SerializedName("price")

        public Double price;

    }

    public class CardDetails implements Serializable {

        @SerializedName("is_default_text")
        public String is_default_text;
        @SerializedName("card_id")
        public long card_id;
        @SerializedName("owner_id")
        public long owner_id;
        @SerializedName("customer_id")
        public String customer_id;
        @SerializedName("last_four")
        public String last_four;
        @SerializedName("card_token")
        public String card_token;
        @SerializedName("card_type")
        public String card_type;
        @SerializedName("is_default")
        public int is_default;


    }

    public class Type_ implements Serializable {

        @SerializedName("name")

        public String name;
        @SerializedName("price")

        public Double price;

    }

    public class Walker implements Serializable {

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
        @SerializedName("rating")

        public Double rating;
        @SerializedName("num_rating")

        public Integer numRating;
        @SerializedName("car_model")

        public String carModel;
        @SerializedName("car_number")

        public String carNumber;

    }

    public class Walker_ implements Serializable {

        @SerializedName("email")

        public String email;
        @SerializedName("amount")

        public String amount;

    }

    public class Walker__ implements Serializable {

        @SerializedName("email")

        public String email;
        @SerializedName("amount")

        public String amount;

    }
}
