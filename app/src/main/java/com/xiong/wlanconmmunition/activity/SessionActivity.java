package com.xiong.wlanconmmunition.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

import com.xiong.wlanconmmunition.ChatInfo;
import com.xiong.wlanconmmunition.DataPacketProtocol;
import com.xiong.wlanconmmunition.MemberInfo;
import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.MessageManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.SessionManager.SessionLister;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.filemanager.FileController;
import com.xiong.wlanconmmunition.filemanager.FileTransRequest;
import com.xiong.wlanconmmunition.filemanager.TransferManager;
import com.xiong.wlanconmmunition.XLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class SessionActivity extends ActionBarBaseActivity implements OnClickListener, OnItemClickListener{
	private ImageView actionbar_img_back;
	private ImageView action_img_voice;
	private TextView actionbar_chat_obj;
	private Button mSendMsg;
	private Button mSendFile;
	private EditText mChatContent;
	private ListView mChatList;
	private PopupWindow popWindow;
	private MemberInfo member;
	private ArrayList<ChatInfo> unReadMsg ;
	private ChatMessageAdapter chatAdapter;
	private String ip,type;
	private FileTransRequest focusRequest;
	private static final int FILE_OPTION = 1;
	private String mImagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sessionlayout);
		String ip = initData();
		initUI();
		initPopupWindow();
		
		SessionManager.getInstance().clearAllUnReadMessages(ip);
		SessionManager.getInstance().addSessionLister(ip, new ChatSessionLister());

		if(type.equals("member")){
			action_img_voice.setVisibility(View.GONE);
		}
	}
	
	private String initData(){
		ip = getIntent().getStringExtra(SessionManager.CHATIP_EXTRA);
		type = getIntent().getStringExtra(SessionManager.CHAT_TYPE);
		member = MemberManager.getInstance().getMemberInfiByIp(ip);
		unReadMsg = SessionManager.getInstance().getUnReadMessages(ip);
		return ip;
	}
	
	private void initUI(){
		actionbar_chat_obj.setText(member.name);
		//main part layout
		mSendMsg = (Button) findViewById(R.id.chatSend);
		mSendMsg.setOnClickListener(this);
		mSendFile = (Button) findViewById(R.id.fileSend);
		mSendFile.setOnClickListener(this);
		mSendFile.setOnCreateContextMenuListener(this);
		mChatContent = (EditText) findViewById(R.id.chatContent);
		mChatList = (ListView) findViewById(R.id.chatListView);
		chatAdapter = new ChatMessageAdapter(getApplicationContext());
		chatAdapter.addChatInfoArrayToQunene(unReadMsg);
		mChatList.setAdapter(chatAdapter);
		mChatList.setSelection(chatAdapter.size() - 1);
		mChatList.setOnItemClickListener(this);
	}
	
	private void initPopupWindow(){
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.popupwindow_layout, null);
		TextView receiver = (TextView) view.findViewById(R.id.filereceiver);
		TextView reject = (TextView) view.findViewById(R.id.filereject);
		receiver.setOnClickListener(this);
		reject.setOnClickListener(this);
		popWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOutsideTouchable(true);
		popWindow.setFocusable(true);
	}
	
	private void updateChatAdapter(ChatInfo cinfo){
		chatAdapter.addChatInfoToQunene(cinfo);
		chatAdapter.notifyDataSetChanged();
		mChatList.setSelection(chatAdapter.size()-1);
	}
	
	class ChatSessionLister implements SessionLister{

		@Override
		public void handleReceiverMessage(ChatInfo srcData) {
			
			updateChatAdapter(srcData);
		}
		
	}

	@Override
	protected View createActionBarView(LayoutInflater inflater) {
		
		return inflater.inflate(R.layout.session_actionbarlayout, null);
	}

	@Override
	protected void initCustomView() {
		actionbar_img_back  = (ImageView) getCustomView().findViewById(R.id.session_back);
		actionbar_chat_obj  = (TextView) getCustomView().findViewById(R.id.caht_obj);
		action_img_voice = (ImageView) findViewById(R.id.session_voice);
		
		actionbar_img_back.setOnClickListener(this);
		action_img_voice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.session_back:
            exitSession();
           
			break;
		case R.id.session_voice:
			//startVoiceSip();
			Intent group = new Intent(this,GroupMemberActivity.class);
			group.putExtra(SessionManager.CHATIP_EXTRA,ip);
			group.putExtra(SessionManager.GROUP_NAME,member.name);
			startActivity(group);
			break;
		case R.id.chatSend:
			String message = mChatContent.getText().toString();
			if(message!=null && message.length()>0){
				if(type.equals("group")){
					Log.d("xx","group send message");
					sendMessage(MessageManager.IPMSG_GROUP_SENDMSG, message, null);
				}else
					sendMessage(MessageManager.IPMSG_SENDMSG,message,null);
			}else{
				Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.sendmessage_toast), 1000).show();
			}
			mChatContent.setText("");
			break;
		case R.id.fileSend:
			/*
			Intent fileTranstor = new Intent(getApplicationContext(), FileOptionActivity.class);
			startActivityForResult(fileTranstor, FILE_OPTION);
			*/
			if(type.equals("group")){
				Toast.makeText(this,R.string.group_not_support_image,Toast.LENGTH_SHORT).show();
				return;
			}
			v.showContextMenu();
			break;
		case R.id.filereceiver:
			TransferManager.getInstance().addReceiverFile(focusRequest);
			sendFileTransResponse(TransferManager.RECEIVER);
			break;
		case R.id.filereject:
			sendFileTransResponse(TransferManager.REJECT);
			break;
		default:
			break;
		}
		
	}
	
	private void startVoiceSip(){
		Intent sipVoice = new Intent(getApplicationContext(), VoiceActivity.class);
		startActivity(sipVoice);
	}
	
	private void sendFileTransResponse(String status){
		StringBuffer sb = new StringBuffer();
		sb.append(focusRequest.packetId);
		sb.append(TransferManager.PATTERN);
		sb.append(status);
		sendMessage(MessageManager.IPMSG_FILETRANS_RESPONSE, sb.toString(), null);
		focusRequest.isResponse = (status.equals(TransferManager.RECEIVER)?SessionManager.RECEIVERED:SessionManager.REJECT);
		popWindow.dismiss();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			/*
			Uri uri = data.getData();
			sendFileTransRequest(uri.getPath());
			*/
			switch (requestCode){
				case 1000:

					sendFileTransRequest(Util.openOrCreateFile(Util.LOCA_ICON_DIR, mImagePath).getPath());
					break;
				case 1001:
					Uri uri_gallery = data.getData();
					XLog.logd("uri == " + uri_gallery.getPath());
					InputStream ins = null;
					try {
						ins = getContentResolver().openInputStream(uri_gallery);
						mImagePath = Util.getFormatterTime()+".jpg";
						File f = Util.openOrCreateFile(Util.LOCA_ICON_DIR, mImagePath);
						OutputStream ous = new FileOutputStream(f);
						byte[] bytes = new byte[1024];
						int len=0;
						while ((len=ins.read(bytes))>0){
							ous.write(bytes,0,len);
						}
						ins.close();
						ous.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					sendFileTransRequest(Util.openOrCreateFile(Util.LOCA_ICON_DIR, mImagePath).getPath());
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void sendMessage(int cmd,String message,String filePath){
		MessageManager msgMgr = MessageManager.getInstance();
		DataPacketProtocol dp =null;
		if(cmd == MessageManager.IPMSG_SENDMSG || cmd == MessageManager.IPMSG_FILETRANS_REQUEST || cmd ==MessageManager.IPMSG_FILETRANS_RESPONSE ){
			dp = msgMgr.createUdpDataPacketProtocol(ip, cmd, message);
			msgMgr.sendUdpMessage(dp);
		}else if(cmd == MessageManager.IPMSG_GROUP_SENDMSG){
			ArrayList<MemberInfo> allGroupmember = MemberManager.getInstance().getGroupAllMembers(ip);
			int count = allGroupmember.size();
			if(count<=0) {
				Toast.makeText(this,R.string.group_empty,Toast.LENGTH_SHORT).show();
				return;
			}
			for (int i=0;i<count;i++){
				Log.d("xx","groud member ip ; " + allGroupmember.get(i).ip);
				dp = msgMgr.createUdpDataPacketProtocol(allGroupmember.get(i).ip, cmd, ip+"_"+message);
				msgMgr.sendUdpMessage(dp);
			}
		}

		Log.d("xx","tranfer file :" + filePath );
		ChatInfo cinfo = SessionManager.getInstance().buildChatInfo(dp, SessionManager.TOMSG, filePath);
		if(cmd == MessageManager.IPMSG_FILETRANS_REQUEST){
			SessionManager.getInstance().addFileTransRequest((FileTransRequest)cinfo);
		}
		if(cmd != MessageManager.IPMSG_FILETRANS_RESPONSE){
			updateChatAdapter(cinfo);
		}
		
	}
	
	private void sendFileTransRequest(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			StringBuffer sb= new StringBuffer();
			sb.append(file.getName());
			sb.append(TransferManager.PATTERN);
			sb.append(file.length());
			sendMessage(MessageManager.IPMSG_FILETRANS_REQUEST, sb.toString(),filePath);
		}else{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_isnot_exitst), 1000).show();
		}
		
	}
	
	private void exitSession(){
		SessionManager.getInstance().removeSessionLister(ip);
		
		finishWithAnimation();
	}
	
	private void setFocusRequest(FileTransRequest request){
		focusRequest = request;
	}
	
	class ChatMessageAdapter extends BaseAdapter{
        private ArrayList<ChatInfo> mChatInfos = new ArrayList<ChatInfo>();
        private Context mContext;
        
        public ChatMessageAdapter(Context cx){
        	mContext = cx;
        }
        
        public void addChatInfoArrayToQunene(ArrayList<ChatInfo> array){
        	if(array!=null){
        		mChatInfos.addAll(array);
        	}
        }
        
        public int size(){
        	return mChatInfos.size();
        }
        
        public void addChatInfoToQunene(ChatInfo chat){
        	mChatInfos.add(chat);
        }

		@Override
		public int getCount() {
			
			return mChatInfos.size();
		}

		@Override
		public Object getItem(int position) {
			
			return mChatInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}
		
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			
			return mChatInfos.get(position).comeOrto==SessionManager.COMEMSG?SessionManager.COMEMSG:SessionManager.TOMSG;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChatHolder holder = null;
			
//			XLog.logd("listview:"+mChatInfos.get(position).dateAndtime);
//			XLog.logd("listview:"+mChatInfos.get(position).comeOrto);
//			XLog.logd("listview:"+mChatInfos.get(position).content);
			if(convertView == null){
				holder = new ChatHolder();
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				int Resource = mChatInfos.get(position).comeOrto==SessionManager.COMEMSG?R.layout.chat_come_item:R.layout.chat_to_item;
				convertView = inflater.inflate(Resource, null);
				
				holder.tvTime = (TextView) convertView.findViewById(R.id.chat_time);
				holder.imageMember = (ImageView) convertView.findViewById(R.id.chat_img_icon);
				holder.groupSender= (TextView) convertView.findViewById(R.id.group_send_name);
				holder.tvContent = (TextView) convertView.findViewById(R.id.chat_tv_content);
				holder.fileTransfer = (ImageView) convertView.findViewById(R.id.file_transfer_img);
				convertView.setTag(holder);
			}else{
				holder = (ChatHolder) convertView.getTag();
			}
			XLog.logd("getview: ip>>>>>>"+mChatInfos.get(position).ip);
			int mode = mChatInfos.get(position).comeOrto;
			MemberManager memMgr = MemberManager.getInstance();
			Drawable icon = mode==SessionManager.TOMSG?memMgr.getLocalUserIcon():memMgr.getMemberInfiByIp(mChatInfos.get(position).ip).iCon;
			holder.imageMember.setImageDrawable(icon==null?mContext.getResources().getDrawable(R.drawable.default_icon):icon);
			if(type.equals("group")){
				holder.groupSender.setVisibility(View.VISIBLE);
				holder.groupSender.setText(mChatInfos.get(position).senderName);
			}else {
				holder.groupSender.setVisibility(View.GONE);
			}
			holder.tvTime.setText(mChatInfos.get(position).dateAndtime);
			if(mChatInfos.get(position) instanceof FileTransRequest){
				holder.fileTransfer.setVisibility(View.VISIBLE);
				FileTransRequest fileRequest = ((FileTransRequest)(mChatInfos.get(position)));
				fileRequest.parseContent();
				holder.tvContent.setText(fileRequest.fileName+"\n\n"+"size:"+Util.calculateUnit(fileRequest.fileLength));
			}else{
				holder.fileTransfer.setVisibility(View.GONE);
				holder.tvContent.setText(mChatInfos.get(position).content);
			}
			
			return convertView;
		}
		
		
		private class ChatHolder{
			private TextView tvTime;
			private ImageView imageMember;
			private TextView groupSender;
			private TextView tvContent;
			private ImageView fileTransfer;
		}
	}

	@Override
	protected boolean onBackKey() {
		exitSession();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		XLog.logd("chat>>onItemClick");
		ListView listView = (ListView) parent;
		ChatInfo cinfo = (ChatInfo) listView.getItemAtPosition(position);
		if(cinfo instanceof FileTransRequest && cinfo.comeOrto == SessionManager.COMEMSG && ((FileTransRequest)cinfo).isResponse == SessionManager.UNRESPONSE){
			if(popWindow.isShowing()){
				popWindow.dismiss();
			}else{
				setFocusRequest((FileTransRequest) cinfo);
				popWindow.showAtLocation(view, Gravity.TOP, 0,(int)view.getY()+view.getHeight());
			}
		}else if(cinfo instanceof FileTransRequest && cinfo.comeOrto == SessionManager.COMEMSG && ((FileTransRequest)cinfo).isResponse == SessionManager.RECEIVERED){
			setFocusRequest((FileTransRequest) cinfo);
			Intent dragImage = new  Intent(this,DragImageActivity.class);
			String path = Environment.getExternalStorageDirectory()+"/"+Util.LOCA_RECEIVER+"/"+focusRequest.fileName;
			dragImage.putExtra("filepath", path);
			startActivity(dragImage);
		}
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.settings_add_usericon, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.bycamera:
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mImagePath = Util.getFormatterTime()+".jpg";
				Uri uri = Uri.fromFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, mImagePath));
				camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(camera, 1000);
				break;
			case R.id.bygallery:
				Intent gallery = new Intent(Intent.ACTION_PICK);
				gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(gallery, 1001);
				break;
		}
		return super.onContextItemSelected(item);
	}


}
