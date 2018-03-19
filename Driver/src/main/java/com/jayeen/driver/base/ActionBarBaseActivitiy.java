package com.jayeen.driver.base;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MainActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.fragment.ClientRequestFragmentNew;
import com.jayeen.driver.locationupdate.LocationUpdateService;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.widget.MyFontButton;
import com.jayeen.driver.widget.MyFontTextViewTitle;
import com.android.volley.Response.ErrorListener;

import java.text.DecimalFormat;
import java.util.Calendar;

abstract public class ActionBarBaseActivitiy extends AppCompatActivity
        implements View.OnClickListener, AsyncTaskCompleteListener, ErrorListener {
    protected ActionBar actionBar;
    private int mFragmentId = 0;
    private String mFragmentTag = null;
    public ImageButton btnNotification, btnActionMenu;
    public MyFontTextViewTitle tvTitle;
    public MyFontButton btnActionInfo;
    public String currentFragment;
    Toolbar toolbar;
    private Dialog mDialog;
    private DecimalFormat decimalFormat, perHourFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.custom_action_bar, null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        btnNotification = (ImageButton) customActionBarView.findViewById(R.id.btnActionNotification);
        btnNotification.setOnClickListener(this);
        tvTitle = (MyFontTextViewTitle) customActionBarView.findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(this);
        btnActionInfo = (MyFontButton) customActionBarView.findViewById(R.id.btnActionInfo);
        btnActionInfo.setOnClickListener(this);
        btnActionMenu = (ImageButton) customActionBarView.findViewById(R.id.btnActionMenu);
        btnActionMenu.setOnClickListener(this);
        try {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setCustomView(customActionBarView,
                    new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setSupportActionBar(toolbar);
        } catch (Exception er) {
            Log.i("" + getLocalClassName(), "" + er.getMessage());
        }
        initDialog();
    }

    void initDialog() {
        mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.bill_layout);
        decimalFormat = new DecimalFormat("0.00");
        perHourFormat = new DecimalFormat("0.0");
    }


    public void setFbTag(String tag) {
        mFragmentId = 0;
        mFragmentTag = tag;
    }

    public void showBillDialog(String basePrice, String total,
                               String distanceCost, String timeCost, String distance, String time,
                               String referralBonus, String promoBonus, String PriceperHr, String PriceperKm,
                               String waitingPrice,
                               String btnTitle, final boolean isHistory) {
        String basePriceDouble = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(basePrice != null ? basePrice : "0.00"))));
        String totalTmp = total;
        if (total.contains(",")) {
            totalTmp = total.replace(",", ".");
        }
        totalTmp = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(totalTmp != null ? totalTmp : "0.00"))));
        String distCostTmp = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(distanceCost != null ? distanceCost : "0.00"))));
        String timeCostDouble = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(timeCost != null ? timeCost : "0.00"))));
        String referralBonusDouble = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(referralBonus != null ? referralBonus : "0.00"))));
        String promoBonusDouble = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(promoBonus != null ? promoBonus : "0.00"))));
//        String adminPerPercentage = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(admin_per_payment != null ? admin_per_payment : "0.00"))));
//        String driverPerPercentage = String.valueOf(decimalFormat.format(Double.parseDouble(ReplaceComma(driver_per_payment != null ? driver_per_payment : "0.00"))));
        ((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(basePriceDouble);
        if (PriceperKm != null) {
            ((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile)).setText(PriceperKm + " " + getResources().getString(R.string.text_cost_per_mile));
        } else {
            ((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile)).setText(String.valueOf(perHourFormat.format(0.00))
                    + getResources().getString(R.string.text_cost_per_mile));
        }
        if (PriceperHr != null) {
            ((TextView) mDialog.findViewById(R.id.tvBillTimePerHour)).setText(PriceperHr + " " + getResources().getString(R.string.text_cost_per_hour));
        } else {
            ((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
                    .setText(String.valueOf(perHourFormat.format((0.00)))
                            + getResources().getString(
                            R.string.text_cost_per_hour));
        }
        ((TextView) mDialog.findViewById(R.id.tvDis1)).setText(distCostTmp);
        ((TextView) mDialog.findViewById(R.id.tvTime1)).setText(timeCostDouble);
        ((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);
        ((TextView) mDialog.findViewById(R.id.tvReferralBonus)).setText(referralBonusDouble);
        ((TextView) mDialog.findViewById(R.id.tvPromoBonus)).setText(promoBonusDouble);
        ((TextView) mDialog.findViewById(R.id.tvWaitingTimePrice)).setText(waitingPrice);
        Button btnConfirm = (Button) mDialog
                .findViewById(R.id.btnBillDialogClose);
        if (!TextUtils.isEmpty(btnTitle)) {
            btnConfirm.setText(btnTitle);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (!isHistory)
                    if (DriverApplication.preferenceHelper.getInstantJobId() == DriverApplication.preferenceHelper.getRequestId()) {
                        addFragment(new ClientRequestFragmentNew(), false,
                                AndyConstants.CLIENT_REQUEST_TAG, true);
                        DriverApplication.preferenceHelper.putIsTripOn(false);
                    }

            }
        });
        mDialog.setCancelable(true);
        mDialog.show();

    }

    private String ReplaceComma(String StrValue) {
        String StrResult = StrValue;
        if (StrValue.contains(",")) {
            StrResult = StrValue.replace(",", ".");
        }
        return StrResult;
    }

    public void startLocationUpdateService() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Intent intent = new Intent(this, LocationUpdateService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 5, pintent);
    }

    protected void stopLocationUpdateService() {

        Intent intent = new Intent(this, LocationUpdateService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = null;
        if (mFragmentId > 0) {
            fragment = getSupportFragmentManager().findFragmentById(mFragmentId);
        } else if (mFragmentTag != null && !mFragmentTag.equalsIgnoreCase("")) {
            fragment = getSupportFragmentManager().findFragmentByTag(mFragmentTag);
        }
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void startActivityForResult(Intent intent, int requestCode, int fragmentId) {
        mFragmentId = fragmentId;
        mFragmentTag = null;
        super.startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(Intent intent, int requestCode, String fragmentTag) {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode);
    }

    public void setActionBarTitle(String title) {
        tvTitle.setText(title);
    }

    public void setActionBarIcon(int image) {
        btnActionMenu.setImageResource(image);
    }

    public void startActivityForResult(Intent intent, int requestCode, int fragmentId, Bundle options) {
        mFragmentId = fragmentId;
        mFragmentTag = null;
        super.startActivityForResult(intent, requestCode, options);
    }

    public void startActivityForResult(Intent intent, int requestCode,
                                       String fragmentTag, Bundle options) {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode, options);
    }

    public void startIntentSenderForResult(Intent intent, int requestCode,
                                           String fragmentTag, Bundle options) {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    @Deprecated
    public void startIntentSenderForResult(IntentSender intent,
                                           int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags, String fragmentTag) throws IntentSender.SendIntentException {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startIntentSenderForResult(intent, requestCode, fillInIntent,
                flagsMask, flagsValues, extraFlags);
    }

    @Override
    @Deprecated
    public void startIntentSenderForResult(IntentSender intent,
                                           int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags, Bundle options)
            throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent,
                flagsMask, flagsValues, extraFlags, options);
    }

    public void startIntentSenderForResult(IntentSender intent,
                                           int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags, Bundle options, String fragmentTag)
            throws IntentSender.SendIntentException {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startIntentSenderForResult(intent, requestCode, fillInIntent,
                flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode,
                                       Bundle options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    public void addFragment(Fragment fragment, boolean addToBackStack,
                            String tag, boolean isAnimate) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (isAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
        }

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    public void removeAllFragment(Fragment replaceFragment,
                                  boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        manager.popBackStackImmediate(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, replaceFragment);
        ft.commit();

    }

    public void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);

        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void clearAll() {
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
