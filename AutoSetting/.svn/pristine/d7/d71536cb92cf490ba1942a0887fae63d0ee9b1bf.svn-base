package com.tchip.autosetting.util;

import com.tchip.autosetting.ui.QuickSettingActivity;
import com.tchip.autosetting.ui.SettingGravityActivity;
import com.tchip.autosetting.ui.SettingSystemDisplayActivity;
import com.tchip.autosetting.ui.SettingSystemVolumeActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.MediaStore;

public class OpenUtil {

	public enum MODULE_TYPE {

		/** 关于 */
		ABOUT,

		/** APN */
		APN,

		/** 设置 */
		AUTO_SETTING,

		/** 性能监视器 */
		CPU_INFO,

		/** 碰撞侦测(视频加锁) */
		CRASH,

		/** 设备测试 */
		DEVICE_TEST,

		/** 拨号 */
		DIALER,

		/** 工程模式 */
		ENGINEER_MODE,

		/** MTKLogger */
		MTK_LOGGER,

		/** 快速设置 */
		QUICK_SETTING,

		/** 应用 */
		APP,

		/** 流量使用情况 */
		DATA_USAGE,

		/** 日期和时间 */
		DATE,

		/** 开发者选项 */
		DEV_SETTING,

		/** 显示设置 */
		DISPLAY,

		/** FM发射设置 */
		FM,

		/** 位置 */
		LOCATION,

		/** USB */
		USB,

		/** 音量设置 */
		VOLUME,

		/** OTA */
		OTA,

		/** 备份和重置 */
		RESET,

		/** 存储设置 */
		STORAGE,

		/** 系统相机 */
		SYSTEM_CAMERA,

		/** 系统设置 */
		SYSTEM_SETTING,

		/** Wi-Fi */
		WIFI,

		/** Wi-Fi热点 */
		WIFI_AP,
	}

	public static void openModule(Activity activity, MODULE_TYPE moduleType) {
		if (!ClickUtil.isQuickClick(1000)) {
			try {
				switch (moduleType) {
				case ABOUT:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
					break;

				case APP:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
					break;

				case APN:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_APN_SETTINGS));
					break;

				case AUTO_SETTING:
					ComponentName componentSetting = new ComponentName(
							"com.tchip.autosetting",
							"com.tchip.autosetting.ui.MainActivity");
					Intent intentSetting = new Intent();
					intentSetting.setComponent(componentSetting);
					activity.startActivity(intentSetting);
					break;

				case CPU_INFO:
					Intent intentCPUInfo = new Intent(Intent.ACTION_VIEW);
					intentCPUInfo.setClassName("eu.chainfire.perfmon",
							"com.common.activity.MainActivity");
					intentCPUInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentCPUInfo);
					break;

				case CRASH:
					Intent intentCrash = new Intent(activity,
							SettingGravityActivity.class);
					intentCrash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentCrash);
					break;

				case DEV_SETTING:
					activity.startActivity(new Intent(
							"android.settings.APPLICATION_DEVELOPMENT_SETTINGS"));
					break;

				case DEVICE_TEST:
					Intent intentDeviceTest = new Intent(Intent.ACTION_VIEW);
					intentDeviceTest.setClassName("com.DeviceTest",
							"com.DeviceTest.DeviceTest");
					intentDeviceTest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentDeviceTest);
					break;

				case DIALER:
					ComponentName componentDialer = new ComponentName(
							"com.goodocom.gocsdk",
							"com.tchip.call.MainActivity");
					Intent intentDialer = new Intent();
					intentDialer.setComponent(componentDialer);
					activity.startActivity(intentDialer);
					break;

				case ENGINEER_MODE:
					Intent intentEngineerMode = new Intent(Intent.ACTION_VIEW);
					intentEngineerMode.setClassName(
							"com.mediatek.engineermode",
							"com.mediatek.engineermode.EngineerMode");
					intentEngineerMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentEngineerMode);
					break;

				case MTK_LOGGER:
					Intent intentMtkLogger = new Intent(Intent.ACTION_VIEW);
					intentMtkLogger.setClassName("com.mediatek.mtklogger",
							"com.mediatek.mtklogger.MainActivity");
					intentMtkLogger.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentMtkLogger);

					break;

				case QUICK_SETTING:
					Intent intentQuickSetting = new Intent(activity,
							QuickSettingActivity.class);
					intentQuickSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentQuickSetting);
					break;

				case DATA_USAGE:
					activity.startActivity(new Intent(
							"android.settings.DATA_USAGE_SETTINGS"));
					break;

				case DATE:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_DATE_SETTINGS));
					break;

				case DISPLAY:
					Intent intentDisplay = new Intent(activity,
							SettingSystemDisplayActivity.class);
					intentDisplay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentDisplay);
					break;

				case FM:
					activity.startActivity(new Intent(
							"android.settings.FM_SETTINGS"));
					break;

				case LOCATION:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					break;

				case USB:
					HintUtil.showToast(activity, "ToDo");
					break;

				case VOLUME:
					Intent intentVolume = new Intent(activity,
							SettingSystemVolumeActivity.class);
					intentVolume.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentVolume);
					break;

				case RESET:
					activity.startActivity(new Intent(
							"android.settings.BACKUP_AND_RESET_SETTINGS"));
					break;

				case STORAGE:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
					break;

				case SYSTEM_CAMERA:
					Intent intent = new Intent(
							MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					activity.startActivity(intent);
					break;

				case SYSTEM_SETTING:
					ComponentName componentSettingSystem = new ComponentName(
							"com.android.settings",
							"com.android.settings.Settings");
					Intent intentSettingSystem = new Intent();
					intentSettingSystem.setComponent(componentSettingSystem);
					activity.startActivity(intentSettingSystem);
					break;

				case WIFI:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS));
					break;

				case WIFI_AP: // android.settings.TETHER_WIFI_SETTINGS
					activity.startActivity(new Intent(
							"mediatek.intent.action.WIFI_TETHER"));
					break;

				case OTA:
					Intent intentSettingOTA = new Intent(Intent.ACTION_VIEW);
					intentSettingOTA.setClassName("com.tchipota",
							"com.tchipota.MainActivity");
					intentSettingOTA.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intentSettingOTA);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				MyLog.e("OpenUtil.Exception:" + e.toString());
			}
		}

	}
}
