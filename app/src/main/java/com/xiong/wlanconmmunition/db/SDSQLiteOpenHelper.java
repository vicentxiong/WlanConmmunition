package com.xiong.wlanconmmunition.db;



import java.io.File;
import java.io.IOException;

import com.xiong.wlanconmmunition.XLog;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.util.Log;

public abstract class SDSQLiteOpenHelper {
	

	private final Context mContext;
	private final String mName;
	private final String mPath;
	private final CursorFactory mFactory;
	private final int mNewVersion;

	private SQLiteDatabase mDatabase = null;
	private boolean mIsInitializing = false;

	
	public SDSQLiteOpenHelper(Context context, String path, String name, CursorFactory factory, int version) {
		if (version < 1)
			throw new IllegalArgumentException("Version must be >= 1, was " + version);

		mContext = context;
		mName = name;
		mPath = path;
		mFactory = factory;
		mNewVersion = version;
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		// If we have a read-only database open, someone could be using it
		// (though they shouldn't), which would cause a lock to be held on
		// the file, and our attempts to open the database read-write would
		// fail waiting for the file lock. To prevent that, we acquire the
		// lock on the read-only database, which shuts out other users.

		boolean success = false;
		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			if (mName == null) {
				db = SQLiteDatabase.create(null);
			} else {
				String path = getDatabasePath(mPath,mName).getPath();
				db = SQLiteDatabase.openOrCreateDatabase(path, mFactory);
			}

			int version = db.getVersion();
			if (version != mNewVersion) {
				db.beginTransaction();
				try {
					if (version == 0) {
						onCreate(db);
					} else {
						onUpgrade(db, version, mNewVersion);
					}
					db.setVersion(mNewVersion);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try {
						mDatabase.close();
					} catch (Exception e) {
					}
				}
				mDatabase = db;
			} else {
				if (db != null)
					db.close();
			}
		}
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mDatabase != null && mDatabase.isOpen()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		try {
			return getWritableDatabase();
		} catch (SQLiteException e) {
			if (mName == null)
				throw e; // Can't open a temp database read-only!
			XLog.loge( "Couldn't open " + mName + " for writing (will try read-only):");
		}

		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			String path =  getDatabasePath(mPath,mName).getPath();
			db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READWRITE);
			if (db.getVersion() != mNewVersion) {
				throw new SQLiteException("Can't upgrade read-only database from version " + db.getVersion() + " to " + mNewVersion + ": " + path);
			}

			onOpen(db);
			XLog.loge( "Opened " + mName + " in read-only mode");
			mDatabase = db;
			return mDatabase;
		} finally {
			mIsInitializing = false;
			if (db != null && db != mDatabase)
				db.close();
		}
	}

	/**
	 * Close any open database object.
	 */
	public synchronized void close() {
		if (mIsInitializing)
			throw new IllegalStateException("Closed during initialization");

		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	/**
	 * Called when the database is created for the first time. This is where the
	 * creation of tables and the initial population of the tables should
	 * happen.
	 * 
	 * @param db
	 *            The database.
	 */
	public abstract void onCreate(SQLiteDatabase db);

	/**
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 * 
	 * <p>
	 * The SQLite ALTER TABLE documentation can be found <a
	 * href="http://sqlite.org/lang_altertable.html">here</a>. If you add new
	 * columns you can use ALTER TABLE to insert them into a live table. If you
	 * rename or remove columns you can use ALTER TABLE to rename the old table,
	 * then create the new table and then populate the new table with the
	 * contents of the old table.
	 * 
	 * @param db
	 *            The database.
	 * @param oldVersion
	 *            The old database version.
	 * @param newVersion
	 *            The new database version.
	 */
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	/**
	 * Called when the database has been opened. Override method should check
	 * {@link SQLiteDatabase#isReadOnly} before updating the database.
	 * 
	 * @param db
	 *            The database.
	 */
	public void onOpen(SQLiteDatabase db) {}

	public File getDatabasePath(String dbpath, String dbName) {
		
		if (dbpath == null) {
			//default db dir
			dbpath = Environment.getExternalStorageDirectory().getPath();
		}
		File path = new File(dbpath);
		if (!path.exists()) {
			path.mkdirs();
		}
		
		File f = new File(path.getPath(), dbName);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
}