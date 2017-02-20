package com.xctx.baidunavi;

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

		public static final String SDCARD_1 = "/sdcard/1";
		public static final String SDCARD_2 = "/sdcard/2";

		/**
		 * 音乐背景图片目录
		 */
		public static final String MUSIC_IMAGE = "MusicBackground";

		/**
		 * 字体目录
		 */
		public static final String FONT = "fonts/";

		/**
		 * 百度离线地图子级目录
		 */
		public static final String BAIDU_OFFLINE_SUB = "/sdcard/BaiduMapSDK/vmp/l/";

		/**
		 * 百度离线地图，存储卡位置
		 */
		public static final String SD_CARD_MAP = "/sdcard";
		
		public static final String SD_CARD_INTERNAL =  "/sdcard";
	}

	/**
	 * FACE++ SDK
	 */
	public static final class FacePlusPlus {
		public static final String API_KEY = "543e743fa43f0550c2977995f3ff2222";
		public static final String API_SECRET = "IEpqaPm-wa-eznyZfKhvwW8rEGgzLxRk";
	}

	/**
	 * 讯飞语音SDK
	 */
	public static final String XUNFEI_APP_ID = "55f793d5";

	/**
	 * 百度地图SDK
	 */
	public static final class BaiduMap {
		public static final String API_KEY = "UGDtOU7G7D4b2ppjOTfmk54m";
		public static final String MCODE = "6B:40:B2:47:13:F5:6A:F7:40:6A:89:84:46:53:33:47:AD:DC:C1:0C;com.tchip.carlauncher";

	}

	/**
	 * SharedPreferences名称
	 */
	public static final String SHARED_PREFERENCES_NAME = "BaiduNavi";
	public static final String SHARED_PREFERENCES_SPEECH_NAME = "speech";

	/**
	 * SharedPreferences 字段
	 */
	public static final class SPString {
		public static final String ROUTE_PLAN_PREF = "routePlanPref";
		
	}

}
