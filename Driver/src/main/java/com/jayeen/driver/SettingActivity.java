package com.jayeen.driver;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Locale;


public class SettingActivity extends ActionBarBaseActivitiy implements
        OnCheckedChangeListener, AsyncTaskCompleteListener {
    private SwitchCompat switchSetting, switchSound;
    private TaxiParseContent parseContent;
    CardView card_lang;
    TextView tv_lang_short, tv_lang_long;
    CardView switchSound_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        parseContent = new TaxiParseContent(this);
        switchSetting = (SwitchCompat) findViewById(R.id.switchAvaibility);
        switchSetting.setVisibility(View.GONE);
        switchSound = (SwitchCompat) findViewById(R.id.switchSound);
        switchSound_card = (CardView) findViewById(R.id.switchSound_card);
        setActionBarTitle(getString(R.string.text_setting));
        setActionBarIcon(R.drawable.promotion_white);
        switchSound.setChecked(DriverApplication.preferenceHelper.getSoundAvailability());
        switchSound.setOnCheckedChangeListener(this);
        switchSound_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchSound.isChecked()) {
                    switchSound.setChecked(false);
                } else {
                    switchSound.setChecked(true);
                }
                AppLog.Log("Setting Activity Sound switch", "" + switchSound.isChecked());
                DriverApplication.preferenceHelper.putSoundAvailability(switchSound.isChecked());
            }
        });
        tv_lang_short = (TextView) findViewById(R.id.tv_lang_short);
        tv_lang_long = (TextView) findViewById(R.id.tv_lang_long);
        card_lang = (CardView) findViewById(R.id.card_lang);
        tv_lang_short.setText((DriverApplication.preferenceHelper.getlang_short() == null) ? "ar" : DriverApplication.preferenceHelper.getlang_short());
        tv_lang_long.setText("(" + ((DriverApplication.preferenceHelper.getlang_long() == null) ? "Arabic" : DriverApplication.preferenceHelper.getlang_long()) + ")");
        DriverApplication.preferenceHelper.getlang_long();
        card_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void checkState() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, SettingActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_getting_avaibility), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
                + "=" + DriverApplication.preferenceHelper.getUserId() + "&" + AndyConstants.Params.TOKEN + "="
                + DriverApplication.preferenceHelper.getSessionToken());
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.CHECK_STATE, this, this));
    }

    private void changeState() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, SettingActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_changing_avaibilty), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.TOGGLE_STATE);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.POST, map, AndyConstants.ServiceCode.TOGGLE_STATE, this, this));
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        AppLog.Log("TAG", "On checked change listener");
        switch (buttonView.getId()) {
            case R.id.switchAvaibility:
                changeState();
                break;
            case R.id.switchSound:
                AppLog.Log("Setting Activity Sound switch", "" + switchSound.isChecked());
                DriverApplication.preferenceHelper.putSoundAvailability(switchSound.isChecked());

                break;
            default:
                break;
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case AndyConstants.ServiceCode.CHECK_STATE:
            case AndyConstants.ServiceCode.TOGGLE_STATE:

                DriverApplication.preferenceHelper.putIsActive(false);
                DriverApplication.preferenceHelper.putDriverOffline(false);
                if (!parseContent.isSuccess(response)) {
                    return;
                }
                AppLog.Log("TAG", "toggle state:" + response);
                if (parseContent.parseAvaibilty(response)) {
                    switchSetting.setOnCheckedChangeListener(null);
                    switchSetting.setChecked(true);

                } else {
                    switchSetting.setOnCheckedChangeListener(null);
                    switchSetting.setChecked(false);
                }
                switchSetting.setOnCheckedChangeListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
    }

    private void showAlertDialog() {
        final String[] items = {"English", "Arabic"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(R.string.text_make_selection);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String language = items[item];
                String loc;
                if (language.equalsIgnoreCase("Arabic")) {
                    loc = "ar";
                } else {
                    loc = "en";
                    language="English";
                }
                DriverApplication.preferenceHelper.putlang_short(loc);
                DriverApplication.preferenceHelper.putlang_long(language);
                tv_lang_short.setText(loc);
                Locale myLocale = new Locale(loc);
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
//                   to set the language refresh to same activity
//                Intent refresh = new Intent(SettingActivity.this, SettingActivity.class);
//                finish();
//                startActivity(refresh);
                DriverApplication.preferenceHelper.putIsLangChanged(true);
                onBackPressed();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}

