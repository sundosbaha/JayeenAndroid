/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayeen.driver.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jayeen.driver.R;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.utills.CommonUtilities;
import com.jayeen.driver.utills.PreferenceHelper;



public class GCMIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {"global"};


	public GCMIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			// [START register_for_gcm]
			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
					GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			// [END get_token]
			//Toast.makeText(this,"GCM REGISTERED",Toast.LENGTH_SHORT).show();
			Log.i(TAG, "GCM Registration Token: " + token);
			new PreferenceHelper(this).putDeviceToken(token);
			AppLog.Log(TAG, "Intent service GCM REGID" + token);

			sendRegistrationToServer(token);
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
		Intent intent = new Intent(CommonUtilities.DISPLAY_MESSAGE_REGISTER);
		intent.putExtra(CommonUtilities.RESULT, resultOk);
		intent.putExtra(CommonUtilities.REGID, token);
		sendBroadcast(intent);
	}


}
