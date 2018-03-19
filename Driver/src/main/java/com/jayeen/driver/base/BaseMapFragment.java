package com.jayeen.driver.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.ParseContent;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;

import java.util.HashMap;

public abstract class BaseMapFragment extends Fragment implements
        OnClickListener, AsyncTaskCompleteListener, ErrorListener {
    protected MapActivity mapActivity;
    protected ParseContent parseContent;
    protected TaxiParseContent taxiparseContent;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapActivity = (MapActivity) getActivity();
        requestQueue = Volley.newRequestQueue(mapActivity);
        parseContent = new ParseContent(mapActivity);
        taxiparseContent = new TaxiParseContent(mapActivity);
    }

    public void startActivityForResult(Intent intent, int requestCode,
                                       String fragmentTag) {
        mapActivity.startActivityForResult(intent, requestCode, fragmentTag);
    }

    @Override
    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }


}