package com.xiong.wlanconmmunition.filemanager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.xiong.wlanconmmunition.DataPacketProtocol;
import com.xiong.wlanconmmunition.Localhost;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.XLog;

public class TransferManager extends Handler{

	private static TransferManager transferMgr;
	private ServerSocket server;
	private ExecutorService executorService;
	public static final String PATTERN = "==";
	public static final String RECEIVER = "receiver";
	public static final String REJECT = "reject";
	public static final int STRAT = 1;
	public static final int PROCESS = 2;
	public static final int STOP = 3;
	public static final int FAIL = 4;
	private static final int POOL_SIZE = 10;
	private ProcessUpdateLister processLister;
	
	private ArrayList<FileTransRequest> receiverFiles = new ArrayList<FileTransRequest>();
	
	private Context mContext;

	private TransferManager(Context c) {
		mContext = c;
		
	}

	public static TransferManager getInstance(Context cx) {
		if (transferMgr == null) {
			transferMgr = new TransferManager(cx);
		}

		return transferMgr;
	}
	
	public static TransferManager getInstance(){
		return transferMgr;
	}
	
	public void resgiterProcessLister(ProcessUpdateLister lister){
		processLister = lister;
	}
	
	public void addReceiverFile(FileTransRequest request){
		receiverFiles.add(request);
	}
	
	public ArrayList<FileTransRequest> getReceiveringFiles(){
		return receiverFiles;
	}
	
	public int getIndexByPacketId(String pid){
		int index = -1;
		for (int i = 0; i < receiverFiles.size(); i++) {
			if(pid.equals(receiverFiles.get(i).packetId)){
				index = i;
				break;
			}
		}
		return index;
	}
	
	public void removeReceiveringFile(String pid){
		for (int i = 0; i < receiverFiles.size(); i++) {
			if(pid.equals(receiverFiles.get(i).packetId)){
				receiverFiles.remove(i);
				break;
			}
		}
	}

	public void startUp() {
		try {
			server = new ServerSocket(Localhost.BINDPORT);
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
			while (true) {
				Socket socket = server.accept();
				XLog.logd("new socket connection");
				MultiThreadTransfer transfer = new ServerMultiThreadTransfer(socket,this);
				executorService.execute(transfer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void dispatchFileTransResponse(DataPacketProtocol srcPacket) {

		String[] content = srcPacket.content.split(PATTERN);
		XLog.logd("receiver:response>>>>dispatchFileTransResponse"+ "  packetId:" + content[0] + " status:" + content[1]);
		if (content[1].equals(REJECT)) {
			SessionManager.getInstance().removeFileTransRequest(srcPacket.getSrcIp(), content[0]);

		} else if (content[1].equals(RECEIVER)) {
			String filePath = SessionManager.getInstance().removeFileTransRequest(srcPacket.getSrcIp(), content[0]);
			XLog.logd("filepath:" + filePath);
			connectServer(srcPacket.getSrcIp(), new File(filePath),content[0]);
		}
	}

	private void connectServer(String ip, File file,String packetId) {

		ClientMultiThreadTransfer transfer = new ClientMultiThreadTransfer(ip);
		transfer.setFile(file);
		transfer.setPacketId(packetId);
		ExecutorService executorCilent = Executors.newCachedThreadPool();
		executorCilent.execute(transfer);
	}
	
	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		Object obj = msg.obj;
	    switch (what) {
		case STRAT:
			Toast.makeText(mContext, (String)obj+mContext.getResources().getString(R.string.file_start_receiver), 1000).show();
			break;
		case STOP:
			String[] stop_str= ((String)obj).split(PATTERN);
			removeReceiveringFile(stop_str[0]);
			XLog.logd("stop receiver");
			if(processLister!=null)
			    processLister.onTransFileSuccessed();
			Toast.makeText(mContext, stop_str[1]+mContext.getResources().getString(R.string.file_ok_receiver), 1000).show();
			break;
		case PROCESS:
			String[] process_str= ((String)obj).split(PATTERN);
			//XLog.logd("process:    "+(String)obj);
			if(processLister!=null){
				processLister.onUpdateFileProcess(process_str[0], Long.parseLong(process_str[1]));
			}
			
			break;
		case FAIL:
			String[] fail_str= ((String)obj).split(PATTERN);
			removeReceiveringFile(fail_str[0]);
			if(processLister!=null)
			     processLister.onTransFileFail();
			Toast.makeText(mContext, fail_str[1]+mContext.getResources().getString(R.string.file_fail_receiver), 1000).show();
			break;
		default:
			break;
		}
		super.handleMessage(msg);
	}
	
	public interface ProcessUpdateLister{
		public void onUpdateFileProcess(String packetid,long process);
		public void onTransFileSuccessed();
		public void onTransFileFail();
	}

}
