package com.xiong.wlanconmmunition.filemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;

import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.XLog;

public class ServerMultiThreadTransfer extends MultiThreadTransfer {
	private FileOutputStream fous;
	private BufferedReader br;
	private BufferedWriter bw;
	private Handler mHandler;
	private String fileName;
	private long current;

	public ServerMultiThreadTransfer(Socket socket,Handler handler) {
		mSocket = socket;
		mHandler = handler;
		init();
	}


	private void doInBackGroup(byte[] bytes, int offset, int length) {
		try {
			fous.write(bytes, offset, length);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createFileOutPutStream(String dir, String filename) {

		try {
			fous = new FileOutputStream(Util.openOrCreateFile(dir, filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getHeadContent(String readLine) {
		return readLine.substring(readLine.indexOf(":")+1);
	}

	@Override
	protected void onLastExecute() {
		try {
			fous.close();
			ins.close();
			ous.close();
			br.close();
			bw.close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void loopExec() {
		do {
			try {
				br = getBufferedReader();
				XLog.logd("server runnable statup");
				String temp = null;
				String head_fileName = null;
				String head_packetId = null;
				int lineCount = 0;	
				while((temp = br.readLine())!=null){
					if(temp.startsWith("over:")){
						break;
					}
					switch (lineCount) {
					case 0:
						head_fileName  = temp;
						break;
					case 1:
						head_packetId = temp;
						break;
					default:
						break;
					}
					lineCount++;
				}
				XLog.logd("server: header>>" +head_fileName +" packeId: " + head_packetId);
				
				if (head_fileName == null || 
					head_packetId ==null || 
					!head_fileName.startsWith("filename:") || 
					!head_packetId.startsWith("packetId:")) {
					bw.write("400"+"\r\n");
					bw.flush();
					break;
				}
				fileName = getHeadContent(head_fileName);
				createFileOutPutStream(Util.LOCA_RECEIVER,fileName);
				packetId = getHeadContent(head_packetId);
				bw = getBufferedWriter();
				bw.write("100"+"\r\n");
				bw.flush();
				sendMsg(TransferManager.STRAT,fileName);
				byte[] by = new byte[1024];
				int len = -1;
				while ((len = ins.read(by)) != -1) {

					doInBackGroup(by, 0, len);
					current+=len;
					
					sendMsg(TransferManager.PROCESS, packetId+TransferManager.PATTERN+current);
				}
                sendMsg(TransferManager.STOP,packetId+TransferManager.PATTERN+fileName);
			} catch (IOException e) {
				sendMsg(TransferManager.FAIL,packetId+TransferManager.PATTERN+fileName);
				e.printStackTrace();
			}
		} while (false);

	}
	
	private void sendMsg(int what,Object obj){
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		msg.sendToTarget();
	}

}
