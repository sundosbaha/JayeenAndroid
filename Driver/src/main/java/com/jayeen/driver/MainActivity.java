package com.jayeen.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jayeen.driver.utills.CommonUtilities;
import com.jayeen.driver.gcm.GCMRegisterHendler;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;


public class MainActivity extends AppCompatActivity implements OnClickListener {
    private boolean isRecieverRegister = false;
    private static final String TAG = "FirstFragment";
    private RelativeLayout rlLoginRegisterLayout;
    private Button tvExitOk, tvExitCancel;
    private Dialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(DriverApplication.preferenceHelper.getUserId())) {
            startActivity(new Intent(this, MapActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
            return;
        }
        setContentView(R.layout.fragment_main);
        findViewById(R.id.btnFirstSignIn).setOnClickListener(this);
        findViewById(R.id.btnFirstRegister).setOnClickListener(this);

        if (TextUtils.isEmpty(DriverApplication.preferenceHelper.getDeviceToken())) {
            isRecieverRegister = true;
            registerGcmReceiver(mHandleMessageReceiver);

        } else {

            AppLog.Log(TAG, "device already registerd with :" + DriverApplication.preferenceHelper.getDeviceToken());
        }
    }

    private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            AndyUtils.removeCustomProgressDialog();
            if (intent.getAction().equals(
                    CommonUtilities.DISPLAY_MESSAGE_REGISTER)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int resultCode = bundle.getInt(CommonUtilities.RESULT);
                    AppLog.Log(TAG, "Result code-----> " + resultCode);
                    if (resultCode == Activity.RESULT_OK) {
                        if (isOrderedBroadcast()) {
                            setResultCode(Activity.RESULT_OK);
                        }
                    } else {
                        AndyUtils.showToast(getString(R.string.register_gcm_failed), R.id.coordinatorLayout, MainActivity.this);
                        if (isOrderedBroadcast()) {
                            setResultCode(Activity.RESULT_CANCELED);
                        }
                        finish();
                    }

                }
            }
        }
    };

    public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            AndyUtils.showCustomProgressDialog(this, "", getResources()
                    .getString(R.string.progress_loading), false);
            new GCMRegisterHendler(MainActivity.this, mHandleMessageReceiver);
        }
    }

    public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {

            if (mHandleMessageReceiver != null) {
                unregisterReceiver(mHandleMessageReceiver);
            }

        }

    }

    @Override
    public void onClick(View v) {

        Intent startRegisterActivity = new Intent(MainActivity.this,
                RegisterActivity.class);
        switch (v.getId()) {

            case R.id.btnFirstRegister:
                if (!AndyUtils.isNetworkAvailable(MainActivity.this)) {
                    AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, MainActivity.this);
                    return;
                }
                startRegisterActivity.putExtra("isSignin", false);
                break;

            case R.id.btnFirstSignIn:
                startRegisterActivity.putExtra("isSignin", true);
                break;
//            case R.id.tvExitOk:
//                if (exitDialog != null)
//                    exitDialog.dismiss();
//                onBackPressed();
//                break;
//            case R.id.tvExitCancel:
//                if (exitDialog != null)
//                    exitDialog.dismiss();
//                break;
            default:
                break;
        }
        startActivity(startRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (isRecieverRegister) {
            unregisterGcmReceiver(mHandleMessageReceiver);

            isRecieverRegister = false;
        }

    }

    @Override
    public void onBackPressed() {
        openExitDialog();
    }

    public void openExitDialog() {
        exitDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.dialog_bg)));
        exitDialog.setContentView(R.layout.exit_layout);
        tvExitOk = (Button) exitDialog.findViewById(R.id.tvExitOk);
        tvExitCancel = (Button) exitDialog.findViewById(R.id.tvExitCancel);
        tvExitOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });
        tvExitCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }
}
