package com.jayeen.customer.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.R;
import com.jayeen.customer.UberViewPaymentActivity;
import com.jayeen.customer.adapter.PlacesAutoCompleteAdapter;
import com.jayeen.customer.component.MyFontButton;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.component.MyFontPopUpTextView;
import com.jayeen.customer.component.MyFontTextView;
import com.jayeen.customer.component.MyTitleFontTextView;
import com.jayeen.customer.models.Route;
import com.jayeen.customer.models.Step;
import com.jayeen.customer.newmodels.RequestLocation;
import com.jayeen.customer.newmodels.RequestStatusModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.jayeen.customer.utils.LocationHelper;
import com.jayeen.customer.utils.MathUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class UberTripFragment extends UberBaseFragment implements LocationHelper.OnLocationReceived, OnMapReadyCallback {
    private GoogleMap map;
    private PolylineOptions lineOptions;
    private Route route;
    ArrayList<LatLng> points;

    private TextView tvDist, tvDriverName, tvRate, tvStatus;//tvTime,
    private RequestStatusModel driver;
    private Marker myMarker, markerDriver;
    private CircularImageView ivDriverPhoto;
    private LocationHelper locHelper;
    private boolean isContinueStatusRequest;
    private boolean isContinueDriverRequest;
    private final int LOCATION_SCHEDULE = 5 * 1000;
    private Polyline polyLine;
    private LatLng myLatLng;
    private Location myLocation;
    private boolean isTripStarted = false;
    private WalkerStatusReceiver walkerReceiver;
    private boolean isAllLocationReceived = false;
    WakeLock wakeLock;
    private PopupWindow notificationWindow, driverStatusWindow;
    private MyFontPopUpTextView tvPopupMsg, tvJobAccepted, tvDriverStarted,
            tvDriverArrvied, tvTripStarted;
    private ImageView ivJobAccepted, ivDriverStarted, ivDriverArrvied,
            ivTripStarted;
    private MyFontTextView tvTaxiModel;
    private MyFontTextView tvTaxiNo;
    private MyFontTextView tvRateStar;
    private MyFontButton btnReasonConfirm, btnReasonCancel;
    private MyFontEdittextView rejectreasonedit;
    private TextView tvEstimatedTime;
    private View markerLayout;
    private TextView tvDurationUnit;
    private ProgressBar pBar;
    private LinearLayout layoutCash;
    private LinearLayout layoutCard;
    private TextView tvCash;
    private TextView tvCardNo;
    private ImageView ivCard;
    private BroadcastReceiver mReceiver;
    private MapView mMapView;
    private Bundle mBundle;
    private View view;
    private ImageButton btnCancelTrip;
    private AutoCompleteTextView etDestination;
    private PlacesAutoCompleteAdapter adapterDestination;
    private Marker markerDestination;
    private Route routeDest, routeDest1;
    private ArrayList<LatLng> pointsDest, pointsDest1;
    private PolylineOptions lineOptionsDest, lineOptionsDest1;
    private Polyline polyLineDest, polyLineDest1;
    private RelativeLayout layout;
    //    private ImageButton ibApplyPromo;
    private Dialog dialog;
    private EditText etPromoCode;
    private ImageView ivPromoResult;
    private TextView tvPromoResult;
    private LinearLayout llErrorMsg;
    private Button btnPromoSubmit, btnPromoSkip;
    //    private RequestQueue requestQueue;
    int prefcount = 0;
    private SoundPool soundPool;
    private int soundid;
    private boolean loaded = false;
    Dialog rDialog;
    String rejectreason = "";
    private boolean cancel_conform = false;
    Location PickupLatLng, DropLatLng, DriverLatLng;
    private TaxiParseContent parseContent;
    //    To check the popup windows is show and to avoid showing more than one @mukesh 20-01-2017
    private boolean notify_Walker_st = false, notify_Walk_st = false, notify_accepted = false, notify_arrived = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = ((MainDrawerActivity) getActivity()).parseContent;
        mBundle = savedInstanceState;
        PowerManager powerManager = (PowerManager) activity
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                Const.TAG);
        wakeLock.acquire();
        driver = (RequestStatusModel) getArguments().getParcelable(Const.DRIVER);
        points = new ArrayList<LatLng>();
        route = new Route();
        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 100);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });

        soundid = soundPool.load(activity, R.raw.beep, 1);
        IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
        walkerReceiver = new WalkerStatusReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                walkerReceiver, filter);
        isAllLocationReceived = false;

        activity.actionBar.setCustomView(R.layout.custom_action_bar);
        View customActionBarView = activity.actionBar.getCustomView();
//        customActionBarView.findViewById(R.id.llTop).setBackgroundResource(R.color.colorPrimary);
//        customActionBarView.setBackgroundResource(R.color.colorPrimary);


        activity.layoutDestination = (LinearLayout) customActionBarView
                .findViewById(R.id.layoutDestination);
        activity.btnNotification = (ImageButton) customActionBarView
                .findViewById(R.id.btnActionNotification);
        activity.btnNotification.setVisibility(View.VISIBLE);
        activity.btnSosNotification = (Button) customActionBarView.findViewById(R.id.btnSosNotification);
        activity.btnSosNotification.setOnClickListener(this);
        activity.btnNotification.setOnClickListener(this);

        activity.imgClearDst = (ImageButton) customActionBarView
                .findViewById(R.id.imgClearDst);

        activity.tvTitle = (MyTitleFontTextView) customActionBarView
                .findViewById(R.id.tvTitle);
        activity.tvTitle.setOnClickListener(this);

        activity.etSource = (AutoCompleteTextView) customActionBarView
                .findViewById(R.id.etEnterSouce);

        activity.btnActionMenu = (ImageButton) customActionBarView
                .findViewById(R.id.btnActionMenu);
        activity.btnActionMenu.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(activity.getString(R.string.app_name));
        activity.tvTitle.setVisibility(View.VISIBLE);
        activity.btnSosNotification.setVisibility(View.GONE);
        view = inflater.inflate(R.layout.fragment_trip_new, container, false);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
        view.findViewById(R.id.btnCall).setOnClickListener(this);
        etDestination = (AutoCompleteTextView) view.findViewById(R.id.et_dest);
//        tvTime = (MyFontTextView) view.findViewById(R.id.tvJobTime);
        tvDist = (MyFontTextView) view.findViewById(R.id.tvJobDistance);
        tvDriverName = (MyFontTextView) view.findViewById(R.id.tvDriverName);
        tvTaxiModel = (MyFontTextView) view.findViewById(R.id.tvTaxiModel);
        tvTaxiNo = (MyFontTextView) view.findViewById(R.id.tvTaxiNo);
        tvRateStar = (MyFontTextView) view.findViewById(R.id.tvRateStar);
        layoutCash = (LinearLayout) view.findViewById(R.id.layoutCash);
        layoutCard = (LinearLayout) view.findViewById(R.id.layoutCard);
        tvCash = (TextView) view.findViewById(R.id.tvCash);
        tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
        ivCard = (ImageView) view.findViewById(R.id.ivCard);
//        ibApplyPromo = (ImageButton) view.findViewById(R.id.ibApplyPromo);
        btnCancelTrip = (ImageButton) view.findViewById(R.id.btnCancelTrip);
        btnCancelTrip.setOnClickListener(this);
        layoutCash.setOnClickListener(this);
        layoutCard.setOnClickListener(this);
        ivDriverPhoto = (CircularImageView) view.findViewById(R.id.ivDriverPhoto);
        tvDriverName.setText(driver.walker.firstName + " " + driver.walker.lastName);
        tvTaxiModel.setText(driver.walker.carModel);
        tvTaxiNo.setText(driver.walker.carNumber);
        tvRateStar.setText(MathUtils.getRound(Float.parseFloat(driver.walker.rating + "")));

        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        mMapView = (MapView) view.findViewById(R.id.maptrip);
        mMapView.onCreate(mBundle);
        setUpMap();
        setDefaultCardDetails();
        if (CustomerApplication.preferenceHelper.getPaymentMode() == Const.CASH) {
            layoutCash.setSelected(true);
            layoutCard.setSelected(false);
            tvCash.setTextColor(getResources().getColor(R.color.white));
        } else {
            layoutCash.setSelected(false);
            layoutCard.setSelected(true);
            tvCardNo.setTextColor(getResources().getColor(R.color.red));
        }
        if (CustomerApplication.preferenceHelper.getIsTripStarted()) {
            btnCancelTrip.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppLog.Log("TripFragment", "Driver Photo : " + driver.walker.picture);

        if (driver.walker.picture != null && !driver.walker.picture.isEmpty()) {
            Picasso.with(getActivity())
                    .load(driver.walker.picture)
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user)
                    .into(ivDriverPhoto);
        }
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        adapterDestination = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);


        etDestination.setAdapter(adapterDestination);
        etDestination.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final String selectedDestPlace = adapterDestination
                        .getItem(arg2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CustomerApplication.preferenceHelper.putClientDestination(getLocationFromAddress(selectedDestPlace));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDestination(CustomerApplication.preferenceHelper.getClientDestination());
                            }
                        });
                    }
                }).start();
            }
        });

        locHelper.onStart();

        LayoutInflater inflate = LayoutInflater.from(activity);
        layout = (RelativeLayout) inflate.inflate(
                R.layout.popup_notification_window, null);
        tvPopupMsg = (MyFontPopUpTextView) layout.findViewById(R.id.tvPopupMsg);
        notificationWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layout.setOnClickListener(this);
        activity.btnNotification.setOnClickListener(this);
        activity.btnSosNotification.setOnClickListener(this);

        RelativeLayout bigPopupLayout = (RelativeLayout) inflate.inflate(
                R.layout.popup_notification_status_window, null);
        tvJobAccepted = (MyFontPopUpTextView) bigPopupLayout
                .findViewById(R.id.tvJobAccepted);
        tvDriverStarted = (MyFontPopUpTextView) bigPopupLayout
                .findViewById(R.id.tvDriverStarted);
        tvDriverArrvied = (MyFontPopUpTextView) bigPopupLayout
                .findViewById(R.id.tvDriverArrvied);
        tvTripStarted = (MyFontPopUpTextView) bigPopupLayout
                .findViewById(R.id.tvTripStarted);
        ivJobAccepted = (ImageView) bigPopupLayout
                .findViewById(R.id.ivJobAccepted);
        ivDriverStarted = (ImageView) bigPopupLayout
                .findViewById(R.id.ivDriverStarted);
        ivDriverArrvied = (ImageView) bigPopupLayout
                .findViewById(R.id.ivDriverArrvied);
        ivTripStarted = (ImageView) bigPopupLayout
                .findViewById(R.id.ivTripStarted);
        driverStatusWindow = new PopupWindow(bigPopupLayout,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        driverStatusWindow.setBackgroundDrawable(new BitmapDrawable());
        driverStatusWindow.setOutsideTouchable(true);
        showNotificationPopUp(activity.getString(R.string.text_job_accepted));
        tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_job_accepted)));
        PlayNotificationSound();
        changeNotificationPopUpUI(1);
    }

    private LatLng getLocationFromAddress(final String place) {
        AppLog.Log("Address", "" + place);
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(getActivity());
        try {
            final List<Address> list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCall:
                if (driver != null) {
                    String number = driver.walker.phone;
                    if (!TextUtils.isEmpty(number)) {
                        int currentapiVersion = Build.VERSION.SDK_INT;
                        if (currentapiVersion >= Build.VERSION_CODES.M) {


                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PRIVILEGED},
                                        10);
                            } else {
                                callPhone(number);
                            }
                        } else {
                            callPhone(number);
                        }
                    }
                }
                break;
            case R.id.rlPopupWindow:
                notificationWindow.dismiss();
                activity.setIcon(R.drawable.notification_box);
                break;
            case R.id.btnActionNotification:
                showDriverStatusNotification();
                break;
            case R.id.layoutCash:
                layoutCash.setSelected(true);
                layoutCard.setSelected(false);
                tvCash.setTextColor(getResources().getColor(R.color.white));
                tvCardNo.setTextColor(getResources().getColor(R.color.gray));
                CustomerApplication.preferenceHelper.putPaymentMode(Const.CASH);
                setPaymentMode(Const.CASH);
                break;
            case R.id.layoutCard:
                if (layoutCard.isSelected()) {
                    startActivity(new Intent(getActivity(),
                            UberViewPaymentActivity.class));
                } else {
                    layoutCash.setSelected(false);
                    layoutCard.setSelected(true);
                    tvCardNo.setTextColor(getResources().getColor(R.color.white));
                    tvCash.setTextColor(getResources().getColor(R.color.gray));
                    CustomerApplication.preferenceHelper.putPaymentMode(Const.CREDIT);
                    setPaymentMode(Const.CREDIT);
                }
                break;
            case R.id.btnCancelTrip:
                showCancelDialog();

                break;
            case R.id.btnConfirmReason:
                rejectreason = rejectreasonedit.getText().toString();
                rDialog.dismiss();
                cancleRequest();
                break;
            case R.id.btnCancelReason:
                rejectreason = "";
                rDialog.dismiss();
                break;
//            case R.id.btnAddDestination:
//                layout.setVisibility(View.GONE);
//                break;
            case R.id.imgClearDst:
                etDestination.setText("");
                break;
//            case R.id.ibApplyPromo:
//                showPromoDialog();
//                break;
            /*case R.id.btnPromoSkip:
                if (dialog != null)
                    dialog.dismiss();
                break;
            case R.id.btnPromoSubmit:
                applyPromoCode();
                break;*/
            default:
                // if(driverStatusWindow.isShowing())
                // driverStatusWindow.dismiss();
                break;
        }
    }


    private void showCancelDialog() {
        rDialog = new Dialog(getActivity());
        rDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rDialog.setContentView(R.layout.cancel_reason);

        btnReasonConfirm = (MyFontButton) rDialog.findViewById(R.id.btnConfirmReason);
        btnReasonCancel = (MyFontButton) rDialog.findViewById(R.id.btnCancelReason);
        rejectreasonedit = (MyFontEdittextView) rDialog.findViewById(R.id.etrejectval);
        btnReasonConfirm.setOnClickListener(this);
        btnReasonCancel.setOnClickListener(this);
        if (!activity.isFinishing())
            rDialog.show();
        rDialog.setCanceledOnTouchOutside(false);
    }


    public void showDriverStatusNotification() {
        if (driverStatusWindow.isShowing())
            driverStatusWindow.dismiss();
        else {
            if (driverStatusWindow.isShowing())
                driverStatusWindow.dismiss();
            else
                driverStatusWindow.showAsDropDown(activity.btnActionMenu);
        }

    }

    public void showNotificationPopUp(String text) {
        tvPopupMsg.setText(text);
        if (!driverStatusWindow.isShowing()) {
            if (!notificationWindow.isShowing()) {
                activity.setIcon(R.drawable.notification_box_arrived);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        notificationWindow.showAsDropDown(activity.btnNotification);
                    }
                }, 100);

            } else {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        activity.btnNotification.setVisibility(View.VISIBLE);
        startUpdateDriverLocation();
        startCheckingStatusUpdate();
        registerCardReceiver();
        CustomerApplication.preferenceHelper.putIsTripOn(true);
    }

    @Override
    public void onPause() {
        stopUpdateDriverLoaction();
        stopCheckingStatusUpdate();
//        ubregisterCardReceiver();
        super.onPause();
        mMapView.onPause();
    }

    private void setUpMap() {
        if (map == null) {
            ((MapView) view.findViewById(R.id.maptrip)).getMapAsync(this);
        }
        initPreviousDrawPath();

    }

    private void setMarkers(LatLng latLang) {
//        LatLng latLngDriver = new LatLng(Double.parseDouble(driver.walker.latitude),Double.parseDouble(driver.walker.longitude));
        LatLng latLngDriver = new LatLng(latLang.latitude, latLang.longitude);
        setMarker(latLngDriver);
//        setDriverMarker(latLngDriver, Double.parseDouble(driver.walker.bearing));
        if (driver != null)
            if (driver.walker != null) {
                setDriverMarker(latLngDriver, Double.valueOf(Double.parseDouble(driver.walker.bearing != null ? driver.walker.bearing : "0.0")));
            }
        animateCameraToMarkerWithZoom(latLngDriver);
        boundLatLang();
    }


    private void drawPath(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        if (destination.latitude != 0) {
            setDestinationMarker(destination);
            boundLatLang();

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.DIRECTIONS_URL + "json?origin="
                    + source.latitude + "," + source.longitude
                    + "&destination=" + destination.latitude + ","
                    + destination.longitude + "&sensor=false&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY);
            requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                    Const.ServiceCode.DRAW_PATH, this, this));
        }

    }

    private void drawPathPickup(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        if (destination.latitude != 0) {
            boundLatLang();

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.DIRECTIONS_URL + "json?origin="
                    + source.latitude + "," + source.longitude
                    + "&destination=" + destination.latitude + ","
                    + destination.longitude + "&sensor=false&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY);
            requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                    Const.ServiceCode.DRAW_PATHPICKUP, this, this));
        }

    }

    private void boundLatLang() {

        try {
            if (myMarker != null && markerDriver != null
                    && markerDestination != null) {
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                bld.include(new LatLng(myMarker.getPosition().latitude,
                        myMarker.getPosition().longitude));
                bld.include(new LatLng(markerDriver.getPosition().latitude,
                        markerDriver.getPosition().longitude));
                bld.include(new LatLng(
                        markerDestination.getPosition().latitude,
                        markerDestination.getPosition().longitude));
                LatLngBounds latLngBounds = bld.build();

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        latLngBounds, 50));
            } else if (myMarker != null && markerDriver != null) {
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                bld.include(new LatLng(myMarker.getPosition().latitude,
                        myMarker.getPosition().longitude));
                bld.include(new LatLng(markerDriver.getPosition().latitude,
                        markerDriver.getPosition().longitude));
                LatLngBounds latLngBounds = bld.build();

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        latLngBounds, 100));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void onDestroyView() {
        wakeLock.release();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.maptrip);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        map = null;
        super.onDestroyView();
    }

    @SuppressLint("NewApi")
    @Override
    public void onTaskCompleted(final String response, int serviceCode) {
        if (!this.isVisible())
            return;
        switch (serviceCode) {
            case Const.ServiceCode.GET_ROUTE:
                AndyUtils.removeCustomProgressDialog();
                if (!TextUtils.isEmpty(response)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            parseContent.parseRoute(response, route);
                            final ArrayList<Step> step = route.getListStep();
                            points = new ArrayList<LatLng>();
                            lineOptions = new PolylineOptions();
                            lineOptions.geodesic(true);
                            for (int i = 0; i < step.size(); i++) {
                                List<LatLng> path = step.get(i).getListPoints();
                                points.addAll(path);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (polyLine != null)
                                        polyLine.remove();
                                    lineOptions.addAll(points);
                                    lineOptions.width(8);
                                    lineOptions.geodesic(true);
                                    lineOptions.color(getResources().getColor(R.color.skyblue));
                                    polyLine = map.addPolyline(lineOptions);
                                    LatLngBounds.Builder bld = new LatLngBounds.Builder();
                                    bld.include(myMarker.getPosition());
                                    bld.include(markerDriver.getPosition());
                                    LatLngBounds latLngBounds = bld.build();
                                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                                }
                            });
                        }
                    }).start();
                }
            case Const.ServiceCode.DRAW_PATH_ROAD:
                if (!TextUtils.isEmpty(response)) {
                    routeDest = new Route();
                    parseContent.parseRoute(response, routeDest);

                    final ArrayList<Step> step = routeDest.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest = new ArrayList<LatLng>();
                    lineOptionsDest = new PolylineOptions();
                    lineOptionsDest.geodesic(true);

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest.addAll(path);
                    }
                    if (pointsDest != null && pointsDest.size() > 0) {
                        drawPath(myMarker.getPosition(),
                                CustomerApplication.preferenceHelper.getClientDestination());
                    }
                }
                break;
            case Const.ServiceCode.DRAW_PATH:
                if (!TextUtils.isEmpty(response)) {
                    routeDest = new Route();
                    parseContent.parseRoute(response, routeDest);

                    final ArrayList<Step> step = routeDest.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest = new ArrayList<LatLng>();
                    lineOptionsDest = new PolylineOptions();
                    lineOptionsDest.geodesic(true);

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest.addAll(path);
                    }
                    if (polyLineDest != null)
                        polyLineDest.remove();
                    lineOptionsDest.addAll(pointsDest);
                    lineOptionsDest.width(8);
                    lineOptionsDest.color(getResources().getColor(
                            R.color.color_text)); // #00008B rgb(0,0,139)
                    try {
                        if (lineOptionsDest != null && map != null) {
                            polyLineDest = map.addPolyline(lineOptionsDest);
                            boundLatLang();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.DRAW_PATHPICKUP:
                if (!TextUtils.isEmpty(response)) {
                    routeDest1 = new Route();
                    parseContent.parseRoute(response, routeDest1);

                    final ArrayList<Step> step = routeDest1.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest1 = new ArrayList<LatLng>();
                    lineOptionsDest1 = new PolylineOptions();
                    lineOptionsDest1.geodesic(true);

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest1.addAll(path);
                    }
                    if (polyLineDest1 != null)
                        polyLineDest1.remove();
                    lineOptionsDest1.addAll(pointsDest1);
                    lineOptionsDest1.width(8);
                    lineOptionsDest1.color(getResources().getColor(
                            R.color.skyblue)); // #00008B rgb(0,0,139)
                    try {
                        if (lineOptionsDest1 != null && map != null) {
                            polyLineDest1 = map.addPolyline(lineOptionsDest1);
                            boundLatLang();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.GET_REQUEST_LOCATION:
                RequestLocation requestLocation = parseContent.getSingleObject(response, RequestLocation.class);
                if (requestLocation != null) {
                    if (requestLocation.success) {
                        if (requestLocation == null || !this.isValidate() || new LatLng(requestLocation.latitude, requestLocation.longitude) == null) {

                            setDriverMarker(requestLocation.latlong(), requestLocation.bearing);
                            drawTrip(requestLocation.latlong());
                            if (isTripStarted) {
                                btnCancelTrip.setVisibility(View.GONE);
                                long startTime = Const.NO_TIME;
//                            if (CustomerApplication.preferenceHelper.getRequestTime() == Const.NO_TIME) {
//                                startTime = System.currentTimeMillis();
//                                CustomerApplication.preferenceHelper.putRequestTime(startTime);
//                            } else {
//                                startTime = CustomerApplication.preferenceHelper.getRequestTime();
//                            }
//                            String str = new String(requestLocation.distance);
//                            str.replace(',', '.');
//                            double distance = Double.parseDouble(requestLocation.distance);
//                            distance = distance / 1625;
//                            if (CustomerApplication.preferenceHelper.getRequestTime() == Const.NO_TIME) {
//                                CustomerApplication.preferenceHelper.putRequestTime(System.currentTimeMillis());
//                            }
                                if (CustomerApplication.preferenceHelper.getRequestTime() == Const.NO_TIME)
                                    CustomerApplication.preferenceHelper.putRequestTime(Calendar.getInstance().getTimeInMillis());
                                String time = String.valueOf(((Calendar.getInstance().getTimeInMillis() -
                                        CustomerApplication.preferenceHelper.getRequestTime())
                                        / (1000 * 60)) > 0 ? ((Calendar.getInstance().getTimeInMillis() -
                                        CustomerApplication.preferenceHelper.getRequestTime()) / (1000 * 60)) : requestLocation.time);
                                tvDist.setText(new DecimalFormat("0.00").format(Double.parseDouble(requestLocation.distance)) + " " + requestLocation.unit);
//                                tvTime.setText(((int) (Float.parseFloat(requestLocation.time))) + " MINS");
                            } else {
                                if (myMarker != null && markerDriver != null) {
                                    getdurationTrip(myMarker.getPosition(), markerDriver.getPosition());
                                }
                            }
                        }
                        isContinueDriverRequest = true;
                        break;
                    } else {
                        AndyUtils.showToast(parseContent.ErrorResponse(requestLocation.error_code), R.id.coordinatorLayout, activity);
                    }
                } else {
                    //AndyUtils.showToast(parseContent.ErrorResponse(requestLocation.error_code), R.id.coordinatorLayout, activity);
                    Log.e("Test", "Solution");
                }


            case Const.ServiceCode.GET_REQUEST_STATUS:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log("GETRequest", response);
                RequestStatusModel requestStatusModel = parseContent.getSingleObject(response, RequestStatusModel.class);
                if (requestStatusModel.success) {
                    switch (parseContent.checkRequestStatus(requestStatusModel)) {
                        case Const.IS_WALK_STARTED:
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_trip_started)));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(4);
                            if (!notify_Walk_st) {
                                showNotificationPopUp(activity.getString(R.string.text_trip_started));
                                notify_Walk_st = true;
                            }
                            isContinueStatusRequest = true;
                            isTripStarted = true;

                            break;
                        case Const.IS_COMPLETED:
                            btnCancelTrip.setVisibility(View.GONE);
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_trip_completed)));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(5);
                            if (!isAllLocationReceived) {
                                isAllLocationReceived = true;
                                getPath(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
                            }
                            isContinueStatusRequest = true;
                            isTripStarted = true;
                            CustomerApplication.preferenceHelper.putIsTripStarted(true);
                            ((MainDrawerActivity) getActivity()).gotoRateFragment(requestStatusModel);
                            break;
                        case Const.IS_WALKER_ARRIVED:
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_driver_arrvied)));
                            if (!notify_arrived) {
                                showNotificationPopUp(activity.getString(R.string.text_driver_arrvied));
                                notify_arrived = true;
                            }
                            PlayNotificationSound();
                            changeNotificationPopUpUI(3);
                            isContinueStatusRequest = true;
                            break;
                        case 11:
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_job_accepted)));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(1);
                            isContinueStatusRequest = true;
                            isTripStarted = false;
                            break;
                        case Const.IS_WALKER_STARTED:
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_driver_started)));
                            if (!notify_Walker_st) {
                                showNotificationPopUp(activity.getString(R.string.text_driver_started));
                                notify_Walker_st = true;
                            }
                            PlayNotificationSound();
                            changeNotificationPopUpUI(2);
                            isContinueStatusRequest = true;
                            isTripStarted = false;
                            break;
                        case Const.IS_WALKER_RATED:
                            stopCheckingStatusUpdate();
                            isTripStarted = false;
                            if (notificationWindow.isShowing())
                                notificationWindow.dismiss();
                            if (driverStatusWindow.isShowing())
                                driverStatusWindow.dismiss();
                            requestStatusModel = parseContent.getDriverDetail(response);
                            driver.distance = "";
                            driver.time = "";
                            activity.gotoRateFragment(requestStatusModel);
                            break;
                        case Const.IS_WALKER_CANC:
                            cancel_conform = true;
                            gotoMap1Fragment(cancel_conform);
                            break;
                        default:

                            break;
                    }
//{"success":false,"error":"Solicitud ID no encontrada","error_code":628}
                } else if (requestStatusModel.error_code == Const.REQUEST_ID_NOT_FOUND2) {
                    AndyUtils.removeCustomProgressDialog();
                    CustomerApplication.preferenceHelper.clearRequestData();
                    isContinueStatusRequest = false;
                    AndyUtils.showToast(activity.getString(R.string.error_something_went_wrong), R.id.coordinatorLayout, activity);
                    activity.gotoMapFragment();
                } else {
                    isContinueStatusRequest = true;
                }
                break;
            case Const.ServiceCode.GET_PATH:
                AndyUtils.removeCustomProgressDialog();
                parseContent.parsePathRequest(response, points);
                initPreviousDrawPath();
                break;
            case Const.ServiceCode.GET_DURATION:
                pBar.setVisibility(View.GONE);

                String[] durationArr = parseContent.parseNearestDriverDurationString(response).split(" ");
                myMarker.setIcon(BitmapDescriptorFactory.fromBitmap(AndyUtils
                        .createDrawableFromView(getActivity(), markerLayout)));

                break;
            case Const.ServiceCode.PAYMENT_TYPE:
                AndyUtils.removeCustomProgressDialog();
                if (!parseContent.isSuccess(response)) {
                    AndyUtils.showToast(activity.getString(R.string.payment_mode), R.id.coordinatorLayout, activity);
                }
                break;
            case Const.ServiceCode.CANCEL_REQUEST:
                if (parseContent.isSuccess(response)) {

                }
                CustomerApplication.preferenceHelper.clearRequestData();
                stopCheckingStatusUpdate();
                stopUpdateDriverLoaction();
                AndyUtils.removeCustomProgressDialog();
                activity.gotoMapFragment();
                break;
            case Const.ServiceCode.SET_DESTINATION:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log("Trip", "Destination Response : " + response);
                if (parseContent.isSuccess(response)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawPath(myMarker.getPosition(),
                                    CustomerApplication.preferenceHelper.getClientDestination());
                        }
                    });
                }
                break;

            case Const.ServiceCode.DURATIONREQUEST:
                try {
                    JSONObject parntObject = new JSONObject(response);
                    String TripDuration = parntObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                    if (tvDurationUnit != null && tvEstimatedTime != null) {
                        String numberOnly = TripDuration.replaceAll("[^0-9]", "");
                        String stringOnl = TripDuration.replaceAll("\\d+.*", "");
                        tvEstimatedTime.setText(numberOnly);
                        tvDurationUnit.setText("mins");
                    }
                } catch (Exception e) {
                    e.getMessage();
                    AppLog.Log("UberTripFragment", e.getMessage());
                }
                break;
        }
    }

    private void changeNotificationPopUpUI(int i) {
        switch (i) {
            case 1:
                ivJobAccepted.setImageResource(R.drawable.checkbox);
                tvJobAccepted.setTextColor(getResources().getColor(R.color.color_text));
                soundPool.stop(soundid);
                break;
            case 2:
                ivJobAccepted.setImageResource(R.drawable.checkbox);
                tvJobAccepted.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivDriverStarted.setImageResource(R.drawable.checkbox);
                tvDriverStarted.setTextColor(getResources().getColor(
                        R.color.color_text));
                break;
            case 3:
                ivJobAccepted.setImageResource(R.drawable.checkbox);
                tvJobAccepted.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivDriverStarted.setImageResource(R.drawable.checkbox);
                tvDriverStarted.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivDriverArrvied.setImageResource(R.drawable.checkbox);
                tvDriverArrvied.setTextColor(getResources().getColor(
                        R.color.color_text));
                break;
            case 4:
                ivJobAccepted.setImageResource(R.drawable.checkbox);
                tvJobAccepted.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivDriverStarted.setImageResource(R.drawable.checkbox);
                tvDriverStarted.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivDriverArrvied.setImageResource(R.drawable.checkbox);
                tvDriverArrvied.setTextColor(getResources().getColor(
                        R.color.color_text));
                ivTripStarted.setImageResource(R.drawable.checkbox);
                tvTripStarted.setTextColor(getResources().getColor(
                        R.color.color_text));
                btnCancelTrip.setVisibility(View.GONE);
                if (tvDurationUnit != null && tvEstimatedTime != null) {
                    tvDurationUnit.setText("");
                    tvEstimatedTime.setText("");
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = activity.getLayoutInflater().inflate(
                        R.layout.info_window_layout, null);
                ((MyFontTextView) v).setText(marker.getTitle());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }


    Handler handlerTrackLocation = new Handler();
    private Runnable runnableTrackLocation = new Runnable() {
        @Override
        public void run() {
            if (isContinueDriverRequest) {
                isContinueDriverRequest = true;
                getDriverLocation();
            }
            handlerTrackLocation.postDelayed(this, LOCATION_SCHEDULE);
        }
    };

    private void getDriverLocation() {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "="
                        + CustomerApplication.preferenceHelper.getRequestId());
        AppLog.Log("TAG",
                Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "="
                        + CustomerApplication.preferenceHelper.getRequestId());
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REQUEST_LOCATION, this, this));

    }

    private void setMarker(LatLng latLng) {
        if (latLng != null) {
            if (map != null && this.isVisible()) {
                if (myMarker == null) {
                    markerLayout = ((LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.custom_marker_layout, null);
                    tvEstimatedTime = (TextView) markerLayout
                            .findViewById(R.id.num_txt);
                    tvDurationUnit = (TextView) markerLayout
                            .findViewById(R.id.tvDurationUnit);
                    pBar = (ProgressBar) markerLayout.findViewById(R.id.pBar);

                    MarkerOptions opt = new MarkerOptions();
                    opt.position(latLng);
                    opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils
                            .createDrawableFromView(getActivity(), markerLayout)));
                    opt.title(activity.getString(R.string.text_my_location));
                    if (CustomerApplication.preferenceHelper.Getpickuplat() != null && CustomerApplication.preferenceHelper.GetpickupLng() != null) {
                        opt.position(new LatLng(Double.valueOf(CustomerApplication.preferenceHelper.GetpickupLng()), Double.valueOf(Double.valueOf(CustomerApplication.preferenceHelper.GetpickupLng()))));
                    }
                    myMarker = map.addMarker(opt);

                } else {
                    myMarker.setPosition(latLng);
                }
                animateCameraToMarkerWithZoom(latLng);
                if (myMarker != null && markerDriver != null) {
                    drawPathPickup(markerDriver.getPosition(), myMarker.getPosition());
                    getdurationTrip(markerDriver.getPosition(), myMarker.getPosition());
                }
                if (myMarker != null && CustomerApplication.preferenceHelper.getClientDestination() != null) {
                    drawPath(myMarker.getPosition(), CustomerApplication.preferenceHelper.getClientDestination());
                }

            }
        }
    }

    private void setDriverMarker(LatLng latLng, double bearing) {
        if (latLng != null) {
            if (map != null && this.isVisible()) {
                if (markerDriver == null) {
                    MarkerOptions opt = new MarkerOptions();
                    opt.flat(true);
                    opt.anchor(0.5f, 0.5f);
                    opt.position(latLng);
                    opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)));
                    opt.title(activity.getString(R.string.text_drive_location));
                    markerDriver = map.addMarker(opt);
                    boundLatLang();
                } else {
                    Location driverLocation = new Location("");
                    driverLocation.setLatitude(latLng.latitude);
                    driverLocation.setLongitude(latLng.longitude);
                    driverLocation.setBearing((float) bearing);
                    animateMarker(markerDriver, latLng, driverLocation, false);
                }
                if (myMarker != null && myMarker.getPosition() != null)
                    getDirectionsUrl(latLng, myMarker.getPosition());
            }
        }
    }

    private void setDestinationMarker(LatLng latLng) {
        if (latLng != null) {
            if (map != null && this.isVisible()) {
                if (markerDestination == null) {
                    MarkerOptions opt = new MarkerOptions();
                    opt.position(latLng);
                    opt.icon(BitmapDescriptorFactory
                            .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                    opt.title(activity.getString(R.string.text_destination_pin_title));
                    markerDestination = map.addMarker(opt);
                } else {
                    markerDestination.setPosition(latLng);
                }
            }
        }
    }

    private void startUpdateDriverLocation() {
        isContinueDriverRequest = true;
        handlerTrackLocation.postDelayed(runnableTrackLocation, LOCATION_SCHEDULE);
    }

    private void stopUpdateDriverLoaction() {
        isContinueDriverRequest = false;

        if (handlerTrackLocation != null) {
            handlerTrackLocation.removeCallbacks(runnableTrackLocation);
        }
    }

    private void animateCameraToMarkerWithZoom(LatLng latLng) {
        CameraUpdate cameraUpdate = null;
        cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(latLng, Const.MAP_ZOOM);
        map.animateCamera(cameraUpdate);
    }

    private void startCheckingStatusUpdate() {
        stopCheckingStatusUpdate();
        if (CustomerApplication.preferenceHelper.getRequestId() != Const.NO_REQUEST) {
            isContinueStatusRequest = true;
            handlerRequestStatus.postDelayed(runnableRequestStatus, Const.TIME_SCHEDULE);
        }
    }

    private void stopCheckingStatusUpdate() {
        isContinueStatusRequest = false;
        if (handlerRequestStatus != null)
            handlerRequestStatus.removeCallbacks(runnableRequestStatus);
    }

    Handler handlerRequestStatus = new Handler();
    private Runnable runnableRequestStatus = new Runnable() {
        @Override
        public void run() {
            if (isContinueStatusRequest) {
                isContinueStatusRequest = false;
                getRequestStatus(String
                        .valueOf(CustomerApplication.preferenceHelper.getRequestId()));
            }
            handlerRequestStatus.postDelayed(this, Const.TIME_SCHEDULE);
        }
    };

    private void getRequestStatus(String requestId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "=" + requestId);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REQUEST_STATUS, this, this));
    }

    private void getPath(String requestId) {
//        AndyUtils.showCustomProgressDialog(activity,getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_PATH + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "=" + requestId);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_PATH, this, this));
    }


    private void drawTrip(LatLng latlng) {
        if (map != null && this.isVisible()) {
            points.add(latlng);
            lineOptions = new PolylineOptions();
            lineOptions.addAll(points);
            lineOptions.width(8);
            lineOptions.geodesic(true);
            lineOptions.color(getResources().getColor(R.color.skyblue));
            map.addPolyline(lineOptions);
        }
    }

    class WalkerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(Const.EXTRA_WALKER_STATUS);
            AppLog.Log("Response ---- Trip", response);
            if (TextUtils.isEmpty(response))
                return;
            stopCheckingStatusUpdate();
            try {
                RequestStatusModel requestStatusModel = parseContent.getSingleObject(response, RequestStatusModel.class);
                if (requestStatusModel.success) {
                    switch (parseContent.checkRequestStatus(requestStatusModel)) {
                        case 11:    //Accepted trip
                            tvStatus.setText(Html.fromHtml(getString(R.string.text_job_accepted)));
                            showNotificationPopUp(activity.getString(R.string.text_job_accepted));
                            changeNotificationPopUpUI(1);
                            isContinueStatusRequest = true;
                            break;
                        case Const.IS_WALK_STARTED: //Trip Started
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_trip_started)));
                            showNotificationPopUp(activity.getString(R.string.text_trip_started));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(4);
                            isContinueStatusRequest = true;
                            isTripStarted = true;
//                            tvStatus.setText(Html.fromHtml(getString(R.string.text_driver_arrvied)));
//                            showNotificationPopUp(getString(R.string.text_driver_arrvied));
//                            //PlayNotificationSound();
//                            changeNotificationPopUpUI(3);
//                            isContinueStatusRequest = true;
//                            isTripStarted = false;
                            break;
                        case Const.IS_COMPLETED:
                            btnCancelTrip.setVisibility(View.GONE);
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_trip_completed)));
//                            showNotificationPopUp(getString(R.string.text_trip_completed));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(5);
                            if (!isAllLocationReceived) {
                                isAllLocationReceived = true;
                                getPath(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
                            }
                            isContinueStatusRequest = true;
                            isTripStarted = true;
                            CustomerApplication.preferenceHelper.putIsTripStarted(true);
                            ((MainDrawerActivity) getActivity()).gotoRateFragment(requestStatusModel);
//                            tvStatus.setText(Html.fromHtml(getString(R.string.text_trip_started)));
//                            showNotificationPopUp(getString(R.string.text_trip_started));
//                            changeNotificationPopUpUI(4);
//                            if (!isAllLocationReceived) {
//                                isAllLocationReceived = true;
//                                getPath(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
//                            }
//                            isContinueStatusRequest = true;
//                            isTripStarted = true;
                            break;
                        case Const.IS_WALKER_ARRIVED:
//                            tvStatus.setText(Html.fromHtml(getString(R.string.text_driver_started)));
//                            showNotificationPopUp(getString(R.string.text_driver_started));
//                            changeNotificationPopUpUI(2);
//                            isContinueStatusRequest = true;
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_driver_arrvied)));
                            showNotificationPopUp(activity.getString(R.string.text_driver_arrvied));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(3);
                            isContinueStatusRequest = true;
                            break;
                        case Const.IS_WALKER_STARTED:
//                            tvStatus.setText(Html.fromHtml(getString(R.string.text_job_accepted)));
//                            showNotificationPopUp(getString(R.string.text_job_accepted));
//                            changeNotificationPopUpUI(1);
//                            isContinueStatusRequest = true;
                            tvStatus.setText(Html.fromHtml(activity.getString(R.string.text_driver_started)));
                            showNotificationPopUp(activity.getString(R.string.text_driver_started));
                            PlayNotificationSound();
                            changeNotificationPopUpUI(2);
                            isContinueStatusRequest = true;
                            isTripStarted = false;
                            break;
                        case Const.IS_WALKER_RATED:
                            stopCheckingStatusUpdate();
                            isTripStarted = false;
                            if (notificationWindow.isShowing())
                                notificationWindow.dismiss();
                            if (driverStatusWindow.isShowing())
                                driverStatusWindow.dismiss();
                            requestStatusModel = parseContent.getDriverDetail(response);
                            driver.distance = "";
                            driver.time = "";
                            activity.gotoRateFragment(requestStatusModel);
                            break;
                        default:
                            break;
                    }
                } else {
                    isContinueStatusRequest = true;
                }
                startCheckingStatusUpdate();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private void initPreviousDrawPath() {
        lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(8);
        lineOptions.geodesic(true);
        lineOptions.color(getResources().getColor(R.color.skyblue));
        if (map != null && this.isVisible())
            map.addPolyline(lineOptions);
        points.clear();
    }

    private void PlayNotificationSound() {
        if (loaded) {
            soundPool.play(soundid, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                walkerReceiver);
        if (notificationWindow.isShowing())
            notificationWindow.dismiss();
        if (driverStatusWindow.isShowing())
            driverStatusWindow.dismiss();
        ubregisterCardReceiver();

        CustomerApplication.preferenceHelper.putIsTripOn(false);
    }

    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final Location toLocation, final boolean hideMarker) {
        if (map == null || !this.isVisible()) {
            return;
        }
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                float rotation = (float) (t * toLocation.getBearing() + (1 - t)
                        * startRotation);
                if (rotation != 0) {
                    marker.setRotation(rotation);
                }
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void getDirectionsUrl(LatLng origin, LatLng destination) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        } else if (origin == null) {
            return;
        }
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;
        String str_dest = "destination=" + destination.latitude + ","
                + destination.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = Const.ServiceType.DIRECTIONS_URL
                + output + "?" + parameters;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, url);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.GET_DURATION, this, this));
    }

    private void setPaymentMode(int type) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(activity,
                getString(R.string.text_changing_payment_mode), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.PAYMENT_TYPE);
        map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
        map.put(Const.Params.TOKEN,
                String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
        map.put(Const.Params.CASH_OR_CARD, String.valueOf(type));

        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.PAYMENT_TYPE, this, this));
    }

    private void setDefaultCardDetails() {
        if (CustomerApplication.preferenceHelper.getDefaultCard() == 0) {
            layoutCard.setVisibility(View.INVISIBLE);
        } else {
            layoutCard.setVisibility(View.VISIBLE);
            tvCardNo.setText("*****" + CustomerApplication.preferenceHelper.getDefaultCardNo());
            String type = CustomerApplication.preferenceHelper.getDefaultCardType();
            if (type.equalsIgnoreCase(Const.VISA)) {
                ivCard.setImageResource(R.drawable.ub__creditcard_visa);
            } else if (type.equalsIgnoreCase(Const.MASTERCARD)) {
                ivCard.setImageResource(R.drawable.ub__creditcard_mastercard);
            } else if (type.equalsIgnoreCase(Const.AMERICAN_EXPRESS)) {
                ivCard.setImageResource(R.drawable.ub__creditcard_amex);
            } else if (type.equalsIgnoreCase(Const.DISCOVER)) {
                ivCard.setImageResource(R.drawable.ub__creditcard_discover);
            } else if (type.equalsIgnoreCase(Const.DINERS_CLUB)) {
                ivCard.setImageResource(R.drawable.ub__creditcard_discover);
            } else {
                ivCard.setImageResource(R.drawable.ub__nav_payment);
            }
        }
    }

    private void registerCardReceiver() {
        IntentFilter intentFilter = new IntentFilter("card_change_receiver");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setDefaultCardDetails();
                AppLog.Log("TripFragment", "Card change Receiver");
            }
        };
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private void ubregisterCardReceiver() {
        if (mReceiver != null) {
            try {
//            if (mReceiver.isOrderedBroadcast())
                getActivity().unregisterReceiver(mReceiver);
            } catch (Exception e) {
                AppLog.Log("UberTripFragment", e.getMessage());
            }
        }
    }

    private void cancleRequest() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AppLog.Log("UberTripFragment", "Request ID : " + CustomerApplication.preferenceHelper.getRequestId());
        AndyUtils.showCustomProgressDialog(activity, activity.getString(R.string.text_canceling_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CANCEL_REQUEST);
        map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
        map.put(Const.Params.TOKEN,
                String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
        map.put(Const.Params.REASONFORREJECT, rejectreason);
        map.put("ontrip", "true");
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.CANCEL_REQUEST, this, this));
    }

    private void setDestination(LatLng destination) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        if (destination != null) {
            AndyUtils.showCustomProgressDialog(activity, activity.
                    getString(R.string.text_adding_dest), true, null);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.SET_DESTINATION);
            map.put(Const.Params.ID,
                    String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
            map.put(Const.Params.TOKEN,
                    String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
            map.put(Const.Params.REQUEST_ID,
                    String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
            map.put(Const.Params.DEST_LAT, String.valueOf(destination.latitude));
            map.put(Const.Params.DEST_LNG,
                    String.valueOf(destination.longitude));

            prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
            prefcount++;
            CustomerApplication.preferenceHelper.putRequestTotal(prefcount);

            requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                    Const.ServiceCode.SET_DESTINATION, this, this));
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // 
        AppLog.Log(Const.TAG, error.getMessage());
    }

    @Override
    public void onLocationReceived(LatLng latlong) {
        // 
        if (isTripStarted && isAllLocationReceived) {
            myLocation.setLatitude(latlong.latitude);
            myLocation.setLongitude(latlong.longitude);
            if (!isTripStarted)
                setMarker(latlong);
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        // 

    }

    @Override
    public void onConntected(Bundle bundle) {
        // 
    }

    @Override
    public void onConntected(Location location) {
        // 
        if (location != null) {

            myLocation = location;
            myLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (UberTripFragment.this.isVisible())
                setMarkers(myLatLng);
            if (CustomerApplication.preferenceHelper.Getpickuplat() != null && CustomerApplication.preferenceHelper.GetpickupLng() != null) {
                double stringlat = Double.parseDouble(CustomerApplication.preferenceHelper.Getpickuplat());
                double stringlng = Double.parseDouble(CustomerApplication.preferenceHelper.GetpickupLng());
                LatLng newclientpickup = new LatLng(stringlat, stringlng);
                setMarker(newclientpickup);
            }
        }
    }

    public void callPhone(String phoneNumbertoCall) {
        if (!TextUtils.isEmpty(phoneNumbertoCall)) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"
                    + phoneNumbertoCall));
            startActivity(callIntent);
        } else {
            // Toast.makeText(mapActivity,mapActivity.getResources().getString(R.string.toast_number_not_found),Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoMap1Fragment(boolean cancel_conform) {
        if (cancel_conform) {
            if (rDialog != null)
                rDialog.dismiss();
            Fragment fragment = new UberMapFragment();
            ((MainDrawerActivity) getActivity()).addFragment(fragment, false, Const.FRAGMENT_MAP);
            ((MainDrawerActivity) getActivity()).drivercanceldialog();

        }
    }

    private void getdurationTrip(LatLng source, LatLng Dest) {
        HashMap<String, String> map = new HashMap<String, String>();

        String Strsource = source.latitude + "," + source.longitude;
        String StrDest = Dest.latitude + "," + Dest.longitude;

        map.put(Const.URL,
                (String.format(Const.ServiceType.DURATONAPI, Strsource, StrDest)));

        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.DURATIONREQUEST, this, this));
    }
}