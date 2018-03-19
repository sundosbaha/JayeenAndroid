package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mukesh on 12/28/16.
 */


public class SchedulehistoryModel {

    @SerializedName("success")
    public Boolean success;
    
    @SerializedName("requests")
    public List<Request> requests = null;

    public class Request {

        @SerializedName("scheduleDate")
        
        public String scheduleDate;
        @SerializedName("scheduleTime")
        
        public String scheduleTime;
        @SerializedName("pickupAddress")
        
        public String pickupAddress;
        @SerializedName("droppAddress")
        
        public String droppAddress;
        @SerializedName("car_type")
        
        public String carType;
        @SerializedName("is_cancelled")
        
        public Boolean isCancelled;
        @SerializedName("id")
        
        public String id;

    }
}
