package com.jayeen.driver;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.jayeen.driver.utills.PreferenceHelper;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mukesh on 1/17/17.
 */

public class DriverApplication extends MultiDexApplication {
    public static PreferenceHelper preferenceHelper;
    public static RequestQueue requestQueue,locationRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        initFabric();
        preferenceHelper = new PreferenceHelper(getApplicationContext());
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        locationRequestQueue = new RequestQueue(cache, network);
        locationRequestQueue.start();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        configureLanguage();
    }

    private void initFabric() {
        try {
            Fabric.with(this, new Crashlytics());
            // You can call any combination of these three methods
            Crashlytics.setUserIdentifier("12346");
            Crashlytics.setUserEmail("nplusindia@fabric.io");
            Crashlytics.setUserName("TaxiAppz Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureLanguage() {
        preferenceHelper = new PreferenceHelper(getApplicationContext());
        String language = ((preferenceHelper.getlang_long() == null) ? "Arabic" : preferenceHelper.getlang_long());
        String loc;
        if (language.equalsIgnoreCase("English")) {
            loc = "en";
        } else {
            loc = "ar";
        }
        Locale myLocale = new Locale(loc);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        preferenceHelper.putlang_short(loc);
        preferenceHelper.putlang_long(language);
    }
}
