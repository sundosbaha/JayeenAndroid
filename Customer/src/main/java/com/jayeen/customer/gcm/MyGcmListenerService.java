package com.jayeen.customer.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.jayeen.customer.utils.CommonUtilities;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.R;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;


/**
 * Created by user on 3/15/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        String team = data.getString("team");
        AppLog.Log("Notificaton", message);
        AppLog.Log("Team", team);
        String title = data.getString("title");
        Intent pushIntent = new Intent(Const.INTENT_WALKER_STATUS);
        pushIntent.putExtra(Const.EXTRA_WALKER_STATUS, team);
        CommonUtilities.displayMessage(this, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);

        // [START_EXCLUDE]
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]
    private void sendNotification(String message) {


        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification notification = new Notification(icon, message, when);
        String title = this.getString(R.string.app_name);

        Intent notificationIntent = new Intent(this, MainDrawerActivity.class);
        notificationIntent.putExtra("fromNotification", "notification");
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this);

        notification.setSmallIcon(getNotificationIcon(notification))
                //mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(when)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, notification.build());

        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
        wakeLock.release();

    }
    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");

    String message = "message deleted ";
    CommonUtilities.displayMessage(this, message);
        sendNotification(message);
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = getResources().getColor(R.color.colorPrimary);
            int color2= Color.parseColor("#006ec3");
            notificationBuilder.setColor(color);

            return R.drawable.pushicon;

        } else {
            return R.drawable.pushicon;
        }
    }

}
