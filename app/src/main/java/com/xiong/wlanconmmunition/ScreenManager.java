package com.xiong.wlanconmmunition;

import java.util.Stack;

import android.app.Activity;

public class ScreenManager {
	private static Stack<Activity> activityStack;
	private static ScreenManager instance;

	private ScreenManager() {
	}

	public static ScreenManager getScreenManager() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	// 退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 退出栈中所有Activity
	public void popAllActivityExceptOne() {
		while (activityStack.size()>0) {
			activityStack.pop().finish();
		}
		
	}
}
