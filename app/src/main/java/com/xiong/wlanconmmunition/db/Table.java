package com.xiong.wlanconmmunition.db;

import android.database.sqlite.SQLiteDatabase;

public interface Table {
	
	public void onCreate(SQLiteDatabase db);
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion);

}
