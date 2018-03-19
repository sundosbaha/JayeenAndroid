package com.jayeen.driver.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.base.BaseMapFragment;
import com.jayeen.driver.db.DBHelper;
import com.jayeen.driver.locationupdate.LocationHelper;
import com.jayeen.driver.locationupdate.LocationHelper.OnLocationReceived;
import com.jayeen.driver.model.BeanRoute;
import com.jayeen.driver.model.BeanStep;
import com.jayeen.driver.model.RequestDetail;
import com.jayeen.driver.newmodels.CheckChangeStateModel;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.newmodels.WalkerCompleteModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.ParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.utills.NplusTextview;
import com.jayeen.driver.widget.MyFontButton;
import com.jayeen.driver.widget.MyFontEdittextView;
import com.jayeen.driver.widget.MyFontTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.jayeen.driver.DriverApplication.requestQueue;

public class JobFragment extends BaseMapFragment implements
        AsyncTaskCompleteListener, OnLocationReceived, OnMapReadyCallback {
    private GoogleMap googleMap;
    private PolylineOptions lineOptions;
    private ArrayList<LatLng> points;
    private MyFontTextView tvJobTime, tvJobDistance, tvJobStatus, tvJobwait, tvClientName,
            tvDestinationAddress;
    private ImageView ivClientProfilePicture;
    private RatingBar tvClientRating;
    private ParseContent parseContent;

    private Location location;
    private LocationHelper locationHelper;
    private RequestDetail requestDetail;
    private Marker markerDriverLocation, markerClientLocation;
    private Timer elapsedTimer;
    private DBHelper dbHelper;
    private int jobStatus = 0;
    private String time, distance = "0";
    private final String TAG = "JobFragment";
    DecimalFormat decimalFormat;
    private BroadcastReceiver mReceiver;
    private MyFontTextView tvPaymentType;
    private BroadcastReceiver modeReceiver;
    private View jobFragmentView;
    private MapView mMapView;
    public static final long ELAPSED_TIME_SCHEDULE = 10 * 1000;//60 * 1000;
    private Bundle mBundle;
    private BroadcastReceiver destReceiver;
    private BeanRoute routeDest;
    private ArrayList<LatLng> pointsDest;
    private PolylineOptions lineOptionsDest;
    private Polyline polyLineDest;
    private Marker markerDestination;
    private boolean isAddMarker = false;
    private BeanRoute routeClient;
    private ArrayList<LatLng> pointsClient;
    private PolylineOptions lineOptionsClient;
    private Polyline polyLineClient;
    private SoundPool soundPool;
    private int soundid;
    private boolean isApprovedCheck = true, loaded = false;
    RelativeLayout PickupDropAddrLayout;
    ImageButton fab;
    Dialog rDialog;
    MyFontButton btnReasonConfirm, btnReasonCancel;
    MyFontEdittextView rejectreasonedit;
    String rejectreason = "";
    ImageButton btnCancelTrip;
    public WaitTripFragment waitTripFragment;
    private NplusTextview tvJobCallClient, tvJobestimaterate;
    private LinearLayout f_ratingview_lay;
    CheckChangeStateModel stateModelwalkstarted = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        jobFragmentView = inflater.inflate(R.layout.fragment_job, container,
                false);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }

        tvJobTime = (MyFontTextView) jobFragmentView.findViewById(R.id.tvJobTime);
        fab = (ImageButton) jobFragmentView.findViewById(R.id.fab);
        tvJobDistance = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvJobDistance);
        tvJobStatus = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvJobStatus);
        tvJobwait = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvJobwait);
        tvClientName = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvClientName);
        tvPaymentType = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvPaymentType);
        PickupDropAddrLayout = (RelativeLayout) jobFragmentView.findViewById(R.id.layout1addr);
        PickupDropAddrLayout.setVisibility(View.GONE);
        btnCancelTrip = (ImageButton) jobFragmentView.findViewById(R.id.btnCancelTrip);
        btnCancelTrip.setOnClickListener(this);
        tvClientRating = (RatingBar) jobFragmentView
                .findViewById(R.id.tvClientRating);
        ivClientProfilePicture = (ImageView) jobFragmentView
                .findViewById(R.id.ivClientImage);
        tvDestinationAddress = (MyFontTextView) jobFragmentView
                .findViewById(R.id.tvDestinationAddress);
        f_ratingview_lay = (LinearLayout) jobFragmentView
                .findViewById(R.id.f_ratingview_lay);

        tvJobStatus.setOnClickListener(this);
        tvJobCallClient = (NplusTextview) jobFragmentView.findViewById(R.id.tvJobCallClient);
        tvJobestimaterate = (NplusTextview) jobFragmentView.findViewById(R.id.tvJobestimaterate);
        tvJobCallClient.setOnClickListener(this);
        fab.setOnClickListener(this);
        tvJobwait.setOnClickListener(this);
        waitTripFragment = new WaitTripFragment();
        setPaymentType();
        if (DriverApplication.preferenceHelper.getInstantJobId() == DriverApplication.preferenceHelper.getRequestId()) {
//            if (fab.getVisibility() != View.GONE)
            fab.setVisibility(View.GONE);
        }
        return jobFragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        soundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 100);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundid = soundPool.load(mapActivity, R.raw.beep, 1);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parseContent = new ParseContent(mapActivity);
        decimalFormat = new DecimalFormat("0.00");
        points = new ArrayList<LatLng>();
        dbHelper = new DBHelper(mapActivity);
        jobStatus = getArguments().getInt(AndyConstants.JOB_STATUS,
                AndyConstants.IS_WALKER_ARRIVED);
        requestDetail = (RequestDetail) getArguments().getSerializable(
                AndyConstants.REQUEST_DETAIL);//}catch (Exception e) {
        if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
            startElapsedTimer();
            getPathFromServer();
        }

        setjobStatus(jobStatus);
        mMapView = (MapView) jobFragmentView.findViewById(R.id.jobMap);
        mMapView.onCreate(savedInstanceState);
        setUpMap();
        locationHelper = new LocationHelper(getActivity());
        locationHelper.setLocationReceivedLister(this);
        locationHelper.onStart();


        if (requestDetail == null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).addToBackStack("Test").commit();
            requestInProcess();
            mapActivity.addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
            return;
        }
        setClientDetails(requestDetail);

        if (requestDetail.getDestinationLatitude() != null && requestDetail.getDestinationLongitude() != null && !requestDetail.getDestinationLatitude().equalsIgnoreCase("null") && !requestDetail.getDestinationLongitude().equalsIgnoreCase("null") &&
                requestDetail.getClientLongitude() != null && requestDetail.getClientLatitude() != null) {
            LatLng d_latlng = new LatLng(Double.valueOf(requestDetail.getDestinationLatitude()),
                    Double.valueOf(requestDetail.getDestinationLongitude()));

            DriverApplication.preferenceHelper.putClientDestination(d_latlng);
            getDestinationAddress(requestDetail.getDestinationLatitude(), requestDetail.getDestinationLongitude());
        }
        DriverApplication.preferenceHelper.putIsNavigate(true);
        if (requestDetail.getClientProfile() != null && !requestDetail.getClientProfile().isEmpty()) {
            Picasso.with(getActivity())
                    .load(requestDetail.getClientProfile())
                    .error(R.drawable.user)
                    .placeholder(R.drawable.user)   // optional
                    .into(ivClientProfilePicture);
        }
    }

    private void requestInProcess() {
        HashMap map = new HashMap();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.REQUEST_IN_PROGRESS + AndyConstants.Params.ID + "=" + DriverApplication.preferenceHelper.getUserId() + "&" + AndyConstants.Params.TOKEN + "=" + DriverApplication.preferenceHelper.getSessionToken() + "&" + AndyConstants.Params.REQUEST_ID + "=-1");
        requestQueue.add(new VolleyHttpRequest(Method.GET, map, AndyConstants.ServiceCode.REQUEST_IN_PROGRESS, this, this));
    }

    private void getPathFromServer() {
        AndyUtils.showCustomProgressDialog(mapActivity, "", mapActivity.getResources()
                .getString(R.string.progress_loading), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL,
                AndyConstants.ServiceType.PATH_REQUEST
                        + AndyConstants.Params.ID + "="
                        + DriverApplication.preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + DriverApplication.preferenceHelper.getSessionToken() + "&"
                        + AndyConstants.Params.REQUEST_ID + "="
                        + DriverApplication.preferenceHelper.getRequestId());
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.PATH_REQUEST, this, this));
    }

    private void setClientDetails(RequestDetail requestDetail) {
        tvClientName.setText(requestDetail.getClientName());
        if (requestDetail.getClientRating() != 0) {
            tvClientRating.setRating(requestDetail.getClientRating());
        }
        if (requestDetail.getClientProfile() != null && !requestDetail.getClientProfile().isEmpty()) {
            Picasso.with(getActivity())
                    .load(requestDetail.getClientProfile())
                    .placeholder(R.drawable.user)   // optional
                    .into(ivClientProfilePicture);
        }
    }

    public void getDestinationAddress(String latitude, String longitude) {

        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            tvClientRating.setVisibility(View.VISIBLE);
            DriverApplication.preferenceHelper.putDestinationLatitude("");
            DriverApplication.preferenceHelper.putDestinationLongitude("");
            tvDestinationAddress.setVisibility(View.GONE);
        } else {

            Geocoder geocoder;
            List<Address> addresses = new ArrayList<>();
            geocoder = new Geocoder(mapActivity, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude), 1);
                if (addresses == null || addresses.size() == 0)
                    return;
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                tvDestinationAddress.setVisibility(View.VISIBLE);
                tvDestinationAddress.setText(" " + address + " " + city + "\n " + state + " " + country + " " + postalCode);
                if (tvDestinationAddress.getText().toString().contains("null")) {
                    String destinationAddress = tvDestinationAddress.getText().toString().replace("null", "");
                    tvDestinationAddress.setText(destinationAddress + " ");
                }
                tvClientRating.setVisibility(View.VISIBLE);
                AppLog.Log("Address", " " + address + " " + city + "\n " + state + " " + country + " " + postalCode);
            } catch (IOException e) {
                e.printStackTrace();
                getLocationInfo(latitude, longitude, String.format(AndyConstants.ServiceType.GEOCODER_URL, latitude + "", longitude + "").replace("http", "https"));
            }

        }

        if (googleMap == null) {
            return;
        }
    }

    private void setjobStatus(int jobStatus) {

        switch (jobStatus) {
            case 11:
            case AndyConstants.IS_WALKER_STARTED:
                tvJobStatus.setText(mapActivity.getResources().getString(
                        R.string.text_walker_started));
                tvJobwait.setEnabled(false);
                tvJobwait.setVisibility(View.GONE);
                break;
            case AndyConstants.IS_WALKER_ARRIVED:
                tvJobStatus.setText(mapActivity.getResources().getString(
                        R.string.text_walker_arrived));
                tvJobwait.setEnabled(false);
                tvJobwait.setVisibility(View.GONE);
                break;
            case AndyConstants.IS_WALK_STARTED:
                tvJobStatus.setText(mapActivity.getResources().getString(
                        R.string.text_walk_started));
                if (DriverApplication.preferenceHelper.getInstantJobId() != DriverApplication.preferenceHelper.getRequestId()) {
//                    tvJobwait.setEnabled(true);
//                    tvJobwait.setVisibility(View.VISIBLE);
                    tvJobestimaterate.setVisibility(View.VISIBLE);
                } else {
                    tvJobwait.setEnabled(false);
                    tvJobwait.setVisibility(View.GONE);
                    tvJobestimaterate.setVisibility(View.GONE);
                    tvJobCallClient.setVisibility(View.GONE);
//                    f_ratingview_lay.setVisibility(View.GONE);
                }
                break;
            case AndyConstants.IS_WALK_COMPLETED:
                btnCancelTrip.setVisibility(View.GONE);
                tvJobStatus.setText(mapActivity.getResources().getString(
                        R.string.text_walk_completed));
//                tvJobwait.setEnabled(true);
//                tvJobwait.setVisibility(View.VISIBLE);
                if (DriverApplication.preferenceHelper.getInstantJobId() == DriverApplication.preferenceHelper.getRequestId()) {
                    tvJobCallClient.setVisibility(View.GONE);
//                    f_ratingview_lay.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(markerDestination == null ? View.GONE : View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvJobStatus:
                switch (jobStatus) {
                    case AndyConstants.IS_WALKER_STARTED:
                        mapActivity.clearAll();
                        walkerStarted();
                        break;
                    case AndyConstants.IS_WALKER_ARRIVED:
                        mapActivity.clearAll();
                        walkerArrived();
                        break;
                    case AndyConstants.IS_WALK_STARTED:
                        mapActivity.clearAll();
                        walkStarted();
                        break;
                    case AndyConstants.IS_WALK_COMPLETED:
                        mapActivity.clearAll();
                        walkCompleted();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.tvJobwait:
                if (waitTripFragment != null)
                    waitTripFragment.show(getFragmentManager(), "");
                break;
            case R.id.fab:
                switch (jobStatus) {
                    case /*AndyConstants.IS_WALKER_STARTED *//**/ 1/**/:
                    case /*AndyConstants.IS_WALKER_ARRIVED*//**/ 2/**/:
                    case /*AndyConstants.IS_WALK_STARTED*/ 3:
                        if (markerClientLocation != null)
                            RedirectToNavigation(Double.valueOf(markerClientLocation.getPosition().latitude), Double.valueOf(markerClientLocation.getPosition().longitude));
                        else
                            Toast.makeText(getActivity(), getString(R.string.txt_dest_not_available), Toast.LENGTH_SHORT).show();
//                            fab.setVisibility(View.GONE);
                        break;

                    case /*AndyConstants.IS_WALK_COMPLETED*/ 4:
                        if (DriverApplication.preferenceHelper.getInstantJobId() == DriverApplication.preferenceHelper.getRequestId()) {
                            RedirectToNavigation(Double.valueOf(markerClientLocation.getPosition().latitude), Double.valueOf(markerClientLocation.getPosition().longitude));
                        }else {
                            if (markerDestination != null)
                                RedirectToNavigation(Double.valueOf(markerDestination.getPosition().latitude), Double.valueOf(markerDestination.getPosition().longitude));
                            else
                                Toast.makeText(getActivity(), getString(R.string.txt_dest_not_available), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case R.id.btnCancelReason:
                if (rDialog.isShowing()) {
                    rDialog.dismiss();
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
            case R.id.tvJobCallClient:
                int currentapiVersion = Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PRIVILEGED}, 10);
                    } else {
                        callPhone(requestDetail.getClientPhoneNumber());
                    }
                } else {
                    callPhone(requestDetail.getClientPhoneNumber());
                }
                break;
            default:
                break;
        }
    }

    private void animateCamera(LatLng latlng) {
        if (markerClientLocation != null) {
            try {
                Location dest = new Location("dest");
                dest.setLatitude(markerClientLocation.getPosition().latitude);
                dest.setLongitude(markerClientLocation.getPosition().longitude);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latlng).bearing(location.bearingTo(dest))
                        .zoom(googleMap.getCameraPosition().zoom).tilt(45)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPathToClient(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.DIRECTIONS_URL + "json?origin="
                + source.latitude + "," + source.longitude
                + "&destination=" + destination.latitude + ","
                + destination.longitude + "&sensor=false&key="
                + AndyConstants.DIRECTION_API_KEY);

        AppLog.Log("Navigation Path", AndyConstants.DIRECTIONS_URL + "json?origin="
                + source.latitude + "," + source.longitude
                + "&destination=" + destination.latitude + ","
                + destination.longitude + "&sensor=false&key="
                + AndyConstants.DIRECTION_API_KEY);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.DRAW_PATH_CLIENT, this, this));
    }

    private void walkCompleted() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(mapActivity.getString(R.string.toast_no_internet));
            return;
        }
        AndyUtils.showCustomProgressDialog(mapActivity, "", mapActivity.getResources().getString(R.string.progress_send_request), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALK_COMPLETED);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID, String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.LATITUDE, DriverApplication.preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE, DriverApplication.preferenceHelper.getWalkerLongitude());
        map.put(AndyConstants.Params.DISTANCE, DriverApplication.preferenceHelper.getDistance() + "");
        map.put(AndyConstants.Params.TIME, time);
        map.put(AndyConstants.Params.TOTAL_WAITING_TIME, DriverApplication.preferenceHelper.getTotalTimerMin());
        map.put(AndyConstants.Params.TRIP_WAITING_TIME, DriverApplication.preferenceHelper.getTripTimerMin());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.WALK_COMPLETED, this, this));
    }

    private void walkStarted() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(getResources().getString(R.string.toast_no_internet));
            return;
        }
        AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
                .getString(R.string.progress_send_request), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALK_STARTED);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID,
                String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.TYPE, String.valueOf(DriverApplication.preferenceHelper.getTYPE()));
        map.put(AndyConstants.Params.LATITUDE,
                DriverApplication.preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE,
                DriverApplication.preferenceHelper.getWalkerLongitude());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.WALK_STARTED, this, this));
//        Toast.makeText(mapActivity, "start", Toast.LENGTH_SHORT).show();
    }

    private void walkerArrived() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            Toast.makeText(getActivity(), R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
            mapActivity.display_toast(getResources().getString(R.string.toast_no_internet));
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
                .getString(R.string.progress_send_request), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALK_ARRIVED);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID,
                String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.LATITUDE,
                DriverApplication.preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE,
                DriverApplication.preferenceHelper.getWalkerLongitude());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.WALKER_ARRIVED, this, this));
    }

    private void walkerStarted() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            mapActivity.display_toast(getResources().getString(R.string.toast_no_internet));
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, "", getResources().getString(R.string.progress_send_request), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALKER_STARTED);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID, String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.LATITUDE, DriverApplication.preferenceHelper.getWalkerLatitude());
        map.put(AndyConstants.Params.LONGITUDE, DriverApplication.preferenceHelper.getWalkerLongitude());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.WALKER_STARTED, this, this));
//        Toast.makeText(mapActivity, "tap to start", Toast.LENGTH_SHORT).show();
    }

    private void setUpMap() {
        if (googleMap == null) {
            ((MapView) jobFragmentView.findViewById(R.id.jobMap)).getMapAsync(this);
        }
    }

    private void addMarker() {
        if (googleMap == null) {
            setUpMap();
            return;
        }

    }

    public void onDestroyView() {
        SupportMapFragment f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.jobMap);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        googleMap = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (Exception e) {
                AppLog.Log("JobFragment", e.getMessage());
            }
        }
        locationHelper.onStop();
        super.onDestroy();
        stopElapsedTimer();
        ubregisterCancelReceiver();
        ubregisterPaymentModeReceiver();
        unRegisterDestinationReceiver();
        DriverApplication.preferenceHelper.putIsTripOn(false);
        if (waitTripFragment != null && waitTripFragment.stopwatch != null)
            waitTripFragment.stopwatch.stop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    private void initPreviousDrawPath() {
        lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(15);
        lineOptions.geodesic(true);
        lineOptions.color(getResources().getColor(R.color.skyblue));
        if (googleMap != null) {
            googleMap.addPolyline(lineOptions);
            points.clear();
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        if (!this.isVisible())
            return;
        LatLng latlong;
        switch (serviceCode) {
            case AndyConstants.ServiceCode.WALKER_STARTED:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(TAG, "walker started response " + response);
                CheckChangeStateModel stateModelstarted = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (stateModelstarted.mSuccess) {
                    jobStatus = AndyConstants.IS_WALK_STARTED;  // navi
                    setjobStatus(jobStatus);
                    if (markerClientLocation != null) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }

                    btnCancelTrip.setVisibility(View.VISIBLE);
                } else {
                    if (stateModelstarted.error_code == AndyConstants.SERVICE_ID_NOT_FOUND2) {
                        DriverApplication.preferenceHelper.clearRequestData();
                        mapActivity.addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                        AndyUtils.showToast(getResources().getString(R.string.error_something_wrong), R.id.coordinatorLayout, getActivity());
                    }
                }

                break;
            case AndyConstants.ServiceCode.WALKER_ARRIVED:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(TAG, "walker arrived response " + response);
                CheckChangeStateModel stateModelarrived = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (stateModelarrived.mSuccess) {
                    jobStatus = AndyConstants.IS_WALK_STARTED;
                    setjobStatus(jobStatus);
                    if (markerDestination != null) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }

                    btnCancelTrip.setVisibility(View.VISIBLE);

                }
                break;
            case AndyConstants.ServiceCode.WALK_STARTED:
                AndyUtils.removeCustomProgressDialog();
//                *****************************
                AppLog.Log(TAG, "walk started response " + response);
                stateModelwalkstarted = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (stateModelwalkstarted.mSuccess) {
                    DriverApplication.preferenceHelper.putIsTripStart(true);
                    jobStatus = AndyConstants.IS_WALK_COMPLETED;
                    setjobStatus(jobStatus);
                    DriverApplication.preferenceHelper.putRequestTime(Calendar.getInstance()
                            .getTimeInMillis());
                    if (markerClientLocation != null) {
                        markerClientLocation.setTitle(mapActivity.getResources().getString(R.string.job_start_location));
                    }
                    startElapsedTimer();
                    if (DriverApplication.preferenceHelper.getInstantJobId() == DriverApplication.preferenceHelper.getRequestId()) {
                        tvJobCallClient.setVisibility(View.GONE);
//                    f_ratingview_lay.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(markerDestination == null ? View.GONE : View.VISIBLE);
                    }
                    /*if (markerDestination != null) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                    }*/
                    btnCancelTrip.setVisibility(View.GONE);
//                    tvJobwait.setEnabled(true);
                    tvJobestimaterate.setVisibility(View.VISIBLE);
//                    tvJobwait.setVisibility(View.VISIBLE);
                } else if (stateModelwalkstarted.error_code == AndyConstants.SERVICE_ID_NOT_FOUND2) {
                    mapActivity.addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                }

                break;
            case AndyConstants.ServiceCode.WALK_COMPLETED:
                AppLog.Log(TAG, response);
                AndyUtils.removeCustomProgressDialog();
                try {
                    new doAsync().execute(response).get();
                } catch (Exception e) {
                    AndyUtils.removeCustomProgressDialog();
                }
                if (DriverApplication.preferenceHelper.getIsWaitDialogVisible()) {
                    if (!waitTripFragment.isVisible()) {
                        waitTripFragment.show(getFragmentManager(), "");
                    }
                }
                break;

            case AndyConstants.ServiceCode.GET_ROUTE:
                AndyUtils.removeCustomProgressDialog();
                break;
            case AndyConstants.ServiceCode.PATH_REQUEST:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log(TAG, "Path request :" + response);
                if (parseContent.isSuccess(response)) {
                    parseContent.parsePathRequest(response, points);
                    initPreviousDrawPath();
                }
                break;
            case AndyConstants.ServiceCode.DRAW_PATH:
                AndyUtils.removeCustomProgressDialog();
                if (!TextUtils.isEmpty(response)) {
                    routeDest = new BeanRoute();
                    parseContent.parseRoute(response, routeDest);

                    final ArrayList<BeanStep> step = routeDest.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest = new ArrayList<LatLng>();
                    lineOptionsDest = new PolylineOptions();

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest.addAll(path);
                    }
                    if (polyLineDest != null)
                        polyLineDest.remove();
                    lineOptionsDest.addAll(pointsDest);
                    lineOptionsDest.width(15);
                    lineOptionsDest.geodesic(true);
                    lineOptionsDest.color(getResources().getColor(
                            R.color.skyblue)); // #00008B rgb(0,0,139)

                    if (lineOptionsDest != null && googleMap != null) {
                        polyLineDest = googleMap.addPolyline(lineOptionsDest);
                        boundLatLang();
                    }
                }
                break;

            case AndyConstants.ServiceCode.DRAW_PATH_CLIENT:
                AndyUtils.removeCustomProgressDialog();
                AppLog.Log("JobFragment", "PATH Response : " + response);
                if (!TextUtils.isEmpty(response)) {
                    routeClient = new BeanRoute();
                    parseContent.parseRoute(response, routeClient);

                    final ArrayList<BeanStep> step = routeClient.getListStep();
                    pointsClient = new ArrayList<LatLng>();
                    lineOptionsClient = new PolylineOptions();
                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        pointsClient.addAll(path);
                    }
                    if (polyLineClient != null)
                        polyLineClient.remove();
                    lineOptionsClient.addAll(pointsClient);
                    lineOptionsClient.width(17);
                    lineOptionsClient.color(Color.BLUE);
                    if (lineOptionsClient != null && googleMap != null) {
                        polyLineClient = googleMap.addPolyline(lineOptionsClient);
                    }
                }
                break;
            case AndyConstants.ServiceCode.CANCEL_REQUEST:
                AndyUtils.removeCustomProgressDialog();
                LoginModel loginModel = taxiparseContent.getSingleObject(response, LoginModel.class);
                if (loginModel.success) {
                    ((MapActivity) getActivity()).addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                    mapActivity.display_toast(getResources().getString(R.string.trip_cancled));
                } else {
                    AndyUtils.showToast(getResources().getString(R.string.cancel_request_failed), R.id.coordinatorLayout, getActivity());
                    mapActivity.display_toast(getResources().getString(R.string.cancel_request_failed));
                }
                break;
            default:
                break;
        }
    }

    private void startElapsedTimer() {
        elapsedTimer = new Timer();
        elapsedTimer.scheduleAtFixedRate(new TimerRequestStatus(),
                AndyConstants.DELAY, ELAPSED_TIME_SCHEDULE);
    }

    private void stopElapsedTimer() {
        if (elapsedTimer != null) {
            elapsedTimer.cancel();
            elapsedTimer = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        initPreviousDrawPath();
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = mapActivity.getLayoutInflater().inflate(R.layout.info_window_layout, null);
                ((TextView) v).setText(marker.getTitle());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        addMarker();
    }

    private class TimerRequestStatus extends TimerTask {
        @Override
        public void run() {
            // isContinueRequest = false;
            AppLog.Log(TAG, "In elapsed time timer");
            mapActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (isVisible()) {
                        if (DriverApplication.preferenceHelper.getRequestTime() == AndyConstants.NO_TIME) {
                            DriverApplication.preferenceHelper.putRequestTime(System.currentTimeMillis());
                        }
//                        time = String.valueOf((Calendar.getInstance().getTimeInMillis() - DriverApplication.preferenceHelper.getRequestTime())
//                                / (1000 * 60));
//                        if (((Calendar.getInstance().getTimeInMillis() - DriverApplication.preferenceHelper.getRequestTime())
//                                / (1000 * 60)) < 0) {
//                            DriverApplication.preferenceHelper.putRequestTime(System.currentTimeMillis());
//                            time = "0.0";
//                        }


                        if (decimalFormat == null)
                            decimalFormat = new DecimalFormat("0.00");
                        time = decimalFormat.format(Double.parseDouble(DriverApplication.preferenceHelper.getDistanceTime())) + "";
                        tvJobTime.setText(time + " " + mapActivity.getResources().getString(R.string.text_mins));

                        //    DriverApplication.preferenceHelper.putDistanceTime(mUpdateLocationModel.mTime + "");


//                        if(stateModelwalkstarted!=null) {
//                            Double distancetime = Double.parseDouble(DriverApplication.preferenceHelper.getDistanceTime());
//                            Double distance = Double.parseDouble(String.valueOf(DriverApplication.preferenceHelper.getDistance()));
//                            Double Time = Double.parseDouble(String.valueOf(DriverApplication.preferenceHelper.getTotalTimerMin()));
//                            System.out.println("++" + distance + "++" + distancetime + "Time" + Time);
//
//                            String totalestimate=decimalFormat.format(stateModelwalkstarted.service_cost.base_price + (distancetime * stateModelwalkstarted.service_cost.price_per_unit_time)
//                                    + (distance * stateModelwalkstarted.service_cost.price_per_unit_distance)
//                                    + (Time * stateModelwalkstarted.service_cost.waiting_price));
//                            tvJobestimaterate.setText(totalestimate);
//                        }


//                        DriverApplication.preferenceHelper.putDistanceTime(time + "");
                    }
                }
            });

        }
    }

    @Override
    public void onLocationReceived(LatLng latlong) {
        if (googleMap == null) {
            return;
        }

        if (requestDetail == null) {
            return;
        }
        if (markerDriverLocation != null && markerClientLocation != null) {
            if (DriverApplication.preferenceHelper.isNavigate()) {
                drawPathToClient(markerDriverLocation.getPosition(), markerClientLocation.getPosition());
            }
        }
        getDestinationAddress(DriverApplication.preferenceHelper.getDestinationLatitude(),
                DriverApplication.preferenceHelper.getDestinationLongitude());

        if (requestDetail != null) {
            if (markerClientLocation == null) {
                markerClientLocation = googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(requestDetail.getClientLatitude() + ""),
                                Double.parseDouble(requestDetail.getClientLongitude() + "")))
                        .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client))));
                if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
                    markerClientLocation.setTitle(mapActivity.getResources()
                            .getString(R.string.job_start_location));
                } else {
                    markerClientLocation.setTitle(mapActivity.getResources()
                            .getString(R.string.client_location));
                }
                drawPath(markerClientLocation.getPosition(),
                        DriverApplication.preferenceHelper.getClientDestination());
            }
        } else {
            Log.e("Request Detail", "null Obj Empty");
        }
        if (latlong != null) {
            if (googleMap != null) {
                if (markerDriverLocation == null) {
                    markerDriverLocation = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latlong.latitude, latlong.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)))
                            .title(getResources().getString(R.string.my_location)));
                    boundLatLang();
                } else {
                    markerDriverLocation.setPosition(new LatLng(
                            latlong.latitude, latlong.longitude));
                    if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
                        drawTrip(new LatLng(latlong.latitude, latlong.longitude));
                        tvJobDistance.setText(decimalFormat.format(DriverApplication.preferenceHelper.getDistance()
                        ) + " " + DriverApplication.preferenceHelper.getUnit());
                    } else {
                        tvJobDistance.setText("0 " + DriverApplication.preferenceHelper.getUnit());
                    }
                }
            }
        }

    }


    private void drawTrip(LatLng latlng) {
        if (googleMap != null) {
            points.add(latlng);
            lineOptions = new PolylineOptions();
            lineOptions.addAll(points);
            lineOptions.width(15);
            lineOptions.geodesic(true);
            lineOptions.color(getResources().getColor(R.color.skyblue));
            googleMap.addPolyline(lineOptions);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        registerCancelReceiver();
        registerPaymentModeReceiver();
        registerDestinationReceiver();
        DriverApplication.preferenceHelper.putIsTripOn(true);
        if (isAddMarker && DriverApplication.preferenceHelper.isNavigate()) {
            if (jobStatus == AndyConstants.IS_WALKER_ARRIVED) {
                drawPathToClient(markerDriverLocation.getPosition(),
                        markerClientLocation.getPosition());
            }
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    private void registerCancelReceiver() {
        IntentFilter intentFilter = new IntentFilter("CANCEL_REQUEST");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AppLog.Log("JobFragment", "CANCEL_REQUEST");
                if (rDialog != null)
                    rDialog.dismiss();

                Toast.makeText(mapActivity, mapActivity.getString(R.string.trip_cancled_client), Toast.LENGTH_SHORT).show();
                if (loaded) {
                    soundPool.play(soundid, 1, 1, 0, 0, 1);
                }
                stopElapsedTimer();
                mapActivity.addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
                DriverApplication.preferenceHelper.putRequestId(AndyConstants.NO_REQUEST);
                DriverApplication.preferenceHelper.clearRequestData();
                soundPool.stop(soundid);
            }
        };
        mapActivity.registerReceiver(mReceiver, intentFilter);
    }

    private void ubregisterCancelReceiver() {
        if (mReceiver != null) {
            mapActivity.unregisterReceiver(mReceiver);
        }
    }

    private void registerPaymentModeReceiver() {
        IntentFilter intentFilter = new IntentFilter("PAYMENT_MODE");
        modeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AppLog.Log("JobFragment", "PAYMENT_MODE");
                if (JobFragment.this.isVisible()) {
                    setPaymentType();
                }
            }
        };
        mapActivity.registerReceiver(modeReceiver, intentFilter);
    }

    private void ubregisterPaymentModeReceiver() {
        if (modeReceiver != null) {
            mapActivity.unregisterReceiver(modeReceiver);
        }
    }

    private void unRegisterDestinationReceiver() {
        if (destReceiver != null) {
            mapActivity.unregisterReceiver(destReceiver);
        }
    }

    private void registerDestinationReceiver() {
        IntentFilter intentFilter = new IntentFilter("CLIENT_DESTINATION");
        destReceiver = new BroadcastReceiver() {
            private LatLng destLatLng;

            @Override
            public void onReceive(Context context, Intent intent) {
                AppLog.Log("JobFragment", "CLIENT_DESTINATION");
                destLatLng = DriverApplication.preferenceHelper.getClientDestination();
                drawPath(markerClientLocation.getPosition(), destLatLng);
            }
        };
        mapActivity.registerReceiver(destReceiver, intentFilter);
    }

    private void setPaymentType() {
        if (DriverApplication.preferenceHelper.getPaymentType() == AndyConstants.CASH)
            tvPaymentType.setText(getString(R.string.text_type_cash));
        else
            tvPaymentType.setText(getString(R.string.text_type_card));
    }

    private void drawPath(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        if (destination.latitude != 0) {
            setDestinationMarker(destination);
            boundLatLang();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, "http://maps.googleapis.com/maps/api/directions/json?origin="
                    + source.latitude + "," + source.longitude + "&destination=" + destination.latitude + ","
                    + destination.longitude + "&sensor=false");
            requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                    AndyConstants.ServiceCode.DRAW_PATH, this, this));
        }
    }

    private void setDestinationMarker(LatLng latLng) {
        if (latLng != null) {
            if (googleMap != null && this.isVisible()) {
                if (markerDestination == null) {
                    MarkerOptions opt = new MarkerOptions();
                    opt.position(latLng);
                    opt.icon(BitmapDescriptorFactory
                            .fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_client_destination)));
                    opt.title(getString(R.string.text_destination));
                    markerDestination = googleMap.addMarker(opt);
                } else {
                    markerDestination.setPosition(latLng);
                }
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
        error.printStackTrace();
        AndyUtils.showToast(mapActivity.getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
    }

    @Override
    public void onLocationReceived(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            if (googleMap != null) {
                if (markerDriverLocation == null) {
                    markerDriverLocation = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(AndyUtils.getBitmap(getActivity(), R.drawable.pin_driver)))
                            .title(getResources().getString(R.string.my_location)));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location
                            .getLongitude()), 16));
                    googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            if (!isAddMarker) {
                                isAddMarker = true;
                                if (DriverApplication.preferenceHelper.isNavigate())
                                    animateCamera(markerDriverLocation.getPosition());
                            }
                        }
                    });
                } else {
                    markerDriverLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        }
    }

    @Override
    public void onConntected(Bundle bundle) {
    }

    @Override
    public void onConntected(Location location) {
    }

    private void boundLatLang() {
        try {
            if (markerDriverLocation != null && markerClientLocation != null
                    && markerDestination != null) {
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                bld.include(new LatLng(markerDriverLocation.getPosition().latitude, markerDriverLocation.getPosition().longitude));
                bld.include(new LatLng(markerClientLocation.getPosition().latitude, markerClientLocation.getPosition().longitude));
                bld.include(new LatLng(markerDestination.getPosition().latitude, markerDestination.getPosition().longitude));
                LatLngBounds latLngBounds = bld.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
            } else if (markerDriverLocation != null && markerClientLocation != null) {
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                bld.include(new LatLng(markerDriverLocation.getPosition().latitude, markerDriverLocation.getPosition().longitude));
                bld.include(new LatLng(markerClientLocation.getPosition().latitude, markerClientLocation.getPosition().longitude));
                LatLngBounds latLngBounds = bld.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        latLngBounds, 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callPhone(String phoneNumbertoCall) {
        if (!TextUtils.isEmpty(phoneNumbertoCall)) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumbertoCall));
            startActivity(callIntent);
        } else {
            mapActivity.display_toast(mapActivity.getResources().getString(R.string.toast_number_not_found));
        }
    }

    public void RedirectToNavigation(double destinationLatitude, double destinationLongitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + destinationLatitude + "," + destinationLongitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void cancleRequest() {
        if (DriverApplication.preferenceHelper.getRequestId() != AndyConstants.NO_REQUEST) {
            if (!AndyUtils.isNetworkAvailable(getActivity())) {
                mapActivity.display_toast(mapActivity.getResources().getString(R.string.dialog_no_internet));
                return;
            }
            AppLog.Log("JobFragment", "Request ID : " + DriverApplication.preferenceHelper.getRequestId());
            AndyUtils.showCustomProgressDialog(getActivity(), "Cancel Request", null, true);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, AndyConstants.ServiceType.CANCEL_REQUEST);
            map.put(AndyConstants.Params.ID, String.valueOf(DriverApplication.preferenceHelper.getUserId()));
            map.put(AndyConstants.Params.TOKEN, String.valueOf(DriverApplication.preferenceHelper.getSessionToken()));
            map.put(AndyConstants.Params.REQUEST_ID, String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
            map.put(AndyConstants.Params.REASONFORREJECT, rejectreason);
            requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                    AndyConstants.ServiceCode.CANCEL_REQUEST, this, this));
        } else {
            ((MapActivity) getActivity()).addFragment(new ClientRequestFragmentNew(), false, AndyConstants.CLIENT_REQUEST_TAG, true);
            mapActivity.display_toast(getResources().getString(R.string.trip_cancled));
        }
    }

    private void showCancelDialog() {
        rDialog = new Dialog(getActivity());
        rDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rDialog.setContentView(R.layout.cancel_reason_trip);
        rDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_bg)));
        btnReasonConfirm = (MyFontButton) rDialog.findViewById(R.id.btnConfirmReason);
        btnReasonCancel = (MyFontButton) rDialog.findViewById(R.id.btnCancelReason);
        rejectreasonedit = (MyFontEdittextView) rDialog.findViewById(R.id.etrejectval);
        btnReasonConfirm.setOnClickListener(this);
        btnReasonCancel.setOnClickListener(this);
        rDialog.show();
        rDialog.setCanceledOnTouchOutside(false);
    }

    public void getLocationInfo(final String latitude, final String longitude, String url) {
        try {
            LatLng address = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            if (address != null) {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                        String.format(url, address.latitude + "", address.longitude + ""),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("OVER_QUERY_LIMIT") || response.getString("status").equals("REQUEST_DENIED")) {
                                        getLocationInfo(latitude, longitude, String.format(AndyConstants.ServiceType.GEOCODER_URL, latitude + "", longitude + "").replace("http", "https") + "&key=" + AndyConstants.DIRECTION_API_KEY);
                                    } else {
                                        tvDestinationAddress.setText(response.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                                        tvClientRating.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
                requestQueue.add(jsonObjReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void billDialogwhenInstantTrip(WalkerCompleteModel requestDetail) {
        mapActivity.showBillDialog(requestDetail.request.bill.basePrice + "",
                requestDetail.request.bill.total + "",
                requestDetail.request.bill.distanceCost + "",
                requestDetail.request.bill.timeCost + "",
                requestDetail.request.bill.distance + "",
                requestDetail.request.bill.time + "",
                requestDetail.request.bill.referralBonus + "",
                requestDetail.request.bill.promoBonus + "",
                requestDetail.request.chargeDetails.pricePerUnitTime + "",
                requestDetail.request.chargeDetails.distancePrice + "",
                requestDetail.request.bill.waiting_price + "",
                getString(R.string.text_confirm), false);
        DriverApplication.preferenceHelper.clearRequestData();
    }


    void doWalkComplete(WalkerCompleteModel walkerCompleteModel, Fragment mFragment) {
        if (walkerCompleteModel.success) {
            if (this.isVisible())
                if (DriverApplication.preferenceHelper.getInstantJobId() != DriverApplication.preferenceHelper.getRequestId()) {
                    btnCancelTrip.setVisibility(View.GONE);
                    mapActivity.addFragment(mFragment, false,
                            AndyConstants.FEEDBACK_FRAGMENT_TAG, true);
                } else {
                    billDialogwhenInstantTrip(walkerCompleteModel);
                }
            if (waitTripFragment != null)
                if (waitTripFragment.stopwatch != null)
                    waitTripFragment.stopwatch.stop();
        } else if (walkerCompleteModel.error_code == AndyConstants.INVALID_TOKEN2) {
            DriverApplication.preferenceHelper.Logout();
            mapActivity.goToMainActivity();
        }
    }


    private class doAsync extends AsyncTask<String, String, WalkerCompleteModel> {
        FeedbackFrament feedbackFrament = new FeedbackFrament();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pdia = new ProgressDialog(mapActivity);
//            pdia.setMessage("Loading...");
//            pdia.show();
        }

        @Override
        protected WalkerCompleteModel doInBackground(String... params) {
            WalkerCompleteModel walkerCompleteModel = taxiparseContent.getSingleObject(params[0], WalkerCompleteModel.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AndyConstants.REQUEST_DETAIL, walkerCompleteModel);
            walkerCompleteModel.bill.distance = DriverApplication.preferenceHelper.getDistance() + "";
            walkerCompleteModel.bill.unit = (DriverApplication.preferenceHelper.getUnit());
            feedbackFrament.setArguments(bundle);
            return walkerCompleteModel;
        }

        @Override
        protected void onPostExecute(WalkerCompleteModel walkerCompleteModel) {
            super.onPostExecute(walkerCompleteModel);
            if (walkerCompleteModel != null) {
                doWalkComplete(walkerCompleteModel, feedbackFrament);
            } else {
                Toast.makeText(mapActivity, mapActivity.getString(R.string.error_contact_server), Toast.LENGTH_SHORT).show();
            }
//            pdia.hide();
        }
    }


}

