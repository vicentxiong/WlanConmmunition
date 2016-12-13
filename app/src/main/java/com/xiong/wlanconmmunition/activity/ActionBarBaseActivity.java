package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.ScreenManager;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public abstract class ActionBarBaseActivity extends Activity {
	private View mCustomView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setDisplayShowCustomEnabled(true);

		mCustomView = createActionBarView(getLayoutInflater());
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		getActionBar().setCustomView(mCustomView, lp);
		
		initCustomView();
		
		ScreenManager.getScreenManager().pushActivity(this);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ScreenManager.getScreenManager().popActivity(this);
		
	}
	
	protected View getCustomView(){
		return mCustomView;
	}
	
	protected void finishWithAnimation(){
		finish();
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}
	
	protected abstract View createActionBarView(LayoutInflater inflater);
	
	protected abstract void initCustomView();
	
	protected abstract boolean onBackKey();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			    onBackKey();
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
