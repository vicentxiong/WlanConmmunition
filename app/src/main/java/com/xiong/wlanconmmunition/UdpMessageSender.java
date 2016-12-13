package com.xiong.wlanconmmunition;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.util.Log;

public class UdpMessageSender extends BaseThread {
	private DatagramSocket mSender;

	public UdpMessageSender(MessageProcesser processer) {
		super(processer);

	}

	@Override
	public boolean loopExec() {
		ArrayList<DataPacketProtocol> dps = mProcesser.getMessageQueue();
		if (dps.size() > 0) {
			try {
				mSender = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			InetAddress address = null;
			try {
				address = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				XLog.loge("unknow local host");
				e.printStackTrace();
			}
			DataPacketProtocol dp = dps.get(0);
			dp.senderHostName = address.getHostName();
			byte[] by = dp.getByte();
			if (!isValidity(by)) {
				XLog.logd("******************");
				mProcesser.deleteFrontMessage();
				return true;
			}
			
			XLog.logd("SEND:"+dp.packetId+"=======");
			XLog.logd("SEND:"+dp.senderName+"=======");
			XLog.logd("SEND:"+dp.senderHostName+"=======");
			XLog.logd("SEND:"+dp.command+"=======");
			
			DatagramPacket mDPacket = new DatagramPacket(by, by.length);
			try {
				address = InetAddress.getByName(dp.getDestIp());
			} catch (UnknownHostException e) {
				XLog.loge("unknown host");
			}
			mDPacket.setAddress(address);
			mDPacket.setPort(Localhost.BINDPORT);
			try {
				mSender.send(mDPacket);
			} catch (IOException e) {
				XLog.loge("send>>IO exception");
				mSender.close();
				return true;
			}
			mSender.close();
			mProcesser.deleteFrontMessage();
		}

		return true;
	}

	private boolean isValidity(byte[] bytes) {
		if (bytes.length <= MessageManager.DATAPACKET_MAX) {
			return true;
		}
		return false;
	}

}
