package com.jayeen.driver.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.RegisterActivity;
import com.jayeen.driver.base.BaseMapFragment;
import com.jayeen.driver.component.CircularMusicProgressBar;
import com.jayeen.driver.locationupdate.LocationHelper;
import com.jayeen.driver.locationupdate.LocationHelper.OnLocationReceived;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.newmodels.CheckChangeStateModel;
import com.jayeen.driver.newmodels.GetRequestModel;
import com.jayeen.driver.newmodels.IncomingRequest;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import static com.jayeen.driver.DriverApplication.requestQueue;

public class ClientRequestFragmentNew extends BaseMapFragment implements
        AsyncTaskCompleteListener, OnMapReadyCallback, OnLocationReceived, OnItemSelectedListener {
    CircularMusicProgressBar progressBar;
    MyCountDownTimer myCountDownTimer;
    private static final String ARG_CUSTOM_BUNDLE = "CUSTOM BUNDLE";
    private GoogleMap mMap;
    private final String TAG = "ClientRequestFragment";
    private RelativeLayout llAcceptReject;
    //    private RelativeLayout PickupDropAddrLayout;
//    private View llUserDetailView;
    private MyFontButton btnReasonConfirm;//, btnClientReqRemainTime,
    private Button btnClientAccept, btnClientReject;
    private boolean isContinueRequest, isAccepted = false;
    private Handler handler = new Handler();
    //    private SeekbarTimer seekbarTimer;
    private RequestDetail requestDetail;
    private Marker markerDriverLocation, markerClientLocation, markerClientDestination;
    private static Location location;
    private LocationHelper locationHelper;
    private TextView tvClientName;// , tvClientPhoneNumber;
    private RatingBar tvClientRating;
    //    private ImageView ivClientProfilePicture;
    private newRequestReciever requestReciever;
    private View clientRequestView;
    private MapView mMapView;
    private Bundle mBundle;
    private boolean isApprovedCheck = true, isActive = true, loaded = false;
    private Dialog mDialog;
    private SoundPool soundPool;
    private int soundid;
    private Button btnGoOffline;
    private RelativeLayout relMap;
    private LinearLayout linearOffline;
    String rejectreason = "";
    //    Spinner rejectspinner;
    EditText editrejectReason;
    Dialog rDialog;
    private TextView text_pickupaddr, text_dropaddr, text_estimate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        clientRequestView = inflater.inflate(R.layout.fragment_client_requests_new,
                container, false);
        try {
            mMapView = (MapView) clientRequestView.findViewById(R.id.clientReqMap);
            mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
        progressBar = (CircularMusicProgressBar) clientRequestView.findViewById(R.id.album_art);
        llAcceptReject = (RelativeLayout) clientRequestView.findViewById(R.id.llAcceptReject);
//        PickupDropAddrLayout = (RelativeLayout) clientRequestView.findViewById(R.id.layout1addr);
//        llUserDetailView = (View) clientRequestView.findViewById(R.id.clientDetailView);
        btnClientAccept = (Button) clientRequestView.findViewById(R.id.btnClientAccept);
        btnClientReject = (Button) clientRequestView.findViewById(R.id.btnClientReject);
        linearOffline = (LinearLayout) clientRequestView.findViewById(R.id.linearOffline);
        text_dropaddr = (TextView) clientRequestView.findViewById(R.id.text_dropaddr);
        text_pickupaddr = (TextView) clientRequestView.findViewById(R.id.text_pickupaddr);
        text_estimate = (TextView) clientRequestView.findViewById(R.id.f_time_estimate);
//        btnClientReqRemainTime = (MyFontButton) clientRequestView.findViewById(R.id.btnClientReqRemainTime);
        tvClientName = (TextView) clientRequestView.findViewById(R.id.tvClientName);
        tvClientRating = (RatingBar) clientRequestView.findViewById(R.id.rtbProductRating);
//        ivClientProfilePicture = (ImageView) clientRequestView.findViewById(R.id.ivClientImage);
        btnClientAccept.setOnClickListener(this);
        btnClientReject.setOnClickListener(this);
        clientRequestView.findViewById(R.id.btnMyLocation).setOnClickListener(this);
        btnGoOffline = (Button) clientRequestView.findViewById(R.id.btnOffline);
        relMap = (RelativeLayout) clientRequestView.findViewById(R.id.relMap);
        linearOffline.setVisibility(View.GONE);
        relMap.setVisibility(View.VISIBLE);
        btnGoOffline.setOnClickListener(this);
        btnGoOffline.setSelected(true);
        text_pickupaddr.setSelected(true);
        text_dropaddr.setSelected(true);
        text_pickupaddr.setMovementMethod(new ScrollingMovementMethod());
        text_dropaddr.setMovementMethod(new ScrollingMovementMethod());
        return clientRequestView;
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        AppLog.Log(TAG, e.getMessage());
        Intent intent = new Intent();
        intent.setAction("com.mydomain.SEND_LOG"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
        System.exit(1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {

                mMapView.onCreate(savedInstanceState);
            } catch (Exception e) {
                e.printStackTrace();
                setUpMap();
            }
        } else {
            setUpMap();
        }
        locationHelper = new LocationHelper(getActivity());
        locationHelper.setLocationReceivedLister(this);
        locationHelper.onStart();
        checkState();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        IntentFilter filter = new IntentFilter(AndyConstants.NEW_REQUEST);
        requestReciever = new newRequestReciever();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                requestReciever, filter);
        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 100);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundid = soundPool.load(mapActivity, R.raw.beep_new, 1);
//        registerIsActive();
    }

    private void addMarker() {
        if (mMap == null) {
            setUpMap();
            return;
        }

    }

    public void showLocationOffDialog() {

        AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(mapActivity);
        gpsBuilder.setCancelable(false);
        gpsBuilder
                .setTitle(mapActivity.getString(R.string.dialog_no_location_service_title))
                .setMessage(mapActivity.getString(R.string.dialog_no_location_service))
                .setPositiveButton(
                        mapActivity.getString(R.string.dialog_enable_location_service),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(viewIntent);
                            }
                        })
                .setNegativeButton(mapActivity.getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                mapActivity.finish();
                            }
                        });
        gpsBuilder.create();
        gpsBuilder.show();
    }

    private void setUpMap() {
        if (mMap == null) {
            ((MapView) clientRequestView.findViewById(R.id.clientReqMap))
                    .getMapAsync(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClientAccept:
                requestAccepted(true);
                break;
            case R.id.btnClientReject:
                requestAccepted(false);
                break;

            case R.id.btnConfirmReason:
                if (editrejectReason != null)
                    rejectreason = editrejectReason.getText().toString();
                if (TextUtils.isEmpty(rejectreason)) {
//                if (TextUtils.isEmpty(editrejectReason.getText().toString())) {
                    mapActivity.display_toast(mapActivity.getResources().getString(R.string.text_reject_stringval));
                } else {
                    mapActivity.clearAll();
                    respondRequest(0);
                    rDialog.dismiss();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                    mMap.animateCamera(cameraUpdate);
                }
                break;
            case R.id.btnMyLocation:
                if (location == null)
                    break;
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        latLng, 18);
                mMap.animateCamera(cameraUpdate);
                break;
            case R.id.btnOffline:
                mapActivity.clearAll();
                changeState();
                break;
            default:
                break;
        }
    }

    private void requestAccepted(boolean b) {
        cancelSeekbarTimer();
        if (b) {
            mapActivity.clearAll();
            isAccepted = true;
            respondRequest(1);
        } else {
            isAccepted = false;
            showRejectDialog();
            soundPool.stop(soundid);
        }

    }


    private void showRejectDialog() {
        rDialog = new Dialog(getActivity());
        rDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rDialog.setContentView(R.layout.cancel_reason);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(rDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        rDialog.getWindow().setAttributes(lp);
        rDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.dialog_bg)));
        btnReasonConfirm = (MyFontButton) rDialog.findViewById(R.id.btnConfirmReason);
//        rejectspinner = (Spinner) rDialog.findViewById(R.id.spinner);
        editrejectReason = (EditText) rDialog.findViewById(R.id.etrejectval);
        List<String> categories = new ArrayList<String>();
        categories.add(mapActivity.getString(R.string.text_lunch));
        categories.add(mapActivity.getString(R.string.text_repair));
        categories.add(mapActivity.getString(R.string.text_ontrip));
        categories.add(mapActivity.getString(R.string.text_leave));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mapActivity, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*rejectspinner.setAdapter(dataAdapter);
        rejectspinner.setOnItemSelectedListener(this);*/
        btnReasonConfirm.setOnClickListener(this);
        rDialog.show();
        rDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (btnGoOffline.isSelected()) {
            if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
                startCheckingUpcomingRequests();
            }
        }
        mapActivity.setActionBarTitle(mapActivity.getString(R.string.app_name));
        btnGoOffline.setText(mapActivity.getResources().getString(R.string.text_go_offline));
//        registerIsActive();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
            stopCheckingUpcomingRequests();
        }
        mMapView.onPause();
        isContinueRequest = false;
    }

    @Override
    public void onDestroy() {
        isContinueRequest = false;
        mMapView.onDestroy();
        locationHelper.onStop();
        super.onDestroy();
        stopCheckingUpcomingRequests();
        cancelSeekbarTimer();
        AndyUtils.removeCustomProgressDialog();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(requestReciever);
//        unregisterIsActive();
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case AndyConstants.ServiceCode.GET_ALL_REQUEST:
                AndyUtils.removeCustomProgressDialog();
                Log.e("Responce", response);
                GetRequestModel getRequestModel = taxiparseContent.getSingleObject(response, GetRequestModel.class);
                if (getRequestModel == null) {
                    AndyUtils.showToast(mapActivity.getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
                    return;
                }
                if (!getRequestModel.mSuccess) {
                    try {
                        if (getRequestModel.error_code == AndyConstants.INVALID_TOKEN) {
                            doLogoutWhenSessionOut();
                            return;
                        }
                    } catch (Exception e) {
                        return;
                    }
                } else {
                    if (!(getRequestModel.mIsApproved == 1)) {
                        if (!DriverApplication.preferenceHelper.getIsApproved().equalsIgnoreCase("1")) {
                            if (isApprovedCheck) {
                                mapActivity.openApprovedDialog();
                                isApprovedCheck = false;
                                return;
                            }
                        }
                    } else if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                        isApprovedCheck = true;
                    }
//
//                    if (!(getRequestModel.mIsAvailable == 1)) { //n2c
//                        changeState();
//                        isActive=false;
//                    }


                    requestDetail = taxiparseContent.parseAllRequests(getRequestModel);
                    if (requestDetail != null && mMap != null) {
                        try {
                            stopCheckingUpcomingRequests();
                            if (llAcceptReject.getVisibility() != View.VISIBLE) {
                                setComponentVisible();
                                if (myCountDownTimer == null)
//                                    initiateCountdown(requestDetail.getTimeLeft(), requestDetail.getClientLatitude(), requestDetail.getClientLongitude(), requestDetail.getDestinationLatitude(), requestDetail.getDestinationLongitude());
                                    initiateCountdown(requestDetail.getTimeLeft(), requestDetail.getMapURL());
                            }
//                            String pickaddr = getDestinationAddress(requestDetail.getClientLatitude(), requestDetail.getClientLongitude(), true);
                            String pickaddr = requestDetail.getPickupAddr();
                            if (TextUtils.isEmpty(pickaddr) || pickaddr == null) {
                                pickaddr = getDestinationAddress(requestDetail.getClientLatitude(), requestDetail.getClientLongitude(), true);
                            }
                            if (!pickaddr.equals("")) {
                                text_pickupaddr.setText(pickaddr);
                                Log.e("pickup", text_pickupaddr.toString());
                            } else {
                                text_pickupaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                            }
//                            String dropaddr = getDestinationAddress(requestDetail.getDestinationLatitude() + "", requestDetail.getDestinationLongitude(), false);
                            String dropaddr = requestDetail.getDropAddr();
                            if (dropaddr != null) {
                                if (!dropaddr.equals("")) {
                                    text_dropaddr.setText(dropaddr);
                                    AppLog.Log("drop", text_dropaddr.toString());
                                } else {
                                    text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                                }
                            } else {
                                text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                            }
//                            if (requestDetail.getEstimate() > 0)
//                                text_estimate.setText(requestDetail.getEstimate()+" "+mapActivity.getString(R.string.test_min_away));
//                            else
                            text_estimate.setVisibility(View.GONE);
//                        btnClientReqRemainTime.setText("" + requestDetail.getTimeLeft());
//                        *************

                            tvClientName.setText(requestDetail.getClientName());
                            if (requestDetail.getClientRating() != 0) {
                                tvClientRating.setRating(requestDetail.getClientRating());
                                Log.i("Rating", "" + requestDetail.getClientRating());
                            }
//                        if (TextUtils.isEmpty(requestDetail.getClientProfile())) {
//                            aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
//                                    .image(R.drawable.user);
//                        } else {
//                            aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
//                                    .image(requestDetail.getClientProfile());
//                        }
                            markerClientLocation = null;
                            markerClientDestination = null;
                            markerClientLocation = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(requestDetail.getClientLatitude()),
                                            Double.parseDouble(requestDetail.getClientLongitude())))
                                    .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client)))
                                    .title(mapActivity.getResources().getString(
                                            R.string.client_location)));
                            if (requestDetail.getDestinationLatitude() != null && requestDetail.getDestinationLongitude() != null && !requestDetail.getDestinationLatitude().equalsIgnoreCase("null") && !requestDetail.getDestinationLongitude().equalsIgnoreCase("null")) {
                                markerClientDestination = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(requestDetail.getDestinationLatitude()),
                                                Double.parseDouble(requestDetail.getDestinationLongitude())))
                                        .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_destination)))
                                        .title(mapActivity.getResources().getString(R.string.client_destination)));
                            }
//                            if (seekbarTimer == null) {
//                                seekbarTimer = new SeekbarTimer(
//                                        requestDetail.getTimeLeft() * 1000, 1000);
//                                seekbarTimer.start();
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            setComponentInvisible();
                            mapActivity.clearAll();
                            respondRequest(0);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                            mMap.animateCamera(cameraUpdate);
                        }
                    }
                }
//                if(getRequestModel.mSuccess) {
//                }else{
//
//                }
                break;
            case AndyConstants.ServiceCode.CHECK_STATE:
            case AndyConstants.ServiceCode.TOGGLE_STATE:
                AndyUtils.removeCustomProgressDialog();
                DriverApplication.preferenceHelper.putIsActive(false);
                DriverApplication.preferenceHelper.putDriverOffline(false);
                CheckChangeStateModel changeStateModel = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (!changeStateModel.mSuccess) {
                    isContinueRequest = false;
                    if (changeStateModel.error_code == AndyConstants.INVALID_TOKEN2 || changeStateModel.error_code == AndyConstants.INVALID_TOKEN)
                        doLogoutWhenSessionOut();
                    return;
                }
                AppLog.Log("TAG", "toggle state:" + response);
                if (changeStateModel.mIsActive == 1) {
                    updateButtonUi(true);
                    if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
                        startCheckingUpcomingRequests();
                    }

                } else {
                    stopCheckingUpcomingRequests();
                    updateButtonUi(false);
                }

                break;
            case AndyConstants.ServiceCode.RESPOND_REQUEST:
                AppLog.Log(TAG, "respond Request Response :" + response);
                removeNotification();
                AndyUtils.removeCustomProgressDialog();
                CheckChangeStateModel checkChangeStateModel = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (checkChangeStateModel == null) {
                    AndyUtils.removeCustomProgressDialog();
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
                    return;
                }
                if (checkChangeStateModel.mSuccess) {
                    if (isAccepted) {
                        JobFragment jobFragment = new JobFragment();
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putInt("RequestID", requestDetail.getRequestId());
                        } catch (Exception e) {
                            e.getMessage();
                            bundle.putInt("RequestID", DriverApplication.preferenceHelper.getRequestId());
                        }
                        bundle.putInt(AndyConstants.JOB_STATUS,
                                AndyConstants.IS_WALK_STARTED); // navi
                        bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
                                requestDetail);
                        jobFragment.setArguments(bundle);
                        if (this.isVisible())
                            mapActivity.addFragment(jobFragment, false,
                                    AndyConstants.JOB_FRGAMENT_TAG, true);
                    } else {
                        Log.i(TAG, "Else Condition");
                        cancelSeekbarTimer();
                        setComponentInvisible();

                        if (markerClientLocation != null) {
                            markerClientLocation.remove();
                            markerClientLocation.setVisible(false);
                            Log.i(TAG, "Marker Client Location- Remove");
                        }
                        if (markerClientDestination != null) {
                            markerClientDestination.remove();
                            markerClientDestination.setVisible(false);
                            Log.i(TAG, "Marker Client Destination Remove");
                        }
                        requestDetail = null;
                        DriverApplication.preferenceHelper.putRequestId(AndyConstants.NO_REQUEST);
                        startCheckingUpcomingRequests();
                    }
                } else if (checkChangeStateModel.error_code == AndyConstants.SERVICE_ID_NOT_FOUND) {
                    cancelSeekbarTimer();
                    setComponentInvisible();
                    if (markerClientLocation != null) {
                        markerClientLocation.remove();
                        markerClientLocation.setVisible(false);
                    }
                    if (markerClientDestination != null) {
                        markerClientDestination.remove();
                        markerClientDestination.setVisible(false);
                    }
                    requestDetail = null;
                    DriverApplication.preferenceHelper.putRequestId(AndyConstants.NO_REQUEST);
                    startCheckingUpcomingRequests();
                } else if (checkChangeStateModel.error_code == AndyConstants.INVALID_TOKEN2 || checkChangeStateModel.error_code == AndyConstants.INVALID_TOKEN) {
                    doLogoutWhenSessionOut();
                } else {
                    mapActivity.display_toast(checkChangeStateModel.error);
                }
                break;
            case AndyConstants.ServiceCode.GET_PICKUP_ADDRESS:
                try {
                    String address = new JSONObject(response).getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    text_pickupaddr.setText(address);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AndyConstants.ServiceCode.GET_DROP_ADDRESS:
                try {
                    String address = new JSONObject(response).getJSONArray("results").getJSONObject(0).getString("formatted_address");

                    text_dropaddr.setText(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        if (ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = mapActivity.getLayoutInflater().inflate(
                        R.layout.info_window_layout, null);

                ((TextView) v).setText(marker.getTitle());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;

            }

        });

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        addMarker();
    }

    private class SeekbarTimer extends CountDownTimer {

        public SeekbarTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
//            if (seekbarTimer == null) {
//                return;
//            }
            mapActivity.display_toast(getResources().getString(R.string.toast_time_over));
            setComponentInvisible();
            respondRequest(0);
            DriverApplication.preferenceHelper.clearRequestData();
            if (markerClientLocation != null
                    && markerClientLocation.isVisible()) {
                markerClientLocation.
                        remove();
            }
            if (markerClientDestination != null
                    && markerClientDestination.isVisible()) {
                markerClientDestination.
                        remove();
            }
            removeNotification();
            startCheckingUpcomingRequests();
            this.cancel();
//            seekbarTimer = null;

        }

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) (millisUntilFinished / 1000);

            if (!isVisible()) {
                return;
            }
            if (DriverApplication.preferenceHelper.getSoundAvailability()) {
                AppLog.Log("ClientRequest Timer Beep", "Beep started");
                if (loaded) {
                    soundPool.play(soundid, 1, 1, 0, 1, 1);
                }
            }

//            btnClientReqRemainTime.setText("" + time);
//            *****
        }
    }

//
//    private void registerIsActive() {
//        IntentFilter intentFilter = new IntentFilter("CHANGE_ACTIVE");
//        getActivity().registerReceiver(mReciver, intentFilter);
//    }
//
//    BroadcastReceiver mReciver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//                    AppLog.Log("ClientReqFrag", "CHANGE_ACTIVE");
//
//        }
//    };
//
//    private void unregisterIsActive() {
//        if (mReciver != null) {
//            getActivity().unregisterReceiver(mReciver);
//        }
//    }

    private void respondRequest(int status) {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(mapActivity.getResources().getString(R.string.toast_no_internet));
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, "", mapActivity.getResources()
                .getString(R.string.progress_respond_request), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.RESPOND_REQUESTS);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        try {
            map.put(AndyConstants.Params.REQUEST_ID,
                    String.valueOf(requestDetail.getRequestId()));
        } catch (Exception e) {
            e.getMessage();
            map.put(AndyConstants.Params.REQUEST_ID,
                    String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        }
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.ACCEPTED, String.valueOf(status));
        map.put(AndyConstants.Params.REASONFORREJECT, rejectreason);

        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.RESPOND_REQUEST, this, this));
    }

    public void checkRequestStatus() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(mapActivity.getResources().getString(R.string.toast_no_internet));
            return;
        }
        AndyUtils.showCustomProgressDialog(mapActivity, "", mapActivity.getResources()
                .getString(R.string.progress_dialog_request), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL,
                AndyConstants.ServiceType.CHECK_REQUEST_STATUS
                        + AndyConstants.Params.ID + "="
                        + DriverApplication.preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + DriverApplication.preferenceHelper.getSessionToken() + "&"
                        + AndyConstants.Params.REQUEST_ID + "="
                        + DriverApplication.preferenceHelper.getRequestId());

        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, this, this));
    }

    public void getAllRequests() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL,
                AndyConstants.ServiceType.GET_ALL_REQUESTS
                        + AndyConstants.Params.ID + "="
                        + DriverApplication.preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + DriverApplication.preferenceHelper.getSessionToken());
        Log.e(TAG, map.get(AndyConstants.URL));
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.GET_ALL_REQUEST, this, this));
    }

    private class TimerRequestStatus extends TimerTask {
        @Override
        public void run() {
            if (isContinueRequest) {
                getAllRequests();
            }
        }
    }

    private void startCheckingUpcomingRequests() {
        AppLog.Log(TAG, "start checking upcomingRequests");
        isContinueRequest = true;
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerRequestStatus(),
//                AndyConstants.DELAY, AndyConstants.TIME_SCHEDULE);
        handler.postDelayed(runnable, 100);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isContinueRequest) {
                getAllRequests();
            }
            handler.postDelayed(this, AndyConstants.TIME_SCHEDULE);
        }
    };

    private void stopCheckingUpcomingRequests() {
        AppLog.Log(TAG, "stop checking upcomingRequests");
        isContinueRequest = false;
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
        if (handler != null)
            handler.removeCallbacks(runnable);

    }

    private void removeNotification() {
        try {
            NotificationManager manager = (NotificationManager) mapActivity
                    .getSystemService(mapActivity.NOTIFICATION_SERVICE);
            manager.cancel(AndyConstants.NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationReceived(LatLng latlong) {
        if (latlong != null) {
            if (mMap != null) {

                if (markerDriverLocation == null) {
                    markerDriverLocation = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latlong.latitude, latlong.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver))).title(mapActivity.getResources().getString(
                                    R.string.my_location)));
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(latlong.latitude,
                                    latlong.longitude), 10));
                } else {
                    markerDriverLocation.setPosition(new LatLng(
                            latlong.latitude, latlong.longitude));
                }

            }
        }
    }

    public void setComponentVisible() {
        btnGoOffline.setVisibility(View.GONE);
//        ********
        llAcceptReject.setVisibility(View.VISIBLE);
//        btnClientReqRemainTime.setVisibility(View.VISIBLE);
//        llUserDetailView.setVisibility(View.VISIBLE);
//        PickupDropAddrLayout.setVisibility(View.VISIBLE);
    }

    public void setComponentInvisible() {
        btnGoOffline.setVisibility(View.VISIBLE);
        llAcceptReject.setVisibility(View.GONE);
//        btnClientReqRemainTime.setVisibility(View.GONE);
//        llUserDetailView.setVisibility(View.GONE);
//        PickupDropAddrLayout.setVisibility(View.GONE);
    }

    public void cancelSeekbarTimer() {
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }

    public void onDestroyView() {
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.clientReqMap);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap = null;
        super.onDestroyView();
    }

    private class newRequestReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(AndyConstants.NEW_REQUEST);
            AppLog.Log(TAG, "FROM BROAD CAST-->" + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 1) {
                    IncomingRequest newRequestModel = taxiparseContent.getSingleObject(response, IncomingRequest.class);
                    if (newRequestModel == null) {
                        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
                        return;
                    }
                    RequestDetail newRequestDetail = taxiparseContent.parsePushNewRequest(newRequestModel);
                    if (newRequestDetail != null)
                        requestDetail = newRequestDetail;
                    if (requestDetail != null) {
                        stopCheckingUpcomingRequests();
                        if (llAcceptReject.getVisibility() != View.VISIBLE) {
                            setComponentVisible();
//                        btnClientReqRemainTime.setText("" + requestDetail.getTimeLeft());
                            if (myCountDownTimer == null)
                                initiateCountdown(requestDetail.getTimeLeft(), requestDetail.getMapURL());
                        }
                        tvClientName.setText(requestDetail.getClientName());
//                        if (requestDetail.getEstimate() > 0)
//                            text_estimate.setText(requestDetail.getEstimate()+" "+mapActivity.getString(R.string.test_min_away));
//                        else
                        text_estimate.setVisibility(View.GONE);
                        if (requestDetail.getClientRating() != 0) {
                            tvClientRating.setRating(requestDetail.getClientRating());
                            Log.i("Rating", "" + requestDetail.getClientRating());
                        }
//                        if (TextUtils.isEmpty(requestDetail.getClientProfile())) {
//                            aQuery.id(ivClientProfilePicture)
//                                    .progress(R.id.pBar).image(R.drawable.user);
//                        } else {
//                            aQuery.id(ivClientProfilePicture)
//                                    .progress(R.id.pBar)
//                                    .image(requestDetail.getClientProfile());
//                        }
                        String dropaddr = "", pickaddr = "";
                        markerClientLocation = null;
                        markerClientLocation = mMap
                                .addMarker(new MarkerOptions().position(
                                        new LatLng(Double.parseDouble(requestDetail.getClientLatitude()),
                                                Double.parseDouble(requestDetail.getClientLongitude())))
                                        .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client)))
                                        .title(mapActivity.getResources().getString(R.string.client_location)));
                        pickaddr = requestDetail.getPickupAddr();
                        if (TextUtils.isEmpty(pickaddr) || pickaddr == null) {
                            pickaddr = getDestinationAddress(requestDetail.getClientLatitude(), requestDetail.getClientLongitude(), true);
                        }
                        if (!pickaddr.equals("")) {
                            text_pickupaddr.setText(pickaddr);
                            Log.e("pickup", text_pickupaddr.toString());
                        } else {
                            text_pickupaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                        }
                        markerClientDestination = null;
                        String destinationLattitude = requestDetail.getDestinationLatitude();
                        String destinationLongitude = requestDetail.getDestinationLongitude();
                        if (destinationLattitude != null && destinationLongitude != null && destinationLattitude != "0" && destinationLongitude != "0" && !destinationLattitude.equalsIgnoreCase("null") && !destinationLongitude.equalsIgnoreCase("null")) {
                            markerClientDestination = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(destinationLattitude),
                                            Double.parseDouble(destinationLongitude)))
                                    .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_destination)))
                                    .title(mapActivity
                                            .getResources()
                                            .getString(
                                                    R.string.client_destination)));
//                            String dropaddr = getDestinationAddress(destinationLattitude, destinationLongitude, false);
                            dropaddr = requestDetail.getDropAddr();
                            if (dropaddr != null) {
                                if (!dropaddr.equals("")) {
                                    dropaddr = getDestinationAddress(destinationLattitude, destinationLongitude, false);
                                    if (dropaddr != null)
                                        if (!dropaddr.equals(""))
                                            text_dropaddr.setText(dropaddr);
                                    AppLog.Log("drop", text_dropaddr.toString());
                                } else {
                                    text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                                }
                            } else {
                                text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                            }
                            if (TextUtils.isEmpty(dropaddr) || dropaddr == null) {
                                dropaddr = getDestinationAddress(destinationLattitude, destinationLongitude, false);
                            }
                            if (!dropaddr.equals("") && dropaddr != null) {
                                text_dropaddr.setText(dropaddr);
                                Log.e("drop", text_dropaddr.toString());
                            } else {
                                text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                            }
                        }  // marker visible check
//                        if (seekbarTimer == null) {
//                            seekbarTimer = new SeekbarTimer(
//                                    requestDetail.getTimeLeft() * 1000, 1000);
//                            seekbarTimer.start();
//                        }
                        else {
                            text_dropaddr.setText(mapActivity.getString(R.string.text_noaddress_title));
                        }
                        AppLog.Log(TAG, "From broad cast recieved request");
                    }
                } else if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 8 || jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 9) {
                    if (jsonObject.getInt(AndyConstants.Params.IS_AVAILABLE) == 1) {
                        updateButtonUi(true);
                        if (DriverApplication.preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
                            startCheckingUpcomingRequests();
                        }

                    } else {
                        stopCheckingUpcomingRequests();
                        updateButtonUi(false);
                    }
                } else {
                    Log.i(TAG, "Client MAin Request Check Else");
                    setComponentInvisible();
                    DriverApplication.preferenceHelper.clearRequestData();
                    if (markerClientLocation != null
                            && markerClientLocation.isVisible()) {
                        markerClientLocation.remove();
                        markerClientLocation.setVisible(false);
                    }
                    if (markerClientDestination != null
                            && markerClientDestination.isVisible()) {
                        markerClientDestination.remove();
                        markerClientDestination.setVisible(false);
                    }

                    cancelSeekbarTimer();
                    removeNotification();
                    startCheckingUpcomingRequests();
                    AndyUtils.showToast(mapActivity.getString(R.string.trip_cancled_client), R.id.coordinatorLayout, mapActivity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (intent.getStringExtra("message") != null) // navi
//                Toast.makeText(getActivity(), "Received:" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(getActivity(), "Jayeen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(mapActivity.getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
    }

    public String getDestinationAddress(String latitude, String longitude, boolean ispickup) {
        String finaladdr = "";
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
        } else {
            Log.i("TAG", "ClientRequest Lat:" + latitude + "Longitude" + longitude);

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(mapActivity, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude), 1);
                if (addresses == null || addresses.size() == 0) {
                    finaladdr = mapActivity.getResources().getString(R.string.text_noaddress_title);
                    return finaladdr;
                }
                String address = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1);
                String city = addresses.get(0).getAddressLine(2);
                String country = addresses.get(0).getAddressLine(3);
                finaladdr = address + " " + city + " " + country + " ";
                if (finaladdr.contains("null")) {
                    finaladdr.replace("null", "");
                }
                AppLog.Log("Address", " " + address + " " + city + "\n " + country);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                finaladdr = mapActivity.getResources().getString(R.string.text_noaddress_title);
            } catch (IOException e) {
                if (addresses == null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(AndyConstants.URL,
                            (String.format(AndyConstants.GEOCODER_URL, latitude, longitude)));
                    if (ispickup) {
                        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                                AndyConstants.ServiceCode.GET_PICKUP_ADDRESS, this, this));
                    } else {
                        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                                AndyConstants.ServiceCode.GET_DROP_ADDRESS, this, this));
                    }
                }
                finaladdr = mapActivity.getResources().getString(R.string.text_noaddress_title);
            }
        }
        return finaladdr;


    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyLocationi", "" + strReturnedAddress.toString());
            } else {
                Log.w("My loction", "N" +
                        "");
            }
        } catch (Exception e) {
            AppLog.Log(TAG, e.getMessage());
        }
        return strAdd;
    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        this.location = location;
        if (location != null) {
            if (mMap != null) {
                if (markerDriverLocation == null) {
                    markerDriverLocation = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)))
                            .title(mapActivity.getResources().getString(
                                    R.string.my_location)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 12));
                } else {
                    markerDriverLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        } else {
            showLocationOffDialog();
        }
    }

    private void updateButtonUi(boolean state) {
        btnGoOffline.setSelected(state);
        if (btnGoOffline.isSelected()) {
            btnGoOffline.setText(mapActivity.getString(R.string.text_go_offline));
            linearOffline.setVisibility(View.GONE);
            relMap.setVisibility(View.VISIBLE);
        } else {
            btnGoOffline.setText(mapActivity.getString(R.string.text_go_online));
            linearOffline.setVisibility(View.VISIBLE);
            relMap.setVisibility(View.GONE);
        }
    }

    private void checkState() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(mapActivity.getResources().getString(R.string.toast_no_internet));
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL,
                AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
                        + "=" + DriverApplication.preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + DriverApplication.preferenceHelper.getSessionToken());
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.CHECK_STATE, this, this));
    }

    private void changeState() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(getResources().getString(R.string.toast_no_internet));
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
                .getString(R.string.progress_changing_avaibilty), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.TOGGLE_STATE);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.TOGGLE_STATE, this, this));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position,
                               long arg3) {
        String item = parent.getItemAtPosition(position).toString();
        rejectreason = item;
        if (rejectreason.equals("") && rejectreason.length() == 0) {
            rejectreason = "LUNCH";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        final Bundle mapViewSaveState = new Bundle(savedInstanceState);
        mMapView.onSaveInstanceState(mapViewSaveState);
        savedInstanceState.putBundle("mapViewSaveState", mapViewSaveState);

        Bundle customBundle = new Bundle();
        savedInstanceState.putBundle(ARG_CUSTOM_BUNDLE, customBundle);
        super.onSaveInstanceState(savedInstanceState);
    }

    //    public void initiateCountdown(long timeLeft, String clientLatitude, String clientLongitude, String destinationLatitude, String destinationLongitude) {
    public void initiateCountdown(long timeLeft, String mapURL) {
        // set progress to 40%
        progressBar.initProgressValue((int) timeLeft);
        myCountDownTimer = new MyCountDownTimer(((int) timeLeft) * 1000, 1000);
        myCountDownTimer.start();
//        String url = "";
//        String source = "", dest = "", sourcDest = "", lastparams = "";
//        if (clientLatitude == null || clientLatitude.equalsIgnoreCase("null") || TextUtils.isEmpty(clientLatitude) ||
//                (clientLongitude == null || clientLongitude.equalsIgnoreCase("null") || TextUtils.isEmpty(clientLongitude)))
//            source = "";
//        else
//            source = clientLatitude + "," + clientLongitude;
//        if (destinationLatitude == null || destinationLatitude.equalsIgnoreCase("null") || TextUtils.isEmpty(destinationLatitude) ||
//                (destinationLongitude == null || destinationLongitude.equalsIgnoreCase("null") || TextUtils.isEmpty(destinationLongitude))) {
//            dest = "";
//            sourcDest = source;
//        } else {
//            if (Double.parseDouble(destinationLatitude) != 0.0 && Double.parseDouble(destinationLongitude) != 0.00) {
//                sourcDest = source + "|" + dest;
//                dest = destinationLatitude + "," + destinationLongitude;
//                lastparams = String.format(AndyConstants.ServiceType.STATIC_MAP_URL2, sourcDest);
//            }
//        }
//
//        sourcDest = source + "|" + dest;
//
//        url = String.format(AndyConstants.ServiceType.STATIC_MAP_URL, source, sourcDest) + lastparams;
//        *******************
        AppLog.Log("mapURL", mapURL);
        Picasso.with(mapActivity).load(mapURL).into(progressBar);
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 1000);
            progressBar.setValue(progress);
            if (!isVisible()) {
                return;
            }
            if (DriverApplication.preferenceHelper.getSoundAvailability()) {
                AppLog.Log("ClientRequest Timer Beep", "Beep started");
                if (loaded) {
                    soundPool.play(soundid, 1, 1, 0, 0, 1);
                }
            }
        }

        @Override
        public void onFinish() {
            progressBar.setValue(0);
            mapActivity.display_toast(mapActivity.getResources().getString(R.string.toast_time_over));
            setComponentInvisible();
            respondRequest(0);
            DriverApplication.preferenceHelper.clearRequestData();
            if (markerClientLocation != null
                    && markerClientLocation.isVisible()) {
                markerClientLocation.
                        remove();
            }
            if (markerClientDestination != null
                    && markerClientDestination.isVisible()) {
                markerClientDestination.
                        remove();
            }
            removeNotification();
            startCheckingUpcomingRequests();
            this.cancel();
            cancelSeekbarTimer();
        }

    }


    private void doLogoutWhenSessionOut() {
        getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class).putExtra("isSignin", true));
        getActivity().finish();
        Toast.makeText(getActivity(), mapActivity.getString(R.string.text_someone_logged_account), Toast.LENGTH_LONG).show();
        DriverApplication.preferenceHelper.clearRequestData();
        isContinueRequest = false;
    }

    @Override
    public void onStart() {
        super.onStart();
//        getActivity().registerReceiver(receiver, new IntentFilter("Request_Response_GCM"));
    }

    @Override
    public void onStop() {
        super.onStop();
//        getActivity().unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null)
                if (intent.getStringExtra("message") != null) {
                    if (intent.getStringExtra("message").matches("Accepted")) {
                        requestAccepted(true);
                        Toast.makeText(getActivity(), "Received:" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        requestAccepted(false);
                        Toast.makeText(getActivity(), "Received:" + intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();

                    }


                }
        }
    };

}