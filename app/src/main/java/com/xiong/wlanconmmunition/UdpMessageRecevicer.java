package com.xiong.wlanconmmunition;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.os.Message;

public class UdpMessageRecevicer extends BaseThread{
	private DatagramSocket mReceiver;
	
	public UdpMessageRecevicer(MessageProcesser processer){
		super(processer);
		try {
			mReceiver = new DatagramSocket(Localhost.BINDPORT);
		} catch (SocketException e) {
			XLog.logd("Recevicer:SocketException fail");
			e.printStackTrace();
		}
	}

	@Override
	public boolean loopExec() {
		byte[] by = new byte[MessageManager.DATAPACKET_MAX];
		DatagramPacket mDPacket = new DatagramPacket(by,by.length);
		try {
			if(mReceiver!=null){
				mReceiver.receive(mDPacket);
			}
		} catch (IOException e) {
			XLog.loge("receiver<<IO exception");
			return true;
		}
		
		DataPacketProtocol dp = new DataPacketProtocol();
		dp.setSrcIp(mDPacket.getAddress().getHostAddress());
		dp.sync(mDPacket.getData(),mDPacket.getLength());
		
		Message msg = mProcesser.obtainMessage();
		msg.obj = dp;
		msg.what = dp.command;
		msg.sendToTarget();
		return true;
	}


}
