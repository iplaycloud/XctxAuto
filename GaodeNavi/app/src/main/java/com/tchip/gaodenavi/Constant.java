package com.tchip.gaodenavi;

import android.os.Environment;

public interface Constant {
	/**
	 * Debug：打印Log
	 */
	public static final boolean isDebug = true;

	/**
	 * 日志Tag
	 */
	public static final String TAG = "ZMS";

	/**
	 * 高德地图
	 */
	public static final class GaodeMap {
		public static final String API_KEY = "c72e8f176cd896fe89d84da10df94032";
	}

	/**
	 * 讯飞语音
	 */
	public static final class XunFei {
		public static final String APP_ID = "55f630a9";
	}

	/**
	 * 行驶轨迹
	 */
	public static final class RouteTrack {
		/**
		 * 存储位置
		 */
		public static final String PATH = Environment
				.getExternalStorageDirectory().getPath() + "/Route/";

		/**
		 * 扩展名
		 */
		public static final String EXTENSION = ".art"; // Auto Route Track
	}

	/**
	 * 路径
	 */
	public static final class Path {
		/**
		 * SDcard Path
		 */
		public static final String SD_CARD = Environment
				.getExternalStorageDirectory().getPath();

		public static final String SDCARD_1 = "/storage/sdcard1";
		public static final String SDCARD_2 = "/storage/sdcard2";

		/**
		 * 字体目录
		 */
		public static final String FONT = "fonts/";
	}

	/**
	 * SharedPreferences名称
	 */
	public static final String SHARED_PREFERENCES_NAME = "ZMS_GaodeNavi";

}
