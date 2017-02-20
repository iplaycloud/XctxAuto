package com.xctx.baidunavi.util;

public class ClickUtil {

	private static long lastClickTime;

	/**
	 * 
	 * @param clickMinSpan
	 *            两次点击至少间隔时间,单位:ms
	 * @return
	 */
	public static boolean isQuickClick(int clickMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < clickMinSpan) {
			MyLog.v("[ClickUtil]isQuickClick:Click Too Quickly!");
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 思必驰语音传递过来的导航目的地Intent是否是最近传递过来
	 * 
	 * @param sendTime
	 * @return
	 */
	public static boolean isVoiceIntentInTime(long sendTime) {
		long nowTime = System.currentTimeMillis();
		long duration = nowTime - sendTime;
		if (duration < 3000) {
			return true;
		} else {
			return false;
		}
	}
}
