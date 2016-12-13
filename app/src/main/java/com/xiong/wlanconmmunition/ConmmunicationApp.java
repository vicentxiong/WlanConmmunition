package com.xiong.wlanconmmunition;

import com.xiong.wlanconmmunition.db.DataBaseManager;
import com.xiong.wlanconmmunition.db.UserSelfTable;
import com.xiong.wlanconmmunition.service.FileAcceptService;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ConmmunicationApp extends Application {
	private MessageManager msgMgr;
	
	@Override
	public void onCreate() {
		super.onCreate();
		DataBaseManager dbMgr = DataBaseManager.getInstance(getApplicationContext());
		dbMgr.open();
		Object obj = dbMgr.query(UserSelfTable.TABLE_NAME, new String[]{UserSelfTable.COL_USERNAME}, null, null, null, null, null);
		if(obj==null){
			obj = "unknow";
		}
		String userName = (String)obj ;
		if(userName==null){
			XLog.logd("userName == null:"+(userName==null));
		}
		XLog.logd("userName:"+userName);
		dbMgr.close();
		
		MemberManager.getInstance().setLocalUserName(userName);
		Drawable drawable = MemberManager.getInstance().getMemberIcon(Util.LOCAL_ICON_NAME);
		MemberManager.getInstance().setLocalUserIcon(drawable);
		
		msgMgr = MessageManager.getInstance();
		msgMgr.sendUdpMessage(msgMgr.createUdpDataPacketProtocol(Localhost.ALLBROADIP, MessageManager.IPMSG_ENTRY));
		
		Intent fileAcceptservice = new Intent(getApplicationContext(), FileAcceptService.class);
		startService(fileAcceptservice);
	}

}
