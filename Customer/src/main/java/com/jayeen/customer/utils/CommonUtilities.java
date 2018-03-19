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
package com.jayeen.customer.utils;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import static android.content.Context.POWER_SERVICE;

public final class CommonUtilities {
    public static final String SENDER_ID = "393778827509";
    public static final String TAG = "GCMDemo";
    public static final String DISPLAY_MESSAGE_ACTION = "com.uberorg.DISPLAY_MESSAGE";
    public static final String DISPLAY_REGISTER_GCM = "com.uberorg.REGISTER_GCM";
    public static final String EXTRA_MESSAGE = "message";
    public static final String RESULT = "result";
    public static final String REGID = "regid";
    public static PowerManager.WakeLock screenOn;
    public static final Integer TIME_TO_LIVE = 5000;

    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
        if (screenOn == null)
            screenOn = ((PowerManager) context.getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "" + context.getPackageName());
        screenOn.acquire(TIME_TO_LIVE);
    }
}
