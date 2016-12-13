package com.xiong.wlanconmmunition.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebChromeClient.CustomViewCallback;

public class DataBaseManager {

	private static DataBaseManager dbMgr;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private DataBaseManager(Context cx) {
		dbHelper = new DatabaseHelper(cx);
	}

	public static DataBaseManager getInstance(Context context) {
		if (dbMgr == null) {
			dbMgr = new DataBaseManager(context);
		}

		return dbMgr;
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}

	public Object query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Cursor cursor = db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		Object obj = null;
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				switch (cursor.getType(0)) {
				case Cursor.FIELD_TYPE_BLOB:
					obj = cursor.getBlob(0);
					break;
				case Cursor.FIELD_TYPE_STRING:
					obj = cursor.getString(0);
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					obj = cursor.getInt(0);
					break;
				default:
					break;
				}
			}
		}
		cursor.close();
		return obj;
	}

	public int getQueryConunt(String table, String[] columns) {
		Cursor cursor = db.query(table, columns, null, null, null, null, null);
		int length = cursor.getCount();
		cursor.close();
		return length;
	}

}
