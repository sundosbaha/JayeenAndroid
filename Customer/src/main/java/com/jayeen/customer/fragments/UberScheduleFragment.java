package com.jayeen.customer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hb.views.PinnedSectionListView;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.HistoryActivityNew;
import com.jayeen.customer.R;
import com.jayeen.customer.adapter.ScheduleHistoryAdapterNew;
import com.jayeen.customer.models.ScheduleHistory;
import com.jayeen.customer.newmodels.SchedulehistoryModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
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

public class UberScheduleFragment extends Fragment implements OnItemClickListener, AsyncTaskCompleteListener, Response.ErrorListener {
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

    HistoryActivityNew activityHistory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulehistory, container, false);
        lvHistory = (PinnedSectionListView) view.findViewById(R.id.sch_lvHistory);
        lvHistory.setOnItemClickListener(this);
        historyList = new ArrayList<>();
        tvNoHistory = (ImageView) view.findViewById(R.id.sch_ivEmptyView);
        dateList = new ArrayList<Date>();
        historyListOrg = new ArrayList<ScheduleHistory>();

        taxiParseContent = new TaxiParseContent(activityHistory);
        getscheduleHistory();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activityHistory = (HistoryActivityNew) getActivity();
        super.onCreate(savedInstanceState);
    }

    private void getscheduleHistory() {
        if (!AndyUtils.isNetworkAvailable(activityHistory)) {
            AndyUtils.showToast(activityHistory.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activityHistory);
            return;
        }
        AndyUtils.showCustomProgressDialog(activityHistory,
                activityHistory.getString(R.string.progress_getting_history),
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

                        schhistoryAdapter = new ScheduleHistoryAdapterNew(activityHistory, historyList,
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
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.toString());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(activityHistory.getString(R.string.error_contact_server), R.id.coordinatorLayout, activityHistory);
    }


    public static UberScheduleFragment newInstance(int index) {
        UberScheduleFragment f = new UberScheduleFragment();
        return f;
    }
}