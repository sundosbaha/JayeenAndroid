package com.jayeen.driver;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.jayeen.driver.base.ActionBarBaseActivitiy;
import com.jayeen.driver.fragment.LoginFragment;
import com.jayeen.driver.fragment.RegisterFragment;
import com.jayeen.driver.gcm.GCMRegisterHendler;
import com.jayeen.driver.utills.AndyConstants;
import com.jayeen.driver.utills.AndyUtils;

public class RegisterActivity extends ActionBarBaseActivitiy {
    public ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        if (getIntent().getBooleanExtra("isSignin", false)) {
            addFragment(new LoginFragment(), true,
                    AndyConstants.LOGIN_FRAGMENT_TAG, false);
        } else {
            addFragment(new RegisterFragment(), true,
                    AndyConstants.REGISTER_FRAGMENT_TAG, false);
        }
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

    public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            AndyUtils.showCustomProgressDialog(this, "", getResources()
                    .getString(R.string.progress_loading), false);
            new GCMRegisterHendler(RegisterActivity.this,
                    mHandleMessageReceiver);

        }
    }

    public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            if (mHandleMessageReceiver != null) {
                unregisterReceiver(mHandleMessageReceiver);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        Fragment signinFragment = getSupportFragmentManager()
                .findFragmentByTag(AndyConstants.LOGIN_FRAGMENT_TAG);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                AndyConstants.REGISTER_FRAGMENT_TAG);
        if (fragment != null && fragment.isVisible()) {

            goToMainActivity();
        } else if (signinFragment != null && signinFragment.isVisible()) {
            goToMainActivity();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
