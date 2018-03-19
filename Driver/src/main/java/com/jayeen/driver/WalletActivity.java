package com.jayeen.driver;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.newmodels.LoginModel;
import com.jayeen.driver.newmodels.WalletModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontTextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;

public class WalletActivity extends ActionBarBaseActivitiy implements AsyncTaskCompleteListener {
    TextView a_txt_wallet_amount;
    Button a_btn_withdraw_money;
    String TAG = getClass().getName();
    TaxiParseContent parseContent;
    WalletModel walletModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        parseContent = new TaxiParseContent(this);
        setActionBarTitle(getString(R.string.text_wallet));
        setActionBarIcon(R.drawable.ic_wallet);
        a_txt_wallet_amount = (TextView) findViewById(R.id.a_txt_wallet_amount);
        a_btn_withdraw_money = (Button) findViewById(R.id.a_btn_withdraw_money);
        a_btn_withdraw_money.setOnClickListener(this);
        getWalletAmount();
    }

    private void getWalletAmount() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getString(R.string.toast_no_internet), R.id.coordinatorLayout, this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getString(R.string.progress_acc_info), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.GET_WALLET_BALANCE);
        map.put(AndyConstants.Params.WALKER_ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken() + "");
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map, AndyConstants.ServiceCode.GET_WALLET_MONEY, this, this));
    }

    private void withdrawWalletAmount(String amount) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getString(R.string.toast_no_internet), R.id.coordinatorLayout, this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getString(R.string.progress_send_request), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.WALLET_MONEY_WITHDRAW);
        map.put(AndyConstants.Params.WALKER_ID, DriverApplication.preferenceHelper.getUserId() + "");
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken() + "");
        map.put(AndyConstants.Params.AMOUNT, amount);
        DriverApplication.requestQueue.add(
                new VolleyHttpRequest(Request.Method.POST, map, AndyConstants.ServiceCode.WITHDRAW_WALLET_MONEY, this, this));
    }

    Dialog widrawalDialog;
    EditText dialog_withdraw_wallet;
    Button dialog_cancel_wallet, dialog_confirm_wallet;

    private void openWidrawalDialog() {
        if (widrawalDialog != null) {
            if (widrawalDialog.isShowing())
                widrawalDialog.dismiss();
            widrawalDialog = null;
        }
        widrawalDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        widrawalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        widrawalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        widrawalDialog.setContentView(R.layout.dialog_withdrawal);
        widrawalDialog.setCancelable(false);
        dialog_withdraw_wallet = (EditText) widrawalDialog.findViewById(R.id.dialog_withdraw_wallet);
        dialog_cancel_wallet = (Button) widrawalDialog.findViewById(R.id.dialog_cancel_wallet);
        dialog_confirm_wallet = (Button) widrawalDialog.findViewById(R.id.dialog_confirm_wallet);
        dialog_cancel_wallet.setOnClickListener(this);
        dialog_confirm_wallet.setOnClickListener(this);

        dialog_cancel_wallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (widrawalDialog != null)
                    widrawalDialog.dismiss();
            }
        });
        if (!isFinishing())
            widrawalDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;
            case R.id.a_btn_withdraw_money:
                if (walletModel != null)
                    if (walletModel.mTotalBalance <=0.00 ) {
                        AndyUtils.showToast(getString(R.string.text_insuff_fund), R.id.coordinatorLayout, this);
                        return;
                    }
                openWidrawalDialog();
                break;
            case R.id.dialog_cancel_wallet:
                if (widrawalDialog != null)
                    widrawalDialog.dismiss();
                break;
            case R.id.dialog_confirm_wallet:
                if (walletModel != null)
                    if(dialog_withdraw_wallet.getText().toString().trim().isEmpty()){
                        Toast.makeText(this, "Enter Valid Amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (walletModel.mTotalBalance <= Double.parseDouble(dialog_withdraw_wallet.getText().toString().trim())) {
                        Toast.makeText(this, getString(R.string.text_insuff_fund), Toast.LENGTH_SHORT).show();
                        return;
                    }
                withdrawWalletAmount(dialog_withdraw_wallet.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        super.onTaskCompleted(response, serviceCode);
        AppLog.Log(TAG, response);
        switch (serviceCode) {
            case AndyConstants.ServiceCode.GET_WALLET_MONEY:
                AndyUtils.removeCustomProgressDialog();
                LoginModel model = parseContent.getSingleObject(response, LoginModel.class);
                if (model == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                    break;
                }
                if (model.success) {
                    if (model.wallet != null) {
                        walletModel = model.wallet;
                        a_txt_wallet_amount.setText(getString(R.string.costarica_currency) + " " + model.wallet.mTotalBalance);
                    }
                } else {
                    AndyUtils.showToast(model.erro, R.id.coordinatorLayout, this);
                }
                break;
            case AndyConstants.ServiceCode.WITHDRAW_WALLET_MONEY:
                AndyUtils.removeCustomProgressDialog();
                LoginModel object = parseContent.getSingleObject(response, LoginModel.class);
                if (object == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                    break;
                }
                if (object.success) {
                    AndyUtils.showToast(getString(R.string.text_withdrawl_done), R.id.coordinatorLayout, this);
                    getWalletAmount();
                } else {
                    AndyUtils.showToast(object.erro, R.id.coordinatorLayout, this);
                }
                if (widrawalDialog != null)
                    widrawalDialog.dismiss();
                break;
        }
    }

}
