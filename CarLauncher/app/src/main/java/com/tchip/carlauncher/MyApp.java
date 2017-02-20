package com.tchip.carlauncher;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tchip.carlauncher.util.MyLog;
import com.tchip.carlauncher.util.MyUncaughtExceptionHandler;

public class MyApp extends Application {
	/** 应用出错:需要停止录像 */
	public static boolean isAppException = false;

	/** 是否处于低功耗待机状态 */
	public static boolean isSleeping = false;

	/** ACC是否连接 */
	public static boolean isAccOn = true;

	/** 正在执行休眠确认 */
	public static boolean isSleepConfirm = false;

	/** 正在执行唤醒确认 */
	public static boolean isWakeConfirm = false;

	/** 插入录像卡：需要启动录像 */
	public static boolean shouldMountRecord = false;

	/** 需要开启录像大视图 */
	public static boolean shouldOpenRecordFullScreen = false;

	/** 需要关闭录像大视图 */
	public static boolean shouldCloseRecordFullScreen = false;

	/** 休眠唤醒：需要启动录像 */
	public static boolean shouldWakeRecord = false;

	/** 底层碰撞：需要启动录像 */
	public static boolean shouldCrashRecord = false;

	/** 录制底层碰撞视频后是否需要停止录像 */
	public static boolean shouldStopWhenCrashVideoSave = false;

	/** 语音拍照 */
	public static boolean shouldTakeVoicePhoto = false;

	/** 语音停止录像 */
	public static boolean shouldStopRecordFromVoice = false;

	/** ACC下电:拍照 */
	public static boolean shouldTakePhotoWhenAccOff = false;

	/** ACC拍照后,在onFileSave中传Path给DSA */
	public static boolean shouldSendPathToDSA = false;

	/** 碰撞将图片传给微信助手 */
	public static boolean shouldSendPathToWechat = false;

	/** 是否正在记录轨迹 */
	public static boolean isRouteRecord = false;

	/** 是否正在录像 */
	public static boolean isVideoReording = false;

	/** 更新录像时间线程是否正在运行 */
	public static boolean isUpdateTimeThreadRun = false;

	/** 当前视频片段是否加锁 */
	public static boolean isVideoLock = false;

	/** 第二段视频加锁 */
	public static boolean isVideoLockSecond = false;

	/** 电源是否连接 */
	public static boolean isPowerConnect = true;

	/** 侦测到碰撞 */
	public static boolean isCrashed = false;

	/** 存储更改视频分辨率前的录像状态 */
	public static boolean shouldVideoRecordWhenChangeSize = false;

	/** 存储更改静音前的录像状态 */
	public static boolean shouldVideoRecordWhenChangeMute = false;

	/** SD卡取出 */
	public static boolean isVideoCardEject = false;

	/** SD卡准备格式化 */
	public static boolean isVideoCardFormat = false;

	/** 系统准备关机 */
	public static boolean isGoingShutdown = false;

	/** 蓝牙是否正在播放音乐 */
	public static boolean isBTPlayMusic = false;

	/** 碰撞侦测开关:默认打开 */
	public static boolean isCrashOn = Constant.GravitySensor.DEFAULT_ON;

	/** 碰撞侦测级别 */
	public static int crashSensitive = Constant.GravitySensor.SENSITIVE_DEFAULT;

	private SharedPreferences sharedPreferences;

	public static boolean shouldResetRecordWhenResume = false;

	/** 是否初次启动 */
	public static boolean isFirstLaunch = true;

	/** 录像界面是否可见 */
	public static boolean isMainForeground = true;

	/** 正在处理ACC休眠拍照 */
	public static boolean isAccOffPhotoTaking = false;

	/** 当前正在录像的视频名称 */
	public static String nowRecordVideoName = "";

	public static String writeImageExifPath = "NULL";

	/** 录像预览窗口是否初始化 */
	public static boolean isCameraPreview = false;

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

		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(getApplicationContext());

		initialCrashData();
		super.onCreate();

	}

	/** 初始化碰撞数据 */
	private void initialCrashData() {
		try {
			sharedPreferences = getSharedPreferences(Constant.MySP.NAME,
					Context.MODE_PRIVATE);

			isCrashOn = sharedPreferences.getBoolean("crashOn",
					Constant.GravitySensor.DEFAULT_ON);
			crashSensitive = sharedPreferences.getInt("crashSensitive",
					Constant.GravitySensor.SENSITIVE_DEFAULT);
		} catch (Exception e) {
			MyLog.e("[MyApplication]initialCrashData: Catch Exception!"
					+ e.getMessage());
		}
	}

}
