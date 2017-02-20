package com.tchip.carlauncher.util;

import com.tchip.carlauncher.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

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
		String strNoNetwork = context.getResources().getString(
				R.string.hint_no_network);
		HintUtil.speakVoice(context, strNoNetwork);
		HintUtil.showToast(context, strNoNetwork);
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

	/**
	 * 外接蓝牙模块是否打开
	 */
	public static boolean isExtBluetoothOn(Context context) {
		String btStatus = "";
		try {
			btStatus = Settings.System.getString(context.getContentResolver(),
					"bt_enable");
		} catch (Exception e) {
		}
		if ("1".equals(btStatus)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 外接蓝牙模块是否连接
	 */
	public static boolean isExtBluetoothConnected(Context context) {
		String btStatus = "";
		try {
			btStatus = Settings.System.getString(context.getContentResolver(),
					"bt_connect");
		} catch (Exception e) {
		}
		if ("1".equals(btStatus)) {
			return true;
		} else {
			return false;
		}
	}

	public static final int WIFI_MIN_RSSI = -100;
	public static final int WIFI_MAX_RSSI = -55;
	public static final int WIFI_RSSI_LEVELS = 5;

	public static int getWifiImageBySignal(int signal) {
		int wifiLevel = calculateWifiSignalLevel(signal, WIFI_RSSI_LEVELS);
		switch (wifiLevel) {
		case -1:
			return R.drawable.ic_qs_wifi_no_network;
		case 0:
			return R.drawable.ic_qs_wifi_full_1;
		case 1:
			return R.drawable.ic_qs_wifi_full_2;
		case 2:
			return R.drawable.ic_qs_wifi_full_3;
		case 3:
			return R.drawable.ic_qs_wifi_full_4;
		case 4:
			return R.drawable.ic_qs_wifi_full_4;

		default:
			return R.drawable.ic_qs_wifi_no_network;
		}
	}

	/**
	 * Calculates the level of the signal. This should be used any time a signal
	 * is being shown.
	 * 
	 * @param rssi
	 *            The power of the signal measured in RSSI.
	 * @param numLevels
	 *            The number of levels to consider in the calculated level.
	 * @return A level of the signal, given in the range of 0 to numLevels-1
	 *         (both inclusive).
	 */
	public static int calculateWifiSignalLevel(int rssi, int numLevels) {
		if (rssi <= WIFI_MIN_RSSI) {
			return 0;
		} else if (rssi >= WIFI_MAX_RSSI) {
			return numLevels - 1;
		} else {
			float inputRange = (WIFI_MAX_RSSI - WIFI_MIN_RSSI);
			float outputRange = (numLevels - 1);
			return (int) ((float) (rssi - WIFI_MIN_RSSI) * outputRange / inputRange);
		}
	}

	public static String getConnectWifiBssid(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
	}

	/**
	 * the GSM Signal Strength, valid values are (0-31, 99) as defined in TS
	 * 
	 * between 0..31, 99 is unknown
	 */
	public static final int SIGNAL_3G_MIN = 0;
	public static final int SIGNAL_3G_MAX = 31;
	public static final int SIGNAL_3G_LEVEL = 5;

	public static int get3GLevelImageByGmsSignalStrength(int gmsSignalStrength) {
		int signalLevel = calculate3GSignalLevel(gmsSignalStrength);
		switch (signalLevel) {
		case -1:
			return R.drawable.ic_qs_signal_no_signal;
		case 0:
			return R.drawable.ic_qs_signal_full_0;
		case 1:
			return R.drawable.ic_qs_signal_full_1;
		case 2:
			return R.drawable.ic_qs_signal_full_2;
		case 3:
			return R.drawable.ic_qs_signal_full_3;
		case 4:
			return R.drawable.ic_qs_signal_full_4;
		default:
			return R.drawable.ic_qs_signal_no_signal;
		}
	}

	/**
	 * L4:23~31:8
	 * 
	 * L3:14~22:8
	 * 
	 * L2:07~13:6
	 * 
	 * L1:01~06:5
	 * 
	 * @param signal
	 * @return
	 */
	public static int calculate3GSignalLevel(int signal) {
		if (signal > 31 || signal < 0)
			return -1;
		else if (signal > 22)
			return 4;
		else if (signal > 13)
			return 3;
		else if (signal > 7)
			return 2;
		else if (signal > 0)
			return 1;
		else
			return -1;
	}

	/**
	 * NETWORK_TYPE_UNKNOWN
	 * 
	 * NETWORK_TYPE_GPRS
	 * 
	 * NETWORK_TYPE_EDGE
	 * 
	 * NETWORK_TYPE_UMTS
	 * 
	 * NETWORK_TYPE_HSDPA
	 * 
	 * NETWORK_TYPE_HSUPA
	 * 
	 * NETWORK_TYPE_HSPA
	 * 
	 * NETWORK_TYPE_CDMA
	 * 
	 * NETWORK_TYPE_EVDO_0
	 * 
	 * NETWORK_TYPE_EVDO_A
	 * 
	 * NETWORK_TYPE_EVDO_B
	 * 
	 * NETWORK_TYPE_1xRTT
	 * 
	 * NETWORK_TYPE_IDEN
	 * 
	 * NETWORK_TYPE_LTE
	 * 
	 * NETWORK_TYPE_EHRPD
	 * 
	 * NETWORK_TYPE_HSPAP
	 */
	public static int get3GTypeImageByNetworkType(int networkType) {
		MyLog.v("[NetworkUtil]get3GTypeImageByNetworkType:" + networkType);
		switch (networkType) {
		/***** 2G *****/
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return R.drawable.ic_qs_signal_full_g; // G

		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return R.drawable.ic_qs_signal_full_1x; // 1X
		case TelephonyManager.NETWORK_TYPE_IDEN:
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return R.drawable.ic_qs_signal_full_e; // E

			/***** 3G *****/
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:

		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return R.drawable.ic_qs_signal_full_h; // H

		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return R.drawable.ic_qs_signal_full_h_plus; // H+

		case TelephonyManager.NETWORK_TYPE_EHRPD: // 3.9G
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return R.drawable.ic_qs_signal_full_3g;

			/***** 4G *****/
		case TelephonyManager.NETWORK_TYPE_LTE:
			return R.drawable.ic_qs_signal_full_4g; // 4G

			/***** Unknown *****/
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		default:
			return R.drawable.ic_qs_signal_full_null;
		}

	}

}
