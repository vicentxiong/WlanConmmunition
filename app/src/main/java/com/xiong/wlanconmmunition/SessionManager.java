package com.xiong.wlanconmmunition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.xiong.wlanconmmunition.filemanager.FileTransRequest;

public class SessionManager {
	public static final int COMEMSG = 0;
	public static final int TOMSG = 1;	
	public static final String CHATIP_EXTRA = "chatip";
	public static final String CHAT_TYPE = "chattype";
	public static final String GROUP_NAME ="groupName";
	public static int UNRESPONSE = 0;
	public static int RECEIVERED = 1;
	public static int REJECT = 2;
	private static SessionManager mSessionMgr ;
	private HashMap<String, SessionLister> listers =  new HashMap<String, SessionManager.SessionLister>();
	private HashMap<String, ArrayList<ChatInfo>> mUnReadMessages = new HashMap<String, ArrayList<ChatInfo>>();
	private HashMap<String, ArrayList<FileTransRequest>> sendRequests = new HashMap<String, ArrayList<FileTransRequest>>();
	
	private SessionManager(){}
	
	public interface SessionLister{
		public void handleReceiverMessage(ChatInfo srcData);
	}
	
	public static SessionManager getInstance(){
		if(mSessionMgr==null){
			mSessionMgr = new SessionManager();
		}
		return mSessionMgr;
	}
	
	public void addSessionLister(String key,SessionLister lister){
		listers.put(key, lister);
	}
	
	public void removeSessionLister(String key){
		listers.remove(key);
	}
	
	public SessionLister getSessionLister(String key){
		return listers.get(key);
	}
	
	public void saveUnReadMesaage(ChatInfo srcDataPacket){
		if(mUnReadMessages.get(srcDataPacket.ip)==null){
			ArrayList<ChatInfo> signleMembrMessages = new ArrayList<ChatInfo>();
			mUnReadMessages.put(srcDataPacket.ip, signleMembrMessages);
		}
		
		ArrayList<ChatInfo> memberMessage = mUnReadMessages.get(srcDataPacket.ip);
		memberMessage.add(srcDataPacket);
	}

	public void saveUnReadGroupMessage(ChatInfo srcDataPacket,String gid){
		if(mUnReadMessages.get(gid)==null){
			ArrayList<ChatInfo> signleMembrMessages = new ArrayList<ChatInfo>();
			mUnReadMessages.put(gid, signleMembrMessages);
		}

		ArrayList<ChatInfo> memberMessage = mUnReadMessages.get(gid);
		memberMessage.add(srcDataPacket);
	}
	
	public ArrayList<ChatInfo> getUnReadMessages(String key){
		return mUnReadMessages.get(key);
	}
	
	public void clearAllUnReadMessages(String key){
		ArrayList<ChatInfo> memberMessage = mUnReadMessages.get(key);
		if(memberMessage!=null&&memberMessage.size()>0){
			memberMessage.clear();
		}
	}
	
	public int getUnReadMessageSize(String key){
		ArrayList<ChatInfo> memberMessage = mUnReadMessages.get(key);
		if(memberMessage==null){
			return 0;
		}
		return memberMessage.size();
	}
	
	public ChatInfo buildChatInfo(DataPacketProtocol srcdatapacket,int form,String filePath){
		ChatInfo cInfo =null;
		if(srcdatapacket.command==MessageManager.IPMSG_FILETRANS_REQUEST){
			cInfo = new FileTransRequest();
			((FileTransRequest)cInfo).filePath = filePath;
			((FileTransRequest)cInfo).isResponse = SessionManager.UNRESPONSE;
			
		}else{
			cInfo = new ChatInfo();
			
		}
		cInfo.packetId = srcdatapacket.packetId;
		cInfo.senderName = srcdatapacket.senderName;
		cInfo.senderHostName = srcdatapacket.senderHostName;
		cInfo.ip = srcdatapacket.getSrcIp();
		cInfo.comeOrto = form;
		if(srcdatapacket.command == MessageManager.IPMSG_SENDMSG || srcdatapacket.command ==MessageManager.IPMSG_FILETRANS_REQUEST||
				srcdatapacket.command == MessageManager.IPMSG_FILETRANS_RESPONSE){
			cInfo.content = srcdatapacket.content;
		}else if(srcdatapacket.command == MessageManager.IPMSG_GROUP_SENDMSG){
			String[] colum = srcdatapacket.content.split("_");
			cInfo.content = colum[1];
		}

		cInfo.dateAndtime = Util.getCurDateAndTime("yyyy-MM-dd HH:mm:ss");
		return cInfo;
	}
	
	public void addFileTransRequest(FileTransRequest request){
		if(sendRequests.get(request.ip)==null){
			ArrayList<FileTransRequest> list = new ArrayList<FileTransRequest>();
			sendRequests.put(request.ip, list);
		}
		
		ArrayList<FileTransRequest> array = sendRequests.get(request.ip);
		array.add(request);
	}
	
	public String removeFileTransRequest(String ip,String packetId){
		
		if(sendRequests.get(ip)==null){
			ArrayList<FileTransRequest> list = new ArrayList<FileTransRequest>();
			sendRequests.put(ip, list);
		}
		String filePath = null;
		ArrayList<FileTransRequest> ls = sendRequests.get(ip);
		for (int i = 0; i < ls.size(); i++) {
			if(packetId.equals(ls.get(i).packetId)){
				filePath = ls.get(i).filePath;
				ls.remove(i);
				break;
			}
		}
		
		return filePath;
	}
	
	
}
