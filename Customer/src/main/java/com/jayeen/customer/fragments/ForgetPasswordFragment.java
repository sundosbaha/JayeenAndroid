/**
 * 
 */
package com.jayeen.customer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.component.MyFontEdittextView;
import com.jayeen.customer.newmodels.SuccessModel;
import com.jayeen.customer.parse.AsyncTaskCompleteListener;
import com.jayeen.customer.parse.TaxiParseContent;
import com.jayeen.customer.parse.VolleyHttpRequest;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;

import java.util.HashMap;

import static com.jayeen.customer.CustomerApplication.requestQueue;


public class ForgetPasswordFragment extends UberBaseFragmentRegister implements
		AsyncTaskCompleteListener {
	private MyFontEdittextView etEmail;
//	private RequestQueue requestQueue;
	int prefcount=0;
	private TaxiParseContent parseContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parseContent=new TaxiParseContent(getActivity());
		View forgetView = inflater.inflate(R.layout.forget_pass_fragment,
				container, false);
		etEmail = (MyFontEdittextView) forgetView
				.findViewById(R.id.etForgetEmail);
		forgetView.findViewById(R.id.tvForgetSubmit).setOnClickListener(this);
		activity.setTitle(activity.getString(R.string.text_forget_password));
		return forgetView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		etEmail.requestFocus();

		activity.showKeyboard(etEmail);
	}

	@Override
	public void onClick(View v) {
		// 
		switch (v.getId()) {
		case R.id.tvForgetSubmit:
			if (etEmail.getText().length() == 0) {
				AndyUtils.showToast(activity.getString(R.string.text_enter_email),R.id.coordinatorLayout,activity);
				return;
			} else if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {

				AndyUtils.showToast(activity.getString(R.string.text_enter_valid_email),R.id.coordinatorLayout,activity);
				return;
			} else {
				if (!AndyUtils.isNetworkAvailable(activity)) {
					AndyUtils.showToast(activity.getString(R.string.dialog_no_inter_message),R.id.coordinatorLayout,activity);
					return;
				}
				forgetPassowrd();
			}
			break;

		default:
			break;
		}
	}

	private void forgetPassowrd() {

		AndyUtils.showCustomProgressDialog(activity,activity.
				getString(R.string.text_forget_password_loading_msg), false,
				null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.FORGET_PASSWORD);
		map.put(Const.Params.TYPE, Const.Params.OWNER);
		map.put(Const.Params.EMAIL, etEmail.getText().toString());
		prefcount= CustomerApplication.preferenceHelper.getRequestTotal();
		prefcount++;
		CustomerApplication.preferenceHelper.putRequestTotal(prefcount);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				Const.ServiceCode.FORGET_PASSWORD, this, this));
		
		
	}


	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// 
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
			case Const.ServiceCode.FORGET_PASSWORD:
				SuccessModel successModel=parseContent.getSingleObject(response,SuccessModel.class);
				AppLog.Log("TAG", "forget" + response);
				if(successModel.success){
					AndyUtils.showToast(activity.getString(R.string.toast_forget_password_success),R.id.coordinatorLayout,activity);
				} else {
					AndyUtils.showToast(activity.getString(R.string.toast_email_ivalid),R.id.coordinatorLayout,activity);
				}
				break;
		default:
			break;
		}
	}

	@Override
	protected boolean isValidate() {
		// 
		return false;
	}

	@Override
	public void onResume() {
		// 
		super.onResume();
		activity.actionBar.setTitle(activity.getString(R.string.text_forget_pass_small));
	}
	@Override
	public void onPause() {
		// 
		super.onPause();

	}

	@Override
	public boolean OnBackPressed() {
		// 

		return false;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// 
		AppLog.Log(Const.TAG, error.getMessage());
	}

}
