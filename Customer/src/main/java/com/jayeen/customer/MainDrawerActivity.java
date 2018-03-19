package com.jayeen.customer;

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
import android.graphics.Color;
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
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.jayeen.customer.adapter.DrawerAdapter;
import com.jayeen.customer.component.MyFontPopUpTextView;
import com.jayeen.customer.fragments.UberFeedbackFragment;
import com.jayeen.customer.fragments.UberMapFragment;
import com.jayeen.customer.fragments.UberTripFragment;
import com.jayeen.customer.models.ApplicationPages;
import com.jayeen.customer.models.User;
import com.jayeen.customer.newmodels.LoginModel;
import com.jayeen.customer.newmodels.ReferralModel;
import com.jayeen.customer.newmodels.RequestProgressModel;
import com.jayeen.customer.newmodels.RequestStatusModel;
import com.jayeen.customer.newmodels.UserModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;


public class MainDrawerActivity extends ActionBarBaseActivitiy {
    private DrawerAdapter adapter;
    public static DrawerLayout drawerLayout;
    public static ListView listDrawer;
    private ActionBarDrawerToggle drawerToggel;
    private ArrayList<ApplicationPages> listMenu;
    private boolean isDataRecieved = false, isRecieverRegistered = false,
            isNetDialogShowing = false, isGpsDialogShowing = false;
    private AlertDialog internetDialog, gpsAlertDialog, locationAlertDialog;
    private Dialog mDialog_Refferal;
    private LocationManager manager;
    private CircularImageView ivMenuProfile;
    private MyFontPopUpTextView tvMenuName;
    private User user;
    private boolean isLogoutCheck = true;
    private View headerView;
    int prefcount = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean cancel_conform = false;
    public TaxiParseContent parseContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = new TaxiParseContent(this);
        moveDrawerToTop();
        initActionBar();
        btnActionMenu.setVisibility(View.VISIBLE);
        setIcon(R.drawable.notification_box);
        setContentView(R.layout.activity_map);
        Log.i("TAG", "Main Drawer Request Count" + CustomerApplication.preferenceHelper.getRequestTotal());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listDrawer = (ListView) findViewById(R.id.left_drawer);
        listMenu = new ArrayList<ApplicationPages>();
        adapter = new DrawerAdapter(this, listMenu);
        headerView = getLayoutInflater().inflate(R.layout.menu_drawer, null);
        listDrawer.addHeaderView(headerView);
        listDrawer.setAdapter(adapter);


        ivMenuProfile = (CircularImageView) headerView.findViewById(R.id.ivMenuProfile);

        tvMenuName = (MyFontPopUpTextView) headerView
                .findViewById(R.id.tvMenuName);
        getMenuItems();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        listDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v,
                                    final int position, long id) {
                if (position == 0)
                    return;
                drawerLayout.closeDrawer(listDrawer);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        if (CustomerApplication.preferenceHelper.getIsTripOn()) {
                            if (position == 1) {
                                Toast.makeText(MainDrawerActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                            } else if (position == 2) {
                                Toast.makeText(MainDrawerActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                            } /*else if (position == 3) {
                                Toast.makeText(MainDrawerActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                            }*/ else if (position == 3) {
                                startActivity(new Intent(MainDrawerActivity.this, HistoryActivityNew.class));
                            } else if (position == 4) {
                                getReffrelaCode();
                            } else if (position == 5) {
                                doSos();
                            } else if (position == 6) {
                                Toast.makeText(MainDrawerActivity.this, getString(R.string.access_denied_during_trip), Toast.LENGTH_SHORT).show();
                            } else if (position == (listMenu.size())) {
                                if (isLogoutCheck) {
                                    openLogoutDialog();
                                    return;
                                }
                            } else {
                                Intent intent = new Intent(MainDrawerActivity.this,
                                        MenuDescActivity.class);
                                intent.putExtra(Const.Params.TITLE,
                                        listMenu.get(position - 1).getTitle());
                                intent.putExtra(Const.Params.CONTENT,
                                        listMenu.get(position - 1).getData());
                                startActivity(intent);
                            }
                        } else {
                            if (position == 1) {
                                startActivity(new Intent(MainDrawerActivity.this, ProfileActivity.class));
                            } else if (position == 2) {
                                startActivity(new Intent(MainDrawerActivity.this, UberViewPaymentActivity.class));
                            }/* else if (position == 3) {
                                startActivity(new Intent(MainDrawerActivity.this, UberWalletActivity.class));
                            }*/ else if (position == 3) {
                                startActivity(new Intent(MainDrawerActivity.this, HistoryActivityNew.class));
                            } else if (position == 4) {
                                getReffrelaCode();
                            } else if (position == 5) {
                                doSos();
                            } else if (position == 6) {
                                startActivity(new Intent(MainDrawerActivity.this, SettingActivity.class));
                            } else if (position == (listMenu.size())) {
                                if (isLogoutCheck) {
                                    openLogoutDialog();
                                    return;
                                }
                            } else {
                                Intent intent = new Intent(MainDrawerActivity.this,
                                        MenuDescActivity.class);
                                intent.putExtra(Const.Params.TITLE,
                                        listMenu.get(position - 1).getTitle());
                                intent.putExtra(Const.Params.CONTENT,
                                        listMenu.get(position - 1).getData());
                                startActivity(intent);
                            }
                        }
                    }
                }, 350);
            }
        });
    }

    void doSos() {
        mDialogsos = new Dialog(MainDrawerActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialogsos.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialogsos.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.WHITE));
        mDialogsos.setContentView(R.layout.sos_infolayout);
        btnPolice = (Button) mDialogsos.findViewById(R.id.btnPolice);
        btnAmbulance = (Button) mDialogsos.findViewById(R.id.btnAmbulanceservice);
        btnFirestation = (Button) mDialogsos.findViewById(R.id.btnFirestation);
        btnSosBack = (Button) mDialogsos.findViewById(R.id.mainsosback);

        btnSosBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogsos.dismiss();
            }
        });
        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSOSDialog(getResources().getString(R.string.text_police_number), getResources().getString(R.string.text_police));
            }
        });
        btnAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSOSDialog(getResources().getString(R.string.text_ambulance_number), getResources().getString(R.string.text_ambulance));
            }
        });
        btnFirestation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSOSDialog(getResources().getString(R.string.text_firestation_number), getResources().getString(R.string.text_firestation));
            }
        });


        mDialogsos.setCancelable(true);
        if (!isFinishing())
            mDialogsos.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ubermap_activity, menu);
        return true;
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (CustomerApplication.preferenceHelper.getIsLangChanged()) {
            finish();
            startActivity(getIntent());
            CustomerApplication.preferenceHelper.putIsLangChanged(false);
        }
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
        if (AndyUtils.isNetworkAvailable(this)
                && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!isDataRecieved) {
                Log.i("MAINDRAWER", "Inside Internet and GPS Condition Ok");
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // FOR API 6.0 DEVICES
                    Log.i("MAINDRAWERRESUME", "Inside  6.0 Devices");
                    if (!checkPermission()) {    // check for runtime permission
                        requestPermission();
                    } else {  //permission granted
                        Log.i("MAINDRAWERRESUME", "Permission already granted In Resume");
                        isDataRecieved = true;
                        Log.i("MAINDRAWERRESUME", "Before Call CheckStatus");
                        checkStatus();
                    }

                } else { // LESS THAN API 6.0 DEVICES
                    isDataRecieved = true;
                    Log.i("MAINDRAWERRESUME", "Inside Lower than 6.0 Devices");
                    Log.i("MAINDRAWERRESUME", "Before Call CheckStatus Lower Than 6.0");
                    checkStatus();
                }

            }
        }
        UserModel userone = CustomerApplication.preferenceHelper.getObject(Const.USER_DETAILS, UserModel.class);
        if (userone != null) {
            if (!userone.picture.isEmpty())
                AndyUtils.Picasso(this, userone.picture, ivMenuProfile, R.drawable.default_user);
            tvMenuName.setText(userone.firstName + " " + userone.lastName);
        } else {
            CustomerApplication.preferenceHelper.Logout();
            goToMainActivity();
        }

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainDrawerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MainDrawerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MAINDRAWERRESUME", "Permission granted In RequestPermissionResult");
                } else {
                    Log.i("MAINDRAWERRESUME", "Permission Denied In RequestPermissionResult");
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionMenu:
            case R.id.tvTitle:
                drawerLayout.openDrawer(listDrawer);
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        drawerLayout.closeDrawer(listDrawer);
        openExitDialog();
    }

    private void getRequestInProgress() {
        HashMap<String, String> map = new HashMap<String, String>();
        AppLog.Log("MainDrawerActivity",
                Const.ServiceType.REQUEST_IN_PROGRESS + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.URL,
                Const.ServiceType.REQUEST_IN_PROGRESS + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken());
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REQUEST_IN_PROGRESS, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub
        super.onTaskCompleted(response, serviceCode);
        switch (serviceCode) {
            case Const.ServiceCode.GET_REQUEST_IN_PROGRESS:
                // LoginModel loginModel = parseContent.getSingleObject(response, LoginModel.class);
                RequestProgressModel requestProgressModel = parseContent.getSingleObject(response, RequestProgressModel.class);
                if (requestProgressModel != null) {
                    if (requestProgressModel.success) {
                        if (requestProgressModel.requestId == Const.NO_REQUEST) {
                            AndyUtils.removeCustomProgressDialog();
                            gotoMapFragment();
                            CustomerApplication.preferenceHelper.putRequestId(requestProgressModel.requestId);
                        } else {
                            CustomerApplication.preferenceHelper.putRequestId(requestProgressModel.requestId);
                            getRequestStatus(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
                        }
                    } else {
                        if (requestProgressModel.error_code == 622 || requestProgressModel.error_code == Const.TOKEN_EXPIRED) {//Not a Valid Token of Login
                            AndyUtils.showToast(parseContent.ErrorResponse(requestProgressModel.error_code), R.id.coordinatorLayout, this);
                            CustomerApplication.preferenceHelper.Logout();
                            goToMainActivity();
                        } else {
                            AndyUtils.showToast(parseContent.ErrorResponse(requestProgressModel.error_code), R.id.coordinatorLayout, this);
                        }
                    }
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
                break;
            case Const.ServiceCode.GET_REQUEST_STATUS:
                RequestStatusModel requestStatusModel = parseContent.getSingleObject(response, RequestStatusModel.class);
                AndyUtils.removeCustomProgressDialog();
                if (requestStatusModel != null) {
                    if (requestStatusModel.success) {
                        AppLog.Log("MainDrawerActivity", response);
                        parseContent.parseCardAndPriceDetails(requestStatusModel);
                        switch (parseContent.checkRequestStatus(requestStatusModel)) {
                            case 11://Accepted Trip
                            case Const.IS_WALK_STARTED:
                            case Const.IS_WALKER_ARRIVED:
                            case Const.IS_WALKER_STARTED:
                                gotoTripFragment(requestStatusModel);
                                break;
                            case Const.IS_COMPLETED:
                            case Const.IS_WALKER_RATED:
                                gotoRateFragment(requestStatusModel);
                                break;
                            case Const.IS_WALKER_CANC:
                                cancel_conform = true;
                                gotoMap1Fragment(cancel_conform);
                                gotoMapFragment();
                                break;
                            default:
                                gotoMapFragment();
                                break;
                        }
                        getMenuItems();
                        break;
                    } else {
                        if (requestStatusModel.error_code == 622) {//Not a Valid Token of Login
                            AndyUtils.showToast(parseContent.ErrorResponse(requestStatusModel.error_code), R.id.coordinatorLayout, this);
                            CustomerApplication.preferenceHelper.Logout();
                            goToMainActivity();
                        } else if (requestStatusModel.error_code == 628) {
                            gotoMapFragment();
                        } else if (requestStatusModel.error_code == 483) {//No Provider found
                            gotoMapFragment();
                            CustomerApplication.preferenceHelper.putRequestId(Const.NO_REQUEST);
                        }
                        AndyUtils.showToast(parseContent.ErrorResponse(requestStatusModel.error_code), R.id.coordinatorLayout, activity);
                    }
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
            case Const.ServiceCode.LOGIN:
                LoginModel loginModel = parseContent.getSingleObject(response, LoginModel.class);
                if (loginModel != null) {
                    if (loginModel.success) {
                        checkStatus();
                    } else {
                        AndyUtils.showToast(parseContent.ErrorResponse(loginModel.error_code), R.id.coordinatorLayout, activity);
                    }
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
                break;
            case Const.ServiceCode.GET_PAGES:
                AppLog.Log(Const.TAG, "Pages::::" + response);
                listMenu.clear();
                listMenu = parseContent.parsePages(listMenu, response);
                ApplicationPages applicationPages = new ApplicationPages();
                applicationPages.setData("");
                applicationPages.setId(listMenu.size());
                applicationPages.setTitle(getString(R.string.dialog_logout));
                listMenu.add(applicationPages);
                adapter.notifyDataSetChanged();
                break;

            case Const.ServiceCode.GET_REFERREL:
                ReferralModel referralModel = parseContent.getSingleObject(response, ReferralModel.class);
                if (referralModel != null) {
                    if (referralModel.success) {
                        if (referralModel != null) {
                            showRefferelDialog(referralModel);
                        }
                    } else {
                        AndyUtils.showToast(parseContent.ErrorResponse(referralModel.error_code), R.id.coordinatorLayout, activity);
                    }
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
                AndyUtils.removeCustomProgressDialog();
                break;
            case Const.ServiceCode.LOGOUT:
                AndyUtils.removeCustomProgressDialog();
                LoginModel loginModel2 = parseContent.getSingleObject(response, LoginModel.class);
                if (loginModel2.success) {
                    CustomerApplication.preferenceHelper.Logout();
                    goToMainActivity();
                } else {
                    if (loginModel2.error_code == 622) {
                       clearSessionLogout();
                    }
                }
                break;
        }
        AndyUtils.removeCustomProgressDialog();
    }

    public void clearSessionLogout() {
        CustomerApplication.preferenceHelper.Logout();
        goToMainActivity();
    }

    private void getRequestStatus(String requestId) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_REQUEST_STATUS
                + Const.Params.ID + "=" + CustomerApplication.preferenceHelper.getUserId() + "&"
                + Const.Params.TOKEN + "=" + CustomerApplication.preferenceHelper.getSessionToken() + "&"
                + Const.Params.REQUEST_ID + "=" + requestId);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REQUEST_STATUS, this, this));
    }

    public void gotoMapFragment() {
        UberMapFragment uberMapFragment = new UberMapFragment();
        addFragment(uberMapFragment, false, Const.FRAGMENT_MAP);
    }

    public void gotoMap1Fragment(boolean cancel_conform) {
        if (cancel_conform) {
            AndyUtils.showToast(getResources().getString(R.string.driver_cancel_req), R.id.coordinatorLayout, MainDrawerActivity.this);
        }
    }

    public void gotoTripFragment(RequestStatusModel statusModel) {
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
        UberTripFragment tripFrag = new UberTripFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.DRIVER, statusModel);
        tripFrag.setArguments(bundle);
        addFragment(tripFrag, false, Const.FRAGMENT_TRIP);
    }

    public void gotoRateFragment(RequestStatusModel statusModel) {
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
        UberFeedbackFragment feedBack = new UberFeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.DRIVER, statusModel);
        feedBack.setArguments(bundle);
        addFragmentWithStateLoss(feedBack, false, Const.FRAGMENT_FEEDBACK);
    }

    private void checkStatus() {
        if (CustomerApplication.preferenceHelper.getRequestId() == Const.NO_REQUEST) {
            getRequestInProgress();
        } else {
            getRequestStatus(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
        }
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    private void getMenuItems() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_PAGES + "?user_type=0");
        AppLog.Log(Const.TAG, Const.URL);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
//        map.put(Const.Params.LATITUDE,CustomerApplication.preferenceHelper.Getpickuplat());
//        map.put(Const.Params.LONGITUDE,CustomerApplication.preferenceHelper.GetpickupLng());
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_PAGES, this, this));
    }

    public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final LocationManager manager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                removeGpsDialog();
                onResume();
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
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeWIFIInfo = connectivityManager
                    .getNetworkInfo(connectivityManager.TYPE_WIFI);

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

    private void ShowGpsDialog() {
        if (gpsAlertDialog != null) {
            if (!gpsAlertDialog.isShowing())
                if (!isFinishing())
                    gpsAlertDialog.show();
            return;
        }
        AndyUtils.removeCustomProgressDialog();
        isGpsDialogShowing = true;
        AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
                MainDrawerActivity.this);
        gpsBuilder.setCancelable(false);
        gpsBuilder
                .setTitle(getString(R.string.dialog_no_gps))
                .setMessage(getString(R.string.dialog_no_gps_messgae))
                .setPositiveButton(getString(R.string.dialog_enable_gps),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // continue with delete
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                removeGpsDialog();
                            }
                        })

                .setNegativeButton(getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                                removeGpsDialog();
                                finish();
                            }
                        });
        gpsAlertDialog = gpsBuilder.create();
        if (!isFinishing())
            gpsAlertDialog.show();
    }

    public void showLocationOffDialog() {

        AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
                MainDrawerActivity.this);
        gpsBuilder.setCancelable(false);
        gpsBuilder
                .setTitle(getString(R.string.dialog_no_location_service_title))
                .setMessage(getString(R.string.dialog_no_location_service))
                .setPositiveButton(
                        getString(R.string.dialog_enable_location_service),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // continue with delete
                                dialog.dismiss();
                                Intent viewIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(viewIntent);

                            }
                        })

                .setNegativeButton(getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                                dialog.dismiss();
                                finish();
                            }
                        });
        locationAlertDialog = gpsBuilder.create();
        if (!isFinishing())
            locationAlertDialog.show();
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
        if (internetDialog != null) {
            if (!internetDialog.isShowing())
                internetDialog.show();
            return;
        }
        AndyUtils.removeCustomProgressDialog();
        isNetDialogShowing = true;
        AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
                MainDrawerActivity.this);
        internetBuilder.setCancelable(false);
        internetBuilder
                .setTitle(getString(R.string.dialog_no_internet))
                .setMessage(getString(R.string.dialog_no_inter_message))
                .setPositiveButton(getString(R.string.dialog_enable_3g),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // continue with delete
                                Intent intent = new Intent(
                                        Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                removeInternetDialog();
                            }
                        })
                .setNeutralButton(getString(R.string.dialog_enable_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // User pressed Cancel button. Write
                                // Logic Here
                                startActivity(new Intent(
                                        Settings.ACTION_WIFI_SETTINGS));
                                removeInternetDialog();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                                removeInternetDialog();
                                finish();
                            }
                        });
        internetDialog = internetBuilder.create();
        if (!isFinishing())
            internetDialog.show();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (isRecieverRegistered) {
            unregisterReceiver(internetConnectionReciever);
            unregisterReceiver(GpsChangeReceiver);
        }

    }


    private void showRefferelDialog(final ReferralModel ref) {
        if (mDialog_Refferal != null) {
            if (!mDialog_Refferal.isShowing())
                mDialog_Refferal.show();
            return;
        }
        mDialog_Refferal = new Dialog(this, R.style.MyDialog);
        mDialog_Refferal.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog_Refferal.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog_Refferal.setContentView(R.layout.ref_code_layout);
        TextView tvTitle = (TextView) mDialog_Refferal.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.text_ref_code) + ref.referralCode);
        TextView tvReferralEarn = (TextView) mDialog_Refferal.findViewById(R.id.tvReferralEarn);
        tvReferralEarn.setText(getString(R.string.payment_unit) + ref.balanceAmount);
        Button btnCancel = (Button) mDialog_Refferal.findViewById(R.id.btnCancel);
        Button btnShare = (Button) mDialog_Refferal.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/html");
//                    sharingIntent.putExtra(
//                            Intent.EXTRA_TEXT,
//                            getString(R.string.text_sharequote)
//                                    + getResources().getString(R.string.text_play_link)
//                                    + getPackageName()
//                                    + getString(R.string.text_note_referral)
//                                    + ref.referralCode);
//
//                startActivity(Intent.createChooser(sharingIntent,
//                        getString(R.string.text_share_ref)));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "" + getString(R.string.text_sharequote)
                                + getResources().getString(R.string.text_play_link)
                                + getPackageName()
                                + getString(R.string.text_note_referral)
                                + ref.referralCode);
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        getString(R.string.app_name));
                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_ref)));

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog_Refferal.dismiss();
            }
        });
        if (!isFinishing())
            mDialog_Refferal.show();

    }

    private void getReffrelaCode() {
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.text_getting_ref_code), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_REFERRAL + Const.Params.ID
                + "=" + CustomerApplication.preferenceHelper.getUserId() + "&" + Const.Params.TOKEN + "="
                + CustomerApplication.preferenceHelper.getSessionToken());
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REFERREL, this, this));
    }

    public void openLogoutDialog() {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.logout);
        mDialog.findViewById(R.id.tvLogoutOk).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        logout();


                    }
                });
        mDialog.findViewById(R.id.tvLogoutCancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        isLogoutCheck = true;
                    }
                });
        if (!isFinishing())
            mDialog.show();
    }

    public void drivercanceldialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(R.string.text_driver_trip_cancled);
        builder1.setMessage(getResources().getString(R.string.driver_cancel_req));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.text_journey_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        if (!isFinishing())
            builder1.show();
    }

    private void logout() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, MainDrawerActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGOUT);
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.LOGOUT, this, this));
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(
                R.layout.drawer_layout, null); // "null" is important.
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer
                .findViewById(R.id.llContent);
        container.addView(child, 0);
        drawer.findViewById(R.id.left_drawer).setPadding(0,
                (actionBar.getHeight() + getStatusBarHeight()), 0, 60);
        decor.addView(drawer);
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
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
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
    }
}
