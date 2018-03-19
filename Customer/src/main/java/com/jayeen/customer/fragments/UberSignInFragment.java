package com.jayeen.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.RegisterActivity;
import com.jayeen.customer.component.MyFontButton;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.newmodels.LoginModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.nplus.countrylist.CountryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class UberSignInFragment extends UberBaseFragmentRegister
        implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private MyFontEdittextView etEmail, etPassword, editcountry_code_signin;
    private MyFontButton btnSignIn;
    private ImageButton btnGPlus;
    private ImageView btnFb, flag_country_signin;
    public ImageView gplus;
    public CoordinatorLayout coordinatorLayout;
    // Gplus
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;
    private Button btnForgetPassowrd;
    private EditText etNumber;
    //    private TextView spCCode;
    private String country;
    private ArrayList<String> list;
    //    private RequestQueue requestQueue;
    int prefcount = 0;
    private String firsttimecheck = "";
    private String firstlogincheck;

    String TAG = "SignIn Fragment";
    private TaxiParseContent parseContent;
    private RegisterActivity registerActivity;
    CountryUtil countryUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, PlusOptions.builder().build())
                .addScope(scope).build();
        country = Locale.getDefault().getDisplayCountry();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parseContent = new TaxiParseContent(getActivity());
        registerActivity = (RegisterActivity) getActivity();
        activity.setTitle(registerActivity.getString(R.string.text_signin));
        activity.setIconMenu(R.drawable.taxi);
        activity.btnSosNotification.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.login, container, false);
        etEmail = (MyFontEdittextView) view.findViewById(R.id.etEmail);
        etEmail.addTextChangedListener(emailPhoneNumberWatcher);
        etPassword = (MyFontEdittextView) view.findViewById(R.id.etPassword);
        btnSignIn = (MyFontButton) view.findViewById(R.id.btnSignIn);
        btnFb = (ImageView) view.findViewById(R.id.fbloginimage);
        gplus = (ImageView) view.findViewById(R.id.sign_in_button);
        etNumber = (EditText) view.findViewById(R.id.etNumber);
        btnForgetPassowrd = (Button) view.findViewById(R.id.btnForgetPassword);
        flag_country_signin = (ImageView) view.findViewById(R.id.flag_country_signin);
        editcountry_code_signin = (MyFontEdittextView) view.findViewById(R.id.country_code_signin);
//        spCCode = (TextView) view.findViewById(R.id.spCCode);
        flag_country_signin.setOnClickListener(this);
        btnForgetPassowrd.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        gplus.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        CustomerApplication.preferenceHelper.putFirsttimeCheck(firsttimecheck);
        AppLog.Log(Const.TAG, country);

        list = parseContent.parseCountryCodes();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).contains(country)) {
//                editcountry_code_signin.setText((list.get(i).substring(0,
//                        list.get(i).indexOf(" "))));
//            }
//        }
//        if (TextUtils.isEmpty(editcountry_code_signin.getText())) {
//            editcountry_code_signin.setText((list.get(0).substring(0, list.get(0).indexOf(" "))));
        countryUtil = new CountryUtil(getActivity(), Const.COUNTRY_CODE);
        countryUtil.initUI(editcountry_code_signin, flag_country_signin);
        countryUtil.initCodes(getActivity());
//        }

        return view;
    }

    TextWatcher emailPhoneNumberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable e) {
            if (e.toString().contains("@") || e.toString().contains(".co") || e.toString().contains(".com")) {
                changeToPhoneNumber(false);

            } else {

                if (e.toString().matches("[a-zA-Z]")) //only alphanumeric
                    changeToPhoneNumber(false);
                else if (e.toString().isEmpty())
                    changeToPhoneNumber(false);
                else if (e.toString().matches("[0-9]+"))//only number
                    changeToPhoneNumber(true);
            }
        }
    };

    void changeToPhoneNumber(boolean isPhonenumber) {
        flag_country_signin.setVisibility(isPhonenumber ? View.VISIBLE : View.GONE);
        editcountry_code_signin.setVisibility(isPhonenumber ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.FRAGMENT_SIGNIN;
        activity.actionBar.setTitle(registerActivity.getString(R.string.text_signin_small));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbloginimage:
                ((RegisterActivity) getActivity()).checkandRegisterFacebook();
                break;
            case R.id.sign_in_button:
                ((RegisterActivity) getActivity()).signIn();
                break;
            case R.id.btnSignIn:
                if (isValidate()) {
                    login();
                }
                break;
            case R.id.btnForgetPassword:
                activity.addFragment(new ForgetPasswordFragment(), true,
                        Const.FOREGETPASS_FRAGMENT_TAG);
                break;
            case R.id.flag_country_signin:
                Builder countryBuilder = new Builder(activity);
                countryBuilder.setTitle(registerActivity.getString(R.string.dialog_title_country_codes));
                final String[] array = new String[list.size()];
                list.toArray(array);
                countryBuilder.setItems(array,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editcountry_code_signin.setText(array[which].substring(0,
                                        array[which].indexOf(" ")));
                            }
                        }).show();
                break;
            default:
                break;
        }
    }


    private List<String> getShareApplication() {
        List<String> mList = new ArrayList<String>();
        mList.add("com.facebook.katana");
        mList.add("com.whatsapp");
        mList.add("com.google.android.apps.plus");
        return mList;

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        AndyUtils.removeCustomProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                activity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null,
                        0, 0, 0, Const.FRAGMENT_SIGNIN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                mSignInClicked = false;
                AndyUtils.removeCustomProgressDialog();
            } else {
                AndyUtils.removeCustomProgressDialog();
                AndyUtils.showToast(registerActivity.getString(R.string.signin_failed), R.id.coordinatorLayout, getActivity());
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            AndyUtils.showToast(registerActivity.getString(R.string.signin_failed), R.id.coordinatorLayout, getActivity());
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
    }

    @Override
    protected boolean isValidate() {
        String msg = null;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
                /*&& TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {*/
            msg = registerActivity.getString(R.string.text_enter_email);
        } else if (editcountry_code_signin.getVisibility() == View.VISIBLE && TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {
//            if (TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {
            msg = registerActivity.getString(R.string.txt_select_country);
//            }
        } else if (!TextUtils.isEmpty(etEmail.getText().toString()) && editcountry_code_signin.getVisibility() != View.VISIBLE && !AndyUtils.eMailValidation(etEmail.getText().toString())) {
//            if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {
            msg = registerActivity.getString(R.string.text_enter_valid_email);
//            }
        } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
            msg = registerActivity.getString(R.string.text_enter_password);
        }
        if (msg == null)
            return true;
        AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
        return false;
    }

    private void login() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(registerActivity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(activity,
                registerActivity.getString(R.string.text_signing), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        if (editcountry_code_signin.getVisibility() == View.VISIBLE) {
            map.put(Const.Params.EMAIL, editcountry_code_signin.getText().toString().trim() + etEmail.getText().toString().replaceAll(" ", ""));
        } else {
            map.put(Const.Params.EMAIL, etEmail.getText().toString());
        }
        map.put(Const.Params.PASSWORD, etPassword.getText().toString());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        // new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.LOGIN, this, this));

    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        super.onTaskCompleted(response, serviceCode);
        switch (serviceCode) {
            case Const.ServiceCode.LOGIN:
                LoginModel loginModel = parseContent.getSingleObject(response, LoginModel.class);
                if (loginModel == null) {
                    AndyUtils.showToast(activity.getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                    break;
                }
                if (loginModel.success) {
                    CustomerApplication.preferenceHelper.putPassword(etPassword.getText().toString());
                    registerActivity.navigateToLandingPage(loginModel);
                } else {
                    AndyUtils.showToast(parseContent.ErrorResponse(loginModel.error_code), R.id.coordinatorLayout, activity);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean OnBackPressed() {
        activity.goToMainActivity();
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(activity.getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
    }


}
