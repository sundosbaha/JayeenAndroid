package com.jayeen.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.dev.sacot41.scviewpager.DotsView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.fragments.CheckOTPCodeFragment;
import com.jayeen.customer.fragments.UberRegisterFragment;
import com.jayeen.customer.fragments.UberSignInFragment;
import com.jayeen.customer.newmodels.LoginModel;
import com.jayeen.customer.newmodels.RegisterModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.MultiPartRequester;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.nplus.countrylist.CountryUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;
import static com.jayeen.customer.utils.Const.Params.ID;
import static com.jayeen.customer.utils.Const.Params.SOCIAL_UNIQUE_ID;

public class RegisterActivity extends ActionBarBaseActivitiy implements AsyncTaskCompleteListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int NUM_PAGES = 4;
    public static String Login_type;
    public DotsView mDotsView;
    ImageView fbloginImage;
    ImageView gplus;
    CallbackManager callbackManager;
    String social_id;
    String type;
    String globalfirstName;
    String globallastName;
    String regEmail;
    //    RequestQueue requestQueue;
    public static String mobileNumber = "";
    String country = "Costa Rica";
    public CountryUtil Cutil;
    TextView tv_username;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    public TaxiParseContent taxiParseContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.register_activity);
        taxiParseContent = new TaxiParseContent(this);
        generateKeyHash();
        fbloginImage = (ImageView) findViewById(R.id.fbloginimage);
        gplus = (ImageView) findViewById(R.id.sign_in_button);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.drawable.dot_selected, R.drawable.dot_unselected);
        mDotsView.setNumberOfPage(NUM_PAGES);
        Cutil = new CountryUtil(this, Const.COUNTRY_CODE);
        fbloginImage.setOnClickListener(this);
        gplus.setOnClickListener(this);
        tv_username = (TextView) findViewById(R.id.tv_username);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setFacebookData(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                AppLog.Log("RegisterActivity", exception.getMessage());
            }
        });
        setIcon(R.drawable.back);
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            Log.i("TAG", "Actionbar Height" + actionBarHeight);
        }

        if (getIntent().getBooleanExtra("isSignin", false)) {
            gotSignInFragment();
        } else {
            goToRegisterFragment();
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                break;

            case R.id.fbloginimage:
                checkandRegisterFacebook();
                break;
            case R.id.btnedituserno:
                CustomerApplication.preferenceHelper.putIsRegEditCheck(false);
                activity.onBackPressed();
                break;
            default:
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void gotSignInFragment() {
        mDotsView.setVisibility(View.GONE);
        UberSignInFragment signInFrag = new UberSignInFragment();
        clearBackStack();
        addFragment(signInFrag, false, Const.FRAGMENT_SIGNIN);
    }

    private void goToRegisterFragment() {
        mDotsView.setVisibility(View.VISIBLE);
        UberRegisterFragment regFrag = new UberRegisterFragment();
        clearBackStack();
        addFragment(regFrag, true, Const.FRAGMENT_REGISTER);
    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    public void showKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
    }

    public void checkandRegisterFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            String firstName = acct.getGivenName();
            String lastName = acct.getFamilyName();
            social_id = acct.getId();
            type = Const.SOCIAL_GOOGLE;
            globalfirstName = firstName;
            globallastName = lastName;
            regEmail = email;
            logingplus(social_id, Const.SOCIAL_GOOGLE, regEmail);
        } else {
            // Signed out
        }
    }


    public void generateKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key new ", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.i("Response", response.toString());
                            String email = response.getJSONObject().optString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");
                            String userid = response.getJSONObject().getString("id");
                            Profile profile = Profile.getCurrentProfile();
                            if (Profile.getCurrentProfile() != null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }
                            social_id = userid;
                            type = Const.SOCIAL_FACEBOOK;
                            globalfirstName = firstName;
                            globallastName = lastName;
                            regEmail = email;
                            loginSocial(userid, Const.SOCIAL_FACEBOOK);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginSocial(String id, String loginType) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, RegisterActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, getResources().getString(R.string.text_signin), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, loginType);
        Login_type = Const.SOCIAL_FACEBOOK;
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));


    }

    private void logingplus(String id, String loginType, String EMAIL) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, RegisterActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.text_signin), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.EMAIL, Const.Params.EMAIL);
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, loginType);

        Login_type = Const.SOCIAL_GOOGLE;
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        super.onTaskCompleted(response, serviceCode);

        switch (serviceCode) {
            case Const.ServiceCode.LOGIN:
                AndyUtils.removeCustomProgressDialog();
                LoginModel loginModel = taxiParseContent.getSingleObject(response, LoginModel.class);
                if(loginModel==null){
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                    break;
                }
                if (loginModel.success) {
                    AndyUtils.removeCustomProgressDialog();
                    navigateToLandingPage(loginModel);
                } else if (loginModel.error_code == 600) {
                    if (Login_type == Const.SOCIAL_GOOGLE) {
                        showSocialLayoutDialog_gplus();
                    } else if (Login_type == Const.SOCIAL_FACEBOOK) {
                        showSocialLayoutDialog();
                    }
                } else {
                    AndyUtils.showToast(taxiParseContent.ErrorResponse(loginModel.error_code), R.id.coordinatorLayout, this);
                }
                break;

            case Const.ServiceCode.REGISTER:
                AndyUtils.removeCustomProgressDialog();
                RegisterModel registerModel = taxiParseContent.getSingleObject(response, RegisterModel.class);

                if (registerModel.success) {
                    AndyUtils.removeCustomProgressDialog();
                    if (!(CustomerApplication.preferenceHelper.getIsOTPVerify())) {
                        CustomerApplication.preferenceHelper.putUserId(registerModel.user.id + "");
                        CustomerApplication.preferenceHelper.putSessionToken(registerModel.user.token);
                        CustomerApplication.preferenceHelper.putLoginBy(registerModel.user.loginBy);

                        goToOTPVerifyFragment(registerModel.user.id + "", registerModel.user.loginBy);
                    }
                } else {
                    AndyUtils.showToast(taxiParseContent.ErrorResponse(registerModel.errorCode), R.id.coordinatorLayout, this);
                    if (Login_type == Const.SOCIAL_GOOGLE) {
                        showSocialLayoutDialog_gplus();
                    } else if (Login_type == Const.SOCIAL_FACEBOOK) {
                        showSocialLayoutDialog();
                    }
                }
                break;
        }
    }


    private void goToOTPVerifyFragment(String id, String LoginBy) {
        CheckOTPCodeFragment reffralCodeFragment = new CheckOTPCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString("LoginBy", LoginBy);
        reffralCodeFragment.setArguments(bundle);
        clearBackStack();
        addFragment(reffralCodeFragment, false,
                Const.FRAGMENT_OTP);
    }

    public void showSocialLayoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_register));
        builder.setCancelable(false);
        Cutil.initCodes(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.layout_social_login_feilds, null, false);
        final MyFontEdittextView Emailinput = (MyFontEdittextView) viewInflated.findViewById(R.id.socialemail);
        final EditText MobileInput = (EditText) viewInflated.findViewById(R.id.phone);
        final ImageView img_spinner = (ImageView) viewInflated.findViewById(R.id.img_spinner);
        Cutil.initUI(MobileInput, img_spinner);
        builder.setView(viewInflated);
        Emailinput.setText(regEmail);
        MobileInput.setText(mobileNumber);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(Emailinput.getText())) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
                } else if (!AndyUtils.eMailValidation((Emailinput.getText().toString()))) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_valid_email), Toast.LENGTH_SHORT).show();
                } else if (!Cutil.validate()) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.text_enter_mobile_number), Toast.LENGTH_SHORT).show();
                } else {
                    String formattedPhone = MobileInput.getText().toString().replace(" ", "");
                    registerSocial(social_id, Const.SOCIAL_FACEBOOK, globalfirstName, globallastName, Emailinput.getText().toString(), formattedPhone);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showSocialLayoutDialog_gplus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(getString(R.string.text_register));
        builder.setCancelable(false);
        Cutil.initCodes(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.layout_social_login_feilds, null, false);
        final MyFontEdittextView Emailinput = (MyFontEdittextView) viewInflated.findViewById(R.id.socialemail);
        final EditText MobileInput = (EditText) viewInflated.findViewById(R.id.phone);
        final ImageView img_spinner = (ImageView) viewInflated.findViewById(R.id.img_spinner);
        Cutil.initUI(MobileInput, img_spinner);
        builder.setView(viewInflated);
        Emailinput.setText(regEmail);
        MobileInput.setText(mobileNumber);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(Emailinput.getText())) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
                } else if (!AndyUtils.eMailValidation((Emailinput.getText().toString()))) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_valid_email), Toast.LENGTH_SHORT).show();
//                } else if (!Cutil.validate()) {
                } else if (TextUtils.isEmpty(MobileInput.getText())) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.text_enter_mobile_number), Toast.LENGTH_SHORT).show();
                } else {
                    String formattedPhone = MobileInput.getText().toString().replace(" ", "");
                    registerSocialgplus(social_id, Const.SOCIAL_GOOGLE, globalfirstName, globallastName, Emailinput.getText().toString(), formattedPhone);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (!isFinishing())
            builder.show();
    }

    private void registerSocial(final String id, final String type, String firstName, String lastName, String regEmail, String Phonenumber) {
        String filePath = "";
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.text_registering), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FIRSTNAME, firstName);
        map.put(Const.Params.LAST_NAME, lastName);
        map.put(Const.Params.EMAIL, regEmail);
        map.put(SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.PHONE, Phonenumber);
        map.put(Const.Params.OTPTYPE, "true");
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.STATE, "");
        map.put(Const.Params.COUNTRY, "");
        map.put(Const.Params.LOGIN_BY, Const.SOCIAL_FACEBOOK);
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.text_loginreg), true, null);
        int prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        new MultiPartRequester(this, map, Const.ServiceCode.REGISTER, this, this);

    }

    private void registerSocialgplus(final String id, final String type, String firstName, String lastName, String regEmail, String Phonenumber) {
        String filePath = "";
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.text_registering), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FIRSTNAME, firstName);
        map.put(Const.Params.LAST_NAME, lastName);
        map.put(Const.Params.EMAIL, regEmail);
        map.put(SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.PHONE, Phonenumber);
        map.put(Const.Params.OTPTYPE, "true");
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.STATE, "");
        map.put(Const.Params.COUNTRY, "");
        map.put(Const.Params.LOGIN_BY, Const.SOCIAL_GOOGLE);
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.text_loginreg), true, null);
        int prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        new MultiPartRequester(this, map, Const.ServiceCode.REGISTER, this, this);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void navigateToLandingPage(LoginModel loginModel) {
        CustomerApplication.preferenceHelper.putObject(Const.USER_DETAILS, loginModel.user);
        taxiParseContent.localUserData(loginModel.user);
        CustomerApplication.preferenceHelper.putIsOTPVerify(loginModel.user.isOtp);
        CustomerApplication.preferenceHelper.putUserId(loginModel.user.id + "");
        CustomerApplication.preferenceHelper.putSessionToken(loginModel.user.token);

        startActivity(new Intent(this, MainDrawerActivity.class));
        finish();
    }

}
