package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.db.UserSelfTable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public abstract class BaseSettingsActivity extends ActionBarBaseActivity {
    private ImageView subSettingsback;
    private Button settingOk;
	@Override
	protected View createActionBarView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.base_settings_actionbarlayout, null);
	}

	@Override
	protected void initCustomView() {
		subSettingsback = (ImageView) findViewById(R.id.settings_back);
		settingOk = (Button) findViewById(R.id.settingOk);
		OkOnClickLister lister = new  OkOnClickLister();
		subSettingsback.setOnClickListener(lister);
		settingOk.setOnClickListener(lister);
	}
	
	class OkOnClickLister implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.settings_back:
				if(!settingEmpty()){
					showDialog(1);
				}else{
					finishWithAnimation();
				}
				break;
			case R.id.settingOk:
				if(!settingEmpty()){
					onSetingOk();
					finishWithAnimation();
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_empty_toast), 1000).show();
				}
				
				break;
			default:
				break;
			}
			
		}
		
	} 
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setIcon(R.drawable.settings_tip_icon);
		ab.setTitle(R.string.settings_back_tip_title);
		ab.setMessage(R.string.settings_back_tip_message);
		ab.setPositiveButton(R.string.settings_back_tip_save, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onSetingOk();
				finishWithAnimation();
			}
		});
		
		ab.setNegativeButton(R.string.settings_back_tip_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onSetingCancel();
				finishWithAnimation();
				
			}
		});
		AlertDialog dialog = ab.create();
		dialog.show();
		return dialog;
	}
	
	protected abstract void onSetingOk();
	protected abstract void onSetingCancel();
	protected abstract boolean settingEmpty();
	
	@Override
	protected boolean onBackKey() {
		if(!settingEmpty()){
			showDialog(1);
		}else{
			finishWithAnimation();
		}
		return true;
	}
	
	protected void updateMemberMgr(String column,Object o){
		if(column.equals(UserSelfTable.COL_USERNAME)){
			MemberManager.getInstance().setLocalUserName((String) o);
		}else if(column.equals(UserSelfTable.COL_SELFICON)){
			
		}
	}

}
