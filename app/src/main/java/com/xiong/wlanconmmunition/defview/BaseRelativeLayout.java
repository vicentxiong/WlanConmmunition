package com.xiong.wlanconmmunition.defview;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class BaseRelativeLayout extends RelativeLayout {

	public BaseRelativeLayout(Context context) {
		super(context);
		createChildViews(context);
		onSetLayoutAndSuperView();
	}
	
	protected abstract void createChildViews(Context cx);
	
	protected abstract void onSetLayoutAndSuperView();

}
