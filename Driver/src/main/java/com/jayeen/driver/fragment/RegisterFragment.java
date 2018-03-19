package com.jayeen.driver.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import com.nplus.countrylist.CountryUtil;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.adapter.VehicalTypeListAdapter;
import com.jayeen.driver.base.BaseRegisterFragment;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.newmodels.VehicleTypesModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.MultiPartRequester;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontButton;
import com.jayeen.driver.widget.MyFontEdittextView;
import com.jayeen.driver.widget.MyFontTextViewCustom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.jayeen.driver.DriverApplication.requestQueue;
/**
 * Created by user on 4/4/2016.
 */
public class RegisterFragment extends BaseRegisterFragment implements
        OnClickListener,
        AsyncTaskCompleteListener {
    private MyFontEdittextView etRegisterFname, etRegisterLName,
            etRegisterPassword, etRegisterEmail, //etRegisterNumber,
            etRegisterAddress, etRegisterBio, etRegisterZipcode,
            etRegisterModel, etRegisterTaxiNo;
    private MyFontButton btnRegisterEmailInfo, btnRegisterModelInfo,
            btnRegisterTaxiNoInfo,btnRegisterPhone;

    private MyFontTextViewCustom tvPopupMsg;
    private GridView gvTypes;
    private SlidingDrawer drawer;
    private ImageView ivProfile;
    private boolean mSignInClicked, mIntentInProgress;
//    private ConnectionResult mConnectionResult;
//    private GoogleApiClient mGoogleApiClient;
    private Uri uri = null;
    private String profileImageFilePath, loginType = AndyConstants.MANUAL,
            socialId, profileImageData = null, socialProPicUrl;
    private List<VehicleTypesModel.Type> listType;
    private VehicalTypeListAdapter adapter;
    private PopupWindow registerInfoPopup;

    private final String TAG = "RegisterFragment";
    private static final int RC_SIGN_IN = 0;
    private int selectedTypePostion = -1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    EditText etRegisterNumber;
    private ImageView tvCountryCode;
    private CountryUtil mCountryUtil;
    private TaxiParseContent parseContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parseContent = new TaxiParseContent(getActivity());
        View registerFragmentView = inflater.inflate(
                R.layout.fragment_register, container, false);

        ivProfile = (ImageView) registerFragmentView.findViewById(R.id.ivRegisterProfile);
        etRegisterAddress = (MyFontEdittextView) registerFragmentView.findViewById(R.id.etRegisterAddress);
        etRegisterBio = (MyFontEdittextView) registerFragmentView.findViewById(R.id.etRegisterBio);
        etRegisterEmail = (MyFontEdittextView) registerFragmentView.findViewById(R.id.etRegisterEmail);
        etRegisterFname = (MyFontEdittextView) registerFragmentView.findViewById(R.id.etRegisterFName);
        etRegisterLName = (MyFontEdittextView) registerFragmentView
                .findViewById(R.id.etRegisterLName);
        etRegisterNumber = (EditText) registerFragmentView
                .findViewById(R.id.etRegisterNumber);
        etRegisterPassword = (MyFontEdittextView) registerFragmentView
                .findViewById(R.id.etRegisterPassword);
        etRegisterZipcode = (MyFontEdittextView) registerFragmentView
                .findViewById(R.id.etRegisterZipCode);
        etRegisterModel = (MyFontEdittextView) registerFragmentView
                .findViewById(R.id.etRegisterModel);
        etRegisterTaxiNo = (MyFontEdittextView) registerFragmentView
                .findViewById(R.id.etRegisterTaxiNo);

        tvCountryCode = (ImageView) registerFragmentView
                .findViewById(R.id.tvRegisterCountryCode);
        btnRegisterEmailInfo = (MyFontButton) registerFragmentView
                .findViewById(R.id.btnRegisterEmailInfo);
        btnRegisterPhone = (MyFontButton) registerFragmentView
                .findViewById(R.id.btnRegisterPhone);
        btnRegisterModelInfo = (MyFontButton) registerFragmentView
                .findViewById(R.id.btnRegisterModelInfo);
        btnRegisterTaxiNoInfo = (MyFontButton) registerFragmentView
                .findViewById(R.id.btnRegisterTaxiNoInfo);
        drawer = (SlidingDrawer) registerFragmentView.findViewById(R.id.drawer);

        gvTypes = (GridView) registerFragmentView.findViewById(R.id.gvTypes);
        ivProfile.setOnClickListener(this);
        btnRegisterEmailInfo.setOnClickListener(this);
        btnRegisterPhone.setOnClickListener(this);
        btnRegisterModelInfo.setOnClickListener(this);
        btnRegisterTaxiNoInfo.setOnClickListener(this);
        registerFragmentView.findViewById(R.id.tvRegisterSubmit)
                .setOnClickListener(this);
        if (AndyConstants.COUNTRY_CODE != null)
            mCountryUtil = new CountryUtil(getActivity(), AndyConstants.COUNTRY_CODE);
        else
            mCountryUtil = new CountryUtil(getActivity());
        mCountryUtil.initUI(etRegisterNumber, tvCountryCode);
        mCountryUtil.initCodes(getActivity());
        return registerFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerActivity.actionBar.show();
        registerActivity.setActionBarTitle(getResources().getString(
                R.string.text_register));
        registerActivity.setActionBarIcon(R.drawable.taxi);
        registerActivity.btnActionInfo.setVisibility(View.INVISIBLE);
        registerActivity.btnActionInfo.setOnClickListener(this);
        LayoutInflater inflate = LayoutInflater.from(registerActivity);
        RelativeLayout layout = (RelativeLayout) inflate.inflate(
                R.layout.popup_notification_window, null);
        tvPopupMsg = (MyFontTextViewCustom) layout.findViewById(R.id.tvPopupMsg);
        registerInfoPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        layout.setOnClickListener(this);
        registerInfoPopup.setBackgroundDrawable(new BitmapDrawable());
        registerInfoPopup.setOutsideTouchable(true);
        listType = new ArrayList<>();
        adapter = new VehicalTypeListAdapter(registerActivity, listType, this);
        gvTypes.setAdapter(adapter);
        gvTypes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                for (int i = 0; i < listType.size(); i++)
                    listType.get(i).isSelected = false;
                listType.get(position).isSelected = true;
                // onItemClick(position);
                selectedTypePostion = position;
                adapter.notifyDataSetChanged();
            }
        });

        getVehicalTypes();
//        Scope scope = new Scope(AndyConstants.GOOGLE_API_SCOPE_URL);
//        mGoogleApiClient = new GoogleApiClient.Builder(registerActivity)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, PlusOptions.builder().build())
//                .addScope(scope).build();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        socialProPicUrl = null;

        switch (v.getId()) {
            case R.id.ivRegisterProfile:

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // FOR API 6.0 DEVICES
                    Log.i("MAINDRAWERRESUME", "Inside  6.0 Devices");
                    if (!checkPermission()) {    // check for runtime permission
                        requestPermission();
                    } else {  //permission granted
                        Log.i("MAINDRAWERRESUME", "Permission already granted In Profile");

                        Log.i("MAINDRAWERRESUME", "Before Call Profile upload dialog");
                        showPictureDialog();
                    }

                } else { // LESS THAN API 6.0 DEVICES

                    Log.i("MAINDRAWERRESUME", "Inside Lower than 6.0 Devices");
                    Log.i("MAINDRAWERRESUME", "Before Call Profile upload dialog Lower Than 6.0");
                    showPictureDialog();

                }

                break;
            case R.id.tvRegisterSubmit:
                onRegisterButtonClick();
                break;
            case R.id.btnRegisterEmailInfo:
                openPopup(btnRegisterEmailInfo,
                        getString(R.string.text_regemail_popup));
                break;
            case R.id.btnRegisterPhone:
                openPopup(btnRegisterPhone,
                        getString(R.string.text_phonenumber_popup));
                break;

            case R.id.btnRegisterModelInfo:
                openPopup(btnRegisterModelInfo,
                        getString(R.string.text_regmodelno_popup));
                break;

            case R.id.btnRegisterTaxiNoInfo:
                openPopup(btnRegisterTaxiNoInfo,
                        getString(R.string.text_regtaxino_popup));
                break;
            default:
                break;
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(registerActivity, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(registerActivity, CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(registerActivity, new String[]{READ_EXTERNAL_STORAGE, CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission_OTP() {
        int result1 = ContextCompat.checkSelfPermission(registerActivity, READ_SMS);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionOTP() {
        ActivityCompat.requestPermissions(registerActivity, new String[]{READ_SMS}, PERMISSION_REQUEST_CODE);

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
                    } else {
                        Log.i("MAINDRAWERRESUME", "Any one permission not granted Camera &  External Storage Granted");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                Log.i("MAINDRAWERRESUME", "External Storage Permission Check");
                                showMessageOKCancel(getResources().getString(
                                        R.string.text_need_accessPermission),
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
                                showMessageOKCancel(getResources().getString(R.string.text_need_accessPermission),
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
        new android.support.v7.app.AlertDialog.Builder(registerActivity)
                .setMessage(message)
                .setPositiveButton(getString(R.string.text_ok), okListener)
                .setNegativeButton(getString(R.string.text_cancel), null)
                .create()
                .show();
    }


    public void openPopup(View view, String msg) {
        if (registerInfoPopup.isShowing())
            registerInfoPopup.dismiss();
        else {
            registerInfoPopup.showAsDropDown(view);
            tvPopupMsg.setText(msg);
        }

    }

    @SuppressWarnings("deprecation")
    private void onRegisterButtonClick() {
        if (etRegisterPassword.getVisibility() == View.GONE) {
            if (!TextUtils.isEmpty(socialProPicUrl)) {
                profileImageData = null;
                Picasso.with(getActivity())
                        .load(socialProPicUrl)
                        .placeholder(R.drawable.default_driver)   // optional
                        .into(ivProfile);
            }
        }
        if (etRegisterFname.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_fname), R.id.coordinatorLayout, registerActivity);
            return;
        } else if (etRegisterLName.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_lname), R.id.coordinatorLayout, registerActivity);
            return;
        } else if (etRegisterEmail.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_email), R.id.coordinatorLayout, registerActivity);
            return;
        } else if (!AndyUtils.eMailValidation(etRegisterEmail.getText()
                .toString())) {
            AndyUtils.showToast(getResources().getString(R.string.error_valid_email), R.id.coordinatorLayout, registerActivity);
            return;
        } else if (etRegisterModel.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_model), R.id.coordinatorLayout, registerActivity);
            return;
        } else if (etRegisterTaxiNo.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_taxi_no), R.id.coordinatorLayout, registerActivity);
            return;

        } else if (etRegisterPassword.getVisibility() == View.VISIBLE) {
            if (etRegisterPassword.getText().length() == 0) {
                AndyUtils.showToast(getResources().getString(R.string.error_empty_reg_password), R.id.coordinatorLayout
                        , registerActivity);
                return;
            } else if (etRegisterPassword.getText().length() < 6) {
                AndyUtils.showToast(getResources().getString(
                        R.string.error_valid_password), R.id.coordinatorLayout, registerActivity);
                return;
            }else if (etRegisterNumber.getText().length() <= 3) {
                AndyUtils.showToast(getResources().getString(R.string.error_empty_number), R.id.coordinatorLayout, registerActivity);
                return;
            }/*else if (etRegisterNumber.getText().length() < 5 || etRegisterNumber.getText().length() > 18) {
                AndyUtils.showToast(getResources().getString(R.string.error_601), R.id.coordinatorLayout, registerActivity);
                return;
            } */else if (selectedTypePostion == -1) {
                AndyUtils.showToast(getResources().getString(R.string.error_empty_type), R.id.coordinatorLayout, registerActivity);
                drawer.open();
                return;
            } else {
                if(!checkPermission_OTP()){
                    Log.i("MAINDRAWERRESUME", "Permission Not granted to OTP");
                    requestPermissionOTP();
                }
                register(loginType, socialId);
            }
        }



    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(
                registerActivity);
        pictureDialog.setTitle(getResources().getString(
                R.string.dialog_chhose_photo));
        String[] pictureDialogItems = {
                getResources().getString(R.string.dialog_from_gallery),
                getResources().getString(R.string.dialog_from_camera)};

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        pictureDialog.show();
    }

//    private void resolveSignInError() {
////        if (mConnectionResult.hasResolution()) {
////            try {
////                mIntentInProgress = true;
////                registerActivity.startIntentSenderForResult(mConnectionResult
////                                .getResolution().getIntentSender(), RC_SIGN_IN, null,
////                        0, 0, 0, AndyConstants.REGISTER_FRAGMENT_TAG);
////            } catch (SendIntentException e) {
////                mIntentInProgress = false;
////                mGoogleApiClient.connect();
////            }
////        }
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (!mIntentInProgress) {
//            mConnectionResult = result;
//            if (mSignInClicked) {
//                resolveSignInError();
//            }
//        }
//
//    }

    private void getFbProfile() {
        AndyUtils.showCustomProgressDialog(registerActivity, "",
                getString(R.string.text_getting_info_facebook), true);
    }

    public void onItemClick(int pos) {
        selectedTypePostion = pos;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            if (resultCode != registerActivity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

//            if (!mGoogleApiClient.isConnecting()) {
//                mGoogleApiClient.connect();
//            }
        } else if (requestCode == AndyConstants.CHOOSE_PHOTO) {
            if (data != null) {

                Uri contentURI = data.getData();
                registerActivity.setFbTag(AndyConstants.REGISTER_FRAGMENT_TAG);
                AppLog.Log(TAG, "Choose photo on activity result");
                beginCrop(contentURI);
                Picasso.with(getActivity())
                        .load(profileImageFilePath)
                        .placeholder(R.drawable.default_driver)   // optional
                        .into(ivProfile);
            }

        } else if (requestCode == AndyConstants.TAKE_PHOTO) {

            if (uri != null) {
                profileImageFilePath = uri.getPath();
                registerActivity.setFbTag(AndyConstants.REGISTER_FRAGMENT_TAG);
                AppLog.Log(TAG, "Take photo on activity result");
                beginCrop(uri);
            } else {
                AndyUtils.showToast(registerActivity.getResources().getString(
                        R.string.toast_unable_to_selct_image), R.id.coordinatorLayout, registerActivity);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            AppLog.Log(TAG, "Crop photo on activity result");
            handleCrop(resultCode, data);
        } else {
        }
    }

    private void choosePhotoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        registerActivity
                .startActivityForResult(galleryIntent,
                        AndyConstants.CHOOSE_PHOTO,
                        AndyConstants.REGISTER_FRAGMENT_TAG);

    }

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(),
                (cal.getTimeInMillis() + ".jpg"));

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        registerActivity.startActivityForResult(cameraIntent,
                AndyConstants.TAKE_PHOTO, AndyConstants.REGISTER_FRAGMENT_TAG);
    }

//    @Override
//    public void onConnected(Bundle arg0) {
//        AndyUtils.removeCustomProgressDialog();
//        if (ActivityCompat.checkSelfPermission(registerActivity, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//        Person currentPerson = Plus.PeopleApi
//                .getCurrentPerson(mGoogleApiClient);
//
//        String personName = currentPerson.getDisplayName();
//        String personPhoto = currentPerson.getImage().toString();
//        etRegisterEmail.setText(email);
//        if (personName.contains(" ")) {
//            String[] split = personName.split(" ");
//            etRegisterFname.setText(split[0]);
//            etRegisterLName.setText(split[1]);
//        } else {
//            etRegisterFname.setText(personName);
//        }
//
//        // etRegisterPassword.setEnabled(false);
//        etRegisterPassword.setVisibility(View.GONE);
//        if (!TextUtils.isEmpty(personPhoto)
//                || !personPhoto.equalsIgnoreCase("null")) {
//            socialProPicUrl = personPhoto;
//            Picasso.with(getActivity())
//                    .load(personPhoto)
//                    .placeholder(R.drawable.default_driver)   // optional
//                    .into(ivProfile);
//        } else {
//            socialProPicUrl = null;
//        }
//
//        socialId = currentPerson.getId();
//        loginType = AndyConstants.SOCIAL_GOOGLE;
//        etRegisterPassword.setVisibility(View.GONE);
//    }

    private void register(String type, String id) {

        if (!AndyUtils.isNetworkAvailable(registerActivity)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, registerActivity);
            return;
        }

        AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
                .getString(R.string.progress_dialog_register), false);

        if (type.equals(AndyConstants.MANUAL)) {
            AppLog.Log(TAG, "Simple Register method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, AndyConstants.ServiceType.REGISTER);
            map.put(AndyConstants.Params.FIRSTNAME, etRegisterFname.getText()
                    .toString());
            map.put(AndyConstants.Params.LAST_NAME, etRegisterLName.getText()
                    .toString());
            map.put(AndyConstants.Params.EMAIL, etRegisterEmail.getText()
                    .toString());
            map.put(AndyConstants.Params.PASSWORD, etRegisterPassword.getText()
                    .toString());

            if (!TextUtils.isEmpty(profileImageData)) {
                map.put(AndyConstants.Params.PICTURE, profileImageData);
            }
            String Str_PhoneNumber = etRegisterNumber.getText().toString().replace(" ", "");
            Str_PhoneNumber = Str_PhoneNumber.replace(" ", "");
            map.put(AndyConstants.Params.PHONE, Str_PhoneNumber);
            map.put(AndyConstants.Params.BIO, etRegisterBio.getText()
                    .toString());
            map.put(AndyConstants.Params.ADDRESS, etRegisterAddress.getText()
                    .toString());
            map.put(AndyConstants.Params.STATE, "");
            map.put(AndyConstants.Params.COUNTRY, "");
            map.put(AndyConstants.Params.ZIPCODE, etRegisterZipcode.getText()
                    .toString().trim());
            map.put(AndyConstants.Params.TYPE,
                    String.valueOf(listType.get(selectedTypePostion).mId));
            map.put(AndyConstants.Params.DEVICE_TYPE,
                    AndyConstants.DEVICE_TYPE_ANDROID);
            map.put(AndyConstants.Params.DEVICE_TOKEN, DriverApplication.preferenceHelper.getDeviceToken());
            map.put(AndyConstants.Params.TAXI_MODEL, etRegisterModel.getText()
                    .toString().trim());
            map.put(AndyConstants.Params.TAXI_NUMBER, etRegisterTaxiNo
                    .getText().toString().trim());
            map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
            new MultiPartRequester(registerActivity, map,
                    AndyConstants.ServiceCode.REGISTER, this);
            System.out.println("keys..:" + map.get(AndyConstants.Params.PHONE));
        } else {
            registerSoicial(id, type);
        }
    }

    private void registerSoicial(String id, String loginType) {
        AppLog.Log(TAG, "Register social method");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.REGISTER);
        map.put(AndyConstants.Params.FIRSTNAME, etRegisterFname.getText()
                .toString());
        map.put(AndyConstants.Params.LAST_NAME, etRegisterLName.getText()
                .toString());
        map.put(AndyConstants.Params.ADDRESS, etRegisterAddress.getText()
                .toString());
        map.put(AndyConstants.Params.EMAIL, etRegisterEmail.getText()
                .toString());
        map.put(AndyConstants.Params.PHONE,
                etRegisterNumber.getText().toString());
        if (!TextUtils.isEmpty(profileImageData)) {
            map.put(AndyConstants.Params.PICTURE, profileImageData);
        }
        map.put(AndyConstants.Params.STATE, "");
        map.put(AndyConstants.Params.TYPE,
                String.valueOf(listType.get(selectedTypePostion).mId));
        map.put(AndyConstants.Params.COUNTRY, "");
        map.put(AndyConstants.Params.BIO, etRegisterBio.getText().toString());
        map.put(AndyConstants.Params.DEVICE_TYPE,
                AndyConstants.DEVICE_TYPE_ANDROID);
        map.put(AndyConstants.Params.DEVICE_TOKEN, DriverApplication.preferenceHelper.getDeviceToken());
        map.put(AndyConstants.Params.ZIPCODE, etRegisterZipcode.getText()
                .toString().trim());
        map.put(AndyConstants.Params.TAXI_MODEL, etRegisterModel.getText()
                .toString().trim());
        map.put(AndyConstants.Params.TAXI_NUMBER, etRegisterTaxiNo.getText()
                .toString().trim());
        map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
        map.put(AndyConstants.Params.LOGIN_BY, loginType);
        new MultiPartRequester(registerActivity, map,
                AndyConstants.ServiceCode.REGISTER, this);

    }

//    @Override
//    public void onConnectionSuspended(int arg0) {
//    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();

        switch (serviceCode) {
            case AndyConstants.ServiceCode.GET_VEHICAL_TYPES:
                AppLog.Log(TAG, "Vehicle types  " + response);
                VehicleTypesModel vehicleTypesModel = parseContent.getSingleObject(response, VehicleTypesModel.class);
                if (vehicleTypesModel.mSuccess) {
                    listType = vehicleTypesModel.mTypes;
                    adapter.notifyDataSetChanged();
                    adapter = new VehicalTypeListAdapter(registerActivity, listType, this);
                    gvTypes.setAdapter(adapter);
                }
                AndyUtils.removeCustomProgressDialog();
                break;

            case AndyConstants.ServiceCode.REGISTER:
                AppLog.Log(TAG, "Register response :" + response);
                LoginModel registerModel = parseContent.getSingleObject(response, LoginModel.class);
                if (registerModel.success) {
                    AndyUtils.showToast(registerActivity.getResources().getString(
                            R.string.toast_register_success), R.id.coordinatorLayout, registerActivity);
                    parseContent.parseUserAndStoreToDb(registerModel.driver);
                    DriverApplication.preferenceHelper.putPassword(etRegisterPassword.getText().toString());
                    registerActivity.addFragment(new LoginFragment(), false,
                            AndyConstants.LOGIN_FRAGMENT_TAG, false);
                } else {
                    AndyUtils.showToast(registerModel.erro, R.id.coordinatorLayout, registerActivity);
                }
                break;

            default:
                break;
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = registerActivity.getContentResolver().query(contentURI,
                null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
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

    @Override
    public void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndyUtils.removeCustomProgressDialog();
    }

    private void getVehicalTypes() {
        AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
                .getString(R.string.progress_getting_types), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.GET_VEHICAL_TYPES);
        AppLog.Log(TAG, AndyConstants.URL);
        requestQueue.add(new VolleyHttpRequest(Request.Method.GET, map,
                AndyConstants.ServiceCode.GET_VEHICAL_TYPES, this, this));
    }

    @Override
    public void onResume() {
        super.onResume();
        registerActivity.currentFragment = AndyConstants.REGISTER_FRAGMENT_TAG;
        mCountryUtil.initCodes(getActivity());
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().start(registerActivity);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == registerActivity.RESULT_OK) {
            AppLog.Log(TAG, "Handle crop");
            profileImageData = getRealPathFromURI(Crop.getOutput(result));
            ivProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            AndyUtils.showToast(Crop.getError(result).getMessage(), R.id.coordinatorLayout, registerActivity);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(registerActivity.getResources().getString(R.string.error_contact_server), R.id.coordinatorLayout, registerActivity);
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
