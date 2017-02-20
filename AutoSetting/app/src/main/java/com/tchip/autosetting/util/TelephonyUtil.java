package com.tchip.autosetting.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.tchip.autosetting.Constant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelephonyUtil {
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

	/** 获取设备Mac地址 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/** 获取设备IMEI */
	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/** 获取设备IP地址 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	/**
	 * 飞行模式是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAirplaneModeOn(Context context) {
		return android.provider.Settings.System.getInt(
				context.getContentResolver(),
				android.provider.Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
	}

	/** 设置飞行模式开关 */
	public static void setAirplaneMode(Context context, boolean enable) {
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
		Intent intentAirplaneOn = new Intent(
				Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intentAirplaneOn.putExtra("state", enable);
		context.sendBroadcast(intentAirplaneOn);
	}

	/** 设置数据流量开关 */
	public static void setMobileDataEnable(Context context, boolean enable) {
		MyLog.v("TelephonyUtil.setMobileDataEnable:" + enable);
		//TelephonyManager telephonyManager = TelephonyManager.from(context);
		//telephonyManager.setDataEnabled(enable);
	}

	/** 获取数据流量开关 */
	public static boolean isMobileDataEnable(Context context) {
		//TelephonyManager telephonyManager = TelephonyManager.from(context);
		//boolean isEnable = telephonyManager.getDataEnabled();
		boolean isEnable = false;

		MyLog.v("TelephonyUtil.isMobileDataEnable:" + isEnable);
		return isEnable;
	}

}
