package com.jayeen.driver.maputills;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.newmodels.IncomingRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AppLog;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Utils {

	public static void bounceMarker(GoogleMap googleMap,final Marker marker) {
	//Make the marker bounce
    final Handler handler = new Handler();
    
    final long startTime = SystemClock.uptimeMillis();
    final long duration = 2000;
    
    Projection proj = googleMap.getProjection();
    final LatLng markerLatLng = marker.getPosition();
    Point startPoint = proj.toScreenLocation(markerLatLng);
    startPoint.offset(0, -100);
    final LatLng startLatLng = proj.fromScreenLocation(startPoint);

    final Interpolator interpolator = new BounceInterpolator();

    handler.post(new Runnable() {
        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - startTime;
            float t = interpolator.getInterpolation((float) elapsed / duration);
            double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
            double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
            marker.setPosition(new LatLng(lat, lng));

            if (t < 1.0) {
                // Post again 16ms later.
                handler.postDelayed(this, 16);
            }
        }
    });
	}
    public static void removeNotification(Context context) {
        try {
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(AndyConstants.NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getSingleObject(String responce, Class<T> modelClass) {
        try {
            return new Gson().fromJson(responce, modelClass);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static RequestDetail parsePushNewRequest(IncomingRequest incomingRequest) {
        RequestDetail requestDetail = null;
        if (incomingRequest == null) {
            return null;
        }
        try {

            if (incomingRequest.mRequestId != AndyConstants.NO_REQUEST) {
                requestDetail = new RequestDetail();
                requestDetail.setRequestId(incomingRequest.mRequestId);

                DriverApplication.preferenceHelper.putRequestId(incomingRequest.mRequestId);

                long timeto_respond = incomingRequest.mTimeLeftToRespond;
                if (timeto_respond < 0) {
                    return null;
                } else {
                    requestDetail.setTimeLeft(timeto_respond);
                }
                requestDetail.setClientName(incomingRequest.mRequestData.mOwner.mName);
                requestDetail.setClientProfile(incomingRequest.mRequestData.mOwner.mPicture);
                requestDetail.setClientPhoneNumber(incomingRequest.mRequestData.mOwner.mPhone);
                if (incomingRequest.mRequestData.mOwner.mRating > 0) {
                    requestDetail.setClientRating((float) incomingRequest.mRequestData.mOwner.mRating);
                } else {
                    requestDetail.setClientRating(0);
                }
                requestDetail.setClientLatitude(incomingRequest.mRequestData.mOwner.mLatitude + "");
                requestDetail.setClientLongitude(incomingRequest.mRequestData.mOwner.mLongitude + "");
                requestDetail.setDestinationLatitude(incomingRequest.mRequestData.mOwner.mDest_Latitude + "");
                requestDetail.setDestinationLongitude(incomingRequest.mRequestData.mOwner.mDest_Longitude + "");
                if(incomingRequest.mRequestData.estimation!=null) {
                    requestDetail.setPickupAddr(incomingRequest.mRequestData.estimation.pickup_address + "");
                    requestDetail.setDropAddr(incomingRequest.mRequestData.estimation.drop_address + "");
                    requestDetail.setMapURL(TextUtils.isEmpty(incomingRequest.gmap_url) ?
                            (incomingRequest.mRequestData.estimation != null ? incomingRequest.mRequestData.estimation.mapURL : "")
                            : incomingRequest.gmap_url);
                    AppLog.Log("issuccesssss","request-map="+incomingRequest.gmap_url
                            +"\nrequest-data-estimation-url="+incomingRequest.mRequestData.estimation.mapURL);
//                    requestDetail.setEstimate(incomingRequest.mRequestData.estimation.estimation);
//                }else{
//                    requestDetail.setPickupAddr(incomingRequest.mRequestData.estimation.pickup_address + "");
//                    requestDetail.setDropAddr(incomingRequest.mRequestData.estimation.drop_address + "");
//                    requestDetail.setMapURL(incomingRequest.estimation.mapURL);
//                    requestDetail.setEstimate(incomingRequest.estimation.estimation);
                }
                DriverApplication.preferenceHelper.putPaymentType(incomingRequest.mRequestData.mOwner.mPaymentType);
                return requestDetail;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;

    }
}
