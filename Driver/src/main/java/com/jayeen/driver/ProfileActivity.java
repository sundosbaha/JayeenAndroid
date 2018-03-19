package com.jayeen.driver;

import android.*;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.db.DatabaseAdapter;
import com.jayeen.driver.newmodels.DriverModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.MultiPartRequester;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontButton;
import com.jayeen.driver.widget.MyFontEdittextView;
import com.jayeen.driver.widget.MyFontTextViewCustom;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends ActionBarBaseActivitiy implements
        OnClickListener, AsyncTaskCompleteListener {
    private MyFontEdittextView etProfileFname, etProfileLName, etProfileEmail,
            etProfileNumber, etProfileAddress, etProfileBio, etProfileZipcode,
            etProfileCurrentPassword, etProfileNewPassword,
            etProfileRetypePassword, etProfileModel, etProfileVehicleNo;
    private ImageView ivProfile, btnActionMenu;
    private MyFontButton tvProfileSubmit;

    private ImageView btnProfileModelInfo,
            btnProfileEmailInfo, btnProfileVehicleNoInfo;
    private MyFontTextViewCustom tvPopupMsg;
    private DatabaseAdapter databaseAdapter;
    private Uri uri = null;
    private String profileImageData, profileImageFilePath, loginType;
    private Bitmap profilePicBitmap;

    private final String TAG = "profileActivity";
    private TaxiParseContent taxiparseContent;
    private PopupWindow registerInfoPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.tvProfileCountryCode).setVisibility(View.GONE);
        etProfileFname = (MyFontEdittextView) findViewById(R.id.etProfileFName);
        etProfileLName = (MyFontEdittextView) findViewById(R.id.etProfileLName);
        etProfileEmail = (MyFontEdittextView) findViewById(R.id.etProfileEmail);
        etProfileCurrentPassword = (MyFontEdittextView) findViewById(R.id.etProfileCurrentPassword);
        etProfileNewPassword = (MyFontEdittextView) findViewById(R.id.etProfileNewPassword);
        etProfileRetypePassword = (MyFontEdittextView) findViewById(R.id.etProfileRetypePassword);
        etProfileNumber = (MyFontEdittextView) findViewById(R.id.etProfileNumber);
        etProfileBio = (MyFontEdittextView) findViewById(R.id.etProfileBio);
        etProfileAddress = (MyFontEdittextView) findViewById(R.id.etProfileAddress);
        etProfileZipcode = (MyFontEdittextView) findViewById(R.id.etProfileZipCode);
        etProfileModel = (MyFontEdittextView) findViewById(R.id.etProfileModel);
        etProfileVehicleNo = (MyFontEdittextView) findViewById(R.id.etProfileVehicleNo);
        tvProfileSubmit = (MyFontButton) findViewById(R.id.tvProfileSubmit);
        ivProfile = (ImageView) findViewById(R.id.ivProfileProfile);
        btnProfileEmailInfo = (ImageView) findViewById(R.id.btnProfileEmailInfo);
        btnProfileModelInfo = (ImageView) findViewById(R.id.btnProfileModelInfo);
        btnProfileVehicleNoInfo = (ImageView) findViewById(R.id.btnProfileVehicleNoInfo);
        ActionBar ab = getActionBar();
        btnActionMenu = (ImageButton) findViewById(R.id.btnActionMenu);
        btnActionMenu.setVisibility(View.VISIBLE);
        setActionBarTitle(getString(R.string.text_profile));
        setActionBarIcon(R.drawable.nav_profile_white);
        ivProfile.setOnClickListener(this);
        tvProfileSubmit.setOnClickListener(this);
        btnProfileEmailInfo.setOnClickListener(this);
        btnProfileModelInfo.setOnClickListener(this);
        btnProfileVehicleNoInfo.setOnClickListener(this);
        tvProfileSubmit.setText(getResources().getString(
                R.string.text_edit_profile));
        taxiparseContent = new TaxiParseContent(this);
        // socialId = preferenceHelper.getSocialId();
        loginType = DriverApplication.preferenceHelper.getLoginBy();
        if (loginType.equals(AndyConstants.MANUAL)) {
            etProfileCurrentPassword.setVisibility(View.VISIBLE);
        }

        // popup
        LayoutInflater inflate = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflate.inflate(
                R.layout.popup_notification_window, null);
        tvPopupMsg = (MyFontTextViewCustom) layout.findViewById(R.id.tvPopupMsg);
        registerInfoPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        layout.setOnClickListener(this);
        registerInfoPopup.setBackgroundDrawable(new BitmapDrawable());
        registerInfoPopup.setOutsideTouchable(true);
        disableViews();
        setData();

    }

    private void disableViews() {
        etProfileFname.setEnabled(false);
        etProfileLName.setEnabled(false);
        etProfileEmail.setEnabled(false);
        etProfileNumber.setEnabled(false);
        etProfileBio.setEnabled(false);
        etProfileAddress.setEnabled(false);
        etProfileZipcode.setEnabled(false);
        etProfileCurrentPassword.setEnabled(false);
        etProfileNewPassword.setEnabled(false);
        etProfileRetypePassword.setEnabled(false);
        etProfileModel.setEnabled(false);
        etProfileVehicleNo.setEnabled(false);
        ivProfile.setEnabled(false);
    }

    private void enableViews() {
        etProfileFname.setEnabled(true);
        etProfileLName.setEnabled(true);
        // etProfileEmail.setEnabled(true);
        etProfileNumber.setEnabled(true);
        etProfileBio.setEnabled(true);
        etProfileAddress.setEnabled(true);
        etProfileZipcode.setEnabled(true);
        etProfileCurrentPassword.setEnabled(true);
        etProfileNewPassword.setEnabled(true);
        etProfileRetypePassword.setEnabled(true);
        ivProfile.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData() {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        DriverModel user = databaseAdapter.getUser();

        if (user != null) {
            if (user.picture != null && !user.picture.isEmpty()) {
                Picasso.with(this)
                        .load(user.picture)
                        .placeholder(R.drawable.user)   // optional
                        .error(R.drawable.user)      // optional
//				.resize(400,400)                        // optional
                        .into(ivProfile);
            }
            etProfileFname.setText(user.firstName);
            etProfileLName.setText(user.lastName);
            etProfileEmail.setText(user.email);
            etProfileNumber.setText(user.phone);
            etProfileBio.setText(user.bio);
            etProfileAddress.setText(user.address);
            etProfileZipcode.setText(user.zipcode);
            etProfileModel.setText(user.carModel);
            etProfileVehicleNo.setText(user.carNumber);
        }
    }

    private void onUpdateButtonClick() {
        if (etProfileFname.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_fname), R.id.coordinatorLayout, ProfileActivity.this);
            return;
        } else if (etProfileLName.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_lname), R.id.coordinatorLayout, ProfileActivity.this);
            return;
        } else if (etProfileEmail.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_email), R.id.coordinatorLayout, ProfileActivity.this);
            return;
        } else if (!AndyUtils.eMailValidation(etProfileEmail.getText()
                .toString())) {
            AndyUtils.showToast(getResources().getString(R.string.error_valid_email), R.id.coordinatorLayout, ProfileActivity.this);
            return;

        } else if (etProfileCurrentPassword.getVisibility() == View.VISIBLE) {
            if (!TextUtils.isEmpty(etProfileNewPassword.getText())) {
                if (etProfileNewPassword.getText().length() < 6) {
                    AndyUtils.showToast(getResources().getString(R.string.error_valid_password), R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                } else if (TextUtils
                        .isEmpty(etProfileCurrentPassword.getText())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_password), R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                } else if (etProfileCurrentPassword.getText().length() < 6) {
                    AndyUtils.showToast(getResources().getString(R.string.error_valid_password), R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                } else if (TextUtils.isEmpty(etProfileRetypePassword.getText())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_retypepassword), R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                } else if (!etProfileRetypePassword.getText().toString()
                        .equals(etProfileNewPassword.getText().toString())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_mismatch_password), R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                }
            } else if (etProfileCurrentPassword.getVisibility() == View.INVISIBLE) {
                etProfileRetypePassword.setVisibility(View.INVISIBLE);
                etProfileRetypePassword.setVisibility(View.INVISIBLE);
            }
        }

        if (etProfileNumber.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.error_empty_number), R.id.coordinatorLayout, ProfileActivity.this);
            return;
        }
//        else if (profileImageData == null || profileImageData.equals("")) {
//            AndyUtils.showToast(getResources().getString(R.string.error_empty_image), R.id.coordinatorLayout, ProfileActivity.this);
//            return;
//        }
        else {
            updateSimpleProfile(loginType);
        }
    }

    private void updateSimpleProfile(String type) {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, ProfileActivity.this);
            return;
        }

        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_update_profile), false);

        if (type.equals(AndyConstants.MANUAL)) {
            AppLog.Log(TAG, "Simple Profile update method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, AndyConstants.ServiceType.UPDATE_PROFILE);
            map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
            map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
            map.put(AndyConstants.Params.FIRSTNAME, etProfileFname.getText().toString());
            map.put(AndyConstants.Params.LAST_NAME, etProfileLName.getText().toString());
            map.put(AndyConstants.Params.EMAIL, etProfileEmail.getText().toString());
            map.put(AndyConstants.Params.OLD_PASSWORD, etProfileCurrentPassword.getText().toString());
            map.put(AndyConstants.Params.NEW_PASSWORD, etProfileNewPassword.getText().toString());
            map.put(AndyConstants.Params.PASSWORD, etProfileNewPassword.getText().toString());
            map.put(AndyConstants.Params.PICTURE, (profileImageData != null ? profileImageData : ""));
            map.put(AndyConstants.Params.PHONE, etProfileNumber.getText().toString());
            map.put(AndyConstants.Params.BIO, etProfileBio.getText().toString());
            map.put(AndyConstants.Params.ADDRESS, etProfileAddress.getText().toString());
            map.put(AndyConstants.Params.STATE, "");
            map.put(AndyConstants.Params.COUNTRY, "");
            map.put(AndyConstants.Params.ZIPCODE, etProfileZipcode.getText().toString().trim());
            new MultiPartRequester(this, map, AndyConstants.ServiceCode.UPDATE_PROFILE, this);
        } else {
            updateSocialProfile(type);
        }
    }

    private void updateSocialProfile(String loginType) {
        AppLog.Log(TAG, "profile social update  method");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.UPDATE_PROFILE);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.FIRSTNAME, etProfileFname.getText().toString());
        map.put(AndyConstants.Params.LAST_NAME, etProfileLName.getText().toString());
        map.put(AndyConstants.Params.ADDRESS, etProfileAddress.getText().toString());
        map.put(AndyConstants.Params.EMAIL, etProfileEmail.getText().toString());
        map.put(AndyConstants.Params.PHONE, etProfileNumber.getText().toString());
        map.put(AndyConstants.Params.PICTURE, (profileImageData != null ? profileImageData : ""));
        map.put(AndyConstants.Params.STATE, "");
        map.put(AndyConstants.Params.COUNTRY, "");
        map.put(AndyConstants.Params.BIO, etProfileBio.getText().toString());
        map.put(AndyConstants.Params.ZIPCODE, etProfileZipcode.getText().toString().trim());
        new MultiPartRequester(this, map, AndyConstants.ServiceCode.UPDATE_PROFILE, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvProfileSubmit:
                if (tvProfileSubmit.getText().toString().equals(getResources()
                        .getString(R.string.text_edit_profile))) {
                    enableViews();
                    etProfileFname.requestFocus();
                    tvProfileSubmit.setText(getResources().getString(
                            R.string.text_update_profile));
                } else {
                    onUpdateButtonClick();
                }
                break;
            case R.id.ivProfileProfile:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission is granted");
                        showPictureDialog();
                    } else {
                        Log.v(TAG, "Permission is revoked");
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    showPictureDialog();
                }
                break;

            case R.id.btnActionNotification:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;

            case R.id.btnProfileEmailInfo:
                openPopup(btnProfileEmailInfo,
                        getString(R.string.text_profile_popup));
                break;

            case R.id.btnProfileModelInfo:
                openPopup(btnProfileModelInfo,
                        getString(R.string.text_profile_popup));
                break;

            case R.id.btnProfileVehicleNoInfo:
                openPopup(btnProfileVehicleNoInfo,
                        getString(R.string.text_profile_popup));
                break;
            default:
                break;
        }
    }

    public void openPopup(View view, String msg) {

        if (registerInfoPopup.isShowing())
            registerInfoPopup.dismiss();
        else {
            registerInfoPopup.showAsDropDown(view);
            tvPopupMsg.setText(msg);
        }

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(
                R.string.dialog_chhose_photo));
        String[] pictureDialogItems = {getResources().getString(R.string.dialog_from_gallery), getResources().getString(R.string.dialog_from_camera)};

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

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, AndyConstants.CHOOSE_PHOTO);

    }

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));
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
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, AndyConstants.TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AndyConstants.CHOOSE_PHOTO) {
            if (data != null) {

                Uri contentURI = data.getData();
                AppLog.Log(TAG, "Choose photo on activity result");
                beginCrop(contentURI);
            }

        } else if (requestCode == AndyConstants.TAKE_PHOTO) {

            if (uri != null) {
                profileImageFilePath = uri.getPath();
                AppLog.Log(TAG, "Take photo on activity result");
                beginCrop(uri);
            } else {
                AndyUtils.showToast(this.getResources().getString(R.string.toast_unable_to_selct_image), R.id.coordinatorLayout, ProfileActivity.this);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            AppLog.Log(TAG, "Crop photo on activity result");
            handleCrop(resultCode, data);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(TAG, response);
        switch (serviceCode) {
            case AndyConstants.ServiceCode.UPDATE_PROFILE:
                DriverModel driverModel = taxiparseContent.getSingleObject(response, DriverModel.class);
                if (!driverModel.success) {
                    AndyUtils.showToast(driverModel.error, R.id.coordinatorLayout, ProfileActivity.this);
                    return;
                }
                if (driverModel.success) {
                    AndyUtils.showToast(getResources().getString(R.string.toast_update_profile_success), R.id.coordinatorLayout, ProfileActivity.this);
                    databaseAdapter.deleteUser();
                    taxiparseContent.parseUserAndStoreToDb(driverModel);
                    DriverApplication.preferenceHelper.putPassword(etProfileCurrentPassword.getText().toString());
                    onBackPressed();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndyUtils.removeCustomProgressDialog();
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            AppLog.Log(TAG, "Handle crop");
            profileImageData = getRealPathFromURI(Crop.getOutput(result));
            ivProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            AndyUtils.showToast(Crop.getError(result).getMessage(), R.id.coordinatorLayout, ProfileActivity.this);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}
