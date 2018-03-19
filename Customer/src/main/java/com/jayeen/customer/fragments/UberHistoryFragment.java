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
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hb.views.PinnedSectionListView;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.HistoryActivityNew;
import com.jayeen.customer.R;
import com.jayeen.customer.adapter.HistoryAdapter;
import com.jayeen.customer.models.History;
import com.jayeen.customer.newmodels.HistoryModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class UberHistoryFragment extends Fragment implements OnItemClickListener ,View.OnClickListener,AsyncTaskCompleteListener,Response.ErrorListener{
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvHistory;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryModel.Request> historyList;
    private ArrayList<HistoryModel.Request> historyListOrg;
    private TaxiParseContent parseContent;
    private ImageView tvNoHistory;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    Calendar cal = Calendar.getInstance();
    private int day;
    private int month;
    private int year;
    private TextView tvNoHistoryTxt;

//    private RequestQueue requestQueue;
    int prefcount = 0;
    HistoryActivityNew activityHistory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_history, container,false);
        lvHistory = (PinnedSectionListView) view.findViewById(R.id.lvHistory);
        lvHistory.setOnItemClickListener(this);
        historyList = new ArrayList<>();
        tvNoHistory = (ImageView) view.findViewById(R.id.ivEmptyView);
        tvNoHistoryTxt= (TextView) view.findViewById(R.id.tvNoHistoryTxt);
        parseContent = new TaxiParseContent(activityHistory);
        dateList = new ArrayList<Date>();
        historyListOrg = new ArrayList<>();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        getHistory();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activityHistory = (HistoryActivityNew) getActivity();
        super.onCreate(savedInstanceState);
    }

    private void getHistory() {
        if (!AndyUtils.isNetworkAvailable(activityHistory)) {
            AndyUtils.showToast(activityHistory.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activityHistory);
            return;
        }
        AndyUtils.showCustomProgressDialog(activityHistory,
                activityHistory.getString(R.string.progress_getting_history),
                false, null);
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.URL,
                Const.ServiceType.HISTORY + Const.Params.ID + "="
                        + CustomerApplication.preferenceHelper.getUserId() + "&"
                        + Const.Params.TOKEN + "="
                        + CustomerApplication.preferenceHelper.getSessionToken());
        AppLog.Log("History", map.get("url"));
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                Const.ServiceCode.HISTORY, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        if (mSeparatorsSet.contains(position))
            return;
        HistoryModel.Request history = historyListOrg.get(position);
        activityHistory.showBillDialog(history.timeCost, history.total,
                history.distanceCost, history.basePrice,
                history.time, history.distance,
                history.promoBonus, history.referralBonus,
                null, history.setbaseDistance,
                history.distancePrice, history.pricePerUnitTime,false,history.waiting_price);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case Const.ServiceCode.HISTORY:
                AppLog.Log("TAG", "History Response :" + response);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    final Calendar cal = Calendar.getInstance();
                    historyListOrg.clear();
                    historyList.clear();
                    dateList.clear();
                    historyList = parseContent.parseHistory(response, historyList);
                    Collections.sort(historyList, new Comparator<HistoryModel.Request>() {
                        @Override
                        public int compare(HistoryModel.Request o1, HistoryModel.Request o2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd hh:mm:ss");
                            try {
                                String firstStrDate = o1.date;
                                String secondStrDate = o2.date;
                                Date date2 = dateFormat.parse(secondStrDate);
                                Date date1 = dateFormat.parse(firstStrDate);
                                return date2.compareTo(date1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });
                    HashSet<Date> listToSet = new HashSet<Date>();
                    for (int i = 0; i < historyList.size(); i++) {
                        AppLog.Log("date", historyList.get(i).date + "");
                        if (listToSet.add(sdf.parse(historyList.get(i).date))) {
                            dateList.add(sdf.parse(historyList.get(i).date));
                        }
                    }
                    ArrayList<HistoryModel.Request>history_temp_list=historyList;
                    for (int i = 0; i <dateList.size(); i++) {
                        cal.setTime(dateList.get(i));
                        HistoryModel.Request item = new HistoryModel().new Request();
                        item=historyList.get(i);
//                        item.date = (sdf.format(dateList.get(i)));
                        String temp_date= (sdf.format(dateList.get(i)));
                        item.date=temp_date;
                        historyListOrg.add(item);
                        mSeparatorsSet.add(historyListOrg.size() - 1);
                        historyList=history_temp_list;
                        for (int j = 0; j < historyList.size(); j++) {
                            Calendar messageTime = Calendar.getInstance();
                            messageTime.setTime(sdf.parse(historyList.get(j).date));
                            if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
                                historyListOrg.add(historyList.get(j));
                            }
                        }
                    }
                    if (historyList.size() > 0) {
                        lvHistory.setVisibility(View.VISIBLE);
                        tvNoHistory.setVisibility(View.GONE);
                        tvNoHistoryTxt.setVisibility(View.GONE);
                    } else {
                        lvHistory.setVisibility(View.GONE);
                        tvNoHistory.setVisibility(View.VISIBLE);
                        tvNoHistoryTxt.setVisibility(View.VISIBLE);
                    }
                    Log.i("historyListOrg size  ", "" + historyListOrg.size());
                    historyAdapter = new HistoryAdapter(activityHistory, historyListOrg, mSeparatorsSet);
                    lvHistory.setAdapter(historyAdapter);
                } catch (ParseException e) {
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
        AndyUtils.showToast("Error Contact Server Try again!", R.id.coordinatorLayout, activityHistory);
    }

    @Override
    public void onClick(View v) {

    }

    public static UberHistoryFragment newInstance(int index) {
        UberHistoryFragment f = new UberHistoryFragment();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);
        return f;
    }
}