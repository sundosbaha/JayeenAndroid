package com.jayeen.driver.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jayeen.driver.MapActivity;
import com.jayeen.driver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstantJobFragment extends DialogFragment {

    public Dialog dialog;
    private View view;
    EditText PhoneNumber;
    Button btn;
    MapActivity activity;

    public InstantJobFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_instant_job, container, false);
        PhoneNumber = (EditText) view.findViewById(R.id.d_phonenumber);
        btn = (Button) view.findViewById(R.id.d_submit_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = PhoneNumber.getText().toString();
                if (number.length() > 0) {
                    activity.doInstantJob(PhoneNumber.getText().toString());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(activity, "Please enter Valid Number", Toast.LENGTH_LONG).show();
                }
            }
        });
        activity = (MapActivity) getActivity();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View NmView = inflater.inflate(R.layout.fragment_instant_job, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(NmView);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
        return dialog;
    }


}
