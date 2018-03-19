/**
 *
 */
package com.jayeen.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.jayeen.customer.adapter.PaymentListAdapter;
import com.jayeen.customer.newmodels.CardTokenAdd;
import com.jayeen.customer.newmodels.CardsModel;
import com.jayeen.customer.newmodels.LoginModel;
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
 * @author Hardik A Bhalodi
 */
public class UberViewPaymentActivity extends ActionBarBaseActivitiy {

    private ListView listViewPayment;
    private PaymentListAdapter adapter;
    private List<CardsModel.Payment> listCards;
    private int REQUEST_ADD_CARD = 1;
    private ImageView tvNoHistory, ivCredit, ivCash;
    private TextView tvHeaderText;
    private View v;
    private TextView btnAddNewPayment;
    private int paymentMode;
    private LinearLayout llPaymentList;
    //    private RequestQueue requestQueue;
    int prefcount = 0;
    private TextView tvNoHistoryTxt;
    private TaxiParseContent parseContent;
    private String CLINET_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = new TaxiParseContent(this);
        setContentView(R.layout.activity_view_payment);
        setTitle(getString(R.string.text_cards));
        setIcon(R.drawable.back);
        setIconMenu(R.drawable.ic_payment_white);
        listViewPayment = (ListView) findViewById(R.id.listViewPayment);
        llPaymentList = (LinearLayout) findViewById(R.id.llPaymentList);
        tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
        tvNoHistoryTxt = (TextView) findViewById(R.id.tvNoHistoryTxt);
        tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
        btnAddNewPayment = (TextView) findViewById(R.id.tvAddNewPayment);

        ivCash = (ImageView) findViewById(R.id.ivCash);
        ivCredit = (ImageView) findViewById(R.id.ivCredit);

        btnAddNewPayment.setOnClickListener(this);
        paymentMode = (int) CustomerApplication.preferenceHelper.getPaymentMode();
        v = findViewById(R.id.line);
        listCards = new ArrayList<CardsModel.Payment>();
        adapter = new PaymentListAdapter(this, listCards, CustomerApplication.preferenceHelper.getDefaultCard());
        listViewPayment.setAdapter(adapter);
        try {
            btnSosNotification.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCards();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnActionNotification:
                onBackPressed();
                break;
            case R.id.tvAddNewPayment:
                /*startActivityForResult(new Intent(this,
                        UberAddPaymentActivity.class), REQUEST_ADD_CARD);*/
                getClientToken();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected boolean isValidate() {
        // TODO Auto-generated method stub
        return false;
    }

    private void getCards() {
        AndyUtils.showCustomProgressDialog(this,
                getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL,
                Const.ServiceType.GET_CARDS + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken());
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.GET_CARDS, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.GET_CARDS:
                listCards.clear();
                listCards = parseContent.parseCards(response, listCards);
                AppLog.Log("UberViewPayment", "listCards : " + listCards.size());
                if (listCards.size() > 0) {
                    llPaymentList.setVisibility(View.VISIBLE);
                    tvNoHistory.setVisibility(View.GONE);
                    tvNoHistoryTxt.setVisibility(View.GONE);
                    paymentMode = 1;
                    tvHeaderText.setVisibility(View.VISIBLE);
                } else {
                    llPaymentList.setVisibility(View.GONE);
                    tvNoHistory.setVisibility(View.VISIBLE);
                    tvNoHistoryTxt.setVisibility(View.VISIBLE);
                    tvHeaderText.setVisibility(View.GONE);
                    paymentMode = 0;
                }
                adapter = new PaymentListAdapter(this, listCards, CustomerApplication.preferenceHelper.getDefaultCard());
                listViewPayment.setAdapter(adapter);

                break;
            case Const.ServiceCode.BRAIN_TREE_CLINET_TOKEN:
                LoginModel msuccessModel = parseContent.getSingleObject(response, LoginModel.class);
                if(msuccessModel != null)
                if (msuccessModel.success) {
                    CLINET_TOKEN = msuccessModel.clientToken;
                    onBraintreeSubmit();
                } else {
                    AppLog.Log(Const.TAG, "Braintree----Can't Get Token");
                }
                break;
            case Const.ServiceCode.ADD_CARD:
                CardTokenAdd cardTokenAdd = parseContent.getSingleObject(response, CardTokenAdd.class);
                if(cardTokenAdd != null)
                if (cardTokenAdd.success) {
                    AndyUtils.showToast(getString(R.string.text_add_card_scucess), R.id.coordinatorLayout, this);
                    getCards();
                } else {
                    if (cardTokenAdd.error != null) {
                        AndyUtils.showToast(cardTokenAdd.error, R.id.coordinatorLayout, this);
                    }


                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_OK:
                getCards();
                break;
            case Const.ServiceCode.BRAIN_TREE_DROP_IN:
                if (resultCode == Activity.RESULT_OK) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    PaymentMethodNonce payDetails = result.getPaymentMethodNonce();
                    addCard(payDetails.getNonce(), payDetails.getDescription().replace("ending in ", ""), payDetails.getTypeLabel());
                } else {
                    if(data!=null){
                    Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                    error.printStackTrace();
                    AppLog.handleException(Const.TAG, error);}
                }
                break;
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(CLINET_TOKEN);
        startActivityForResult(dropInRequest.getIntent(this), Const.ServiceCode.BRAIN_TREE_DROP_IN);
    }

    private void getClientToken() {
        AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.GET_BRAINTREE_TOKEN);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map, Const.ServiceCode.BRAIN_TREE_CLINET_TOKEN, this, this));
    }

    private void addCard(String braintreeToken, String lastFour, String type) {
        AndyUtils.showCustomProgressDialog(this, getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.ADD_CARD);
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.STRIPE_TOKEN, braintreeToken);
        map.put(Const.Params.LAST_FOUR, lastFour);
        map.put(Const.Params.CARD_TYPE, type);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map, Const.ServiceCode.ADD_CARD, this, this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
    }
}
