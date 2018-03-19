package com.jayeen.driver.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.maps.model.LatLng;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MainActivity;
import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.fragment.ClientRequestFragmentNew;
import com.jayeen.driver.fragment.JobFragment;
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
import com.jayeen.driver.utills.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.jayeen.driver.DriverApplication.requestQueue;


/**
 * Created by user on 3/15/2016.
 */
public class MyGcmListenerService extends GcmListenerService implements
        AsyncTaskCompleteListener, Response.ErrorListener {

    private static final String TAG = "MyGcmListenerService";
//    protected TaxiParseContent taxiparseContent;
//    protected MapActivity mapActivity;

    /**
     * Called when message is received.
     * data Data bundle containing message data as key/value pairs.
     * For Set of keys use data.keySet().
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
//        taxiparseContent=new TaxiParseContent(getApplicationContext());
        String message = data.getString("message");
//        mapActivity = (MapActivity) getBaseContext();
//        taxiparseContent = new TaxiParseContent(mapActivity);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }
        //String message = intent.getExtras().getString("message");


        AppLog.Log(TAG, "Message is: --->" + message);
        String team = data.getString("team");
        Intent pushIntent = new Intent(AndyConstants.NEW_REQUEST);
        pushIntent.putExtra(AndyConstants.NEW_REQUEST, team);
        // String messageBedge = intent.getExtras().getString("bedge");
        CommonUtilities.displayMessage(this, message);
        // notifies user
        if (!TextUtils.isEmpty(message)) {

            try {
                JSONObject jsonObject = new JSONObject(team);
                AppLog.Log(TAG, "GCM Respons is: --->" + jsonObject);
                if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 1) {
                    //  New Request Notification
                    generateNewNotification(this, message, jsonObject + "", true, true);
                } else if ((jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 2) || (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 6)) {
                    DriverApplication.preferenceHelper.clearRequestData();
                    Intent i = new Intent("CANCEL_REQUEST");
                    this.sendBroadcast(i);
                    generateNewNotification(this, message, false);
                } else if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 3) {
                    DriverApplication.preferenceHelper.putPaymentType(jsonObject.getJSONObject(
                            "owner_data").getInt("payment_type"));
                    Intent i = new Intent("PAYMENT_MODE");
                    this.sendBroadcast(i);
                } else if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 9 || jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 8) {
                    Intent i = new Intent("CHANGE_ACTIVE");
                    this.sendBroadcast(i);
                    generateNewNotification(this, message, false);
                } else if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 5) {

                    DriverApplication.preferenceHelper.putIsApproved(jsonObject
                            .getString(AndyConstants.Params.IS_APPROVED));
                    Intent i = new Intent("IS_APPROVED");
                    generateNewNotification(this, message, false);
                    this.sendBroadcast(i);

                } else {
                    JSONObject ownerObject = jsonObject.getJSONObject("request_data").getJSONObject("owner");
                    try {
                        if (ownerObject.has("dest_latitude"))
                            if (ownerObject.getString("dest_latitude").length() != 0) {
                                LatLng destLatLng = new LatLng(ownerObject.getDouble("dest_latitude"),
                                        ownerObject.getDouble("dest_longitude"));
                                DriverApplication.preferenceHelper.putClientDestination(destLatLng);
                                Intent i = new Intent("CLIENT_DESTINATION");
                                this.sendBroadcast(i);
                            } else {
                                Intent i = new Intent("CANCEL_REQUEST");
                                this.sendBroadcast(i);
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
        wakeLock.release();
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     */


    private void generateNewNotification(Context context, String message, String content, boolean isNewRequest, boolean newNotify) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentAccept ;
        Intent intentCancel ;
        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if (isAppOnForeground()) {
            intentAccept = new Intent();
            intentCancel = new Intent();
            intentAccept.setAction(AndyConstants.NEW_REQUEST);
            intentAccept.putExtra("message", "Accepted");
            intentAccept.putExtra(AndyConstants.NEW_REQUEST, content);
            intentCancel.setAction(AndyConstants.NEW_REQUEST);
            intentCancel.putExtra("message", "Rejected");
            intentCancel.putExtra(AndyConstants.NEW_REQUEST, content);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(getNotificationIcon(builder))
                    .setTicker(res.getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
/*
                    .addAction(R.drawable.success, getString(R.string.text_accept),
                            isAppOnForeground() ?
                                    PendingIntent.getBroadcast(this, 0, intentAccept, PendingIntent.FLAG_CANCEL_CURRENT)
                                    : PendingIntent.getService(this, 0, intentAccept, PendingIntent.FLAG_UPDATE_CURRENT)

                    ) // #0
                    .addAction(R.drawable.error, getString(R.string.text_reject),
                            isAppOnForeground() ? PendingIntent.getBroadcast(this, 1, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT)
                                    : PendingIntent.getService(this, 0, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT))  // #1
*/
                    .setContentTitle(res.getString(R.string.app_name))
                    .setContentText(message);
            Uri uri = (!isAppOnForeground() && isNewRequest) ? Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep_while_notification) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);
        } else {

            intentAccept = new Intent(this, AcceptRejectSerice.class);
            intentAccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentCancel = new Intent(this, AcceptRejectSerice.class);
            intentCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentAccept.setAction("Request_Response_GCM");
            intentAccept.putExtra("message", "Accepted");
            intentAccept.putExtra("requestData", content);
            intentCancel.setAction("Request_Response_GCM");
            intentCancel.putExtra("message", "Rejected");
            intentCancel.putExtra("requestData", content);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(getNotificationIcon(builder))
                    .setTicker(res.getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                    .addAction(R.drawable.success, getString(R.string.text_accept),PendingIntent.getService(this, 0, intentAccept, PendingIntent.FLAG_CANCEL_CURRENT)) // #0
                    .addAction(R.drawable.error, getString(R.string.text_reject),PendingIntent.getService(this, 1, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT))  // #1
                    .setContentTitle(res.getString(R.string.app_name))
                    .setContentText(message);
            Uri uri = (!isAppOnForeground() && isNewRequest) ? Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep_while_notification) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);
        }
        /*
        intent2.setAction("Request_Response_GCM");
        intent2.putExtra("message", "Rejected");
        intent2.putExtra("requestData", content);*/



        Notification n = builder.build();
        nm.notify(AndyConstants.NOTIFICATION_ID, n);
    }

    private void generateNewNotification(Context context, String message, boolean isNewRequest) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(getNotificationIcon(builder))
                .setTicker(res.getString(R.string.app_name))
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(res.getString(R.string.app_name))
                .setContentText(message);
        Uri uri = (!isAppOnForeground() && isNewRequest) ? Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep_while_notification) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        Notification n = builder.build();
        nm.notify(AndyConstants.NOTIFICATION_ID, n);
    }

    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {//send Broadcast for accept or reject
            return false;
        }
        final String packageName = getPackageName();    // open Application
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected void onDeletedMessages(Context context, int total) {
        AppLog.Log(TAG, "Received deleted messages notification");
        String message = "message deleted " + total;
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNewNotification(context, message, false);
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = getResources().getColor(R.color.colorPrimary);
            notificationBuilder.setColor(color);
            return R.drawable.pushicon;
        } else {
            return R.drawable.ic_launcher;
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case AndyConstants.ServiceCode.RESPOND_REQUEST:
                AppLog.Log(TAG, "respond Request Response :" + response);
                Utils.removeNotification(this);
                AndyUtils.removeCustomProgressDialog();
                CheckChangeStateModel checkChangeStateModel = Utils.getSingleObject(response, CheckChangeStateModel.class);

                if (checkChangeStateModel.mSuccess) {
                    startActivity(new Intent(this, MapActivity.class));
                }
        }
    }
    /*private void removeNotification() {
        try {
            NotificationManager manager = (NotificationManager) getBaseContext()
                    .getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(AndyConstants.NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        registerReceiver(receiver, new IntentFilter("Request_Response_GCM"));
    }*/

    /*@Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        AppLog.Log("keys","MessageSent:");
    }

*/
    @Override
    public void onCreate() {
        super.onCreate();
//        AppLog.Log("keys","ServiceStarted:");
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("Request_Response_GCM"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        AppLog.Log("keys","ServiceStoped:");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    /*BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.Log("keys","ReceivedSomething:");
            if (intent != null) {
                String requestData=intent.getStringExtra("requestData");
                String message=intent.getStringExtra("message");
                if (message!= null&&requestData!= null) {
                    if (message.matches("Accepted")) {
                        respondRequest(1,requestData);
//                        Toast.makeText(getBaseContext(), "Received:" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        respondRequest(0,requestData);
                    }
                } *//*else
                    Toast.makeText(getBaseContext(), "Received", Toast.LENGTH_SHORT).show();*//*
            }
        }
    };*/


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, getString(R.string.error_contact_server), Toast.LENGTH_SHORT).show();
    }
}