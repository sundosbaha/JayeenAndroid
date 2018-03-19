package com.jayeen.driver.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;
import com.jayeen.driver.db.DatabaseAdapter;

public class PreferenceHelper {

    private SharedPreferences app_prefs;
    private final String USER_ID = "user_id";
    private final String DEVICE_TOKEN = "device_token";
    private final String SESSION_TOKEN = "session_token";
    private final String REQUEST_ID = "request_id";
    private final String INSTANT_REQUEST_ID = "instant_request_id";
    private final String WALKER_LATITUDE = "walkerlatitude";
    private final String WALKER_LONGITUDE = "walkerlongitude";
    private final String PASSWORD = "password";
    private final String EMAIL = "email";
    private final String LOGIN_BY = "login_by";
    private final String TYPE = "type";
    private final String SOCIAL_ID = "social_id";
    private final String REQUEST_TIME = "request_time";
    private final String WAITING_REQUEST_TIME = "waiting_request_time";
    private final String TRIP_START = "trip_start";
    private final String DISTANCE = "distance";
    private final String UNIT = "unit";
    private Context context;
    private final String PAYMENT_TYPE = "paymentType";
    private final String DEST_LAT = "dest_lat";
    private final String DEST_LNG = "dest_lng";
    private final String IS_APPROVED = "is_approved";
    private final String SOUND_AVAILABILITY = "sound_availability";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String IS_NAVIGATE = "is_navigate";
    private final String DIST_LATITUDE = "dist_latitude";
    private final String DIST_LONGITUDE = "dist_longitude";
    private final String IS_OFFLINE = "is_offline";
    private final String IS_ACTIVE = "is_active";
    private final String LANG_SHORT = "lang_short";
    private final String LANG_LONG = "lan_long";
    private final String SHARE_TIME = "time";
    private final String PRE_MIN = "premin";
    private final String TRIP_PRE_MIN = "trippremin";
    private final String TOTAL_TRIP_PRE_MIN = "totaltrippremin";
    private final String PRE_SEC = "presec";
    private final String TIMER_START = "startTime";
    private final String TRIP_TIMER_START = "tripStartTime";
    private final String TIMER_PAUSE = "PauseTime";
    private final String TRIP_MIN_PAUSED = "trip_min_paused";
    private final String TRIP_SEC_PAUSED = "trip_sec_paused";
    private final String TRIP_TIMER_PAUSE = "TripPauseTime";
    private final String IS_WAIT = "is_wait";
    private final String IS_TRIP = "is_trip";
    private final String IS_LANGUAGE_CHANGED = "is_lang_changed";
    private DatabaseAdapter databaseAdapter;

    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences(AndyConstants.PREF_NAME,
                Context.MODE_PRIVATE);
        this.context = context;
        databaseAdapter = new DatabaseAdapter(context);
    }

    public void putDestinationLatitude(String latitude) {
        Editor edit = app_prefs.edit();
        edit.putString(DIST_LATITUDE, latitude);
        edit.commit();

    }

    public String getDestinationLatitude() {
        return app_prefs.getString(DIST_LATITUDE, "");
    }

    public void putDestinationLongitude(String longitude) {
        Editor edit = app_prefs.edit();
        edit.putString(DIST_LONGITUDE, longitude);
        edit.commit();

    }

    public String getDestinationLongitude() {
        return app_prefs.getString(DIST_LONGITUDE, "");
    }

    public boolean isNavigate() {
        return app_prefs.getBoolean(IS_NAVIGATE, false);
    }

    public void putIsNavigate(boolean navigate) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_NAVIGATE, navigate);
        edit.commit();
    }

    public boolean getDriverOffline() {
        return app_prefs.getBoolean(IS_OFFLINE, false);
    }

    public void putDriverOffline(boolean offline) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_OFFLINE, offline);
        edit.commit();
    }

    public boolean getIsActive() {
        return app_prefs.getBoolean(IS_ACTIVE, false);
    }

    public void putIsActive(boolean isActive) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_ACTIVE, isActive);
        edit.commit();
    }

    public void putLatitude(double latiDouble) {
        Editor edit = app_prefs.edit();
        edit.putFloat(LATITUDE, (float) latiDouble);
        edit.commit();
    }

    public double getLatitude() {
        return app_prefs.getFloat(LATITUDE, 0.0f);
    }

    public void putLongitude(double longiDouble) {
        Editor edit = app_prefs.edit();
        edit.putFloat(LONGITUDE, (float) longiDouble);
        edit.commit();
    }

    public double getLongitude() {
        return app_prefs.getFloat(LONGITUDE, 0.0f);
    }

    public void putIsApproved(String approved) {
        Editor edit = app_prefs.edit();
        edit.putString(IS_APPROVED, approved);
        edit.commit();
    }

    public String getIsApproved() {
        return app_prefs.getString(IS_APPROVED, null);
    }

    public void putUserId(String userId) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_ID, userId);
        edit.commit();
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

    public void putRequestId(int reqId) {
        Editor edit = app_prefs.edit();
        edit.putInt(REQUEST_ID, reqId);
        edit.commit();
    }

    public int getRequestId() {
        return app_prefs.getInt(REQUEST_ID, AndyConstants.NO_REQUEST);
    }

    public void putInstantJobId(int reqId) {
        Editor edit = app_prefs.edit();
        edit.putInt(INSTANT_REQUEST_ID, reqId);
        edit.commit();
    }

    public int getInstantJobId() {
        return app_prefs.getInt(INSTANT_REQUEST_ID, AndyConstants.NO_REQUEST);
    }

    public void putDistance(Float distance) {
        Editor edit = app_prefs.edit();
        edit.putFloat(DISTANCE, distance);
        edit.commit();
    }

    public float getDistance() {
        return app_prefs.getFloat(DISTANCE, 0.0f);
    }

    public void putUnit(String unit) {
        Editor edit = app_prefs.edit();
        edit.putString(UNIT, unit);
        edit.commit();
    }

    public String getUnit() {
        return app_prefs.getString(UNIT, " ");
    }

    public void putIsTripStart(boolean status) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(TRIP_START, status);
        edit.commit();
    }

    public boolean getIsTripStart() {
        return app_prefs.getBoolean(TRIP_START, false);
    }

    public void putWalkerLatitude(String latitude) {
        Editor edit = app_prefs.edit();
        edit.putString(WALKER_LATITUDE, latitude);
        edit.commit();
    }

    public String getWalkerLatitude() {
        return app_prefs.getString(WALKER_LATITUDE, null);
    }

    public void putWalkerLongitude(String longitude) {
        Editor edit = app_prefs.edit();
        edit.putString(WALKER_LONGITUDE, longitude);
        edit.commit();
    }

    public String getWalkerLongitude() {
        return app_prefs.getString(WALKER_LONGITUDE, null);
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

    public void putLoginBy(String loginBy) {
        Editor edit = app_prefs.edit();
        edit.putString(LOGIN_BY, loginBy);
        edit.commit();
    }


    public void putType(Integer Type) {
        Editor edit = app_prefs.edit();
        edit.putInt(TYPE, Type);
        edit.commit();
    }

    public Integer getTYPE() {
        return app_prefs.getInt(TYPE,0);
    }

    public String getLoginBy() {
        return app_prefs.getString(LOGIN_BY, AndyConstants.MANUAL);
    }

    public void putSocialId(String socialId) {
        Editor edit = app_prefs.edit();
        edit.putString(SOCIAL_ID, socialId);
        edit.commit();
    }

    public String getSocialId() {
        return app_prefs.getString(SOCIAL_ID, null);
    }

    public void putRequestTime(long time) {
        Editor edit = app_prefs.edit();
        edit.putLong(REQUEST_TIME, time);
        edit.commit();
    }

    public long getRequestTime() {
        return app_prefs.getLong(REQUEST_TIME, AndyConstants.NO_TIME);
    }

    public void putWaitingRequestTime(long time) {
        Editor edit = app_prefs.edit();
        edit.putLong(WAITING_REQUEST_TIME, time);
        edit.commit();
    }

    public long getWaitingRequestTime() {
        return app_prefs.getLong(WAITING_REQUEST_TIME, AndyConstants.NO_TIME);
    }

    public void putSoundAvailability(Boolean soundAvailability) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(SOUND_AVAILABILITY, soundAvailability);
        edit.commit();
    }

    public Boolean getSoundAvailability() {
        return app_prefs.getBoolean(SOUND_AVAILABILITY, true);
    }

    public void putPaymentType(int type) {
        Editor edit = app_prefs.edit();
        edit.putInt(PAYMENT_TYPE, type);
        edit.commit();
    }

    public int getPaymentType() {
        return app_prefs.getInt(PAYMENT_TYPE, -1);
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
        if (app_prefs.getString(DEST_LAT, null) != null) {
            return new LatLng(Double.parseDouble(app_prefs.getString(DEST_LAT,
                    "0")), Double.parseDouble(app_prefs
                    .getString(DEST_LNG, "0")));
        } else {
            return null;
        }
    }

    public void clearRequestData() {
        putRequestId(AndyConstants.NO_REQUEST);
        putRequestTime(AndyConstants.NO_TIME);
        putInstantJobId(AndyConstants.NO_TIME);
        putIsTripStart(false);
        putClientDestination(null);
        putPaymentType(-1);
        putDestinationLatitude("");
        putDestinationLongitude("");
        putTimerMin("0");
        putTotalTimerMin("0");
        putTripTimerMin("0");
        putIsNavigate(false);
        putDistance((float) 0.0);
        putDistanceTime("0");
        // new DBHelper(context).deleteAllLocations();
    }

    public void Logout() {
        clearRequestData();
        putUserId(null);
        putSessionToken(null);
        putLoginBy(AndyConstants.MANUAL);
        putSocialId(null);
        putClientDestination(null);
        databaseAdapter.deleteUser();
//        new DBHelper(context).deleteUser();

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

    public void ClearPref() {
        SharedPreferences.Editor myyShared = app_prefs.edit();
        myyShared.clear();
        myyShared.apply();
        myyShared.commit();
    }

    public String getDistanceTime() {
        return app_prefs.getString(SHARE_TIME, "0");
    }

    public void putDistanceTime(String Time) {
        Editor edit = app_prefs.edit();
        edit.putString(SHARE_TIME, Time);
        edit.commit();
    }

    public String getTimerMin() {
        return app_prefs.getString(PRE_MIN, "0");
    }

    public void putTimerMin(String Time) {
        Editor edit = app_prefs.edit();
        edit.putString(PRE_MIN, Time);
        edit.commit();
    }

    public String getTotalTimerMin() {
        return app_prefs.getString(TOTAL_TRIP_PRE_MIN, "0");
    }

    public void putTotalTimerMin(String Time) {
        Editor edit = app_prefs.edit();
        edit.putString(TOTAL_TRIP_PRE_MIN, Time);
        edit.commit();
    }

    public String getTripTimerMin() {
        return app_prefs.getString(TRIP_PRE_MIN, "0");
    }

    public void putTripTimerMin(String Time) {
        Editor edit = app_prefs.edit();
        edit.putString(TRIP_PRE_MIN, Time);
        edit.commit();
    }

    public String getTimerSec() {
        return app_prefs.getString(PRE_SEC, "0");
    }

    public void putTimerSec(String sec) {
        Editor edit = app_prefs.edit();
        edit.putString(PRE_SEC, sec);
        edit.commit();
    }


//from pandiyan

    public long getTimerPause() {
        return app_prefs.getLong(TIMER_PAUSE, 0);
    }

    public void putTimerPause(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TIMER_PAUSE, sec);
        edit.commit();
    }

    public long getTripTimerPause() {
        return app_prefs.getLong(TIMER_PAUSE, 0);
    }

    public void putTripMinPaused(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TRIP_MIN_PAUSED, sec);
        edit.commit();
    }

    public long getTripMinPaused() {
        return app_prefs.getLong(TRIP_MIN_PAUSED, 0);
    }

    public void putTripSecPaused(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TRIP_SEC_PAUSED, sec);
        edit.commit();
    }

    public long getTripSecPaused() {
        return app_prefs.getLong(TRIP_SEC_PAUSED, 0);
    }

    public void putTripTimerPause(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TRIP_TIMER_PAUSE, sec);
        edit.commit();
    }

    public long getStartTime() {
        return app_prefs.getLong(TIMER_START, 0);
    }

    public void putStartTime(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TIMER_START, sec);
        edit.commit();
    }

    public long getTripStartTime() {
        return app_prefs.getLong(TRIP_TIMER_START, 0);
    }

    public void putTripStartTime(long sec) {
        Editor edit = app_prefs.edit();
        edit.putLong(TRIP_TIMER_START, sec);
        edit.commit();
    }


    public boolean getIsWaitDialogVisible() {
        return app_prefs.getBoolean(IS_WAIT, false);
    }

    public void putIsWaitDialogVisible(boolean isVisible) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_WAIT, isVisible);
        edit.commit();
    }


    public boolean getIsTripOn() {
        return app_prefs.getBoolean(IS_TRIP, false);
    }

    public void putIsTripOn(boolean isVisible) {
        Editor edit = app_prefs.edit();
        edit.putBoolean(IS_TRIP, isVisible);
        edit.commit();
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
