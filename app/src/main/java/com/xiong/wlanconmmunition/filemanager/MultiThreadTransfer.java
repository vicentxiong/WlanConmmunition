package com.xiong.wlanconmmunition.filemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public abstract class MultiThreadTransfer implements Runnable {
	protected Socket mSocket;
	protected InputStream ins;
	protected OutputStream ous;
    protected String packetId;

	protected void init() {
		try {
			ins = mSocket.getInputStream();
			ous = mSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	protected abstract void onLastExecute();
	
	protected abstract void loopExec();

	protected BufferedReader getBufferedReader() {
		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		return br;
	}
	protected BufferedWriter getBufferedWriter(){
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ous));
		return bw;
	}

	@Override
	public void run() {
		
		loopExec();

        onLastExecute();
	}

}
