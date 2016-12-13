package com.xiong.wlanconmmunition.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

public class DatabaseHelper extends BaseSqilteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
	private static final String DB_PATH = ROOT_PATH + "/wlancommunication";
	private static final String DB_NAME = "wlanCommuniction.db";
	private static List<Table> tables = new ArrayList<Table>();
	
	static {
		tables.add(new UserSelfTable());
		tables.add(new OtherUserTable());
	
	}
	
	public DatabaseHelper(Context context){
		this(context, DB_PATH,DB_NAME, null, DB_VERSION);
	}
	

	public DatabaseHelper(Context context, String path ,String name, CursorFactory factory,
			int version) {
		super(context, path,name, factory, version);
	}

	@Override
	protected List<Table> getDatabaseTables() {
		return tables;
	}

}
