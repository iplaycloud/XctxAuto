package com.tchip.autosetting.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ProviderUtil {

	public ProviderUtil() {
	}

	public static final class Name { // Ctrl+Shift+X
		/** ACC状态 */
		public static final String ACC_STATE = "acc_state";

		/** 后录录像状态:0-未录像，1-录像 */
		public static final String REC_BACK_STATE = "rec_back_state";

		/** 是否开启后录 */
		public static final String REC_BACK_ENABLE = "rec_back_enable";

		/** ACC下电是否可亮屏 */
		public static final String DEBUG_ACCOFF_WAKE = "debug_accoff_wake";

		/** 前录录像状态:0-未录像，1-录像 */
		public static final String REC_FRONT_STATE = "rec_front_state";

		/** 前录分辨率:720,1080 */
		public static final String REC_FRONT_SIZE = "rec_front_size";

		/** 前录分段:1,3 */
		public static final String REC_FRONT_TIME = "rec_front_time";

		/** 前录比特率 */
		public static final String REC_FRONT_1080_BITRATE = "rec_front_1080_bitrate";

		/** 后录比特率 */
		public static final String REC_BACK_BITRATE = "rec_back_bitrate";

		/** 天气定位城市:深圳 */
		public static final String WEATHER_LOC_CITY = "weather_loc_city";

		/** 天气定位时间 */
		public static final String WEATHER_LOC_TIME = "weather_loc_time";

		/** 天气信息:晴 */
		public static final String WEATHER_INFO = "weather_info";

		/** 当日最低气温:15 */
		public static final String WEATHER_TEMP_LOW = "weather_temp_low";

		/** 当日最高气温:25 */
		public static final String WEATHER_TEMP_HIGH = "weather_temp_high";

		/** 音乐播放状态:0,1 */
		public static final String MUSIC_PLAY_STATE = "music_play_state";

		/** 正在播放歌曲名 */
		public static final String MUSIC_PLAY_NAME = "music_play_name";

		/** 蓝牙播放音乐状态:0,1 */
		public static final String BT_MUSIC_STATE = "bt_music_state";

		/** 蓝牙开关:0,1 */
		public static final String BT_ENABLE = "bt_enable";

		/** 蓝牙连接状态：0,1 */
		public static final String BT_CONNECT_STATE = "bt_connect_state";

		/** FM发射开关:0,1 */
		public static final String FM_TRANSMIT_STATE = "fm_transmit_state";

		/** FM发射频率: 8750-10800 */
		public static final String FM_TRANSMIT_FREQ = "fm_transmit_freq";

		/** 电子狗电源状态:0,1 */
		public static final String EDOG_POWER_STATE = "edog_power_state";

		/** 自动亮度开关:0,1[根据时间调整亮度开关] */
		public static final String SET_AUTO_LIGHT_STATE = "set_auto_light_state";

		/** 碰撞侦测开关:0,1 */
		public static final String SET_DETECT_CRASH_STATE = "set_detect_crash_state";

		/** 碰撞侦测灵敏度:0-低,1-中,2-高 */
		public static final String SET_DETECT_CRASH_LEVEL = "set_detect_crash_level";

		/** 停车守卫开关:0,1 */
		public static final String SET_PARK_MONITOR_STATE = "set_park_monitor_state";

		/** 数据流量开关 */
		public static final String SET_MOBILE_DATA = "set_mobile_data";

	}

	public static String getValue(Context context, String name,
			String defaultValue) {
		String dbValue = defaultValue;
		try {
			Uri uri = Uri
					.parse("content://com.tchip.provider.AutoProvider/state/name/"
							+ name);
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(uri, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				dbValue = cursor.getString(cursor.getColumnIndex("value"));
				cursor.close();
			} else {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("ProviderUtil.get " + name + " Value Exception:"
					+ e.toString());
		}
		MyLog.v("ProviderUtil.getValue.Name:" + name + ",value:" + dbValue);
		return dbValue;
	}

	public static void setValue(Context context, String name, String value) {
		MyLog.v("ProviderUtil.setValue.Name:" + name + ",value:" + value);
		try {
			Uri uriUpdate = Uri
					.parse("content://com.tchip.provider.AutoProvider/state/name/"
							+ name);
			ContentResolver contentResolverUpdate = context
					.getContentResolver();
			ContentValues valuesUpdate = new ContentValues();
			valuesUpdate.put("value", value);
			int count = contentResolverUpdate.update(uriUpdate, valuesUpdate,
					"name=?", new String[] { name }); // Update
			if (count == 0) {
				Uri uriInsert = Uri
						.parse("content://com.tchip.provider.AutoProvider/state/");
				ContentResolver contentResolverInsert = context
						.getContentResolver();
				ContentValues valuesInsert = new ContentValues();
				valuesInsert.put("name", name);
				valuesInsert.put("value", value);
				contentResolverInsert.insert(uriInsert, valuesInsert); // Insert
			}
		} catch (Exception e) {
			MyLog.e("ProviderUtil.set " + name + " Value Exception:"
					+ e.toString());
		}
	}

}
