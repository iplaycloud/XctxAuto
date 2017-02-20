package com.tchip.carlauncher.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DriveVideoProvider extends ContentProvider {

	private DriveVideoDbHelper driveVideoDbHelper;

	private static final UriMatcher VIDEO_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final int VIDEOS = 1;
	private static final int VIDEO = 2;
	private static final int VIDEO_NAME = 3;

	static {
		VIDEO_MATCHER.addURI("com.tchip.carlauncher.model.DriveVideoProvider",
				"videos", VIDEOS);
		VIDEO_MATCHER.addURI("com.tchip.carlauncher.model.DriveVideoProvider",
				"video/#", VIDEO);
		VIDEO_MATCHER.addURI("com.tchip.carlauncher.model.DriveVideoProvider",
				"video/name/*", VIDEO_NAME);
	}

	@Override
	public boolean onCreate() {
		this.driveVideoDbHelper = new DriveVideoDbHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = driveVideoDbHelper.getReadableDatabase();
		switch (VIDEO_MATCHER.match(uri)) {
		case VIDEOS:
			return db.query("videos", projection, selection, selectionArgs,
					null, null, sortOrder);

		case VIDEO:
			long id = ContentUris.parseId(uri);

			String where = "name=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}
			return db.query("video", projection, where, selectionArgs, null,
					null, sortOrder);

		case VIDEO_NAME:
			String name = uri.getPathSegments().get(2);
			String nameWhere = "name='" + name + "'";
			return db.query("video", projection, nameWhere, selectionArgs,
					null, null, sortOrder);

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	/**
	 * 该方法用于返回当前Url所代表数据的MIME类型。
	 * 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头
	 * 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头
	 */
	@Override
	public String getType(Uri uri) {
		switch (VIDEO_MATCHER.match(uri)) {
		case VIDEOS:
			return "vnd.android.cursor.dir/video";

		case VIDEO:
			return "vnd.android.cursor.item/video";

		case VIDEO_NAME:
			return "vnd.android.cursor.item/video/name";

		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
