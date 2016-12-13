package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.Localhost;
import com.xiong.wlanconmmunition.MessageManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.ScreenManager;
import com.xiong.wlanconmmunition.R.layout;
import com.xiong.wlanconmmunition.R.menu;
import com.xiong.wlanconmmunition.defview.MemberRelativeLayout;
import com.xiong.wlanconmmunition.service.UdpMessageProgressService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainEntryActivity extends Activity {
	private FragmentManager fgManager = null;
	private FragmentTransaction fgTransmitter = null;
	private AllMemberFragment mAllMemberFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitleColor(R.color.titleBgColor);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainentrylayout);
		fgManager = getFragmentManager();
		fgTransmitter = fgManager.beginTransaction();
		
		initUI();
		ScreenManager.getScreenManager().pushActivity(this);
	}
	
	private void initUI(){
		mAllMemberFragment  = new AllMemberFragment();
		fgTransmitter.add(R.id.fragment_container, mAllMemberFragment);
        fgTransmitter.commit();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.build_group:
				buildGroupDialog();
				break;
			case R.id.action_settings:
				Intent settings = new Intent(this, SettingsActivity.class);
				startActivity(settings);
				break;
			case R.id.file_list:
				Intent fileList = new Intent(this,ReceiverFileActivity.class);
				startActivity(fileList);
				break;
			default:
				break;
		}
		return true;
	}

	private void buildGroupDialog(){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(R.string.build_group_title);
		View v = LayoutInflater.from(this).inflate(layout.buildgroup_dialog,null,false);
		final EditText et = (EditText) v.findViewById(R.id.groupName);
		ab.setView(v);
		ab.setPositiveButton(R.string.save_group_name, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAllMemberFragment.buildGroupListOwner(et.getText().toString().trim());
				dialog.dismiss();
			}
		});
		ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		Dialog d = ab.create();
		d.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		MessageManager msgMgr = MessageManager.getInstance();
//		msgMgr.sendUdpMessage(msgMgr.createUdpDataPacketProtocol(Localhost.ALLBROADIP, MessageManager.IPMSG_EXIT));
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent backService = new Intent(getApplicationContext(), UdpMessageProgressService.class);
		startService(backService);
		
		ScreenManager.getScreenManager().popActivity(this);
	}

}
