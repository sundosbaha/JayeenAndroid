package com.jayeen.driver.gcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.maputills.Utils;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.newmodels.CheckChangeStateModel;
import com.jayeen.driver.newmodels.IncomingRequest;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;

import java.util.HashMap;
import java.util.List;

import static com.jayeen.driver.DriverApplication.requestQueue;
import static com.jayeen.driver.utills.CommonUtilities.TAG;

/**
 * Created by root on 7/2/18.
 */

public class AcceptRejectSerice extends Service implements AsyncTaskCompleteListener, Response.ErrorListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLog.Log("keys", "onStartCommand");
        if (intent != null) {
            String requestData = intent.getStringExtra("requestData");
            String message = intent.getStringExtra("message");
            if (message != null && requestData != null) {
                AppLog.Log("keys", "IntentNot Null");



                if (message.matches("Accepted")) {
                    Intent intentEND_ = new Intent("END_");
                    intentEND_.putExtra("message", "App Restarted");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentEND_);

                    respondRequest(1, requestData);
//                    Toast.makeText(getBaseContext(),"Accepted", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentEND_ = new Intent("END_");
                    intentEND_.putExtra("message", "App Restarted");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentEND_);

//                    Toast.makeText(getBaseContext(),"Rejected", Toast.LENGTH_SHORT).show();
                    respondRequest(0, requestData);

                }
            }
        }
        return Service.START_STICKY;
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        AppLog.Log("keys", "onDestroy");
    }

    private void respondRequest(int status, String reqestData) {
        AppLog.Log("keys", "API call");
        IncomingRequest newRequestModel = Utils.getSingleObject(reqestData, IncomingRequest.class);
        RequestDetail newRequestDetail = null;
        if (newRequestModel != null) {
            newRequestDetail = Utils.parsePushNewRequest(newRequestModel);
        }
        if (!AndyUtils.isNetworkAvailable(this) || newRequestDetail == null) {
            Toast.makeText(getBaseContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.RESPOND_REQUESTS);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        try {
            map.put(AndyConstants.Params.REQUEST_ID,
                    String.valueOf(newRequestDetail.getRequestId()));
        } catch (Exception e) {
            e.getMessage();
            map.put(AndyConstants.Params.REQUEST_ID,
                    String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        }
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.ACCEPTED, String.valueOf(status));
        map.put(AndyConstants.Params.REASONFORREJECT, status == 1 ? "" : "Canceled");

        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                AndyConstants.ServiceCode.RESPOND_REQUEST, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AppLog.Log("keys", "API response");
        switch (serviceCode) {
            case AndyConstants.ServiceCode.RESPOND_REQUEST:
                AppLog.Log(TAG, "respond Request Response :" + response);
                Utils.removeNotification(this);
                AndyUtils.removeCustomProgressDialog();
                CheckChangeStateModel checkChangeStateModel = Utils.getSingleObject(response, CheckChangeStateModel.class);




                if (checkChangeStateModel.mSuccess) {
//                     End activity
                    Intent intent = new Intent(this, MapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    this.stopSelf();
                }
        }
    }
//    private void isAppOnForeground() {
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        if (appProcesses == null) {//send Broadcast for accept or reject
//            return ;
//        }
//        final String packageName = getPackageName();    // open Application
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
////            appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
//            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
//                getApplication().
//            }
//        }
//
//    }
    @Override
    public void onErrorResponse(VolleyError error) {

    }


}
