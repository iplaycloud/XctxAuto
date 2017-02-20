package com.tchip.carlauncher.util;

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
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.MyApp;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SettingUtil {

	/** 设置飞行模式 */
	public static void setAirplaneMode(Context context, boolean setAirPlane) {
		MyLog.v("[SettingUtil]setAirplaneMode:" + setAirPlane);
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式的改变，让相应的程序可以处理。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
	}

	public static void setGpsState(final Context context, final boolean isGpsOn) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ContentResolver resolver = context.getContentResolver();
				boolean nowState = getGpsState(context);
				if (isGpsOn != nowState) {
					MyLog.v("[GPS]Set State:" + isGpsOn);
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
					Settings.Secure.putInt(resolver,
							Settings.Secure.LOCATION_MODE, mode);
				}
			}

		}, 3000);
	}

	public static boolean getGpsState(Context context) {
		ContentResolver resolver = context.getContentResolver();
		boolean gpsState = Settings.Secure.isLocationProviderEnabled(resolver,
				LocationManager.GPS_PROVIDER);
		MyLog.v("[GPS]Now State:" + gpsState);
		return gpsState;
	}

	/** 调整系统亮度 */
	public static void setBrightness(Context context, int brightness) {
		if (brightness <= Constant.Setting.MAX_BRIGHTNESS && brightness > -1) {
			boolean setSuccess = Settings.System.putInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, brightness);
			MyLog.v("[SettingUtil]setBrightness: " + brightness + ", "
					+ setSuccess);

			SharedPreferences sharedPreferences = context.getSharedPreferences(
					Constant.MySP.NAME, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putInt(Constant.MySP.STR_MANUL_LIGHT_VALUE, brightness);
			editor.commit();
		}
	}

	/** 获取系统亮度 */
	public static int getBrightness(Context context) {
		try {
			int nowBrightness = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			MyLog.v("[SettingUtil]nowBrightness:" + nowBrightness);
			return nowBrightness;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return Constant.Setting.DEFAULT_BRIGHTNESS;
		}
	}

	/** 设置熄屏时间 */
	public static void setScreenOffTime(Context context, int time) {
		Settings.System.putInt(context.getContentResolver(),
				android.provider.Settings.System.SCREEN_OFF_TIMEOUT, time);
	}

	/** 获取熄屏时间 */
	public static int getScreenOffTime(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return 155;
		}
	}

	/** FM发射开关节点,1：开 0：关 */
	public static File nodeFmEnable = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/enable_qn8027");

	/** FM发射频率节点，频率范围：7600~10800:8750-10800 */
	public static File nodeFmChannel = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-002c/setch_qn8027");

	public static boolean isFmTransmitOnSetting(Context context) {
		boolean isFmTransmitOpen = false;
		String fmEnable = Settings.System.getString(
				context.getContentResolver(),
				Constant.FMTransmit.SETTING_ENABLE);
		if (fmEnable != null && fmEnable.trim().length() > 0) {
			if ("1".equals(fmEnable)) {
				isFmTransmitOpen = true;
			} else {
				isFmTransmitOpen = false;
			}
		}
		return isFmTransmitOpen;
	}

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	public static int getFmFrequceny(Context context) {
		String fmChannel = Settings.System.getString(
				context.getContentResolver(),
				Constant.FMTransmit.SETTING_CHANNEL);

		return Integer.parseInt(fmChannel);
	}

	/**
	 * 设置FM发射频率:8750-10800
	 * 
	 * @param frequency
	 */
	public static void setFmFrequency(Context context, int frequency) {
		if (frequency >= 8750 || frequency <= 10800) {
			Settings.System.putString(context.getContentResolver(),
					Constant.FMTransmit.SETTING_CHANNEL, "" + frequency);

			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			MyLog.v("[SettingUtil]:Set FM Frequency success:" + frequency
					/ 100.0f + "MHz");
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
					Log.e(Constant.TAG, "SaveFileToNode:output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "SaveFileToNode:IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "SaveFileToNode:File:" + file + "not exists");
		}
	}

	/** 点亮屏幕 */
	public static void lightScreen(Context context) {
		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");

		wl.acquire(); // 点亮屏幕
		wl.release(); // 释放

		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE); // 得到键盘锁管理器对象
		KeyguardLock kl = km.newKeyguardLock("ZMS"); // 参数是LogCat里用的Tag
		kl.disableKeyguard();
	}

	/** Camera自动调节亮度节点，1：开 0：关;默认打开 */
	public static File fileAutoLightSwitch = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/back_car_status");

	/** 设置Camera自动调节亮度开关 */
	public static void setAutoLight(Context context, boolean isAutoLightOn) {
		SaveFileToNode(fileAutoLightSwitch, isAutoLightOn ? "1" : "0");
		MyLog.v("[SettingUtil]setAutoLight:" + isAutoLightOn);
	}

	/** 停车侦测开关节点，2：打开 3：关闭（默认） */
	public static File fileParkingMonitor = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/back_car_status");

	public static void setParkingMonitor(Context context, boolean isParkingOn) {
		MyLog.v("[SettingUtil]setParkingMonitor:" + isParkingOn);
		SaveFileToNode(fileParkingMonitor, isParkingOn ? "2" : "3");

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constant.MySP.NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constant.MySP.STR_PARKING_ON, isParkingOn);
		editor.commit();
	}

	/** ACC状态节点 */
	public static File fileAccStatus = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/acc_car_status");

	/**
	 * 获取ACC状态
	 * 
	 * @return 0:ACC下电,1:ACC上电
	 */
	public static int getAccStatus() {
		return getFileInt(fileAccStatus);
	}

	public static int getFileInt(File file) {
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				int ch = 0;
				if ((ch = inputStreamReader.read()) != -1)
					return Integer.parseInt(String.valueOf((char) ch));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/** 获取背光亮度值 */
	public static int getLCDValue() {
		File fileLCDValue = new File("/sys/class/leds/lcd-backlight/brightness"); // 背光值节点

		String strValue = "";
		if (fileLCDValue.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(fileLCDValue), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strValue += lineTxt.toString();
				}
				read.close();
				return Integer.parseInt(strValue);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: IOException");
			}
		}
		return -5;
	}

	/** 电子狗电源开关节点，1-打开 0-关闭 */
	public static File fileEDogPower = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/edog_car_status");

	/**
	 * 设置电子狗电源开关
	 * 
	 * @param isEDogOn
	 */
	public static void setEDogEnable(boolean isEDogOn) {
		MyLog.v("[SettingUtil]setEDogEnable:" + isEDogOn);
		SaveFileToNode(fileEDogPower, isEDogOn ? "1" : "0");
	}

	/** 初始化节点状态 */
	public static void initialNodeState(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constant.MySP.NAME, Context.MODE_PRIVATE);

		// 1.启动时初始化FM发射频率节点,频率范围：7600~10800:8750-10800
		try {
			if (MyApp.isAccOn) {
				int freq = getFmFrequceny(context);
				if (freq >= 8750 && freq <= 10800)
					setFmFrequency(context, freq);
				else
					setFmFrequency(context, 8750);

				boolean isFmOn = isFmTransmitOnSetting(context);
				// Settings.System.putString(context.getContentResolver(),
				// Constant.FMTransmit.SETTING_ENABLE, isFmOn ? "1" : "0");
				int intFmEnable = getFileInt(nodeFmEnable);
				if (isFmOn) {
					if (intFmEnable == 0) {
						SaveFileToNode(nodeFmEnable, "1");
						context.sendBroadcast(new Intent(
								"com.tchip.FM_OPEN_CARLAUNCHER")); // 通知状态栏同步图标
					}
				} else {
					if (intFmEnable == 1) {
						SaveFileToNode(nodeFmEnable, "0");
						context.sendBroadcast(new Intent(
								"com.tchip.FM_CLOSE_CARLAUNCHER")); // 通知状态栏同步图标
					}
				}
			}
		} catch (Exception e) {
			MyLog.e("[SettingUtil]initFmTransmit: Catch Exception!");
		}

		// 2.初始化自动亮度节点
		try {
			boolean autoScreenLight = sharedPreferences.getBoolean(
					"autoScreenLight", Constant.Setting.AUTO_BRIGHT_DEFAULT_ON);
			setAutoLight(context, autoScreenLight);
		} catch (Exception e) {
			MyLog.e("[SettingUtil]initialAutoLight: Catch Exception!");
		}

		// 3.初始化停车侦测开关
		try {
			boolean isParkingMonitorOn = sharedPreferences.getBoolean(
					"parkingOn", Constant.Record.parkDefaultOn);
			setParkingMonitor(context, isParkingMonitorOn);
		} catch (Exception e) {
			MyLog.e("[SettingUtil]initialParkingMonitor: Catch Exception!");
		}

	}

	/** 获取设备Mac地址 */
	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/** 获取设备IMEI */
	public String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/** 获取设备IP地址 */
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
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	public static float getGravityVauleBySensitive(int sensitive) {

		if (sensitive == Constant.GravitySensor.SENSITIVE_LOW) {
			return Constant.GravitySensor.VALUE_LOW;
		} else if (sensitive == Constant.GravitySensor.SENSITIVE_MIDDLE) {
			return Constant.GravitySensor.VALUE_MIDDLE;
		} else {
			return Constant.GravitySensor.VALUE_HIGH;
		}
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param step
	 *            增加音量
	 */
	public static void plusVolume(Context context, int type, int step) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		int nowVolume = audioManager.getStreamVolume(type);
		int toVolume = nowVolume + step;
		if (toVolume <= 15)
			audioManager.setStreamVolume(type, toVolume, 0);
		else
			audioManager.setStreamVolume(type, 15, 0);
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 * @param step
	 */
	public static void minusVolume(Context context, int type, int step) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int nowVolume = audioManager.getStreamVolume(type);
		int toVolume = nowVolume - step;
		if (toVolume > 0)
			audioManager.setStreamVolume(type, toVolume, 0);
		else
			audioManager.setStreamVolume(type, 0, 0);
	}

	/**
	 * 设置最大音量
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 */
	public static void setMaxVolume(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 15, 0);
	}

	/**
	 * 设置最小音量
	 * 
	 * @param context
	 * @param type
	 *            AudioManager.STREAM_MUSIC;STREAM_RING
	 */
	public static void setMinVolume(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 0, 0);
	}

	/** 静音 */
	public static void setMute(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(audioManager.RINGER_MODE_SILENT);
	}

	/** 关闭静音 */
	public static void setUnmute(Context context, int type) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(type, 8, 0);
	}

	public static void killApp(Context context, String app) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningPros = myActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros) {
			if (amPro.processName.contains(app)) {
				try {
					Method forceStopPackage = myActivityManager
							.getClass()
							.getDeclaredMethod("forceStopPackage", String.class);
					forceStopPackage.setAccessible(true);
					forceStopPackage.invoke(myActivityManager,
							amPro.processName);
					MyLog.d("kill kuwo music ok...............");
				} catch (Exception e) {
					MyLog.d("kill kuwo music failed..............."
							+ e.toString());
				}
			}
		}
	}

	public static void killApp(Context context, String[] app) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningPros = myActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros) {
			for (String strApp : app) {
				if (amPro.processName.contains(strApp)) {
					try {
						Method forceStopPackage = myActivityManager.getClass()
								.getDeclaredMethod("forceStopPackage",
										String.class);
						forceStopPackage.setAccessible(true);
						forceStopPackage.invoke(myActivityManager,
								amPro.processName);
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
