/**
 *
 */
package com.jayeen.customer.fragments;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.newmodels.LoginModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.OtpReciver;
import com.jayeen.customer.parse.SmsListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;

public class CheckOTPCodeFragment extends UberBaseFragmentRegister implements
        AsyncTaskCompleteListener {
    private MyFontEdittextView etOTPCode;
    private String id;
    private int is_skip = 0;
//    private RequestQueue requestQueue;
    int prefcount = 0;
    private SeekbarTimer seekbarTimer;
    private TextView btnReSendOTP, btnEditPhNoOTP;
    private int timerequest = 120;
    private SoundPool soundPool;
    private int soundid;
    private boolean loaded = false;
    String otpentertext;
    String LoginBy = "";
    private TaxiParseContent parseContent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getString(Const.Params.ID);
        LoginBy = getArguments().getString("LoginBy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parseContent = new TaxiParseContent(getActivity());
        activity.setIconMenu(R.drawable.taxi);
        activity.setTitle(activity.getString(R.string.text_otpref_code));
        activity.btnNotification.setVisibility(View.INVISIBLE);
        activity.mDotsView.selectDot(1);
        View refView = inflater.inflate(R.layout.otp_code_fragment, container, false);
        etOTPCode = (MyFontEdittextView) refView.findViewById(R.id.etOTPCode);
        etOTPCode.setHint(activity.getString(R.string.text_enter_otp_code));
        refView.findViewById(R.id.btnotpSubmit).setOnClickListener(this);
        btnReSendOTP = (TextView) refView.findViewById(R.id.btnresendotp);
        btnEditPhNoOTP = (TextView) refView.findViewById(R.id.btnedituserno);
        btnReSendOTP.setOnClickListener(this);
        btnEditPhNoOTP.setOnClickListener(this);
        LoginBy = LoginBy != null ? LoginBy : "";
        btnEditPhNoOTP.setVisibility(LoginBy.equalsIgnoreCase(Const.SOCIAL_FACEBOOK) ? View.GONE : View.VISIBLE);
        SpannableString str = new SpannableString("Resend OTP");
        str.setSpan(new UnderlineSpan(), 0, str.length(), Spanned.SPAN_PARAGRAPH);
        if (seekbarTimer == null) {
            seekbarTimer = new SeekbarTimer(timerequest * 1000, 1000);

            seekbarTimer.start();
        }

        soundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 100);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundid = soundPool.load(activity, R.raw.beep, 1);

        if (CustomerApplication.preferenceHelper.getIsRegEditCheck()) {
            ResendOTPCode();
        }
        OtpReciver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                if (messageText.contains("OTP Number")) {
                    etOTPCode.setText(messageText.trim().replaceAll("[^0-9]", ""));
                    sendOTP();
                }
            }
        });
        return refView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        activity.currentFragment = Const.FRAGMENT_OTP;
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnotpSubmit:
                if (etOTPCode.getText().length() == 0) {
                    AndyUtils.showToast(activity.getString(R.string.text_blank_otp_code), R.id.coordinatorLayout, activity);
                    return;
                } else {
                    if (!AndyUtils.isNetworkAvailable(activity)) {
                        AndyUtils.showToast(activity.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activity);
                        return;
                    }
                    is_skip = 0;
                    soundPool.stop(soundid);
                    otpentertext = etOTPCode.getText().toString();
                    cancelSeekbarTimer();
                    applyOTPCode(otpentertext);
                }
                break;

            case R.id.btnresendotp:
                soundPool.stop(soundid);
                ResendOTPCode();
                break;

            case R.id.btnedituserno:
                soundPool.stop(soundid);
                CustomerApplication.preferenceHelper.putIsRegEditCheck(false);
                activity.onBackPressed();

                break;

            default:
                break;
        }
    }

    private void sendOTP() {
        if (etOTPCode.getText().toString().trim().length() == 0) {
            AndyUtils.showToast(activity.getString(R.string.text_blank_otp_code), R.id.coordinatorLayout, activity);
        } else if (!AndyUtils.isNetworkAvailable(activity)) {
            AndyUtils.showToast(activity.getString(R.string.dialog_no_inter_message), R.id.coordinatorLayout, activity);
        } else {
            is_skip = 0;
            soundPool.stop(soundid);
            otpentertext = etOTPCode.getText().toString();
            cancelSeekbarTimer();
            applyOTPCode(otpentertext);
        }
    }

    private void applyOTPCode(String otpcode) {
        AndyUtils.showCustomProgressDialog(activity, activity.getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.APPLY_OTP_CODE);
        map.put(Const.Params.OTP_CODE, otpcode);
        map.put(Const.Params.OWNERID, id);
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.APPLY_OTP_CODE, this, this));
    }


    private void ResendOTPCode() {

        AndyUtils.showCustomProgressDialog(activity,activity.
                getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.RESEND_OTP_CODE);
        map.put(Const.Params.OWNERID, id);


        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.RESEND_OTP_CODE, this, this));

    }


    public void RemoveUSerfromDb() {

        AndyUtils.showCustomProgressDialog(activity,activity.
                getString(R.string.progress_loading), false, null);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.URL, Const.ServiceType.REMOVE_USER);
        map.put(Const.Params.OWNERID, id);
        CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                Const.ServiceCode.REMOVEUSER, this, this));

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        // TODO Auto-generated method stub
        AndyUtils.removeCustomProgressDialog();
        AppLog.Log(Const.TAG, "Apply-OTP Response ::: " + response);
        switch (serviceCode) {
            case Const.ServiceCode.APPLY_OTP_CODE:
                LoginModel loginModel = parseContent.getSingleObject(response, LoginModel.class);
                if (loginModel.success && loginModel!=null) {
                    CustomerApplication.preferenceHelper.putObject(Const.USER_DETAILS,loginModel.user);
                    CustomerApplication.preferenceHelper.putIsOTPVerify(loginModel.user.isOtp);
                    CustomerApplication.preferenceHelper.putUserId(loginModel.user.id + "");
                    CustomerApplication.preferenceHelper.putSessionToken(loginModel.user.token);
                    goToReffralCodeFragment(loginModel.user.id + "", loginModel.user.token);
                } else {
                    AndyUtils.showToast(parseContent.ErrorResponse(loginModel.error_code), R.id.coordinatorLayout, activity);
                }
                break;

            case Const.ServiceCode.RESEND_OTP_CODE:
                LoginModel loginModel1 = parseContent.getSingleObject(response, LoginModel.class);
                if (loginModel1.success && loginModel1!=null) {
                    if (seekbarTimer == null) {
                        seekbarTimer = new SeekbarTimer(
                                timerequest * 1000, 1000);
                        seekbarTimer.start();
                    }
                } else {
                    AndyUtils.showToast(parseContent.ErrorResponse(loginModel1.error_code), R.id.coordinatorLayout, activity);
                }

                break;

            case Const.ServiceCode.REMOVEUSER:
                try {
                    if (new JSONObject(response).getBoolean("success")) {
                        CustomerApplication.preferenceHelper.putIsRegEditCheck(false);
                        activity.goToMainRegActivity(LoginBy.equalsIgnoreCase("facebook"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            default:
                break;
        }

    }

    @Override
    protected boolean isValidate() {
        // TODO Auto-generated method stub
        return false;
    }

    private class SeekbarTimer extends CountDownTimer {

        public SeekbarTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if (seekbarTimer == null) {
                return;
            }

            this.cancel();
            seekbarTimer = null;

        }

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) (millisUntilFinished / 1000);

            if (!isVisible()) {
                return;
            }
            if (CustomerApplication.preferenceHelper.getSoundAvailability()) {
                if (time <= 15) {
                    AppLog.Log("ClientRequest Timer Beep", "Beep started");
                    if (loaded) {
                        //soundPool.play(soundid, 1, 1, 0, 0, 1);
                    }

                }
            }
        }
    }


    private void goToRegisterFragment() {
        UberRegisterFragment regFrag = new UberRegisterFragment();
        activity.clearBackStack();
        activity.addFragment(regFrag, false, Const.FRAGMENT_REGISTER);
    }

    private void goToReffralCodeFragment(String id, String token) {
        ReffralCodeFragment reffralCodeFragment = new ReffralCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.Params.TOKEN, token);
        bundle.putString(Const.Params.ID, id);
        reffralCodeFragment.setArguments(bundle);
        activity.addFragment(reffralCodeFragment, false,
                Const.FRAGMENT_REFFREAL);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub
        AppLog.Log(Const.TAG, error.getMessage());

    }

    public void cancelSeekbarTimer() {
        if (seekbarTimer != null) {
            seekbarTimer.cancel();
            seekbarTimer = null;
        }
    }


}
