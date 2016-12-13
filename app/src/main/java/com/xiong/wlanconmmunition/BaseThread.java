package com.xiong.wlanconmmunition;

public abstract class BaseThread extends Thread {
	protected MessageProcesser mProcesser;
	
	public BaseThread(MessageProcesser processer){
		mProcesser = processer;
	}
	
	public abstract boolean loopExec();
	
	@Override
	public void run() {
		while(true){
			if(!loopExec()){
				break;
			}
		}
	}

}
