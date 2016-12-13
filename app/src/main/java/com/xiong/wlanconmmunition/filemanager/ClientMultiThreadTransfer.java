package com.xiong.wlanconmmunition.filemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.xiong.wlanconmmunition.Localhost;
import com.xiong.wlanconmmunition.XLog;

public class ClientMultiThreadTransfer extends MultiThreadTransfer {
	private BufferedWriter bw;
	private BufferedReader br;
	private FileInputStream fis;
	private File file;
	private String ip;

	public ClientMultiThreadTransfer(String ip) {
		this.ip = ip;
	}

	public void setFile(File f) {
		file = f;
	}
	
	public void setPacketId(String pid){
		packetId = pid;
	}
	
	private void createFileInputStream(){
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onLastExecute() {
        try {
			fis.close();
			br.close();
			bw.close();
			ous.close();
			ins.close();
			mSocket.close();
			XLog.logd("client onlast close");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

	@Override
	protected void loopExec() {
		XLog.logd("new client ready ip:" + ip);
		try {
			mSocket = new Socket(InetAddress.getByName(ip), Localhost.BINDPORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(mSocket == null){
			return;
		}
		init();
		XLog.logd("new client startup");
        XLog.logd("client runnable statup");
		try {
			bw = getBufferedWriter();
			bw.write("filename:" + file.getName() + "\r\n");
			bw.write("packetId:" + packetId + "\r\n");
			bw.write("over:\r\n");
			bw.flush();
			br = getBufferedReader();
			String readLine = br.readLine();
			XLog.logd("client response:" + readLine);
			if(readLine.equals("100")){
				createFileInputStream();
				byte[] by = new byte[1024];
				int len ;
				while((len = fis.read(by))!=-1){
					ous.write(by, 0, len);
				}
			}
			XLog.logd("client send over");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
