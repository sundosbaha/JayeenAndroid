package com.jayeen.driver.locationupdate;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.locationupdate.LocationHelper.OnLocationReceived;
import com.jayeen.driver.newmodels.UpdateLocationModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;

import java.util.HashMap;

import static com.jayeen.driver.DriverApplication.locationRequestQueue;
import static com.jayeen.driver.utills.AndyConstants.ServiceType.REQUEST_LOCATION_UPDATE;

public class  LocationUpdateService extends IntentService implements
        OnLocationReceived, AsyncTaskCompleteListener, Response.ErrorListener {
    //private PreferenceHelper preferenceHelper;
    private LocationHelper locationHelper;
    private String id, token, latitude, longitude;
    //private static Timer timer;
    private LatLng latlngPrevious, latlngCurrent;
    private static boolean isNoRequest;
    private double bearing;
    private Gson mGson;
    private static boolean isServerUpdateComplete = true;
    private static boolean isTripUpdateComplete = true;
    private double oldLat = 0.0, oldLong = 0.0;
    private boolean isRequestDone = true;

    public LocationUpdateService() {
        this("MySendLocationService");
    }

    public LocationUpdateService(String name) {
        super("MySendLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
        locationHelper.onStart();
        id = DriverApplication.preferenceHelper.getUserId();
        token = DriverApplication.preferenceHelper.getSessionToken();
        mGson = new Gson();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        if (locationHelper != null) {
            locationHelper.onStop();
        }
        super.onDestroy();
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }


    @Override
    public void onLocationReceived(Location location) {
        LatLng latlong = locationHelper.getLatLng(location);
        AppLog.Log("TAG", "onLocationReceived Lat : " + latlong.latitude + " , long : " + latlong.longitude);
        if (oldLat != latlong.latitude || oldLong != latlong.longitude) {
            oldLat = latlong.latitude;
            oldLong = latlong.longitude;
            if (isRequestDone) {
                if (latlong != null) {
                    DriverApplication.preferenceHelper.putWalkerLatitude(String.valueOf(latlong.latitude));
                    DriverApplication.preferenceHelper.putWalkerLongitude(String.valueOf(latlong.longitude));
                    latlngCurrent = new LatLng(latlong.latitude, latlong.longitude);
                    if (latlngPrevious == null) {
                        latlngPrevious = new LatLng(latlong.latitude, latlong.longitude);
                    }
                }
                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(token) && latlong != null) {
                    latitude = String.valueOf(latlong.latitude);
                    longitude = String.valueOf(latlong.longitude);
                    bearing = location.hasBearing() ? location.getBearing() : 0.0;
                    if (!AndyUtils.isNetworkAvailable(getApplicationContext())) {
                        return;
                    }
                    if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
//                    new UploadDataToServer().execute();
                        updateLocationData(false);
                    } else {
                        isNoRequest = false;
//                    new UploadTripLocationData().execute();
                        updateLocationData(true);
                    }
                }
            } else {
                AppLog.Log("TAG", "onLocation    :  Old request is pending ");
            }
        } else {
            AppLog.Log("TAG", "onLocationReceived : same Old lat and Long");
        }
    }


    @Override
    public void onConntected(Bundle bundle) {

    }


    @Override
    public void onConntected(Location location) {

    }

    public <T> T getSingleObject(String responce, Class<T> modelClass) {
        AppLog.Log("Location Data", responce);
        return mGson.fromJson(responce, modelClass);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        try {
            isRequestDone = true;
            UpdateLocationModel mUpdateLocationModel = getSingleObject(response, UpdateLocationModel.class);
            if (serviceCode == AndyConstants.ServiceCode.REQUEST_LOCATION_UPDATE) {
                if (mUpdateLocationModel.mSuccess) {
                    if (DriverApplication.preferenceHelper.getIsTripOn()) {
                        DriverApplication.preferenceHelper.putDistance(mUpdateLocationModel.mDistance);
                        DriverApplication.preferenceHelper.putUnit(mUpdateLocationModel.mUnit);
                        DriverApplication.preferenceHelper.putDistanceTime(mUpdateLocationModel.mTime + "");
                        DriverApplication.preferenceHelper.putDestinationLatitude(mUpdateLocationModel.mDestLatitude + "");
                        DriverApplication.preferenceHelper.putDestinationLongitude(mUpdateLocationModel.mDestLongitude + "");
                    } else {
                        DriverApplication.preferenceHelper.putDistanceTime("0.00");
                        DriverApplication.preferenceHelper.putDistance(0.00f);
                    }
                } else {
                    DriverApplication.preferenceHelper.putUnit(getString(R.string.text_miles));
                }

                if (!mUpdateLocationModel.mSuccess)
                    if (mUpdateLocationModel.mIsCancelled == 1) {
                        DriverApplication.preferenceHelper.clearRequestData();
                    }
            } else if (serviceCode == AndyConstants.ServiceCode.UPDATE_PROVIDER_LOCATION) {
                DriverApplication.preferenceHelper.putDistanceTime("0.00");
                DriverApplication.preferenceHelper.putDistance(0.00f);
                if (mUpdateLocationModel.mSuccess) {
                    if (mUpdateLocationModel.mIsActive == 1)
                        DriverApplication.preferenceHelper.putIsActive(true);
                    else
                        DriverApplication.preferenceHelper.putIsActive(false);
                }
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            isRequestDone = true;
            System.gc();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("Location Update", error.getMessage());
        isRequestDone = true;
    }

    public void updateLocationData(boolean isTrip) {
        int SERVICE_CODE = 0;
        isRequestDone = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.Params.ID, id);
        map.put(AndyConstants.Params.TOKEN, token);
        map.put(AndyConstants.Params.LATITUDE, latitude);
        map.put(AndyConstants.Params.LONGITUDE, longitude);
        map.put(AndyConstants.Params.BEARING, bearing + "");
        if (isTrip) {
            map.put(AndyConstants.Params.TIME, DriverApplication.preferenceHelper.getDistanceTime());
            map.put(AndyConstants.Params.REQUEST_ID, DriverApplication.preferenceHelper
                    .getRequestId() + "");
            map.put(AndyConstants.URL, REQUEST_LOCATION_UPDATE);
            SERVICE_CODE = AndyConstants.ServiceCode.REQUEST_LOCATION_UPDATE;
        } else {
            map.put(AndyConstants.URL, AndyConstants.ServiceType.UPDATE_PROVIDER_LOCATION);
            SERVICE_CODE = AndyConstants.ServiceCode.UPDATE_PROVIDER_LOCATION;
        }
        locationRequestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                SERVICE_CODE, this, this));
    }

}

