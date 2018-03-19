package com.jayeen.driver.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.utills.StopWatchTextView;

/**
 */
public class WaitTripFragment extends DialogFragment {

    public Dialog dialog;
    View view;
    TextView WaitMinDuration;
    StopWatchTextView stopwatch;

    public WaitTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wait_trip, container, false);
        WaitMinDuration = (TextView) view.findViewById(R.id.d_wait_txt_min);
        setCancelable(false);
        view.findViewById(R.id.d_wait_trip_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopwatch.finalSec > 30) {
                    stopwatch.finalTime++;
                }

                if (DriverApplication.preferenceHelper.getIsTripStart()) {
                    DriverApplication.preferenceHelper.putTripTimerMin((stopwatch.finalTime - Long.parseLong(DriverApplication.preferenceHelper.getTimerMin())) + "");
                } else {
                    DriverApplication.preferenceHelper.putTimerMin(stopwatch.finalTime + "");
                }
                DriverApplication.preferenceHelper.putTotalTimerMin(stopwatch.finalTime + "");
                DriverApplication.preferenceHelper.putIsWaitDialogVisible(false);
                AppLog.Log(getClass().getName(), stopwatch.finalTime + "");
                stopwatch.pause();
                dismiss();
            }
        });
        initStopWatch();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View NmView = inflater.inflate(R.layout.fragment_wait_trip, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(NmView);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    void initStopWatch() {
        if (stopwatch == null) {
            stopwatch = new StopWatchTextView(WaitMinDuration, 1000);
            stopwatch.start();
        } else {
            stopwatch.resume(WaitMinDuration);
        }

    }

}
