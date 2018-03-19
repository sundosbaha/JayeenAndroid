package com.jayeen.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;

import com.android.volley.toolbox.Volley;
import com.jayeen.customer.component.MyTitleFontTextView;
import com.jayeen.customer.fragments.CheckOTPCodeFragment;
import com.jayeen.customer.fragments.UberBaseFragmentRegister;
import com.jayeen.customer.fragments.UberRegisterFragment;
import com.jayeen.customer.fragments.UberSignInFragment;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import static com.jayeen.customer.CustomerApplication.requestQueue;


/**
 * @author Hardik A Bhalodi
 */
@SuppressLint("NewApi")

abstract public class ActionBarBaseActivitiy extends AppCompatActivity
        implements OnClickListener, AsyncTaskCompleteListener, ErrorListener {

    public ActionBar actionBar;
    private int mFragmentId = 0;
    private String mFragmentTag = null;
    public ImageButton btnNotification, btnActionMenu;
    public MyTitleFontTextView tvTitle;
    public AutoCompleteTextView etSource;
    public String currentFragment = null;
    public LinearLayout layoutDestination;
    public ImageButton imgClearDst;
    public Button btnSosNotification;
    public Button btnPolice, btnConfirmCall, btnCancelCall;
    TextView confirmtext;
    public Button btnAmbulance;
    public Button btnFirestation, btnSosBack;
    Dialog sosdialog;
    public String number, contentval;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    private static String[] PERMISSIONS_PHONECALL = {Manifest.permission.CALL_PHONE};
    Dialog mDialogsos;
//    private RequestQueue requestQueue;

    protected abstract boolean isValidate();

    private Dialog rDialog;
    RegisterActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.custom_action_bar,
                null);
        layoutDestination = (LinearLayout) customActionBarView
                .findViewById(R.id.layoutDestination);
        btnNotification = (ImageButton) customActionBarView
                .findViewById(R.id.btnActionNotification);
        btnSosNotification = (Button) customActionBarView.findViewById(R.id.btnSosNotification);
        btnSosNotification.setOnClickListener(this);
        btnNotification.setOnClickListener(this);

        imgClearDst = (ImageButton) customActionBarView
                .findViewById(R.id.imgClearDst);

        tvTitle = (MyTitleFontTextView) customActionBarView
                .findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(this);

        etSource = (AutoCompleteTextView) customActionBarView
                .findViewById(R.id.etEnterSouce);

        btnActionMenu = (ImageButton) customActionBarView
                .findViewById(R.id.btnActionMenu);
        btnActionMenu.setOnClickListener(this);
        try {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setCustomView(customActionBarView,
                    new ActionBar.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.btnShare).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(
                                Intent.EXTRA_TEXT,
                                getString(R.string.text_iam_using)
                                        + getString(R.string.app_name)
                                        + getString(R.string.text_try_itout)
                                        + getString(R.string.app_name)
                                        + getString(R.string.text_play_link)
                                        + getPackageName());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.app_name) + " App !");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,
                        getString(R.string.text_share_app)));
            }
        });
    }

    public void setFbTag(String tag) {
        mFragmentId = 0;
        mFragmentTag = tag;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = null;

        if (mFragmentId > 0) {
            fragment = getSupportFragmentManager()
                    .findFragmentById(mFragmentId);
        } else if (mFragmentTag != null && !mFragmentTag.equalsIgnoreCase("")) {
            fragment = getSupportFragmentManager().findFragmentByTag(
                    mFragmentTag);
        }
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode,
                                       int fragmentId) {
        mFragmentId = fragmentId;
        mFragmentTag = null;
        super.startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(Intent intent, int requestCode,
                                       String fragmentTag) {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    @Deprecated
    public void startIntentSenderForResult(IntentSender intent,
                                           int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags) throws SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent,
                flagsMask, flagsValues, extraFlags);
    }

    public void startIntentSenderForResult(IntentSender intent,
                                           int requestCode, Intent fillInIntent, int flagsMask,
                                           int flagsValues, int extraFlags, String fragmentTag)
            throws SendIntentException {

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
            throws SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent,
                flagsMask, flagsValues, extraFlags, options);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode,
                                       Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GIVE_TIPS:
                AndyUtils.removeCustomProgressDialog();
                if (new TaxiParseContent(this).isSuccess(response)) {
                    if (rDialog != null)
                        rDialog.dismiss();
                }
                break;
            default:
                break;
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack,
                            String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, fragment, tag);
        ft.commitAllowingStateLoss();

    }

    public void addFragmentWithStateLoss(Fragment fragment,
                                         boolean addToBackStack, String tag) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, fragment, tag);
        ft.commitAllowingStateLoss();
    }


    public void clearBackStack() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = manager
                        .getBackStackEntryAt(0);
                manager.popBackStack(first.getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Log.i("TAGACTIONBAR", "Inside ActionbarBaseActivity BackPress");
        if (!TextUtils.isEmpty(currentFragment)) {
            Log.i("TAGACTIONBAR", "Inside Main Actionbar" + currentFragment);
            FragmentManager manager = getSupportFragmentManager();
            UberBaseFragmentRegister frag = ((UberBaseFragmentRegister) manager
                    .findFragmentByTag(currentFragment));

            if (frag != null && frag.isVisible()) {
                Log.i("TAGACTIONBAR", "Fragment IF");

                if (currentFragment.equals(Const.FRAGMENT_OTP)) {
                    Log.i("TAGACTIONBAR", "Fragment IF");
                    Log.i("TAGACTIONBAR", "Entry Count" + manager.getBackStackEntryCount());
                    CustomerApplication.preferenceHelper.putIsRegEditCheck(true);
                    manager.popBackStack(Const.FRAGMENT_OTP, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Fragment uberReigster;
                    String TAG;
                    if (CustomerApplication.preferenceHelper.getLoginBy().equalsIgnoreCase(Const.MANUAL)) {
                        uberReigster = new UberRegisterFragment();
                        TAG = Const.FRAGMENT_REGISTER;
                    } else {
                        uberReigster = new UberSignInFragment();
                        TAG = Const.FRAGMENT_SIGNIN;
                    }
                    addFragment(uberReigster, false, TAG);
                    try {
                        List<Fragment> f = manager.getFragments();
                        CheckOTPCodeFragment cFragment = (CheckOTPCodeFragment) f.get(f.size() - 1);
                        if (cFragment != null) {
                            cFragment.RemoveUSerfromDb();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    frag.OnBackPressed();
                }
            } else {
                Log.i("TAGACTIONBAR", "Fragment ELSE");
                super.onBackPressed();
            }
        } else {
            Log.i("TAGACTIONBAR", "Main ELSE CURRENT NULL");
            super.onBackPressed();

        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        return true;
    }

    public void openExitDialog() {
        final Dialog mDialog = new Dialog(this);//, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.exit_layout);
        mDialog.findViewById(R.id.tvExitOk).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
        mDialog.findViewById(R.id.tvExitCancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
        if (!isFinishing())
            mDialog.show();
    }

    public void showBillDialog(String timeCost, String total, String distCost,
                               String basePrice, String time, String distance, String promoBouns,
                               String referralBouns, String btnTitle, String BaseDistance,
                               String PriceperHr, String PriceperKm, boolean isTips, String waiting_price) {
        final Dialog mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.bill_layout);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DecimalFormat perHourFormat = new DecimalFormat("0.0");
        String basePricetmp = decimalFormat.format(Double
                .parseDouble(ReplaceComma(basePrice)))+"";
        String totalTmp = decimalFormat.format(Double
                .parseDouble(ReplaceComma(total)))+"";
        String distCostTmp = decimalFormat.format(Double
                .parseDouble(ReplaceComma(distCost)));
        String timeCostTmp = decimalFormat.format(Double
                .parseDouble(ReplaceComma(timeCost)));

        AppLog.Log("Distacne", distance);
        AppLog.Log("Time", time);

        ((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(basePricetmp);
        if (PriceperKm != null) {
            ((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
                    .setText(PriceperKm + " " + getResources().getString(R.string.text_cost_per_mile));
        } else {
            ((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
                    .setText(perHourFormat.format(0.00)
                            + getResources().getString(
                            R.string.text_cost_per_mile));
        }
        if (PriceperHr != null) {
            ((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
                    .setText(PriceperHr + " " + getResources().getString(R.string.text_cost_per_hour));
        } else {
            ((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
                    .setText(perHourFormat.format((0.00))
                            + getResources().getString(
                            R.string.text_cost_per_hour));
        }
        ((TextView) mDialog.findViewById(R.id.tvDis1)).setText(distCostTmp);

        ((TextView) mDialog.findViewById(R.id.tvTime1)).setText(timeCostTmp);
//**********************************************************************
        ((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp+"");
        ((TextView) mDialog.findViewById(R.id.tvPromoBonus))
                .setText(decimalFormat.format(Double.parseDouble(ReplaceComma(promoBouns))));
        ((TextView) mDialog.findViewById(R.id.tvReferralBonus))
                .setText(decimalFormat.format(Double.parseDouble(ReplaceComma(referralBouns))));
        ((TextView) mDialog.findViewById(R.id.tvWaitingTime))
                .setText(changeEmptyValues(waiting_price));
        Button btnConfirm = (Button) mDialog
                .findViewById(R.id.btnBillDialogClose);
//        Button btnTips = (Button) mDialog
//                .findViewById(R.id.btntipsDialogClose);

//        btnTips.setVisibility(isTips ? View.VISIBLE : View.GONE);
//        if (!TextUtils.isEmpty(btnTitle)) {
//            btnConfirm.setText(btnTitle);
//        }
//        btnConfirm.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//            }
//        });
//
//        btnTips.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//                showTipsDialog();
//            }
//        });

        if (!TextUtils.isEmpty(btnTitle)) {
            btnConfirm.setText(btnTitle);
        }
        btnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        if (!isFinishing())
            mDialog.show();
    }

    private String changeEmptyValues(String value) {
        String resultStr = "0.00";
        try {

            if (value != null) {
                if (value.isEmpty()) {
                    return resultStr;
                } else {
                    resultStr = new DecimalFormat("0.00").format(Double.parseDouble(value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    private void showTipsDialog() {
        rDialog = new Dialog(this);
        rDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rDialog.setContentView(R.layout.fragment_tips_dialog);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        rDialog.setCancelable(false);
        rDialog.setCanceledOnTouchOutside(false);
        rDialog.getWindow().setLayout(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
        Button btnTipsSkip = (Button) rDialog.findViewById(R.id.btn_skip);
        Button btnTipsSubmit = (Button) rDialog.findViewById(R.id.btn_submit);
        final EditText edt_tips = (EditText) rDialog.findViewById(R.id.edt_tips);
        btnTipsSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rDialog.dismiss();
            }
        });
        btnTipsSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_tips.getText().toString().length() > 0) {
                    AddTips(edt_tips.getText().toString());
                } else {
                    Toast.makeText(ActionBarBaseActivitiy.this, getString(R.string.text_error_tips), Toast.LENGTH_LONG).show();
                }
            }
        });
        if (!isFinishing())
            rDialog.show();
    }

    protected void AddTips(String tips) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), true, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GIVE_TIPS);
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId()+"");
        map.put(Const.Params.TOKEN,
                CustomerApplication.preferenceHelper.getSessionToken()+"");
        map.put(Const.Params.REQUEST_ID,
                CustomerApplication.preferenceHelper.getRequestId()+"");
        map.put(Const.Params.TIPS, tips+"");
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.GIVE_TIPS, this, this));
    }

    private String ReplaceComma(String StrValue) {
        String StrResult = StrValue;
        if (StrValue.contains(",")) {
            StrResult = StrValue.replace(",", ".");
        }
        return StrResult;
    }

    public void ShowSOSDialog(String recnumber, String content) {
        number = recnumber;
        contentval = content;

        sosdialog = new Dialog(this);
        sosdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sosdialog.setContentView(R.layout.sosdialog_info);

        btnConfirmCall = (Button) sosdialog.findViewById(R.id.sosconfirmcallbtn);
        btnCancelCall = (Button) sosdialog.findViewById(R.id.sosconfirmclosebtn);
        confirmtext = (TextView) sosdialog.findViewById(R.id.confirmtext1);
        confirmtext.setText(this.getResources().getString(R.string.sosdialoginfo) + " " + contentval + " " + "-" + recnumber);
        btnConfirmCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sosdialog.dismiss();
                Log.i("SOSTAG", "Number" + number);
//                if (number == null || number.isEmpty())
//                    number = "123456";
                call(number);
            }
        });
        btnCancelCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sosdialog.dismiss();
            }
        });
        if (!isFinishing())
            sosdialog.show();
        sosdialog.setCanceledOnTouchOutside(false);

    }

    public void call(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        } else {
            if (!TextUtils.isEmpty(number)) {
                mDialogsos.dismiss();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            }
        }
    }


    public void setTitle(String str) {
        tvTitle.setText(str);
    }

    public void setIconMenu(int img) {
        btnActionMenu.setImageResource(img);
    }

    public void setIcon(int img) {
        btnNotification.setImageResource(img);
    }

    public void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    public void goToMainRegActivity(boolean isSignIn) {
        if (isSignIn) {
            Intent regintent = new Intent(this, RegisterActivity.class);
            regintent.putExtra("isSignin", isSignIn);
            startActivity(regintent);
        }
    }

}
