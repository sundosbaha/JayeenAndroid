package com.jayeen.customer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.jayeen.customer.gcm.GCMRegisterHendler;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.CommonUtilities;


public class MainActivity extends AppCompatActivity implements OnClickListener {


    private Button btnSignIn, btnRegister;
    private boolean isReceiverRegister;
    private int oldOptions;
    String TAG = "MAINACTIVITY";
    public Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Inside Main Oncreate");
        if (!TextUtils.isEmpty(CustomerApplication.preferenceHelper.getUserId())) {
            if (CustomerApplication.preferenceHelper.getIsOTPVerify()) {
                Log.i(TAG, "Inside mIAn check");
                startActivity(new Intent(this, MainDrawerActivity.class));
                this.finish();
                return;
            }

        } else {
            Log.i(TAG, "Inside mAIn check Else");
        }

        isReceiverRegister = false;
        Log.i(TAG, "Inside Main Oncreate Set Content View");

        setContentView(R.layout.activity_main);
        Log.e("TAG", "Main Activity Request Count" + CustomerApplication.preferenceHelper.getIsOTPVerify());
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        if (TextUtils.isEmpty(CustomerApplication.preferenceHelper.getDeviceToken())) {
            isReceiverRegister = true;
            registerGcmReceiver(mHandleMessageReceiver);
        }
    }

    public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            AndyUtils.showCustomProgressDialog(this,
                    getString(R.string.progress_loading), false, null);
            new GCMRegisterHendler(this, mHandleMessageReceiver);

        }
    }

    public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            if (mHandleMessageReceiver != null) {
                unregisterReceiver(mHandleMessageReceiver);
            }
        }
    }

    private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "Received Intent");
            AndyUtils.removeCustomProgressDialog();
            if (intent.getAction().equals(CommonUtilities.DISPLAY_REGISTER_GCM)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int resultCode = bundle.getInt(CommonUtilities.RESULT);
                    if (resultCode == Activity.RESULT_OK) {

                    } else {
                        AndyUtils.showToast(getString(R.string.register_gcm_failed), R.id.coordinatorLayout, MainActivity.this);
                        finish();
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent startRegisterActivity = new Intent(MainActivity.this,
                RegisterActivity.class);
        switch (v.getId()) {
            case R.id.btnSignIn:
                CustomerApplication.preferenceHelper.clearRegistrationData();
                startRegisterActivity.putExtra("isSignin", true);
                break;
            case R.id.btnRegister:
                CustomerApplication.preferenceHelper.clearRegistrationData();
                startRegisterActivity.putExtra("isSignin", false);
                break;
        }
        startActivity(startRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onDestroy() {
        if (isReceiverRegister) {
            unregisterGcmReceiver(mHandleMessageReceiver);
            isReceiverRegister = false;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        openExitDialog();
    }

    public void openExitDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                return;
            } else {
                if (!isFinishing())
                    mDialog.show();
                return;
            }
        }

        mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.setContentView(R.layout.exit_layout);
        mDialog.findViewById(R.id.tvExitOk).setOnClickListener(dialogBtnListener);
        mDialog.findViewById(R.id.tvExitCancel).setOnClickListener(dialogBtnListener);
        if (!isFinishing())
            mDialog.show();
    }

    OnClickListener dialogBtnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tvExitOk) {
                mDialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            } else {
                mDialog.dismiss();
            }
        }
    };
}