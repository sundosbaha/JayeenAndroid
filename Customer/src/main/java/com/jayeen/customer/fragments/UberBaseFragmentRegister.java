package com.jayeen.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View.OnClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.MainDrawerActivity;
import com.jayeen.customer.RegisterActivity;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;

/**
 * @author Hardik A Bhalodi
 */
import static com.jayeen.customer.CustomerApplication.requestQueue;
abstract public class UberBaseFragmentRegister extends Fragment implements
        OnClickListener, AsyncTaskCompleteListener, ErrorListener {
    RegisterActivity activity;
//    RequestQueue requestQueue;
    String currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (RegisterActivity) getActivity();
//        requestQueue = Volley.newRequestQueue(getActivity());
        currentFragment = activity.currentFragment;
    }

    protected abstract boolean isValidate();

    public boolean OnBackPressed() {
        Log.i("TAG", "Inside BaseFragementRegister BackPress");
        activity.startActivity(new Intent(activity, MainDrawerActivity.class));
        activity.finish();
        return true;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.actionBar.show();

    }

}
