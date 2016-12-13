package com.xiong.wlanconmmunition.activity;

import java.io.File;

import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.SessionManager;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.XLog;
import com.xiong.wlanconmmunition.filemanager.FileController;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FileOptionActivity extends ActionBarBaseActivity implements OnItemClickListener ,OnClickListener {
    private LinearLayout group_path;
    private ListView fileListView;
    private FileOptionAdapter adapter;
    private FileController controller;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.fileoption_layout);
    	controller = new FileController();
    	controller.init();
    	init();
    }
    
    private void init(){
    	fileListView  = (ListView) findViewById(R.id.file_list);
    	fileListView.setOnItemClickListener(this);
    	adapter = new FileOptionAdapter(this);
    	adapter.setFileList(controller.getCurrentDirList());
    	fileListView.setAdapter(adapter);
    }
	
	@Override
	protected View createActionBarView(LayoutInflater inflater) {
		
		return inflater.inflate(R.layout.fileoption_actionbarlayout, null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateActionBarPath();
	}
	
	private void updateActionBarPath(){
		group_path.removeAllViews();
		int size = controller.size();
		for (int i = 0; i < size; i++) {
			File file = controller.getFileByIndex(i);
			String filename = file.getName();
			XLog.logd("first file: " + file.getAbsolutePath());
			TextView tv = new TextView(this);
			tv.setBackgroundResource(R.drawable.custom_tab);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setOnClickListener(this);
			tv.setText(filename);
			tv.setTag(i);
			group_path.addView(tv);
		}
	}

	@Override
	protected void initCustomView() {
		group_path = (LinearLayout) getCustomView().findViewById(R.id.opt_path_llayout);

	}

	@Override
	protected boolean onBackKey() {
		boolean mReturn = controller.returnParentDir();
		if(mReturn){
			adapter.setFileList(controller.getCurrentDirList());
			adapter.notifyDataSetChanged();
			updateActionBarPath();
		}else{
			finishWithAnimation();
		}
		
		return mReturn;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ListView listview = (ListView) parent;
		File file = (File) listview.getItemAtPosition(position);
		if(file.isDirectory()){
			controller.enterSubDir(file);
			adapter.setFileList(controller.getCurrentDirList());
			adapter.notifyDataSetChanged();
			updateActionBarPath();
		}else{
			Intent result = new Intent();
			result.setData(Uri.fromFile(file));
			setResult(RESULT_OK, result);
			finishWithAnimation();
		}
	}
	
	class FileOptionAdapter extends BaseAdapter{
		private File[] files;
		private Context mcontext;
		
		public FileOptionAdapter(Context cx){
			mcontext = cx;
		}
		
		public void setFileList(File[] array){
			files = array;
		}

		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public Object getItem(int position) {
			return files[position];
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
				LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				int Resource = R.layout.file_list_layout;
				convertView = inflater.inflate(Resource, null);
				
				holder.image = (ImageView) convertView.findViewById(R.id.file_image);
				holder.filename = (TextView) convertView.findViewById(R.id.file_name);
				holder.filelength = (TextView) convertView.findViewById(R.id.file_length);
				convertView.setTag(holder);
			}else{
				holder = (ChatHolder) convertView.getTag();
			}
			
			holder.image.setImageResource(files[position].isFile()?R.drawable.fm_unknown:R.drawable.fm_folder);
			holder.filename.setText(files[position].getName());
			if(files[position].isFile()){
				holder.filelength.setText(Util.calculateUnit(files[position].length()));
			}else{
				holder.filelength.setText("");
			}
			
			return convertView;
		}
		
		private class ChatHolder{
			private ImageView image;
			private TextView filename;
			private TextView filelength;
		}
		
	}

	@Override
	public void onClick(View v) {
		XLog.logd("file return tag: " + v.getTag());
		int position = (Integer) v.getTag();
		controller.returnDir(position);
		adapter.setFileList(controller.getCurrentDirList());
		adapter.notifyDataSetChanged();
		updateActionBarPath();
	}

}
