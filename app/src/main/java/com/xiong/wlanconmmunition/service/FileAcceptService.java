package com.xiong.wlanconmmunition.service;

import com.xiong.wlanconmmunition.filemanager.TransferManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FileAcceptService extends Service implements Runnable{
	
	@Override
	public void onCreate() {
		super.onCreate();
		TransferManager.getInstance(getApplicationContext());
		Thread serverSocket = new Thread(this);
		serverSocket.start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void run() {
		TransferManager.getInstance().startUp();
		
	}

}
