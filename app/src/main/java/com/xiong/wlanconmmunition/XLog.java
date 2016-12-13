package com.xiong.wlanconmmunition;

import android.util.Log;

public class XLog {
	private static String TAG = "WlanConmmunication";
	
	public static void logv(String msg){
		Log.v(TAG, msg);
	}
	
	public static void logd(String msg){
		Log.d(TAG, msg);
	}
	
	public static void logw(String msg){
		Log.d(TAG, msg);
	}
	
	public static void loge(String msg){
		Log.e(TAG, msg);
	}

}
