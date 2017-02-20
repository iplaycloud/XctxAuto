package com.tchip.carlauncher;

import java.io.File;

import android.os.Environment;

public interface Constant {
	/** Debug：打印Log */
	public static final boolean isDebug = true;

	/** 日志Tag */
	public static final String TAG = "AZ";

	/** SharedPreferences */
	public static final class MySP {
		/** 名称 */
		public static final String NAME = "CarLauncher";

		/** 是否开机自动录像[boolean:true] */
		public static final String STR_AUTO_RECORD = "autoRecord";

		/** 停车侦测是否打开[boolean:true] */
		public static final String STR_PARKING_ON = "parkingOn";

		/** 手动设置的亮度[int] */
		public static final String STR_MANUL_LIGHT_VALUE = "manulLightValue";
	}

	/** 广播 */
	public static final class Broadcast {
		/** ACC上电 */
		public static final String ACC_ON = "com.tchip.ACC_ON";

		/** ACC下电 */
		public static final String ACC_OFF = "com.tchip.ACC_OFF";

		/** 进入休眠 */
		public static final String SLEEP_ON = "com.tchip.SLEEP_ON";

		/** 取消休眠 */
		public static final String SLEEP_OFF = "com.tchip.SLEEP_OFF";

		/** 停车守卫:发生碰撞 */
		public static final String GSENSOR_CRASH = "com.tchip.GSENSOR_CRASH";

		/** 系统设置进入格式化界面 */
		public static final String MEDIA_FORMAT = "tchip.intent.action.MEDIA_FORMAT";

		/** 系统关机 */
		public static final String GOING_SHUTDOWN = "tchip.intent.action.GOING_SHUTDOWN";

		/** 外置蓝牙连接 */
		public static final String BT_CONNECTED = "com.tchip.BT_CONNECTED";

		/** 外置蓝牙断开 */
		public static final String BT_DISCONNECTED = "com.tchip.BT_DISCONNECTED";

		/** 蓝牙音乐播放 */
		public static final String BT_MUSIC_PLAYING = "com.tchip.BT_MUSIC_PLAYING";

		/** 蓝牙音乐停止 */
		public static final String BT_MUSIC_STOPED = "com.tchip.BT_MUSIC_STOPED";

		/**
		 * 设置同步广播,Extra:content
		 * 
		 * 1.停车守卫开关：parkOn,parkOff
		 * 
		 * 2.碰撞侦测，开关：crashOn,crashOff;灵敏度:crashLow,crashMiddle,crashHigh
		 */
		public static final String SETTING_SYNC = "com.tchip.SETTING_SYNC";

		/**
		 * 语音命令,Extra:command
		 * 
		 * 1.语音拍照：take_photo
		 * 
		 * 2.语音开始录像：open_dvr
		 * 
		 * 3.语音停止录像：close_dvr
		 */
		public static final String SPEECH_COMMAND = "com.tchip.SPEECH_COMMAND";

		/** 杀死语音 */
		public static final String AISPEECH_OFF = "com.tchip.aiSpeechSleep"; // "com.tchip.AISPEECH_OFF";

		/**
		 * 行车记录仪抓拍到图片之后发送以下广播,DSA接收
		 * 
		 * String[] picPaths = new String[2]; //第一张保存前置的图片路径 ；第二张保存后置的，如无可以为空
		 * 
		 * Intent intent = new Intent("com.action.http.post.picture");
		 * 
		 * intent.putExtra("picture", picPaths);
		 * 
		 * sendBroadcast(intent);
		 */
		public static final String SEND_PIC_PATH = "com.action.http.post.picture";

		/**
		 * DSA接收到广播之后进行图片的上传成功之后返回广播：
		 * 
		 * Intent intent = new Intent("dsa.action.http.picture.result");
		 * 
		 * intent.putExtra("result",1); // 0失败 1成功
		 * 
		 * sendBroadcast(intent);
		 */
		public static final String GET_PIC_RESULT = "dsa.action.http.picture.result";

		/**
		 * 照片保存广播
		 * 
		 * Extra:path
		 */
		public static final String ACTION_IMAGE_SAVE = "tchip.intent.action.ACTION_IMAGE_SAVE";

	}

	public static final class Setting {

		/** 最大亮度 */
		public static final int MAX_BRIGHTNESS = 196; // 255;

		/** 默认亮度 */
		public static final int DEFAULT_BRIGHTNESS = 180;

		/** Camera自动调节亮度是否打开 */
		public static final boolean AUTO_BRIGHT_DEFAULT_ON = false;
	}

	public static final class GravitySensor {
		/** 碰撞侦测是否默认打开 */
		public static final boolean DEFAULT_ON = true;

		/** 碰撞侦测默认灵敏度Level */
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

	public static final class Record {
		/** 是否开机自动录像 */
		public static final boolean autoRecord = true;

		/** 默认是否静音 */
		public static final boolean muteDefault = Module.isPublic;

		/** 停车侦测录像是否加锁 */
		public static final boolean parkVideoLock = false;

		/** 停车侦测录像时长(s) */
		public static final int parkVideoLength = 60;

		/** 停车侦测是否默认打开 */
		public static final boolean parkDefaultOn = true;

		/** 开机自动录像延时 */
		public static final int autoRecordDelay = 2500;

		/** 循环录像保留空间(单位：字节B) */
		public static final long SD_MIN_FREE_STORAGE = 400 * 1024 * 1024; // 400M

		/** 比特率240P */
		public static final int BIT_RATE_240P = 120 * 1000;

		/**
		 * 比特率720P:
		 * 
		 * MTK:9000 * 1000
		 */
		public static final int BIT_RATE_720P = 3500 * 1000;

		/**
		 * 比特率1080P:
		 * 
		 * MTK:17000 * 1000
		 */
		public static final int BIT_RATE_1080P = 8000 * 1000;

		/** 帧率 */
		public static final int FRAME_RATE = Module.isPublic ? 24 : 30;

		/** 默认分段 */
		public static final int DEFAULT_VIDEO_LENGTH = Module.isPublic ? 1 : 3;

		// 分辨率
		public static final int STATE_RESOLUTION_720P = 0;
		public static final int STATE_RESOLUTION_1080P = 1;

		// 录像状态
		public static final int STATE_RECORD_STARTED = 0;
		public static final int STATE_RECORD_STOPPED = 1;

		// 视频分段
		public static final int STATE_INTERVAL_3MIN = 0;
		public static final int STATE_INTERVAL_1MIN = 2;

		// 第二视图
		public static final int STATE_SECONDARY_ENABLE = 0;
		public static final int STATE_SECONDARY_DISABLE = 1;

		// 路径
		public static final int STATE_PATH_ZERO = 0;
		public static final int STATE_PATH_ONE = 1;
		public static final int STATE_PATH_TWO = 2;
		public static final String PATH_ZERO = "/mnt/sdcard";

		// 重叠
		public static final int STATE_OVERLAP_ZERO = 0;
		public static final int STATE_OVERLAP_FIVE = 1;

		// 静音
		public static final int STATE_MUTE = 0;
		public static final int STATE_UNMUTE = 1;

		/** 系统相机参数 */
		public static final String CAMERA_PARAMS = "zoom=0;fb-smooth-level-max=4;max-num-detected-faces-hw=15;"
				+ "cap-mode=normal;whitebalance=auto;afeng-min-focus-step=0;"
				+ "preview-format-values=yuv420sp,yuv420p,yuv420i-yyuvyy-3plane;"
				+ "rotation=0;jpeg-thumbnail-quality=100;preview-format=yuv420sp;"
				+ "iso-speed=auto;hue-values=low,middle,high;preview-frame-rate=30;"
				+ "jpeg-thumbnail-width=160;"
				+ "scene-mode-values=auto,portrait,landscape,night,night-portrait,theatre,beach,snow,sunset,steadyphoto,fireworks,sports,party,candlelight,hdr;"
				+ "video-size=1920x1088;preview-fps-range-values=(5000,60000);"
				+ "contrast-values=low,middle,high;"
				+ "preview-size-values=176x144,320x240,352x288,480x320,480x368,640x480,720x480,800x480,800x600,864x480,960x540,1280x720;"
				+ "auto-whitebalance-lock=false;preview-fps-range=5000,60000;"
				+ "antibanding=auto;min-exposure-compensation=-3;max-num-focus-areas=1;"
				+ "vertical-view-angle=49;fb-smooth-level-min=-4;eng-focus-fullscan-frame-interval=0;"
				+ "fb-skin-color=0;brightness_value=17;video-stabilization-supported=true;"
				+ "saturation-values=low,middle,high;eng-flash-duty-value=-1;edge=middle;"
				+ "iso-speed-values=auto,100,200,400,800,1600;picture-format-values=jpeg;"
				+ "exposure-compensation-step=1.0;eng-flash-duty-min=0;picture-size=2560x1440;"
				+ "saturation=middle;picture-format=jpeg;"
				+ "whitebalance-values=auto,incandescent,fluorescent,warm-fluorescent,daylight,cloudy-daylight,twilight,shade;"
				+ "afeng-max-focus-step=0;eng-shading-table=0;"
				+ "preferred-preview-size-for-video=1280x720;hue=middle;"
				+ "eng-focus-fullscan-frame-interval-max=65535;recording-hint=true;"
				+ "video-stabilization=false;zoom-supported=true;fb-smooth-level=0;"
				+ "fb-sharp=0;contrast=middle;eng-save-shading-table=0;jpeg-quality=90;"
				+ "scene-mode=auto;burst-num=1;metering-areas=(0,0,0,0,0);eng-flash-duty-max=1;"
				+ "video-size-values=176x144,480x320,640x480,864x480,1280x720,1920x1080;"
				+ "eng-focus-fullscan-frame-interval-min=0;focal-length=3.5;"
				+ "preview-size=1280x720;rec-mute-ogg=0;"
				+ "cap-mode-values=normal,face_beauty,continuousshot,smileshot,bestshot,evbracketshot,autorama,mav,asd;"
				+ "preview-frame-rate-values=15,24,30;max-num-metering-areas=9;fb-sharp-max=4;"
				+ "sensor-type=252;focus-mode-values=auto,macro,infinity,continuous-picture,continuous-video,manual,fullscan;"
				+ "fb-sharp-min=-4;jpeg-thumbnail-size-values=0x0,160x128,320x240;"
				+ "zoom-ratios=100,114,132,151,174,200,229,263,303,348,400;"
				+ "picture-size-values=320x240,640x480,1024x768,1280x720,1280x768,1280x960,1600x1200,2048x1536,2560x1440,2560x1920;"
				+ "edge-values=low,middle,high;horizontal-view-angle=53;brightness=middle;"
				+ "eng-flash-step-max=0;jpeg-thumbnail-height=128;capfname=/sdcard/DCIM/cap00;"
				+ "smooth-zoom-supported=true;zsd-mode=off;focus-mode=auto;auto-whitebalance-lock-supported=true;"
				+ "fb-skin-color-max=4;fb-skin-color-min=-4;max-num-detected-faces-sw=0;"
				+ "video-frame-format=yuv420p;max-exposure-compensation=3;focus-areas=(0,0,0,0,0);"
				+ "exposure-compensation=0;video-snapshot-supported=true;"
				+ "brightness-values=low,middle,high;auto-exposure-lock=false;"
				+ "effect-values=none,mono,negative,sepia,aqua,whiteboard,blackboard;"
				+ "eng-flash-step-min=0;effect=none;max-zoom=10;focus-distances=0.95,1.9,Infinity;"
				+ "mtk-cam-mode=2;auto-exposure-lock-supported=true;zsd-mode-values=off,on;"
				+ "antibanding-values=off,50hz,60hz,auto";
	}

	public static final class Module {
		/** 是否是公版软件 */
		public static final boolean isPublic = true;

		/** 双录是否录制到单卡 */
		public static final boolean isRecordSingleCard = true;

		/** 导航是否是百度:true-(自写百度SDK导航) false-图吧导航 **/
		public static final boolean isNavigationBaidu = false;

		/** 是否有微信助手 */
		public static final boolean hasWechat = isPublic;

		/** 是否有善领云中心(云电话、一键接人) */
		public static final boolean hasCloudCenter = !isPublic;

		/** 是否有微密 */
		public static final boolean hasWeme = false;// isPublic;

		/** 是否有翼卡在线 */
		public static final boolean hasECarOnline = isPublic;

		/** 是否有网络电台 */
		public static final boolean hasOnlineFM = false;// isPublic;

		/** 是否提示90s后启动停车守卫 */
		public static final boolean hintParkingMonitor = !isPublic;

		/** 是否有前后摄像切换图标 */
		public static final boolean hasCameraSwitch = true;

		/** 是否有天气预报 */
		public static final boolean hasWeather = false;

		/** 图标是否以块为单位移动 */
		public static final boolean isIconAtom = false;
	}

	/** FM发射 */
	public static final class FMTransmit {
		/** 系统设置：FM发射开关 */
		public static final String SETTING_ENABLE = "fm_transmitter_enable";

		/** 系统设置：FM发射频率 */
		public static final String SETTING_CHANNEL = "fm_transmitter_channel";
	}

	/** 路径 */
	public static final class Path {
		/** SDcard Path */
		public static final String SD_CARD = Environment
				.getExternalStorageDirectory().getPath();

		public static final String SDCARD_0 = "/storage/sdcard0";
		public static final String SDCARD_1 = "/storage/sdcard1";
		public static final String SDCARD_2 = "/storage/sdcard2";

		/** 录像存储卡路径 */
		public static String RECORD_SDCARD = SDCARD_2 + File.separator;// "/storage/sdcard2/";

		/** 前录存储路径 */
		public static String RECORD_FRONT = RECORD_SDCARD + "tachograph"
				+ File.separator;

		/** 后录存储路径 */
		public static String RECORD_BACK = RECORD_SDCARD + "tachograph_back"
				+ File.separator;

		/** 字体目录 */
		public static final String FONT = "fonts/";

		/** 百度离线地图子级目录 */
		public static final String BAIDU_OFFLINE_SUB = "/storage/sdcard0/BaiduMapSDK/vmp/l/";

		/** 百度离线地图，存储卡位置 */
		public static final String SD_CARD_MAP = "/storage/sdcard1";
	}

}
