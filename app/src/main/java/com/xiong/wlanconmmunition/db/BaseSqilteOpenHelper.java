package com.xiong.wlanconmmunition.db;

import java.util.List;

import com.xiong.wlanconmmunition.XLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class BaseSqilteOpenHelper extends SDSQLiteOpenHelper {

	public BaseSqilteOpenHelper(Context context, String path,String name,
			CursorFactory factory, int version) {
		super(context, path, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Table table : getDatabaseTables()) {
			table.onCreate(db);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		XLog.logd("oldversion " + oldversion +"\nnewversion " + newversion);
		for (Table table : getDatabaseTables()) {
			table.onUpgrade(db, oldversion, newversion);
		}
	}
	
	protected abstract List<Table> getDatabaseTables();

}
