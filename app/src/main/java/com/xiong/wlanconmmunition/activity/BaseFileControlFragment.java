package com.xiong.wlanconmmunition.activity;

import java.util.ArrayList;

import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.XLog;
import com.xiong.wlanconmmunition.filemanager.TransferManager;
import com.xiong.wlanconmmunition.filemanager.TransferManager.ProcessUpdateLister;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class BaseFileControlFragment extends Fragment implements ProcessUpdateLister{
	protected ListView fileList;
	protected TextView emptyList;
	protected Activity attachActivity;
	protected ReceiverFileAdapter adapter;
	
	public static final int RECEIVERING = 0;
	public static final int RECEIVERED = 1;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attachActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TransferManager.getInstance().resgiterProcessLister(this);
	}
	
	private View initView(LayoutInflater inflater ,ViewGroup container,Bundle savedInstanceState){
		View mView = inflater.inflate(R.layout.receiverfile_fragment_layout, null);
		fileList = (ListView) mView.findViewById(R.id.receiverfile_listview);
		emptyList = (TextView) mView.findViewById(R.id.empty_file_list);
		return mView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new ReceiverFileAdapter(attachActivity);
		adapter.copyInfoArray(onGetResumeArray());
		fileList.setAdapter(adapter);
		fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ReceiverFileInfo info = (ReceiverFileInfo) parent.getItemAtPosition(position);
				Intent dragImage = new Intent(attachActivity,DragImageActivity.class);
				String path = Environment.getExternalStorageDirectory()+"/"+Util.LOCA_RECEIVER+"/"+info.fileName;
				dragImage.putExtra("filepath",path);
				startActivity(dragImage);
			}
		});
		
	}
	
	protected void updateView(){
		adapter.clearArray();
		adapter.copyInfoArray(onGetResumeArray());
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		TransferManager.getInstance().resgiterProcessLister(null);
	}
	
	protected void setFileEmpyt(boolean empty){
		XLog.logd("setFileEmpyt" + empty);
		fileList.setVisibility(empty?View.GONE:View.VISIBLE);
		emptyList.setVisibility(empty?View.VISIBLE:View.GONE);
	}
	
	@Override
	public void onUpdateFileProcess(String packetid, long process) {
		notifyFileUpdateProcess(packetid, process);
		
	}
	
	@Override
	public void onTransFileSuccessed() {
		notifyFileSuccessed();
	}
	
	@Override
	public void onTransFileFail() {
		notifyFileFail();
		
	}
	
	protected abstract void notifyFileUpdateProcess(String packetid, long process);
	
	protected abstract void notifyFileSuccessed();
	
	protected abstract void notifyFileFail();
	
	protected abstract ArrayList<ReceiverFileInfo> onGetResumeArray();
	
	class ReceiverFileInfo{
		public String fileName;
		public long fileLength;
		public long filetime;
		public int status;
	}
	
	class ReceiverFileAdapter extends BaseAdapter{
		private ArrayList<ReceiverFileInfo> array = new ArrayList<BaseFileControlFragment.ReceiverFileInfo>();
        private Context mContext;
        
		public ReceiverFileAdapter(Context c){
			mContext = c;
		}
		
		public void copyInfoArray(ArrayList<ReceiverFileInfo> infos){
			if(infos != null){
				array.addAll(infos);
			}
		}
		
		public void clearArray(){
			array.clear();
		}
		
		@Override
		public int getCount() {
			
			return array.size();
		}

		@Override
		public Object getItem(int position) {
			
			return array.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChatHolder holder = null;
			if(convertView == null){
				holder = new ChatHolder();
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				int Resource = R.layout.receiverfile_item;
				convertView = inflater.inflate(Resource, null);
				
				holder.receiver_filename = (TextView) convertView.findViewById(R.id.receiver_file_name);
				holder.receiver_filelength = (TextView) convertView.findViewById(R.id.receiver_file_length);
				holder.receiver_filetime= (TextView) convertView.findViewById(R.id.receiver_file_time);
				holder.receiver_fileprocess = (ProgressBar) convertView.findViewById(R.id.receiver_file_process);
				convertView.setTag(holder);
			}else{
				holder = (ChatHolder) convertView.getTag();
			}
			ReceiverFileInfo info = array.get(position);
			if(info.status==RECEIVERING){
				holder.receiver_filetime.setVisibility(View.GONE);
				holder.receiver_fileprocess.setVisibility(View.VISIBLE);
				holder.receiver_fileprocess.setMax((int) info.fileLength);
			}else if(info.status==RECEIVERED){
				holder.receiver_filetime.setVisibility(View.VISIBLE);
				holder.receiver_fileprocess.setVisibility(View.GONE);
				holder.receiver_filetime.setText(Util.getFormatterTime("yyyy-MM-dd HH:mm:ss", info.filetime));
			}

			holder.receiver_filename.setText(info.fileName);
			holder.receiver_filelength.setText(Util.calculateUnit(info.fileLength));
			
			return convertView;
		}
		
		private class ChatHolder{
			private TextView receiver_filename;
			private TextView receiver_filelength;
			private TextView receiver_filetime;
			private ProgressBar receiver_fileprocess;
		}
		
	}
}
