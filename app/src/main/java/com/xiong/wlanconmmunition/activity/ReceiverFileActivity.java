package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ReceiverFileActivity extends ActionBarBaseActivity implements OnClickListener{

	private ImageView receiverfileBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab receivering_tab 
		    = getActionBar().newTab().setText(getResources().getString(R.string.file_receivering_tab));
		ActionBar.Tab receivered_tab 
	    = getActionBar().newTab().setText(getResources().getString(R.string.file_receivered_tab));
		receivering_tab.setTabListener(new TabLister( new ReceiveringFileFragment()));
		receivered_tab.setTabListener(new TabLister(new ReceiveredFileFragment()));
		getActionBar().addTab(receivering_tab);
		getActionBar().addTab(receivered_tab);
		
		setContentView(R.layout.receiverfile_layout);
	}


	@Override
	protected View createActionBarView(LayoutInflater inflater) {
		
		return inflater.inflate(R.layout.receiverfile_actionbarlayout, null);
	}

	@Override
	protected void initCustomView() {
		receiverfileBack = (ImageView) getCustomView().findViewById(R.id.receiverfile_back);
		receiverfileBack.setOnClickListener(this);
	}

	@Override
	protected boolean onBackKey() {
		finishWithAnimation();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.receiverfile_back:
			finishWithAnimation();
			break;

		default:
			break;
		}
		
	}
	
	class TabLister implements ActionBar.TabListener{
		private Fragment fragment;

		public TabLister(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.receiverfile_fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		
		}
		
	}


}
