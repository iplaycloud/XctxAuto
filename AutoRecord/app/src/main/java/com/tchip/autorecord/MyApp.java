package com.tchip.autorecord;

import com.tchip.autorecord.util.MyLog;
import com.tchip.autorecord.util.MyUncaughtExceptionHandler;
import com.tchip.autorecord.util.ProviderUtil;
import com.tchip.autorecord.util.ProviderUtil.Name;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApp extends Application {
	/** 应用出错:需要停止录像 */
	public static boolean isAppException = false;

	/** ACC是否连接 */
	public static boolean isAccOn = true;

	/** 正在执行休眠确认 */
	public static boolean isSleepConfirm = false;

	/** 正在执行唤醒确认 */
	public static boolean isWakeConfirm = false;

	/** 插入录像卡：需要启动录像 */
	public static boolean shouldMountRecordFront = false;
	public static boolean shouldMountRecordBack = false;

	/**
	 * 分辨率
	 * 
	 * Constant.Record.STATE_RESOLUTION_1080P
	 * 
	 * Constant.Record.STATE_RESOLUTION_720P
	 */
	public static int resolutionState;

	/** 休眠唤醒：需要启动录像 */
	public static boolean shouldWakeRecord = false;

	/** 是否正在进行停车守卫录像 */
	public static boolean isParkRecording = false;

	/** 语音停止录像 */
	public static boolean shouldStopFrontFromVoice = false;
	public static boolean shouldStopBackFromVoice = false;

	/** ACC拍照后,在onFileSave中传Path给DSA */
	public static boolean shouldSendPathToDSA = false;

	public static boolean shouldSendPathToDSAUpload = false;

	/** 前录是否正在录像 */
	public static boolean isFrontRecording = false;
	/** 后录是否正在录像 */
	public static boolean isBackRecording = false;

	/** 前录录像预览窗口是否初始化 */
	public static boolean isFrontPreview = false;

	public static boolean isBackPreview = false;

	/** 更新录像时间线程是否正在运行 */
	public static boolean isUpdateFrontTimeRun = false;
	public static boolean isUpdateBackTimeRun = false;

	/** 当前视频片段是否加锁 */
	public static boolean isFrontLock = false;
	public static boolean isBackLock = false;

	/** 第二段视频加锁 */
	public static boolean isFrontLockSecond = false;
	public static boolean isBackLockSecond = false;

	/** 侦测到碰撞 */
	public static boolean isFrontCrashed = false;
	public static boolean isBackCrashed = false;

	/** 存储更改视频分辨率前的录像状态 */
	public static boolean shouldVideoRecordWhenChangeSize = false;

	/** 存储更改静音前的录像状态 */
	public static boolean shouldVideoRecordWhenChangeMute = false;

	/** SD卡取出 */
	public static boolean isVideoCardEject = false;

	/** 视频卡是否存在 */
	public static boolean isVideoCardExist = false;

	/** SD卡准备格式化 */
	public static boolean isVideoCardFormat = false;

	/** 系统准备关机 */
	public static boolean isGoingShutdown = false;

	/** 碰撞侦测开关:默认打开 */
	public static boolean isCrashOn = Constant.GravitySensor.DEFAULT_ON;

	/** 碰撞侦测级别 */
	public static int crashSensitive = Constant.GravitySensor.SENSITIVE_DEFAULT;

	private SharedPreferences sharedPreferences;

	/** 是否初次启动 */
	public static boolean isFirstLaunch = true;

	/** 当前正在录像的视频名称 */
	public static String nowRecordVideoName = "";

	/** 是否正在显示内部存储空间过低对话框 */
	public static boolean isFlashCleanDialogShow = false;

	private Context context;

	public static enum SLEEP_STATE {
		/** 未休眠：ACC连接 */
		SLEEP_NONE,

		/** ACC刚断开的2秒 */
		SLEEP_CONFIRM,

		/** 执行90秒线程，过后休眠 */
		SLEEP_GOING,

		/** 休眠中 */
		SLEEPING
	}

	/** 休眠状态 */
	public static SLEEP_STATE sleepState = SLEEP_STATE.SLEEP_NONE;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(context);

		initialCrashData();
		super.onCreate();
	}

	/** 初始化碰撞数据 */
	private void initialCrashData() {
		try {
			sharedPreferences = getSharedPreferences(Constant.MySP.NAME,
					Context.MODE_PRIVATE);

			String strDetectCrashState = ProviderUtil.getValue(context,
					Name.SET_DETECT_CRASH_STATE, "1");
			if ("0".equals(strDetectCrashState)) {
				isCrashOn = false;
			} else {
				isCrashOn = true;
			}

			String strDetectCrashLevel = ProviderUtil.getValue(context,
					Name.SET_DETECT_CRASH_LEVEL, "1");
			if ("0".equals(strDetectCrashLevel)) {
				crashSensitive = 0;
			} else if ("2".equals(strDetectCrashLevel)) {
				crashSensitive = 2;
			} else {
				crashSensitive = 1;
			}
		} catch (Exception e) {
			MyLog.e("MyApp.initialCrashData: Catch Exception!" + e.getMessage());
		}
	}

}
