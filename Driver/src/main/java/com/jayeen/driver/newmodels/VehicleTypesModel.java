
package com.jayeen.driver.newmodels;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class VehicleTypesModel {

    @SerializedName("success")
    public Boolean mSuccess;

    @SerializedName("types")
    public List<Type> mTypes;

    public class Type {

        @SerializedName("base_distance")
        public String mBaseDistance;
        @SerializedName("base_price")
        public String mBasePrice;
        @SerializedName("currency")
        public String mCurrency;
        @SerializedName("icon")
        public String mIcon;
        @SerializedName("id")
        public Long mId;
        @SerializedName("is_default")
        public Boolean mIsDefault;
        @SerializedName("max_size")
        public Long mMaxSize;
        @SerializedName("min_fare")
        public String mMinFare;
        @SerializedName("name")
        public String mName;
        @SerializedName("price_per_unit_distance")
        public String mPricePerUnitDistance;
        @SerializedName("price_per_unit_time")
        public String mPricePerUnitTime;
        @SerializedName("unit")
        public String mUnit;
        public boolean isSelected;
    }
}
