package com.jayeen.customer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.adapter.HistoryPagerAdapter;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;


/**
 *
 */
public class HistoryActivityNew extends ActionBarBaseActivitiy {
//        RequestQueue requestQueue;
    TabLayout tabLayout;
    ViewPager viewPager;
    HistoryPagerAdapter adapter;
    private ArrayList<String> tabTitles=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_new);
        setIconMenu(R.drawable.ub__nav_history_white);
        setTitle(getString(R.string.text_trips));
        getSupportActionBar().setElevation(0);//removing the actionbar elevation
        setIcon(R.drawable.back);
        tabLayout = (TabLayout) findViewById(R.id.a_history_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.a_history_viewPager);
        try {
            btnSosNotification.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPagerAdapter();
    }

    private void setPagerAdapter() {
        tabLayout.removeAllTabs();
        tabTitles.add(this.getResources().getString(R.string.text_past));
        tabTitles.add(this.getResources().getString(R.string.text_upcoming));
        FragmentManager fm = getSupportFragmentManager();
        adapter = new HistoryPagerAdapter(fm, 2, this,tabTitles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void cancelBooking(String Id) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,getResources().getString(R.string.progress_getting_history),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.URL, Const.ServiceType.SCHEDULECANCEL);
        map.put(Const.Params.ID, Id);
        requestQueue.add(new VolleyHttpRequest(Request.Method.POST, map,
                Const.ServiceCode.SCHEDULECANCEL, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            default:
                break;
        }
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
    protected boolean isValidate() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.toString());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, this);
    }
}
