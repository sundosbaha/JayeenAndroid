/**
 *
 */
package com.jayeen.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hb.views.PinnedSectionListView;
import com.jayeen.customer.adapter.ScheduleHistoryAdapterNew;
import com.jayeen.customer.models.ScheduleHistory;
import com.jayeen.customer.newmodels.SchedulehistoryModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static com.jayeen.customer.CustomerApplication.requestQueue;


public class ScheduleListActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener {
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvHistory;
    private ScheduleHistoryAdapterNew schhistoryAdapter;
    private List<SchedulehistoryModel.Request> historyList;
    private ArrayList<ScheduleHistory> historyListOrg;
    private ImageView tvNoHistory;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    Calendar cal = Calendar.getInstance();
//    private RequestQueue requestQueue;
    int prefcount = 0;
    private TaxiParseContent taxiParseContent;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulehistory);
        setIconMenu(R.drawable.ub__nav_history_white);
        setTitle(getString(R.string.text_schedulehistory));
        setIcon(R.drawable.back);
        lvHistory = (PinnedSectionListView) findViewById(R.id.sch_lvHistory);
        lvHistory.setOnItemClickListener(this);
        historyList = new ArrayList<>();
        tvNoHistory = (ImageView) findViewById(R.id.sch_ivEmptyView);
        dateList = new ArrayList<Date>();
        historyListOrg = new ArrayList<ScheduleHistory>();
        try {
            btnSosNotification.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        taxiParseContent = new TaxiParseContent(this);
        getscheduleHistory();

    }

    private void getscheduleHistory() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_getting_history),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.SCHEDULEHISTORY);
        map.put(Const.Params.SCHUSERID, CustomerApplication.preferenceHelper.getUserId());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.SCHEDULEHISTORY, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (mSeparatorsSet.contains(position))
            return;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.SCHEDULECANCEL:
                if (response.contains("true"))
                    getscheduleHistory();
                break;
            case Const.ServiceCode.SCHEDULEHISTORY:
                AppLog.Log("TAG", "History Response :" + response);
                try {
                    SchedulehistoryModel historylistmodel = taxiParseContent.getSingleObject(response, SchedulehistoryModel.class);
                    if (historylistmodel.success) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        final Calendar cal = Calendar.getInstance();
                        historyListOrg.clear();
                        historyList.clear();
                        dateList.clear();
                        historyList = historylistmodel.requests;
                        if (historyList.size() > 0) {
                            lvHistory.setVisibility(View.VISIBLE);
                            tvNoHistory.setVisibility(View.GONE);
                        } else {
                            lvHistory.setVisibility(View.GONE);
                            tvNoHistory.setVisibility(View.VISIBLE);
                        }
                        Log.i("historyListOrg size  ", "" + historyListOrg.size());

                        schhistoryAdapter = new ScheduleHistoryAdapterNew(this, historyList,
                                mSeparatorsSet);
                        lvHistory.setAdapter(schhistoryAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

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
        AndyUtils.showToast("Error Contact Server Try again!", R.id.coordinatorLayout, this);
    }

    public void CancelBooking(String Id) {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_getting_history),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.URL, Const.ServiceType.SCHEDULECANCEL);
        map.put(Const.Params.ID, Id);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.SCHEDULECANCEL, this, this));
    }
}
