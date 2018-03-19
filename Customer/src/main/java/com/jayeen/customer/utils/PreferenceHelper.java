package com.jayeen.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class PreferenceHelper {

    private SharedPreferences app_prefs;
    private final String USER_ID = "user_id";
    private final String EMAIL = "email";
    private final String PHONENO = "phone";
    private final String PASSWORD = "password";
    private final String IS_OTPVERIFY = "is_otp_verified";
    private final String DEVICE_TOKEN = "device_token";
    private final String SESSION_TOKEN = "session_token";
    private final String REQUEST_ID = "request_id";
    private final String REQUEST_TIME = "request_time";
    private final String REQUEST_LATITUDE = "request_latitude";
    private final String REQUEST_LONGITUDE = "request_longitude";
    private final String LOGIN_BY = "login_by";
    private final String SOCIAL_ID = "social_id";
    private final String PAYMENT_MODE = "payment_mode";
    private final String DEFAULT_CARD = "default_card";
    private final String DEFAULT_CARD_NO = "default_card_no";
    private final String DEFAULT_CARD_TYPE = "default_card_type";
    private final String BASE_PRICE = "base_cost";
    private final String DISTANCE_PRICE = "distance_cost";
    private final String TIME_PRICE = "time_cost";
    private final String IS_TRIP_STARTED = "is_trip_started";
    private final String HOME_ADDRESS = "home_address";
    private final String WORK_ADDRESS = "work_address";
    private final String DEST_LAT = "dest_lat";
    private final String DEST_LNG = "dest_lng";
    private final String REFEREE = "is_referee";
    private final String PROMO_CODE = "promo_code";
    private final String TOTREQUEST_COUNT = "request_total";
    private final String APP_FIRSTCHECK = "firstlogincheck";
    private final String PICKUPLAT = "pickuplat";
    private final String PICKUPLNG = "pickuplng";
    private final String SOUND_AVAILABILITY = "sound_availability";

    private final String EDITUSERID = "otpuser_id";
    private final String EDITFNAME = "otpfirst_name";
    private final String EDITLNAME = "otplast_name";
    private final String EDITEMAIL = "otpemail";
    private final String EDITPIC = "otppicture";
    private final String EDITPHONE = "otpphone";
    private final String EDITPASS = "otppassword";
    private final String COUNTRYCODE = "countrycodesel";
    private final String EDITNEWCNFPASS = "otpcnfnew_password";

    private final String IS_EDITVERIFY = "is_edit_verified";
    private final String FLAG_ID = "flag_id";
    private final String LANG_SHORT = "lang_short";
    private final String LANG_LONG = "lan_long";


    private final String PICK_LAT = "pick_lat";
    private final String PICK_LONG = "pick_long";
    private final String DROP_LAT = "drop_lat";
    private final String DROP_LONG = "drop_long";
    private final String IS_TRIP = "is_trip";
    private final String IS_LANGUAGE_CHANGED = "is_lang_changed_customer";


    private Context context;


    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences(Const.PREF_NAME,
                Context.MODE_PRIVATE);
        this.context = context;
    }


    public boolean getIsRegEditCheck() {
        return app_prefs.getBoolean(IS_EDITVERIFY, false);
    }

    public void putIsRegEditCheck(boolean editval) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_EDITVERIFY, editval);
        edit.commit();
    }

    public String getlang_short() {
        return app_prefs.getString(LANG_SHORT, null);
    }

    public void putlang_short(String editval) {
        Editor edit = app_prefs.edit();
        edit.putString(LANG_SHORT, editval);
        edit.commit();
    }

    public String getlang_long() {
        return app_prefs.getString(LANG_LONG, null);
    }

    public void putlang_long(String editval) {
        Editor edit = app_prefs.edit();
        edit.putString(LANG_LONG, editval);
        edit.commit();
    }

    public void putEditCountryCode(String firstlogincheck) {
        Editor edit = app_prefs.edit();
        edit.putString(COUNTRYCODE, firstlogincheck);
        edit.commit();
    }

    public String getEditCountryCode() {
        return app_prefs.getString(COUNTRYCODE, null);

    }


    public void putFirsttimeCheck(String firstlogincheck) {
        Editor edit = app_prefs.edit();
        edit.putString(APP_FIRSTCHECK, firstlogincheck);
        edit.commit();
    }

    public String getFirsttimeCheck() {
        return app_prefs.getString(APP_FIRSTCHECK, null);

    }

    public void putOTPEditId(String editusrid) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITUSERID, editusrid);
        edit.commit();
    }

    public String getOTPEditId() {
        return app_prefs.getString(EDITUSERID, null);

    }


    public void putOTPEditFname(String editfname) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITFNAME, editfname);
        edit.commit();
    }

    public String getOTPEditFname() {
        return app_prefs.getString(EDITFNAME, null);

    }

    public void putOTPEditLname(String editlname) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITLNAME, editlname);
        edit.commit();
    }

    public String getOTPEditLname() {
        return app_prefs.getString(EDITLNAME, null);

    }

    public void putOTPEditEmail(String editemail) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITEMAIL, editemail);
        edit.commit();
    }

    public String getOTPEditEmail() {
        return app_prefs.getString(EDITEMAIL, null);

    }
public void clearRegistrationData() {
    putIsRegEditCheck(false);
    putOTPEditEmail("");
    putOTPEditFname("");
    putOTPEditLname("");
    putEditCountryCode("");
    putPassword("") ;
    putOTPEditPhone("") ;
    putOTPEditPhone("") ;

    }


    public void putOTPEditPicture(String editpic) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITPIC, editpic);
        edit.commit();
    }

    public String getOTPEditPicture() {
        return app_prefs.getString(EDITPIC, null);

    }


    public void putOTPEditPhone(String editphno) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITPHONE, editphno);
        edit.commit();
    }

    public String getOTPEditPhone() {
        return app_prefs.getString(EDITPHONE, null);

    }


    public void putOTPEditPasswrd(String editpass) {
        Editor edit = app_prefs.edit();
        edit.putString(EDITPASS, editpass);
        edit.commit();
    }

    public String getOTPEditPasswrd() {
        return app_prefs.getString(EDITPASS, null);

    }


    public void putRequestTotal(int mode) {
        Editor edit = app_prefs.edit();
        edit.putInt(TOTREQUEST_COUNT, mode);
        edit.commit();
    }

    public int getRequestTotal() {
        return app_prefs.getInt(TOTREQUEST_COUNT, 0);
    }

    public void putUserId(String userId) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_ID, userId);
        edit.commit();
    }

    public void putPhoneNo(String email) {
        Editor edit = app_prefs.edit();
        edit.putString(PHONENO, email);
        edit.commit();
    }

    public String getPhoneNo() {
        return app_prefs.getString(PHONENO, null);
    }


    public void putEmail(String email) {
        Editor edit = app_prefs.edit();
        edit.putString(EMAIL, email);
        edit.commit();
    }

    public String getEmail() {
        return app_prefs.getString(EMAIL, null);
    }

    public void putPassword(String password) {
        Editor edit = app_prefs.edit();
        edit.putString(PASSWORD, password);
        edit.commit();
    }

    public String getPassword() {
        return app_prefs.getString(PASSWORD, null);
    }


    public boolean getIsOTPVerify() {
        return app_prefs.getBoolean(IS_OTPVERIFY, false);
    }

    public void putIsOTPVerify(boolean started) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_OTPVERIFY, started);
        edit.commit();
    }

    public void putSoundAvailability(Boolean soundAvailability) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(SOUND_AVAILABILITY, soundAvailability);
        edit.commit();
    }

    public Boolean getSoundAvailability() {
        return app_prefs.getBoolean(SOUND_AVAILABILITY, true);
    }

    public void putBasePrice(float price) {
        Editor edit = app_prefs.edit();
        edit.putFloat(BASE_PRICE, price);
        edit.commit();
    }

    public float getBasePrice() {
        return app_prefs.getFloat(BASE_PRICE, 0f);
    }

    public void putDistancePrice(float price) {
        Editor edit = app_prefs.edit();
        edit.putFloat(DISTANCE_PRICE, price);
        edit.commit();
    }

    public float getDistancePrice() {
        return app_prefs.getFloat(DISTANCE_PRICE, 0f);
    }

    public void putTimePrice(float price) {
        Editor edit = app_prefs.edit();
        edit.putFloat(TIME_PRICE, price);
        edit.commit();
    }

    public float getTimePrice() {
        return app_prefs.getFloat(TIME_PRICE, 0f);
    }

    public void putSocialId(String id) {
        Editor edit = app_prefs.edit();
        edit.putString(SOCIAL_ID, id);
        edit.commit();
    }

    public String getSocialId() {
        return app_prefs.getString(SOCIAL_ID, null);
    }

    public String getUserId() {
        return app_prefs.getString(USER_ID, null);

    }

    public void putDeviceToken(String deviceToken) {
        Editor edit = app_prefs.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.commit();
    }

    public String getDeviceToken() {
        return app_prefs.getString(DEVICE_TOKEN, null);

    }

    public void putSessionToken(String sessionToken) {
        Editor edit = app_prefs.edit();
        edit.putString(SESSION_TOKEN, sessionToken);
        edit.commit();
    }

    public String getSessionToken() {
        return app_prefs.getString(SESSION_TOKEN, null);

    }

    public void putRequestId(int requestId) {
        Editor edit = app_prefs.edit();
        edit.putInt(REQUEST_ID, requestId);
        edit.commit();
    }

    public int getRequestId() {
        return app_prefs.getInt(REQUEST_ID, Const.NO_REQUEST);
    }

    public void putLoginBy(String loginBy) {
        Editor edit = app_prefs.edit();
        edit.putString(LOGIN_BY, loginBy);
        edit.commit();
    }

    public String getLoginBy() {
        return app_prefs.getString(LOGIN_BY, Const.MANUAL);
    }

    public void putRequestTime(long time) {
        Editor edit = app_prefs.edit();
        edit.putLong(REQUEST_TIME, time);
        edit.commit();
    }

    public long getRequestTime() {
        return app_prefs.getLong(REQUEST_TIME, Const.NO_TIME);
    }

    public void putPaymentMode(int mode) {
        Editor edit = app_prefs.edit();
        edit.putInt(PAYMENT_MODE, mode);
        edit.commit();
    }

    public int getPaymentMode() {
        return app_prefs.getInt(PAYMENT_MODE, Const.CASH);
    }

    public void putDefaultCard(int cardId) {
        Editor edit = app_prefs.edit();
        edit.putInt(DEFAULT_CARD, cardId);
        edit.commit();
    }

    public int getDefaultCard() {
        return app_prefs.getInt(DEFAULT_CARD, 0);
    }

    public void putDefaultCardNo(String cardNo) {
        Editor edit = app_prefs.edit();
        edit.putString(DEFAULT_CARD_NO, cardNo);
        edit.commit();
    }

    public String getDefaultCardNo() {
        return app_prefs.getString(DEFAULT_CARD_NO, "");
    }

    public void putDefaultCardType(String cardType) {
        Editor edit = app_prefs.edit();
        edit.putString(DEFAULT_CARD_TYPE, cardType);
        edit.commit();
    }

    public String getDefaultCardType() {
        return app_prefs.getString(DEFAULT_CARD_TYPE, "");
    }

    public boolean getIsTripStarted() {
        return app_prefs.getBoolean(IS_TRIP_STARTED, false);
    }

    public void putIsTripStarted(boolean started) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_TRIP_STARTED, started);
        edit.commit();
    }

    public String getHomeAddress() {
        return app_prefs.getString(HOME_ADDRESS, null);
    }

    public void putHomeAddress(String homeAddress) {
        Editor edit = app_prefs.edit();
        edit.putString(HOME_ADDRESS, homeAddress);
        edit.commit();
    }

    public String getWorkAddress() {
        return app_prefs.getString(WORK_ADDRESS, null);
    }

    public void putWorkAddress(String homeAddress) {
        Editor edit = app_prefs.edit();
        edit.putString(WORK_ADDRESS, homeAddress);
        edit.commit();
    }

    public void putRequestLocation(LatLng latLang) {
        Editor edit = app_prefs.edit();
        edit.putString(REQUEST_LATITUDE, String.valueOf(latLang.latitude));
        edit.putString(REQUEST_LONGITUDE, String.valueOf(latLang.longitude));
        edit.commit();
    }

    public LatLng getRequestLocation() {
        LatLng latLng = new LatLng(0.0, 0.0);
        try {
            latLng = new LatLng(Double.parseDouble(app_prefs.getString(
                    REQUEST_LATITUDE, "0.0")), Double.parseDouble(app_prefs
                    .getString(REQUEST_LONGITUDE, "0.0")));
        } catch (NumberFormatException nfe) {
            latLng = new LatLng(0.0, 0.0);
        }
        return latLng;
    }

    public void putClientDestination(LatLng destination) {
        Editor edit = app_prefs.edit();
        if (destination == null) {
            edit.putString(DEST_LAT, null);
            edit.putString(DEST_LNG, null);
        } else {
            edit.putString(DEST_LAT, String.valueOf(destination.latitude));
            edit.putString(DEST_LNG, String.valueOf(destination.longitude));
        }
        edit.commit();
    }

    public LatLng getClientDestination() {
        try {
            if (app_prefs.getString(DEST_LAT, null) != null) {
                return new LatLng(Double.parseDouble(app_prefs.getString(
                        DEST_LAT, "0.0")), Double.parseDouble(app_prefs
                        .getString(DEST_LNG, "0.0")));
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public void Putpickuplat(String lat) {
        Editor edit = app_prefs.edit();
        edit.putString(PICKUPLAT, lat);
        edit.commit();
    }

    public String Getpickuplat() {
        return app_prefs.getString(PICKUPLAT, null);
    }

    public void PutpickupLng(String lng) {
        Editor edit = app_prefs.edit();
        edit.putString(PICKUPLNG, lng);
        edit.commit();
    }

    public String GetpickupLng() {
        return app_prefs.getString(PICKUPLNG, null);
    }

    public void putReferee(int is_referee) {
        Editor edit = app_prefs.edit();
        edit.putInt(REFEREE, is_referee);
        edit.commit();
    }

    public int getReferee() {
        return app_prefs.getInt(REFEREE, 0);
    }

    public void putPromoCode(String promoCode) {
        Editor edit = app_prefs.edit();
        edit.putString(PROMO_CODE, promoCode);
        edit.commit();
    }

    public String getPromoCode() {
        return app_prefs.getString(PROMO_CODE, null);
    }

    public void clearRequestData() {
        putRequestId(Const.NO_REQUEST);
        putRequestTime(Const.NO_TIME);
        putRequestLocation(new LatLng(0.0, 0.0));
        putIsTripStarted(false);
        putClientDestination(null);
        putPromoCode(null);
        // new DBHelper(context).deleteAllLocations();
    }

    public void Logout() {
        clearRequestData();
        // new DBHelper(context).deleteUser();
        putUserId(null);
        putSessionToken(null);
        putSocialId(null);
        putClientDestination(null);
        putLoginBy(Const.MANUAL);
        putLoginBy(Const.SOCIAL_FACEBOOK);
        putLoginBy(Const.SOCIAL_GOOGLE);
        ClearPref();
    }

    public void ClearPref() {
        SharedPreferences.Editor myyShared = app_prefs.edit();
        myyShared.clear();
        myyShared.apply();
        myyShared.commit();
    }


    public void PutFlagId(int FlagID) {
        Editor edit = app_prefs.edit();
        edit.putInt(FLAG_ID, FlagID);
        edit.commit();
    }

    public int GetFlagId() {
        return app_prefs.getInt(FLAG_ID, 0);
    }


    public void PutPickLat(double pickLat) {
        Editor edit = app_prefs.edit();
        edit.putFloat(PICK_LAT, (float) pickLat);
        edit.commit();
    }

    public float GetPickLat() {
        return app_prefs.getFloat(PICK_LAT, 0.0f);
    }

    public void PutPickLong(double pickLong) {
        Editor edit = app_prefs.edit();
        edit.putFloat(PICK_LONG, (float) pickLong);
        edit.commit();
    }

    public float GetPickLong() {
        return app_prefs.getFloat(PICK_LONG, 0.0f);
    }


    public void PutDropLat(double dropLat) {
        Editor edit = app_prefs.edit();
        edit.putFloat(DROP_LAT, (float) dropLat);
        edit.commit();
    }

    public float GetDropLat() {
        return app_prefs.getFloat(DROP_LAT, 0.0f);
    }

    public void PutDropLong(double dropLong) {
        Editor edit = app_prefs.edit();
        edit.putFloat(DROP_LONG, (float) dropLong);
        edit.commit();
    }

    public float GetDropLong() {
        return app_prefs.getFloat(DROP_LONG, 0.0f);
    }


    public boolean getIsTripOn() {
        return app_prefs.getBoolean(IS_TRIP, false);
    }

    public void putIsTripOn(boolean isVisible) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_TRIP, isVisible);
        edit.commit();
    }

    public void putObject(final String key, final Object value) {
        final Editor editor = app_prefs.edit();
        editor.putString(key, new Gson().toJson(value));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    public <T> T getObject(final String key, final Class modelClass) {
        try {
            return (T) new Gson().fromJson(app_prefs.getString(key, null), modelClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean getIsLangChanged() {
        return app_prefs.getBoolean(IS_LANGUAGE_CHANGED, false);
    }

    public void putIsLangChanged(boolean isChanged) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_LANGUAGE_CHANGED, isChanged);
        edit.commit();
    }

}
