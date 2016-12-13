package com.xiong.wlanconmmunition.filemanager;

import java.io.File;
import java.util.Stack;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

public class FileController {
	public static final String MB = "MB";
	public static final String KB = "KB";
	public static final String B = "B";
    private File rootDir = new File("/sdcard");
	private Stack<File> overDir = new Stack<File>();
	
	public void init(){
		overDir.push(rootDir);
	}
	
	public void enterSubDir(File fileDir){
		overDir.push(fileDir);
	}
	
	public boolean returnParentDir(){
		if(overDir.size()>1){
			overDir.pop();
			return true;
		}
		return false;
	}
	
	public void returnDir(int index){
		while (overDir.indexOf(overDir.peek())!=index) {
			
			overDir.pop();
		}
	}
	
	public File[] getCurrentDirList(){
		File dir = overDir.peek();
		return dir.listFiles();
	}
	
	public int size(){
		return overDir.size();
	}
	
	public File getFileByIndex(int index){
		return overDir.elementAt(index);
	}

}
