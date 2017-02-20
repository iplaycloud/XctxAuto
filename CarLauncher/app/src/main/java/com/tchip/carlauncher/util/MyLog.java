package com.tchip.carlauncher.util;

import com.tchip.carlauncher.Constant;

import android.util.Log;

public class MyLog {

	public static void e(String log) {
		if (Constant.isDebug) {
			String systemTime = DateUtil.getTimeStr("HH:mm:ss");
			Log.e(Constant.TAG, systemTime + "-" + log);
		}
	}

	public static void v(String log) {
		if (Constant.isDebug) {
			String systemTime = DateUtil.getTimeStr("HH:mm:ss");
			Log.v(Constant.TAG, systemTime + "-" + log);
		}
	}

	public static void d(String log) {
		if (Constant.isDebug) {
			String systemTime = DateUtil.getTimeStr("HH:mm:ss");
			Log.d(Constant.TAG, systemTime + "-" + log);
		}
	}

	public static void i(String log) {
		if (Constant.isDebug) {
			String systemTime = DateUtil.getTimeStr("HH:mm:ss");
			Log.i(Constant.TAG, systemTime + "-" + log);
		}
	}

	public static void w(String log) {
		if (Constant.isDebug) {
			String systemTime = DateUtil.getTimeStr("HH:mm:ss");
			Log.w(Constant.TAG, systemTime + "-" + log);
		}
	}

}
