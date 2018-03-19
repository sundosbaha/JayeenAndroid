package com.jayeen.customer.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev-Kalai on 12/26/2016.
 */

public class RequestStatusModel implements Serializable, Parcelable {


    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("unique_id")
    public Integer uniqueId;
    @SerializedName("request_id")
    public Integer requestId;

    @SerializedName("status")
    public String status;

    @SerializedName("is_referral_active")
    public String isReferralActive;

    @SerializedName("is_referral_active_txt")
    public String isReferralActiveTxt;

    @SerializedName("is_promo_active")
    public String isPromoActive;

    @SerializedName("is_promo_active_txt")
    public String isPromoActiveTxt;

    @SerializedName("confirmed_walker")
    public String confirmedWalker;

    @SerializedName("is_walker_started")
    public String isWalkerStarted;

    @SerializedName("is_walker_arrived")
    public String isWalkerArrived;

    @SerializedName("is_walk_started")
    public String isWalkStarted;

    @SerializedName("is_completed")
    public String isCompleted;

    @SerializedName("is_walker_rated")
    public String isWalkerRated;

    @SerializedName("is_cancelled")
    public String isCancelled;

    @SerializedName("dest_latitude")
    public Double destLatitude;

    @SerializedName("dest_longitude")
    public Double destLongitude;

    @SerializedName("promo_id")
    public String promoId;

    @SerializedName("promo_code")
    public String promoCode;

    @SerializedName("walker")
    public WalkerDetail walker;

    @SerializedName("time")
    public String time;

    @SerializedName("bill")
    public BillModel bill;

    @SerializedName("owner")
    public OwnerStatus owner;

    @SerializedName("card_details")
    public CardDetails cardDetails;

    @SerializedName("charge_details")
    public ChargeDetails chargeDetails;

    @SerializedName("accepted_time")
    public String acceptedTime;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("distance")
    public String distance;

    @SerializedName("unit")
    public String unit;

    @SerializedName("end_time")
    public String endTime;

    protected RequestStatusModel(Parcel in) {
        error = in.readString();
        status = in.readString();
        isReferralActive = in.readString();
        isReferralActiveTxt = in.readString();
        isPromoActive = in.readString();
        isPromoActiveTxt = in.readString();
        confirmedWalker = in.readString();
        isWalkerStarted = in.readString();
        isWalkerArrived = in.readString();
        isWalkStarted = in.readString();
        isCompleted = in.readString();
        isWalkerRated = in.readString();
        isCancelled = in.readString();
        promoId = in.readString();
        promoCode = in.readString();
        time = in.readString();
        acceptedTime = in.readString();
        startTime = in.readString();
        distance = in.readString();
        unit = in.readString();
        endTime = in.readString();
    }

    public static final Creator<RequestStatusModel> CREATOR = new Creator<RequestStatusModel>() {
        @Override
        public RequestStatusModel createFromParcel(Parcel in) {
            return new RequestStatusModel(in);
        }

        @Override
        public RequestStatusModel[] newArray(int size) {
            return new RequestStatusModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(error);
        dest.writeString(status);
        dest.writeString(isReferralActive);
        dest.writeString(isReferralActiveTxt);
        dest.writeString(isPromoActive);
        dest.writeString(isPromoActiveTxt);
        dest.writeString(confirmedWalker);
        dest.writeString(isWalkerStarted);
        dest.writeString(isWalkerArrived);
        dest.writeString(isWalkStarted);
        dest.writeString(isCompleted);
        dest.writeString(isWalkerRated);
        dest.writeString(isCancelled);
        dest.writeString(promoId);
        dest.writeString(promoCode);
        dest.writeString(time);
        dest.writeString(acceptedTime);
        dest.writeString(startTime);
        dest.writeString(distance);
        dest.writeString(unit);
        dest.writeString(endTime);
    }


    public class VehiclePrice implements Serializable {

        @SerializedName("name")
        public String name;

        @SerializedName("price")
        public Integer price;
    }

    public class BillModel implements Serializable {
        @SerializedName("unit")
        public String unit;

        @SerializedName("payment_mode")
        public String paymentMode;

        @SerializedName("distance")
        public String distance;

        @SerializedName("time")
        public String time;

        @SerializedName("base_distance")
        public String baseDistance;

        @SerializedName("base_price")
        public String basePrice;

        @SerializedName("distance_cost")
        public String distanceCost;
        @SerializedName("waiting_price")
        public String waiting_price;

        @SerializedName("time_cost")
        public String timeCost;

        @SerializedName("currency")
        public String currency;

        @SerializedName("main_total")
        public String mainTotal;

        @SerializedName("total")
        public String total;

        @SerializedName("referral_bonus")
        public String referralBonus;

        @SerializedName("promo_bonus")
        public String promoBonus;

        @SerializedName("payment_type")
        public String paymentType;

        @SerializedName("is_paid")
        public String isPaid;

        @SerializedName("promo_discount")
        public Integer promoDiscount;

        @SerializedName("actual_total")
        public String actualTotal;

        @SerializedName("distance_price")
        public String distancePrice;

        @SerializedName("price_per_unit_time")
        public String pricePerUnitTime;

        @SerializedName("type")
        public List<VehiclePrice> type = null;
    }

    public class ChargeDetails implements Serializable {

        @SerializedName("unit")
        public String unit;

        @SerializedName("base_distance")
        public String baseDistance;

        @SerializedName("base_price")
        public String basePrice;

        @SerializedName("distance_price")
        public String distancePrice;

        @SerializedName("price_per_unit_time")
        public String pricePerUnitTime;

        @SerializedName("total")
        public String total;

        @SerializedName("is_paid")
        public String isPaid;
    }

    public class OwnerStatus implements Serializable {

        @SerializedName("owner_lat")
        public String ownerLat;

        @SerializedName("owner_long")
        public String ownerLong;

        @SerializedName("owner_dist_lat")
        public String ownerDistLat;

        @SerializedName("owner_dist_long")
        public String ownerDistLong;

        @SerializedName("payment_type")
        public String paymentType;

        @SerializedName("default_card")
        public Integer defaultCard;

        @SerializedName("dest_latitude")
        public String destLatitude;

        @SerializedName("dest_longitude")
        public String destLongitude;

        @SerializedName("referral_code")
        public String referralCode;

        @SerializedName("is_referee")
        public String isReferee;

        @SerializedName("promo_count")
        public String promoCount;
    }

    public class WalkerStatus implements Serializable {

        @SerializedName("unique_id")
        public Integer uniqueId;

        @SerializedName("id")
        public String id;

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
        public String latitude;

        @SerializedName("longitude")
        public String longitude;

        @SerializedName("d_latitude")
        public String dLatitude;

        @SerializedName("d_longitude")
        public String dLongitude;

        @SerializedName("type")
        public String type;

        @SerializedName("rating")
        public String rating;

        @SerializedName("num_rating")
        public String numRating;

        @SerializedName("car_model")
        public String carModel;

        @SerializedName("car_number")
        public String carNumber;

        @SerializedName("bearing")
        public String bearing;
    }

    public class CardDetails implements Serializable {

        @SerializedName("card_id")
        public Integer cardId;

        @SerializedName("owner_id")
        public Integer ownerId;

        @SerializedName("customer_id")
        public String customerId;

        @SerializedName("last_four")
        public String lastFour;

        @SerializedName("card_token")
        public String cardToken;

        @SerializedName("card_type")
        public String cardType;

        @SerializedName("is_default")
        public String isDefault;

        @SerializedName("is_default_text")
        public String is_default_text;
    }

}

