package com.jayeen.customer.fragments;

import android.Manifest;
import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.R;
import com.jayeen.customer.adapter.PlacesAutoCompleteAdapter;
import com.jayeen.customer.adapter.VehicalTypeNewAdapter;
import com.jayeen.customer.interfaces.OnProgressCancelListener;
import com.jayeen.customer.models.AllProviderList;
import com.jayeen.customer.models.Route;
import com.jayeen.customer.newmodels.ApplyPromoModel;
import com.jayeen.customer.newmodels.ETA_calc_Model;
import com.jayeen.customer.newmodels.LatestProvidesListModel;
import com.jayeen.customer.newmodels.ProviderListModel;
import com.jayeen.customer.newmodels.RequestStatusModel;
import com.jayeen.customer.newmodels.RideNowSuccess;
import com.jayeen.customer.newmodels.SuccessModel;
import com.jayeen.customer.newmodels.VehicleTypeModel;
import com.jayeen.customer.newmodels.VehicleTypeSuccess;
import com.jayeen.customer.parse.RecycleOnItemClick;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.jayeen.customer.utils.LocationHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class UberMapFragment extends UberBaseFragment implements
        OnProgressCancelListener, OnSeekBarChangeListener, OnItemClickListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        LocationHelper.OnLocationReceived, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, OnDateSelectedListener, TimePickerDialog.OnTimeSetListener, OnMapReadyCallback {
    private PlacesAutoCompleteAdapter adapter;
    private AutoCompleteTextView etSource, etDropoff;
    public static boolean isMapTouched = false;
    private float currentZoom = -1;
    private GoogleMap map;
    public RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    VehicalTypeNewAdapter mAdapter;
    RecycleOnItemClick recycleOnItemClick;
    String Firststraddress = "";
    public LatLng curretLatLng;
    private String strAddress = null;
    private boolean isContinueRequest;
    private Timer timer;
    private WalkerStatusReceiver walkerReceiver;
    private ImageView btnMyLocation;
    private FrameLayout mapFrameLayout;
    private ArrayList<VehicleTypeModel> listType;
    private int selectedPostion = -1;
    private boolean isGettingVehicalType = true;
    private ImageView ivCash;
    private ImageView ivCredit;
    private ImageButton ivDatepick;
    private ImageButton ivTimepick;
    private TextView ivdatetextView, ivtimetextView;
    private int paymentMode = 1;
    private PlacesAutoCompleteAdapter adapterDestination;
    private PlacesAutoCompleteAdapter adapterDropoff;
    private Marker markerDestination, markerSource;
    private Route route;
    private ArrayList<LatLng> points;
    private PolylineOptions lineOptions;
    private boolean isSource;
    private boolean setup_ = false;
    private Polyline polyLine;
    private View layoutBubble;
    private boolean isAddDestination;
    private Button btnRidenow, btnRidelater;
    // private SeekBar sb;
    private int start;
    private int pointer;
    private LinearLayout layoutMarker;
    private TextView tvNo;
    private LinearLayout layoutDestination;
    private Dialog dialogPaymentOpt, dialogEta, rateCardDialog, rideDialog, rideSuccess;
    private View view;
    private MapView mMapView;
    private Bundle mBundle;
    private TextView tvMaxSize;
    private TextView tvMinFare;
    private TextView tvETA;
    private String estimatedTimeTxt;
    private AutoCompleteTextView etPopupDestination;
    private PlacesAutoCompleteAdapter adapterPopUpDestination;
    private TextView tvGetFareEst;
    private Dialog destinationDialog;
    private ProgressBar pbMinFare;
    private TextView tvLblMinFare;
    private TextView tvTotalFare;
    private LinearLayout layoutHomeText;
    private LinearLayout layoutHomeEdit;
    private LinearLayout layoutWorkText;
    private LinearLayout layoutWorkEdit;
    private AutoCompleteTextView etHomeAddress;
    private AutoCompleteTextView etWorkAddress;
    private PlacesAutoCompleteAdapter adapterHomeAddress;
    private PlacesAutoCompleteAdapter adapterWorkAddress;
    private TextView tvHomeAddress;
    private TextView tvWorkAddress;
    private ListView nearByList;
    private ArrayAdapter<String> nearByAd;
    private Address address;
    private ProgressBar pbNearby;
    private List<ProviderListModel.WalkerList> listDriver = new ArrayList<>();
    private HashMap<Integer, Marker> nearDriverMarker;
    private final int LOCATION_SCHEDULE = 5 * 1000;
    private final int LOCATIONMIN_SCHEDULE = 4 * 1000;
    private ArrayList<ProviderListModel.WalkerList> listUpdatedDriver;
    private Dialog referralDialog;
    private EditText etRefCode;
    private int is_skip = 0;
    private LinearLayout llErrorMsg;
    private LocationHelper locHelper;
    private Location myLocation;
    private TextView tvRateCard;
    boolean selecttypecheck = false;
    int refreecheck = 0;
    String rejectreason = "";
    AlertDialog Displaydialog;
    MaterialCalendarView widget;
    String selecteddate = "", selecteddatenew = "", selectedtime = "", finaltime = "", finaldatesel = "";
    Boolean isSrcMarker = true, isDesMarker = true;
    Boolean IsMapDragable = false;
    double Pickup_Lat, Pickup_Long, DrioIn_Lat, DrioIn_Long;
    ImageView dotted_line_img, downArrowPick, downArrowDrop, downArrowActionBar;
    ImageView etpickclosebtn, etdropclosebtn;
    public static Location static_my_Location;
    boolean isTimevalid = false;
    boolean isServiceStarted = false, isFirstLocationRecived = false;
    String checkselecteddate = "";
    AutoCompleteTextView actionBar_ACTV;
    Boolean flagIsDropAddress = true;
    String promoCode;
    EditText etPromoCode;
    CardView pickAddressView, dropAddressView;
    RelativeLayout pickAddresslayoutView, dropAddresslayoutView;
    View v;
    String TAG = "MAP FRAGMENT";
    static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat formatter2 = new SimpleDateFormat("MM-dd-yyyy");
    final Calendar calendarpick = Calendar.getInstance();

    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendarpick.get(Calendar.HOUR_OF_DAY), calendarpick.get(Calendar.MINUTE), false, false);
    public static final String TIMEPICKER_TAG = "timepicker";

    private ArrayList<AllProviderList> providerlistType;
    private ArrayList<AllProviderList> finalproviderlist;
    ArrayList<Integer> vhecileidlist = new ArrayList<Integer>();
    public TextView ridesuccesscontent, tvcash, tvcard;
    Button ridesuccessbtn;
    String SelectedType;
    Location sourceLocFare, destLocFare;
    private TaxiParseContent taxiparseContent;
    private Handler handlerThread = new Handler();
    private LatLng markerLocation;
    ArrayList<String> resultList = new ArrayList<String>();

    public static UberMapFragment newInstance() {
        UberMapFragment mapFragment = new UberMapFragment();
        return mapFragment;
    }


    LinearLayout f_ubermap_function_icon, lay_rides, bottomrecycle1;
    RelativeLayout f_ubermap_d_P_address;
    ImageView pin_marker, xMark;


    void initHideViewswhenDrag() {
        f_ubermap_function_icon = (LinearLayout) view.findViewById(R.id.f_ubermap_function_icon);
        lay_rides = (LinearLayout) view.findViewById(R.id.lay_rides);
        bottomrecycle1 = (LinearLayout) view.findViewById(R.id.bottomrecycle1);
        f_ubermap_d_P_address = (RelativeLayout) view.findViewById(R.id.f_ubermap_d_P_address);
        pickAddressView = (CardView) view.findViewById(R.id.pickAddressView);
        dropAddressView = (CardView) view.findViewById(R.id.dropAddressView);
        pickAddresslayoutView = (RelativeLayout) view.findViewById(R.id.pickAddresslayoutView);
        dropAddresslayoutView = (RelativeLayout) view.findViewById(R.id.dropAddresslayoutView);

        dotted_line_img = (ImageView) view.findViewById(R.id.dotted_line_img);
        downArrowDrop = (ImageView) view.findViewById(R.id.downArrowOfDropAdd);
        downArrowPick = (ImageView) view.findViewById(R.id.downArrowOfPickAdd);
        downArrowActionBar = (ImageView) view.findViewById(R.id.downArrowActionBar);
        activity.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#364757")));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taxiparseContent = ((MainDrawerActivity) getActivity()).parseContent;
        view = inflater.inflate(R.layout.fragment_map, container, false);
        try {
            if (checkLocationPermission()) {
                MapsInitializer.initialize(getActivity());
            }
        } catch (Exception e) {
        }
        pin_marker = (ImageView) view.findViewById(R.id.myCustomMarker);
        xMark = (ImageView) view.findViewById(R.id.Img_xmark);
        layoutMarker = (LinearLayout) view.findViewById(R.id.layoutMarker);
        layoutBubble = view.findViewById(R.id.layoutBubble);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        tvNo = (TextView) view.findViewById(R.id.tvNo);
        tvcash = (TextView) view.findViewById(R.id.tvcash);
        tvcard = (TextView) view.findViewById(R.id.tvcard);
        view.findViewById(R.id.btnFareInfo).setOnClickListener(this);

        IsMapDragable = false;

        if (CustomerApplication.preferenceHelper.getReferee() == 0) {
            refreecheck = 1;
            showReferralDialog();
        }
        etSource = (AutoCompleteTextView) view.findViewById(R.id.etEnterSouce);

        etDropoff = (AutoCompleteTextView) view.findViewById(R.id.etEnterDrop);

        etpickclosebtn = (ImageView) view.findViewById(R.id.pickupclosebtn);
        etdropclosebtn = (ImageView) view.findViewById(R.id.dropclosebtn);

        etpickclosebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etSource.setText("");
                reset_PickLocation();
            }
        });

        etdropclosebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etDropoff.setText("");
                if (markerDestination != null) {
                    markerDestination.remove();
                }
                markerDestination = null;
                updateProviderOnMap();
            }
        });


        initHideViewswhenDrag();
        layoutDestination = (LinearLayout) view
                .findViewById(R.id.layoutDestination);
        btnRidenow = (Button) view.findViewById(R.id.btnRidenow);
        btnRidelater = (Button) view.findViewById(R.id.btnRidelater);
        btnRidenow.setOnClickListener(this);
        btnRidelater.setOnClickListener(this);

        pickAddresslayoutView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                IsSource_selected();
                return true;
            }
        });
        dropAddresslayoutView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                IsDrop_selected();
                return true;
            }
        });

        etSource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IsSource_selected();
            }
        });
        etDropoff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IsDrop_selected();
            }
        });
        IsSource_selected();
        mapFrameLayout = (FrameLayout) view.findViewById(R.id.mapFrameLayout);
        mapFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE:
                        UberMapFragment.isMapTouched = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        UberMapFragment.isMapTouched = false;
                        break;
                }
                return true;
            }
        });

        btnMyLocation = (ImageButton) view.findViewById(R.id.btnMyLocation);
        mMapView = (MapView) view.findViewById(R.id.map);
        try {
            mMapView.onCreate(mBundle);
            if (checkLocationPermission()) {
                setUpMapIfNeeded();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setUpMapIfNeeded();
        }
        return view;
    }

    private void reset_PickLocation() {
        Pickup_Lat = curretLatLng.latitude;
        Pickup_Long = curretLatLng.longitude;
        updateProviderOnMap();

    }

    private Boolean IsSource_selected() {
        pickAddressView.bringToFront();
        pin_marker.setImageResource(R.drawable.pin_client_org);
        etSource.setEnabled(true);
        etDropoff.setEnabled(false);
        flagIsDropAddress = true;
        dropAddressView.animate().scaleX(0.98f).alpha(0.5f).translationY(-0.9f);
        pickAddressView.animate().scaleX(1.0f).alpha(1.0f).translationY(0.0f);
        pickAddresslayoutView.setVisibility(View.GONE);
        dropAddresslayoutView.setVisibility(View.VISIBLE);
        isDesMarker = true;
        downArrowPick.setVisibility(View.VISIBLE);
        downArrowDrop.setVisibility(View.INVISIBLE);
        activity.actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));
        if (Pickup_Lat != 0.0 && Pickup_Long != 0.0)
            animateCameraToMarker(new LatLng(Pickup_Lat, Pickup_Long), true);
        return true;
    }

    private Boolean IsDrop_selected() {
        dropAddressView.bringToFront();
        pin_marker.setImageResource(R.drawable.destination_pin);
        flagIsDropAddress = false;
        pickAddressView.animate().scaleX(0.98f).alpha(0.5f).translationY(0.9f);
        dropAddressView.animate().scaleX(1.0f).alpha(1.0f).translationY(0.0f);
        activity.actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        etSource.setEnabled(false);
        etDropoff.setEnabled(true);
        isSrcMarker = true;
        downArrowPick.setVisibility(View.INVISIBLE);
        downArrowDrop.setVisibility(View.VISIBLE);
        if (DrioIn_Lat != 0.0 && DrioIn_Long != 0.0)
            animateCameraToMarker(new LatLng(DrioIn_Lat, DrioIn_Long), true);
        dropAddresslayoutView.setVisibility(View.GONE);
        pickAddresslayoutView.setVisibility(View.VISIBLE);
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        Log.i(Const.TAG, "Inside OnCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
        walkerReceiver = new WalkerStatusReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                walkerReceiver, filter);
//        requestQueue = activity.requestQueue;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(Const.TAG, "Inside onActivityCreated");
        Log.i(TAG, "etsource strin" + etSource.getText().toString());

        activity.tvTitle.setVisibility(View.VISIBLE);
        activity.btnSosNotification.setVisibility(View.GONE);
        activity.imgClearDst.setOnClickListener(this);
        adapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        adapterDestination = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        adapterDropoff = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);

        etSource.setAdapter(adapter);
        etDropoff.setAdapter(adapterDropoff);
        Log.i("MAP FRAGMEWNR", "Place Onactivitycreate place set");

        locHelper = new LocationHelper(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                locHelper.setLocationReceivedLister(this);
            }
        } //marsh api check
        else {
            locHelper.setLocationReceivedLister(this);

        }
        etSource.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Log.i("MAP FRAGMEWNR", "Inside source click listener");
                final String selectedDestPlace = adapter.getItem(arg2);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final LatLng latlng = getLocationFromAddress(selectedDestPlace, Const.ServiceCode.GETSOURCE_POSITION);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeMarkerSource(latlng);
                            }
                        });
                    }
                }).start();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                locHelper.onStart();
            }
        } else {
            locHelper.onStart();
        }

        etDropoff.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final String selectedDestPlace = adapterDropoff.getItem(arg2);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final LatLng latlng = getLocationFromAddressDest(selectedDestPlace);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeMarkerDest(latlng);
                            }
                        });
                    }
                }).start();
            }
        });
        providerlistType = new ArrayList<AllProviderList>();
        finalproviderlist = new ArrayList<AllProviderList>();
        listType = new ArrayList<VehicleTypeModel>();
        if (!Firststraddress.equals("")) {
            if (flagIsDropAddress) {
                etSource.setText(Firststraddress);
            } else {
                etDropoff.setText(Firststraddress);
            }
        }
//        getVehicalTypes();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        setUpMapIfNeeded();
                        map.setMyLocationEnabled(true);
                    }

                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.tvTitle.setText(getStr(R.string.app_name));
        activity.btnNotification.setVisibility(View.INVISIBLE);
        etSource.setVisibility(View.VISIBLE);
        mMapView.onResume();
        isServiceStarted = true;
        startCheckingStatusUpdate();
        startUpdatingLocationDuration();
        isFirstLocationRecived = false;
        btnRidelater.setText(getStr(R.string.text_ride_later));
        btnRidenow.setText(getStr(R.string.text_ride_now));

    }

    private void showReferralDialog() {
        referralDialog = new Dialog(getActivity(), R.style.MyDialog);
        referralDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        referralDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        referralDialog.setContentView(R.layout.dialog_referral);
        referralDialog.setCancelable(false);
        etRefCode = (EditText) referralDialog.findViewById(R.id.etRefCode);
        llErrorMsg = (LinearLayout) referralDialog.findViewById(R.id.llErrorMsg);
        referralDialog.findViewById(R.id.btnRefSubmit).setOnClickListener(this);
        referralDialog.findViewById(R.id.btnSkip).setOnClickListener(this);
        referralDialog.show();

    }

    private void applyReffralCode(boolean isShowLoader) {
        if (isShowLoader)
            AndyUtils.showCustomProgressDialog(activity, getStr(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.APPLY_REFFRAL_CODE);
        map.put(Const.Params.REFERRAL_CODE, etRefCode.getText().toString());
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.IS_SKIP, String.valueOf(is_skip));
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.APPLY_REFFRAL_CODE, this, this));

    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            ((MapView) view.findViewById(R.id.map)).getMapAsync(this);
            btnMyLocation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (myLocation != null) {
                        LatLng latLang = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        animateCameraToMarker(latLang, true);
                    }
                }
            });
        }
        if (refreecheck == 0) {
            AndyUtils.showToast(getStr(R.string.change_pickup), R.id.coordinatorLayout, activity);
            if (!Firststraddress.equals("")) {
                if (flagIsDropAddress) {
                    etSource.setText(Firststraddress);
                } else {
                    etDropoff.setText(Firststraddress);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        stopUpdatingLocationDuration();
    }

    @Override
    public void onStop() {
        stopCheckingStatusUpdate();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        map = null;
        isFirstLocationRecived = true;
        isServiceStarted = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        CustomerApplication.preferenceHelper.PutPickLat(0.0);
        CustomerApplication.preferenceHelper.PutPickLong(0.0);
        CustomerApplication.preferenceHelper.PutDropLat(0.0);
        CustomerApplication.preferenceHelper.PutDropLong(0.0);
        mMapView.onDestroy();
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(walkerReceiver);
        etSource.setText("");
        etDropoff.setText("");
        isFirstLocationRecived = true;
        isServiceStarted = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRidenow:
                if (isValidate()) {
                    if (nearDriverMarker != null && nearDriverMarker.size() > 0)
                        showPaymentOptionDialog();
                    else
                        AndyUtils.showToast(getStr(R.string.no_provider_found) + " " + (SelectedType.length() == 0 ? "Type" : SelectedType), R.id.coordinatorLayout,
                                getActivity());
                }
                break;
            case R.id.btnRidelater:
                if (isValidate()) {
                    showRideLaterDialog();
                }
                break;
            case R.id.ivCredit:
                ivCredit.setSelected(true);
                ivCash.setSelected(false);
                paymentMode = 0;
                CustomerApplication.preferenceHelper.putPaymentMode(paymentMode);
                tvcash.setTextColor(getColor(R.color.gray));
                tvcard.setTextColor(getColor(R.color.colorPrimary));
                break;
            case R.id.ivCash:
                ivCredit.setSelected(false);
                ivCash.setSelected(true);
                paymentMode = 1;
                CustomerApplication.preferenceHelper.putPaymentMode(paymentMode);
                tvcash.setTextColor(getColor(R.color.colorPrimary));
                tvcard.setTextColor(getColor(R.color.gray));
                break;
            case R.id.btnSendRidelater:
                if (isTimevalid) {
                    finaldatesel = selecteddate;
                    finaltime = selectedtime;
                    if (finaldatesel.trim().equalsIgnoreCase("") && finaltime.trim().equalsIgnoreCase("")) {
                        AndyUtils.showToast(getStr(R.string.select_date_time), R.id.coordinatorLayout, activity);
                    } else {
                        rideDialog.dismiss();
                        pickMeRideLater();
                    }
                } else {
                    Toast.makeText(activity, getStr(R.string.text_chk_time), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnCancelRidelater:
                rideDialog.dismiss();
                break;
            case R.id.btnSendRequest:
                dialogPaymentOpt.dismiss();
//                pickMeUp();
                checkpromoCode();
                break;
            case R.id.btnCancelRequest:
                dialogPaymentOpt.dismiss();
                break;
            case R.id.imgClearDst:
                etSource.setText("");
                break;
            case R.id.btnFareInfo:
                showVehicleDetails();
                break;
            case R.id.tvGetFareEst:
                showDestinationPopup();
                break;
            case R.id.btnEditHome:
                layoutHomeEdit.setVisibility(LinearLayout.VISIBLE);
                layoutHomeText.setVisibility(LinearLayout.GONE);
                break;
            case R.id.btnEditWork:
                layoutWorkEdit.setVisibility(LinearLayout.VISIBLE);
                layoutWorkText.setVisibility(LinearLayout.GONE);
                break;
            case R.id.layoutHomeText:
                if (CustomerApplication.preferenceHelper.getHomeAddress() != null)
                    sendQuoteRequest(CustomerApplication.preferenceHelper.getHomeAddress());
                break;
            case R.id.layoutWorkText:
                if (CustomerApplication.preferenceHelper.getWorkAddress() != null)
                    sendQuoteRequest(CustomerApplication.preferenceHelper.getWorkAddress());
                break;
            case R.id.imgClearDest:
                etPopupDestination.setText("");
                break;
            case R.id.imgClearHome:
                etHomeAddress.setText("");
                break;
            case R.id.imgClearWork:
                etWorkAddress.setText("");
                break;
            case R.id.btnRefSubmit:
                if (etRefCode.getText().length() == 0) {
                    AndyUtils.showToast(getStr(R.string.text_blank_ref_code), R.id.coordinatorLayout, activity);
                    return;
                } else {
                    if (!AndyUtils.isNetworkAvailable(activity)) {
                        AndyUtils.showToast(getStr(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activity);
                        return;
                    }
                    is_skip = 0;
                    applyReffralCode(true);
                }
                break;
            case R.id.btnSkip:
                is_skip = 1;
                applyReffralCode(false);
                this.OnBackPressed();
                break;
            default:
                break;
        }
    }

    private void getAddressFromLocation(final LatLng latlng, final EditText et) {
        et.setHint(getStr(R.string.text_waiting_for_address));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder gCoder = new Geocoder(getActivity());
                try {
                    Log.e(TAG, "Inside Map Addrss Run loop1");
                    final List<Address> list = gCoder.getFromLocation(
                            latlng.latitude, latlng.longitude, 1);

                    if (flagIsDropAddress) {
                        Pickup_Lat = latlng.latitude;
                        Pickup_Long = latlng.longitude;
                        //  setMarker(new LatLng(Pickup_Lat,Pickup_Long), flagIsDropAddress);
                        isSrcMarker = false;

                    } else {
                        DrioIn_Lat = latlng.latitude;
                        DrioIn_Long = latlng.longitude;
                        //   setMarker(new LatLng(DrioIn_Lat,DrioIn_Long), flagIsDropAddress);
                        isDesMarker = false;
                    }

                    if (list != null && list.size() > 0) {
                        address = list.get(0);
                        StringBuilder sb = new StringBuilder();
                        if (address.getAddressLine(0) != null) {
                            if (address.getMaxAddressLineIndex() > 0) {
                                for (int i = 0; i < address
                                        .getMaxAddressLineIndex(); i++) {
                                    sb.append(address.getAddressLine(i))
                                            .append("\n");
                                }
                                sb.append(",");
                                sb.append(address.getCountryName());
                            } else {
                                sb.append(address.getAddressLine(0));
                            }
                        }

                        strAddress = sb.toString();
                        strAddress = strAddress.replace(",null", "");
                        strAddress = strAddress.replace("null", "");
                        strAddress = strAddress.replace("Unnamed", "");
                    }
                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(strAddress)) {
                                Firststraddress = strAddress;
                                et.setFocusable(false);
                                et.setFocusableInTouchMode(false);
                                et.setText(strAddress);

                                if (actionBar_ACTV != null) actionBar_ACTV.setText(strAddress);

                                et.setTextColor(getColor(
                                        color.black));
                                et.setFocusable(true);
                                et.setFocusableInTouchMode(true);
                            } else {
                                et.setText("");
                                et.setTextColor(getColor(
                                        color.black));
                            }

                            et.setEnabled(true);

                            if (IsMapDragable) {
                                if (etSource.isEnabled()) {
                                    setMarker(new LatLng(Pickup_Lat, Pickup_Long), true);
                                }
                                if (etDropoff.isEnabled()) {
                                    setMarker(new LatLng(DrioIn_Lat, DrioIn_Long), false);
                                }
                            }


                        }
                    });
                } catch (Exception exc) {
                    exc.printStackTrace();
                    getLocationInfo(latlng, et);
                }
            }
        }).start();
    }

    private void animateCameraToMarker(LatLng latLng, boolean isAnimate) {
        try {
            etSource.setFocusable(false);
            etSource.setFocusableInTouchMode(false);
            etDropoff.setFocusable(false);
            etDropoff.setFocusableInTouchMode(false);
            CameraUpdate cameraUpdate = null;
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);

            if (cameraUpdate != null && map != null) {
                if (isAnimate) {
                    map.animateCamera(cameraUpdate);
                    layoutMarker.setVisibility(View.INVISIBLE);
                } else
                    map.moveCamera(cameraUpdate);
            }

            etSource.setFocusable(true);
            etSource.setFocusableInTouchMode(true);
            etDropoff.setFocusable(true);
            etDropoff.setFocusableInTouchMode(true);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final Location toLocation, final boolean hideMarker) {
        if (map == null || !this.isVisible() || marker == null
                || marker.getPosition() == null) {
            return;
        }
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final double startRotation = marker.getRotation();
        final long duration = 100;

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

    private LatLng getLocationFromAddress(final String place, final int serviceCode) {
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(getActivity());
        try {
            final List<Address> list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            AppLog.handleException(TAG, e);
            getDestAddressFromGoogle(place, serviceCode);

        }
        return loc;
    }

    private LatLng getLocationFromAddressDest(final String place) {
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
            getDestPosition(place);
        }
        return loc;
    }

    public void getDestAddressFromGoogle(String place, int serviceCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        String url = "https://maps.google.com/maps/api/geocode/json?address=" + place + "&sensor=false&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY;
        map.put(Const.URL, url.replaceAll(" ", "%20"));
        requestQueue.add(new VolleyHttpRequest(Method.GET, map, serviceCode, this, this));
    }

    private void getDestPosition(String place) {
        Looper.prepare();
        AndyUtils.showCustomProgressDialog(activity, activity.getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        place = place.replaceAll(" ", "%20");
        map.put(Const.URL, "https://maps.google.com/maps/api/geocode/json?address=" + place + "&sensor=false&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map, Const.ServiceCode.GETDEST_POSITION, this, this));
    }

    @Override
    public void onProgressCancel() {
        cancleRequest();
    }

    @Override
    protected boolean isValidate() {
        String msg = null;
        if (curretLatLng == null) {
            msg = getStr(R.string.text_location_not_found);
        } else if (selectedPostion == -1) {
            msg = getStr(R.string.text_select_type);

        } else if (TextUtils.isEmpty(etSource.getText().toString())
                || etSource.getText().toString()
                .equalsIgnoreCase(getStr(R.string.text_wait_addr))) {
            msg = getStr(R.string.text_waiting_for_address);
        } else if (!selecttypecheck) {
            msg = getStr(R.string.text_select_type);
        }
        if (msg == null)
            return true;
        AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
        return false;
    }

    private void pickMeUp() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getStr(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        } else if (CustomerApplication.preferenceHelper.getDefaultCard() == 0
                && paymentMode == Const.CREDIT) {
            AndyUtils.showToast(getStr(R.string.no_card), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressRequestDialog(activity, getStr(R.string.text_creating_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.URL, Const.ServiceType.CREATE_REQUEST);
        map.put(Const.Params.TOKEN,
                CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        if (markerSource != null) {
            if (!(curretLatLng.longitude == 0.0) && !(curretLatLng.latitude == 0.0))
                if (curretLatLng != null) {
                    if (markerSource.getPosition().latitude == 0.0 && markerSource.getPosition().longitude == 0.0)
                        markerSource.setPosition(new LatLng(curretLatLng.latitude, curretLatLng.longitude));
                }
            LatLng dest = markerSource.getPosition();
            map.put(Const.Params.LATITUDE, String.valueOf(dest.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(dest.longitude));
            CustomerApplication.preferenceHelper.Putpickuplat(String.valueOf(dest.latitude));
            CustomerApplication.preferenceHelper.PutpickupLng(String.valueOf(dest.longitude));

        }
//        map.put(Const.Params.LATITUDE, String.valueOf(markerLocation.latitude));
//        map.put(Const.Params.LONGITUDE, String.valueOf(markerLocation.longitude));
        map.put(Const.Params.PAYMENT_OPT,
                String.valueOf(CustomerApplication.preferenceHelper.getPaymentMode()));
        map.put(Const.Params.TYPE,
                String.valueOf(listType.get(selectedPostion).id));
        map.put(Const.Params.CARD_ID,
                String.valueOf(CustomerApplication.preferenceHelper.getDefaultCard()));
        map.put(Const.Params.DISTANCE, "1");

        map.put(Const.Params.PICKUP_ADDRESS,
                etSource.getText().toString());
        map.put(Const.Params.DROP_ADDRESS, etDropoff.getText().toString().length() > 0 ? etDropoff.getText().toString() : "");

        if (etDropoff.getText().toString().trim().length() > 0) {
            if (markerDestination != null) {
                final LatLng dest = markerDestination.getPosition();
                map.put(Const.Params.DESTINATION_LATITUDE1, String.valueOf(dest.latitude));
                map.put(Const.Params.DESTINATION_LONGITUDE1,
                        String.valueOf(dest.longitude));
            }
        }
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.CREATE_REQUEST, this, this));
    }

    private void checkpromoCode() {
        promoCode = etPromoCode.getText().toString().trim();
        if (!promoCode.matches("")) {
            applyPromoCode(promoCode);
        } else {
            pickMeUp();
        }


    }

//    private void showPromoDialog() {
//        if (dialog != null) {
//            if (!dialog.isShowing())
//                if (!activity.isFinishing())
//                    dialog.show();
//            return;
//        }
//        dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_promo);
//        ivPromoResult = (ImageView) dialog.findViewById(R.id.ivPromoResult);
//        tvPromoResult = (TextView) dialog.findViewById(R.id.tvPromoResult);
//
//        llErrorMsg = (LinearLayout) dialog.findViewById(R.id.llErrorMsg);
//        btnPromoSubmit = (Button) dialog.findViewById(R.id.btnPromoSubmit);
//        btnPromoSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                applyPromoCode();
//            }
//        });
//        btnPromoSkip = (Button) dialog.findViewById(R.id.btnPromoSkip);
//        btnPromoSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        if (!TextUtils.isEmpty(CustomerApplication.preferenceHelper.getPromoCode())) {
//            etPromoCode.setText(CustomerApplication.preferenceHelper.getPromoCode());
//            btnPromoSkip.setText(activity.getString(R.string.text_done));
//            btnPromoSubmit.setEnabled(false);
//            etPromoCode.setEnabled(false);
//        }
//        if (!activity.isFinishing())
//            dialog.show();
//    }


    private void pickMeRideLater() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getStr(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressRequestDialog(activity,
                getStr(R.string.text_creating_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CREATE_RIDELATERREQUEST);
//        map.put(Const.Params.USERMOBILENO, CustomerApplication.preferenceHelper.getPhoneNo());
        map.put(Const.Params.USERID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.PICKUPLAT, Pickup_Lat != 0.00 ? "" + Pickup_Lat : String.valueOf(curretLatLng.latitude));
        map.put(Const.Params.PICKUPLNG, Pickup_Long != 0.00 ? "" + Pickup_Long : String.valueOf(curretLatLng.longitude));
        map.put(Const.Params.NOOFADULTS, "");
        map.put(Const.Params.NOOFCHILD, "");
        map.put(Const.Params.LUGGAGECOUNT, "");
        map.put(Const.Params.RIDECOMMENT, "");
        map.put(Const.Params.TYPEOFCAR, String.valueOf(listType.get(selectedPostion).id));
        map.put(Const.Params.PICKUPLOC, etSource.getText().toString());
        map.put(Const.Params.PICKUPDET, "");
        map.put(Const.Params.DROPOFFLOC_RIDE_LATER, etDropoff.getText().toString());
        map.put(Const.Params.DROPOFFDET, "");
        map.put(Const.Params.SCHEDULETIME, selecteddate + " " + selectedtime + ":00");
        map.put(Const.Params.USER_TIMEZONE, "+05:30");
//        *****************************
        TimeZone mTimeZone = Calendar.getInstance().getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        if (markerDestination != null) {
            final LatLng dest = markerDestination.getPosition();

            map.put(Const.Params.DROPOFFLAT, String.valueOf(dest.latitude));
            map.put(Const.Params.DROPOFFLNG,
                    String.valueOf(dest.longitude));
        }
        CustomerApplication.preferenceHelper.Putpickuplat(String.valueOf(curretLatLng.latitude));
        CustomerApplication.preferenceHelper.PutpickupLng(String.valueOf(curretLatLng.longitude));
        Log.i("MAINDRAWER", "Inside PickMeRideLater");

        requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.CREATE_RIDELATERREQUEST, this, this));
    }


    private void showRideLaterSuccessDialog() {
        Log.i("MAINDRAWER", "Final Select Date" + finaldatesel + "Final Time" + finaltime);
        rideSuccess = new Dialog(getActivity());
        rideSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rideSuccess.setContentView(R.layout.ridesuccess_dialog);
        String ridedate = finaldatesel;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(ridedate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("dd-MMM-yyyy");
        System.out.println(sdf.format(date));
        String displaydateval = sdf.format(date);
        ridesuccesscontent = (TextView) rideSuccess.findViewById(R.id.ridesuccesscontent);
        ridesuccesscontent.setText(getStr(R.string.success_content1) + displaydateval + " " + finaltime + " Hrs" + getStr(R.string.success_content2));
        ridesuccessbtn = (Button) rideSuccess.findViewById(R.id.btnRideSuccessok);
        ridesuccessbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rideSuccess.dismiss();
            }
        });
        rideSuccess.show();
        selecteddate = "";
        selectedtime = "";
    }


    public void showPaymentOptionDialog() {
        if (dialogPaymentOpt != null) {
            if (!dialogPaymentOpt.isShowing())
                dialogPaymentOpt.show();
            return;
        }
        dialogPaymentOpt = new Dialog(getActivity());
        dialogPaymentOpt.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPaymentOpt.setContentView(R.layout.payment_option);
        dialogPaymentOpt.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialogPaymentOpt.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

        ivCash = (ImageView) dialogPaymentOpt.findViewById(R.id.ivCash);
        ivCredit = (ImageView) dialogPaymentOpt.findViewById(R.id.ivCredit);
        etPromoCode = (EditText) dialogPaymentOpt.findViewById(R.id.etPromo);
        ivCash.setOnClickListener(this);
        ivCredit.setOnClickListener(this);
        tvcash = (TextView) dialogPaymentOpt.findViewById(R.id.tvcash);
        tvcard = (TextView) dialogPaymentOpt.findViewById(R.id.tvcard);
        dialogPaymentOpt.findViewById(R.id.btnCancelRequest).setOnClickListener(this);
        dialogPaymentOpt.findViewById(R.id.btnSendRequest).setOnClickListener(this);

        paymentMode = (int) CustomerApplication.preferenceHelper
                .getPaymentMode();
        if (paymentMode == 1) {
            ivCredit.setSelected(false);
            ivCash.setSelected(true);
            tvcash.setTextColor(getColor(R.color.colorPrimary));
            tvcard.setTextColor(getColor(R.color.gray));
        } else {
            ivCredit.setSelected(true);
            ivCash.setSelected(false);
            tvcash.setTextColor(getColor(R.color.gray));
            tvcard.setTextColor(getColor(R.color.colorPrimary));
        }
        dialogPaymentOpt.show();
    }


    private void showRideLaterDialog() {
        if (rideDialog != null) {
            if (!rideDialog.isShowing())
                rideDialog.show();
            return;
        }
        rideDialog = new Dialog(getActivity());
        rideDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rideDialog.setContentView(R.layout.ridelater_dialog);
        rideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ivDatepick = (ImageButton) rideDialog.findViewById(R.id.ib_date_picker);
        ivTimepick = (ImageButton) rideDialog.findViewById(R.id.ib_time_picker);
        ivdatetextView = (TextView) rideDialog.findViewById(R.id.txtselectDate);
        ivtimetextView = (TextView) rideDialog.findViewById(R.id.txtselectTime);
        ivDatepick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Shownewdialog();
            }
        });
        ivTimepick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (timePickerDialog != null){
                if (timePickerDialog != null) {
                    if (!timePickerDialog.isVisible())
                        timePickerDialog.show(activity.getSupportFragmentManager(), TIMEPICKER_TAG);
                }
            }
        });
        rideDialog.findViewById(R.id.btnCancelRidelater).setOnClickListener(this);
        rideDialog.findViewById(R.id.btnSendRidelater).setOnClickListener(this);
        rideDialog.show();
    }


    private void Shownewdialog() {
        if (Displaydialog != null) {
            if (!Displaydialog.isShowing())
                Displaydialog.show();
            return;
        }
        Displaydialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View convertView2 = (View) inflater.inflate(R.layout.dialog_basic, null);
        widget = (MaterialCalendarView) convertView2.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        widget.setOnDateChangedListener(this);
        widget.setMinimumDate(cal.getTime());
        Displaydialog.setView(convertView2);
        Displaydialog.show();
        Displaydialog.setCanceledOnTouchOutside(false);

    }


    @Override
    public void onTaskCompleted(final String response, int serviceCode) {
        super.onTaskCompleted(response, serviceCode);
        switch (serviceCode) {
            case Const.ServiceCode.CREATE_REQUEST:
                AppLog.Log(Const.TAG, "Create Request Response::::" + response);
                AndyUtils.removeCustomProgressRequestDialog();
                RideNowSuccess rideNowSuccess = taxiparseContent.getSingleObject(response, RideNowSuccess.class);
                if (rideNowSuccess.success) {
                    CustomerApplication.preferenceHelper.putRequestId(rideNowSuccess.requestId);
                    AndyUtils.showCustomProgressDialog(activity, getStr(R.string.text_contacting), false, this, rideNowSuccess.walker);
                    startCheckingStatusUpdate();
                    stopUpdatingLocationDuration();
                } else {
                    AndyUtils.showToast(rideNowSuccess.error, R.id.coordinatorLayout, activity);
                }
                break;
            case Const.ServiceCode.CREATE_RIDELATERREQUEST:
                AppLog.Log(Const.TAG, "Create Ride Later  Response::::" + response);
                AndyUtils.removeCustomProgressRequestDialog();
                if (taxiparseContent.isSuccessRequestRideLater(response)) {
                    showRideLaterSuccessDialog();
                    AndyUtils.showToast(getStr(R.string.schedule_status), R.id.coordinatorLayout, activity);
                }
                break;
            case Const.ServiceCode.GET_REQUEST_STATUS:
                AppLog.Log(Const.TAG, "Get Request Response::::" + response);
                RequestStatusModel requestStatusModel = taxiparseContent.getSingleObject(response, RequestStatusModel.class);
                if (requestStatusModel != null)
                    if (requestStatusModel.success) {
                        switch (taxiparseContent.checkRequestStatus(requestStatusModel)) {
                            case 11:
                            case Const.IS_WALK_STARTED:
                            case Const.IS_WALKER_ARRIVED:
                            case Const.IS_COMPLETED:
                            case Const.IS_WALKER_STARTED:
                                AndyUtils.removeCustomProgressRequestDialog();
                                stopCheckingStatusUpdate();
                                if (this.isVisible())
                                    activity.gotoTripFragment(requestStatusModel);
                                break;
                            case Const.IS_WALKER_RATED:
                                stopCheckingStatusUpdate();
                                if (this.isVisible())
                                    activity.gotoRateFragment(taxiparseContent.getDriverDetail(response));
                                break;
                            case Const.IS_REQEUST_CREATED:
                                if (CustomerApplication.preferenceHelper.getRequestId() != Const.NO_REQUEST) {
                                    AndyUtils.showCustomProgressDialog(activity, getStr(R.string.text_contacting), false, this);
                                    RequestStatusModel requestStatusModel1 = taxiparseContent.getDriverDetail(response);
                                    AndyUtils.updateDriverDetailsProgress(activity, requestStatusModel1 != null ? requestStatusModel1.walker : null);
                                }
                                isContinueRequest = true;
                                break;
                            case Const.NO_REQUEST:
                                isContinueRequest = false;
                                if (!isGettingVehicalType) {
                                    AndyUtils.removeCustomProgressDialog();
//                                startUpdateProvidersLocation();
//                                startUpdateVehTypeLocation();@required
                                }
                                stopCheckingStatusUpdate();
                                startUpdatingLocationDuration();
                                break;
                            default:
                                isContinueRequest = false;
                                break;
                        }

                    } else if (requestStatusModel.error_code == Const.REQUEST_ID_NOT_FOUND || requestStatusModel.error_code == Const.REQUEST_ID_NOT_FOUND2) {
                        AndyUtils.removeCustomProgressDialog();
                        CustomerApplication.preferenceHelper.clearRequestData();
                        isContinueRequest = false;
                    } else if (requestStatusModel.error_code == Const.INVALID_TOKEN) {
                        activity.clearSessionLogout();
                        /*if (CustomerApplication.preferenceHelper.getLoginBy().equalsIgnoreCase(Const.MANUAL))
                            login();
                        else
                            loginSocial(CustomerApplication.preferenceHelper.getUserId(),
                                    CustomerApplication.preferenceHelper.getLoginBy());*/
                    } else if (requestStatusModel.error_code == 483) {
                        AndyUtils.removeCustomProgressDialog();
                        AndyUtils.showToast(getStr(R.string.error_483), R.id.coordinatorLayout, activity);
//                    CustomerApplication.preferenceHelper.putRequestId(Const.NO_REQUEST);
                        CustomerApplication.preferenceHelper.clearRequestData();
                    } else {
                        isContinueRequest = true;
                    }
                break;
            case Const.ServiceCode.CANCEL_REQUEST:
                SuccessModel successModel = taxiparseContent.getSingleObject(response, SuccessModel.class);
                if (successModel != null) {
                    if (successModel.success) {
                        CustomerApplication.preferenceHelper.clearRequestData();
                    } else {
                        AndyUtils.showToast(successModel.error, R.id.coordinatorLayout, activity);
                    }
                } else {
                    AndyUtils.showToast(getStr(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
                AndyUtils.removeCustomProgressRequestDialog();
                break;
            case Const.ServiceCode.GET_VEHICAL_TYPES:
                VehicleTypeSuccess vehicleTypeSuccess = taxiparseContent.getSingleObject(response, VehicleTypeSuccess.class);
                AppLog.Log(Const.TAG, "Get Request Response::::" + response);
                if (vehicleTypeSuccess != null) {
                    if (vehicleTypeSuccess.success) {
                        listType.clear();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listType = vehicleTypeSuccess.types;
                        pointer = listType.size();
                        isGettingVehicalType = false;
                        if (listType.size() > 0) {
                            if (listType != null && listType.get(0) != null)
                                recycleOnItemClick = new RecycleOnItemClick() {
                                    @Override
                                    public void onItemClick(View view, int position) {
//                                    getAllProviders(curretLatLng);@required
                                        selectedPostion = position;
                                        selecttypecheck = true;
                                        for (int i = 0; i < listType.size(); i++)
                                            listType.get(i).isSelected = false;
                                        listType.get(position).isSelected = true;
                                        SelectedType = listType.get(position).name;
                                        mAdapter.notifyDataSetChanged();
                                    }
                                };

                            mAdapter = new VehicalTypeNewAdapter(activity, listType, recycleOnItemClick);
                            mRecyclerView.setAdapter(mAdapter);
                            if (curretLatLng != null) {
//                            getProviderDuration();  @required
                            }
                            setup_ = true;
//                        mAdapter.notifyDataSetChanged();
//                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    } else {
                        if (setup_) {
                            listType.clear();
                            mRecyclerView.setVisibility(View.GONE);
                            AndyUtils.showToast(vehicleTypeSuccess.error, R.id.coordinatorLayout, activity);
                        }
                    }
                }
                AndyUtils.removeCustomProgressDialog();
                break;
            case Const.ServiceCode.GET_PROVIDERS:
                try {
                    map.getUiSettings().setScrollGesturesEnabled(true);
                    AppLog.Log("", "Provider Response : " + response);
                    final ProviderListModel providerListModel = taxiparseContent.getSingleObject(response, ProviderListModel.class);
                    if (providerListModel.Success) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listDriver = new ArrayList<>();
                                listDriver = providerListModel.mWalkerList;
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
                                setProvirderOnMap();
//                                    }
//                                });
                            }
                        }).start();
                    } else {
                        map.clear();
                        Log.i(TAG, "Inside Get Provider Else");
                        if (!TextUtils.isEmpty(etDropoff.getText().toString())) {
                            if (markerDestination != null) {
                                Log.i(TAG, "Inside setprovider destination check marker");
                                final LatLng dest = markerDestination.getPosition();
                                MarkerOptions opt = new MarkerOptions();
                                opt.position(dest);
                                opt.title(getStr(R.string.text_destination_pin_title));
                                opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                                markerDestination = map.addMarker(opt);
                                markerDestination.setDraggable(true);
                                markerDestination.setVisible(true);
                            }
                        }
                    }
                } catch (Exception e) {
                }
                break;
            case Const.ServiceCode.APPLY_PROMO:
                AndyUtils.removeCustomProgressDialog();
                ApplyPromoModel applyPromoModel = activity.parseContent.getSingleObject(response, ApplyPromoModel.class);
                if (applyPromoModel.success) {
                    CustomerApplication.preferenceHelper.putPromoCode(etPromoCode.getText().toString());
                    AndyUtils.showToast(getStr(R.string.text_promo_code_success), R.id.coordinatorLayout, activity);
                    pickMeUp();
                } else {
                    AndyUtils.showToast(getStr(R.string.text_promo_code_invalid), R.id.coordinatorLayout, activity);
//                    showPaymentOptionDialog();
                }
                break;
            case Const.ServiceCode.GET_DURATION:
                if (!TextUtils.isEmpty(response)) {
                    estimatedTimeTxt = activity.parseContent.parseNearestDriverDurationString(response);
                }
                break;
            case Const.ServiceCode.GET_FARE_QUOTE:
                if (!TextUtils.isEmpty(response)) {
                    try {
/*                        JSONArray jsonArray = new JSONObject(response).getJSONArray("routes");
                        JSONArray jArrSub = jsonArray.getJSONObject(0).getJSONArray("legs");
                        JSONObject legObj = jArrSub.getJSONObject(0);
                        JSONObject durationObj = legObj.getJSONObject("duration");
                        JSONObject distanceObj = legObj.getJSONObject("distance");
                        double minute = durationObj.getDouble("value") / 60;
                        double kms = distanceObj.getDouble("value") / 1000;
                        pbMinFare.setVisibility(View.GONE);
                        tvTotalFare.setVisibility(View.VISIBLE);
                        tvTotalFare.setText(getStr(R.string.payment_unit)
                                + getFareCalculation(kms));
                        DecimalFormat d = new DecimalFormat("0.00");
                        tvETA.setText(d.format(minute) + " " + getStr(R.string.text_mins));*/
                        ETA_calc_Model eta_calc_model = taxiparseContent.getSingleObject(response, ETA_calc_Model.class);
                        AppLog.Log("ETA response", "ETA Calc  : " + response);
                        if (eta_calc_model != null) {
                            if (!eta_calc_model.success) {
                                AndyUtils.showToast(eta_calc_model.error, R.id.coordinatorLayout, activity);
                                return;
                            }
                            pbMinFare.setVisibility(View.GONE);
                            tvTotalFare.setVisibility(View.VISIBLE);
                            tvTotalFare.setText(activity.getString(R.string.payment_unit) + " " + eta_calc_model.cal_total);
                            tvETA.setText(eta_calc_model.total_duration + " " + activity.getString(R.string.text_min));
                        }
                    } catch (Exception e) {
                        AppLog.Log("UberMapFragment=====",
                                "GET_FARE_QUOTE Response: " + e);
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.GET_NEAR_BY:
                AppLog.Log("TAG", "Near by : " + response);
                pbNearby.setVisibility(View.GONE);
                nearByList.setVisibility(View.VISIBLE);
                taxiparseContent.parseNearByPlaces(response, resultList);
                nearByAd = new ArrayAdapter<String>(activity, R.layout.autocomplete_list_text, R.id.tvPlace, resultList);
                nearByList.setAdapter(nearByAd);
                break;
            case Const.ServiceCode.UPDATE_PROVIDERS:
                AppLog.Log("Mapfragment", "UPDATE_PROVIDERS : " + response);
                try {
                    final ProviderListModel providerListModel = taxiparseContent.getSingleObject(response, ProviderListModel.class);
                    if (providerListModel.Success) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listUpdatedDriver = new ArrayList<>();
                                listDriver = providerListModel.mWalkerList;
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
                                updateProviderOnMap();
//                                    }
//                                });
                            }
                        }).start();
                    } else {
                        Log.i(TAG, "Update Provider Else");
                        map.clear();
                        if (!TextUtils.isEmpty(etDropoff.getText().toString())) {
                            if (markerDestination != null) {
                                Log.i(TAG, "Inside setprovider destination check marker");
                                final LatLng dest = markerDestination.getPosition();
                                MarkerOptions opt = new MarkerOptions();
                                opt.position(dest);
                                opt.title(getStr(
                                        R.string.text_destination_pin_title));
                                opt.icon(BitmapDescriptorFactory
                                        .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                                markerDestination = map.addMarker(opt);
                                markerDestination.setDraggable(true);
                                markerDestination.setVisible(true);
                            }
                        }
                    }
                } catch (Exception e) {
                }
                break;
            case Const.ServiceCode.APPLY_REFFRAL_CODE:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(Const.TAG, "Referral Response: " + response);
                if (taxiparseContent.isSuccess(response)) {
                    CustomerApplication.preferenceHelper.putReferee(1);
                    referralDialog.dismiss();
                } else {
                    llErrorMsg.setVisibility(View.VISIBLE);
                    etRefCode.requestFocus();
                }
                break;


            case Const.ServiceCode.LATEST_GET_PROVIDERS:
                AppLog.Log("TAG", "LatestGETALLProvider by : " + response);
                LatestProvidesListModel providersModel = taxiparseContent.getSingleObject(response, LatestProvidesListModel.class);
                if (providersModel != null)
                    if (providersModel.success) {
                        setLatestProviderOnMap(providersModel);
                    } else {
                        if (map != null) {
                            map.clear();
                            if (mAdapter != null)
                                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                    mAdapter.UpdateETA((VehicalTypeNewAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(i), "-:-");
                                }
                        }
                        if (providersModel.error_code == 622) {
                            /*login();*/
                            activity.clearSessionLogout();
                        }
                    }
                break;
            case Const.ServiceCode.GETSOURCE_POSITION:
                try {
                    AndyUtils.removeCustomProgressDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    final Double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    final Double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    changeMarkerSource(new LatLng(latitude, longitute));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GET_FARE_EST:
                AppLog.Log(Const.TAG, response);
                try {
                    AndyUtils.removeCustomProgressDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    final Double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    final Double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    getAddressFromLocation(new LatLng(latitude, longitute), etPopupDestination);
                    sendQuoteRequest(jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GETDEST:
                try {
                    AppLog.Log(TAG, response);
                    JSONObject jsonObject = new JSONObject(response);

                    Double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    Double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    destLocFare = new Location("");
                    destLocFare.setLatitude(latitude);
                    destLocFare.setLongitude(longitute);
                    getFareQuote(new LatLng(sourceLocFare.getLatitude(), sourceLocFare.getLongitude()),
                            new LatLng(destLocFare.getLatitude(), destLocFare.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GETDEST_POSITION:
                AppLog.Log(Const.TAG, response);
                try {
                    AndyUtils.removeCustomProgressDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    final Double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    final Double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    changeMarkerDest(new LatLng(latitude, longitute));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setLatestProviderOnMap(LatestProvidesListModel providersModel) {
        HashMap<Integer, LatLng> driverList = new HashMap<>();
        List<Integer> driverKey = new ArrayList<>();
        if (providersModel != null) {
            if (selectedPostion >= 0) {
                if (providersModel.walkers != null)
                    if (providersModel.walkers.get(selectedPostion) != null)
                        if (providersModel.walkers.get(selectedPostion).drivers != null)
                            for (LatestProvidesListModel.Driver driver : providersModel.walkers.get(selectedPostion).drivers) {
                                driverList.put(Integer.parseInt(driver.id + ""), new LatLng(driver.current.latitude, driver.current.longitude));
                                driverKey.add(Integer.parseInt(driver.id + ""));
                            }
            } else {
                for (LatestProvidesListModel.Walker walker : providersModel.walkers) {
                    if (walker.drivers != null)
                        if (walker.drivers.size() > 0)
                            for (LatestProvidesListModel.Driver driver : walker.drivers) {
                                driverList.put(Integer.parseInt(driver.id + ""), new LatLng(driver.current.latitude, driver.current.longitude));
                                driverKey.add(Integer.parseInt(driver.id + ""));
                            }
                }
            }
            int count = 0;
            for (LatestProvidesListModel.Walker walker : providersModel.walkers) {
                if (walker.drivers != null && mAdapter != null)
                    if (walker.drivers.size() != 0) {
                        boolean isAvailable = false;
                        LatestProvidesListModel.Driver driverone = null;
                        for (LatestProvidesListModel.Driver driver : walker.drivers) {
                            if (driver.is_near) {
                                isAvailable = true;
                                driverone = driver;
                                break;
                            }
                        }
                        if (mAdapter != null) {
                            if (isAvailable && driverone != null) {
                                mAdapter.UpdateETA((VehicalTypeNewAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(count), driverone.duration);
                            } else {
                                mAdapter.UpdateETA((VehicalTypeNewAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(count), "-:-");
                                driverone = null;
//                            break;
                            }
                        }
                    } else {
                        mAdapter.UpdateETA((VehicalTypeNewAdapter.ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(count), "-:-");
                    }
                count++;
            }


        }

        if (map != null)

        {
            map.clear();
            nearDriverMarker = new HashMap<Integer, Marker>();
            for (int i = 0; i < driverKey.size(); i++) {
                LatLng driverLatlong = driverList.get(driverKey.get(i));
                MarkerOptions mo = new MarkerOptions();
                mo.flat(true);
                mo.anchor(0.5f, 0.5f);
                mo.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)));
                mo.title(getStr(R.string.text_drive_location));
                mo.position(new LatLng(driverLatlong.latitude, driverLatlong.longitude));
                // mo.rotation((float) driverLatlong.); //Bearing
                nearDriverMarker.put(driverKey.get(i), map.addMarker(mo));
            }
            if (markerDestination != null && isDesMarker) {
                LatLng dest = markerDestination.getPosition();
                MarkerOptions opt = new MarkerOptions();
                opt.position(dest);
                opt.title(getStr(R.string.text_destination_pin_title));
                opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                markerDestination = map.addMarker(opt);
                markerDestination.setDraggable(true);
                markerDestination.setVisible(true);
            }
            if (markerSource != null && isSrcMarker) {
                LatLng source = markerSource.getPosition();
                MarkerOptions opt = new MarkerOptions();
                opt.position(source);
                opt.title(getStr(R.string.text_source_pin_title));
                opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_org)));
                markerSource = map.addMarker(opt);
                markerSource.setDraggable(true);
                markerSource.setVisible(true);
            }
        }
    }

//    private void getProviderDuration() {
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put(Const.URL, Const.ServiceType.GET_ALLPROVIDER_LIST);
//        AppLog.Log(Const.TAG, Const.URL);
//        map.put(Const.Params.USER_LATITUDE,
//                String.valueOf(curretLatLng.latitude));
//        map.put(Const.Params.USER_LONGITUDE,
//                String.valueOf(curretLatLng.longitude));
//
//
//        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
//                Const.ServiceCode.GET_PROVIDER_LIST, this, this));
//
//    }


//    private void updateProviderDuration(LatLng curretLatLng) {
//
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put(Const.URL, Const.ServiceType.GET_ALLPROVIDER_LIST);
//        AppLog.Log(Const.TAG, Const.URL);
//        map.put(Const.Params.USER_LATITUDE,
//                String.valueOf(this.curretLatLng.latitude));
//        map.put(Const.Params.USER_LONGITUDE,
//                String.valueOf(this.curretLatLng.longitude));
//        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
//                Const.ServiceCode.UPDATE_ALLPROVIDER_LIST, this, this));
//
//    }


    private String splitbyspace(String ele) {
        String value = "";
        String[] vals = ele.split(" ");
        if (vals.length > 1) {
            value = vals[0];
        }

        return value;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");

        if (flagIsDropAddress) {
//            Toast.makeText(getContext(),"Pick Click",Toast.LENGTH_SHORT).show();
            IsDrop_selected();
        } else {
//            Toast.makeText(getContext(),"Drop Click",Toast.LENGTH_SHORT).show();
            IsSource_selected();
        }

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        toPosition = marker.getPosition();
//        getAddressFromLocation(toPosition, etDropoff);

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selecteddate = getSelectedDatesString();
        CalendarDay datenew = widget.getSelectedDate();
        selecteddatenew = formatter2.format(datenew.getDate());
        ivdatetextView.setText(selecteddatenew);
        Displaydialog.dismiss();
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            checkselecteddate = "";
            return checkselecteddate;
        } else {
            checkselecteddate = formatter.format(date.getDate());
        }
        return checkselecteddate;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Date date1, date2;
        selectedtime = hourOfDay + ":" + minute;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStart = sdf.format(new Date());
        Log.i(TAG, "Str" + dateStart);
        String dateStop = selecteddate + " " + selectedtime + ":00";
        Log.i(TAG, "Start Date:" + dateStart + "End Date" + dateStop);
        SimpleDateFormat formatsd = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        ivtimetextView.setText(selectedtime);
        try {
            date1 = formatsd.parse(dateStart);
            date2 = formatsd.parse(dateStop);
            long difference = date2.getTime() - date1.getTime();
            long diffMinutes = difference / (60 * 1000) % 60;
            long diffDays = difference / (24 * 60 * 60 * 1000);
            long diffHours = difference / (60 * 60 * 1000) % 24;
            Log.i(TAG, "Minutes" + diffMinutes);
            Log.i(TAG, "Days" + diffDays + "Hours" + diffHours);
            if (diffDays <= 0) {  //day check open
                if (diffHours > 0) {  //hour check
                    ivtimetextView.setText(selectedtime);
                    isTimevalid = true;
                } else {  //hour check close
                    if (diffMinutes > 0) {
                        if (diffMinutes >= 30) {
                            ivtimetextView.setText(selectedtime);
                            isTimevalid = true;
                        } else {
                            AndyUtils.showToast(getStr(R.string.schedule_time), R.id.coordinatorLayout, activity);
                            ivtimetextView.setText("");

                        }
                    } else {
                        AndyUtils.showToast(getStr(R.string.invalid_date_time), R.id.coordinatorLayout, activity);
                        ivtimetextView.setText("");
                        isTimevalid = false;
                    }
                }
            } else {    //day check close
                isTimevalid = true;
                ivtimetextView.setText(selectedtime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        } else {
            map.setMyLocationEnabled(true);
        }


        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {

            }
        });

        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveCanceledListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveStartedListener(this);

        map.setOnCameraChangeListener(new OnCameraChangeListener() {

            public void onCameraChange(CameraPosition camPos) {
                if (currentZoom == -1) {
                    currentZoom = camPos.zoom;
                } else if (camPos.zoom != currentZoom) {
                    currentZoom = camPos.zoom;
                    return;
                }
                curretLatLng = camPos.target;
                markerLocation = camPos.target;
                if (!isAddDestination) {
                    layoutMarker.setVisibility(LinearLayout.VISIBLE);
                    if (listType.size() > 0) {
                        if (!(curretLatLng.longitude == 0.0) && !(curretLatLng.latitude == 0.0))
                            if (curretLatLng != null) {
                                if (markerLocation.latitude == 0.0 && markerLocation.longitude == 0.0)
                                    markerLocation = new LatLng(curretLatLng.latitude, curretLatLng.longitude);
                            }
                    }
                    if (flagIsDropAddress)
                        getAddressFromLocation(camPos.target, etSource);
                    else
                        getAddressFromLocation(camPos.target, etDropoff);

                    Log.i(TAG, etSource.getText().toString());
                    if (!TextUtils.isEmpty(etSource.getText().toString()) || !etSource.getText().toString()
                            .equalsIgnoreCase(getStr(R.string.text_wait_addr))) {
                        Log.i(TAG, "Inisde etsourcefirsttime" + etSource.getText().toString());
                    } else {
                        if (!Firststraddress.equals("")) {
                            if (flagIsDropAddress) {
                                etSource.setText(Firststraddress);
                            } else {
                                etDropoff.setText(Firststraddress);
                            }
                        }
                    }
                    isMapTouched = false;

                }
            }

        });
    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            showHideViewWhenDragMap(false);

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
        }
        activity.hideKeyboard();
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveCanceled() {
    }

    @Override
    public void onCameraIdle() {
        showHideViewWhenDragMap(true);
        if (flagIsDropAddress)
            getVehicalTypes();
    }

    private class TimerRequestStatus extends TimerTask {
        @Override
        public void run() {
            if (isContinueRequest) {
                getRequestStatus(String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
            } else {
                cancel();
            }
        }
    }

    private void getRequestStatus(String requestId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken()
                        + "&" + Const.Params.REQUEST_ID + "=" + requestId);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_REQUEST_STATUS, this, this));
    }

    private void startCheckingStatusUpdate() {
        stopCheckingStatusUpdate();
        if (CustomerApplication.preferenceHelper.getRequestId() != Const.NO_REQUEST) {
            isContinueRequest = true;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY, Const.TIME_SCHEDULE);
        }
    }

    private void stopCheckingStatusUpdate() {
        isContinueRequest = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void cancleRequest() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getStr(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.removeCustomProgressRequestDialog();
        AndyUtils.showCustomProgressRequestDialog(activity, getStr(R.string.text_canceling_request), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.CANCEL_REQUEST);
        map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
        map.put(Const.Params.TOKEN,
                String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
        map.put(Const.Params.REASONFORREJECT, rejectreason);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.CANCEL_REQUEST, this, this));
    }

    class WalkerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(Const.EXTRA_WALKER_STATUS);
            if (TextUtils.isEmpty(response))
                return;

            try {
                RequestStatusModel requestStatusModel = taxiparseContent.getSingleObject(response, RequestStatusModel.class);
                if (requestStatusModel.success) {
                    switch (taxiparseContent.checkRequestStatus(requestStatusModel)) {
                        case Const.IS_WALK_STARTED:
                        case Const.IS_WALKER_ARRIVED:
                        case Const.IS_COMPLETED:
                        case Const.IS_WALKER_STARTED:
                            AppLog.Log("Map", "Remove ");
                            AndyUtils.removeCustomProgressRequestDialog();
                            requestStatusModel = taxiparseContent.getDriverDetail(response);
                            removeThisFragment();
                            if (UberMapFragment.this.isVisible())
                                activity.gotoTripFragment(requestStatusModel);
                            break;
                        case Const.IS_WALKER_RATED:
                            if (UberMapFragment.this.isVisible())
                                activity.gotoRateFragment(taxiparseContent
                                        .getDriverDetail(response));
                            break;

                        case Const.IS_REQEUST_CREATED:
                            AndyUtils.showCustomProgressDialog(activity, getStr(R.string.text_contacting), false,
                                    UberMapFragment.this);
                            startCheckingStatusUpdate();
                            isContinueRequest = true;
                            break;
                        default:
                            isContinueRequest = false;
                            break;
                    }

                } else if (requestStatusModel.error_code == Const.REQUEST_ID_NOT_FOUND) {
                    AndyUtils.removeCustomProgressDialog();
                    CustomerApplication.preferenceHelper.clearRequestData();
                    isContinueRequest = false;
                } else if (requestStatusModel.error_code == Const.INVALID_TOKEN) {
                    activity.clearSessionLogout();
                    /*if (CustomerApplication.preferenceHelper.getLoginBy()
                            .equalsIgnoreCase(Const.MANUAL))
                        login();
                    else
                        loginSocial(CustomerApplication.preferenceHelper.getUserId(),
                                CustomerApplication.preferenceHelper.getLoginBy());*/
                } else {
                    isContinueRequest = true;
                    startCheckingStatusUpdate();
                }
            } catch (Exception e) {
                AppLog.Log(TAG, e.getMessage());//To avoid crashing as only push of this text is received so in order to catch that request
                if (response.contains("No Driver")) {
                    AndyUtils.removeCustomProgressDialog();
                    AndyUtils.showToast(getStr(R.string.error_483), R.id.coordinatorLayout, activity);
                    if (activity != null) {
                        CustomerApplication.preferenceHelper.putRequestId(Const.NO_REQUEST);
                        CustomerApplication.preferenceHelper.clearRequestData();
                    }
                    startUpdatingLocationDuration();
                }

            }
        }
    }

    private void removeThisFragment() {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(this).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVehicalTypes() {
        if (curretLatLng != null) {
            AndyUtils.showCustomProgressDialog(activity, getStr(R.string.progress_loading), false, null);
            isGettingVehicalType = true;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.GET_VEHICAL_TYPES + "?latitude=" + curretLatLng.latitude + "&longitude=" + curretLatLng.longitude);
            AppLog.Log(Const.TAG, Const.URL);
            requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                    Const.ServiceCode.GET_VEHICAL_TYPES, this, this));
        }
    }

    private void LatestAllProviders(LatLng latlang) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            return;
        } else if (latlang == null) {
            return;
        }
        if (curretLatLng == null) {
            curretLatLng = latlang;
        }
        if (!(curretLatLng.longitude == 0.0) && !(curretLatLng.latitude == 0.0))
            if (curretLatLng != null) {
                if (markerLocation.latitude == 0.0 && markerLocation.longitude == 0.0)
                    markerLocation = new LatLng(curretLatLng.latitude, curretLatLng.longitude);
            }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LATEST_GET_PROVIDERS);
        map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
        map.put(Const.Params.TOKEN, String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
        map.put(Const.Params.LATITUDE, String.valueOf(latlang.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(latlang.longitude));
        requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.LATEST_GET_PROVIDERS, this, this));
    }


    private void getFareQuote(LatLng origin, String destination) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(getStr(R.string.no_internet), R.id.coordinatorLayout, activity);
        } else {
            LatLng dest = getLocationFromAddress(destination, Const.ServiceCode.GETDEST);
            if (dest == null) {
                sourceLocFare = new Location("");
                sourceLocFare.setLatitude(origin.latitude);
                sourceLocFare.setLongitude(origin.longitude);
                destLocFare = new Location("");
                return;
            }
     /*       String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            String sensor = "sensor=false";
            String parameters = str_origin + "&" + str_dest + "&" + sensor;
            String output = "json";
            String url = Const.ServiceType.DIRECTIONS_URL + output + "?" + parameters + "&key="
                    + Const.PLACES_AUTOCOMPLETE_API_KEY;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, url);
            requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                    Const.ServiceCode.GET_FARE_QUOTE, this, this));*/
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.ETA_CALC_URL);
            map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
            map.put(Const.Params.TOKEN, String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
            map.put(Const.Params.PICK_LAT, String.valueOf(curretLatLng.latitude));
            map.put(Const.Params.PICK_LONG, String.valueOf(curretLatLng.longitude));
            map.put(Const.Params.DROP_LAT, String.valueOf(dest.latitude));
            map.put(Const.Params.DROP_LONG, String.valueOf(dest.longitude));
            map.put(Const.Params.TYPE, listType.get(selectedPostion).id + "");
            requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.GET_FARE_QUOTE, this, this));

        }
    }

    private void getFareQuote(LatLng origin, LatLng dest) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
        } else {
            if (dest == null) {
                sourceLocFare = new Location("");
                sourceLocFare.setLatitude(origin.latitude);
                sourceLocFare.setLongitude(origin.longitude);
                destLocFare = new Location("");
                return;
            }
            /*String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            String sensor = "sensor=false";
            String parameters = str_origin + "&" + str_dest + "&" + sensor;
            String output = "json";
            String url = Const.ServiceType.DIRECTIONS_URL + output + "?" + parameters + "&key="
                    + Const.PLACES_AUTOCOMPLETE_API_KEY;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, url);
            requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.GET_FARE_QUOTE, this, this));*/
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.ETA_CALC_URL);
            map.put(Const.Params.ID, String.valueOf(CustomerApplication.preferenceHelper.getUserId()));
            map.put(Const.Params.TOKEN, String.valueOf(CustomerApplication.preferenceHelper.getSessionToken()));
            map.put(Const.Params.PICK_LAT, String.valueOf(curretLatLng.latitude));
            map.put(Const.Params.PICK_LONG, String.valueOf(curretLatLng.longitude));
            map.put(Const.Params.DROP_LAT, String.valueOf(dest.latitude));
            map.put(Const.Params.DROP_LONG, String.valueOf(dest.longitude));
            map.put(Const.Params.TYPE, listType.get(selectedPostion).id + "");
            requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.GET_FARE_QUOTE, this, this));

        }
    }

    private void setMarker(LatLng latLng, boolean isSource) {
        if (!UberMapFragment.this.isVisible())
            return;
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            activity.hideKeyboard();
        }
        if (latLng != null && map != null) {
            if (isSource) {
                if (markerSource == null) {
                    markerSource = map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getStr(
                                    R.string.text_source_pin_title))
                            .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_org))));
                } else {
                    markerSource.setPosition(latLng);
                    markerSource.setVisible(true);
                }
                CameraUpdateFactory.newLatLng(latLng);
            } else {
                if (markerDestination == null) {
                    MarkerOptions opt = new MarkerOptions();
                    opt.position(latLng);
                    opt.title(getStr(R.string.text_destination_pin_title));
                    opt.icon(BitmapDescriptorFactory
                            .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                    markerDestination = map.addMarker(opt);
                    markerDestination.setDraggable(true);
                    if (markerSource != null) {
                        LatLngBounds.Builder bld = new LatLngBounds.Builder();

                        bld.include(new LatLng(markerSource.getPosition().latitude,
                                markerSource.getPosition().longitude));
                        bld.include(new LatLng(markerDestination.getPosition().latitude,
                                markerDestination.getPosition().longitude));
                        LatLngBounds latLngBounds = bld.build();
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                latLngBounds, 30));
                    } else {
                        CameraUpdateFactory.newLatLng(latLng);
                    }

                } else {
                    markerDestination.setPosition(latLng);
                    markerDestination.setVisible(true);
                }
            }
        } else {
            AndyUtils.showToast(getStr(R.string.unable_location), R.id.coordinatorLayout, activity);
        }
    }

    private void applyPromoCode(String promocode) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressRequestDialog(activity, activity.
                getString(R.string.text_apply_promo), true, null);
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.URL, Const.ServiceType.APPLY_PROMO);
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.PROMO_CODE, promocode);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.APPLY_PROMO, this, this));
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        start = seekBar.getProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void setProvirderOnMap() {
        VehicleTypeModel vehicle = null;

        if (listType != null && listType.size() > selectedPostion) {
            vehicle = listType.get(selectedPostion);
        }
        if (vehicle == null) {
            return;
        }
        if (map != null) {
            map.clear();
        }

        nearDriverMarker = new HashMap<Integer, Marker>();
        for (int i = 0; i < listDriver.size(); i++) {
            ProviderListModel.WalkerList driver = listDriver.get(i);

            if (selecttypecheck) {
                if (vehicle.id == driver.mType) {
                    MarkerOptions mo = new MarkerOptions();
                    mo.flat(true);
                    mo.anchor(0.5f, 0.5f);
                    mo.icon(BitmapDescriptorFactory
                            .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)));
                    mo.title(getStr(R.string.text_drive_location));
                    mo.position(new LatLng(driver.mLatitude, driver
                            .mLongitude));
                    mo.rotation((float) driver.mBearing);
                    nearDriverMarker.put((int) driver.mId, map.addMarker(mo));
                }
            } else {
                MarkerOptions mo2 = new MarkerOptions();
                mo2.flat(true);
                mo2.anchor(0.5f, 0.5f);
                mo2.icon(BitmapDescriptorFactory
                        .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)));
                mo2.title(getStr(R.string.text_drive_location));
                mo2.position(new LatLng(driver.mLatitude, driver
                        .mLongitude));
                mo2.rotation((float) driver.mBearing);
                map.addMarker(mo2);
                nearDriverMarker.put((int) driver.mId, map.addMarker(mo2));
            }
        }
        if (markerDestination != null) {
            final LatLng dest = markerDestination.getPosition();
            MarkerOptions opt = new MarkerOptions();
            opt.position(dest);
            opt.title(getStr(
                    R.string.text_destination_pin_title));
            opt.icon(BitmapDescriptorFactory
                    .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
            markerDestination = map.addMarker(opt);

            markerDestination.setDraggable(true);
            markerDestination.setVisible(true);
        }


        boolean isGetProvider = false;
        for (int i = 0; i < listDriver.size(); i++) {
            ProviderListModel.WalkerList driver = listDriver.get(i);
            if (vehicle.id == driver.mType) {
                isGetProvider = true;
                break;
            }
        }
    }

    private void updateProviderOnMap() {
        try {
            VehicleTypeModel vehicle = listType.get(selectedPostion);
            for (int i = 0; i < listDriver.size(); i++) {
                ProviderListModel.WalkerList driver = listDriver.get(i);
                if (vehicle.id == driver.mType) {
                    for (int j = 0; j < listUpdatedDriver.size(); j++) {
                        ProviderListModel.WalkerList updatedDriver = listUpdatedDriver.get(j);
                        if (driver.mId == updatedDriver.mId) {
                            Location driverLocation = new Location("");
                            driverLocation.setLatitude(updatedDriver.mLatitude);
                            driverLocation.setLongitude(updatedDriver.mLongitude);
                            driverLocation.setBearing((float) updatedDriver.mBearing);
                            animateMarker(nearDriverMarker.get(i), new LatLng(updatedDriver.mLatitude, updatedDriver.mLongitude),
                                    driverLocation, false);
                            break;
                        }
                    }
                }
            }

            boolean isGetProvider = false;
            for (int i = 0; i < listUpdatedDriver.size(); i++) {
                ProviderListModel.WalkerList driver = listUpdatedDriver.get(i);
                if (vehicle.id == driver.mType) {
                    isGetProvider = true;
                    break;
                }
            }

            if (markerDestination != null) {
                final LatLng dest = markerDestination.getPosition();
                MarkerOptions opt = new MarkerOptions();
                opt.position(dest);
                opt.title(getStr(
                        R.string.text_destination_pin_title));
                opt.icon(BitmapDescriptorFactory
                        .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.destination_pin)));
                markerDestination = map.addMarker(opt);
                markerDestination.setDraggable(true);
                markerDestination.setVisible(true);
            }
            if (markerSource != null) {
                LatLng source = markerSource.getPosition();
                MarkerOptions opt = new MarkerOptions();
                opt.position(source);
                opt.title(getStr(R.string.text_source_pin_title));
                opt.icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_org)));
                markerSource = map.addMarker(opt);
                markerSource.setDraggable(true);
                markerSource.setVisible(true);
            }
            listDriver.clear();
            listDriver.addAll(listUpdatedDriver);
            if (!isGetProvider) {
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showVehicleDetails() {

        if (listType != null && listType.size() > 0) {
            if (selecttypecheck) {
                VehicleTypeModel vehicle = listType.get(selectedPostion);
                dialogEta = new Dialog(getActivity());
                dialogEta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogEta.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogEta.setContentView(R.layout.vehicle_details);
                dialogEta.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                tvMaxSize = (TextView) dialogEta.findViewById(R.id.tvMaxSize);
                tvMinFare = (TextView) dialogEta.findViewById(R.id.tvMinFare);
                tvLblMinFare = (TextView) dialogEta.findViewById(R.id.tvLblMinFare);
                tvETA = (TextView) dialogEta.findViewById(R.id.tvETA);
                tvGetFareEst = (TextView) dialogEta.findViewById(R.id.tvGetFareEst);
                pbMinFare = (ProgressBar) dialogEta.findViewById(R.id.pbMinFare);
                tvTotalFare = (TextView) dialogEta.findViewById(R.id.tvTotalFare);
                tvRateCard = (TextView) dialogEta.findViewById(R.id.tvRateCard);
                tvRateCard.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRateCardDialog();
                    }
                });
                tvGetFareEst.setOnClickListener(this);
                tvETA.setText((estimatedTimeTxt != null ? estimatedTimeTxt + " " + getStr(R.string.text_min) : ""));
                tvMaxSize.setText(vehicle.maxSize + " " + getStr(R.string.Txt_person));
                tvMinFare.setText(getStr(R.string.payment_unit) + vehicle.minFare);
                dialogEta.show();
                if (!etDropoff.getText().toString().isEmpty())
                    sendQuoteRequest(etDropoff.getText().toString());
            } else {
                AndyUtils.showToast(getStr(R.string.select_vehicle), R.id.coordinatorLayout, activity);
            }
        }
    }

    private void showRateCardDialog() {
        VehicleTypeModel vehicle = listType.get(selectedPostion);
        rateCardDialog = new Dialog(getActivity());
        rateCardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateCardDialog.setContentView(R.layout.dialog_rate_card);
        rateCardDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        TextView tvRateVehicleTypeName, tvRateBasePrice, tvRateDistanceCost, tvRateTimeCost;
        tvRateVehicleTypeName = (TextView) rateCardDialog
                .findViewById(R.id.tvRateVehicleTypeName);
        tvRateBasePrice = (TextView) rateCardDialog
                .findViewById(R.id.tvRateBasePrice);
        tvRateDistanceCost = (TextView) rateCardDialog
                .findViewById(R.id.tvRateDistanceCost);
        tvRateTimeCost = (TextView) rateCardDialog
                .findViewById(R.id.tvRateTimeCost);
        tvRateVehicleTypeName.setText(vehicle.name);

        tvRateBasePrice.setText(getStr(R.string.payment_unit)
                + vehicle.basePrice + " "
                + getStr(R.string.text_for) + " "
                + vehicle.baseDistance + vehicle.unit);
        tvRateDistanceCost.setText(getStr(R.string.payment_unit)
                + vehicle.pricePerUnitDistance
                + getStr(R.string.text_per) + vehicle.unit);
        tvRateTimeCost.setText(getStr(R.string.payment_unit)
                + vehicle.pricePerUnitTime
                + getStr(R.string.text_per) + getStr(R.string.text_min));
        if (!activity.isFinishing())
            rateCardDialog.show();
    }

    private void showDestinationPopup() {
        destinationDialog = new Dialog(getActivity());
        destinationDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        destinationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        destinationDialog.setContentView(R.layout.destination_popup);
        etPopupDestination = (AutoCompleteTextView) destinationDialog
                .findViewById(R.id.etPopupDestination);
        etHomeAddress = (AutoCompleteTextView) destinationDialog
                .findViewById(R.id.etHomeAddress);
        etWorkAddress = (AutoCompleteTextView) destinationDialog
                .findViewById(R.id.etWorkAddress);
        tvHomeAddress = (TextView) destinationDialog
                .findViewById(R.id.tvHomeAddress);
        tvWorkAddress = (TextView) destinationDialog
                .findViewById(R.id.tvWorkAddress);
        tvHomeAddress.setText(CustomerApplication.preferenceHelper.getHomeAddress());
        tvWorkAddress.setText(CustomerApplication.preferenceHelper.getWorkAddress());
        etHomeAddress.setText(CustomerApplication.preferenceHelper.getHomeAddress());
        etWorkAddress.setText(CustomerApplication.preferenceHelper.getWorkAddress());

        layoutHomeText = (LinearLayout) destinationDialog
                .findViewById(R.id.layoutHomeText);
        layoutHomeEdit = (LinearLayout) destinationDialog
                .findViewById(R.id.layoutHomeEdit);
        layoutWorkText = (LinearLayout) destinationDialog
                .findViewById(R.id.layoutWorkText);
        layoutWorkEdit = (LinearLayout) destinationDialog
                .findViewById(R.id.layoutWorkEdit);
        layoutHomeText.setOnClickListener(this);
        layoutWorkText.setOnClickListener(this);
        destinationDialog.findViewById(R.id.imgClearDest).setOnClickListener(
                this);
        destinationDialog.findViewById(R.id.imgClearHome).setOnClickListener(
                this);
        destinationDialog.findViewById(R.id.imgClearWork).setOnClickListener(
                this);

        destinationDialog.findViewById(R.id.btnEditHome).setOnClickListener(
                this);
        destinationDialog.findViewById(R.id.btnEditWork).setOnClickListener(
                this);
        nearByList = (ListView) destinationDialog.findViewById(R.id.nearByList);
        pbNearby = (ProgressBar) destinationDialog.findViewById(R.id.pbNearby);

        adapterPopUpDestination = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        etPopupDestination.setAdapter(adapterPopUpDestination);
        adapterHomeAddress = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        etHomeAddress.setAdapter(adapterHomeAddress);
        adapterWorkAddress = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        etWorkAddress.setAdapter(adapterWorkAddress);
        destinationDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        etPopupDestination.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                sendQuoteRequest(adapterPopUpDestination.getItem(arg2));
            }
        });
        etHomeAddress.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String selectedPlace = adapterHomeAddress.getItem(arg2);
                CustomerApplication.preferenceHelper.putHomeAddress(selectedPlace);
                tvHomeAddress.setText(selectedPlace);
                layoutHomeEdit.setVisibility(LinearLayout.GONE);
                layoutHomeText.setVisibility(LinearLayout.VISIBLE);
            }
        });
        etWorkAddress.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String selectedPlace = adapterWorkAddress.getItem(arg2);
                CustomerApplication.preferenceHelper.putWorkAddress(selectedPlace);
                tvWorkAddress.setText(selectedPlace);
                layoutWorkEdit.setVisibility(LinearLayout.GONE);
                layoutWorkText.setVisibility(LinearLayout.VISIBLE);
            }
        });
        nearByLocations();
        nearByList.setOnItemClickListener(this);
        if (!activity.isFinishing())
            destinationDialog.show();
    }

    private void sendQuoteRequest(String destination) {
        pbMinFare.setVisibility(View.VISIBLE);
        tvMinFare.setVisibility(View.GONE);
        tvLblMinFare.setVisibility(View.GONE);
        tvTotalFare.setVisibility(View.GONE);
        tvGetFareEst.setText(destination);
        if (destinationDialog != null)
            destinationDialog.dismiss();
        getFareQuote(curretLatLng, destination);
    }

    private void nearByLocations() {
        StringBuilder sb = new StringBuilder(Const.PLACES_API_BASE
                + Const.TYPE_NEAR_BY + Const.OUT_JSON);
        sb.append("?sensor=true&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY);
        sb.append("&location=" + curretLatLng.latitude + ","
                + curretLatLng.longitude);
        sb.append("&radius=500");
        AppLog.Log("", "Near location Url : " + sb.toString());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, sb.toString());
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_NEAR_BY, this, this));


    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//        address=((MyFontTextView)arg0.getChildAt(arg2)).getText();
        LatLng temporaryLatLng = getLocationFromAddress(nearByAd.getItem(arg2), Const.ServiceCode.GET_FARE_EST);
        getAddressFromLocation(temporaryLatLng, etPopupDestination);
        if (address != null)
            sendQuoteRequest(nearByAd.getItem(arg2) + ", " + address.getLocality()
                    + ", " + address.getAdminArea() + ", " + address.getCountryName());
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.removeCustomProgressRequestDialog();
        AndyUtils.showToast(getStr(R.string.error_contact_server), R.id.coordinatorLayout, activity);
    }

    @Override
    public void onLocationReceived(LatLng latlong) {
    }

    @Override
    public void onLocationReceived(Location location) {
        //

        if (location != null) {
            myLocation = location;
            static_my_Location = location;
            curretLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            if (!isFirstLocationRecived) {
                if (myLocation.getLatitude() != 0.0 && myLocation.getLongitude() != 0.0) {
                    if (etSource != null && (etSource.getText().toString().trim().equalsIgnoreCase(getString(R.string.text_waiting_for_address)) || etSource.length() == 0)) {
                        if (getActivity() != null) {
                            markerLocation = curretLatLng;
                            if (flagIsDropAddress) {
                                getAddressFromLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), etSource);

                            } else
                                getAddressFromLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), etDropoff);

                        }
                        isFirstLocationRecived = true;
                    }
                }
            }

        }
    }

    @Override
    public void onConntected(Bundle bundle) {
        //

    }

    @Override
    public void onConntected(Location location) {
        //

        if (location != null) {
            Log.e("TAG", "Inside On Connect");
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            curretLatLng = latLang;
//            getAllProviders(latLang);@required
            animateCameraToMarker(latLang, false);

//            if( (Pickup_Lat > 0.0 && Pickup_Long > 0.0) && (DrioIn_Lat > 0.0 && DrioIn_Long > 0.0 ))
//            {
//
//            }else {
            Pickup_Lat = curretLatLng.latitude;
            Pickup_Long = curretLatLng.longitude;
            DrioIn_Lat = curretLatLng.latitude;
            DrioIn_Long = curretLatLng.longitude;
//            }

        } else {
            activity.showLocationOffDialog();
        }
    }

    private String getFareCalculation(double distanceInKm) {
        VehicleTypeModel vehicle = listType.get(selectedPostion);
        double basePrice = vehicle.basePrice;
        float baseDistance = vehicle.baseDistance;
        double distanceCost = vehicle.pricePerUnitDistance;
        double fare = (distanceInKm > baseDistance ? (distanceInKm - baseDistance) : baseDistance) * distanceCost + basePrice;
        DecimalFormat format = new DecimalFormat("0.00");
        String finalFare = format.format(fare);
        return finalFare;

    }

    public boolean OnBackPressed() {
        //
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        final Bundle mapViewSaveState = new Bundle(savedInstanceState);
        mMapView.onSaveInstanceState(mapViewSaveState);
        savedInstanceState.putBundle("mapViewSaveState", mapViewSaveState);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void getLocationInfo(LatLng address, final EditText et) {
        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                    String.format(Const.ServiceType.GEOCODER_URL, address.latitude + "", address.longitude + ""),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                setCurrentAddress(response.getJSONArray("results").getJSONObject(0).getString("formatted_address"), et);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    // hide the progress dialog
                }
            });
            requestQueue.add(jsonObjReq);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void setCurrentAddress(String address, final EditText et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setText(address);
        et.setTextColor(getColor(color.black));
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
    }

    private void startUpdatingLocationDuration() {

        handlerThread.postDelayed(runnable, 5000);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LatestAllProviders(markerLocation);
            handlerThread.postDelayed(this, 5000);
        }
    };

    private void stopUpdatingLocationDuration() {
        if (handlerThread != null)
            handlerThread.removeCallbacks(runnable);

    }

    void showHideViewWhenDragMap(boolean show) {
        f_ubermap_function_icon.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        f_ubermap_d_P_address.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        if (show) {
            activity.actionBar.setCustomView(R.layout.custom_action_bar);
            View v2 = activity.actionBar.getCustomView();
            ImageButton imgbtn = (ImageButton) v2.findViewById(R.id.btnActionNotification);
            ImageButton imgAction = (ImageButton) v2.findViewById(R.id.btnActionMenu);
            imgbtn.setVisibility(View.INVISIBLE);
            imgAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainDrawerActivity.drawerLayout.openDrawer(MainDrawerActivity.listDrawer);
                }
            });
            IsMapDragable = true;
            lay_rides.animate().translationY(0).alpha(1.0f);
            bottomrecycle1.animate().translationY(0).alpha(1.0f);
            pin_marker.animate().translationY(0).alpha(1.0f);
            xMark.animate().scaleX(0.4f).scaleY(0.4f).alpha(0.5f);
            dotted_line_img.setVisibility(View.INVISIBLE);
            downArrowActionBar.setVisibility(View.INVISIBLE);
            if (flagIsDropAddress) {
                setMarker(new LatLng(Pickup_Lat, Pickup_Long), true);
                downArrowPick.setVisibility(View.VISIBLE);
                dropAddresslayoutView.setVisibility(View.VISIBLE);
            } else {
                setMarker(new LatLng(DrioIn_Lat, DrioIn_Long), false);
                downArrowDrop.setVisibility(View.VISIBLE);
                pickAddresslayoutView.setVisibility(View.VISIBLE);
            }
        } else {                                                                                 // onPress down
            layoutMarker.setVisibility(View.VISIBLE);
            pin_marker.animate().translationY(-80).alpha(0.5f);
            lay_rides.animate().translationY(100).alpha(0.0f);
            xMark.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.7f);
            bottomrecycle1.animate().translationY(100).alpha(0.0f);
            activity.actionBar.setCustomView(R.layout.actionbar_locationview);
            v = activity.actionBar.getCustomView();
            actionBar_ACTV = (AutoCompleteTextView) v.findViewById(R.id.etEnterSouce);
            LinearLayout bgActionBar = (LinearLayout) v.findViewById(R.id.bgActionBar);
            dotted_line_img.setVisibility(View.VISIBLE);
            downArrowActionBar.setVisibility(View.VISIBLE);
//            map.clear();
            IsMapDragable = false;
            if (flagIsDropAddress) {
                bgActionBar.setBackgroundResource(R.color.colorPrimary_second);
                activity.actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));
                downArrowActionBar.setImageResource(R.drawable.downarrow_);
                CustomerApplication.preferenceHelper.PutPickLat(Pickup_Lat);
                CustomerApplication.preferenceHelper.PutPickLong(Pickup_Long);
                isSrcMarker = false;
                downArrowPick.setVisibility(View.INVISIBLE);
                dropAddresslayoutView.setVisibility(View.GONE);
            } else {
                bgActionBar.setBackgroundResource(R.color.colorPrimary);
                downArrowActionBar.setImageResource(R.drawable.downarrow_drop);
                activity.actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
                CustomerApplication.preferenceHelper.PutDropLat(DrioIn_Lat);
                CustomerApplication.preferenceHelper.PutDropLong(DrioIn_Long);
                isDesMarker = false;
                downArrowDrop.setVisibility(View.INVISIBLE);
                pickAddresslayoutView.setVisibility(View.GONE);
            }
        }
    }

    public void changeMarkerDest(LatLng latlng) {
        isSource = false;
        if (latlng == null)
            return;
        setMarker(curretLatLng, isSource);
        animateCameraToMarker(latlng, true);
        DrioIn_Lat = latlng.latitude;
        DrioIn_Long = latlng.longitude;

    }

    public void changeMarkerSource(LatLng latlng) {
        isMapTouched = true;
        curretLatLng = latlng;
        isSource = true;
        if (latlng == null)
            return;
        setMarker(curretLatLng, isSource);
        animateCameraToMarker(curretLatLng, true);
        Pickup_Lat = latlng.latitude;
        Pickup_Long = latlng.longitude;
    }
}
