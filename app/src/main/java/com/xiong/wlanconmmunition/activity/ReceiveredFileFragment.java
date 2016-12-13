package com.xiong.wlanconmmunition.activity;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.filemanager.FileController;

public class ReceiveredFileFragment extends BaseFileControlFragment {

	@Override
	protected void notifyFileUpdateProcess(String packetid, long process) {

	}

	@Override
	protected void notifyFileSuccessed() {

	}

	@Override
	protected void notifyFileFail() {

	}

	@Override
	protected ArrayList<ReceiverFileInfo> onGetResumeArray() {
		ArrayList<ReceiverFileInfo> infos = new ArrayList<BaseFileControlFragment.ReceiverFileInfo>();
		File receiverDir = new File(Environment.getExternalStorageDirectory(),Util.LOCA_RECEIVER);
		File[] files = receiverDir.listFiles();
		if(!receiverDir.exists() || files == null || files.length==0){
			setFileEmpyt(true);
			return infos;
		}
		setFileEmpyt(false);
		for (int i = 0; i < files.length; i++) {
			ReceiverFileInfo info = new ReceiverFileInfo();
			info.fileName = files[i].getName();
			info.fileLength = files[i].length();
			info.filetime = files[i].lastModified();
			info.status = BaseFileControlFragment.RECEIVERED;
			infos.add(info);
		}
		return infos;
	}

}
