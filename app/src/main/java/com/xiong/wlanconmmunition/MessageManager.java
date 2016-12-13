/*
该类为调停者类

*/
package com.xiong.wlanconmmunition;

import com.xiong.wlanconmmunition.MessageProcesser.DispatchUdpDataPacketLister;

public class MessageManager {
	
	public static final int IPMSG_MASK = 0XFFFF;
	public static final int IPMSG_ENTRY = 0X1;
	public static final int IPMSG_EXIT = 0X2;
	public static final int IPMSG_ANSENTRY = 0X3;
	public static final int IPMSG_SENDMSG = 0X4;
	public static final int IPMSG_RCVMSG = 0X5;
	public static final int IPMSG_SENDMSGCHECK = 0X6;
	public static final int IPMSG_UPDATEICON = 0X7;
	public static final int IPMSG_ANSUPDATEICON = 0X8;
	public static final int IPMSG_FILETRANS_REQUEST = 0X9;
	public static final int IPMSG_FILETRANS_RESPONSE = 0XA;
	public static final int IPMSG_GROUP_REQUEST = 0XB;
	public static final int IPMSG_GROUP_RESPONSE = 0XC;
	public static final int IPMSG_GROUP_SENDMSG = 0XD;
	public static final int IPMSG_GROUP_RCVMSG = 0XE;
	
	public static final int DATAPACKET_MAX = 1024*40;
	
	private static MessageManager mMessageMgr = null;
	private UdpMessageRecevicer mUdpMsgRcv;
	private UdpMessageSender mUdpMsgSend;
	private MessageProcesser mProcesser;
	
	private MessageManager(){
	   mProcesser = new MessageProcesser();
	   mUdpMsgSend = new UdpMessageSender(mProcesser);
	   mUdpMsgRcv = new UdpMessageRecevicer(mProcesser);
	   
	   mUdpMsgRcv.start();
	   mUdpMsgSend.start();
 	}
	
	public static MessageManager getInstance(){
		if(mMessageMgr==null){
			mMessageMgr = new MessageManager();
		}
		return mMessageMgr;
	}
	
	public DataPacketProtocol createUdpDataPacketProtocol(String destIp,int cmd){
		return mProcesser.createDataPacketProtocol(destIp, cmd);
	}
	
	public DataPacketProtocol createUdpDataPacketProtocol(String destIp,int cmd,String content){
		return mProcesser.createDataPacketProtocol(destIp, cmd, content);
	}
	
	public void sendUdpMessage(DataPacketProtocol datapacket){
		mProcesser.addMessageToQueue(datapacket);
	}
	
	public void registerUdpRcvMsgLister(DispatchUdpDataPacketLister lister){
		mProcesser.setmDispatchLister(lister);
	}

}
