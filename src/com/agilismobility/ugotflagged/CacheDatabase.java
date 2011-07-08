package com.agilismobility.ugotflagged;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class CacheDatabase extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "cache.db";
	private static HashMap<String, String> sCacheProjectionMap;

	static {
		sCacheProjectionMap = new HashMap<String, String>();
		sCacheProjectionMap.put(Cache._ID, Cache._ID);
		sCacheProjectionMap.put(Cache.COLUMN_NAME_URL, Cache.COLUMN_NAME_URL);
		sCacheProjectionMap.put(Cache.COLUMN_NAME_LOGIN_USER_NAME, Cache.COLUMN_NAME_LOGIN_USER_NAME);
		sCacheProjectionMap.put(Cache.COLUMN_NAME_XML, Cache.COLUMN_NAME_XML);
		sCacheProjectionMap.put(Cache.COLUMN_NAME_CREATE_DATE, Cache.COLUMN_NAME_CREATE_DATE);
		sCacheProjectionMap.put(Cache.COLUMN_NAME_MODIFICATION_DATE, Cache.COLUMN_NAME_MODIFICATION_DATE);
	}

	public CacheDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Cache.TABLE_NAME + " (" + Cache._ID + " INTEGER PRIMARY KEY," + Cache.COLUMN_NAME_URL + " TEXT,"
				+ Cache.COLUMN_NAME_LOGIN_USER_NAME + " TEXT," + Cache.COLUMN_NAME_XML + " TEXT," + Cache.COLUMN_NAME_CREATE_DATE + " INTEGER,"
				+ Cache.COLUMN_NAME_MODIFICATION_DATE + " INTEGER" + ", CONSTRAINT unqkeys UNIQUE (" + Cache.COLUMN_NAME_LOGIN_USER_NAME + ","
				+ Cache.COLUMN_NAME_URL + ") ON CONFLICT REPLACE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Cache.TABLE_NAME);
		onCreate(db);
	}

	public void setXML(String url, String xml, String userName) {
		SQLiteDatabase db = getWritableDatabase();
		Long now = Long.valueOf(System.currentTimeMillis());
		ContentValues values = new ContentValues();
		values.put(Cache.COLUMN_NAME_LOGIN_USER_NAME, userName);
		values.put(Cache.COLUMN_NAME_URL, url);
		values.put(Cache.COLUMN_NAME_XML, xml);
		values.put(Cache.COLUMN_NAME_CREATE_DATE, now);
		values.put(Cache.COLUMN_NAME_MODIFICATION_DATE, now);
		long rowId = db.insert(Cache.TABLE_NAME, null, values);
		if (rowId == -1) {
			Log.d("", "error");
		}
	}

	public String getXML(String url, String userName) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Cache.TABLE_NAME);
		qb.setProjectionMap(sCacheProjectionMap);
		SQLiteDatabase db = getReadableDatabase();
		Cursor c;
		if (userName != null) {
			c = qb.query(db, new String[] { Cache.COLUMN_NAME_XML }, Cache.COLUMN_NAME_LOGIN_USER_NAME + " = ? AND " + Cache.COLUMN_NAME_URL
					+ " = ?", new String[] { userName, url }, null, null, null);
		} else {
			c = qb.query(db, new String[] { Cache.COLUMN_NAME_XML }, Cache.COLUMN_NAME_URL + " = ?", new String[] { url }, null, null, null);
		}
		c.moveToFirst();
		if (c.getCount() > 0) {
			return c.getString(0);
		} else {
			return null;
		}

	}

	public static final class Cache implements BaseColumns {
		private Cache() {
		}

		public static final String TABLE_NAME = "Cache";
		public static final String COLUMN_NAME_URL = "url";
		public static final String COLUMN_NAME_LOGIN_USER_NAME = "user_name";
		public static final String COLUMN_NAME_XML = "xml";
		public static final String COLUMN_NAME_CREATE_DATE = "create_date";
		public static final String COLUMN_NAME_MODIFICATION_DATE = "update_date";
	}

}
