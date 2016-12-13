package com.xiong.wlanconmmunition.filemanager;

import com.xiong.wlanconmmunition.ChatInfo;

public class FileTransRequest extends ChatInfo{
	public String fileName;
	public String filePath;
	public long fileLength;
	public int isResponse;
	
	public void parseContent(){
		if(content!=null){
			String[] entry = content.split(TransferManager.PATTERN);
			fileName = entry[0];
			fileLength = Long.parseLong(entry[1]);
		}
	}

}
