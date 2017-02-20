package com.tchip.gaodenavi.util;

import com.tchip.gaodenavi.service.SpeakService;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class NetworkUtil {

	public static int getNetworkType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			return info.getType();
		} else {
			return -1;
		}
	}

	public static void noNetworkHint(Context context) {
		String strNoNetwork = "无网络链接";

		Intent intent = new Intent(context, SpeakService.class);
		intent.putExtra("content", strNoNetwork);
		context.startService(intent);

		Toast.makeText(context, strNoNetwork, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 返回网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 返回当前Wifi是否连接上
	 * 
	 * @param context
	 * @return true 已连接
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		if (netInfo != null
				&& netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 获取设备IMEI
	 * 
	 * @param context
	 * @return
	 */
	public String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}
