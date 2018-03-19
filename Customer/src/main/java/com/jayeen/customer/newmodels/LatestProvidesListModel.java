package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pandian on 31/1/17.
 */

public class LatestProvidesListModel implements Serializable {

    @SerializedName("success")
    public Boolean success;
    @SerializedName("walkers")
    public List<Walker> walkers;
    @SerializedName("error_code")
    public int error_code;
    public class Current implements Serializable {
        @SerializedName("latitude")
        public Double latitude;
        @SerializedName("longitude")
        public Double longitude;
    }

    public class Walker implements Serializable {

        @SerializedName("typeid")
        public int typeid;
        @SerializedName("typename")
        public String typename;
        @SerializedName("drivers")
        public List<Driver> drivers;

    }

    public class Driver implements Serializable {
        @SerializedName("id")
        public Long id;
        @SerializedName("current")
        public Current current;
        @SerializedName("bearing")
        public String bearing;
        @SerializedName("type")
        public Long type;
        @SerializedName("is_near")
        public boolean is_near;
        @SerializedName("duration")
        public String duration;
    }
}
