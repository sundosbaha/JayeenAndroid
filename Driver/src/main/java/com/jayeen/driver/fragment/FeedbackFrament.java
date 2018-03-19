package com.jayeen.driver.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.jayeen.driver.DriverApplication;
import com.jayeen.driver.R;
import com.jayeen.driver.base.BaseMapFragment;
import com.jayeen.driver.newmodels.CheckChangeStateModel;
import com.jayeen.driver.newmodels.WalkerCompleteModel;
import com.jayeen.driver.parse.AsyncTaskCompleteListener;
import com.jayeen.driver.parse.VolleyHttpRequest;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;
import com.jayeen.driver.utills.AppLog;
import com.jayeen.driver.widget.MyFontEdittextView;
import com.jayeen.driver.widget.MyFontTextView;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

import static com.jayeen.driver.DriverApplication.requestQueue;

public class FeedbackFrament extends BaseMapFragment implements
        AsyncTaskCompleteListener {

    private MyFontEdittextView etFeedbackComment;
    private ImageView ivDriverImage;
    private RatingBar ratingFeedback;
    private MyFontTextView tvTime, tvDistance, tvClientName;

    private final String TAG = "FeedbackFrament";
    private MyFontTextView tvAmount;
    private String paymentMode;
    DecimalFormat twoDecimalFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feedbackFragmentView = inflater.inflate(
                R.layout.fragment_feedback, container, false);
        etFeedbackComment = (MyFontEdittextView) feedbackFragmentView.findViewById(R.id.etFeedbackComment);
        tvTime = (MyFontTextView) feedbackFragmentView.findViewById(R.id.tvFeedBackTime);
        tvDistance = (MyFontTextView) feedbackFragmentView.findViewById(R.id.tvFeedbackDistance);
        tvAmount = (MyFontTextView) feedbackFragmentView.findViewById(R.id.tvFeedbackAmount);
        ratingFeedback = (RatingBar) feedbackFragmentView.findViewById(R.id.ratingFeedback);
        ivDriverImage = (ImageView) feedbackFragmentView.findViewById(R.id.ivFeedbackDriverImage);
        tvClientName = (MyFontTextView) feedbackFragmentView.findViewById(R.id.tvClientName);
        mapActivity.setActionBarTitle(getResources().getString(
                R.string.text_feedback));
        feedbackFragmentView.findViewById(R.id.tvFeedbackSubmit).setOnClickListener(this);
        return feedbackFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WalkerCompleteModel requestDetail = (WalkerCompleteModel) getArguments()
                .getSerializable(AndyConstants.REQUEST_DETAIL);
        if (requestDetail.owner != null) {
            if (requestDetail.owner.picture != null && !requestDetail.owner.picture.isEmpty())
                Picasso.with(getActivity())
                        .load(requestDetail.owner.picture)
                        .placeholder(R.drawable.user)   // optional
                        .error(R.drawable.user)
                        .into(ivDriverImage);
            tvClientName.setText(requestDetail.owner.name);
        } else if (requestDetail.request.owner != null) {
            if (requestDetail.request.owner.picture != null && !requestDetail.request.owner.picture.isEmpty())
                Picasso.with(getActivity())
                        .load(requestDetail.request.owner.picture)
                        .error(R.drawable.user)
                        .placeholder(R.drawable.user).into(ivDriverImage);
            tvClientName.setText(requestDetail.request.owner.name);
        }
        twoDecimalFormat = new DecimalFormat("0.00");

        if (requestDetail.request.bill != null) {
            mapActivity.showBillDialog(requestDetail.request.bill.basePrice + "",
                    requestDetail.request.bill.total + "",
                    requestDetail.request.bill.distanceCost + "",
                    requestDetail.request.bill.timeCost + "",
                    requestDetail.request.bill.distance + "",
                    requestDetail.request.bill.time + "",
                    requestDetail.request.bill.referralBonus + "",
                    requestDetail.request.bill.promoBonus + "",
                    requestDetail.request.chargeDetails.pricePerUnitTime + "",
                    requestDetail.request.chargeDetails.distancePrice + "",
                    requestDetail.request.bill.waiting_price + "",
                    getString(R.string.text_confirm), false);
            tvTime.setText((int) Double.parseDouble(ReplaceComma(requestDetail.request.bill.time + "")) + " " + getString(R.string.text_mins));
            tvDistance.setText(twoDecimalFormat.format(Double.parseDouble(ReplaceComma(requestDetail.request.bill.distance))) + " "
                    + requestDetail.request.bill.unit);
            if (DriverApplication.preferenceHelper.getPaymentType() == AndyConstants.CASH)
                paymentMode = getString(R.string.text_type_cash);
            else
                paymentMode = getString(R.string.text_type_card);
            tvAmount.setText(getString(R.string.costarica_currency) + new DecimalFormat("0.00").format(Double
                    .parseDouble(ReplaceComma(requestDetail.request.bill.total + ""))) + " " + paymentMode);
        }
    }

    private String ReplaceComma(String StrValue) {
        String StrResult = StrValue;
        if (StrValue.contains(",")) {
            StrResult = StrValue.replace(",", ".");
        }
        return StrResult;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFeedbackSubmit:
                if (ratingFeedback.getRating() == 0) {
                    AndyUtils.showToast(mapActivity.getResources().getString(
                            R.string.text_empty_rating), R.id.coordinatorLayout, mapActivity);
                } else {
                    giveRating();
                }
                break;
            default:
                break;
        }
    }

    private void giveRating() {
        if (!AndyUtils.isNetworkAvailable(mapActivity)) {
            AndyUtils.showToast(getResources().getString(R.string.toast_no_internet), R.id.coordinatorLayout, mapActivity);
            return;
        }

        AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
                .getString(R.string.progress_rating), false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.RATING);
        map.put(AndyConstants.Params.ID, DriverApplication.preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, DriverApplication.preferenceHelper.getSessionToken());
        map.put(AndyConstants.Params.REQUEST_ID,
                String.valueOf(DriverApplication.preferenceHelper.getRequestId()));
        map.put(AndyConstants.Params.RATING,
                String.valueOf(ratingFeedback.getRating()));
        map.put(AndyConstants.Params.COMMENT, etFeedbackComment.getText()
                .toString().trim());
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.RATING, this, this));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case AndyConstants.ServiceCode.RATING:
                AppLog.Log(TAG, "rating response" + response);
                CheckChangeStateModel changeStateModel = taxiparseContent.getSingleObject(response, CheckChangeStateModel.class);
                if (changeStateModel == null) {
                    AndyUtils.showToast(getString(R.string.error_contact_server), R.id.coordinatorLayout, mapActivity);
                    return;
                }
                if (changeStateModel.mSuccess) {
                    DriverApplication.preferenceHelper.clearRequestData();
                    AndyUtils.showToast(mapActivity.getResources().getString(R.string.toast_feedback_success), R.id.coordinatorLayout, mapActivity);
                    mapActivity.addFragment(new ClientRequestFragmentNew(), false,
                            AndyConstants.CLIENT_REQUEST_TAG, true);
                    DriverApplication.preferenceHelper.putIsTripOn(false);
                } else if (changeStateModel.error_code == AndyConstants.ALREADY_RATED) {
                    DriverApplication.preferenceHelper.clearRequestData();
                    mapActivity.addFragment(new ClientRequestFragmentNew(), false,
                            AndyConstants.CLIENT_REQUEST_TAG, true);
                    DriverApplication.preferenceHelper.putIsTripOn(false);
                } else {
                    if (changeStateModel.error_code == 622) {
                        mapActivity.login();
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AppLog.Log("TAG", error.getMessage());
    }
}
