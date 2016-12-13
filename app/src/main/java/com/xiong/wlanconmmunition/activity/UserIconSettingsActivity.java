package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.MemberManager;
import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.Util;
import com.xiong.wlanconmmunition.XLog;
import com.xiong.wlanconmmunition.db.DataBaseManager;
import com.xiong.wlanconmmunition.db.UserSelfTable;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class UserIconSettingsActivity extends BaseSettingsActivity implements OnClickListener {
	private ImageView newUserIcon;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private static final int PHOTO_ZOOM = 2;
    private Bitmap curBitmap;
    private boolean newIcon = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usericon_settinglayout);
		
		initUI();
		
	}
	
	private void initUI(){
		newUserIcon = (ImageView) findViewById(R.id.new_usericon);
		newUserIcon.setOnCreateContextMenuListener(this);
		newUserIcon.setOnClickListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.settings_add_usericon, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bycamera:
			XLog.logd("bycamera");
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri uri = Uri.fromFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.TEMP_ICON_NAME));
			camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(camera, CAMERA_REQUEST);
			break;
		case R.id.bygallery:
			XLog.logd("bygallery");
			Intent gallery = new Intent(Intent.ACTION_PICK);
			gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(gallery, GALLERY_REQUEST);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void startPhotoZoom(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");  
	    //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
	    intent.putExtra("crop", "true");  
	    // aspectX aspectY 是宽高的比例  
	    intent.putExtra("aspectX", 1);  
	    intent.putExtra("aspectY", 1);  
	    // outputX outputY 是裁剪图片宽高  
	    intent.putExtra("outputX", 60);  
	    intent.putExtra("outputY", 60);  
	    intent.putExtra("return-data", false);  
	    intent.putExtra("noFaceDetection", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.CROP_ICON)));
	    startActivityForResult(intent, PHOTO_ZOOM);
	}
	
	private void setPicToImageView(){
		Bitmap bitmap = BitmapFactory.decodeFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.CROP_ICON).getPath());
		
		newIcon = true;
		newUserIcon.setImageBitmap(bitmap);
	}

	@Override
	protected void onSetingOk() {
		Util.cpFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.CROP_ICON).getPath(),
				Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.LOCAL_ICON_NAME).getPath());
		
		Drawable drawable = MemberManager.getInstance().getMemberIcon(Util.LOCAL_ICON_NAME);
		MemberManager.getInstance().setLocalUserIcon(drawable);
		
		Util.deleteFile(Util.LOCA_ICON_DIR, Util.CROP_ICON);
	    int res = -1;
		DataBaseManager dbMgr = DataBaseManager.getInstance(getApplicationContext());
		dbMgr.open();
		ContentValues values = new ContentValues();
		values.put(UserSelfTable.COL_SELFICON, 1);
		if(dbMgr.getQueryConunt(UserSelfTable.TABLE_NAME, new String[]{UserSelfTable.COL_SELFICON})>0){
			res = dbMgr.update(UserSelfTable.TABLE_NAME, values, null, null);
		}else{
			res = (int) dbMgr.insert(UserSelfTable.TABLE_NAME, values);
		}
		dbMgr.close();
	}

	@Override
	public void onClick(View v) {
		v.showContextMenu();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case CAMERA_REQUEST:
				XLog.logd("by camera");
				startPhotoZoom(Uri.fromFile(Util.openOrCreateFile(Util.LOCA_ICON_DIR, Util.TEMP_ICON_NAME)));
				break;
			case GALLERY_REQUEST:
				startPhotoZoom(data.getData());
				break;
			case PHOTO_ZOOM:
			    setPicToImageView();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	protected void onSetingCancel() {
		Util.deleteFile(Util.LOCA_ICON_DIR, Util.CROP_ICON);
		
	}

	@Override
	protected boolean settingEmpty() {
		return !newIcon;
	}

}
