package com.jayeen.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.jayeen.driver.adapter.DrawerAdapter;
import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.db.DBHelper;
import com.jayeen.driver.db.DatabaseAdapter;
import com.jayeen.driver.fragment.ClientRequestFragmentNew;
import com.jayeen.driver.fragment.FeedbackFrament;
import com.jayeen.driver.fragment.JobFragment;
import com.jayeen.driver.model.ApplicationPages;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.newmodels.DriverModel;
import com.jayeen.driver.newmodels.GetRequestModel;
import com.jayeen.driver.newmodels.IncomingRequest;
import com.jayeen.driver.newmodels.InstantJobModel;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.newmodels.RequestInProgressModel;
import com.jayeen.driver.newmodels.WalkerCompleteModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.ParseContent;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontTextView;
import com.jayeen.driver.widget.MyFontTextViewDrawer;

import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener, AsyncTaskCompleteListener {
    private DrawerLayout drawerLayout;
    private DrawerAdapter adapter;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private MyFontTextView tvApprovedClose;
    private Button tvLogoutOk, tvLogoutCancel;
    private Button tvExitOk, tvExitCancel;
    private ParseContent parseContent;
    private TaxiParseContent taxiparseContent;
    private static final String TAG = "MapActivity";
    private ArrayList<ApplicationPages> arrayListApplicationPages;
    private boolean isDataRecieved = false, isRecieverRegistered = false,
            isNetDialogShowing = false, isGpsDialogShowing = false;
    private AlertDialog internetDialog, gpsAlertDialog;
    private LocationManager manager;
    private DBHelper dbHelper;
    private DatabaseAdapter databaseAdapter;
    private ImageView ivMenuProfile;
    private MyFontTextViewDrawer tvMenuName;
    private boolean isLogoutCheck = true, isApprovedCheck = true;
    private BroadcastReceiver mReceiver;
    private Dialog mDialog;
    private View headerView;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayListApplicationPages = new ArrayList<ApplicationPages>();
        parseContent = new ParseContent(this);
        taxiparseContent = new TaxiParseContent(this);
        mTitle = mDrawerTitle = getTitle();
        btnActionMenu.setVisibility(View.VISIBLE);
        btnActionMenu.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        btnNotification.setVisibility(View.GONE);
        setActionBarIcon(R.drawable.menu);
        moveDrawerToTop();
        initActionBar();
        initDrawer();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        if (DriverApplication.preferenceHelper.getIsApproved() != null
                && DriverApplication.preferenceHelper.getIsApproved().equals("1")) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    public void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout.setDrawerListener(createDrawerToggle());
        drawerList.setOnItemClickListener(this);
        adapter = new DrawerAdapter(this, arrayListApplicationPages);
        headerView = getLayoutInflater().inflate(R.layout.menu_drawer, null);
        drawerList.addHeaderView(headerView);
        drawerList.setAdapter(adapter);
        ivMenuProfile = (ImageView) headerView.findViewById(R.id.ivMenuProfile);
        tvMenuName = (MyFontTextViewDrawer) headerView.findViewById(R.id.tvMenuName);
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
    }

    private DrawerListener createDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.menu, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int state) {
            }
        };
        return mDrawerToggle;
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.activity_map, null); // "null" is important.
        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.llContent); // This is the container we
        // defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.left_drawer).setPadding(0, (actionBar.getHeight() + getStatusBarHeight()), 0, 0);
        // Make the drawer replace the first child
        decor.addView(drawer);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void checkStatus() {
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_dialog_loading), false);
        if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
            AppLog.Log(TAG, "onResume getreuest in progress");
            getRequestsInProgress();
        } else {
            AppLog.Log(TAG, "onResume check request status");
            checkRequestStatus();
        }
    }

    private void getMenuItems() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.APPLICATION_PAGES);
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map,
                AndyConstants.ServiceCode.APPLICATION_PAGES, this, this));
    }

    public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.Log(TAG, "On recieve GPS provider broadcast");
            final LocationManager manager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onResume();
                removeGpsDialog();
            } else {
                if (isGpsDialogShowing) {
                    return;
                }
                ShowGpsDialog();
            }

        }
    };
    public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeWIFIInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
            if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
                removeInternetDialog();
            } else {
                if (isNetDialogShowing) {
                    return;
                }
                showInternetDialog();
            }
        }
    };

    private void registerIsApproved() {
        IntentFilter intentFilter = new IntentFilter("IS_APPROVED");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AppLog.Log("MapFragment", "IS_APPROVED");
                if (DriverApplication.preferenceHelper.getIsApproved() != null && DriverApplication.preferenceHelper.getIsApproved().equals("1")) {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                        getRequestsInProgress();
                    }
                }

            }
        };
        registerReceiver(mReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("END_"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            Toast.makeText(getBaseContext(),"_ _ App Restart _ _",Toast.LENGTH_LONG).show();
            finish();

        }
    };


    private void unregisterIsApproved() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void ShowGpsDialog() {
        AndyUtils.removeCustomProgressDialog();
        isGpsDialogShowing = true;
        AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(MapActivity.this);
        gpsBuilder.setCancelable(false);
        gpsBuilder.setTitle(getString(R.string.dialog_no_gps))
                .setMessage(getString(R.string.dialog_no_gps_messgae))
                .setPositiveButton(getString(R.string.dialog_enable_gps),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                removeGpsDialog();
                            }
                        }).setNegativeButton(getString(R.string.dialog_exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeGpsDialog();
                        finish();
                    }
                });
        gpsAlertDialog = gpsBuilder.create();
        gpsAlertDialog.show();
    }

    private void removeGpsDialog() {
        if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
            gpsAlertDialog.dismiss();
            isGpsDialogShowing = false;
            gpsAlertDialog = null;

        }
    }

    private void removeInternetDialog() {
        if (internetDialog != null && internetDialog.isShowing()) {
            internetDialog.dismiss();
            isNetDialogShowing = false;
            internetDialog = null;

        }
    }

    private void showInternetDialog() {
        AndyUtils.removeCustomProgressDialog();
        isNetDialogShowing = true;
        AlertDialog.Builder internetBuilder = new AlertDialog.Builder(MapActivity.this);
        internetBuilder.setCancelable(false);
        internetBuilder
                .setTitle(getString(R.string.dialog_no_internet))
                .setMessage(getString(R.string.dialog_no_inter_message))
                .setPositiveButton(getString(R.string.dialog_enable_3g),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                removeInternetDialog();
                            }
                        })
                .setNeutralButton(getString(R.string.dialog_enable_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                removeInternetDialog();
                            }
                        }).setNegativeButton(getString(R.string.dialog_exit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeInternetDialog();
                        finish();
                    }
                });
        internetDialog = internetBuilder.create();
        internetDialog.show();
    }

    @Override
    protected void onResume() {

        if (DriverApplication.preferenceHelper.getIsLangChanged()) {
            finish();
            startActivity(getIntent());
            DriverApplication.preferenceHelper.putIsLangChanged(false);
        }
        super.onResume();
        getMenuItems();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ShowGpsDialog();
        } else {
            removeGpsDialog();
        }
        registerReceiver(internetConnectionReciever, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(GpsChangeReceiver, new IntentFilter(
                LocationManager.PROVIDERS_CHANGED_ACTION));
        isRecieverRegistered = true;
        if (AndyUtils.isNetworkAvailable(this) && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!isDataRecieved) {
                Log.i("MAINDRAWER", "Inside Internet and GPS Condition Ok");
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // FOR API 6.0 DEVICES
                    Log.i("MAINDRAWERRESUME", "Inside  6.0 Devices");
                    if (!checkPermission()) {    // check for runtime permission
                        requestPermission();
                    } else {  //permission granted
                        Log.i("MAINDRAWERRESUME", "Permission already granted In Resume\n" + "Before Call CheckStatus");
                        checkStatus();
                        startLocationUpdateService();
                    }
                } else { // LESS THAN API 6.0 DEVICES
                    //isDataRecieved = true;
                    Log.i("MAINDRAWERRESUME", "Inside Lower than 6.0 Devices\n" + "Before Call CheckStatus Lower Than 6.0");
                    checkStatus();
                    startLocationUpdateService();
                }
            }
//            else {
//                checkStatus();
//            }
        }

        registerIsApproved();
        DriverModel user = databaseAdapter.getUser();
        if (user != null) {
            if (user.picture != null && !TextUtils.isEmpty(user.picture))
                AndyUtils.Picasso(this, user.picture, ivMenuProfile, R.drawable.user);
            tvMenuName.setText(user.firstName + " " + user.lastName);
        }


    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AndyUtils.showToast(getString(R.string.text_granted_accessPermission), R.id.coordinatorLayout, MapActivity.this);
                    Log.i("MAINDRAWERRESUME", "Permission granted In RequestPermissionResult");
                } else {
                    AndyUtils.showToast(getString(R.string.text_denied_accessPermission), R.id.coordinatorLayout, MapActivity.this);
                    Log.i("MAINDRAWERRESUME", "Permission Denied In RequestPermissionResult");
                    finish();

                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Mint.closeSession(this);
        AndyUtils.removeCustomProgressDialog();
        // Mint.closeSession(this);
        if (isRecieverRegistered) {
            unregisterReceiver(internetConnectionReciever);
            unregisterReceiver(GpsChangeReceiver);

        }
        unregisterIsApproved();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
        if (position == 0) {
            return;
        }
        drawerLayout.closeDrawer(drawerList);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (DriverApplication.preferenceHelper.getIsTripOn()) {
                    if (position == 1) {
                        Toast.makeText(MapActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                    }
//                    else if (position == 2) {
//                        startActivity(new Intent(MapActivity.this,
//                                WalletActivity.class));
//                    }
                    else if (position == 2) {
                        startActivity(new Intent(MapActivity.this,
                                HistoryActivity.class));
                    }
                    else if (position == 3) {
                        Toast.makeText(MapActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                    } else if (position == 4) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "" + getString(R.string.text_iam_using) + " " + getString(R.string.app_name) + getString(R.string.why_not_install_app) + " "
                                        + getString(R.string.app_name) + " " + getString(R.string.play_link)
                                        + getPackageName());
                        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                getString(R.string.app_name) + getString(R.string.text_app));
                        sendIntent.setType("text/plain");

                        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_app)));
                    } else if (position == 5) {
//                    new InstantJobFragment().show(getSupportFragmentManager(), "");
                        doInstantJob("");
                    } else if (position == arrayListApplicationPages.size()) {
                        if (isLogoutCheck) {
                            openLogoutDialog();
                            isLogoutCheck = true;
                            return;
                        }
                    } else {
                        Intent intent = new Intent(MapActivity.this,
                                MenuDescActivity.class);
                        intent.putExtra(AndyConstants.Params.TITLE,
                                arrayListApplicationPages.get(position - 1)
                                        .getTitle());
                        intent.putExtra(AndyConstants.Params.CONTENT,
                                arrayListApplicationPages.get(position - 1)
                                        .getData());
                        startActivity(intent);
                    }
                } else {
                    if (position == 1) {
                        startActivity(new Intent(MapActivity.this,
                                ProfileActivity.class));
                    }
//                    else if (position == 2) {
//                        startActivity(new Intent(MapActivity.this,
//                                WalletActivity.class));
//                    }
                    else if (position == 2) {
                        startActivity(new Intent(MapActivity.this,
                                HistoryActivity.class));
                    }else if (position == 3) {
                        startActivityForResult(new Intent(MapActivity.this, SettingActivity.class), 5000);
                    } else if (position == 4) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "" + getString(R.string.text_iam_using) + " " + getString(R.string.app_name) + getString(R.string.why_not_install_app) + " "
                                        + getString(R.string.app_name) + " " + getString(R.string.play_link)
                                        + getPackageName());
                        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                getString(R.string.app_name) + getString(R.string.text_app));
                        sendIntent.setType("text/plain");

                        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_app)));
                    } else if (position == 5) {
//                    new InstantJobFragment().show(getSupportFragmentManager(), "");
                        doInstantJob("");
                    } else if (position == arrayListApplicationPages.size()) {
                        if (isLogoutCheck) {
                            openLogoutDialog();
                            isLogoutCheck = true;
                            return;
                        }
                    } else {
                        Intent intent = new Intent(MapActivity.this,
                                MenuDescActivity.class);
                        intent.putExtra(AndyConstants.Params.TITLE,
                                arrayListApplicationPages.get(position - 1)
                                        .getTitle());
                        intent.putExtra(AndyConstants.Params.CONTENT,
                                arrayListApplicationPages.get(position - 1)
                                        .getData());
                        startActivity(intent);
                    }
                }
            }
        }, 350);

    }

    public void doInstantJob(String phoneNumber) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_dialog_loading), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.INSTANT_JOB);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.LATITUDE, DriverApplication.preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE, DriverApplication.preferenceHelper.getWalkerLongitude());
        map.put(AndyConstants.Params.PHONE_NUMBER, phoneNumber);
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.INSTANT_JOB, this, this));
    }

    public void openLogoutDialog() {
        final Dialog mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_bg)));
        mDialog.setContentView(R.layout.logout);
        tvLogoutOk = (Button) mDialog.findViewById(R.id.tvLogoutOk);
        tvLogoutCancel = (Button) mDialog
                .findViewById(R.id.tvLogoutCancel);
        tvLogoutOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
                mDialog.dismiss();

            }
        });
        tvLogoutCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isLogoutCheck = true;
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000 && resultCode == 2) {
            startActivity(new Intent(MapActivity.this, MapActivity.class));
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnActionMenu:
                drawerLayout.openDrawer(drawerList);
                break;

            case R.id.tvTitle:
                drawerLayout.openDrawer(drawerList);
            default:
                break;
        }
    }

    public void logout() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGOUT);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_dialog_loading), false);
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.LOGOUT, this, this));
    }

    public void getRequestsInProgress() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }

        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_dialog_loading), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.REQUEST_IN_PROGRESS
                + AndyConstants.Params.ID + "="
                + DriverApplication.preferenceHelper.getUserId() + "&"
                + AndyConstants.Params.TOKEN + "="
                + DriverApplication.preferenceHelper.getSessionToken());
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.REQUEST_IN_PROGRESS, this, this));
    }

    public void checkRequestStatus() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_dialog_request), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.CHECK_REQUEST_STATUS
                + AndyConstants.Params.ID + "="
                + DriverApplication.preferenceHelper.getUserId() + "&"
                + AndyConstants.Params.TOKEN + "="
                + DriverApplication.preferenceHelper.getSessionToken() + "&"
                + AndyConstants.Params.REQUEST_ID + "="
                + DriverApplication.preferenceHelper.getRequestId());
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, this, this));
    }

    public void openApprovedDialog() {
        mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.provider_approve_dialog);
        mDialog.setCancelable(false);
        tvApprovedClose = (MyFontTextView) mDialog
                .findViewById(R.id.tvApprovedClose);
        tvApprovedClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
//                preferenceHelper.ClearPref();
                finish();
            }
        });
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!drawerLayout.isShown())
            openExitDialog();
        else
            drawerLayout.closeDrawers();
        openExitDialog();
    }

    public void openExitDialog() {


        final Dialog mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_bg)));
        mDialog.setContentView(R.layout.exit_layout);
        tvExitOk = (Button) mDialog.findViewById(R.id.tvExitOk);
        tvExitCancel = (Button) mDialog.findViewById(R.id.tvExitCancel);
        tvExitOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        tvExitCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        super.onTaskCompleted(response, serviceCode);

        switch (serviceCode) {
            case AndyConstants.ServiceCode.REQUEST_IN_PROGRESS:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(TAG, "requestInProgress Response :" + response);
                RequestInProgressModel requestInProgressModel = taxiparseContent.getSingleObject(response, RequestInProgressModel.class);
                if (requestInProgressModel.mSuccess) {
                    if (!(requestInProgressModel.mIsApproved == 1)) {
                        if (isApprovedCheck) {
                            openApprovedDialog();
                            isApprovedCheck = false;
                        }
                        return;
                    }
                } else {
                    if (requestInProgressModel.error_code == AndyConstants.REQUEST_ID_NOT_FOUND) {
                        AndyUtils.removeCustomProgressDialog();
                        DriverApplication.preferenceHelper.clearRequestData();
                        getMenuItems();
                        addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                    } else if (requestInProgressModel.error_code == AndyConstants.INVALID_TOKEN || requestInProgressModel.error_code == 622
                            || requestInProgressModel.error_code == AndyConstants.INVALID_TOKEN2) {
                        if (DriverApplication.preferenceHelper.getLoginBy().equalsIgnoreCase(AndyConstants.MANUAL))
                            login();
                        else
                            loginSocial(DriverApplication.preferenceHelper.getUserId(), DriverApplication.preferenceHelper.getLoginBy());
                    }
                    return;
                }
                AndyUtils.removeCustomProgressDialog();
                int requestId = requestInProgressModel.mRequestId;
                DriverApplication.preferenceHelper.putRequestId(requestId);
                if (requestId == AndyConstants.NO_REQUEST) {
                    getMenuItems();
                    addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                } else {
                    checkRequestStatus();
                }
                break;
            case AndyConstants.ServiceCode.CHECK_REQUEST_STATUS:
                AppLog.Log(TAG, "checkrequeststatus Response :" + response);
                GetRequestModel getRequestModel = taxiparseContent.getSingleObject(response, GetRequestModel.class);
                if (!getRequestModel.mSuccess) {
                    if (getRequestModel.error_code == AndyConstants.REQUEST_ID_NOT_FOUND || getRequestModel.error_code == AndyConstants.SERVICE_ID_NOT_FOUND) {
                        DriverApplication.preferenceHelper.clearRequestData();
                        AndyUtils.removeCustomProgressDialog();
                        addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                    } else if (getRequestModel.error_code == AndyConstants.INVALID_TOKEN || getRequestModel.error_code == AndyConstants.INVALID_TOKEN2) {
                        if (DriverApplication.preferenceHelper.getLoginBy().equalsIgnoreCase(AndyConstants.MANUAL))
                            login();
                        else
                            loginSocial(DriverApplication.preferenceHelper.getUserId(), DriverApplication.preferenceHelper.getLoginBy());
                    } else if (getRequestModel.error_code == AndyConstants.REQUEST_ID_MISMATCH) {
                        DriverApplication.preferenceHelper.clearRequestData();
                        AndyUtils.removeCustomProgressDialog();
                        addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                    } else if (getRequestModel.error_code == AndyConstants.SERVICE_ID_NOT_FOUND2) {
                        AndyUtils.removeCustomProgressDialog();
                    }
                    return;
                }
                AndyUtils.removeCustomProgressDialog();
                Bundle bundle = new Bundle();
                JobFragment jobFragment = new JobFragment();
                RequestDetail requestDetail = taxiparseContent.parseRequestStatus(getRequestModel);
                if (requestDetail == null) {
                    return;
                }
                getMenuItems();//This is to identify that this trip is Instant Trip by request Type==Hand--else--Normal @mukesh
                if (getRequestModel.request.request_type.equalsIgnoreCase(AndyConstants.Params.INSTANT_TRIP))
                    DriverApplication.preferenceHelper.putInstantJobId(DriverApplication.preferenceHelper.getRequestId());
                switch (requestDetail.getJobStatus()) {
                    case AndyConstants.NO_REQUEST:
                        DriverApplication.preferenceHelper.clearRequestData();
                        Intent i = new Intent(this, MapActivity.class);
                        startActivity(i);
                        break;
                    case AndyConstants.IS_WALKER_STARTED:
                        bundle.putInt(AndyConstants.JOB_STATUS,
                                AndyConstants.IS_WALKER_STARTED);
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                requestDetail);
                        jobFragment.setArguments(bundle);
                        addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
                                true);
                        break;
                    case AndyConstants.IS_WALKER_ARRIVED:
                        bundle.putInt(AndyConstants.JOB_STATUS,
                                AndyConstants.IS_WALKER_ARRIVED);
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                requestDetail);
                        jobFragment.setArguments(bundle);
                        addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
                                true);
                        break;
                    case AndyConstants.IS_WALK_STARTED:
                        bundle.putInt(AndyConstants.JOB_STATUS,
                                3);
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                requestDetail);
                        jobFragment.setArguments(bundle);
                        addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
                                true);
                        break;
                    case AndyConstants.IS_WALK_COMPLETED:
                        bundle.putInt(AndyConstants.JOB_STATUS,
                                AndyConstants.IS_WALK_COMPLETED);
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                requestDetail);
                        jobFragment.setArguments(bundle);
                        addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
                                true);
                        break;
                    case AndyConstants.IS_DOG_RATED:
                        WalkerCompleteModel walkerCompleteModel = taxiparseContent.getSingleObject(response, WalkerCompleteModel.class);
                        FeedbackFrament feedbackFrament = new FeedbackFrament();
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                walkerCompleteModel);
                        bundle.putString(AndyConstants.Params.TIME, 0 + " "
                                + getResources().getString(R.string.text_mins));
                        bundle.putString(AndyConstants.Params.DISTANCE, 0 + " "
                                + getResources().getString(R.string.text_miles));
                        feedbackFrament.setArguments(bundle);
                        addFragment(feedbackFrament, false,
                                AndyConstants.FEEDBACK_FRAGMENT_TAG, true);
                        break;

                    case AndyConstants.ServiceCode.APPLICATION_PAGES:

                        break;
                    default:
                        break;
                }

                break;

            case AndyConstants.ServiceCode.LOGIN:
                AndyUtils.removeCustomProgressDialog();
                LoginModel loginModel = taxiparseContent.getSingleObject(response, LoginModel.class);
                if (taxiparseContent.isSuccessWithId(loginModel)) {
                    checkStatus();
                } else {
                    DriverApplication.preferenceHelper.Logout();
                    goToMainActivity();
                    stopLocationUpdateService();
                }
                break;
            case AndyConstants.ServiceCode.INSTANT_JOB:
                AndyUtils.removeCustomProgressDialog();
                InstantJobModel jobModel = taxiparseContent.getSingleObject(response, InstantJobModel.class);
                if (jobModel.success) {
                    DriverApplication.preferenceHelper.putRequestId(jobModel.requests.id);
                    DriverApplication.preferenceHelper.putInstantJobId(jobModel.requests.id);
                    DriverApplication.preferenceHelper.putTimerMin("0");
                    DriverApplication.preferenceHelper.putTotalTimerMin("0");
                    DriverApplication.preferenceHelper.putTripTimerMin("0");
                    checkRequestStatus();
                } else {
                    Toast.makeText(MapActivity.this, jobModel.error, Toast.LENGTH_LONG).show();
                }

                break;

            case AndyConstants.ServiceCode.APPLICATION_PAGES:
                AppLog.Log(TAG, "Menuitems Response::" + response);
                arrayListApplicationPages = parseContent.parsePages(arrayListApplicationPages, response);
                ApplicationPages applicationPages = new ApplicationPages();
                applicationPages.setData("");
                applicationPages.setId(-arrayListApplicationPages.size());
                applicationPages.setIcon("");
                applicationPages.setTitle(getString(R.string.text_logout));
                arrayListApplicationPages.add(applicationPages);
                adapter.notifyDataSetChanged();
                isDataRecieved = true;
                break;
            case AndyConstants.ServiceCode.LOGOUT:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log("Logout Response", response);
                if (parseContent.isSuccess(response)) {
                    DriverApplication.preferenceHelper.Logout();
                    goToMainActivity();
                    stopLocationUpdateService();
                }
                break;
            default:
                break;
        }
    }

    private IncomingRequest changeGetRequestModel_To_IncommingRequest(GetRequestModel getRequestModel) {
//        getRequestModel.mIncomingRequests.get(0)
        IncomingRequest result = new IncomingRequest();
        result.mRequestData.mPaymentMode = getRequestModel.request.paymentMode;
        result.mRequestData.mOwner.mAddress = getRequestModel.request.owner.address;
        result.mRequestData.mOwner.mDestLatitude = getRequestModel.request.owner.destLatitude;
        result.mRequestData.mOwner.mDestLongitude = getRequestModel.request.owner.destLongitude;
        result.mRequestData.mOwner.mLatitude = getRequestModel.request.owner.latitude;
        result.mRequestData.mOwner.mLongitude = getRequestModel.request.owner.longitude;
        result.mRequestData.mOwner.mName = getRequestModel.request.owner.name;
        result.mRequestData.mOwner.mPhone = getRequestModel.request.owner.phone;
        result.mRequestData.mOwner.mPicture = getRequestModel.request.owner.picture;
        result.mRequestData.mOwner.mRating = getRequestModel.request.owner.rating;
        result.mRequestData.mOwner.mNumRating = getRequestModel.request.owner.numRating;
        return result;
    }

    public void login() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", "", true);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
        map.put(AndyConstants.Params.EMAIL, DriverApplication.preferenceHelper.getEmail());
        map.put(AndyConstants.Params.PASSWORD, DriverApplication.preferenceHelper.getPassword());
        map.put(AndyConstants.Params.DEVICE_TYPE, AndyConstants.DEVICE_TYPE_ANDROID);
        map.put(AndyConstants.Params.DEVICE_TOKEN, DriverApplication.preferenceHelper.getDeviceToken());
        map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.POST, map, AndyConstants.ServiceCode.LOGIN, this, this));
    }

    public void loginSocial(String id, String loginType) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MapActivity.this);
            return;
        }
        AndyUtils.showSimpleProgressDialog(this);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
        map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
        map.put(AndyConstants.Params.DEVICE_TYPE, AndyConstants.DEVICE_TYPE_ANDROID);
        map.put(AndyConstants.Params.DEVICE_TOKEN, DriverApplication.preferenceHelper.getDeviceToken());
        map.put(AndyConstants.Params.LOGIN_BY, loginType);
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.POST, map, AndyConstants.ServiceCode.LOGIN, this, this));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, MapActivity.this);
    }


    public void display_toast(String toast_message) {
        try {
            AndyUtils.showToast(toast_message, R.id.coordinatorLayout, MapActivity.this);
        } catch (Exception e) {
            AppLog.Log("MapActivity", e.getMessage());
        }
    }


}
