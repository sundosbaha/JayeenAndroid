/**
 * 
 */
package com.jayeen.customer;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;
public class MenuDescActivity extends ActionBarBaseActivitiy {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_desc_activity);
		setIcon(R.drawable.back);
		setIconMenu(R.drawable.taxi);
		setTitle(getIntent().getStringExtra(Const.Params.TITLE));
		webView = (WebView) findViewById(R.id.wvDesc);
		webView.loadData(getIntent().getStringExtra(Const.Params.CONTENT),
				"text/html", "utf-8");
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
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log(Const.TAG, error.getMessage());
	}
}
