package com.jayeen.customer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.component.MyFontButton;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.newmodels.RequestStatusModel;
import com.jayeen.customer.newmodels.SuccessModel;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class UberFeedbackFragment extends UberBaseFragment {
    private EditText etComment;
    private RatingBar rtBar;
    private Button btnSubmit;
    private Button btnShare;

    private CircularImageView ivDriverImage;
    private RequestStatusModel driver;
    private TextView tvDistance, tvTime, tvClientName;
//    private RequestQueue requestQueue;
    int prefcount = 0;
    String finalcommentval = "";
    Dialog sharedialog;
    private MyFontButton btnConfirmShare, btnCancelShare;
    private MyFontEdittextView editcommentshare;
    String finalsharetext = "";
    TaxiParseContent parseContent;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseContent = new TaxiParseContent(getActivity());
        driver = getArguments().getParcelable(Const.DRIVER);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(activity.getString(R.string.text_feedback));
        activity.tvTitle.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.feedback, container, false);

        tvClientName = (TextView) view.findViewById(R.id.tvClientName);
        etComment = (EditText) view.findViewById(R.id.etComment);
        rtBar = (RatingBar) view.findViewById(R.id.ratingBar);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnShare = (Button) view.findViewById(R.id.btnshare);
        ivDriverImage = (CircularImageView) view.findViewById(R.id.ivDriverImage);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvDistance.setText(decimalFormat.format(Double.parseDouble(
                driver.bill.distance != null || !driver.bill.distance.isEmpty() ? driver.bill.distance : "0.00"))
                + " " + driver.bill.unit);

        tvTime.setText((int) (Double.parseDouble(driver.bill.time))
                + " " +activity. getString(R.string.text_mins));
        activity.btnNotification.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (driver.walker.picture != null && !driver.walker.picture.isEmpty())
            Picasso.with(getActivity())
                    .load(driver.walker.picture)
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user)
                    .into(ivDriverImage);

        tvClientName.setText(driver.walker.firstName + " "
                + driver.walker.lastName);

        activity.showBillDialog(driver.bill.timeCost, driver
                        .bill.total, driver.bill.distanceCost,
                driver.bill.basePrice,
                driver.bill.time, driver.bill.distance,
                driver.bill.promoBonus, driver.bill
                        .referralBonus,
                activity.getString(R.string.text_confirm), driver.bill.baseDistance,
                driver.chargeDetails != null ? driver.chargeDetails.pricePerUnitTime : driver.bill.pricePerUnitTime,
                driver.chargeDetails != null ? driver.chargeDetails.distancePrice : driver.bill.distancePrice,true,
                driver.bill.waiting_price);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (isValidate()) {
                    rating();
                } else
                    AndyUtils.showToast(activity.getString(R.string.error_empty_rating), R.id.coordinatorLayout, activity);
                break;

            case R.id.btnshare:
                finalcommentval = etComment.getText().toString();
                if (!TextUtils.isEmpty(finalcommentval)) {
                    finalsharetext = activity.getString(R.string.app_name) + " Customer" + "\n" + finalcommentval + "\n" +
                            activity.getString(R.string.text_play_link) + getActivity().getPackageName();
                    Share(getShareApplication(), finalsharetext);
                } else {
                    AndyUtils.showToast(activity.getString(R.string.enter_comment), R.id.coordinatorLayout, activity);
                }
                break;
            default:
                break;
        }
    }

    private List<String> getShareApplication() {
        List<String> mList = new ArrayList<String>();
        mList.add("com.facebook.katana");
        mList.add("com.whatsapp");
        mList.add("com.google.android.apps.plus");
        return mList;

    }

    private void Share(List<String> PackageName, String Text) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(Intent.ACTION_SEND);
                    targetedShare.setType("text/plain"); // put here your mime type
                    if (PackageName.contains(info.activityInfo.packageName.toLowerCase())) {
                        targetedShare.putExtra(Intent.EXTRA_TEXT, Text);
                        targetedShare.setPackage(info.activityInfo.packageName.toLowerCase());
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), "Share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected boolean isValidate() {
        if (rtBar.getRating() <= 0)
            return false;
        return true;
    }

    private void rating() {
        finalcommentval = etComment.getText().toString();
        AndyUtils.showCustomProgressDialog(activity,activity.
                getString(R.string.text_rating), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.RATING);
        map.put(Const.Params.TOKEN, CustomerApplication.preferenceHelper.getSessionToken());
        map.put(Const.Params.ID, CustomerApplication.preferenceHelper.getUserId());
        map.put(Const.Params.COMMENT, etComment.getText().toString());
        map.put(Const.Params.RATING, String.valueOf(((int) rtBar.getRating())));
        map.put(Const.Params.REQUEST_ID,
                String.valueOf(CustomerApplication.preferenceHelper.getRequestId()));
        // new HttpRequester(activity, map, Const.ServiceCode.RATING, this);
        prefcount = CustomerApplication.preferenceHelper.getRequestTotal();
        prefcount++;
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.RATING, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.RATING:
                SuccessModel successModel = parseContent.getSingleObject(response, SuccessModel.class);
                AndyUtils.removeCustomProgressDialog();
                if (successModel.success) {
                    CustomerApplication.preferenceHelper.clearRequestData();
                    AndyUtils.showToast(activity.getString(R.string.text_feedback_completed), R.id.coordinatorLayout, activity);
                    activity.gotoMapFragment();
                    CustomerApplication.preferenceHelper.putIsTripOn(false);
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log(Const.TAG, error.getMessage());
        AndyUtils.removeCustomProgressDialog();
    }
}
