/**
 *
 */
package com.jayeen.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.hb.views.PinnedSectionListView;
import com.jayeen.driver.adapter.HistoryAdapter;
import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.newmodels.HistoryModel;
import com.jayeen.driver.newmodels.Request;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.TaxiParseContent;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class HistoryActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener, AsyncTaskCompleteListener {

    private HistoryAdapter historyAdapter;
    private List<Request> historyList;
    private TaxiParseContent taxiparseContent;
    private ImageView tvEmptyHistory;
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvHistory;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    private ArrayList<Request> historyListOrg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lvHistory = (PinnedSectionListView) findViewById(R.id.lvHistory);
        tvEmptyHistory = (ImageView) findViewById(R.id.tvHistoryEmpty);
        lvHistory.setOnItemClickListener(this);
        historyList = new ArrayList<Request>();
        dateList = new ArrayList<Date>();
        taxiparseContent = new TaxiParseContent(this);
        historyListOrg = new ArrayList<Request>();
        setActionBarTitle(getString(R.string.text_history));
        setActionBarIcon(R.drawable.ub__nav_history_white);

        getHistory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void getHistory() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, HistoryActivity.this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "", getResources().getString(R.string.progress_getting_history), false);
        AppLog.Log("Histoy", "UserId : " + DriverApplication.preferenceHelper.getUserId()
                + " Tocken : " + DriverApplication.preferenceHelper.getSessionToken());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.HISTORY + AndyConstants.Params.ID
                + "=" + DriverApplication.preferenceHelper.getUserId() + "&"
                + AndyConstants.Params.TOKEN + "=" + DriverApplication.preferenceHelper.getSessionToken());
        DriverApplication.requestQueue.add(new VolleyHttpRequest(Method.GET, map, AndyConstants.ServiceCode.HISTORY, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (mSeparatorsSet.contains(position))
            return;

        Request history = historyListOrg.get(position);
//        showBillDialog(history.getBasePrice(), history.getTotal(),
//                history.getDistanceCost(), history.getTimecost(),
//                history.getDistance(), history.getTime(),
//                history.getReferralBonus(), history.getPromoBonus(), history.getPriceperHr(),
//                history.getPriceperKm(), history.getAdmin_per_payment(), history.getDriver_per_payment(),
//                getString(R.string.text_close));
        showBillDialog(history.mBasePrice + "", history.mTotal + "",
                history.mDistanceCost + "", history.mTimeCost + "",
                history.mDistance + "", history.mTime + "",
                history.mReferralBonus + "", history.mPromoBonus + "", history.price_per_unit_time + "",
                history.price_per_unit_distance + "",
                history.waiting_price + "",
                getString(R.string.text_close),true);
//                history.mAdminPerPayment + "", history.mDriverPerPayment + "",

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case AndyConstants.ServiceCode.HISTORY:

                AppLog.Log("TAG", "History Response :" + response);
                HistoryModel historyModel = taxiparseContent.getSingleObject(response, HistoryModel.class);
                if (!historyModel.mSuccess) {
                    if (historyModel.error_code == AndyConstants.INVALID_TOKEN2) {
                        AndyUtils.removeCustomProgressDialog();
                        finish();
                    }
                    return;
                }
                historyListOrg.clear();
                historyList.clear();
                dateList.clear();
                historyList = historyModel.mRequests;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    final Calendar cal = Calendar.getInstance();
                    Collections.sort(historyList, new Comparator<Request>() {
                        @Override
                        public int compare(Request o1, Request o2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            try {
                                String firstStrDate = o1.mDate;
                                String secondStrDate = o2.mDate;
                                Date date2 = dateFormat.parse(secondStrDate);
                                Date date1 = dateFormat.parse(firstStrDate);
                                int value = date2.compareTo(date1);
                                return value;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }

                    });
                    HashSet<Date> listToSet = new HashSet<Date>();

                    for (int i = 0; i < historyList.size(); i++) {
                        AppLog.Log("date", historyList.get(i).mDate + "");
                        if (listToSet.add(sdf.parse(historyList.get(i).mDate))) {
                            dateList.add(sdf.parse(historyList.get(i).mDate));
                        }
                    }
                    for (int i = 0; i < dateList.size(); i++) {
                        cal.setTime(dateList.get(i));
                        Request item = new Request();
                        item.mDate = (sdf.format(dateList.get(i)));
                        historyListOrg.add(item);
                        mSeparatorsSet.add(historyListOrg.size() - 1);
                        for (int j = 0; j < historyList.size(); j++) {
                            Calendar messageTime = Calendar.getInstance();
                            messageTime.setTime(sdf.parse(historyList.get(j).mDate));
                            if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
                                historyListOrg.add(historyList.get(j));
                            }
                        }
                    }
                    if (historyList.size() > 0) {
                        lvHistory.setVisibility(View.VISIBLE);
                        tvEmptyHistory.setVisibility(View.GONE);

                    } else {
                        lvHistory.setVisibility(View.GONE);
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                    }
                    historyAdapter = new HistoryAdapter(this, historyListOrg,
                            mSeparatorsSet);
                    lvHistory.setAdapter(historyAdapter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                AndyUtils.removeCustomProgressDialog();
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
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;

            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
        AndyUtils.removeCustomProgressDialog();
        AndyUtils.showToast(getResources().getString(R.string.connection_error_internal) + "\nTry again! ", R.id.coordinatorLayout, HistoryActivity.this);

    }
}
