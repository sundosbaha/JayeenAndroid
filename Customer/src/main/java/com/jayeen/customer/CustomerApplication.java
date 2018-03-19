package com.jayeen.customer;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.crashlytics.android.Crashlytics;
import com.jayeen.customer.utils.PreferenceHelper;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by pandian on 17/1/17.
 */

public class CustomerApplication extends MultiDexApplication {

    public static PreferenceHelper preferenceHelper;
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        initFabric();
        configureLanguage();
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//        requestQueue = Volley.newRequestQueue(this);
    }


    void initFabric() {
        try {
            Fabric.with(this, new Crashlytics());
            logUser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logUser() {
        try {
            // You can call any combination of these three methods
            Crashlytics.setUserIdentifier("12345");
            Crashlytics.setUserEmail("nplusindia@fabric.io");
            Crashlytics.setUserName("Taxiappz User");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureLanguage() {
        preferenceHelper = new PreferenceHelper(getApplicationContext());
        String loc = ((preferenceHelper.getlang_short() == null) ? "ar" : preferenceHelper.getlang_short());
        String language;
        if (loc.equalsIgnoreCase("pt")) {
            language = "Portuguese";
        } else if (loc.equalsIgnoreCase("en")) {
            language = "English";
        } else if (loc.equalsIgnoreCase("fr")) {
            language = "French";
        } else if (loc.equalsIgnoreCase("fa")) {
            language = "Persian";
        } else {
            language = "Arabic";
        }

        Locale myLocale = new Locale(TextUtils.isEmpty(loc) ? "ar" : loc);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        preferenceHelper.putlang_short(loc);
        preferenceHelper.putlang_long(language);
    }
}
