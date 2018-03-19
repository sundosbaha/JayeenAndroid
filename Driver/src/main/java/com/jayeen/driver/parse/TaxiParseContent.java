package com.jayeen.driver.parse;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.db.DatabaseAdapter;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.model.User;
import com.jayeen.driver.newmodels.DriverModel;
import com.jayeen.driver.newmodels.GetRequestModel;
import com.jayeen.driver.newmodels.IncomingRequest;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TaxiParseContent implements Serializable {
    private Activity activity;
    private DatabaseAdapter dbadapter;
    private Gson mGson;

    public TaxiParseContent(Activity ctx) {
        this.activity = ctx;
        dbadapter = new DatabaseAdapter(ctx);
        mGson = new Gson();
    }
 public TaxiParseContent(Context ctx) {
        this.activity = (Activity) ctx;
        dbadapter = new DatabaseAdapter(ctx);
        mGson = new Gson();
    }

    public void insertUser(DriverModel mUser) {
        dbadapter.creatUser(mUser);
    }

    public DriverModel getUser() {
        return dbadapter.getUser();
    }

    public RequestDetail parseAllRequests(GetRequestModel response) {
        RequestDetail requestDetail = null;
        if (response == null) {
            return null;
        }
        try {

            if (response.mSuccess) {
                if (response.mIncomingRequests != null && (response.mIncomingRequests.size() > 0)) {
                    IncomingRequest incomingRequest = response.mIncomingRequests.get(0);
                    if (response.mIncomingRequests.get(0).mRequestId != AndyConstants.NO_REQUEST) {
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
                        requestDetail.setDestinationLatitude(incomingRequest.mRequestData.mOwner.mDestLatitude + "");
                        requestDetail.setDestinationLongitude(incomingRequest.mRequestData.mOwner.mDestLongitude + "");
                        requestDetail.setPickupAddr(TextUtils.isEmpty(incomingRequest.pickupAddress) ?
                                    (incomingRequest.mRequestData.estimation != null ? incomingRequest.mRequestData.estimation.pickup_address : "")
                                    : incomingRequest.pickupAddress);
                        requestDetail.setDropAddr(TextUtils.isEmpty(incomingRequest.dropAddress) ?
                                (incomingRequest.mRequestData.estimation != null ? incomingRequest.mRequestData.estimation.drop_address : "")
                                : incomingRequest.dropAddress);
                        requestDetail.setMapURL(TextUtils.isEmpty(incomingRequest.gmap_url) ?
                                (incomingRequest.mRequestData.estimation != null ? incomingRequest.mRequestData.estimation.mapURL : "")
                                : incomingRequest.gmap_url);
                        requestDetail.setEstimate(incomingRequest.mRequestData.estimation.estimation);
                        DriverApplication.preferenceHelper.putPaymentType(incomingRequest.mRequestData.mOwner.mPaymentType);
                        return requestDetail;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;

    }

    public RequestDetail parsePushNewRequest(IncomingRequest incomingRequest) {
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

    public int checkRequestStatus(GetRequestModel response) {
        int status = AndyConstants.NO_REQUEST;
        if (response.request.isWalkerStarted == 0) {
            status = AndyConstants.IS_WALKER_STARTED;
        }
        if (response.request.isWalkerStarted == 1) {
            status = AndyConstants.IS_WALKER_ARRIVED;
        }
        if (response.request.isWalkerArrived == 1) {
            status = AndyConstants.IS_WALK_STARTED;
        }
        if (response.request.isStarted == 1) {
            status = AndyConstants.IS_WALK_COMPLETED;
        }
        if (response.request.isCompleted == 1) {
            status = AndyConstants.IS_DOG_RATED;
        }
        if (response.request.isDogRated == 1) {
            status = AndyConstants.IS_DOG_RATED;
        }
        if (status == AndyConstants.NO_REQUEST) { //When walker confirmed and the status is no satisfied in above state Driver Accepted
            status = 11;
        }
        if (response.request.isCancelled == 1) {
            status = AndyConstants.NO_REQUEST;
        }
        String time = response.request.start_time != null ? response.request.start_time : "";
        if (!TextUtils.isEmpty(time)) {
            try {
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.ENGLISH).parse(time);
                AppLog.Log("TAG", "START DATE---->" + date.toString()
                        + " month:" + date.getMonth() + "time" + date.getTime());
                DriverApplication.preferenceHelper.putRequestTime(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            DriverApplication.preferenceHelper.putRequestTime(AndyConstants.NO_TIME);
        }
        if (response.request.destLatitude != 0) {
            DriverApplication.preferenceHelper.putClientDestination(new LatLng(response.request.destLatitude, response.request.destLongitude));
        }
        return status;
    }

    public RequestDetail parseRequestStatus(GetRequestModel response) {
        if (response == null) {
            return null;
        }
        RequestDetail requestDetail = null;
        try {
            if (response.mSuccess) {
                requestDetail = new RequestDetail();
                requestDetail.setJobStatus(checkRequestStatus(response));
                String time = response.request.start_time != null ? response.request.start_time : "";
                if (!TextUtils.isEmpty(time)) {
                    try {
                        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(time);
                        AppLog.Log("TAG", "START DATE---->" + date.toString() + " month:" + date.getMonth());
                        DriverApplication.preferenceHelper.putRequestTime(date.getTime());
                        requestDetail.setStartTime(date.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                DriverApplication.preferenceHelper.putPaymentType(response.request.paymentType);
                try {
                    if (response.request.destLatitude != 0) {
                        DriverApplication.preferenceHelper.putClientDestination(new LatLng(response.request.destLatitude, response.request.destLongitude));
                        DriverApplication.preferenceHelper.putDestinationLatitude(response.request.destLatitude + "");
                        DriverApplication.preferenceHelper.putDestinationLongitude(response.request.destLongitude + "");
                        requestDetail.setDestinationLatitude(response.request.destLatitude + "");
                        requestDetail.setDestinationLongitude(response.request.destLongitude + "");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                requestDetail.setClientName(response.request.owner.name);
                requestDetail.setClientProfile(response.request.owner.picture);
                requestDetail.setClientPhoneNumber(response.request.owner.phone);
                requestDetail.setClientRating((float) response.request.owner.rating);
                requestDetail.setClientLatitude(response.request.owner.latitude + "");
                requestDetail.setClientLongitude(response.request.owner.longitude + "");
                requestDetail.setUnit(response.request.unit != null ? response.request.unit : response.request.chargeDetails.unit);
                GetRequestModel.Bill jsonObjectBill = response.request.bill;
                if (jsonObjectBill != null) {
                    requestDetail.setAmount(jsonObjectBill.total + "");
                    requestDetail.setTime(jsonObjectBill.time + "");
                    requestDetail.setDistance(jsonObjectBill.distance);
                    requestDetail.setBasePrice(jsonObjectBill.basePrice + "");
                    requestDetail.setDistanceCost(jsonObjectBill.distanceCost + "");
                    requestDetail.setTimecost(jsonObjectBill.timeCost + "");
                    requestDetail.setReferralBonus(jsonObjectBill.referralBonus + "");
                    requestDetail.setPromoBonus(jsonObjectBill.promoBonus + "");
                    requestDetail.setTotal(new DecimalFormat("0.00").format(jsonObjectBill.total));
                    requestDetail.setTime(new DecimalFormat("0.00").format(jsonObjectBill.time));
                    GetRequestModel.ChargeDetails charge_details = response.request.chargeDetails;
                    requestDetail.setPriceperHr(charge_details.pricePerUnitTime + "");
                    requestDetail.setPriceperKm(charge_details.distancePrice + "");
                    requestDetail.setAdmin_per_payment("0.00");
                    requestDetail.setDriver_per_payment("0.00");
                }

            } else {
                requestDetail.setUnit(activity.getResources().getString(R.string.text_miles));
//                requestDetail.setDestinationLatitude(response.request.destLatitude + "");
//                requestDetail.setDestinationLongitude(response.request.destLongitude + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;
    }

    public boolean parseAvaibilty(String response) {
        if (TextUtils.isEmpty(response))
            return false;
        try {
            LoginModel loginModel = getSingleObject(response, LoginModel.class);
            if (loginModel.success) {
                if (loginModel.is_active == 1) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSuccess(String response) {
        if (TextUtils.isEmpty(response))
            return false;
        try {
            LoginModel loginModel = getSingleObject(response, LoginModel.class);
            if (loginModel.success) {
                return true;
            } else {
                AndyUtils.showToast(loginModel.erro, R.id.coordinatorLayout, activity);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSuccessWithId(LoginModel response) {
        if (response == null) {
            return false;
        }
        try {

            if (response.success) {
                DriverApplication.preferenceHelper.putUserId(response.driver.id + "");
                DriverApplication.preferenceHelper.putSessionToken(response.driver.token);
                DriverApplication.preferenceHelper.putEmail(response.driver.email);
                DriverApplication.preferenceHelper.putLoginBy(response.driver.login_by);
                DriverApplication.preferenceHelper.putType(response.driver.type);
                return true;
            } else {
                AndyUtils.showErrorToast(response.errorCode, activity, R.id.coordinatorLayout, activity);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User parseUserAndStoreToDb(DriverModel response) {
        User user = null;
        try {
            dbadapter.creatUser(response);
            DriverApplication.preferenceHelper.putIsApproved(response.isApproved ? "1" : (0 + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public <T> T getSingleObject(String responce, Class<T> modelClass) {
        try {
            return mGson.fromJson(responce, modelClass);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
