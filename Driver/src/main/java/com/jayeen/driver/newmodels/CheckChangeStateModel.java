
package com.jayeen.driver.newmodels;

import com.google.gson.annotations.SerializedName;

public class CheckChangeStateModel {

    @SerializedName("is_active")
    public Integer mIsActive;
    @SerializedName("success")
    public Boolean mSuccess;
    @SerializedName("tips_status")
    public Boolean tips_status;
    @SerializedName("error")
    public String error;
    @SerializedName("error_code")
    public int error_code;

    @SerializedName("service_cost")

    public servicecost service_cost;


    public class servicecost {

        @SerializedName("zone_id")
        public String zone_id;

        @SerializedName("type")
        public String type;

        @SerializedName("base_price")
        public Double base_price;


        @SerializedName("base_distance")
        public Double base_distance;


        @SerializedName("price_per_unit_distance")
        public Double price_per_unit_distance;

        @SerializedName("price_per_unit_time")
        public Double price_per_unit_time;

       @SerializedName("waiting_price")
        public Double waiting_price;

        @SerializedName("max_size")
        public Double max_size;


    }
}
