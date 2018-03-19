/**
 *
 */
package com.jayeen.customer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.jayeen.customer.adapter.PaymentListAdapter;
import com.jayeen.customer.newmodels.CardsModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jayeen.customer.CustomerApplication.requestQueue;

/**
 * @author nPlus
 */
public class UberWalletActivity extends ActionBarBaseActivitiy implements View.OnClickListener {

    private ListView listViewPayment;
    private PaymentListAdapter adapter;
    private List<CardsModel.Payment> listCards;
    private ImageView tvNoHistory;
    private LinearLayout llPaymentList;
    private TextView tvNoHistoryTxt, tvWalletBalanceTxt, tvMoneyOne, tvMoneytwo, tvMoneythree, tvMoneyfour, tvButtonAddAmount;
    private EditText editMoneyToAdd;
    private TaxiParseContent parseContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = new TaxiParseContent(this);
        setContentView(R.layout.activity_wallet_details);
        setTitle(getString(R.string.text_wallet));
        setIcon(R.drawable.back);
        setIconMenu(R.drawable.ic_payment_white);
        listViewPayment = (ListView) findViewById(R.id.listViewPayment);
        tvWalletBalanceTxt = (TextView) findViewById(R.id.txt_balance_money_wallet);
        tvMoneyOne = (TextView) findViewById(R.id.txt_money_one_wallet);
        tvMoneytwo = (TextView) findViewById(R.id.txt_money_two_wallet);
        tvMoneythree = (TextView) findViewById(R.id.txt_money_three_wallet);
        tvMoneyfour = (TextView) findViewById(R.id.txt_money_four_wallet);
        tvButtonAddAmount = (TextView) findViewById(R.id.txt_btn_add_wallet);
        editMoneyToAdd = (EditText) findViewById(R.id.edit_money_wallet);
        llPaymentList = (LinearLayout) findViewById(R.id.llPaymentList);
        tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
        tvNoHistoryTxt = (TextView) findViewById(R.id.tvNoHistoryTxt);
        tvMoneyOne.setOnClickListener(this);
        tvMoneytwo.setOnClickListener(this);
        tvMoneythree.setOnClickListener(this);
        tvMoneyfour.setOnClickListener(this);
        tvButtonAddAmount.setOnClickListener(this);
        listCards = new ArrayList<CardsModel.Payment>();
        adapter = new PaymentListAdapter(this, listCards, CustomerApplication.preferenceHelper.getDefaultCard());
        listViewPayment.setAdapter(adapter);
        getCards();
        getWalletBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                break;
            case R.id.txt_money_one_wallet:
                tvMoneyOne.setBackgroundResource(R.drawable.prizeselected);
                tvMoneytwo.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneythree.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneyfour.setBackgroundResource(R.drawable.prizeunselected);
                editMoneyToAdd.setText(tvMoneyOne.getText().toString().trim());
                if (tvMoneyOne.getText().toString().trim().length() > 0) {
                    editMoneyToAdd.setSelection(tvMoneyOne.getText().toString().trim().length());
                    editMoneyToAdd.setError(null);
                }
                break;
            case R.id.txt_money_two_wallet:
                tvMoneyOne.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneytwo.setBackgroundResource(R.drawable.prizeselected);
                tvMoneythree.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneyfour.setBackgroundResource(R.drawable.prizeunselected);
                editMoneyToAdd.setText(tvMoneytwo.getText().toString().trim());
                if (tvMoneytwo.getText().toString().trim().length() > 0) {
                    editMoneyToAdd.setSelection(tvMoneytwo.getText().toString().trim().length());
                    editMoneyToAdd.setError(null);
                }
                break;
            case R.id.txt_money_three_wallet:
                tvMoneyOne.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneytwo.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneythree.setBackgroundResource(R.drawable.prizeselected);
                tvMoneyfour.setBackgroundResource(R.drawable.prizeunselected);
                editMoneyToAdd.setText(tvMoneythree.getText().toString().trim());
                if (tvMoneythree.getText().toString().trim().length() > 0) {
                    editMoneyToAdd.setSelection(tvMoneythree.getText().toString().trim().length());
                    editMoneyToAdd.setError(null);
                }
                break;
            case R.id.txt_money_four_wallet:
                tvMoneyOne.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneytwo.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneythree.setBackgroundResource(R.drawable.prizeunselected);
                tvMoneyfour.setBackgroundResource(R.drawable.prizeselected);
                editMoneyToAdd.setText(tvMoneyfour.getText().toString().trim());
                if (tvMoneyfour.getText().toString().trim().length() > 0) {
                    editMoneyToAdd.setSelection(tvMoneyfour.getText().toString().trim().length());
                    editMoneyToAdd.setError(null);
                }
                break;
            case R.id.txt_btn_add_wallet:
                if (isValidate())
                    addWalletAmount(editMoneyToAdd.getText().toString().trim());
                break;
            default:
                break;
        }
    }


    @Override
    protected boolean isValidate() {
        if (TextUtils.isEmpty(editMoneyToAdd.getText().toString())) {
            AndyUtils.showToast(getString(R.string.text_amount_blank), R.id.coordinatorLayout, this);
            editMoneyToAdd.setError("");
            return false;
        } else if (adapter.getNumberofCards() <= 0) {
            AndyUtils.showToast(getString(R.string.text_add_card_wallet), R.id.coordinatorLayout, this);
            return false;
        }
        return true;
    }

    private void getCards() {
        AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_CARDS + Const.Params.ID + "="
                + CustomerApplication.preferenceHelper.getUserId() + "&"
                + Const.Params.TOKEN + "="
                + CustomerApplication.preferenceHelper.getSessionToken());
        requestQueue.add(new VolleyHttpRequest(Method.GET, map, Const.ServiceCode.GET_CARDS, this, this));
    }

    public void addWalletAmount(String amount) {
        AndyUtils.showCustomProgressDialog(this, getString(R.string.text_process_wallet_amt_add), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.ADD_WALLET_AMOUNT);
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.AMOUNT, amount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.ADD_WALLET_AMOUNT, this, this));
    }

    public void getWalletBalance() {
        AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_WALLET_BALANCE + Const.Params.ID + "="
                + CustomerApplication.preferenceHelper.getUserId() + "&"
                + Const.Params.TOKEN + "="
                + CustomerApplication.preferenceHelper.getSessionToken());
        requestQueue.add(new VolleyHttpRequest(Method.GET, map, Const.ServiceCode.WALLET_BALANCE, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.GET_CARDS:
                listCards.clear();
                listCards = parseContent.parseCards(response, listCards);
                if (listCards == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                    break;
                }
                if (listCards.size() > 0) {
                    llPaymentList.setVisibility(View.VISIBLE);
                    tvNoHistory.setVisibility(View.GONE);
                    tvNoHistoryTxt.setVisibility(View.GONE);
                } else {
                    llPaymentList.setVisibility(View.GONE);
                    tvNoHistory.setVisibility(View.VISIBLE);
                    tvNoHistoryTxt.setVisibility(View.VISIBLE);
                }
                adapter = new PaymentListAdapter(this, listCards, CustomerApplication.preferenceHelper.getDefaultCard());
                listViewPayment.setAdapter(adapter);
                break;
            case Const.ServiceCode.WALLET_BALANCE:
                CardsModel.Wallet model = parseContent.getSingleObject(response, CardsModel.Wallet.class);
                if (model == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                } else if (model.success) {
                    if (!TextUtils.isEmpty(model.total_balance_amount))
                        tvWalletBalanceTxt.setText(getString(R.string.payment_unit) + " " + model.total_balance_amount);
                    editMoneyToAdd.setText("");
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                }
                break;
            case Const.ServiceCode.ADD_WALLET_AMOUNT:
                CardsModel addMoneyModel = parseContent.getSingleObject(response, CardsModel.class);
                if (addMoneyModel == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                } else if (addMoneyModel.wallet_maintain == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                } else if (addMoneyModel.success) {
                    if (!TextUtils.isEmpty(addMoneyModel.wallet_maintain.total_balance))
                        tvWalletBalanceTxt.setText(getString(R.string.payment_unit) + " " + addMoneyModel.wallet_maintain.total_balance);
                    editMoneyToAdd.setText("");
                } else {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
    }


}
