package com.xiong.wlanconmmunition.service;

import com.xiong.wlanconmmunition.ChatInfo;
import com.xiong.wlanconmmunition.DataPacketProtocol;
import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.MessageManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.SessionManager.SessionLister;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.XLog;
import com.xiong.wlanconmmunition.MessageProcesser.DispatchUdpDataPacketLister;
import com.xiong.wlanconmmunition.activity.SessionActivity;
import com.xiong.wlanconmmunition.filemanager.FileTransRequest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class UdpMessageProgressService extends Service implements DispatchUdpDataPacketLister{
    private MemberManager memMgr;
    private static final String MESSAGE_TIP_SOUDN = "message_sound.ogg";
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	memMgr = MemberManager.getInstance();
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MessageManager.getInstance().registerUdpRcvMsgLister(this);
		XLog.logd("startup udp message service");
		return START_STICKY;
	}

	@Override
	public void processDataPacket(DataPacketProtocol srcDataPacket) {
		int cmd = srcDataPacket.command;
		switch (cmd) {
		case MessageManager.IPMSG_ENTRY:
		case MessageManager.IPMSG_ANSENTRY:
			if(!memMgr.hasMemberInfo(srcDataPacket.getSrcIp())){
				MemberInfo member = 
						memMgr.buildMember(getResources().getDrawable(R.drawable.default_icon), srcDataPacket.senderName, srcDataPacket.senderHostName, srcDataPacket.getSrcIp());
				int index = memMgr.addMemberToList(member);
				
			}else{
				if(!srcDataPacket.senderName.equals(memMgr.getSenderNameByIp(srcDataPacket.getSrcIp()))){
					memMgr.updataSenderName(srcDataPacket.getSrcIp(), srcDataPacket.senderName);
				}
			}
			XLog.logd("ENTRY OR ANSENTRY"+srcDataPacket.getSrcIp()+"==>");
			break;
		case MessageManager.IPMSG_EXIT:
			int index = memMgr.deleteMemberFromList(srcDataPacket.getSrcIp());
			
			break;
		case MessageManager.IPMSG_FILETRANS_REQUEST:
		case MessageManager.IPMSG_SENDMSG:
			
			
			ChatInfo chat = SessionManager.getInstance().buildChatInfo(srcDataPacket, SessionManager.COMEMSG,null);
			SessionLister mSsLister = SessionManager.getInstance().getSessionLister(srcDataPacket.getSrcIp());
			if(mSsLister!=null){
				mSsLister.handleReceiverMessage(chat);
			}else{
				XLog.logd("service IPMSG_SENDMSG");
				SessionManager.getInstance().saveUnReadMesaage(chat);
				updateNotification(chat);
				
				Util.playAssetsSound(getApplicationContext(), MESSAGE_TIP_SOUDN);
			}
			break;
		case MessageManager.IPMSG_RCVMSG:
			break;
		case MessageManager.IPMSG_SENDMSGCHECK:
			break;
			
		case MessageManager.IPMSG_ANSUPDATEICON:	
		case MessageManager.IPMSG_UPDATEICON:
			Util.outPutIconFile(Util.stringTobytes(srcDataPacket.content), srcDataPacket.getSrcIp());
			memMgr.updateIcon(srcDataPacket.getSrcIp(), memMgr.getMemberIcon(srcDataPacket.getSrcIp()));
			break;
		case MessageManager.IPMSG_FILETRANS_RESPONSE:
			
			break;
		default:
			break;
		}
	}
	
	private void updateNotification(ChatInfo info){
		StringBuffer tipMsg = new StringBuffer();
		if(info instanceof FileTransRequest){
			FileTransRequest request = (FileTransRequest)info;
			request.parseContent();
			tipMsg.append(request.fileName);
			tipMsg.append("\n");
			tipMsg.append(getResources().getString(R.string.service_request_notification));
		}else{
			tipMsg.append(info.content);
		}
		NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.icon_notification, tipMsg.toString(), System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent intent = new Intent(getApplicationContext(), SessionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SessionManager.CHATIP_EXTRA, info.ip);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 
				                                                    R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(getApplicationContext(), info.senderName, tipMsg.toString(), pendingIntent);
		
		notifyMgr.notify(R.string.app_name, n);
	}

}
