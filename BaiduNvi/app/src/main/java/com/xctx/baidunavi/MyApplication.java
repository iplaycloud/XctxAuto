package com.xctx.baidunavi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;

public class MyApplication extends Application {

	// Route Record
	public static boolean isRouteRecord = false;

	public static boolean isRouteComputing = false;

	@Override
	public void onCreate() {

		// 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		super.onCreate();
		SpeechUtility
				.createUtility(this, "appid=" + getString(R.string.app_id));

		/*
		 * 百度地图SDK初始化
		 * 
		 * 初始化全局 context，指定 sdcard 路径，若采用默认路径，请使用initialize(Context context)
		 * 重载函数 参数:
		 * 
		 * sdcardPath - sd 卡路径，请确保该路径能正常使用 context - 必须是 application context，SDK
		 * 各组件中需要用到。
		 */
		try {
			SDKInitializer.initialize(Constant.Path.SD_CARD_INTERNAL,
					getApplicationContext());
		} catch (Exception e) {

		}

	}

	// 百度导航实例是否初始化成功
	public static boolean isBaiduNaviInitialSuccess = false;
	public static boolean isBaiduNaviAuthSuccess = false;

}
