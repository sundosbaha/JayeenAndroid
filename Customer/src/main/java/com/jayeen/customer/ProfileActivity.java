package com.jayeen.customer;

import android.Manifest;
import android.app.ActionBar.LayoutParams;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.jayeen.customer.component.MyFontButton;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.component.MyFontTextView;
import com.jayeen.customer.newmodels.ProfileUpdatedSuccess;
import com.jayeen.customer.newmodels.UserModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.MultiPartRequester;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Kishan H Dhamat
 *         //
 */
public class ProfileActivity extends ActionBarBaseActivitiy implements
        OnClickListener, AsyncTaskCompleteListener {
    private MyFontEdittextView etProfileFname, etProfileLName, etProfileEmail,
            etProfileNumber, etProfileAddress, etProfileBio, etProfileZipcode,
            etCurrentPassword, etNewPassword, etRetypePassword;
    private ImageView btnProfileEmailInfo;
    private CircularImageView ivProfile;
    private MyFontButton tvProfileSubmit;
    private Uri uri = null;
    private String profileImageData, profileImageFilePath, loginType;
    private final String TAG = "profileActivity";
    private PopupWindow registerInfoPopup;
    private MyFontTextView tvPopupMsg;
    private TaxiParseContent parseContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = new TaxiParseContent(this);
        setContentView(R.layout.activity_profile);
        setTitle(getString(R.string.text_profile));
        setIconMenu(R.drawable.nav_profile_white);
        setIcon(R.drawable.back);
        findViewById(R.id.tvProfileCountryCode).setVisibility(View.GONE);
        etProfileFname = (MyFontEdittextView) findViewById(R.id.etProfileFName);
        etProfileLName = (MyFontEdittextView) findViewById(R.id.etProfileLName);
        etProfileEmail = (MyFontEdittextView) findViewById(R.id.etProfileEmail);
        etCurrentPassword = (MyFontEdittextView) findViewById(R.id.etCurrentPassword);
        etNewPassword = (MyFontEdittextView) findViewById(R.id.etNewPassword);
        etRetypePassword = (MyFontEdittextView) findViewById(R.id.etRetypePassword);
        etProfileNumber = (MyFontEdittextView) findViewById(R.id.etProfileNumber);
        etProfileBio = (MyFontEdittextView) findViewById(R.id.etProfileBio);
        etProfileAddress = (MyFontEdittextView) findViewById(R.id.etProfileAddress);
        etProfileZipcode = (MyFontEdittextView) findViewById(R.id.etProfileZipCode);
        tvProfileSubmit = (MyFontButton) findViewById(R.id.tvProfileSubmit);
        ivProfile = (CircularImageView) findViewById(R.id.ivProfileProfile);
        btnProfileEmailInfo = (ImageView) findViewById(R.id.btnProfileEmailInfo);
        btnProfileEmailInfo.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        tvProfileSubmit.setOnClickListener(this);
        tvProfileSubmit.setText(getResources().getString(
                R.string.text_edit_profile));
        try {
            btnSosNotification.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginType = CustomerApplication.preferenceHelper.getLoginBy();

        AppLog.Log(Const.TAG, "Login type==+> " + loginType);
        if (loginType.equals(Const.MANUAL)) {
            etCurrentPassword.setVisibility(View.VISIBLE);
            etNewPassword.setVisibility(View.VISIBLE);
            etRetypePassword.setVisibility(View.VISIBLE);
        }
        disableViews();
        setData();
        LayoutInflater inflate = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflate.inflate(
                R.layout.popup_info_window, null);
        tvPopupMsg = (MyFontTextView) layout.findViewById(R.id.tvPopupMsg);
        registerInfoPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        layout.setOnClickListener(this);
        registerInfoPopup.setBackgroundDrawable(new BitmapDrawable());
        registerInfoPopup.setOutsideTouchable(true);

    }

    private void disableViews() {
        etProfileFname.setEnabled(false);
        etProfileLName.setEnabled(false);
        etProfileEmail.setEnabled(false);
        etProfileNumber.setEnabled(false);
        etCurrentPassword.setEnabled(false);
        etNewPassword.setEnabled(false);
        etRetypePassword.setEnabled(false);
        ivProfile.setEnabled(false);
    }

    private void enableViews() {
        etProfileFname.setEnabled(true);
        etProfileLName.setEnabled(true);
//        etProfileNumber.setEnabled(true);
        etCurrentPassword.setEnabled(true);
        etNewPassword.setEnabled(true);
        etRetypePassword.setEnabled(true);
        ivProfile.setEnabled(true);
    }

    private void setData() {
        UserModel user = CustomerApplication.preferenceHelper.getObject(Const.USER_DETAILS, UserModel.class);
        if (user != null) {
            if (user.picture != null && !user.picture.isEmpty()) {
                Picasso.with(this)
                        .load(user.picture)
                        .placeholder(R.drawable.default_user)
                        .into(ivProfile);
            }
            etProfileFname.setText(user.firstName);
            etProfileLName.setText(user.lastName);
            etProfileEmail.setText(user.email);
            etProfileNumber.setText(user.phone);
        }
    }

    private void onUpdateButtonClick() {
        if (etProfileFname.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.text_enter_name), R.id.coordinatorLayout, this);
            return;
        } else if (etProfileLName.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.text_enter_lname), R.id.coordinatorLayout, this);
            return;
        } else if (etProfileEmail.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.text_enter_email), R.id.coordinatorLayout, this);
            return;
        } else if (!AndyUtils.eMailValidation(etProfileEmail.getText().toString())) {
            AndyUtils.showToast(getResources().getString(R.string.text_enter_valid_email), R.id.coordinatorLayout, this);
            return;
        } else if (etCurrentPassword.getVisibility() == View.VISIBLE) {
            if (!TextUtils.isEmpty(etNewPassword.getText())) {
                if (etNewPassword.getText().length() < 6) {
                    AndyUtils.showToast(getResources().getString(R.string.error_valid_password), R.id.coordinatorLayout, this);
                    return;
                } else if (TextUtils.isEmpty(etCurrentPassword.getText())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_password), R.id.coordinatorLayout, this);
                    return;
                } else if (etCurrentPassword.getText().length() < 6) {
                    AndyUtils.showToast(getResources().getString(R.string.error_valid_password), R.id.coordinatorLayout, this);
                    return;
                } else if (TextUtils.isEmpty(etRetypePassword.getText())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_retypepassword), R.id.coordinatorLayout, this);
                    return;
                } else if (!etRetypePassword.getText().toString()
                        .equals(etNewPassword.getText().toString())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_mismatch_password), R.id.coordinatorLayout, this);
                    return;
                }
            } else if (etCurrentPassword.getVisibility() == View.INVISIBLE) {
                etNewPassword.setVisibility(View.INVISIBLE);
                etRetypePassword.setVisibility(View.INVISIBLE);
            }

        }

        if (etProfileNumber.getText().length() == 0) {
            AndyUtils.showToast(getResources().getString(R.string.text_enter_number), R.id.coordinatorLayout, this);
            return;
        } else {
            updateSimpleProfile(loginType);
        }
    }

    private void updateSimpleProfile(String type) {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, this);
            return;
        }

        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_update_profile),
                false, null);

        if (type.equals(Const.MANUAL)) {
            AppLog.Log(TAG, "Simple Profile update method");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
            map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
            map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
            map.put(Const.Params.FIRSTNAME, etProfileFname.getText().toString());
            map.put(Const.Params.LAST_NAME, etProfileLName.getText().toString());
            map.put(Const.Params.EMAIL, etProfileEmail.getText().toString());
            map.put(Const.Params.OLD_PASSWORD, etCurrentPassword.getText()
                    .toString());
            map.put(Const.Params.NEW_PASSWORD, etNewPassword.getText()
                    .toString());
            map.put(Const.Params.PICTURE, profileImageData);
            map.put(Const.Params.PHONE, etProfileNumber.getText().toString());
            new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
                    this,this
            );
        } else {
            updateSocialProfile(type);
        }
    }

    private void updateSocialProfile(String loginType) {
        AppLog.Log(TAG, "profile social update  method");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.FIRSTNAME, etProfileFname.getText().toString());
        map.put(Const.Params.LAST_NAME, etProfileLName.getText().toString());
        map.put(Const.Params.ADDRESS, etProfileAddress.getText().toString());
        map.put(Const.Params.EMAIL, etProfileEmail.getText().toString());
        map.put(Const.Params.PHONE, etProfileNumber.getText().toString());
        map.put(Const.Params.PICTURE, profileImageData);
        map.put(Const.Params.STATE, "");
        map.put(Const.Params.COUNTRY, "");
        map.put(Const.Params.BIO, etProfileBio.getText().toString());
        map.put(Const.Params.ZIPCODE, etProfileZipcode.getText().toString()
                .trim());
        new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
                this,this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvProfileSubmit:
                if (tvProfileSubmit
                        .getText()
                        .toString()
                        .equals(getResources()
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
                    if (checkSelfPermission(android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                            &&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                            &&checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission is granted");
                        showPictureDialog();
                    } else {
                        Log.v(TAG, "Permission is revoked");
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                } else {
                    showPictureDialog();
                }
                break;
            case R.id.btnActionNotification:
                onBackPressed();
                break;
            case R.id.btnProfileEmailInfo:
                if (registerInfoPopup.isShowing())
                    registerInfoPopup.dismiss();
                else {
                    registerInfoPopup.showAsDropDown(btnProfileEmailInfo);
                    tvPopupMsg.setText(getString(R.string.text_profile_popup));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            showPictureDialog();
//            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
//        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(
                R.string.text_choosepicture));
        String[] pictureDialogItems = {
                getResources().getString(R.string.text_gallary),
                getResources().getString(R.string.text_camera)};

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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);

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
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.CHOOSE_PHOTO) {
            if (data != null) {
                Uri contentURI = data.getData();
                AppLog.Log(TAG, "Choose photo on activity result");
                beginCrop(contentURI);

            }

        } else if (requestCode == Const.TAKE_PHOTO) {

            if (uri != null) {
                profileImageFilePath = uri.getPath();
                AppLog.Log(TAG, "Take photo on activity result");
                beginCrop(uri);

            } else {
                AndyUtils.showToast(getResources().getString(R.string.toast_unable_to_selct_image), R.id.coordinatorLayout, ProfileActivity.this);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            AppLog.Log(TAG, "Crop photo on activity result");
            handleCrop(resultCode, data);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null,
                null, null);

        if (cursor == null) {
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
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(TAG, response);
        switch (serviceCode) {
            case Const.ServiceCode.UPDATE_PROFILE:
                AndyUtils.removeCustomProgressDialog();
                ProfileUpdatedSuccess profileUpdatedSuccess = parseContent.getSingleObject(response, ProfileUpdatedSuccess.class);
                if (profileUpdatedSuccess.success) {
                    UserModel userModel = parseContent.getSingleObject(response, UserModel.class);
                    if (userModel != null)
                        if (userModel.id != null) {
                            CustomerApplication.preferenceHelper.putObject(Const.USER_DETAILS, userModel);
                            CustomerApplication.preferenceHelper.putPassword(etCurrentPassword.getText().toString());
                            onBackPressed();
                            break;
                        }
                    CustomerApplication.preferenceHelper.putObject(Const.USER_DETAILS, profileUpdatedSuccess.user);
                    CustomerApplication.preferenceHelper.putPassword(etCurrentPassword.getText().toString());
                    onBackPressed();
                } else {
//                    AndyUtils.showToast(getString(R.string.toast_update_failed), R.id.coordinatorLayout, this);
                    AndyUtils.showToast(profileUpdatedSuccess.error, R.id.coordinatorLayout, ProfileActivity.this);
                }
        }

    }

    @Override
    protected boolean isValidate() {
        // 
        return false;
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            AppLog.Log(Const.TAG, "Handle crop");
            profileImageData = getRealPathFromURI(Crop.getOutput(result));
            ivProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            AndyUtils.showToast(Crop.getError(result).getMessage(), R.id.coordinatorLayout, this);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // 
        AppLog.Log(Const.TAG, error.getMessage());
    }

}
