package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.ScreenManager;
import com.xiong.wlanconmmunition.XLog;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingsActivity extends ActionBarBaseActivity implements OnClickListener{
	private ImageView settingsBack;
	private RelativeLayout username_rl;
	private RelativeLayout usericon_rl;
	private RelativeLayout exit_rl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		 initUI();
	}
	
	private void initUI(){
		username_rl = (RelativeLayout) findViewById(R.id.localusername_setting_parent);
		usericon_rl = (RelativeLayout) findViewById(R.id.localusericon_setting_parent);
		exit_rl = (RelativeLayout) findViewById(R.id.exit_setting_parent);
		
		username_rl.setOnClickListener(this);
		usericon_rl.setOnClickListener(this);
		exit_rl.setOnClickListener(this);
	}

	@Override
	protected View createActionBarView(LayoutInflater inflater) {
		
		return inflater.inflate(R.layout.settings_actionbarlayout, null);
	}

	@Override
	protected void initCustomView() {
		settingsBack = (ImageView) getCustomView().findViewById(R.id.settings_back);
		settingsBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settings_back:
			finishWithAnimation();
			break;
		case R.id.localusername_setting_parent:
			Intent username_setting = new Intent(this, UserNameSettingsActivity.class);
			startActivity(username_setting);
			break;
		case R.id.localusericon_setting_parent:
			Intent usericon_setting = new Intent(this, UserIconSettingsActivity.class);
			startActivity(usericon_setting);
			break;
		case R.id.exit_setting_parent:
			//android.os.Process.killProcess(android.os.Process.myPid());
			XLog.logd("exit: " + getPackageName());
//			ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
//			am.restartPackage(getPackageName());
			ScreenManager.getScreenManager().popAllActivityExceptOne();
			
			break;
		default:
			break;
		}
		
	}

	@Override
	protected boolean onBackKey() {
		finishWithAnimation();
		return true;
	}

}
