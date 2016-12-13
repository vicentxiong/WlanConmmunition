package com.xiong.wlanconmmunition;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.xiong.wlanconmmunition.filemanager.TransferManager;

import android.os.Handler;
import android.os.Message;

public class MessageProcesser extends Handler {
	private DispatchUdpDataPacketLister mDispatchLister;
	private ArrayList<DataPacketProtocol> messageQueue = new ArrayList<DataPacketProtocol>();

	public void setmDispatchLister(DispatchUdpDataPacketLister mDispatchLister) {
		this.mDispatchLister = mDispatchLister;
	}

	public interface DispatchUdpDataPacketLister{
		public void  processDataPacket(DataPacketProtocol srcDataPacket); 
	}

	public void addMessageToQueue(DataPacketProtocol msg) {
		messageQueue.add(msg);
	}

	public DataPacketProtocol deleteFrontMessage() {
		return messageQueue.remove(0);

	}

	public ArrayList<DataPacketProtocol> getMessageQueue() {
		return messageQueue;
	}

	@Override
	public void handleMessage(Message msg) {
		DataPacketProtocol dp = (DataPacketProtocol) msg.obj;
		switch (msg.what) {
		case MessageManager.IPMSG_ENTRY:
			addMessageToQueue(createDataPacketProtocol(dp.getSrcIp(), MessageManager.IPMSG_ANSENTRY));
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
		case MessageManager.IPMSG_EXIT:
		case MessageManager.IPMSG_ANSENTRY:
		case MessageManager.IPMSG_RCVMSG:
		case MessageManager.IPMSG_SENDMSG:
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
		case MessageManager.IPMSG_SENDMSGCHECK:
			addMessageToQueue(createDataPacketProtocol(dp.getSrcIp(), MessageManager.IPMSG_RCVMSG));
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
		case MessageManager.IPMSG_UPDATEICON:
			addMessageToQueue(createDataPacketProtocol(dp.getSrcIp(), 
					          MessageManager.IPMSG_ANSUPDATEICON, 
					          Util.bytesToString(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.LOCAL_ICON_NAME).getPath())));
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
		case MessageManager.IPMSG_ANSUPDATEICON:
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
		case MessageManager.IPMSG_FILETRANS_RESPONSE:
			TransferManager.getInstance().dispatchFileTransResponse(dp);
			break;
		case MessageManager.IPMSG_FILETRANS_REQUEST:
			if(mDispatchLister!=null){
				mDispatchLister.processDataPacket(dp);
			}
			break;
			case MessageManager.IPMSG_GROUP_REQUEST:
				pollGroupResponse(dp.content);
				if(mDispatchLister!=null){
					mDispatchLister.processDataPacket(dp);
				}
				break;
			case MessageManager.IPMSG_GROUP_RESPONSE:
				if(mDispatchLister!=null){
					mDispatchLister.processDataPacket(dp);
				}
				break;
			case MessageManager.IPMSG_GROUP_SENDMSG:
				if(mDispatchLister!=null){
					mDispatchLister.processDataPacket(dp);
				}
				break;
		default:
			break;
		};
	}

	private void pollGroupResponse(String content){
		String[] ips = content.split("_");
		if(ips==null || ips.length == 0) return;
		for (int i=2;i<ips.length;i++){
			addMessageToQueue(createDataPacketProtocol(ips[i],MessageManager.IPMSG_GROUP_RESPONSE,ips[1]));
		}
	}
	
	public DataPacketProtocol createDataPacketProtocol(String destIp ,int cmd){
		return createDataPacketProtocol(destIp,cmd,null);
	}
	
	public DataPacketProtocol createDataPacketProtocol(String destIp ,int cmd , String content){
		DataPacketProtocol dp = new DataPacketProtocol();
		dp.packetId = Util.getCurDateAndTime("yyyyMMddHHmmss");
		dp.senderName = MemberManager.getInstance().getLocalUserName();   //鐢辩敤鎴峰姩鎬佽缃�		
		dp.command = cmd;
		dp.content = content;
		dp.setDestIp(destIp);
		dp.setSrcIp(destIp);
		return dp;
	}

}
