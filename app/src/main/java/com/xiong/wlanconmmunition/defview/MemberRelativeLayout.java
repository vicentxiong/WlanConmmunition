package com.xiong.wlanconmmunition.defview;

import com.xiong.wlanconmmunition.R;

import android.content.Context;
import android.location.Address;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MemberRelativeLayout extends BaseRelativeLayout {
	private ImageView img_icon;
    private LinearLayout ll_memberinfo_container;
    private TextView tv_name,tv_hostname,tv_ip;
    
    private static final int ICON_ID = 0x5f361427;
    private static final int CONTAINER_ID = 0x5f361428;
    private static final int IP_ID = 0x5f361429;
    
    public MemberRelativeLayout(Context context) {
  		super(context);
  	}
    
	@Override
	protected void createChildViews(Context cx) {
		img_icon = new ImageView(cx);
		img_icon.setImageResource(R.drawable.default_icon);
		img_icon.setId(ICON_ID);
		ll_memberinfo_container = new LinearLayout(cx);
		ll_memberinfo_container.setId(CONTAINER_ID);
		tv_name = new TextView(cx);
		tv_hostname = new TextView(cx);
	    tv_ip = new TextView(cx);
	    tv_ip.setId(IP_ID);
	    tv_name.setText("xiongxin");
	    tv_hostname.setText("localhost");
	    tv_ip.setText("192.168.0.16");
	}
	@Override
	protected void onSetLayoutAndSuperView() {
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		addView(img_icon, rl);
		
		rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll_memberinfo_container.addView(tv_name);
		ll_memberinfo_container.addView(tv_hostname);
		rl.addRule(RelativeLayout.RIGHT_OF, ICON_ID);
		addView(ll_memberinfo_container, rl);
		
		rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.RIGHT_OF, ICON_ID);
		rl.addRule(RelativeLayout.BELOW, CONTAINER_ID);
		addView(tv_ip, rl);
	}
	
	public void setImageIcon(int icon){
		img_icon.setImageResource(icon);
	}
	
	public void setName(String name){
		tv_name.setText(name);
	}
	
	public void setHostName(String hostName){
		tv_hostname.setText(hostName);
	}
	
	public void setIP(String ip){
		tv_ip.setText(ip);
	}
	
	public String getName(){
		return tv_hostname.getText().toString();
	}
 












	
}
