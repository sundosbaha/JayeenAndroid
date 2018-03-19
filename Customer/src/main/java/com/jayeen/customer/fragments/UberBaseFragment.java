package com.jayeen.customer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View.OnClickListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.R;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.Const;

import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;

@SuppressLint("ValidFragment")
abstract public class UberBaseFragment extends Fragment implements
        OnClickListener, AsyncTaskCompleteListener, ErrorListener {
    public static MainDrawerActivity activity;
//    public RequestQueue requestQueue;
    int prefcount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainDrawerActivity) getActivity();
//        requestQueue = Volley.newRequestQueue(activity);
    }

    protected abstract boolean isValidate();

    @Override
    public void onTaskCompleted(final String response, int serviceCode) {

    }

    protected void login() {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(Const.Params.EMAIL, CustomerApplication.preferenceHelper.getEmail());
        map.put(Const.Params.PASSWORD, CustomerApplication.preferenceHelper.getPassword());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN, CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));

    }

    protected void loginSocial(String id, String loginType) {
        if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.no_internet), R.id.coordinatorLayout, activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.LOGIN);
        map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.DEVICE_TOKEN,
                CustomerApplication.preferenceHelper.getDeviceToken());
        map.put(Const.Params.LOGIN_BY, loginType);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.LOGIN, this, this));
    }

    public String getResourcesfromID(int resoureceID) {
        String resource_result = "";
        try {
            getActivity().getResources().getString(resoureceID);
        } catch (Exception e) {
            e.getMessage();
        }
        return resource_result;
    }
    protected String getStr(int resValue) {
        return activity.getString(resValue);
    }

    protected int getColor(int resValue) {
        return ContextCompat.getColor(activity, resValue);
    }
}
