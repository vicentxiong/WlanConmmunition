package com.xiong.wlanconmmunition.db;

import android.database.sqlite.SQLiteDatabase;

public class UserSelfTable implements Table {
	public static final String TABLE_NAME =  "userself";
	public static final String COL_USERNAME  = "username";
	public static final String COL_SELFICON  = "selficon";
	private final static String DROP_TABLE = "drop table if exists userself";
	private final static String CREATE_TABLE = "create table userself (username TEXT,selficon INTEGER)";

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
