package com.tchip.autosetting;

public interface Constant {
	/** Debug：打印Log */
	public static final boolean isDebug = true;
	/** 日志Tag */
	public static final String TAG = "AZ";

	/** SharedPreferences */
	public static final class MySP {
		/** 名称 */
		public static final String NAME = "AutoSetting";

		/** 手动设置的亮度[int] */
		public static final String STR_MANUL_LIGHT_VALUE = "manulLightValue";

	}

	/** 广播 */
	public static final class Broadcast {
		/** 改变蓝牙状态 */
		public static final String BT_STATUS_CHANGE = "com.tchip.BT_STATUS_CHANGE";

	}

	public static final class Setting {

		/** 最大亮度 */
		public static final int MAX_BRIGHTNESS = 190; // 255;

		/** 默认亮度 */
		public static final int DEFAULT_BRIGHTNESS = 190;
		
		/** 根据时间自动调节亮度-白天 */
		public static final int AUTO_BRIGHT_DAY = 180;
		
		/** 根据时间自动调节亮度-晚上 */
		public static final int AUTO_BRIGHT_NIGHT = 32;

		/** Camera自动调节亮度是否打开 */
		public static final boolean AUTO_BRIGHT_DEFAULT_ON = false;

		public static final int SCREEN_OFF_30S = 30 * 1000;

		public static final int SCREEN_OFF_1M = 60 * 1000;

		public static final int SCREEN_OFF_2M = 2 * SCREEN_OFF_1M;

		public static final int SCREEN_OFF_10M = 10 * SCREEN_OFF_1M;

		public static final int SCREEN_OFF_NEVER = Integer.MAX_VALUE;

		public static final int SCREEN_OFF_DEFAULT = SCREEN_OFF_NEVER; // 2147483647
	}

	public static final class Module {

		/** 进入MagicActivity的密码 */
		public static final String MagicCode = "error";

		public static final String RecordCode = "crash";

		/** 是否有APN设置 */
		public static final boolean hasAPNSetting = false;
		
		/**
		 * 是否有自动调节亮度功能
		 */
		public static final boolean hasAutoLight = true;
	}

	public static final class GravitySensor {
		/**
		 * 碰撞侦测是否默认打开
		 */
		public static final boolean DEFAULT_ON = true;

		/**
		 * 碰撞侦测默认灵敏度Level
		 */
		public static final float VALUE = 9.8f;

		public static final int SENSITIVE_LOW = 0;
		public static final int SENSITIVE_MIDDLE = 1;
		public static final int SENSITIVE_HIGH = 2;
		public static final int SENSITIVE_DEFAULT = SENSITIVE_MIDDLE;

		public static final float VALUE_LOW = VALUE * 1.8f;
		public static final float VALUE_MIDDLE = VALUE * 1.5f;
		public static final float VALUE_HIGH = VALUE * 1;
		public static final float VALUE_DEFAULT = VALUE_MIDDLE;

	}

	/** 路径 */
	public static final class Path {

		/** 字体目录 */
		public static final String FONT = "fonts/";

		/**
		 * 音频通道：0-系统到喇叭 1-蓝牙到喇叭 2-系统到FM 3-BT到FM
		 */
		public static final String NODE_SWITCH_AUDIO = "/sys/bus/i2c/devices/0-007f/Spk_Choose_Num";

		/** USB/UVC切换节点:0-USB 1-UVC */
		public static final String NODE_SWITCH_USB_UVC = "/sys/bus/i2c/devices/0-007f/Connect_To_PC";

		/** FM开关:0-下电 1-上电 */
		public static final String NODE_FM_ENABLE = "/sys/bus/i2c/devices/2-0011/enable_si4712";

		/** FM频率 */
		public static final String NODE_FM_FREQUENCY = "/sys/bus/i2c/devices/2-0011/setch_si4712";

		/** ACC状态 */
		public static final String NODE_ACC_STATUS = "/sys/bus/i2c/devices/0-007f/ACC_status";

		/**
		 * Read: 0-未倒车 1-倒车
		 * 
		 * Write：1：自动调节亮度节点开 0：关;默认打开 ；2：停车侦测开关节点打开 3：关闭（默认）
		 */
		public static final String NODE_PARK_MONITOR = "/sys/bus/i2c/devices/0-007f/back_car_status";

	}

}
