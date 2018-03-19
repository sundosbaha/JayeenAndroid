package com.jayeen.customer.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jayeen.customer.utils.CommonUtilities;
import com.jayeen.customer.R;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.jayeen.customer.utils.PreferenceHelper;

import java.io.IOException;

/**
 * Created by user on 3/15/2016.
 */
public class GCMIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    BroadcastReceiver mHandleMessageReceiver;

    public GCMIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            new PreferenceHelper(this).putDeviceToken(token);
            AppLog.Log(Const.TAG, "Intent service GCM REGID" + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
          //  subscribeTopics(token);



            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
    }


    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        publishResults(token, Activity.RESULT_OK);
    }

    private void publishResults(String token, int resultOk) {
        Log.i(TAG,"Inside Intenet Called with pusblish result");
        Intent intent = new Intent(CommonUtilities.DISPLAY_REGISTER_GCM);
        intent.putExtra(CommonUtilities.RESULT, resultOk);
        intent.putExtra(CommonUtilities.REGID, token);
        sendBroadcast(intent);
    }


    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
