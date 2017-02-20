package com.tchip.autosetting.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.tchip.autosetting.Constant;
import com.tchip.autosetting.util.ProviderUtil.Name;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;

public class SettingUtil {

	private static File nodeUsbUvcSwitch = new File(
			Constant.Path.NODE_SWITCH_USB_UVC);

	private static File nodeSwitchAudio = new File(
			Constant.Path.NODE_SWITCH_AUDIO);

	private static File nodeFMEnable = new File(Constant.Path.NODE_FM_ENABLE);
	private static File nodeFMFrequency = new File(
			Constant.Path.NODE_FM_FREQUENCY);

	/** 停车侦测开关节点 2：打开(默认) 3：关闭 */
	public static File fileParkingMonitor = new File(
			Constant.Path.NODE_PARK_MONITOR);

	public static void setUVCEnable(boolean isUVCOn) {
		MyLog.v("SettingUtil.setFMEnable:" + isUVCOn);
		SaveFileToNode(nodeUsbUvcSwitch, isUVCOn ? "1" : "0");
	}

	public static boolean isUVCEnable() {
		return getFileInt(nodeUsbUvcSwitch) == 1;
	}

	public static void setFMEnable(boolean isFmOn) {
		MyLog.v("SettingUtil.setFMEnable:" + isFmOn);
		SaveFileToNode(nodeFMEnable, isFmOn ? "1" : "0");
	}

	public static boolean isFMEnable() {
		return getFileInt(nodeFMEnable) == 1;
	}

	public static void writeAudioNode(String content) {
		MyLog.v("SettingUtil.writeAudioNode:" + content);
		SaveFileToNode(nodeSwitchAudio, content);
	}

	public static void setUsbMode(boolean isUsbOn) {
		MyLog.v("SettingUtil.setUsbMode:" + isUsbOn);
		SaveFileToNode(nodeUsbUvcSwitch, isUsbOn ? "40" : "41");
	}

	private static boolean isUsbMode() {
		int fileValue = 0;
		String strValue = "";
		if (nodeUsbUvcSwitch.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(nodeUsbUvcSwitch), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					strValue += lineTXT.toString();
				}
				read.close();

				fileValue = Integer.parseInt(strValue);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("SettingUtil.isUsbMode: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("SettingUtil.isUsbMode: IOException");
			}
		}
		MyLog.v("SettingUtil.isUsbMode,fileValue:" + fileValue);

		return fileValue == 40;
	}

	public static void setParkMonitorNode(boolean isParkingOn) {
		SaveFileToNode(fileParkingMonitor, isParkingOn ? "2" : "3");
		MyLog.v("SettingUtil.setParkingMonitorNode:" + isParkingOn);
	}

	public static void setParkMonitorConfig(Context context, boolean isParkingOn) {
		ProviderUtil.setValue(context, Name.SET_PARK_MONITOR_STATE,
				isParkingOn ? "1" : "0");
	}

	/**
	 * 调整系统亮度
	 * 
	 * @param brightness
	 */
	public static void setBrightness(Context context, int brightness,
			boolean saveLightAsManul) {
		if (brightness <= Constant.Setting.MAX_BRIGHTNESS && brightness > -1) {
			boolean setSuccess = Settings.System.putInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, brightness);
			MyLog.v("SettingUtil.setBrightness: " + brightness + ", "
					+ setSuccess + ",saveLightAsManul:" + saveLightAsManul);

			if (saveLightAsManul) {
				SharedPreferences sharedPreferences = context
						.getSharedPreferences(Constant.MySP.NAME,
								Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putInt(Constant.MySP.STR_MANUL_LIGHT_VALUE, brightness);
				editor.commit();
			}
		}
	}

	public static int getBrightness(Context context) {
		try {
			int nowBrightness = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			MyLog.v("SettingUtil.nowBrightness:" + nowBrightness);
			return nowBrightness;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return Constant.Setting.DEFAULT_BRIGHTNESS;
		}
	}

	public static void setScreenOffTime(Context context, int time) {
		boolean isSuccess = Settings.System.putInt(
				context.getContentResolver(),
				android.provider.Settings.System.SCREEN_OFF_TIMEOUT, time);

		MyLog.v("SettingUtil.setScreenOffTime " + time + ",isSuccess:"
				+ isSuccess);
	}

	public static int getScreenOffTime(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return Constant.Setting.SCREEN_OFF_DEFAULT;
		}
	}

	public static void SaveFileToNode(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;
				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					MyLog.e("SaveFileToNode.FileNotFoundException:"
							+ e.toString());
				}
			} catch (IOException e) {
				MyLog.e("SaveFileToNode.IOException:" + e.toString());
			}
		} else {
			MyLog.e("SaveFileToNode.File:" + file + "not exists");
		}
	}

	public static int getFileInt(File file) {
		if (file.exists()) {
			try {
				InputStream is = new FileInputStream(file);
				InputStreamReader fr = new InputStreamReader(is);
				int ch = 0;
				if ((ch = fr.read()) != -1) {
					fr.close();
					return Integer.parseInt(String.valueOf((char) ch));
				} else {
					fr.close();
					return 0;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 获取Mac地址
	 * 
	 * @param context
	 * @return
	 */
	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
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

	/**
	 * 获取本机IP地址
	 * 
	 * @return
	 */
	public String getLocalIpAddress() {
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
			MyLog.e("WifiPreference IpAddress:" + ex.toString());
		}
		return null;
	}

	/** GPS开关 */
	public static boolean isGpsOn(Context context) {
		ContentResolver resolver = context.getContentResolver();
		boolean gpsState = Settings.Secure.isLocationProviderEnabled(resolver,
				LocationManager.GPS_PROVIDER);
		MyLog.v("Now GPS State:" + gpsState);
		return gpsState;
	}

	/** 设置GPS开关 */
	public static void setGpsOn(final Context context, final boolean isGpsOn) {
		ContentResolver resolver = context.getContentResolver();
		boolean nowState = isGpsOn(context);
		if (isGpsOn != nowState) {
			MyLog.v("Set GPS State:" + isGpsOn);
			// Settings.Secure.setLocationProviderEnabled(resolver,
			// LocationManager.GPS_PROVIDER, isGpsOn);
			int mCurrentMode = (!isGpsOn) ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY
					: Settings.Secure.LOCATION_MODE_OFF;
			int mode = isGpsOn ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY
					: Settings.Secure.LOCATION_MODE_OFF;
			Intent intent = new Intent(
					"com.android.settings.location.MODE_CHANGING");
			intent.putExtra("CURRENT_MODE", mCurrentMode);
			intent.putExtra("NEW_MODE", mode);
			context.sendBroadcast(intent,
					android.Manifest.permission.WRITE_SECURE_SETTINGS);
			Settings.Secure.putInt(resolver, Settings.Secure.LOCATION_MODE,
					mode);
		}
	}

	/**
	 * Camera自动调节亮度节点
	 * 
	 * 1：开 0：关;默认打开
	 */
	public static File fileAutoLightSwitch = new File(
			Constant.Path.NODE_PARK_MONITOR);

	// public static void setAutoLight(boolean enable) {
	// if (enable) {
	// SaveFileToNode(fileAutoLightSwitch, "1");
	// } else {
	// SaveFileToNode(fileAutoLightSwitch, "0");
	// }
	// MyLog.v("[SettingUtil]setAutoLight:" + enable);
	// }

	public static File fileAccOffWake = new File(Constant.Path.NODE_ACC_STATUS);

	public static void setAccOffWake(boolean enable) {
		if (enable) {
			SaveFileToNode(fileAccOffWake, "1");
		} else {
			SaveFileToNode(fileAccOffWake, "0");
		}
	}
}
