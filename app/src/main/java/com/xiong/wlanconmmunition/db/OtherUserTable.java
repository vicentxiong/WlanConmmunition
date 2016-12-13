package com.xiong.wlanconmmunition.db;

import android.database.sqlite.SQLiteDatabase;

public class OtherUserTable implements Table {
	public static final String TABLE_NAME =  "userother";
	public static final String COL_IP  = "ip";
	public static final String COL_SELFICON  = "selficon";
	private final static String DROP_TABLE = "drop table if exists userother";
	private final static String CREATE_TABLE = "create table userother (ip TEXT,selficon BLOB)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		if(newversion > oldversion){
			db.execSQL(DROP_TABLE);
			db.execSQL(CREATE_TABLE);
		}
	}

}
