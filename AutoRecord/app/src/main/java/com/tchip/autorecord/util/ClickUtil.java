package com.tchip.autorecord.util;

public class ClickUtil {

	private static long lastClickTime;

	/**
	 * @param clickMinSpan
	 *            两次点击至少间隔时间,单位:ms
	 * @return
	 */
	public static boolean isQuickClick(int clickMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < clickMinSpan) { // Click Too Quickly
			return true;
		}
		lastClickTime = time;
		return false;
	}

	private static long lastHintNoSd2Time;

	public static boolean isHintNoSd2TooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastHintNoSd2Time;
		if (0 < timeD && timeD < runMinSpan) {
			return true;
		}
		lastHintNoSd2Time = time;
		return false;
	}

	private static long lastHintSdEjectTime;

	public static boolean isHintSdEjectTooQuick(int minRunSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastHintSdEjectTime;
		if (0 < timeD && timeD < minRunSpan) {
			return true;
		}
		lastHintSdEjectTime = time;
		return false;
	}

	private static long lastHintSleepTime;

	public static boolean isHintSleepTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastHintSleepTime;
		if (0 < timeD && timeD < runMinSpan) {
			return true;
		}
		lastHintSleepTime = time;
		return false;
	}

	private static long lastPlusFrontTime;

	public static boolean isPlusFrontTimeTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastPlusFrontTime;
		if (0 < timeD && timeD < runMinSpan) {
			return true;
		}
		lastPlusFrontTime = time;
		return false;
	}

	private static long lastPlusBackTime;

	public static boolean isPlusBackTimeTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastPlusBackTime;
		if (0 < timeD && timeD < runMinSpan) {
			return true;
		}
		lastPlusBackTime = time;
		return false;
	}

	private static long lastSaveLogTime;

	public static boolean isSaveLogTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastSaveLogTime;
		if (0 < timeD && timeD < runMinSpan) {
			MyLog.v("[ClickUtil]isSaveLogTooQuick,Run Too Quickly!");
			return true;
		}
		lastSaveLogTime = time;
		return false;
	}

	/**
	 * 传递过来的Intent是否是最近传递过来
	 * 
	 * @param sendTime
	 * @return
	 */
	public static boolean isIntentInTime(long sendTime) {
		long nowTime = System.currentTimeMillis();
		long duration = nowTime - sendTime;
		MyLog.v("ClickUtil.sendTime:" + sendTime + ",nowTime:" + nowTime);
		if (duration < 3000) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 上次倒车时间 */
	private static long lastBackTime;

	public static boolean shouldSaveBackPkg(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastBackTime;
		lastBackTime = time;
		return timeD > runMinSpan;
	}
}
