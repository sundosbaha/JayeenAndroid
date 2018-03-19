package com.jayeen.customer.newmodels;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.jayeen.customer.models.Route;

public class WayPointsModel {

    @SerializedName("geocoded_waypoints")
    public List<Object> geocodedWaypoints = null;

    @SerializedName("routes")
    public List<Route> routes = null;

    @SerializedName("status")
    public String status;

}