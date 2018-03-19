package com.jayeen.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.base.BaseRegisterFragment;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.ParseContent;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontEdittextView;
import com.nplus.countrylist.CountryUtil;

import java.util.HashMap;

import static com.jayeen.driver.DriverApplication.requestQueue;

public class LoginFragment extends BaseRegisterFragment implements
        OnClickListener,
        AsyncTaskCompleteListener {
    private MyFontEdittextView etLoginEmail, etLoginPassword, editcountry_code_signin;
    private ImageView flag_country_signin;
    private ImageButton btnFb, btnGplus, btnActionMenu;
    private ParseContent parseContent;
    private TaxiParseContent taxiparseContent;
    private boolean mSignInClicked, mIntentInProgress;
    private final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 0;
    CountryUtil countryUtil;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View loginFragmentView = inflater.inflate(R.layout.fragment_login,
                container, false);
        etLoginEmail = (MyFontEdittextView) loginFragmentView
                .findViewById(R.id.etLoginEmail);
        etLoginEmail.addTextChangedListener(emailPhoneNumberWatcher);
        etLoginPassword = (MyFontEdittextView) loginFragmentView
                .findViewById(R.id.etLoginPassword);
        btnActionMenu = (ImageButton) loginFragmentView
                .findViewById(R.id.btnActionMenu);
        editcountry_code_signin = (MyFontEdittextView) loginFragmentView
                .findViewById(R.id.country_code_signin);

        flag_country_signin = (ImageView) loginFragmentView
                .findViewById(R.id.flag_country_signin);

        loginFragmentView.findViewById(R.id.tvLoginForgetPassword)
                .setOnClickListener(this);
        loginFragmentView.findViewById(R.id.tvLoginSignin).setOnClickListener(
                this);
        countryUtil = new CountryUtil(getActivity(), AndyConstants.COUNTRY_CODE);
        countryUtil.initUI(editcountry_code_signin, flag_country_signin);
        countryUtil.initCodes(getActivity());
        return loginFragmentView;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerActivity.actionBar.show();
        registerActivity.setActionBarTitle(getResources().getString(
                R.string.text_signin));
        registerActivity.btnActionInfo.setVisibility(View.INVISIBLE);
        registerActivity.setActionBarIcon(R.drawable.taxi);
        parseContent = new ParseContent(registerActivity);
        taxiparseContent = new TaxiParseContent(registerActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLoginForgetPassword:
                registerActivity.addFragment(new ForgetPasswordFragment(), true,
                        AndyConstants.FOREGETPASS_FRAGMENT_TAG, true);
                break;

            case R.id.tvLoginSignin:
                if (etLoginEmail.getText().length() == 0) {
                    AndyUtils.showToast(getResources().getString(R.string.text_enter_email_phone), R.id.coordinatorLayout, registerActivity);
                    return;
                } else if (editcountry_code_signin.getVisibility() == View.VISIBLE && TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {
//                    if (TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {
                    AndyUtils.showToast(registerActivity.getString(R.string.txt_select_country), R.id.coordinatorLayout, registerActivity);
//                    }
                } else if (editcountry_code_signin.getVisibility() != View.VISIBLE && !AndyUtils.eMailValidation(etLoginEmail.getText().toString())) {
//                    if (TextUtils.isEmpty(editcountry_code_signin.getText().toString())) {
                    AndyUtils.showToast(registerActivity.getString(R.string.error_valid_email), R.id.coordinatorLayout, registerActivity);
//                    }
                } else if (etLoginPassword.getText().length() == 0) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_password), R.id.coordinatorLayout, registerActivity);
                    return;
                } else {
                    login();
                }

                break;

            default:
                break;
        }
    }

    private void login() {
        if (!AndyUtils.isNetworkAvailable(registerActivity)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, registerActivity);
            return;
        }
        AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
                .getString(R.string.progress_dialog_sign_in), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
        map.put(AndyConstants.Params.EMAIL, editcountry_code_signin.getVisibility() == View.VISIBLE ?
                (editcountry_code_signin.getText().toString().replaceAll(" ", "") + etLoginEmail.getText().toString().replaceAll(" ", ""))
                : etLoginEmail.getText().toString());
        map.put(AndyConstants.Params.PASSWORD, etLoginPassword.getText().toString());
        map.put(AndyConstants.Params.DEVICE_TYPE,AndyConstants.DEVICE_TYPE_ANDROID);
        map.put(AndyConstants.Params.DEVICE_TOKEN, DriverApplication.preferenceHelper.getDeviceToken());
        map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,AndyConstants.ServiceCode.LOGIN, this, this));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            if (resultCode != registerActivity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;
        } else {
            AppLog.Log("TAG", "on activity result facebook");
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerActivity.currentFragment = AndyConstants.LOGIN_FRAGMENT_TAG;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndyUtils.removeCustomProgressDialog();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(TAG, response);
        switch (serviceCode) {
            case AndyConstants.ServiceCode.LOGIN:
                LoginModel loginModel = taxiparseContent.getSingleObject(response, LoginModel.class);
                AppLog.Log(TAG, "response:" + response);
                if (loginModel == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, registerActivity);
                    return;
                }
                if (!loginModel.success) {
                    AndyUtils.showToast(loginModel.erro, R.id.coordinatorLayout, registerActivity);
                    return;
                }
                if (taxiparseContent.isSuccessWithId(loginModel)) {
                    taxiparseContent.parseUserAndStoreToDb(loginModel.driver);
                    DriverApplication.preferenceHelper.putPassword(etLoginPassword.getText().toString());
                    startActivity(new Intent(registerActivity, MapActivity.class));
                    registerActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    registerActivity.finish();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AndyUtils.removeCustomProgressDialog();
        error.printStackTrace();
        AndyUtils.showToast(registerActivity.getResources().getString(R.string.error_contact_server), R.id.coordinatorLayout, registerActivity);
    }

}
