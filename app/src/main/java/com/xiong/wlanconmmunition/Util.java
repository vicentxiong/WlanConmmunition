package com.xiong.wlanconmmunition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xiong.wlanconmmunition.filemanager.FileController;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Base64;

public class Util {
	public static final String LOCA_ICON_DIR = "wlancommunication/tempicondir";
	public static final String LOCA_RECEIVER ="wlancommunication/receiverdir";
	public static final String TEMP_ICON_NAME = "templocalicon";
	public static final String CROP_ICON = "cropicon";
	public static final String LOCAL_ICON_NAME = "localicon";

	public static String getCurDateAndTime(String pattern) {
		return getFormatterTime(pattern, System.currentTimeMillis());
	}
	
	public static String getFormatterTime(String pattern,long millis){
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date curDate = new Date(millis);
		return formatter.format(curDate);
	}

	public static String getFormatterTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date();
		return formatter.format(curDate);
	}
	
	public static void cpFile(String copyfrom,String copyto){
		File from = new File(copyfrom);
		if(!from.exists()){
			return;
		}
		File to = new File(copyto);
		if(!to.exists()){
			try {
				to.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(from);
			FileOutputStream fos = new FileOutputStream(to);
			byte[] by = new byte[1024];
			int len = -1;
			while((len = fis.read(by))!=-1){
				fos.write(by, 0, len);
			}
			
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File openOrCreateFile(String dir, String filename) {
		File path = new File(Environment.getExternalStorageDirectory(), dir);
		if (!path.exists()) {
			path.mkdirs();
		}

		File tempIconFile = new File(path, filename);
		if (!tempIconFile.exists()) {
			try {
				XLog.logd("filename:" + filename);
				tempIconFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return tempIconFile;
	}
	
	public static boolean exists(String dir,String filename){
		File path = new File(Environment.getExternalStorageDirectory(), dir);
		if (!path.exists()) {
			path.mkdirs();
		}
		File IconFile = new File(path, filename);
		return IconFile.exists();
	}
	
	public static void deleteFile(String dir, String filename){
		File path = new File(Environment.getExternalStorageDirectory(), dir);
		if (!path.exists()) {
			path.mkdirs();
		}
		File tempIconFile = new File(path, filename);
		if(tempIconFile.exists()){
			tempIconFile.delete();
		}
	}

	public static void outPutIconFile(byte[] dt, String fileName) {
		File localIIconFile = openOrCreateFile(LOCA_ICON_DIR, fileName);
		FileOutputStream fous = null;
		try {
			fous = new FileOutputStream(localIIconFile);
			fous.write(dt);
			fous.flush();
			fous.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void compressIconFile(Bitmap Srcbitmap) {
		
		if (Srcbitmap != null) {
			ByteArrayOutputStream bous = new ByteArrayOutputStream();
			Srcbitmap.compress(CompressFormat.PNG, 100, bous);
            XLog.logd("bitmap size: " + bous.toByteArray().length + " width: " + Srcbitmap.getWidth() +" height: "  + Srcbitmap.getHeight());
			outPutIconFile(bous.toByteArray(), LOCAL_ICON_NAME);
			bous.reset();
		}

	}
	
	public static byte[] stringTobytes(String recv){
		byte[] data = null;
		if(recv != null){
			data = Base64.decode(recv, Base64.DEFAULT);
		}
		return data;
	}
	
	public static String bytesToString(String path){
		ByteArrayOutputStream bous = null;
		try {
			FileInputStream fins = new FileInputStream(new File(path));
			bous = new ByteArrayOutputStream();
			byte[] by = new byte[1024];
			int len;
			while ((len = fins.read(by))!=-1) {
				bous.write(by, 0, len);
			}
			fins.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		String icon_data = Base64.encodeToString(bous.toByteArray(), 0,bous.toByteArray().length, Base64.DEFAULT);
		bous.reset();
		try {
			bous.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return icon_data;
	}
	
	public static String calculateUnit(long length){
		StringBuffer sb = new StringBuffer();
		double result = length;
		int count = 0;
		while(result >= 1024){
			result = result/1024;
			count++;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#0.00");
		sb.append(df.format(result));
		switch (count) {
		case 0:
			sb.append(FileController.B);
			break;
		case 1:
			sb.append(FileController.KB);
			break;
		case 2:
			sb.append(FileController.MB);
			break;
		default:
			break;
		}
		return sb.toString();
	}
	
	public static void playAssetsSound(Context mcontext,String assetsName) {
		final MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setLooping(false);
        try {
            try {
                AssetFileDescriptor fileDescriptor = mcontext.getAssets().openFd(assetsName);
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                mMediaPlayer.prepare();
            }
            catch (Exception e) {
                XLog.loge(e.getMessage());
            }
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                	XLog.logd("OnCompletionListener");
                    mMediaPlayer.release();
                    
                }
            });
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.release();
                  
                    return false;
                }
            });
        } catch (IllegalStateException e) {
            XLog.loge(e.getMessage());
        }
    }

	public static String createTradeSerialNumber(Context cx){
		File file = new File(cx.getFilesDir(),"sn");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long fileSize = file.length();
		if(fileSize>999999){
			file.delete();
			return String.format("%06d",0);
		}

		try {
			FileOutputStream fous = new FileOutputStream(file,true);
			fous.write(new byte[]{0x00});
			fous.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.format("%06d",fileSize);
	}

	public static String getLocalIpAdress(Context c){
		//获取wifi服务
		WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		//判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = int2ip(ipAddress);
		return ip;
	}

	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}
}
