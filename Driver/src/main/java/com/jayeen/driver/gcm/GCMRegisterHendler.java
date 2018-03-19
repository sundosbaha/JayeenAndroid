package com.jayeen.driver.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jayeen.driver.utills.CommonUtilities;


public class GCMRegisterHendler {

	private Activity activity;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public GCMRegisterHendler(Activity activity,
			BroadcastReceiver mHandleMessageReceiver) {


			this.activity = activity;
			checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");

		activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				CommonUtilities.DISPLAY_MESSAGE_REGISTER));
		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(activity, GCMIntentService.class);
			activity.startService(intent);
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(
					"sender id is null please recompile the app");
		}
	}


	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i("TAG", "This device is not supported.");
				activity.finish();
			}
			return false;
		}
		return true;
	}

}
