package com.jayeen.customer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class Splash extends AppCompatActivity implements Animation.AnimationListener {
    final String[] Array_permissions = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
//                android.Manifest.permission.CAMERA,
//                android.Manifest.permission.READ_SMS,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE

    };

    long SplashTime;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "Splash";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        SplashTime = 0;
//        doAnimation();
        new Handler().postDelayed(runnable, 3000);
      /*  try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            doAfterSplashWork();
        }*/
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            doAfterSplashWork();
        }

    };
    private boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity, getString(R.string.play_unavailable), Toast.LENGTH_LONG).show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            Intent myIntent = new Intent(Splash.this, MainActivity.class);
            getLocationInfo();
            startActivityForResult(myIntent, 0);
            finish();

        } else {
            alertCalling();
        }

    }

    public void getLocationInfo() {
        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    String.format(Const.ServiceType.COUNTRY_CODE_URL),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                SetCountryCode(response.getString("country_code"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Splash", "Error: " + error.getMessage());

                }
            });
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void SetCountryCode(String ContryCode) {
        Const.COUNTRY_CODE = ContryCode;
        AppLog.Log(TAG,ContryCode);
    }


//    void doAnimation() {
//        ImageView image = (ImageView) findViewById(R.id.ic_outter_icon);
//        Animation rollAnimation = AnimationUtils.loadAnimation(this, R.anim.circle_roll);
//        rollAnimation.setAnimationListener(this);
//        image.startAnimation(rollAnimation);
//    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        doAfterSplashWork();
        SplashTime = 0;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
//    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//    checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
//    checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&

    void doAfterSplashWork() {
        if (checkPlayServices(Splash.this)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (
                        checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Log.v("Splash", "Permission is granted");
                    Intent myIntent = new Intent(Splash.this, MainActivity.class);
                    startActivityForResult(myIntent, 0);
                    getLocationInfo();
                    finish();
                } else {
                    Log.v("Splash", "Permission is revoked");
                    alertCalling();
                }
            } else {
                Intent myIntent = new Intent(Splash.this, MainActivity.class);
                getLocationInfo();
                startActivityForResult(myIntent, 0);
                finish();
            }
        } else {

        }
    }

    private void alertCalling() {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle("Permission")
                .setCancelable(false)
                .setMessage("Location and Phone State are mandatory permission please allow to access.\n Are you sure you want to continue ?")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                        ActivityCompat.requestPermissions(Splash.this, Array_permissions, 1);

                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        finish();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        if (!isFinishing())
            ad.show();
    }
}