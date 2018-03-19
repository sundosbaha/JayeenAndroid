package com.jayeen.driver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.jayeen.driver.DriverApplication.requestQueue;

public class Splash extends AppCompatActivity {
    String[] Array_permissions = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.ic_launcher);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(imageView);
        linearLayout.setGravity(Gravity.CENTER);
        setContentView(R.layout.splash);
        getLocationInfo();
     /*   try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            doAfterSplashWork();
        }*/
        new Handler().postDelayed(runnable, 3000);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            doAfterSplashWork();
        }

    };

    public boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.text_playservices_unavailable), Toast.LENGTH_LONG).show();
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
            startActivityForResult(myIntent, 0);
            finish();
        } else {
            AppLog.Log("Splash", "Denied Permissions");
            alertCalling();
        }

    }

    public void getLocationInfo() {
        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    String.format(AndyConstants.ServiceType.COUNTRY_CODE_URL),
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void SetCountryCode(String ContryCode) {
        AndyConstants.COUNTRY_CODE = ContryCode;
    }

    void doAfterSplashWork() {
        if (checkPlayServices(Splash.this)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Splash", "Permission is granted");
                    Intent myIntent = new Intent(Splash.this, MainActivity.class);
                    startActivityForResult(myIntent, 0);
                    getLocationInfo();
                    finish();
                } else {
                    Log.v("Splash", "Permission is revoked");
//                    ActivityCompat.requestPermissions(Splash.this, Array_permissions, 1);
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
        new AlertDialog.Builder(this, R.style.myDialog)
                .setTitle(getString(R.string.text_permission))
                .setCancelable(false)
                .setMessage(getString(R.string.text_revoke_permission))
                .setPositiveButton(getString(R.string.text_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                        ActivityCompat.requestPermissions(Splash.this, Array_permissions, 1);

                    }
                })
                .setNegativeButton(getString(R.string.dialog_exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        finish();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void openBrowser(String str_url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(str_url));
            startActivity(browserIntent);
        } catch (Exception e) {
            AppLog.Log(getClass().getName(), e.getMessage());
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }
}