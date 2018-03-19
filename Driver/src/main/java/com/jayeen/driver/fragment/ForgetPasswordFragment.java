/**
 *
 */
package com.jayeen.driver.fragment;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.jayeen.driver.R;
import com.jayeen.driver.base.BaseRegisterFragment;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.ParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontEdittextView;
import static com.jayeen.driver.DriverApplication.requestQueue;

public class ForgetPasswordFragment extends BaseRegisterFragment implements
        AsyncTaskCompleteListener {
    private MyFontEdittextView etEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View forgetView = inflater.inflate(R.layout.fragment_forgetpassword,
                container, false);
        etEmail = (MyFontEdittextView) forgetView
                .findViewById(R.id.etForgetEmail);
        forgetView.findViewById(R.id.tvForgetSubmit).setOnClickListener(this);
        return forgetView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerActivity.setActionBarTitle(getResources().getString(
                R.string.text_forget_password));
        etEmail.requestFocus();
        showKeyboard(etEmail);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvForgetSubmit:
                if (etEmail.getText().length() == 0) {
                    AndyUtils.showToast(getResources().getString(R.string.error_empty_email), R.id.coordinatorLayout, registerActivity);
                    return;
                } else if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {
                    AndyUtils.showToast(getResources().getString(R.string.error_valid_email), R.id.coordinatorLayout, registerActivity);
                    return;
                } else {
                    if (!AndyUtils.isNetworkAvailable(registerActivity)) {
                        AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, registerActivity);
                        return;
                    }
                    forgetPassowrd();
                }
                break;
            case R.id.btnActionNotification:
                // OnBackPressed();
                break;

            default:
                break;
        }
    }

    private void forgetPassowrd() {

        AndyUtils.showCustomProgressDialog(registerActivity, "",
                getString(R.string.progress_loading), false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.FORGET_PASSWORD);
        map.put(AndyConstants.Params.TYPE, 1 + "");
        map.put(AndyConstants.Params.EMAIL, etEmail.getText().toString());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.FORGET_PASSWORD, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case AndyConstants.ServiceCode.FORGET_PASSWORD:
                AppLog.Log("TAG", "forget res:" + response);
                if (new ParseContent(registerActivity).isSuccess(response)) {
                    AndyUtils.showToast(getResources().getString(R.string.toast_forget_password_success), R.id.coordinatorLayout, registerActivity);

                }
                break;

            default:
                break;
        }

    }

    public void showKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
    }

}
