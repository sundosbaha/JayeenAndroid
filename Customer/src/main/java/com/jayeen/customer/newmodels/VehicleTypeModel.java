package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/23/2016.
 */

public class VehicleTypeModel implements Serializable {
    public String duration;
    @SerializedName("id")
    public Integer id;

    @SerializedName("name")
    public String name;

    @SerializedName("min_fare")
    public Float minFare;

    @SerializedName("max_size")
    public Integer maxSize;

    @SerializedName("icon")
    public String icon;

    @SerializedName("is_default")
    public Boolean isDefault;

    @SerializedName("price_per_unit_time")
    public Float pricePerUnitTime;

    @SerializedName("price_per_unit_distance")
    public Float pricePerUnitDistance;

    @SerializedName("base_price")
    public Float basePrice;

    @SerializedName("base_distance")
    public Float baseDistance;

    @SerializedName("currency")
    public String currency;

    @SerializedName("unit")
    public String unit;
    public boolean isSelected;

    public void setMinDuration(String duration) {
        this.duration=duration;
    }
}
