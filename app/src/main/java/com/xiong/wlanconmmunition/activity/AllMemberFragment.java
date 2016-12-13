package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.ChatInfo;
import com.xiong.wlanconmmunition.DataPacketProtocol;
import com.xiong.wlanconmmunition.Localhost;
import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.MessageManager;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.SessionManager.SessionLister;
import com.xiong.wlanconmmunition.XLog;
import com.xiong.wlanconmmunition.MessageProcesser.DispatchUdpDataPacketLister;
import com.xiong.wlanconmmunition.db.DataBaseManager;
import com.xiong.wlanconmmunition.db.UserSelfTable;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.GroupInfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class AllMemberFragment extends Fragment implements DispatchUdpDataPacketLister,OnClickListener{
	private View mRoot;
	private MessageManager msgMgr = null;
	private MemberManager memMgr = null;
	private SessionLister mSsLister;
	private LinearLayout mFmlayout,mGloblelayout;
	private ScrollView mScrView;
	private LinearLayout listHeader;
	private Activity attachActivity;
	private ProgressBar mProgress;
	private ImageView headerArrow;
	private TextView headerTips;
	private RotateAnimation tipsAnimation,reverseAnimation;
	private int headerHeight,lastHeaderHeightPadding;
	private int headerState = DONE;
	private boolean isBack ;
	private String groupId;
	
	private static final int DURATION = 200;
	private static final int PULLTOREFRESH = 0;
	private static final int RELEASETOREFRESH = 1;
	private static final int REFRESHING = 3;
	private static final int DONE = 4;

	@Override
	public void onAttach(Activity activity) {
		attachActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		msgMgr = MessageManager.getInstance();
		memMgr = MemberManager.getInstance();
		
		msgMgr.registerUdpRcvMsgLister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return initUI(inflater, container);
	}
	
	private View initUI(LayoutInflater inflater, ViewGroup container){
		mRoot = inflater.inflate(R.layout.allmemberlist, container,false);
		mGloblelayout = (LinearLayout) mRoot.findViewById(R.id.globlelayout);
		mScrView = (ScrollView) mRoot.findViewById(R.id.memberviewroot);
		mFmlayout = (LinearLayout) mRoot.findViewById(R.id.memberviewcontainer);
		
		listHeader = (LinearLayout) inflater.inflate(R.layout.memberlistheader, null);
		measureHeaderView(listHeader);
		headerHeight = listHeader.getMeasuredHeight();
		lastHeaderHeightPadding = -1*headerHeight;
		listHeader.setPadding(0, lastHeaderHeightPadding, 0, 0);
		listHeader.invalidate();
		mGloblelayout.addView(listHeader, 0);
		
		mProgress = (ProgressBar) listHeader.findViewById(R.id.head_progressBar);
		headerArrow = (ImageView) listHeader.findViewById(R.id.head_arrowImageView);
		headerTips = (TextView) listHeader.findViewById(R.id.head_tipsTextView);
		
		tipsAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		tipsAnimation.setInterpolator(new LinearInterpolator());
		tipsAnimation.setDuration(DURATION);
		tipsAnimation.setFillAfter(true);
		
		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(DURATION);
		reverseAnimation.setFillAfter(true);
		
		mScrView.setOnTouchListener(new OnTouchListener() {
			private int beginY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					beginY = (int) (event.getY()+mScrView.getScrollY());
					break;
				case MotionEvent.ACTION_MOVE:
					if(mScrView.getScrollY()==0||lastHeaderHeightPadding>-headerHeight&&headerState!=REFRESHING){
						int interval  = (int) (event.getY() - beginY);
						//寰�笅婊戝姩
						if(interval>0){
							interval = interval/2;
							lastHeaderHeightPadding = interval - headerHeight;
							listHeader.setPadding(0,lastHeaderHeightPadding, 0,0);
							if(lastHeaderHeightPadding>0){
								headerState = RELEASETOREFRESH;
								if(!isBack){
									isBack = true;
									changeHeaderViewByState();
								}
							}else{
								headerState = PULLTOREFRESH;
								changeHeaderViewByState();
							}
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if(headerState!=REFRESHING){
						switch (headerState) {
						case DONE:
							
							break;
						case PULLTOREFRESH:
							headerState = DONE;
							changeHeaderViewByState();
							break;
						case RELEASETOREFRESH:
							isBack = false;
							headerState = REFRESHING;
							changeHeaderViewByState();
							onRefresh();
							break;
						default:
							break;
						}
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateMemberListUI();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		for (int i = 0; i < MemberManager.getInstance().getMemberSize(); i++) {
			updateUnReadMsgIcon(mFmlayout.getChildAt(i), MemberManager.getInstance().getIpByIndex(i));
		}
	}

	public void buildGroupListOwner(String groupName){
		groupId = Util.createTradeSerialNumber(attachActivity);
		MemberInfo member = memMgr.buildMember(attachActivity.getResources().getDrawable(R.drawable.default_icon),groupName,groupId);
		memMgr.addMemberToList(member);
		updateMemberListUI();
	}

	public void buildGroupListOther(String srcIp,String content){
		String[] col = content.split("_");
		groupId = col[1];
		for (int j=0;j<col.length;j++)Log.d("xx",col[j]);
		for (int i=2;i<col.length;i++){
			memMgr.addGroupMember(col[1], memMgr.getMemberInfiByIp(col[i]));
		}
		memMgr.addGroupMember(col[1],memMgr.getMemberInfiByIp(srcIp));
		MemberInfo member = memMgr.buildMember(attachActivity.getResources().getDrawable(R.drawable.default_icon),col[0],groupId);
		memMgr.addMemberToList(member);
		updateMemberListUI();
	}

	public void buildGroupListOtherResponse(String srcIP,String content){
		memMgr.addGroupMember(content, memMgr.getMemberInfiByIp(srcIP));
	}

	@Override
	public void processDataPacket(DataPacketProtocol srcDataPacket) {
		int cmd = srcDataPacket.command;
		switch (cmd) {
		case MessageManager.IPMSG_ENTRY:
		case MessageManager.IPMSG_ANSENTRY:
			if(!memMgr.hasMemberInfo(srcDataPacket.getSrcIp())){
				MemberInfo member = 
						memMgr.buildMember(attachActivity.getResources().getDrawable(R.drawable.default_icon), srcDataPacket.senderName, srcDataPacket.senderHostName, srcDataPacket.getSrcIp());
				int index = memMgr.addMemberToList(member);
				
//				View mView = contructsNewMemberView(member);
//				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//				if(index!=0){
//					llp.topMargin = (int) attachActivity.getResources().getDimension(R.dimen.memberlayout_padding);
//				}
//				mFmlayout.addView(mView,index,llp);
				
				updateMemberListUI();
			}else{
				if(!srcDataPacket.senderName.equals(memMgr.getSenderNameByIp(srcDataPacket.getSrcIp()))){
					memMgr.updataSenderName(srcDataPacket.getSrcIp(), srcDataPacket.senderName);
					updateMemberListUI();
				}
			}
			XLog.logd("ENTRY OR ANSENTRY"+srcDataPacket.getSrcIp()+"==>");
			break;
		case MessageManager.IPMSG_EXIT:
			int index = memMgr.deleteMemberFromList(srcDataPacket.getSrcIp());
//			if(index >= 0){
//				mFmlayout.removeViewAt(index);
//			}
			
			updateMemberListUI();
			break;
		case MessageManager.IPMSG_FILETRANS_REQUEST:
		case MessageManager.IPMSG_SENDMSG:
			ChatInfo chat = SessionManager.getInstance().buildChatInfo(srcDataPacket, SessionManager.COMEMSG,null);
			mSsLister = SessionManager.getInstance().getSessionLister(srcDataPacket.getSrcIp());
			if(mSsLister!=null){
				mSsLister.handleReceiverMessage(chat);
			}else{
				SessionManager.getInstance().saveUnReadMesaage(chat);
				int positoin = MemberManager.getInstance().getIndexByIp(srcDataPacket.getSrcIp());
				if(positoin > -1){
					updateUnReadMsgIcon(mFmlayout.getChildAt(positoin), srcDataPacket.getSrcIp());
				}
			}
			break;
		case MessageManager.IPMSG_RCVMSG:
			break;
			case MessageManager.IPMSG_GROUP_SENDMSG:
				Log.d("xx","receiver group message");
				String[] colum=srcDataPacket.content.split("_");
				String groupID = colum[0];
				ChatInfo groupChat = SessionManager.getInstance().buildChatInfo(srcDataPacket, SessionManager.COMEMSG,null);
				mSsLister = SessionManager.getInstance().getSessionLister(groupID);
				if(mSsLister!=null){
					mSsLister.handleReceiverMessage(groupChat);
				}else{
					SessionManager.getInstance().saveUnReadGroupMessage(groupChat,groupID);
					int positoin = MemberManager.getInstance().getIndexByIp(groupID);
					if(positoin > -1){
						updateUnReadMsgIcon(mFmlayout.getChildAt(positoin), groupID);
					}
				}
				break;
		case MessageManager.IPMSG_SENDMSGCHECK:
			break;
			
		case MessageManager.IPMSG_ANSUPDATEICON:	
		case MessageManager.IPMSG_UPDATEICON:
			Util.outPutIconFile(Util.stringTobytes(srcDataPacket.content), srcDataPacket.getSrcIp());
			memMgr.updateIcon(srcDataPacket.getSrcIp(), memMgr.getMemberIcon(srcDataPacket.getSrcIp()));
			updateMemberListUI();
			break;
		case MessageManager.IPMSG_FILETRANS_RESPONSE:
			
			break;
			case MessageManager.IPMSG_GROUP_REQUEST:
				buildGroupListOther(srcDataPacket.getSrcIp(),srcDataPacket.content);
				break;
			case MessageManager.IPMSG_GROUP_RESPONSE:
				buildGroupListOtherResponse(srcDataPacket.getSrcIp(),srcDataPacket.content);
				break;
		default:
			break;
		}
	}
	
	public void updateUnReadMsgIcon(View view,String ip){
		if(view==null){
			return;
		}
		TextView tv_size = (TextView) view.findViewById(R.id.unreadSize);
		int size = SessionManager.getInstance().getUnReadMessageSize(ip);
		if(size == 0){
			tv_size.setText("");
		}else if(size > 99){
			tv_size.setText("+"+99);
		}else{
			tv_size.setText("+"+size);
		}
		
	}
	
	private View createMemberView(){
		LayoutInflater inflater = (LayoutInflater) attachActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.signalmember, null);
	}
	
	private View contructsNewMemberView(MemberInfo meminfo){
		View mView = createMemberView();
		mView.setOnClickListener(this);
		
		ImageView icon = (ImageView) mView.findViewById(R.id.membericon);
		TextView tvname = (TextView) mView.findViewById(R.id.membername);
		TextView tvhostname = (TextView) mView.findViewById(R.id.memberhostname);
		TextView tvip = (TextView) mView.findViewById(R.id.memberip);
		if(meminfo instanceof GroupInfo){
			icon.setImageDrawable(meminfo.iCon);
			tvname.setText(meminfo.name);
			tvip.setText(meminfo.ip);
		}else {
			icon.setImageDrawable(meminfo.iCon);
			tvname.setText(meminfo.name);
			tvhostname.setText(meminfo.hostname);
			tvip.setText(meminfo.ip);
		}

		if(meminfo instanceof GroupInfo){

			mView.setTag(R.id.type,"group");
			mView.setTag(R.id.ip,groupId);
		}else {
			mView.setTag(R.id.type,"member");
			mView.setTag(R.id.ip,meminfo.ip);
		}

		return mView;
	}
	
	private void updateMemberListUI(){
		mFmlayout.removeAllViews();
		int size = memMgr.getMemberSize();
		if(size > 0){
			for (int i = 0; i < size; i++) {
				String ip = memMgr.getIpByIndex(i);
				MemberInfo member = memMgr.getMemberInfiByIp(ip);
				View mView = contructsNewMemberView(member);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				if(i!=0){
					llp.topMargin = (int) attachActivity.getResources().getDimension(R.dimen.memberlayout_padding);
				}
				mFmlayout.addView(mView,i,llp);
			}
		}
	}
	
	//鐢变簬oncreate涓嬁涓嶅埌header鐨勯珮搴︽墍浠ヨ鎵嬫満璁＄畻
	private void measureHeaderView(View headerView){
		android.view.ViewGroup.LayoutParams lp = headerView.getLayoutParams();
		if(lp==null){
			lp = new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int height = lp.height;
		int childHeightSpec ;
		if(height>0){
			childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		}else{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		headerView.measure(childWidthSpec, childHeightSpec);
	}
	
	private void changeHeaderViewByState(){
		switch (headerState) {
		case PULLTOREFRESH:
			if(isBack){
				isBack = false;
				headerArrow.startAnimation(reverseAnimation);
			}
			headerTips.setText(attachActivity.getResources().getString(R.string.pulltorefresh));
			break;
		case RELEASETOREFRESH:
			headerArrow.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
			headerTips.setVisibility(View.VISIBLE);
			headerTips.setText(attachActivity.getResources().getString(R.string.realesetorefresh));
			headerArrow.clearAnimation();
			headerArrow.startAnimation(tipsAnimation);
			break;
		case REFRESHING:
			lastHeaderHeightPadding = 0;
			listHeader.setPadding(0, lastHeaderHeightPadding, 0, 0);
			listHeader.invalidate();
			mProgress.setVisibility(View.VISIBLE);
			headerArrow.setVisibility(View.GONE);
			headerTips.setText(attachActivity.getResources().getString(R.string.refreshing));
			headerArrow.clearAnimation();
			break;
		case DONE:
			lastHeaderHeightPadding = -1*headerHeight;
			listHeader.setPadding(0, lastHeaderHeightPadding, 0, 0);
			listHeader.invalidate();
			mProgress.setVisibility(View.GONE);
			headerArrow.setVisibility(View.VISIBLE);
			headerTips.setText(attachActivity.getResources().getString(R.string.pulltorefresh));
			headerArrow.clearAnimation();
			break;

		default:
			break;
		}
	}
	
    private void onRefresh() {  
         new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				msgMgr.sendUdpMessage(msgMgr.createUdpDataPacketProtocol(Localhost.ALLBROADIP, MessageManager.IPMSG_ENTRY));
				DataBaseManager dbMgr = DataBaseManager.getInstance(attachActivity);
				dbMgr.open();
				Object obj = dbMgr.query(UserSelfTable.TABLE_NAME, new String[]{UserSelfTable.COL_SELFICON}, null, null, null, null, null);
				if(obj==null){
					obj = 0;
				}
				int result = (Integer)obj ;
				if(result == 1){
					XLog.logd("update newicon");
					msgMgr.sendUdpMessage(msgMgr.createUdpDataPacketProtocol(Localhost.ALLBROADIP, MessageManager.IPMSG_UPDATEICON, 
				              Util.bytesToString(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.LOCAL_ICON_NAME).getPath())));
					int res = -1;
					ContentValues values = new ContentValues();
					values.put(UserSelfTable.COL_SELFICON, 0);
				    res = dbMgr.update(UserSelfTable.TABLE_NAME, values, null, null);
				}
				dbMgr.close();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				onRefreshComplete();
			}
        	
        }.execute();
    } 
    
   public void onRefreshComplete(){
	   headerState = DONE;
	   changeHeaderViewByState();
   }

   @Override
   public void onClick(View v) {
	   String type = (String) v.getTag(R.id.type);
	   String ip = (String) v.getTag(R.id.ip);
	   XLog.logd("tag" + ip);
	   Intent sessionIntent = new Intent(attachActivity, SessionActivity.class);
	   sessionIntent.putExtra(SessionManager.CHATIP_EXTRA, ip);
	   sessionIntent.putExtra(SessionManager.CHAT_TYPE,type);
	   attachActivity.startActivityForResult(sessionIntent, 0);
	   //attachActivity.overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
   }
}
