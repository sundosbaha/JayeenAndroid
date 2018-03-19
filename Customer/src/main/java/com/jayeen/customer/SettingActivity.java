package com.jayeen.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.Locale;

public class SettingActivity extends ActionBarBaseActivitiy {
    CardView card_lang;
    TextView tv_lang_short, tv_lang_long, txt_language;
    int resultcode = 1;

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setIconMenu(R.drawable.promotion_white);
        setTitle(getString(R.string.title_activity_setting));
        setIcon(R.drawable.back);
        LinearLayout lay = (LinearLayout) findViewById(R.id.lay_select_Language);
        txt_language = (TextView) findViewById(R.id.textView_language);
        tv_lang_short = (TextView) findViewById(R.id.tv_lang_short);
        tv_lang_long = (TextView) findViewById(R.id.tv_lang_long);
        card_lang = (CardView) findViewById(R.id.card_lang);

        tv_lang_short.setText((CustomerApplication.preferenceHelper.getlang_short() == null) ? "ar" : CustomerApplication.preferenceHelper.getlang_short());
        tv_lang_long.setText("(" + ((CustomerApplication.preferenceHelper.getlang_long() == null) ? "Arabic" : CustomerApplication.preferenceHelper.getlang_long()) + ")");
        CustomerApplication.preferenceHelper.getlang_long();
        card_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialog();
            }

        });

    }

    private void showAlertDialog() {
        final String[] items = {
                "English", "Arabic"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(getString(R.string.text_make_select));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                txt_language.setText(items[item]);
//                tv_lang_long.setText(items[item]);
                String language = "English";
                language = items[item];
                String loc;
//                if (language.equalsIgnoreCase("French")) {
//                    loc = "fr";
//                }else{
//                    loc="en";
//                }

                if (language.equalsIgnoreCase("Arabic")) {
                    loc = "ar";
                }else {
                    loc = "en";
                    language="English";
                }
                CustomerApplication.preferenceHelper.putlang_short(loc);
                CustomerApplication.preferenceHelper.putlang_long(language);
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

                resultcode = 2;
                CustomerApplication.preferenceHelper.putIsLangChanged(true);
                onBackPressed();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }

    @Override
    public void onBackPressed() {
        setResult(resultcode);
        super.onBackPressed();
    }

}
