package com.xctx.baidunavi.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NaviHistoryDbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "navi_db";

	private static final String NAVI_TABLE_NAME = "navi";
	private static final String NAVI_COL_ID = "_id";
	private static final String NAVI_COL_KEY = "key"; // 导航关键字
	private static final String NAVI_COL_CITY = "city"; // 导航城市

	private static final String[] ROUTE_COL_PROJECTION = new String[] {
			NAVI_COL_ID, NAVI_COL_KEY, NAVI_COL_CITY, };

	public NaviHistoryDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createRouteTableSql = "CREATE TABLE " + NAVI_TABLE_NAME + " ("
				+ NAVI_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ NAVI_COL_KEY + " TEXT," + NAVI_COL_CITY + " TEXT" + ");";
		db.execSQL(createRouteTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + NAVI_TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	// Add new NaviHistory
	public int addNaviHistory(NaviHistory naviHistory) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAVI_COL_KEY, naviHistory.getKey());
		values.put(NAVI_COL_CITY, naviHistory.getCity());

		// Insert to database
		long rowId = db.insert(NAVI_TABLE_NAME, null, values);

		// Close the database
		db.close();

		return (int) rowId;
	}

	// Get NaviHistory By ID
	public NaviHistory getNaviHistoryById(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(NAVI_TABLE_NAME, ROUTE_COL_PROJECTION,
				NAVI_COL_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		NaviHistory naviHistory = new NaviHistory(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2));

		return naviHistory;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public NaviHistory getNaviHistoryByKey(String key) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(NAVI_TABLE_NAME, ROUTE_COL_PROJECTION,
				NAVI_COL_KEY + "=?", new String[] { key }, null, null, null,
				null);

		if (cursor != null)
			cursor.moveToFirst();

		NaviHistory naviHistory = new NaviHistory(cursor.getInt(0),
				cursor.getString(1), cursor.getString(2));

		return naviHistory;
	}

	/**
	 * 获取所有的导航历史记录
	 * 
	 * @return
	 */
	public ArrayList<NaviHistory> getAllNaviHistory() {
		ArrayList<NaviHistory> naviHistoryList = new ArrayList<NaviHistory>();
		String selectQuery = "SELECT * FROM " + NAVI_TABLE_NAME + " ORDER BY "
				+ NAVI_COL_ID + " DESC";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				NaviHistory naviHistory = new NaviHistory(cursor.getInt(0),
						cursor.getString(1), cursor.getString(2));
				naviHistoryList.add(naviHistory);
			} while (cursor.moveToNext());
		}

		// return list
		return naviHistoryList;
	}

	public Cursor getAllDriveVideoCursor() {
		String selectQuery = "SELECT * FROM " + NAVI_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	/**
	 * 获取最旧导航历史ID
	 * 
	 * @return
	 */
	public int getOldestNaviHistoryId() {
		String sqlLine = "SELECT * FROM " + NAVI_TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex(NAVI_COL_ID));
			return id;
		} else {
			return -1;
		}
	}

	public String getNaviKeyById(int id) {
		String sqlLine = "SELECT * FROM " + NAVI_TABLE_NAME + " WHERE "
				+ NAVI_COL_ID + "=?";
		String selection[] = new String[] { String.valueOf(id) };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String naviKey = cursor.getString(cursor
					.getColumnIndex(NAVI_COL_KEY));
			return naviKey;
		} else {
			return "";
		}
	}

	public int getNaviIdByKey(String key) {
		String sqlLine = "SELECT * FROM " + NAVI_TABLE_NAME + " WHERE "
				+ NAVI_COL_KEY + "=?";
		String selection[] = new String[] { key };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int naviKey = cursor.getInt(cursor.getColumnIndex(NAVI_COL_ID));
			return naviKey;
		} else {
			return -1;
		}
	}

	public int updateDriveVideo(NaviHistory naviHistory) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAVI_COL_KEY, naviHistory.getKey());

		return db.update(NAVI_TABLE_NAME, values, NAVI_COL_ID + "=?",
				new String[] { String.valueOf(naviHistory.getId()) });
	}

	public void deleteNaviHistoryById(int driveVideoId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(NAVI_TABLE_NAME, NAVI_COL_ID + "=?",
				new String[] { String.valueOf(driveVideoId) });
		db.close();
	}

	public void deleteNaviHistoryByKey(String key) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(NAVI_TABLE_NAME, NAVI_COL_KEY + "=?", new String[] { key });
		db.close();
	}

	public void deleteAllNaviHistory() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(NAVI_TABLE_NAME, null, null);
		db.close();
	}

}
