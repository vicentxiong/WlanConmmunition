package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.db.DataBaseManager;
import com.xiong.wlanconmmunition.db.UserSelfTable;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.EditText;

public class UserNameSettingsActivity extends BaseSettingsActivity {
	private EditText et_username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.username_settinglayout);
		et_username = (EditText) findViewById(R.id.new_username);
	}

	@Override
	protected void onSetingOk() {
	    int res = -1;
	    String newUserName = et_username.getText().toString();
		DataBaseManager dbMgr = DataBaseManager.getInstance(getApplicationContext());
		dbMgr.open();
		ContentValues values = new ContentValues();
		values.put(UserSelfTable.COL_USERNAME, newUserName);
		if(dbMgr.getQueryConunt(UserSelfTable.TABLE_NAME, new String[]{UserSelfTable.COL_USERNAME})>0){
			res = dbMgr.update(UserSelfTable.TABLE_NAME, values, null, null);
		}else{
			res = (int) dbMgr.insert(UserSelfTable.TABLE_NAME, values);
		}
		dbMgr.close();
		if(res > 0)
		   updateMemberMgr(UserSelfTable.COL_USERNAME, newUserName);

	}

	@Override
	protected void onSetingCancel() {
		
	}

	@Override
	protected boolean settingEmpty() {
		
		return et_username.getText().toString().equals("");
	}

}
