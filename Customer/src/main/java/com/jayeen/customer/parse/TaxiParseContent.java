package com.jayeen.customer.parse;

import android.app.Activity;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.maputils.PolyLineUtils;
import com.jayeen.customer.models.AllProviderList;
import com.jayeen.customer.models.ApplicationPages;
import com.jayeen.customer.models.Route;
import com.jayeen.customer.models.Step;
import com.jayeen.customer.newmodels.ApplyPromoModel;
import com.jayeen.customer.newmodels.CardsModel;
import com.jayeen.customer.newmodels.HistoryModel;
import com.jayeen.customer.newmodels.LoginModel;
import com.jayeen.customer.newmodels.PlacesResultsNames;
import com.jayeen.customer.newmodels.ReferralModel;
import com.jayeen.customer.newmodels.RequestProgressModel;
import com.jayeen.customer.newmodels.RequestStatusModel;
import com.jayeen.customer.newmodels.SuccessModel;
import com.jayeen.customer.newmodels.UserModel;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.jayeen.customer.utils.ReadFiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class TaxiParseContent {
    private Activity activity;
//    private DatabaseAdapter dbadapter;
    private Gson mGson;
    private final String KEY_SUCCESS = "success";
    private final String REQUESTS = "requests";
    private final String WALKERLIST = "walker_list";
    private final String BASE_PRICE = "base_price";
    private final String BASE_DISTANCE = "base_distance";
    private final String UNIT = "unit";
    private final String ID = "id";
    private final String ICON = "icon";
    private final String PRICE_PER_UNIT_TIME = "price_per_unit_time";
    private final String PRICE_PER_UNIT_DISTANCE = "price_per_unit_distance";
    private final String NAME = "name";
    private final String MIN_FARE = "min_fare";
    private final String MAX_SIZE = "max_size";

    public TaxiParseContent(Activity ctx) {
        this.activity = ctx;
        mGson = new Gson();

    }



    public <T> T getSingleObject(String responce, Class<T> modelClass) {
        AppLog.Log("Parse", responce);
        try {
            return mGson.fromJson(responce, modelClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean localUserData(UserModel user) {

        CustomerApplication.preferenceHelper.putUserId(user.id + "");
        CustomerApplication.preferenceHelper.putSessionToken(user.token);
        CustomerApplication.preferenceHelper.putEmail(user.email);
        CustomerApplication.preferenceHelper.putPhoneNo(user.phone);
        CustomerApplication.preferenceHelper.putLoginBy(user.loginBy);
        CustomerApplication.preferenceHelper.putReferee(user.isReferee);
        CustomerApplication.preferenceHelper.putSocialId(user.loginBy.equalsIgnoreCase(
                Const.MANUAL) ? "" : user.socialUniqueId);

        return false;
    }

    public int getRequestInProgress(String response) {
        if (TextUtils.isEmpty(response))
            return Const.NO_REQUEST;
        AppLog.Log(Const.TAG, response);
//            JSONObject jsonObject = new JSONObject(response);
        RequestProgressModel requestProgressModel = getSingleObject(response, RequestProgressModel.class);
        if (requestProgressModel.success) {
            int requestId = requestProgressModel.requestId;
            CustomerApplication.preferenceHelper.putRequestId(requestId);
            return requestId;
        }
        return Const.NO_REQUEST;
    }

    public void parseCardAndPriceDetails(RequestStatusModel requestStatusModel) {

        if (requestStatusModel.cardDetails != null) {
            CustomerApplication.preferenceHelper.putDefaultCard(requestStatusModel.cardDetails.cardId);
            CustomerApplication.preferenceHelper.putDefaultCardNo(requestStatusModel.cardDetails.lastFour);
            CustomerApplication.preferenceHelper.putDefaultCardType(requestStatusModel.cardDetails.cardType);
        }
        if (requestStatusModel.chargeDetails != null) {
            CustomerApplication.preferenceHelper.putBasePrice(Float.parseFloat(requestStatusModel.chargeDetails.basePrice));
            CustomerApplication.preferenceHelper.putDistancePrice(Float.parseFloat(requestStatusModel.chargeDetails.distancePrice));
            CustomerApplication.preferenceHelper.putTimePrice(Float.parseFloat(requestStatusModel.chargeDetails.pricePerUnitTime));
        }
        if (requestStatusModel.owner != null && requestStatusModel.owner.paymentType != null) {
            CustomerApplication.preferenceHelper.putPaymentMode(Integer.parseInt(requestStatusModel.owner.paymentType));
        }

    }

    public int checkRequestStatus(RequestStatusModel requestStatusModel) {
        int status = Const.NO_REQUEST;
//        RequestStatusModel requestStatusModel = getSingleObject(response, RequestStatusModel.class);
        // JSONObject jsonObject = new JSONObject(response);
//        if (requestStatusModel.isCancelled != null) {
//            if (Integer.parseInt(requestStatusModel.isCancelled) == 1) {
//                return Const.IS_WALKER_CANC;
//            }
//        }
        if (Integer.parseInt(requestStatusModel.confirmedWalker) == 0 && Integer.parseInt(requestStatusModel.status) == 0) {
            if (requestStatusModel.requestId != null)
                if (requestStatusModel.requestId > 0)
                    CustomerApplication.preferenceHelper.putRequestId(requestStatusModel.requestId);
            return Const.IS_REQEUST_CREATED;
        } else if (Integer.parseInt(requestStatusModel.confirmedWalker) == 0 && Integer.parseInt(requestStatusModel.status) == 1) {
            CustomerApplication.preferenceHelper.putRequestId(Const.NO_REQUEST);
            return Const.NO_REQUEST;
        } else if (Integer.parseInt(requestStatusModel.confirmedWalker) != 0 && Integer.parseInt(requestStatusModel.status) == 1) {

            if (Integer.parseInt(requestStatusModel.isWalkerStarted) == 1) {
                status = Const.IS_WALKER_STARTED;
            }
            if (Integer.parseInt(requestStatusModel.isWalkerArrived) == 1) {
                status = Const.IS_WALKER_ARRIVED;

            }
            if (Integer.parseInt(requestStatusModel.isWalkStarted) == 1) {
                status = Const.IS_WALK_STARTED;
            }
            if (Integer.parseInt(requestStatusModel.isCompleted) == 1) {
                status = Const.IS_COMPLETED;
            }
            if (Integer.parseInt(requestStatusModel.isWalkerRated) == 1) {
                status = Const.IS_WALKER_RATED;
            }
            if (status == Const.NO_REQUEST) { //When walker confirmed and the status is no satisfied in above state Driver Accepted
                status = 11;
            }
            if (requestStatusModel.isCancelled != null) {
                if (Integer.parseInt(requestStatusModel.isCancelled) == 1) {
                    status = Const.IS_WALKER_CANC;
                }
            }

        }
        CustomerApplication.preferenceHelper.putPromoCode((requestStatusModel.promoCode));
        String time = (requestStatusModel.startTime);
        if (!TextUtils.isEmpty(time)) {
            try {
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.ENGLISH).parse(time);
                AppLog.Log("TAG", "START DATE---->" + date.toString()
                        + " month:" + date.getMonth() + "time" + date.getTime());
                CustomerApplication.preferenceHelper.putRequestTime(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (requestStatusModel.destLatitude != null) {
            if (requestStatusModel.destLatitude != 0) {
                CustomerApplication.preferenceHelper.putClientDestination(new LatLng(requestStatusModel.destLatitude, requestStatusModel.destLongitude));
            }
        }
        return status;
    }

    public RequestStatusModel getDriverDetail(String response) {
        AppLog.Log(Const.TAG, response);
        return getSingleObject(response, RequestStatusModel.class);

    }


    //    public ReferralModel parseReffrelCode(String response) {
//        Reffral reffral = null;
//            ReferralModel referralModel=getSingleObject(response,ReferralModel.class);
//            reffral = new Reffral();
//            reffral.setReferralCode(referralModel.referralCode);
//            reffral.setAmountSpent(referralModel.amountSpent);
//            reffral.setBalanceAmount(referralModel.balanceAmount);
//            reffral.setTotalReferrals(referralModel.totalRefferal);
//            reffral.setAmountEarned(referralModel.amountEarned);
//        return reffral;
//    }
    public ReferralModel parseReffrelCode(String response) {
        return getSingleObject(response, ReferralModel.class);
    }

    public String getMessage(String response) {
        if (TextUtils.isEmpty(response))
            return "";
        AppLog.Log(Const.TAG, response);
        ApplyPromoModel applyPromoModel = getSingleObject(response, ApplyPromoModel.class);
        return applyPromoModel.error;
    }

    public List<CardsModel.Payment> parseCards(String response, List<CardsModel.Payment> list) {
        list.clear();
        if (TextUtils.isEmpty(response)) {
            return list;
        }
        CardsModel cardsModel = getSingleObject(response, CardsModel.class);
        if (cardsModel.success) {
            if (cardsModel.payments.size() > 0) {
                list = cardsModel.payments;
                return cardsModel.payments;
            }
        } else {
            AndyUtils.showToast(ErrorResponse(cardsModel.errorCode), R.id.coordinatorLayout, activity);
        }
        return list;
    }

    public boolean isSuccess(String response) {
        if (TextUtils.isEmpty(response))
            return false;
        try {
            LoginModel loginModel = getSingleObject(response, LoginModel.class);
            if (loginModel.success) {
                return true;
            } else {
                AndyUtils.showToast(loginModel.error, R.id.coordinatorLayout, activity);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<LatLng> parsePathRequest(String response, ArrayList<LatLng> points) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                JSONArray jsonArray = jsonObject
                        .getJSONArray(Const.Params.LOCATION_DATA);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    points.add(new LatLng(Double.parseDouble(json.getString(Const.Params.LATITUDE)),
                            Double.parseDouble(json.getString(Const.Params.LONGITUDE))));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return points;
    }

    public ArrayList<String> parseCountryCodes() {
        String response = "";
        ArrayList<String> list = new ArrayList<String>();
        try {
            response = ReadFiles.readRawFileAsString(activity,
                    R.raw.countrycodes);

            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("phone-code") + " "
                        + object.getString("name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isSuccessWithOTPEdit(String response) {
        AppLog.Log(Const.TAG, "Response PArse Content" + response);
        if (TextUtils.isEmpty(response))
            return false;
        try {
            LoginModel loginModel = getSingleObject(response, LoginModel.class);
            if (loginModel.success) {
                CustomerApplication.preferenceHelper.putUserId(loginModel.user.id + "");
                CustomerApplication.preferenceHelper.putSessionToken(loginModel.user.token);
                CustomerApplication.preferenceHelper.putEmail(loginModel.user.email);
                CustomerApplication.preferenceHelper.putPhoneNo(loginModel.user.phone);
                CustomerApplication.preferenceHelper.putLoginBy(loginModel.user.loginBy);
                CustomerApplication.preferenceHelper.putReferee(loginModel.user.isReferee);
                CustomerApplication.preferenceHelper.putOTPEditId(loginModel.user.id + "");
                CustomerApplication.preferenceHelper.putOTPEditEmail(loginModel.user.email);
                CustomerApplication.preferenceHelper.putOTPEditFname(loginModel.user.firstName);
                CustomerApplication.preferenceHelper.putOTPEditLname(loginModel.user.lastName);
                CustomerApplication.preferenceHelper.putOTPEditPhone(loginModel.user.phone);
                CustomerApplication.preferenceHelper.putOTPEditPicture(loginModel.user.picture);
                CustomerApplication.preferenceHelper.putOTPEditPasswrd(CustomerApplication.preferenceHelper.getPassword());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Route parseRoute(String response, Route routeBean) {

        try {
            Step stepBean;
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("routes");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject innerjObject = jArray.getJSONObject(i);
                if (innerjObject != null) {
                    JSONArray innerJarry = innerjObject.getJSONArray("legs");
                    for (int j = 0; j < innerJarry.length(); j++) {
                        JSONObject jObjectLegs = innerJarry.getJSONObject(j);
                        routeBean.setDistanceText(jObjectLegs.getJSONObject("distance").getString("text"));
                        routeBean.setDistanceValue(jObjectLegs.getJSONObject("distance").getInt("value"));
                        routeBean.setDurationText(jObjectLegs.getJSONObject("duration").getString("text"));
                        routeBean.setDurationValue(jObjectLegs.getJSONObject("duration").getInt("value"));
                        routeBean.setStartAddress(jObjectLegs.getString("start_address"));
                        routeBean.setEndAddress(jObjectLegs.getString("end_address"));
                        routeBean.setStartLat(jObjectLegs.getJSONObject("start_location").getDouble("lat"));
                        routeBean.setStartLon(jObjectLegs.getJSONObject("start_location").getDouble("lng"));
                        routeBean.setEndLat(jObjectLegs.getJSONObject("end_location").getDouble("lat"));
                        routeBean.setEndLon(jObjectLegs.getJSONObject("end_location").getDouble("lng"));
                        JSONArray jstepArray = jObjectLegs
                                .getJSONArray("steps");
                        if (jstepArray != null) {
                            for (int k = 0; k < jstepArray.length(); k++) {
                                stepBean = new Step();
                                JSONObject jStepObject = jstepArray
                                        .getJSONObject(k);
                                if (jStepObject != null) {

                                    stepBean.setHtml_instructions(jStepObject
                                            .getString("html_instructions"));
                                    stepBean.setStrPoint(jStepObject
                                            .getJSONObject("polyline")
                                            .getString("points"));
                                    stepBean.setStartLat(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lat"));
                                    stepBean.setStartLon(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lng"));
                                    stepBean.setEndLat(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lat"));
                                    stepBean.setEndLong(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lng"));

                                    stepBean.setListPoints(new PolyLineUtils()
                                            .decodePoly(stepBean.getStrPoint()));
                                    routeBean.getListStep().add(stepBean);
                                }
                            }
                        }
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routeBean;
    }

    public ArrayList<ApplicationPages> parsePages(
            ArrayList<ApplicationPages> list, String response) {
        list.clear();
        ApplicationPages applicationPages = new ApplicationPages();
        applicationPages.setId(-1);
        applicationPages.setTitle(activity.getResources().getString(R.string.text_profile));
        applicationPages.setData("");
        list.add(applicationPages);
        applicationPages = new ApplicationPages();
        applicationPages.setId(-2);
        applicationPages.setTitle(activity.getResources().getString(R.string.text_cards));
        applicationPages.setData("");
        list.add(applicationPages);/*
      applicationPages = new ApplicationPages();
        applicationPages.setId(-3);
        applicationPages.setTitle(activity.getResources().getString(R.string.text_wallet));
        applicationPages.setData("");
        list.add(applicationPages);
       */ applicationPages = new ApplicationPages();
        applicationPages.setId(-3);
        applicationPages.setTitle(activity.getResources().getString(R.string.text_history));
        applicationPages.setData("");
        list.add(applicationPages);
        applicationPages = new ApplicationPages();
        applicationPages.setId(-4);
        applicationPages.setTitle(activity.getResources().getString(R.string.text_refferal));
        applicationPages.setData("");
        list.add(applicationPages);
        applicationPages = new ApplicationPages();
        applicationPages.setId(-5);
        applicationPages.setTitle(activity.getString(R.string.text_sos));
        applicationPages.setData("");
        list.add(applicationPages);
        applicationPages = new ApplicationPages();
        applicationPages.setId(-6);
        applicationPages.setTitle(activity.getResources().getString(R.string.title_activity_setting));
        applicationPages.setData("");
        list.add(applicationPages);
        if (TextUtils.isEmpty(response)) {
            return list;
        }
        try {
            SuccessModel successModel = getSingleObject(response, SuccessModel.class);
            if (successModel.success) {
                if (successModel.applicationPages.size() > 0) {
                    for (int i = 0; i < successModel.applicationPages.size(); i++) {
                        applicationPages = new ApplicationPages();
                        applicationPages = successModel.applicationPages.get(i);
                        if (applicationPages != null)
                            list.add(applicationPages);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isSuccessRequestRideLater(String response) {
        if (TextUtils.isEmpty(response))
            return false;
        try {
            SuccessModel successModel = getSingleObject(response, SuccessModel.class);
            if (successModel.success) {

                return true;
            } else {
                AndyUtils.showToast(successModel.error, R.id.coordinatorLayout, activity);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String parseNearestDriverDurationString(String response) {
        if (TextUtils.isEmpty(response))
            return "1 min";
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONArray("routes");
            JSONArray jArrSub = jsonArray.getJSONObject(0).getJSONArray("legs");
            return jArrSub.getJSONObject(0).getJSONObject("duration").getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
            return "1 min";
        }
    }

    public ArrayList<String> parseNearByPlaces(String response, ArrayList<String> resultList) {
        try {
            PlacesResultsNames.PlacesResults resultObject;
            PlacesResultsNames placesResultsNames = getSingleObject(response, PlacesResultsNames.class);
//            JSONObject job = new JSONObject(response);
            List<PlacesResultsNames.PlacesResults> resultArr = placesResultsNames.mResults;
            for (int i = 0; i < resultArr.size(); i++) {
                resultObject = resultArr.get(i);
                resultList.add(resultObject.mName);
            }
        } catch (Exception e) {
            AppLog.Log("MainDrawerActivity", "" + e);
        }
        return resultList;
    }

    public ArrayList<AllProviderList> parseAllproviderslist(String response,
                                                            ArrayList<AllProviderList> list) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                JSONArray jsonArray = jsonObject.getJSONArray(WALKERLIST);
                for (int i = 0; i < jsonArray.length(); i++) {
                    AllProviderList providerlist = new AllProviderList();
                    JSONObject typeJson = jsonArray.getJSONObject(i);
                    providerlist.setDriverId(typeJson.getInt(ID));
                    providerlist.setFirstName(typeJson.getString(Const.Params.FIRSTNAME));
                    providerlist.setLastName(typeJson.getString(Const.Params.LAST_NAME));
                    providerlist.setPhone(typeJson.getString(Const.Params.PHONE));
                    providerlist.setEmail(typeJson.getString(Const.Params.EMAIL));
                    providerlist.setLatitude(typeJson.getDouble(Const.Params.LATITUDE));
                    providerlist.setLongitude(typeJson.getDouble(Const.Params.LONGITUDE));
                    providerlist.setBasePrice(typeJson.getDouble(BASE_PRICE));
                    providerlist.setBaseDistance(typeJson.getInt(BASE_DISTANCE));
                    providerlist.setUnit(typeJson.getString(UNIT));
                    providerlist.setIcon(typeJson.getString(ICON));
                    providerlist.setVehicleTypeId(typeJson.getInt(Const.Params.TAXI_TYPE));
                    providerlist.setName(typeJson.getString(NAME));
                    providerlist.setPricePerUnitDistance(typeJson
                            .getDouble(PRICE_PER_UNIT_DISTANCE));
                    providerlist.setPricePerUnitTime(typeJson
                            .getDouble(PRICE_PER_UNIT_TIME));
                    providerlist.setMinFare(typeJson.optString(MIN_FARE));
                    providerlist.setMaxSize(typeJson.optString(MAX_SIZE));
                    providerlist.setCarModel(typeJson.getString(Const.Params.TAXI_MODEL));
                    providerlist.setCarNumber(typeJson.getString(Const.Params.TAXI_NUMBER));
                    providerlist.setDuration(typeJson.getString(Const.Params.TAXI_DURATION));
                    list.add(providerlist);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;

    }

    public ArrayList<HistoryModel.Request> parseHistory(String response, ArrayList<HistoryModel.Request> list) {
        list = new ArrayList<>();
        if (TextUtils.isEmpty(response)) {
            return list;
        }
        HistoryModel historyModel = getSingleObject(response, HistoryModel.class);
        if (historyModel.success) {
            if (historyModel.requests.size() > 0) {
                list = historyModel.requests;
                return historyModel.requests;
            }
        }
        return list;
    }

    public String ErrorResponse(int errorCode) {
        String messages = null;
        switch (errorCode) {
            case 600:
                messages = activity.getString(R.string.error_600);
                break;
            case 601:
                messages = activity.getString(R.string.error_601);
                break;
            case 602:
                messages = activity.getString(R.string.error_602);
                break;
            case 603:
                messages = activity.getString(R.string.error_603);
                break;
            case 604:
                messages = activity.getString(R.string.error_604);
                break;
            case 605:
                messages = activity.getString(R.string.error_605);
                break;
            case 606:
                messages = activity.getString(R.string.error_606);
                break;
            case 607:
                messages = activity.getString(R.string.error_607);
                break;
            case 608:
                messages = activity.getString(R.string.error_608);
                break;
            case 609:
                messages = activity.getString(R.string.error_609);
                break;
            case 610:
                messages = activity.getString(R.string.error_610);
                break;
            case 611:
                messages = activity.getString(R.string.error_611);
                break;
            case 612:
                messages = activity.getString(R.string.error_612);
                break;
            case 613:
                messages = activity.getString(R.string.error_613);
                break;
            case 614:
                messages = activity.getString(R.string.error_614);
                break;
            case 615:
                messages = activity.getString(R.string.error_615);
                break;
            case 616:
                messages = activity.getString(R.string.error_616);
                break;
            case 617:
                messages = activity.getString(R.string.error_617);
                break;
            case 618:
                messages = activity.getString(R.string.error_618);
                break;
            case 619:
                messages = activity.getString(R.string.error_619);
                break;
            case 620:
                messages = activity.getString(R.string.error_620);
                break;
            case 621:
                messages = activity.getString(R.string.error_621);
                break;
            case 622:
                messages = activity.getString(R.string.error_622);
                break;
            case 623:
                messages = activity.getString(R.string.error_623);
                break;
            case 624:
                messages = activity.getString(R.string.error_624);
                break;
            case 625:
                messages = activity.getString(R.string.error_625);
                break;
            case 626:
                messages = activity.getString(R.string.error_626);
                break;
            case 627:
                messages = activity.getString(R.string.error_627);
                break;
            case 628:
                messages = activity.getString(R.string.error_628);
                break;
            case 629:
                messages = activity.getString(R.string.error_629);
                break;
            case 630:
                messages = activity.getString(R.string.error_630);
                break;
            case 631:
                messages = activity.getString(R.string.error_631);
                break;
            case 632:
                messages = activity.getString(R.string.error_632);
                break;
            case 633:
                messages = activity.getString(R.string.error_633);
                break;
            case 634:
                messages = activity.getString(R.string.error_634);
                break;
            case 635:
                messages = activity.getString(R.string.error_635);
                break;
            case 636:
                messages = activity.getString(R.string.error_636);
                break;
            case 637:
                messages = activity.getString(R.string.error_637);
                break;
            case 638:
                messages = activity.getString(R.string.error_638);
                break;
            case 639:
                messages = activity.getString(R.string.error_639);
                break;
            case 640:
                messages = activity.getString(R.string.error_640);
                break;
            case 641:
                messages = activity.getString(R.string.error_641);
                break;
            case 642:
                messages = activity.getString(R.string.error_642);
                break;
            case 643:
                messages = activity.getString(R.string.error_643);
                break;
            case 644:
                messages = activity.getString(R.string.error_644);
                break;
            case 645:
                messages = activity.getString(R.string.error_645);
                break;
            case 646:
                messages = activity.getString(R.string.error_646);
                break;
            case 647:
                messages = activity.getString(R.string.error_647);
                break;

        }
        return messages;
    }


}

