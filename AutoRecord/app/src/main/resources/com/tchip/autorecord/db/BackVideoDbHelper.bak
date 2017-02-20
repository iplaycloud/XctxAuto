package com.tchip.autorecord.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BackVideoDbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "video_back";

	private static final String VIDEO_TABLE_NAME = "video";
	private static final String VIDEO_COL_ID = "_id";
	private static final String VIDEO_COL_NAME = "name"; // 视频文件名称，如：2015-07-01_105536.mp4
	private static final String VIDEO_COL_LOCK = "lock"; // 是否加锁：0-否 1-是
	private static final String VIDEO_COL_RESOLUTION = "resolution"; // 视频分辨率：720/1080
	private static final String VIDEO_COL_WHICH = "which"; // 0-前置 1-后置

	private static final String[] VIDEO_COL_PROJECTION = new String[] {
			VIDEO_COL_ID, VIDEO_COL_NAME, VIDEO_COL_LOCK, VIDEO_COL_RESOLUTION, };

	public BackVideoDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createRouteTableSql = "CREATE TABLE " + VIDEO_TABLE_NAME + " ("
				+ VIDEO_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ VIDEO_COL_NAME + " TEXT," + VIDEO_COL_LOCK + " INTEGER,"
				+ VIDEO_COL_RESOLUTION + " INTEGER," + VIDEO_COL_WHICH
				+ " INTEGER" + ");";
		db.execSQL(createRouteTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	// Add new DriveVideo
	public int addDriveVideo(DriveVideo driveVideo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(VIDEO_COL_NAME, driveVideo.getName());
		values.put(VIDEO_COL_LOCK, driveVideo.getLock());
		values.put(VIDEO_COL_RESOLUTION, driveVideo.getResolution());
		values.put(VIDEO_COL_WHICH, driveVideo.getWhich());

		// Insert to database
		long rowId = db.insert(VIDEO_TABLE_NAME, null, values);

		// Close the database
		db.close();

		return (int) rowId;
	}

	// Get DriveVideo By ID
	public DriveVideo getRouteDistanceById(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(VIDEO_TABLE_NAME, VIDEO_COL_PROJECTION,
				VIDEO_COL_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		DriveVideo driveVideo = new DriveVideo(cursor.getInt(0),
				cursor.getString(1), cursor.getInt(2), cursor.getInt(3),
				cursor.getInt(4));

		db.close();
		cursor.close();

		return driveVideo;
	}

	// Get DriveVideo By Name
	public DriveVideo getDriveVideoByName(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(VIDEO_TABLE_NAME, VIDEO_COL_PROJECTION,
				VIDEO_COL_NAME + "=?", new String[] { name }, null, null, null,
				null);

		if (cursor != null)
			cursor.moveToFirst();

		DriveVideo driveVideo = new DriveVideo(cursor.getInt(0),
				cursor.getString(1), cursor.getInt(2), cursor.getInt(3),
				cursor.getInt(4));

		db.close();
		cursor.close();

		return driveVideo;
	}

	public boolean isVideoExist(String name) {
		boolean isVideoExist = false;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(VIDEO_TABLE_NAME, VIDEO_COL_PROJECTION,
				VIDEO_COL_NAME + "=?", new String[] { name }, null, null, null,
				null);

		if (cursor.getCount() > 0) {
			isVideoExist = true;
		} else {
			isVideoExist = false;
		}
		db.close();
		cursor.close();

		return isVideoExist;
	}

	/**
	 * 根据视频名查询是否加锁
	 * 
	 * @param name
	 * @return
	 */
	public int getLockStateByVideoName(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(VIDEO_TABLE_NAME, VIDEO_COL_PROJECTION,
				VIDEO_COL_NAME + "=?", new String[] { name }, null, null, null,
				null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int videoLock = cursor
					.getInt(cursor.getColumnIndex(VIDEO_COL_LOCK));

			db.close();
			cursor.close();

			return videoLock;
		} else {
			db.close();
			cursor.close();

			return 0;
		}
	}

	/**
	 * 获取所有的视频信息
	 * 
	 * @return
	 */
	public List<DriveVideo> getAllDriveVideo() {
		List<DriveVideo> driveVideoList = new ArrayList<DriveVideo>();
		String selectQuery = "SELECT * FROM " + VIDEO_TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				DriveVideo driveVideo = new DriveVideo(cursor.getInt(0),
						cursor.getString(1), cursor.getInt(2),
						cursor.getInt(3), cursor.getInt(4));
				driveVideoList.add(driveVideo);
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();

		// return list
		return driveVideoList;
	}

	public Cursor getAllDriveVideoCursor() {
		String selectQuery = "SELECT * FROM " + VIDEO_TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	/**
	 * 获取加锁视频Cursor
	 * 
	 * @return
	 */
	public Cursor getLockVideoCursor() {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE "
				+ VIDEO_COL_LOCK + "=?";
		String selection[] = new String[] { "1" };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		return cursor;
	}

	/**
	 * 获取最旧且未加锁视频ID(Back)
	 * 
	 * @return
	 */
	public int getOldestUnlockVideoId() {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE "
				+ VIDEO_COL_LOCK + "=?";
		String selection[] = new String[] { "0" };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.moveToFirst()) {
			int id = -1;
			String videoName = "";
			do {
				id = cursor.getInt(cursor.getColumnIndex(VIDEO_COL_ID));
				videoName = cursor.getString(cursor
						.getColumnIndex(VIDEO_COL_NAME));
			} while (!videoName.endsWith("_1.mp4") && cursor.moveToNext());
			db.close();
			cursor.close();
			return id;
		} else {
			db.close();
			cursor.close();
			return -1;
		}
	}

	/**
	 * 获取最旧加锁视频ID
	 * 
	 * @return
	 */
	public int getOldestLockVideoId() {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE "
				+ VIDEO_COL_LOCK + "=?";
		String selection[] = new String[] { "1" };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.moveToFirst()) {
			int id = -1;
			id = cursor.getInt(cursor.getColumnIndex(VIDEO_COL_ID));
			db.close();
			cursor.close();
			return id;
		} else {
			db.close();
			cursor.close();
			return -1;
		}
	}

	/**
	 * 获取最旧视频(包括加锁)ID
	 * 
	 * @return
	 */
	public int getOldestVideoId() {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex(VIDEO_COL_ID));

			db.close();
			cursor.close();

			return id;
		} else {
			db.close();
			cursor.close();

			return -1;
		}
	}

	public String getVideNameById(int id) {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE "
				+ VIDEO_COL_ID + "=?";
		String selection[] = new String[] { String.valueOf(id) };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String videoName = cursor.getString(cursor
					.getColumnIndex(VIDEO_COL_NAME));

			db.close();
			cursor.close();

			return videoName;
		} else {
			db.close();
			cursor.close();

			return "";
		}
	}

	/** 0-前置 ；1-后置 */
	public String getVideoWhichById(int id) {
		String sqlLine = "SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE "
				+ VIDEO_COL_ID + "=?";
		String selection[] = new String[] { String.valueOf(id) };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlLine, selection);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String videoWhich = cursor.getString(cursor
					.getColumnIndex(VIDEO_COL_WHICH));

			db.close();
			cursor.close();

			return videoWhich;
		} else {
			db.close();
			cursor.close();
			return "0";
		}
	}

	public int updateDriveVideo(DriveVideo driveVideo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(VIDEO_COL_NAME, driveVideo.getName());
		values.put(VIDEO_COL_LOCK, driveVideo.getLock());
		values.put(VIDEO_COL_RESOLUTION, driveVideo.getResolution());
		values.put(VIDEO_COL_WHICH, driveVideo.getWhich());

		return db.update(VIDEO_TABLE_NAME, values, VIDEO_COL_ID + "=?",
				new String[] { String.valueOf(driveVideo.getId()) });
	}

	public void deleteDriveVideoById(int driveVideoId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(VIDEO_TABLE_NAME, VIDEO_COL_ID + "=?",
				new String[] { String.valueOf(driveVideoId) });
		db.close();
	}

	public void deleteDriveVideoByName(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(VIDEO_TABLE_NAME, VIDEO_COL_NAME + "=?",
				new String[] { name });
		db.close();
	}

	public void deleteAllDriveVideo() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(VIDEO_TABLE_NAME, null, null);
		db.close();
	}

}
