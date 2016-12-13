package com.xiong.wlanconmmunition.activity;

import java.util.ArrayList;

import android.view.View;
import android.widget.ProgressBar;

import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.activity.BaseFileControlFragment.ReceiverFileInfo;
import com.xiong.wlanconmmunition.filemanager.FileTransRequest;
import com.xiong.wlanconmmunition.filemanager.TransferManager;

public class ReceiveringFileFragment extends BaseFileControlFragment {
	private ArrayList<FileTransRequest> list ;

	@Override
	protected void notifyFileUpdateProcess(String packetid, long process) {
		if(fileList != null && adapter != null){
			int index = TransferManager.getInstance().getIndexByPacketId(packetid);
			if(index > -1){
				View view = fileList.getChildAt(index);
				if(view!=null){
					ProgressBar bar = (ProgressBar) view.findViewById(R.id.receiver_file_process);
					bar.setProgress((int) process);
				}
			}
		}

	}

	@Override
	protected void notifyFileSuccessed() {
		updateView();

	}

	@Override
	protected void notifyFileFail() {
		updateView();

	}

	@Override
	protected ArrayList<ReceiverFileInfo> onGetResumeArray() {
		ArrayList<ReceiverFileInfo> infos = new ArrayList<BaseFileControlFragment.ReceiverFileInfo>();
		list = TransferManager.getInstance().getReceiveringFiles();
		if(list.size()==0){
			setFileEmpyt(true);
			return infos;
		}
		setFileEmpyt(false);
		for (int i = 0; i < list.size(); i++) {
			ReceiverFileInfo info = new ReceiverFileInfo();
			info.fileName = list.get(i).fileName;
			info.fileLength = list.get(i).fileLength;
			info.status = BaseFileControlFragment.RECEIVERING;
			infos.add(info);
		}
		return infos;
	}

}
