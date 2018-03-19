package com.jayeen.customer.fragments;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.RegisterActivity;
import com.jayeen.customer.component.MyFontTextView;
import com.jayeen.customer.newmodels.RegisterModel;
import com.jayeen.customer.parse.MultiPartRequester;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nplus.countrylist.Country;
import com.nplus.countrylist.CountryAdapter;
import com.nplus.countrylist.CountryUtil;
import com.nplus.countrylist.DialogCountryAdapter;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class UberRegisterFragment extends UberBaseFragmentRegister
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button btnNext;
    private ImageButton btnGPlus, btnFb;
    private EditText etFName, etLName, etEmail, etBio, etZipCode, etAddress,
            etPassword;
    private CircularImageView ivProPic;
    int prefcount = 0;
    String TAG = "Register Fragment";

    // Gplus
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;
    private Uri uri = null;
    private String imageFilePath;
    private String filePath = null;
    private Bitmap bmp;
    private String type = Const.MANUAL;
    private String socialId = "";
    private String socialUrl;
    ArrayList<String> list;
    private String country;
    private PopupWindow registerInfoPopup;
    private MyFontTextView tvPopupMsg;
    private ImageView btnRegisterEmailInfo;
    private static final int PERMISSION_REQUEST_CODE = 200;

    // private AQuery aQuery;
    //	android.app.AlertDialog dialog_countrycode;
    Dialog dialog_countrycode;
    private CountryAdapter arrayAdapter;
    private DialogCountryAdapter arrayAdapter1;
    private ArrayList<Country> ctdata;
    private CountryUtil mCountryUtil;
    private EditText MphoneUtli;
    private ImageView mImage_Spinner_View;
    private TaxiParseContent parseContent;
    private RegisterActivity registerActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, PlusOptions.builder().build())
                .addScope(scope).build();
        country = Locale.getDefault().getDisplayCountry();
    }

    @Override
    public void onStart() {
        // 
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parseContent = new TaxiParseContent(getActivity());
        registerActivity = (RegisterActivity) getActivity();
        activity.setTitle(activity.getString(R.string.text_register));
        activity.setIconMenu(R.drawable.taxi);
        activity.btnSosNotification.setVisibility(View.GONE);
        activity.mDotsView.selectDot(0);
        View view = inflater.inflate(R.layout.register, container, false);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etFName = (EditText) view.findViewById(R.id.etFName);
        etLName = (EditText) view.findViewById(R.id.etLName);
        etBio = (EditText) view.findViewById(R.id.etBio);
        etZipCode = (EditText) view.findViewById(R.id.etZipCode);
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        ivProPic = (CircularImageView) view.findViewById(R.id.ivChooseProPic);
        MphoneUtli = (EditText) view.findViewById(R.id.phone);
        mImage_Spinner_View = (ImageView) view.findViewById(R.id.img_spinner);
        btnRegisterEmailInfo = (ImageView) view.findViewById(R.id.btnRegisterEmailInfo);
        btnRegisterEmailInfo.setOnClickListener(this);
        mCountryUtil = new CountryUtil(getActivity(), Const.COUNTRY_CODE);
        mCountryUtil.initUI(MphoneUtli, mImage_Spinner_View);
        mCountryUtil.initCodes(getActivity());
        ivProPic.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // 
        super.onActivityCreated(savedInstanceState);
        country = "Costa Rica";
        AppLog.Log(Const.TAG, country);
        list = parseContent.parseCountryCodes();
        LayoutInflater inflate = LayoutInflater.from(activity);
        RelativeLayout layout = (RelativeLayout) inflate.inflate(
                R.layout.popup_info_window, null);
        tvPopupMsg = (MyFontTextView) layout.findViewById(R.id.tvPopupMsg);
        registerInfoPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        layout.setOnClickListener(this);
        registerInfoPopup.setBackgroundDrawable(new BitmapDrawable());
        registerInfoPopup.setOutsideTouchable(true);

        if (CustomerApplication.preferenceHelper.getIsRegEditCheck()) {
            String E_email = CustomerApplication.preferenceHelper.getOTPEditEmail() == null ? "" : CustomerApplication.preferenceHelper.getOTPEditEmail();
            String E_fname = CustomerApplication.preferenceHelper.getOTPEditFname() == null ? "" : CustomerApplication.preferenceHelper.getOTPEditFname();
            String E_lname = CustomerApplication.preferenceHelper.getOTPEditLname() == null ? "" : CustomerApplication.preferenceHelper.getOTPEditLname();
            String E_cntrycode = CustomerApplication.preferenceHelper.getEditCountryCode() == null ? "" : CustomerApplication.preferenceHelper.getEditCountryCode();
            String E_password = CustomerApplication.preferenceHelper.getPassword() == null ? "" : CustomerApplication.preferenceHelper.getPassword();
            String E_phone = CustomerApplication.preferenceHelper.getOTPEditPhone() == null ? "" : CustomerApplication.preferenceHelper.getOTPEditPhone();
            String newph = CustomerApplication.preferenceHelper.getOTPEditPhone() == null ? "" : CustomerApplication.preferenceHelper.getOTPEditPhone();
            String newStringPhone = newph.replace(E_cntrycode, "");

            Log.i("TAG", "Email:" + E_email + "Phone" + E_phone + "Country Code" + E_cntrycode);
            etFName.setText(CustomerApplication.preferenceHelper.getOTPEditFname());
            etLName.setText(CustomerApplication.preferenceHelper.getOTPEditLname());
            etEmail.setText(CustomerApplication.preferenceHelper.getOTPEditEmail());
            etPassword.setText(CustomerApplication.preferenceHelper.getOTPEditPasswrd());
            MphoneUtli.setText(newStringPhone);
            if ((int) CustomerApplication.preferenceHelper.GetFlagId() != 0) {
                mImage_Spinner_View.setImageResource((int) CustomerApplication.preferenceHelper.GetFlagId());
                mImage_Spinner_View.setTag(CustomerApplication.preferenceHelper.GetFlagId());
            }

            Picasso.with(getActivity())
                    .load(imageFilePath)
                    .placeholder(R.drawable.default_user)   // optional
//                    .error(R.drawable.ic_car)      // optional
//                    .resize(159,159)                        // optional
                    .into(ivProPic);
        }


    }

    @Override
    public void onResume() {
        activity.currentFragment = Const.FRAGMENT_REGISTER;
        super.onResume();
        activity.actionBar.setTitle(activity.getString(R.string.text_register_small));
        mCountryUtil.initCodes(getActivity());
    }


    @Override
    public void onClick(View v) {
        // 
        socialUrl = null;
        switch (v.getId()) {
            case R.id.btnNext:
                if (isValidate()) {
                    if (!checkPermission_OTP()) {
                        requestPermissionOTP();
                        break;
                    }
                    if (CustomerApplication.preferenceHelper.getIsRegEditCheck()) {
                        updateSimpleProfile(Const.MANUAL);
                    } else {
                        register(type, socialId);
                    }
                }
                break;//
            case R.id.ivChooseProPic:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // FOR API 6.0 DEVICES
                    if (!checkPermission_ProfilePic()) {    // check for runtime permission
                        requestPermission();
                    } else {  //permission granted
                        showPictureDialog();
                    }
                } else { // LESS THAN API 6.0 DEVICES
                    showPictureDialog();
                }
                break;
            case R.id.btnRegisterEmailInfo:
                if (registerInfoPopup.isShowing())
                    registerInfoPopup.dismiss();
                else {
                    registerInfoPopup.showAsDropDown(btnRegisterEmailInfo);
                }
                break;
            default:
                break;
        }
    }


    private void updateSimpleProfile(String type) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(activity,
                activity.getString(R.string.progress_update_profile),
                false, null);

        if (type.equals(Const.MANUAL)) {
            AppLog.Log(TAG, "Simple Profile update method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
            map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
            map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
            map.put(Const.Params.FIRSTNAME, etFName.getText().toString());
            map.put(Const.Params.LAST_NAME, etLName.getText().toString());
            map.put(Const.Params.EMAIL, etEmail.getText().toString());
            map.put(Const.Params.PICTURE, filePath);
            map.put(Const.Params.PHONE, MphoneUtli.getText().toString().replace(" ", ""));
            new MultiPartRequester(activity, map, Const.ServiceCode.UPDATE_PROFILE, this, this);
        }
    }


    private boolean checkPermission_ProfilePic() {
        int result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(activity, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermission_OTP() {
        int result1 = ContextCompat.checkSelfPermission(activity, READ_SMS);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private void requestPermissionOTP() {
        ActivityCompat.requestPermissions(activity, new String[]{READ_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        Log.i("MAINDRAWERRESUME", "Camera &  External Storage Granted");
                        //Toast.makeText(activity, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MAINDRAWERRESUME", "Any one permission not granted Camera OR External Storage Granted");
                        //Toast.makeText(activity, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                Log.i("MAINDRAWERRESUME", "External Storage Permission Check");
                                showMessageOKCancel(activity.getString(R.string.text_allow_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            } else if (shouldShowRequestPermissionRationale(CAMERA)) {
                                Log.i("MAINDRAWERRESUME", "Else camera Permission Check");
                                showMessageOKCancel(activity.getString(R.string.text_allow_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.text_journey_ok), okListener)
                .setNegativeButton(activity.getString(R.string.text_cancel), null)
                .create()
                .show();
    }


    private void showPictureDialog() {
        Builder dialog = new Builder(activity);
        dialog.setTitle(activity.getString(R.string.text_choosepicture));
        String[] items = {activity.getString(R.string.text_gallary),
                activity.getString(R.string.text_camera)};

        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;

                }
            }
        });
        if (!activity.isFinishing())
            dialog.show();
    }

    private void resolveSignInError() {

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                activity.startIntentSenderForResult(mConnectionResult
                                .getResolution().getIntentSender(), RC_SIGN_IN, null,
                        0, 0, 0, Const.FRAGMENT_REGISTER);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent. Return to the
                // default
                // state and attempt to connect to get an updated
                // ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // 

        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the
            // user clicks
            // 'sign-in'.

            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all

                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        // 
        AndyUtils.removeCustomProgressDialog();
        mSignInClicked = false;
        //btnGPlus.setEnabled(false);
        //btnFb.setEnabled(false);
        /*String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);

		String personName = currentPerson.getDisplayName();

		String personPhoto = currentPerson.getImage().toString();
		String personGooglePlusProfile = currentPerson.getUrl();
		socialId = currentPerson.getId();
		// etPassword.setEnabled(false);
		etPassword.setVisibility(View.GONE);
		etEmail.setText(email);
		type = Const.SOCIAL_GOOGLE;
		// etFName.setText(personName);
		if (personName.contains(" ")) {
			String[] split = personName.split(" ");
			etFName.setText(split[0]);
			etLName.setText(split[1]);
		} else {
			etFName.setText(personName);
		}
		if (!TextUtils.isEmpty(personPhoto)
				|| !personPhoto.equalsIgnoreCase("null")) {
			socialUrl = personPhoto;
			AQuery aQuery = new AQuery(activity);
			aQuery.id(ivProPic).image(personPhoto, getAqueryOption());
		} else { 
			socialUrl = null;
		}*/

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // 
        //mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // 
        super.onStop();
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
        /*if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
		} */

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i(TAG,"On Activity Result request code"+requestCode+"result code"+resultCode);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode != Activity.RESULT_OK) {
                    mSignInClicked = false;
                    AndyUtils.removeCustomProgressDialog();
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;

            default:

			/*mSimpleFacebook.onActivityResult(activity, requestCode, resultCode,
                    data);
			if (mSimpleFacebook.isLogin()) {
				getProfile();
			} else {
				Toast.makeText(activity, "facebook login failed",
						Toast.LENGTH_SHORT).show();
			}*/
                //Toast.makeText(activity,"Inside activityResult"+resultCode,Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
                //Toast.makeText(activity,"Inside activityResult after super",Toast.LENGTH_SHORT).show();
                //callbackManager.onActivityResult(requestCode, resultCode, data);

                break;
            case Const.CHOOSE_PHOTO:
                if (data != null) {

                    uri = data.getData();
                    if (uri != null) {
                        activity.setFbTag(Const.FRAGMENT_REGISTER);
                        beginCrop(uri);
                    } else {
                        AndyUtils.showToast(activity.getString(R.string.img_status), R.id.coordinatorLayout, activity);
                    }
                }
                break;
            case Const.TAKE_PHOTO:
                if (uri != null) {
                    activity.setFbTag(Const.FRAGMENT_REGISTER);
                    beginCrop(uri);
                    // imageFilePath = uri.getPath();
                    // if (imageFilePath != null && imageFilePath.length() > 0) {
                    // File myFile = new File(imageFilePath);
                    //
                    // try {
                    // if (bmp != null)
                    // bmp.recycle();
                    // bmp = new ImageHelper().decodeFile(myFile);
                    // } catch (OutOfMemoryError e) {
                    // // System.out.println("out of bound");
                    // }
                    // ivProPic.setImageBitmap(bmp);
                    // // try {
                    // filePath = imageFilePath;
                    //
                    // break;
                    // } else {
                    // Toast.makeText(activity, "unable to select image",
                    // Toast.LENGTH_LONG).show();
                    // }

                } else {
                    AndyUtils.showToast(activity.getString(R.string.img_status), R.id.coordinatorLayout, activity);
                }
                break;
            case Crop.REQUEST_CROP:

                AppLog.Log(Const.TAG, "Crop photo on activity result");
                if (data != null)
                    handleCrop(resultCode, data);

                break;
        }

    }

    private void goToOTPVerifyFragment(String id) {

        CheckOTPCodeFragment reffralCodeFragment = new CheckOTPCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.Params.ID, id);
        reffralCodeFragment.setArguments(bundle);
        activity.clearBackStack();
        activity.addFragment(reffralCodeFragment, false,
                Const.FRAGMENT_OTP);


    }

    @Override
    protected boolean isValidate() {
        // 
        String msg = null;
        if (TextUtils.isEmpty(etFName.getText().toString())) {
            msg = activity.getString(R.string.text_enter_name);
            etFName.requestFocus();
        } else if (TextUtils.isEmpty(etLName.getText().toString())) {
            msg = activity.getString(R.string.text_enter_lname);
            etLName.requestFocus();
        } else if (TextUtils.isEmpty(etEmail.getText().toString())) {
            msg = activity.getString(R.string.text_enter_email);
            etEmail.requestFocus();
        } else if (!AndyUtils.eMailValidation((etEmail.getText().toString()))) {
            msg = activity.getString(R.string.text_enter_valid_email);
            etEmail.requestFocus();
        } else if (etPassword.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(etPassword.getText().toString())) {
                msg = activity.getString(R.string.text_enter_password);
                etPassword.requestFocus();
            } else if (etPassword.getText().toString().length() < 6) {
                msg = activity.getString(R.string.text_enter_password_valid);
                etPassword.requestFocus();
            }
        }
        if (msg != null) {
            //Converting from Toast to Snack bar @kalai 30/08/2016
//			Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
            return false;

        }
        if (etPassword.getVisibility() == View.GONE) {
            if (!TextUtils.isEmpty(socialUrl)) {
                filePath = null;
//                filePath = new AQuery(activity).getCachedFile(socialUrl)
//                        .getAbsolutePath();
                Picasso.with(getActivity())
                        .load(socialUrl)
                        .placeholder(R.drawable.default_user)   // optional
//                    .error(R.drawable.ic_car)      // optional
//                    .resize(159,159)                        // optional
                        .into(ivProPic);
            }
        }
//        if (!mCountryUtil.validate()) {
        if (TextUtils.isEmpty(MphoneUtli.getText())) {
            msg = activity.getString(R.string.text_enter_number);
            MphoneUtli.requestFocus();
        }

        // else if (TextUtils.isEmpty(filePath)) {
        // msg = getString(R.string.text_pro_pic);
        //
        // }

        if (msg == null) {
            return true;
        }
        //Converting from Toast to Snack bar @kalai 30/08/2016
//		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
        return false;
    }

    /*private void getProfile(LoginResult loginResult) {
        AndyUtils.showCustomProgressDialog(activity,
                getString(R.string.text_getting_info), true, null);

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "Graph Req Response"+response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        if(bFacebookData!=null){
                            progressDialog.dismiss();
                        }

                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
        *//*mSimpleFacebook.getProfile(new OnProfileListener() {
            @Override
			public void onComplete(Profile profile) {
				AndyUtils.removeCustomProgressDialog();
				Log.i("Uber", "My profile id = " + profile.getId());
				//btnGPlus.setEnabled(false);
				//btnFb.setEnabled(false);
				
				etEmail.setText(profile.getEmail());
				etFName.setText(profile.getFirstName());
				etLName.setText(profile.getLastName());
				
				Log.i("REGISTER","Email iD"+profile.getEmail());				
			
				socialId = profile.getId(); 
				type = Const.SOCIAL_FACEBOOK;
				// etPassword.setEnabled(false); 
				etPassword.setVisibility(View.GONE); 

				if (!TextUtils.isEmpty(profile.getPicture())
						|| !profile.getPicture().equalsIgnoreCase("null")) {
					socialUrl = profile.getPicture();
					AQuery aQuery = new AQuery(activity);
					aQuery.id(ivProPic).image(profile.getPicture(),
							getAqueryOption());
				} else {
					socialUrl = null; 
				}

			}
		});*//*
    }

	private Bundle getFacebookData(JSONObject object) {
		Bundle bundle = new Bundle();
		try {
			String id = object.getString("id");

			try {
				profile_pic= new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
				Log.i("profile_pic", profile_pic + "");
				bundle.putString("profile_pic", profile_pic.toString());

			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}

			bundle.putString("idFacebook", id);
			if (object.has("first_name"))
				Log.i(TAG,"First Name"+object.getString("first_name"));
			bundle.putString("first_name", object.getString("first_name"));
			if (object.has("last_name"))
				Log.i(TAG,"Last Name"+object.getString("last_name"));
			bundle.putString("last_name", object.getString("last_name"));
			if (object.has("email"))
				Log.i(TAG,"Email"+object.getString("email"));
			bundle.putString("email", object.getString("email"));
			if (object.has("gender"))
				Log.i(TAG,"Gender"+object.getString("gender"));
			bundle.putString("gender", object.getString("gender"));
			if (object.has("birthday"))
				Log.i(TAG,"Birathday"+object.getString("birthday"));
			bundle.putString("birthday", object.getString("birthday"));
			if (object.has("location"))
				Log.i(TAG,"Location"+object.getJSONObject("location").getString("name"));
			bundle.putString("location", object.getJSONObject("location").getString("name"));

			Log.i("Uber", "My profile id = " +  object.getString("id"));
			//btnGPlus.setEnabled(false);
			btnFb.setEnabled(false);

			etEmail.setText(object.getString("email"));
			etFName.setText(object.getString("first_name"));
			etLName.setText(object.getString("last_name"));

			Log.i("REGISTER","Email iD"+object.getString("email"));

			socialId = object.getString("id");
			type = Const.SOCIAL_FACEBOOK;
			// etPassword.setEnabled(false);
			etPassword.setVisibility(View.GONE);

			if (!TextUtils.isEmpty(profile_pic.toString())
					|| !profile_pic.toString().equalsIgnoreCase("null")) {
				socialUrl =profile_pic.toString();
				AQuery aQuery = new AQuery(activity);
				aQuery.id(ivProPic).image(profile_pic.toString(),
						getAqueryOption());
			} else {
				socialUrl = null;
			}



		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
 */
    private void register(String type, String id) {

        if (type.equalsIgnoreCase(Const.MANUAL)) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.REGISTER);
            map.put(Const.Params.FIRSTNAME, etFName.getText().toString());
            map.put(Const.Params.LAST_NAME, etLName.getText().toString());
            map.put(Const.Params.EMAIL, etEmail.getText().toString());
            map.put(Const.Params.PASSWORD, etPassword.getText().toString());
            map.put(Const.Params.PICTURE, filePath);
            map.put(Const.Params.PHONE, MphoneUtli.getText().toString().replace(" ", ""));
            map.put(Const.Params.DEVICE_TOKEN,
                    CustomerApplication.preferenceHelper.getDeviceToken());
            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            map.put(Const.Params.ADDRESS, etAddress.getText().toString());
            map.put(Const.Params.BIO, etBio.getText().toString());
            map.put(Const.Params.ZIPCODE, etZipCode.getText().toString());
            map.put(Const.Params.STATE, "");
            map.put(Const.Params.COUNTRY, "");
            map.put(Const.Params.LOGIN_BY, Const.MANUAL);
            map.put(Const.Params.OTPTYPE, Const.OTPTYPE);
            AndyUtils.showCustomProgressDialog(activity, activity.
                    getString(R.string.text_registering), true, null);
            new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER,
                    this, this);
            CustomerApplication.preferenceHelper.putOTPEditEmail(etEmail.getText().toString());
            CustomerApplication.preferenceHelper.putOTPEditFname(etFName.getText().toString());
//			CustomerApplication.preferenceHelper.putEditCountryCode(spCCode.getText().toString().trim());
            CustomerApplication.preferenceHelper.putOTPEditLname(etLName.getText().toString());
            CustomerApplication.preferenceHelper.putOTPEditPasswrd(etPassword.getText().toString());
            CustomerApplication.preferenceHelper.putOTPEditPhone(MphoneUtli.getText().toString().replace(" ", ""));
            CustomerApplication.preferenceHelper.PutFlagId((int) mImage_Spinner_View.getTag());
            CustomerApplication.preferenceHelper.putOTPEditPicture(filePath);
        } else {
            registerSocial(id, type);
        }

    }

    private void registerSocial(final String id, final String type) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FIRSTNAME, etFName.getText().toString());
        map.put(Const.Params.LAST_NAME, etLName.getText().toString());
        map.put(Const.Params.EMAIL, etEmail.getText().toString());
        map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.PHONE, MphoneUtli.getText().toString().replace(" ", ""));
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        // map.put(Const.Params.ADDRESS, etAddress.getText().toString());
        // map.put(Const.Params.BIO, etBio.getText().toString());
        // map.put(Const.Params.ZIPCODE, etZipCode.getText().toString());
        map.put(Const.Params.STATE, "");
        map.put(Const.Params.COUNTRY, "");
        map.put(Const.Params.LOGIN_BY, type);
        map.put(Const.Params.OTPTYPE, Const.OTPTYPE);
        AndyUtils.showCustomProgressDialog(activity, activity.
                getString(R.string.text_registering), true, null);
        new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER, this, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        super.onTaskCompleted(response, serviceCode);
        switch (serviceCode) {
            case Const.ServiceCode.REGISTER:
                AndyUtils.removeCustomProgressDialog();
                RegisterModel registerModel = parseContent.getSingleObject(response, RegisterModel.class);
                if (registerModel != null) {
                    if (registerModel.success) {
                        if (!(CustomerApplication.preferenceHelper.getIsOTPVerify())) {
                            CustomerApplication.preferenceHelper.putUserId(registerModel.user.id + "");
                            CustomerApplication.preferenceHelper.putSessionToken(registerModel.user.token);
                            goToOTPVerifyFragment(CustomerApplication.preferenceHelper.getUserId());
                        }
                    } else {
                        AndyUtils.showToast(parseContent.ErrorResponse(registerModel.errorCode), R.id.coordinatorLayout, activity);
                    }
                } else {
                    AndyUtils.showToast(activity.getString(R.string.error_contact_server), R.id.coordinatorLayout, activity);
                }
                break;

            case Const.ServiceCode.UPDATE_PROFILE:
                AndyUtils.removeCustomProgressDialog();
                if (parseContent.isSuccessWithOTPEdit(response)) {
                    CustomerApplication.preferenceHelper.putPassword(etPassword
                            .getText().toString());
                    if (!(CustomerApplication.preferenceHelper.getIsOTPVerify())) {
                        goToOTPVerifyFragment(CustomerApplication.preferenceHelper.getUserId());
                    }
                } else {
                    AndyUtils.showToast(activity.getString(R.string.toast_update_failed), R.id.coordinatorLayout, activity);
                }
                break;


        }
    }

    private void choosePhotoFromGallary() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        activity.startActivityForResult(i, Const.CHOOSE_PHOTO,
                Const.FRAGMENT_REGISTER);

    }


    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(),
                (cal.getTimeInMillis() + ".jpg"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        uri = Uri.fromFile(file);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(i, Const.TAKE_PHOTO,
                Const.FRAGMENT_REGISTER);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.fragments.UberBaseFragmentRegister#OnBackPressed()
     */
    @Override
    public boolean OnBackPressed() {
        // 
        // activity.removeAllFragment(new UberMainFragment(), false,
        // Const.FRAGMENT_MAIN);
        //Toast.makeText(activity,"Inside Register Frafment BackPress",Toast.LENGTH_SHORT).show();
        activity.goToMainActivity();
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.uberorg.fragments.BaseFragmentRegister#OnBackPressed()
     */
    private void beginCrop(Uri source) {
        // Uri outputUri = Uri.fromFile(new File(registerActivity.getCacheDir(),
        // "cropped"));
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().start(activity);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == activity.RESULT_OK) {
            AppLog.Log(Const.TAG, "Handle crop");
            filePath = getRealPathFromURI(Crop.getOutput(result));
            ivProPic.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {

            //Converting from Toast to Snack bar @kalai 30/08/2016
//			Toast.makeText(activity, Crop.getError(result).getMessage(),
            AndyUtils.showToast(Crop.getError(result).getMessage(), R.id.coordinatorLayout, activity);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // 
        AppLog.Log(Const.TAG, error.getMessage());
    }

    public String getConutry() {
        StringBuffer result = null;
        try {
            InputStream inputStream = getResources().openRawResource(
                    R.raw.countrycodes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {

        }
        return result != null ? result.toString() : "";
    }
}
