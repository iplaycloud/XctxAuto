package com.tchip.autorecord.util;

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

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.util.ProviderUtil.Name;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class SettingUtil {

	/** ACC 是否在 */
	public static boolean isAccOn(Context context) {
		String accState = ProviderUtil.getValue(context, Name.ACC_STATE, "0");
		return "1".equals(accState);
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
		MyLog.v("SettingUtil.setAirplaneMode:" + enable);
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
		Intent intentAirplaneOn = new Intent(
				Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intentAirplaneOn.putExtra("state", enable);
		context.sendBroadcast(intentAirplaneOn);
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

	/** ACC状态节点 */
	public static File fileAccStatus = new File(Constant.Path.NODE_ACC_STATUS);

	/**
	 * 获取ACC状态
	 * 
	 * @return 0:ACC下电,1:ACC上电
	 */
	public static int getAccStatus() {
		int accStatus = getFileInt(fileAccStatus);
		//return accStatus;

		return 1;
	}

	/**
	 * 获取CVBS状态
	 * 
	 * @return
	 */
	public static boolean isCVBSIn() {
		File fileCVBSState = new File(Constant.Path.NODE_CVBS_STATUS); // 背光值节点
		boolean isCVBSIn = false;
		String strValue = "";
		if (fileCVBSState.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(fileCVBSState), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strValue += lineTxt.toString();
				}
				read.close();
				isCVBSIn = strValue.endsWith("1");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getCVBSState: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getCVBSState: IOException");
			}
		}
		MyLog.i("[SettingUtil]isCVBSIn:" + isCVBSIn);
		return isCVBSIn;
	}

	public static int getFileInt(File file) {
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				int ch = 0;
				if ((ch = inputStreamReader.read()) != -1) {
					inputStreamReader.close();
					return Integer.parseInt(String.valueOf((char) ch));
				} else {
					inputStreamReader.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("getFileInt.FileNotFoundException:" + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("getFileInt.IOException:" + e.toString());
			}
		}
		return 0;
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

}
