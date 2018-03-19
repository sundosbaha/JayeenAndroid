/**
 *
 */
package com.jayeen.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.R;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.newmodels.ApplyReferral;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class ReffralCodeFragment extends UberBaseFragmentRegister implements
        AsyncTaskCompleteListener {
    private MyFontEdittextView etRefCode;
    private String token, id;
    private LinearLayout llErrorMsg;
    private int is_skip = 0;
//    private RequestQueue requestQueue;
    int prefcount = 0;
    private TaxiParseContent parseContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        parseContent = new TaxiParseContent(getActivity());
        super.onCreate(savedInstanceState);
        token = getArguments().getString(Const.Params.TOKEN);
        id = getArguments().getString(Const.Params.ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        activity.setIconMenu(R.drawable.nav_referral_white);
        activity.setTitle(activity.getString(R.string.text_referral_code));
        activity.btnNotification.setVisibility(View.INVISIBLE);
        activity.mDotsView.selectDot(2);
        View refView = inflater.inflate(R.layout.ref_code_fragment, container,
                false);
        etRefCode = (MyFontEdittextView) refView.findViewById(R.id.etRefCode);
        etRefCode.setHint(activity.getString(R.string.text_enter_ref_code));
        llErrorMsg = (LinearLayout) refView.findViewById(R.id.llErrorMsg);
        refView.findViewById(R.id.btnRefSubmit).setOnClickListener(this);
        refView.findViewById(R.id.btnSkip).setOnClickListener(this);

        return refView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        etRefCode.requestFocus();
        activity.showKeyboard(etRefCode);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        activity.currentFragment = Const.FRAGMENT_REFFREAL;
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnRefSubmit:
                if (etRefCode.getText().length() == 0) {

                    AndyUtils.showToast(activity.getString(R.string.text_blank_ref_code), R.id.coordinatorLayout, activity);
                    return;
                } else {
                    if (!AndyUtils.isNetworkAvailable(activity)) {
                        AndyUtils.showToast(activity.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activity);
                        return;
                    }
                    is_skip = 0;
                    applyReffralCode(true);
                }
                break;
            case R.id.btnSkip:
                is_skip = 1;
                applyReffralCode(true);
                this.OnBackPressed();
                break;

            default:
                break;
        }
    }

    private void applyReffralCode(boolean isShowLoader) {
        if (isShowLoader)
            AndyUtils.showCustomProgressDialog(activity,activity.
                    getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.APPLY_REFFRAL_CODE);
        map.put(Const.Params.REFERRAL_CODE, etRefCode.getText().toString());
        map.put(Const.Params.ID, id);
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.IS_SKIP, String.valueOf(is_skip));
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.APPLY_REFFRAL_CODE, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(Const.TAG, "Apply-Referral Response ::: " + response);
        switch (serviceCode) {
            case Const.ServiceCode.APPLY_REFFRAL_CODE:
                ApplyReferral applyReferral = parseContent.getSingleObject(response, ApplyReferral.class);
                if (applyReferral.success) {
                    CustomerApplication.preferenceHelper.putReferee(1);
                    gotoPaymentFragment();
                } else {
                    llErrorMsg.setVisibility(View.VISIBLE);
                    etRefCode.requestFocus();
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected boolean isValidate() {
        return false;
    }

    @Override
    public boolean OnBackPressed() {
        return true;
    }

    private void gotoPaymentFragment() {
        activity.startActivity(new Intent(activity, MainDrawerActivity.class));
        activity.finish();
//        UberAddPaymentFragmentRegister paymentFragment = new UberAddPaymentFragmentRegister();
//        Bundle bundle = new Bundle();
//        bundle.putString(Const.Params.TOKEN, token);
//        bundle.putString(Const.Params.ID, id);
//        paymentFragment.setArguments(bundle);
//        activity.addFragment(paymentFragment, false,
//                Const.FRAGMENT_PAYMENT_REGISTER);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());

    }

}
