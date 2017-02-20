package com.tchip.autorecord.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.R;
import com.tchip.autorecord.Typefaces;
import com.tchip.autorecord.service.SensorWatchService;
import com.tchip.autorecord.thread.MoveImageThread;
import com.tchip.autorecord.thread.WriteImageExifThread;
import com.tchip.autorecord.util.ClickUtil;
import com.tchip.autorecord.util.DateUtil;
import com.tchip.autorecord.util.FileUtil;
import com.tchip.autorecord.util.Flash2SDUtil;
import com.tchip.autorecord.util.HintUtil;
import com.tchip.autorecord.util.MyLog;
import com.tchip.autorecord.util.ProviderUtil;
import com.tchip.autorecord.util.ProviderUtil.Name;
import com.tchip.autorecord.util.SettingUtil;
import com.tchip.autorecord.util.StorageUtil;
import com.tchip.autorecord.view.BackLineView;
import com.tchip.tachograph.TachographCallback;
import com.tchip.tachograph.TachographRecorder;

import java.io.File;

//import com.tchip.autorecord.db.BackVideoDbHelper;
//import com.tchip.autorecord.db.DriveVideo;
//import com.tchip.autorecord.db.FrontVideoDbHelper;

public class MainActivity extends Activity {

    private Context context;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    // private FrontVideoDbHelper frontVideoDb;
    // private BackVideoDbHelper backVideoDb;
    private PowerManager powerManager;
    private WakeLock partialWakeLock;
    private WakeLock fullWakeLock;

    // 前置
    private RelativeLayout layoutFront;
    private TextView textFrontTime; // 时间跑秒
    private ImageButton imageFrontState; // 录像按钮
    private ImageButton imageFrontLock; // 加锁按钮
    private TextView textFrontLock;
    private ImageButton imageFrontSwitch; // 前后切换
    private TextView textFrontSwitch;
    private ImageButton imageVideoSize; // 视频尺寸
    private TextView textVideoSize;
    private ImageButton imageVideoLength; // 视频分段
    private TextView textVideoLength;
    private ImageButton imageVideoMute; // 静音按钮
    private TextView textVideoMute;
    private ImageButton imagePhotoTake; // 拍照按钮

    private Camera cameraFront;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolderFront;
    private TachographRecorder recorderFront;
    private int intervalState, muteState;

    // 后置
    // 倒车线控制
    private LinearLayout layoutBackLine;
    private BackLineView backLineView;

    /**
     * Intent是否是新的
     */
    private boolean isIntentInTime = false;

    private Handler mMainHandler; // 主线程Handler

    private enum UIConfig {
        /**
         * 公版 6.86
         */
        TQ6,
        /**
         * 善领 6.86
         */
        SL6,
        /**
         * 公版 7.84
         */
        TQ7,
        /**
         * 善领 7.84
         */
        SL7,
        /**
         * 公版 9.76
         */
        TQ9,
        /**
         * 善领 9.76
         */
        SL9
    }

    private String brand = "TQ";
    private String model = "TX2";
    /**
     * UI配置
     */
    private UIConfig uiConfig = UIConfig.TQ6;
    private int CAMERA_WIDTH = 1184;
    private int CAMERA_HEIGHT = 480;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHandler = new Handler(this.getMainLooper());
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        brand = Build.BRAND;
        model = Build.MODEL;
        if ("TX2S".equals(model)) { // TX2S-9.76
            CAMERA_WIDTH = 1280;
            CAMERA_HEIGHT = 445;
            if ("SL".equals(brand)) {
                uiConfig = UIConfig.SL9;
            } else {
                uiConfig = UIConfig.TQ9;
            }
        } else if ("TX3".equals(model)) { // TX3-7.84
            CAMERA_WIDTH = 1184;
            CAMERA_HEIGHT = 400;
            if ("SL".equals(brand)) {
                uiConfig = UIConfig.SL7;
            } else {
                uiConfig = UIConfig.TQ7;
            }
        } else { // TX2-6.86
            CAMERA_WIDTH = 1184;
            CAMERA_HEIGHT = 480;
            if ("SL".equals(brand)) {
                uiConfig = UIConfig.SL6;
            } else {
                uiConfig = UIConfig.TQ6;
            }
        }

        if ("TX2S".equals(model)) {
            setStatusBarVisible(true);
            if (Constant.Module.isTX2SBackFull) {
                setContentView(R.layout.activity_main_tx2s);
            } else {
                setContentView(R.layout.activity_main);
            }
        } else {
            // setStatusBarVisible(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);
        }

        context = getApplicationContext();

        /** 获取唤醒锁,保持屏幕常亮 */
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE); // 获取屏幕状态
        fullWakeLock = powerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.FULL_WAKE_LOCK, this.getClass()
                        .getCanonicalName() + "full");

        sharedPreferences = getSharedPreferences(Constant.MySP.NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // frontVideoDb = new FrontVideoDbHelper(context); // 视频数据库
        // backVideoDb = new BackVideoDbHelper(context);

        /** 初始化布局 */
        initialLayout();

        /** 用于管理所有程序的contentprovider实例 */
        getContentResolver()
                .registerContentObserver(
                        Uri.parse("content://com.tchip.provider.AutoProvider/state/name/"),
                        true, new AutoContentObserver(new Handler()));

        StorageUtil.createRecordDirectory();

        /** 设置录制初始值 */
        setupFrontDefaults();
        setupBackDefaults();

        /** 绘制录像按钮 */
        setupFrontViews();
//        setupBackViews();

        // 首次启动是否需要自动录像
        if (1 == SettingUtil.getAccStatus()) {
            MyApp.isAccOn = true; // 同步ACC状态
            new Thread(new AutoThread()).start(); // 序列任务线程
        } else {
            MyApp.isAccOn = false; // 同步ACC状态
            MyLog.v("[Main]ACC Check:OFF");
            String strParkRecord = ProviderUtil.getValue(context,
                    Name.PARK_REC_STATE, "0");
            if ("1".equals(strParkRecord)) {
                MyApp.isParkRecording = true;
                acquirePartialWakeLock(10 * 1000);
                new Thread(new AutoThread()).start(); // 序列任务线程
            } else {
                MyApp.isParkRecording = false;
            }
        }

        // 碰撞侦测服务
        Intent intentSensor = new Intent(this, SensorWatchService.class);
        startService(intentSensor);

        new Thread(new BackgroundThread()).start(); // 后台线程

        /** 主广播 */
        mainReceiver = new MainReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Broadcast.ACC_ON);
        intentFilter.addAction(Constant.Broadcast.ACC_OFF);
        intentFilter.addAction(Constant.Broadcast.BACK_CAR_ON);
        intentFilter.addAction(Constant.Broadcast.BACK_CAR_OFF);
        intentFilter.addAction(Constant.Broadcast.SPEECH_COMMAND);
        intentFilter.addAction(Constant.Broadcast.MEDIA_FORMAT);
        intentFilter.addAction(Constant.Broadcast.GOING_SHUTDOWN);
        intentFilter.addAction(Constant.Broadcast.RELEASE_RECORD);
        intentFilter.addAction(Constant.Broadcast.RELEASE_RECORD_TEST);
        intentFilter.addAction("tchip.intent.action.MOVE_RECORD_BACK");
        registerReceiver(mainReceiver, intentFilter);

        // 接收额外信息
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String reason = extras.getString("reason");
            long sendTime = extras.getLong("time");
            isIntentInTime = ClickUtil.isIntentInTime(sendTime);
            MyLog.v("isIntentInTime:" + isIntentInTime + ",reason:" + reason);
            if (isIntentInTime) {
                if ("autoui_oncreate".equals(reason)) { // 回到主界面
                    MyApp.shouldMountRecordFront = true;
                    MyApp.shouldMountRecordBack = true;
                    // new Thread(new BackHomeWhenBootThread()).start();
                } else if ("acc_on".equals(reason)) {
                    MyApp.shouldMountRecordFront = true;
                    MyApp.shouldMountRecordBack = true;
                    new Thread(new BackHomeWhenAccOnThread()).start();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        MyLog.v("onResume");

        /** AutoRecord是否初始化 */
        ProviderUtil.setValue(context, Name.RECORD_INITIAL, "1");

        try {
            refreshFrontButton(); // 更新录像界面按钮状态

            /** 绘制录像按钮 */
            setupFrontViews();
//            setupBackViews();
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e("onResume catch Exception:" + e.toString());
        }

//		if (cameraBeforeBack == 0) {
//			String strBackState = ProviderUtil.getValue(context,
//					Name.BACK_CAR_STATE, "0");
//
//			if ("1".equals(strBackState)) { // 隐藏格式化对话框
//				if (Constant.Module.hasCVBSDetect && !SettingUtil.isCVBSIn()) {
//					HintUtil.showToast(context,
//							getString(R.string.no_cvbs_detect));
//				} else {
//					switchCameraTo(Integer.parseInt(strBackState));
//					setBackPreviewBig(true);
//					sendBroadcast(new Intent(
//							Constant.Broadcast.HIDE_FORMAT_DIALOG));
//				}
//			} else {
//				switchCameraTo(Integer.parseInt(strBackState));
//			}
//		}

        super.onResume();
    }

    @Override
    protected void onPause() {
        MyLog.v("onPause,FrontRecording:" + MyApp.isFrontRecording
                + ",BackRecording:" + MyApp.isBackRecording);

        if (!MyApp.isFrontRecording) {
            MyApp.isFrontLockSecond = false;
        }
        if (!MyApp.isBackRecording) {
            MyApp.isBackLockSecond = false;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        MyLog.v("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MyLog.v("onDestroy");

        ProviderUtil.setValue(context, Name.RECORD_INITIAL, "0");
        // 释放录像区域
        releaseFrontRecorder();
//        releaseBackRecorder();
        closeFrontCamera();
//        closeBackCamera();
        // frontVideoDb.close();
        // backVideoDb.close();
        // 关闭碰撞侦测服务
        Intent intentCrash = new Intent(context, SensorWatchService.class);
        stopService(intentCrash);

        if (mainReceiver != null) {
            unregisterReceiver(mainReceiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyLog.i("[onKeyDown]keyCode:" + keyCode);
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            sendHomeKey();
//            return true;
//        } else
            return super.onKeyDown(keyCode, event);
    }

    private void setStatusBarVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);

        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * TX2S设置行车模式:非全屏，不显示按钮和倒车线
     *
     * @param isDriveOn
     */
    private void setDriveMode(boolean isDriveOn) {
        if (isDriveOn) { // 隐藏录像按钮

        } else { // 显示录像按钮

        }
    }

    /**
     * 设置倒车预览全屏
     *
     * @param big
     */
    private void setBackPreviewBig(boolean big) {
        if (Constant.Module.isTX2SBackFull && "TX2S".equals(model)) {
            MyLog.i("setBackPreviewBig:" + big);
            if (big) {
                setStatusBarVisible(false);

                /* 1.切换到后置摄像头 */

                /* 2.显示倒车线 */
                String strBackState = ProviderUtil.getValue(context,
                        Name.BACK_CAR_STATE, "0");
                if ("1".equals(strBackState)) {
//                    setBackLineVisible(true);
                } else {
//                    setBackLineVisible(false);
                }
            } else {

                /* 1.隐藏倒车线 */
//                setBackLineVisible(false);
                setStatusBarVisible(true);
            }
        }
    }

    class BackHomeWhenBootThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // sendHomeKey()
            // startLauncher();
            String strBackState = ProviderUtil.getValue(context,
                    Name.BACK_CAR_STATE, "0");
            if ("1".equals(strBackState)) { // 倒车不返回主页
            } else {
                moveTaskToBack(true);
            }
        }

    }

    class BackHomeWhenAccOnThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendHomeKey(); // moveTaskToBack(true);
        }

    }

    private void sendHomeKey() {
        String strBackState = ProviderUtil.getValue(context,
                Name.BACK_CAR_STATE, "0");
        if ("1".equals(strBackState)) { // 倒车不返回主页
        } else {
            sendKeyCode(KeyEvent.KEYCODE_HOME);
        }
    }

    /**
     * ContentProvder监听
     */
    public class AutoContentObserver extends ContentObserver {

        public AutoContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            String name = uri.getLastPathSegment(); // getPathSegments().get(2);
            if (name.equals("state")) { // insert

            } else { // update
                if (Name.SET_DETECT_CRASH_STATE.equals(name)) {
                    String strDetectCrashState = ProviderUtil.getValue(context,
                            Name.SET_DETECT_CRASH_STATE, "1");
                    if ("0".equals(strDetectCrashState)) {
                        MyApp.isCrashOn = false;
                    } else {
                        MyApp.isCrashOn = true;
                    }
                } else if (Name.SET_DETECT_CRASH_LEVEL.equals(name)) {
                    String strDetectCrashLevel = ProviderUtil.getValue(context,
                            Name.SET_DETECT_CRASH_LEVEL, "1");
                    if ("0".equals(strDetectCrashLevel)) {
                        MyApp.crashSensitive = 0;
                    } else if ("2".equals(strDetectCrashLevel)) {
                        MyApp.crashSensitive = 2;
                    } else {
                        MyApp.crashSensitive = 1;
                    }
                } else if (Name.SET_PARK_MONITOR_STATE.equals(name)) {

                } else if (Name.ACC_STATE.equals(name)) {
                } else if (Name.PARK_REC_STATE.equals(name)) {
                    if (!MyApp.isAccOn) {
                        String strParkRecord = ProviderUtil.getValue(context,
                                Name.PARK_REC_STATE, "0");
                        if ("1".equals(strParkRecord)) {
                            MyApp.isParkRecording = true;
                        } else {
                            MyApp.isParkRecording = false;
                        }
                    } else {
                        ProviderUtil
                                .setValue(context, Name.PARK_REC_STATE, "0");
                        MyApp.isParkRecording = false;
                    }
                }
            }
            super.onChange(selfChange, uri);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

    }

    /**
     * 获取休眠锁
     * <p>
     * PARTIAL_WAKE_LOCK
     * <p>
     * SCREEN_DIM_WAKE_LOCK
     * <p>
     * FULL_WAKE_LOCK
     * <p>
     * ON_AFTER_RELEASE
     */
    private void acquirePartialWakeLock(long timeout) {
        partialWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, this.getClass()
                        .getCanonicalName());
        partialWakeLock.acquire(timeout);
    }

    private void acquireFullWakeLock() {
        if (!fullWakeLock.isHeld()) {
            fullWakeLock.acquire();
        }
    }

    private void releaseFullWakeLock() {
        if (fullWakeLock.isHeld()) {
            fullWakeLock.release();
        }
    }

    private MainReceiver mainReceiver;
    private int cameraBeforeBack = 0; // 倒车前界面：0-前 1-后

    public class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.v("MainReceiver.action:" + action);

            if (action.equals(Constant.Broadcast.ACC_OFF)) {
                MyApp.isAccOn = false;
                MyApp.isAccOn = (1 == SettingUtil.getAccStatus());

                MyApp.shouldSendPathToDSA = true;
                takePhoto(true);
            } else if (action.equals(Constant.Broadcast.ACC_ON)) {
                MyApp.isAccOn = true;
                MyApp.shouldWakeRecord = true;

                String videoTimeStr = sharedPreferences.getString("videoTime",
                        "1");
                intervalState = "3".equals(videoTimeStr) ? Constant.Record.STATE_INTERVAL_3MIN
                        : Constant.Record.STATE_INTERVAL_1MIN;
                setRecordInterval(("3".equals(videoTimeStr)) ? 3 * 60 : 1 * 60); // 重设视频分段

                // 碰撞侦测服务
                Intent intentSensor = new Intent(context,
                        SensorWatchService.class);
                startService(intentSensor);
            } else if (action.equals(Constant.Broadcast.BACK_CAR_ON)) {

                /** 开启倒车界面 */
                MyLog.i("cameraBeforeBack:" + cameraBeforeBack);
                if (Constant.Module.hasCVBSDetect && !SettingUtil.isCVBSIn()) {
                    HintUtil.showToast(context, getString(R.string.no_cvbs_detect));
                } else {
                    acquireFullWakeLock();

                    switchCameraTo(1);

                    /** 显示倒车界面 */
//                    setBackLineVisible(true);
                    setBackPreviewBig(true);
                }
            } else if (action.equals(Constant.Broadcast.BACK_CAR_OFF)) {
                releaseFullWakeLock();

                switchCameraTo(0);

                setBackPreviewBig(false);
//                setBackLineVisible(false);
//                if ("com.tchip.autorecord".equals(ProviderUtil.getValue(
//                        context, Name.PKG_WHEN_BACK, "com.xxx.xxx"))) {
//                    switchCameraWhenBackOver(cameraBeforeBack);
//                }

            } else if (action.equals(Constant.Broadcast.SPEECH_COMMAND)) {
                String command = intent.getExtras().getString("command");
                if ("open_dvr".equals(command)) {
                    if (MyApp.isAccOn) {
                        if (!isFrontRecord()) {
                            MyApp.shouldMountRecordFront = true;
                        }
//                        if (!isBackRecord()) {
//                            MyApp.shouldMountRecordBack = true;
//                        }
                    }
                } else if ("close_dvr".equals(command)) {
                    moveTaskToBack(true); // 只关闭界面,不停止录像
                } else if ("start_dvr".equals(command)) {
                    if (MyApp.isAccOn) {
                        if (!isFrontRecord()) {
                            MyApp.shouldMountRecordFront = true;
                        }
//                        if (!isBackRecord()) {
//                            MyApp.shouldMountRecordBack = true;
//                        }
                    }
                } else if ("stop_dvr".equals(command)) {
                    if (isFrontRecord()) {
                        MyApp.shouldStopFrontFromVoice = true;
                    }
//                    if (isBackRecord()) {
//                        MyApp.shouldStopBackFromVoice = true;
//                    }
                } else if ("take_photo".equals(command)
                        || "take_photo_wenxin".equals(command)) {
                    takePhoto(MyApp.isAccOn);
                } else if ("take_park_photo".equals(command)) { // 停车照片
                    MyApp.shouldSendPathToDSA = true;
                    takePhoto(MyApp.isAccOn);
                } else if ("take_photo_dsa".equals(command)) { // 语音拍照上传
                    MyApp.shouldSendPathToDSAUpload = true;
                    takePhoto(MyApp.isAccOn);
                }
            } else if (action.equals(Constant.Broadcast.MEDIA_FORMAT)) {
                String path = intent.getExtras().getString("path");
                MyLog.e("MEDIA_FORMAT !! Path:" + path);
                if (Constant.Path.SDCARD_1.equals(path)) {
                    MyApp.isVideoCardFormat = true;
                }
            } else if (Constant.Broadcast.GOING_SHUTDOWN.equals(action)) {
                MyApp.isGoingShutdown = true;
            } else if (Constant.Broadcast.RELEASE_RECORD.equals(action)) { // 退出录像
                killAutoRecord();
            } else if (Constant.Broadcast.RELEASE_RECORD_TEST.equals(action)) {
                killAutoRecordForTest();
            } else if ("tchip.intent.action.MOVE_RECORD_BACK".equals(action)) {
                moveTaskToBack(true);
            }
        }
    }

    private void speakVoice(String content) {
        sendBroadcast(new Intent(Constant.Broadcast.TTS_SPEAK).putExtra(
                "content", content));
    }

    /**
     * 序列任务线程，分步执行：
     * <p>
     * 1.初次启动清空录像文件夹
     * <p>
     * 2.自动录像
     */
    public class AutoThread implements Runnable {

        @Override
        public void run() {
            try {

                /** 检查并删除异常视频文件：SD存在但数据库中不存在的文件 */
                StartCheckErrorFileThread();

                Thread.sleep(Constant.Record.autoRecordDelay);
                if (MyApp.isParkRecording) {
                    setRecordInterval(3 * 60); // 防止在分段一分钟的时候，停车守卫录出1分和0秒两段视频
                } else {
                    if (intervalState == Constant.Record.STATE_INTERVAL_3MIN) {
                        setRecordInterval(3 * 60);
                    } else {
                        setRecordInterval(1 * 60);
                    }
                }
                // 自动录像:如果已经在录像则不处理
                if (Constant.Record.autoRecordFront && !isFrontRecord()) {
                    if (!StorageUtil.isFrontCardExist()) {
                        /** 视频SD卡不存在提示 */
                        Message message = new Message();
                        message.what = 3;
                        autoHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 1;
                        autoHandler.sendMessage(message);
                    }
                }

                if (shouldRecordBack()) {
                    Message message = new Message();
                    message.what = 2;
                    autoHandler.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                MyLog.e("AutoThread: Catch Exception!");
            }
        }
    }

    /**
     * 是否开启后录
     */
    private boolean shouldRecordBack() {
        return Constant.Record.autoRecordBack;
        /*
         * if (uiConfig == UIConfig.SL6 || uiConfig == UIConfig.SL9) { return
		 * true; } else return sharedPreferences.getBoolean(
		 * Constant.MySP.STR_SHOULD_RECORD_BACK,
		 * Constant.Record.autoRecordBack);
		 */
    }

    final Handler autoHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    startRecordFront();
                    break;

                case 2:
//                    startRecordBack();
                    break;

                case 3:
                    /** 视频SD卡不存在提示 */
                    noVideoSDHint();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 后台线程，用以监测是否需要录制碰撞加锁视频(停车侦测)
     */
    public class BackgroundThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                backgroundHandler.sendMessage(message);
                // 修正标志：不对第二段视频加锁
                if (MyApp.isFrontLockSecond && !MyApp.isFrontRecording) {
                    MyApp.isFrontLockSecond = false;
                }
                if (MyApp.isBackLockSecond && !MyApp.isBackRecording) {
                    MyApp.isBackLockSecond = false;
                }

                if (!MyApp.isAccOn && !MyApp.isFrontRecording
                        && !MyApp.isBackRecording && !isFroceSleeping) {
                    new Thread(new ForceSleepThread()).start();
                }
            }
        }
    }

    private boolean isFroceSleeping = false;

    public class ForceSleepThread implements Runnable {

        @Override
        public void run() {
            MyLog.w("[ForceSleepThread]RUN");
            isFroceSleeping = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!MyApp.isAccOn && !MyApp.isFrontRecording
                    && !MyApp.isBackRecording) {
                MyApp.isParkRecording = false;
                ProviderUtil.setValue(context, Name.PARK_REC_STATE, "0");
                killAutoRecord();
                sendBroadcast(new Intent(Constant.Broadcast.KILL_APP).putExtra(
                        "name", "com.tchip.autorecord"));
            }
            isFroceSleeping = false;
            MyLog.w("[ForceSleepThread]END");
        }
    }

    /**
     * 后台线程的Handler,监测：
     * <p>
     * 1.是否需要休眠唤醒
     * <p>
     * 2.停车守卫侦测，启动录像
     * <p>
     * 3.ACC下电，拍照
     * <p>
     * 4.插入录像卡，若ACC在，启动录像
     */
    final Handler backgroundHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (MyApp.shouldWakeRecord) {
                        MyApp.shouldWakeRecord = false;
                        if (MyApp.isAccOn && !isFrontRecord()) {
                            new Thread(new AutoThread()).start(); // 序列任务线程
                        }
                    }
                    if (MyApp.shouldMountRecordFront) {
                        MyApp.shouldMountRecordFront = false;
                        if (MyApp.isAccOn && !isFrontRecord()) {
                            if (!StorageUtil.isFrontCardExist()) {
                                noVideoSDHint();
                            } else {
                                new Thread(new RecordFrontWhenMountThread())
                                        .start();
                            }
                        }
                    }
                    if (MyApp.shouldMountRecordBack) {
                        MyApp.shouldMountRecordBack = false;
                        if (MyApp.isAccOn && shouldRecordBack()) {
                            new Thread(new RecordBackWhenMountThread()).start();
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 插入录像卡录制一个视频线程
     */
    public class RecordFrontWhenMountThread implements Runnable {

        @Override
        public void run() {
            MyLog.v("Front.run RecordWhenMountThread");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = 1;
            recordFrontWhenMountHandler.sendMessage(message);
        }

    }

    /**
     * 插入视频卡时录制视频
     */
    final Handler recordFrontWhenMountHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        if (!isFrontRecord()) {
                            startFrontRecord();
                        }
                        MyLog.v("isFrontRecording:" + MyApp.isFrontRecording);
                    } catch (Exception e) {
                        MyLog.e("recordWhenMountHandler catch exception: "
                                + e.toString());
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 插入录像卡录制一个视频线程
     */
    public class RecordBackWhenMountThread implements Runnable {

        @Override
        public void run() {
            MyLog.v("Back.run RecordWhenMountThread");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = 1;
            recordBackWhenMountHandler.sendMessage(message);
        }

    }

    /**
     * 插入视频卡时录制视频
     */
    final Handler recordBackWhenMountHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
//                        if (!isBackRecord()) {
//                            startBackRecord();
//                        }
                        MyLog.v("isBackRecording:" + MyApp.isBackRecording);
                    } catch (Exception e) {
                        MyLog.e("recordWhenMountHandler catch exception: "
                                + e.toString());
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 初始化布局
     */
    private void initialLayout() {
        MyOnClickListener myOnClickListener = new MyOnClickListener();

        layoutFront = (RelativeLayout) findViewById(R.id.layoutFront);
        layoutFront.setVisibility(View.VISIBLE);
        initialSurface(); // 前置

        textFrontTime = (TextView) findViewById(R.id.textFrontTime);
        textFrontTime.setTypeface(Typefaces.get(this, Constant.Path.FONT
                + "Font-Quartz-Regular.ttf"));

        // 录制
        imageFrontState = (ImageButton) findViewById(R.id.imageFrontState);
        imageFrontState.setOnClickListener(myOnClickListener);

        // 锁定
        imageFrontLock = (ImageButton) findViewById(R.id.imageFrontLock);
        imageFrontLock.setOnClickListener(myOnClickListener);
        textFrontLock = (TextView) findViewById(R.id.textFrontLock);
        textFrontLock.setOnClickListener(myOnClickListener);

        textFrontLock.setOnClickListener(myOnClickListener);

        // 前后切换图标
        imageFrontSwitch = (ImageButton) findViewById(R.id.imageFrontSwitch);
        imageFrontSwitch.setOnClickListener(myOnClickListener);
        textFrontSwitch = (TextView) findViewById(R.id.textFrontSwitch);
        textFrontSwitch.setOnClickListener(myOnClickListener);

        // 拍照
        imagePhotoTake = (ImageButton) findViewById(R.id.imagePhotoTake);
        imagePhotoTake.setOnClickListener(myOnClickListener);

        // 视频尺寸
        imageVideoSize = (ImageButton) findViewById(R.id.imageVideoSize);
        imageVideoSize.setOnClickListener(myOnClickListener);
        textVideoSize = (TextView) findViewById(R.id.textVideoSize);
        textVideoSize.setOnClickListener(myOnClickListener);

        // 视频分段长度
        imageVideoLength = (ImageButton) findViewById(R.id.imageVideoLength);
        imageVideoLength.setOnClickListener(myOnClickListener);
        textVideoLength = (TextView) findViewById(R.id.textVideoLength);
        textVideoLength.setOnClickListener(myOnClickListener);

        // 静音
        imageVideoMute = (ImageButton) findViewById(R.id.imageVideoMute);
        imageVideoMute.setOnClickListener(myOnClickListener);
        textVideoMute = (TextView) findViewById(R.id.textVideoMute);
        textVideoMute.setOnClickListener(myOnClickListener);

        // 倒车线
        layoutBackLine = (LinearLayout) findViewById(R.id.layoutBackLine);
        backLineView = new BackLineView(this);
        //layoutBackLineControl = (RelativeLayout) findViewById(R.id.layoutBackLineControl);
        //layoutBackLineControl.setVisibility(View.GONE);

    }

    /**
     * 切换前后摄像画面
     */
    private void switchCameraTo(int camera) {
        MyLog.v("switchCameraTo:" + camera);
        switch (camera) {
            case 0:

                break;

            case 1:

                break;

            default:
                break;
        }
        // 更新录像界面按钮状态
        refreshFrontButton();
        setupFrontViews();
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageFrontState:
                    if (!ClickUtil.isQuickClick(1000)) {
                        if (MyApp.isFrontRecording) {
                            speakVoice(getResources().getString(
                                    R.string.hint_front_record_stop));
                            MyLog.v("[onClick]stopRecorder()");
                            stopFrontRecorder5Times();
                        } else {
                            if (!MyApp.isAccOn) {
                                HintUtil.showToast(
                                        context,
                                        getResources().getString(
                                                R.string.hint_stop_record_sleeping));
                            } else if (StorageUtil.isFrontCardExist()) {
                                if (isFrontRecord()) {
                                    recorderFront.stop();
                                    ProviderUtil.setValue(context,
                                            Name.REC_FRONT_STATE, "0");
                                }
                                speakVoice(getResources().getString(
                                        R.string.hint_front_record_start));
                                startRecordFront();
                            } else {
                                noVideoSDHint();
                            }
                        }
                    }
                    break;

                case R.id.imageBackState:
                    if (!ClickUtil.isQuickClick(1000)) {
                        if (shouldRecordBack()) { // 后录功能开启
                            if (MyApp.isBackRecording) {
                                speakVoice(getResources().getString(
                                        R.string.hint_back_record_stop));
                                MyLog.v("[onClick]stopRecorder()");
                                // stopBackRecorder5Times();
//                                if (stopBackRecorder() == 0) {
//                                    setBackState(false);
//                                }
                                editor.putBoolean(
                                        Constant.MySP.STR_SHOULD_RECORD_BACK, false);
                                editor.commit();
                            } else {
                                if (!MyApp.isAccOn) {
                                    HintUtil.showToast(
                                            context,
                                            getResources()
                                                    .getString(
                                                            R.string.hint_stop_record_sleeping));
                                } else if (StorageUtil.isBackCardExist()) {
//                                    if (isBackRecord()) {
//                                        recorderBack.stop();
                                        ProviderUtil.setValue(context,
                                                Name.REC_BACK_STATE, "0");
//                                    }
                                    speakVoice(getResources().getString(
                                            R.string.hint_back_record_start));
//                                    startRecordBack();
                                } else {
                                    noVideoSDHint();
                                }
                            }
                        } else { // 后录功能关闭：显示对话框
                            AlertDialog.Builder builder = new Builder(
                                    MainActivity.this);
                            builder.setMessage(getResources().getString(
                                    R.string.hint_enable_record_back));
                            builder.setTitle("提示");
                            builder.setPositiveButton(
                                    getResources()
                                            .getString(R.string.enable_cancel),
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                android.content.DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(
                                    getResources().getString(
                                            R.string.enable_confirm),
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                android.content.DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                            editor.putBoolean(
                                                    Constant.MySP.STR_SHOULD_RECORD_BACK,
                                                    true);
                                            editor.commit();

                                            if (!MyApp.isAccOn) {
                                                HintUtil.showToast(
                                                        context,
                                                        getResources()
                                                                .getString(
                                                                        R.string.hint_stop_record_sleeping));
                                            } else if (StorageUtil
                                                    .isBackCardExist()) {
//                                                if (isBackRecord()) {
//                                                    recorderBack.stop();
                                                    ProviderUtil.setValue(context,
                                                            Name.REC_BACK_STATE,
                                                            "0");
//                                                }
                                                speakVoice(getResources()
                                                        .getString(
                                                                R.string.hint_back_record_start));
//                                                startRecordBack();
                                            } else {
                                                noVideoSDHint();
                                            }
                                        }
                                    });
                            builder.create().show();
                        }
                    }
                    break;

                case R.id.imageFrontLock:
                case R.id.textFrontLock:
                    if (!ClickUtil.isQuickClick(500)) {
                        if (MyApp.isFrontRecording) {
                            lockOrUnlockFrontVideo();
                        } else {
                            HintUtil.showToast(MainActivity.this, getResources()
                                    .getString(R.string.hint_not_record_front));
                        }
                    }
                    break;

                case R.id.imageBackLock:
                case R.id.textBackLock:
                    if (!ClickUtil.isQuickClick(500)) {
                        if (MyApp.isBackRecording) {
                            lockOrUnlockBackVideo();
                        } else {
                            HintUtil.showToast(MainActivity.this, getResources()
                                    .getString(R.string.hint_not_record_back));
                        }
                    }
                    break;

                case R.id.imageVideoSize:
                case R.id.textVideoSize:
                    if (!ClickUtil.isQuickClick(3000)) {
                        // 切换分辨率录像停止，需要重置时间
                        MyApp.shouldVideoRecordWhenChangeSize = MyApp.isFrontRecording;
                        MyApp.isFrontRecording = false;
                        resetFrontTimeText();
                        textFrontTime.setVisibility(View.INVISIBLE);
                        if (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
                            setFrontResolution(Constant.Record.STATE_RESOLUTION_720P);
                            editor.putString("videoSize", "720");
                            MyApp.isFrontRecording = false;
                            speakVoice(getResources().getString(
                                    R.string.hint_video_size_720));
                        } else if (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_720P) {
                            setFrontResolution(Constant.Record.STATE_RESOLUTION_1080P);
                            editor.putString("videoSize", "1080");
                            MyApp.isFrontRecording = false;
                            speakVoice(getResources().getString(
                                    R.string.hint_video_size_1080));
                        }
                        editor.commit();
                    }
                    break;

                case R.id.imageVideoLength:
                case R.id.textVideoLength:
                    if (!ClickUtil.isQuickClick(500)) {
                        if (intervalState == Constant.Record.STATE_INTERVAL_3MIN) {
                            if (setRecordInterval(1 * 60) == 0) {
                                intervalState = Constant.Record.STATE_INTERVAL_1MIN;
                                editor.putString("videoTime", "1");
                                speakVoice(getResources().getString(
                                        R.string.hint_video_time_1));
                            }
                        } else if (intervalState == Constant.Record.STATE_INTERVAL_1MIN) {
                            if (setRecordInterval(3 * 60) == 0) {
                                intervalState = Constant.Record.STATE_INTERVAL_3MIN;
                                editor.putString("videoTime", "3");
                                speakVoice(getResources().getString(
                                        R.string.hint_video_time_3));
                            }
                        }
                        editor.commit();
                        setupFrontViews();
                    }
                    break;

                case R.id.imageVideoMute:
                case R.id.textVideoMute:
                    if (!ClickUtil.isQuickClick(1000)) {
                        // 切换录音/静音状态停止录像，需要重置时间
                        MyApp.shouldVideoRecordWhenChangeMute = MyApp.isFrontRecording;
                        if (muteState == Constant.Record.STATE_MUTE) {
                            setFrontMute(false, true);
                            muteState = Constant.Record.STATE_UNMUTE;
                            editor.putBoolean("videoMute", false);
                            editor.commit();
                        } else if (muteState == Constant.Record.STATE_UNMUTE) {
                            setFrontMute(true, true);
                            muteState = Constant.Record.STATE_MUTE;
                            editor.putBoolean("videoMute", true);
                            editor.commit();
                        }
                        setupFrontViews();
                        if (MyApp.shouldVideoRecordWhenChangeMute) { // 修改录音/静音后按需还原录像状态
                            MyApp.shouldVideoRecordWhenChangeMute = false;
                            new Thread(new StartRecordWhenChangeMuteThread())
                                    .start();
                        }
                    }
                    break;

                // case R.id.surfaceView:
                case R.id.imagePhotoTake:
                    if (!ClickUtil.isQuickClick(1000)) {
                        takePhoto(true);
                    }
                    break;

                case R.id.imageFrontSwitch:
                case R.id.textFrontSwitch:
                    if (Constant.Module.hasCVBSDetect && !SettingUtil.isCVBSIn()) {
                        HintUtil.showToast(context,
                                getString(R.string.no_cvbs_detect));
                    } else {

                        switchCameraTo(1);
                        cameraBeforeBack = 1;
                    }
                    break;

//                case R.id.imageBackSwitch:
//                case R.id.textBackSwitch:
//                    switchCameraTo(0);
//                    cameraBeforeBack = 0;
//                    break;

                case R.id.imageBackLineShow:
                    String strBackLineShow = ProviderUtil.getValue(context,
                            Name.BACK_LINE_SHOW, "1");
                    if ("1".equals(strBackLineShow)) {
                        ProviderUtil.setValue(context, Name.BACK_LINE_SHOW, "0");
                    } else {
                        ProviderUtil.setValue(context, Name.BACK_LINE_SHOW, "1");
                    }
//                    updateBackLineControlView();
                    break;

                case R.id.imageBackLineEdit:
                    boolean isModifyMode = backLineView.getModifyMode();
                    backLineView.setModifyMode(!isModifyMode);
                    backLineView.invalidate();
                    break;

                case R.id.imageBackLineReset:
                    backLineView.clearPonitConfig();
                    backLineView.invalidate();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 加锁或解锁视频
     */
    private void lockOrUnlockFrontVideo() {
        if (!MyApp.isFrontLock) {
            MyApp.isFrontLock = true;
            speakVoice(getResources().getString(R.string.hint_video_lock_front));
        } else {
            MyApp.isFrontLock = false;
            MyApp.isFrontLockSecond = false;
            speakVoice(getResources().getString(
                    R.string.hint_video_unlock_front));
        }
        setupFrontViews();
    }

    /**
     * 加锁或解锁视频
     */
    private void lockOrUnlockBackVideo() {
        if (!MyApp.isBackLock) {
            MyApp.isBackLock = true;
            speakVoice(getResources().getString(R.string.hint_video_lock_back));
        } else {
            MyApp.isBackLock = false;
            MyApp.isBackLockSecond = false;
            speakVoice(getResources()
                    .getString(R.string.hint_video_unlock_back));
        }
    }

    /**
     * 视频SD卡不存在提示
     */
    private void noVideoSDHint() {
        if (MyApp.isAccOn && !ClickUtil.isHintSleepTooQuick(3000)) {
            String strNoSD = getResources().getString(
                    R.string.hint_sd_record_not_exist);
            HintUtil.showToast(context, strNoSD);
            speakVoice(strNoSD);
        } else {
        }
    }

    /**
     * 拍照
     */
    public void takePhoto(boolean playSound) {
        if (!MyApp.isFrontRecording && !MyApp.isBackRecording
                && !StorageUtil.isFrontCardExist()) { // 判断SD卡2是否存在，需要耗费一定时间
            noVideoSDHint(); // SDCard不存在
        } else if (recorderFront != null) {
            setFrontDirectory(); // 设置保存路径，否则会保存到内部存储
            HintUtil.playAudio(getApplicationContext(),
                    com.tchip.tachograph.TachographCallback.FILE_TYPE_IMAGE,
                    playSound);
            recorderFront.takePicture();
        }
    }

    class ReleaseFrontStorageThread implements Runnable {

        @Override
        public void run() {
            StorageUtil.releaseFrontStorage(context);
        }

    }

    class ReleaseBackStorageThread implements Runnable {

        @Override
        public void run() {
            StorageUtil.releaseBackStorage(context);
        }

    }

    /**
     * 前录:切换分辨率
     */
    private class ChangeFrontSizeThread implements Runnable {

        @Override
        public void run() {
            releaseFrontRecorder();
            closeFrontCamera();
            if (openFrontCamera()) {
                setupFrontRecorder();
            }
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setupFrontViews();
                }
            });
            if (MyApp.shouldVideoRecordWhenChangeSize) { // 修改分辨率后按需启动录像
                MyApp.shouldVideoRecordWhenChangeSize = false;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                startFrontRecord();
            }
        }

    }

    private class StartFrontRecordThread implements Runnable {

        @Override
        public void run() {
            if (!isFrontRecord() && MyApp.isFrontPreview
                    && recorderFront != null) {
                MyLog.d("Front.Record Start");

                /** 1.设置保存路径 */
                setFrontDirectory();

                /** 2.是否录制声音 */
                if (sharedPreferences.getBoolean("videoMute",
                        Constant.Record.muteDefault)) {
                    setFrontMute(true, false); // 设置录像静音
                } else {
                    setFrontMute(false, false);
                }

                final boolean isDeleteFrontSuccess = StorageUtil.releaseFrontStorage(context);
                if (isDeleteFrontSuccess) {

                    /** start()开始录制 */
                    if (MyApp.isFrontPreview && recorderFront.start() == 0) {
                        mMainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                resetFrontTimeText();

                                /** 播放提示音 */
                                HintUtil.playAudio(
                                        getApplicationContext(),
                                        com.tchip.tachograph.TachographCallback.FILE_TYPE_VIDEO,
                                        MyApp.isAccOn);

                                setFrontState(true);
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 停止录像x5
     */
    private void stopFrontRecorder5Times() {
        if (isFrontRecord()) {
            new Thread(new StopFrontRecordThread()).start();
            textFrontTime.setVisibility(View.INVISIBLE);
            resetFrontTimeText();
        }
    }

    private class StopFrontRecordThread implements Runnable {

        @Override
        public void run() {
            if (isFrontRecord()) {
                try {
                    int tryTime = 0;
                    while (stopFrontRecorder() != 0 && tryTime < 5) { // 停止录像
                        tryTime++;
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ProviderUtil.setValue(context, Name.REC_FRONT_STATE, "0");
                    mMainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            setFrontState(false);
                        }
                    });
                }
            }
            // 处理停车录像过程中，拔卡停止录像或者手动停止录像情况
            if (!MyApp.isAccOn && !MyApp.isBackRecording) {
                if (!MyApp.isParkRecording) {
                } else {
                    MyApp.isParkRecording = false;
                    ProviderUtil.setValue(context, Name.PARK_REC_STATE, "0");
                }
                new Thread(new CloseRecordThread()).start();
            }
        }

    }

    /**
     * 检查并删除异常视频文件：SD存在但数据库中不存在的文件
     */
    private void StartCheckErrorFileThread() {
        MyLog.v("CheckErrorFile.");
        if (!isVideoChecking) {
            new Thread(new CheckVideoThread()).start();
        }
        if (Constant.Record.flashToCard) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!MyApp.isFlashCleanDialogShow
                            && FileUtil.isFlashStorageLess()) {
                        FileUtil.showFlashCleanDialog(context);
                        Flash2SDUtil.deleteFlashDotFileForcely();
                    }
                }
            });
        }
    }

    /**
     * 当前是否正在校验错误视频
     */
    private boolean isVideoChecking = false;

    private class CheckVideoThread implements Runnable {

        @Override
        public void run() {
            isVideoChecking = true;
            File dirRecord = new File(Constant.Path.RECORD_DIRECTORY);
            StorageUtil.RecursionCheckDotFile(MainActivity.this, dirRecord);
            isVideoChecking = false;
        }

    }

    private void sendKeyCode(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    MyLog.e("Exception when sendPointerSync:" + e.toString());
                }
            }
        }.start();
    }

    class FrontCallBack implements Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            MyLog.v("Front.surfaceChanged");
            // surfaceHolder = holder;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            MyLog.v("Front.surface Created");

            surfaceHolderFront = holder;

            //openFrontCameraAndPreviewOnSurface();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            MyLog.v("Front.surface Destroyed");
        }

    }

    /**
     * 绘制录像按钮
     */
    private void setupFrontViews() {
        // 视频分辨率
        if (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_720P) {
            imageVideoSize.setImageDrawable(getResources().getDrawable(
                    R.drawable.video_size_hd, null));
            textVideoSize.setText(getResources().getString(
                    R.string.icon_hint_720p));
        } else if (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
            imageVideoSize.setImageDrawable(getResources().getDrawable(
                    R.drawable.video_size_fhd, null));
            textVideoSize.setText(getResources().getString(
                    R.string.icon_hint_1080p));
        }
        // 录像按钮
        imageFrontState.setImageDrawable(getResources().getDrawable(
                MyApp.isFrontRecording ? R.drawable.video_stop
                        : R.drawable.video_start, null));
        // 视频分段
        if (intervalState == Constant.Record.STATE_INTERVAL_1MIN) {
            imageVideoLength.setImageDrawable(getResources().getDrawable(
                    R.drawable.video_length_1m, null));
            textVideoLength.setText(getResources().getString(
                    R.string.icon_hint_1_minute));
        } else if (intervalState == Constant.Record.STATE_INTERVAL_3MIN) {
            imageVideoLength.setImageDrawable(getResources().getDrawable(
                    R.drawable.video_length_3m, null));
            textVideoLength.setText(getResources().getString(
                    R.string.icon_hint_3_minutes));
        }
        // 视频加锁
        imageFrontLock.setImageDrawable(getResources().getDrawable(
                MyApp.isFrontLock ? R.drawable.video_lock
                        : R.drawable.video_unlock, null));
        textFrontLock.setText(getResources().getString(
                MyApp.isFrontLock ? R.string.icon_hint_lock
                        : R.string.icon_hint_unlock));
        // 静音按钮
        boolean videoMute = sharedPreferences.getBoolean("videoMute",
                Constant.Record.muteDefault);
        muteState = videoMute ? Constant.Record.STATE_MUTE
                : Constant.Record.STATE_UNMUTE;
        imageVideoMute.setImageDrawable(getResources().getDrawable(
                videoMute ? R.drawable.video_mute : R.drawable.video_unmute,
                null));
        textVideoMute.setText(getResources()
                .getString(
                        videoMute ? R.string.icon_hint_mute
                                : R.string.icon_hint_unmute));
    }

    /**
     * 启动录像
     */
    private void startRecordFront() {
        try {
            if (!isFrontRecord()) {
                if (!MyApp.isAccOn) {
                    if (MyApp.isParkRecording) {
                        StartCheckErrorFileThread();
                        startFrontRecord();
                    } else if (!ClickUtil.isHintSleepTooQuick(3000)) {
                        HintUtil.showToast(MainActivity.this, getResources()
                                .getString(R.string.hint_stop_record_sleeping));
                    }
                } else {
                    StartCheckErrorFileThread();
                    startFrontRecord();
                }
            } else {
                MyLog.v("Front.startRecord.Already record yet");
            }
            setupFrontViews();
            MyLog.v("MyApp.isFrontReording:" + MyApp.isFrontRecording);
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e("Front.startRecord catch exception: " + e.toString());
        }
    }


    /**
     * 开启录像
     *
     * @return 0:成功 -1:失败
     */
    public void startFrontRecord() {
        new Thread(new StartFrontRecordThread()).start();
    }

    /**
     * 打开摄像头
     *
     * @return
     */
    private boolean openFrontCamera() {
        if (cameraFront != null) {
            closeFrontCamera();
        }
        try {
            MyLog.v("Front.Open Camera 0");
            cameraFront = Camera.open(0);
            previewFrontCamera();
            return true;
        } catch (Exception ex) {
            closeFrontCamera();
            MyLog.e("Front.openCamera:Catch Exception!");
            return false;
        }
    }

    /**
     * Camera预览：
     * <p>
     * lock > setPreviewDisplay > startPreview > unlock
     */
    private void previewFrontCamera() {
        MyLog.v("Front.preview Camera");
        try {
            cameraFront.lock();
            if (Constant.Module.useSystemCameraParam) { // 设置系统Camera参数
                Camera.Parameters para = cameraFront.getParameters();
                para.unflatten(Constant.Record.CAMERA_PARAMS);
                cameraFront.setParameters(para);
            }
            cameraFront.setPreviewDisplay(surfaceHolderFront);
            // camera.setDisplayOrientation(180);
            cameraFront.startPreview();
            cameraFront.unlock();
        } catch (Exception e) {
            MyApp.isFrontPreview = false;
            e.printStackTrace();
        } finally {
            MyApp.isFrontPreview = true;
        }
    }

    /**
     * 关闭Camera
     * <p>
     * lock > stopPreview > setPreviewDisplay > release > unlock
     */
    private boolean closeFrontCamera() {
        MyLog.v("Front.Close Camera");
        if (cameraFront == null)
            return true;
        try {
            cameraFront.lock();
            cameraFront.stopPreview();
            cameraFront.setPreviewDisplay(null);
            cameraFront.release();
            cameraFront.unlock();
            cameraFront = null;
            return true;
        } catch (Exception e) {
            cameraFront = null;
            MyLog.e("Front.closeCamera:Catch Exception:" + e.toString());
            return false;
        }
    }

    /**
     * 设置当前录像状态
     */
    private void setFrontState(boolean isVideoRecord) {
        ProviderUtil.setValue(context, Name.REC_FRONT_STATE,
                isVideoRecord ? "1" : "0");
        if (isVideoRecord) {
            if (!MyApp.isFrontRecording) {
                MyApp.isFrontRecording = true;
                textFrontTime.setVisibility(View.VISIBLE);
                startUpdateFrontTimeThread();
                setupFrontViews();
            }
        } else {
            if (MyApp.isFrontRecording) {
                MyApp.isFrontRecording = false;
                textFrontTime.setVisibility(View.INVISIBLE);
                resetFrontTimeText();
                MyApp.isUpdateFrontTimeRun = false;
                setupFrontViews();
            }
        }
    }

    private int secondFrontCount = -1;

    /**
     * 录制时间秒钟复位:
     * <p>
     * 1.停止录像{stopRecorder()}
     * <p>
     * 2.录像过程中更改录像分辨率
     * <p>
     * 3.录像过程中更改静音状态
     * <p>
     * 4.视频保存失败{onError(int)}
     * <p>
     * 5.开始录像{startRecordTask()}
     */
    private void resetFrontTimeText() {
        secondFrontCount = -1;
        textFrontTime.setText("00 : 00");
    }

    private int secondBackCount = -1;

    /**
     * 开启录像跑秒线程
     */
    private void startUpdateFrontTimeThread() {
        if (!MyApp.isUpdateFrontTimeRun) {
            new Thread(new UpdateFrontTimeThread()).start(); // 更新录制时间
        } else {
            MyLog.e("Front.UpdateRecordTimeThread already run");
        }
    }

    public class UpdateFrontTimeThread implements Runnable {

        @Override
        public void run() {
            // 解决录像时，快速点击录像按钮两次，线程叠加跑秒过快的问题
            do {
                MyApp.isUpdateFrontTimeRun = true;
                if (MyApp.isFrontCrashed) {
                    Message messageVideoLock = new Message();
                    messageVideoLock.what = 4;
                    updateFrontTimeHandler.sendMessage(messageVideoLock);
                }
                if (MyApp.isAppException) { // 程序异常,停止录像
                    MyApp.isAppException = false;
                    MyLog.e("Front.App exception, stop record!");
                    Message messageException = new Message();
                    messageException.what = 8;
                    updateFrontTimeHandler.sendMessage(messageException);
                    return;
                } else if (MyApp.isVideoCardEject) { // 录像时视频SD卡拔出停止录像
                    MyLog.e("Front.SD card remove badly or power unconnected, stop record!");
                    Message messageEject = new Message();
                    messageEject.what = 2;
                    updateFrontTimeHandler.sendMessage(messageEject);
                    return;
                } else if (MyApp.isVideoCardFormat) { // 录像SD卡格式化
                    MyApp.isVideoCardFormat = false;
                    MyLog.e("Front.SD card is format, stop record!");
                    Message messageFormat = new Message();
                    messageFormat.what = 7;
                    updateFrontTimeHandler.sendMessage(messageFormat);
                    return;
                } else if (MyApp.isGoingShutdown) {
                    MyApp.isGoingShutdown = false;
                    MyLog.e("Front.Going shutdown, stop record!");
                    Message messageFormat = new Message();
                    messageFormat.what = 9;
                    updateFrontTimeHandler.sendMessage(messageFormat);
                    return;
                } else if (MyApp.shouldStopFrontFromVoice) {
                    MyApp.shouldStopFrontFromVoice = false;
                    Message messageStopRecordFromVoice = new Message();
                    messageStopRecordFromVoice.what = 6;
                    updateFrontTimeHandler
                            .sendMessage(messageStopRecordFromVoice);
                    return;
                } else if (!MyApp.isAccOn && !MyApp.isParkRecording) { // ACC下电停止录像
                    MyLog.e("Front.Stop Record:isSleeping = true");
                    Message messageSleep = new Message();
                    messageSleep.what = 5;
                    updateFrontTimeHandler.sendMessage(messageSleep);
                    return;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message messageSecond = new Message();
                    messageSecond.what = 1;
                    updateFrontTimeHandler.sendMessage(messageSecond);
                }
            } while (MyApp.isFrontRecording);
            MyApp.isUpdateFrontTimeRun = false;
        }

    }

    final Handler updateFrontTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.removeMessages(1);
                    if (!ClickUtil.isPlusFrontTimeTooQuick(900)) {
                        secondFrontCount++;
                        if (MyApp.isFrontRecording && secondFrontCount % 10 == 0) {
                            acquirePartialWakeLock(10 * 1000);
                            if (secondFrontCount % 30 == 0) {
                                ProviderUtil.setValue(context,
                                        Name.REC_FRONT_STATE, "1");
                            }
                        }
                    }

                    if (!MyApp.isAccOn) { // 处理停车守卫录像
                        if (MyApp.isParkRecording) {
                            if (MyApp.isFrontRecording
                                    && secondFrontCount == Constant.Record.parkVideoLength) {
                                String videoTimeStr = sharedPreferences.getString(
                                        "videoTime", "1");
                                intervalState = "3".equals(videoTimeStr) ? Constant.Record.STATE_INTERVAL_3MIN
                                        : Constant.Record.STATE_INTERVAL_1MIN;

                                MyLog.v("Front.updateFrontTimeHandler.Stop Park Record");
                                stopFrontRecorder5Times(); // 停止录像
//                                stopBackRecorder5Times();
                                SettingUtil.setAirplaneMode(context, true);
                                setRecordInterval(("3".equals(videoTimeStr)) ? 3 * 60
                                        : 1 * 60); // 重设视频分段
                                ProviderUtil.setValue(context, Name.PARK_REC_STATE,
                                        "0");
                                MyApp.isParkRecording = false;
                            }
                        } else {
                        }
                    }
                    switch (intervalState) { // 重置时间
                        case Constant.Record.STATE_INTERVAL_3MIN:
                            if (secondFrontCount >= 180) {
                                secondFrontCount = 0;
                            }
                            break;

                        case Constant.Record.STATE_INTERVAL_1MIN:
                            if (secondFrontCount >= 60) {
                                secondFrontCount = 0;
                            }
                            break;

                        default:
                            break;
                    }
                    textFrontTime.setText(DateUtil
                            .getFormatTimeBySecond(secondFrontCount));
                    this.removeMessages(1);
                    break;

                case 2:// SD卡异常移除：停止录像
                    this.removeMessages(2);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 2,Video SD Removed");
                    stopFrontRecorder5Times();
                    hintCardEject();
                    this.removeMessages(2);
                    break;

                case 4:
                    this.removeMessages(4);
                    MyApp.isFrontCrashed = false;
                    setupFrontViews();
                    // 碰撞后判断是否需要加锁第二段视频
                    if (intervalState == Constant.Record.STATE_INTERVAL_1MIN) {
                        if (secondFrontCount > 45) {
                            MyApp.isFrontLockSecond = true;
                        }
                    } else if (intervalState == Constant.Record.STATE_INTERVAL_3MIN) {
                        if (secondFrontCount > 165) {
                            MyApp.isFrontLockSecond = true;
                        }
                    }
                    this.removeMessages(4);
                    break;

                case 5: // 进入休眠，停止录像
                    this.removeMessages(5);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 5");
                    stopFrontRecorder5Times();
                    this.removeMessages(5);
                    break;

                case 6: // 语音命令：停止录像
                    this.removeMessages(6);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 6");
                    stopFrontRecorder5Times();
                    this.removeMessages(6);
                    break;

                case 7:
                    this.removeMessages(7);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 7");
                    stopFrontRecorder5Times();
                    String strVideoCardFormat = getResources().getString(
                            R.string.hint_sd2_format);
                    HintUtil.showToast(MainActivity.this, strVideoCardFormat);
                    speakVoice(strVideoCardFormat);
                    this.removeMessages(7);
                    break;

                case 8: // 程序异常，停止录像
                    this.removeMessages(8);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 8");
                    stopFrontRecorder5Times();
                    this.removeMessages(8);
                    break;

                case 9: // 系统关机，停止录像
                    this.removeMessages(9);
                    MyLog.v("Front.UpdateRecordTimeHandler.stopRecorder() 9");
                    stopFrontRecorder5Times();
                    String strGoingShutdown = getResources().getString(
                            R.string.hint_going_shutdown);
                    HintUtil.showToast(MainActivity.this, strGoingShutdown);
                    // speakVoice(strGoingShutdown);
                    this.removeMessages(9);
                    break;

                default:
                    break;
            }
        }
    };

    class CloseRecordThread implements Runnable {

        @Override
        public void run() {
            sendHomeKey();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!MyApp.isAccOn) {
                killAutoRecord();
                sendBroadcast(new Intent(Constant.Broadcast.KILL_APP).putExtra(
                        "name", "com.tchip.autorecord"));
            }
        }

    }

    /**
     * 更改录音/静音状态后重启录像
     */
    public class StartRecordWhenChangeMuteThread implements Runnable {

        @Override
        public void run() {
            if (isFrontRecord()) {
                if (stopFrontRecorder() == 0) { // 停止录像
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setFrontState(false);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!MyApp.isFrontRecording && recorderFront != null) {
                        MyLog.d("StartRecordWhenChangeMuteThread.Record Start");
                        setFrontDirectory(); // 设置保存路径
                        // 开始录像前设置静音，过程中无法设置
                        if (muteState == Constant.Record.STATE_UNMUTE) {
                            if (recorderFront != null) {
                                recorderFront.setMute(false);
                            }
                        } else if (muteState == Constant.Record.STATE_MUTE) {
                            if (recorderFront != null) {
                                recorderFront.setMute(true);
                            }
                        }
                        if (MyApp.isFrontPreview && recorderFront.start() == 0) {
                            mMainHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    resetFrontTimeText();
                                    HintUtil.playAudio(
                                            getApplicationContext(),
                                            com.tchip.tachograph.TachographCallback.FILE_TYPE_VIDEO,
                                            MyApp.isAccOn);
                                    setFrontState(true);
                                }
                            });
                        }
                    }
                }
            }
        }

    }

    private void initialSurface() {
        MyLog.v("initialFrontSurface");
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setOnClickListener(new MyOnClickListener());
        surfaceView.getHolder().addCallback(new FrontCallBack());
    }

    /**
     * 设置保存路径
     */
    public int setFrontDirectory() {
        if (recorderFront != null) {
            return recorderFront
                    .setDirectory(Constant.Record.flashToCard ? Constant.Path.SDCARD_0
                            : Constant.Path.SDCARD_1);
        }
        return -1;
    }

    /**
     * 设置录像静音，需要已经初始化recorderFront
     */
    private int setFrontMute(boolean mute, boolean speakVoice) {
        int result = -1;
        if (recorderFront != null) {
            if (speakVoice) {
                speakVoice(getResources().getString(
                        mute ? R.string.hint_video_mute_on
                                : R.string.hint_video_mute_off));
            }
            editor.putBoolean("videoMute", mute);
            editor.commit();
            muteState = mute ? Constant.Record.STATE_MUTE
                    : Constant.Record.STATE_UNMUTE;
            result = recorderFront.setMute(mute);
        }
        MyLog.v("setFrontMute:" + result);
        return result;
    }

    /**
     * 重置预览区域
     */
    private void recreateFrontCameraZone() {
        if (cameraFront == null) {
            // surfaceHolder = holder;
            releaseFrontRecorder();
            closeFrontCamera();
            if (openFrontCamera()) {
                setupFrontRecorder();
            }
        } else {
            try {
                cameraFront.lock();
                cameraFront.setPreviewDisplay(surfaceHolderFront);
                cameraFront.startPreview();
                cameraFront.unlock();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    /**
     * 关闭录像程序
     */
    private void killAutoRecord() {
        if (0 == SettingUtil.getAccStatus()) {
            releaseFrontCameraZone();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 关闭录像程序
     */
    private void killAutoRecordForTest() {
        // Reset Record State
        ProviderUtil.setValue(context, Name.REC_FRONT_STATE, "0");
        ProviderUtil.setValue(context, Name.REC_BACK_STATE, "0");
        releaseFrontCameraZone();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /**
     * 释放Camera
     */
    private void releaseFrontCameraZone() {
        if (!MyApp.isAccOn) {
            // 释放录像区域
            releaseFrontRecorder();
            closeFrontCamera();
            // surfaceHolder = null;
            if (cameraFront != null) {
                cameraFront.stopPreview();
            }
            MyLog.v("Front.releaseCameraZone");
            MyApp.isFrontPreview = false;
        }
    }

    /**
     * 设置录制初始值
     */
    private void setupFrontDefaults() {
        refreshFrontButton();

        MyApp.isFrontRecording = false;

        // 录音,静音;默认录音
        boolean videoMute = sharedPreferences.getBoolean("videoMute",
                Constant.Record.muteDefault);
        muteState = videoMute ? Constant.Record.STATE_MUTE
                : Constant.Record.STATE_UNMUTE;
    }

    /**
     * 设置录制初始值
     */
    private void setupBackDefaults() {
        MyApp.isBackRecording = false;
    }

    private void refreshFrontButton() {
        String videoSizeStr = sharedPreferences.getString("videoSize", "720");
        MyApp.resolutionState = "1080".equals(videoSizeStr) ? Constant.Record.STATE_RESOLUTION_1080P
                : Constant.Record.STATE_RESOLUTION_720P;

        String videoTimeStr = sharedPreferences.getString("videoTime", "1"); // 视频分段
        intervalState = "3".equals(videoTimeStr) ? Constant.Record.STATE_INTERVAL_3MIN
                : Constant.Record.STATE_INTERVAL_1MIN;
    }

    /**
     * 设置分辨率
     */
    public void setFrontResolution(int state) {
        if (state != MyApp.resolutionState) {
            MyApp.resolutionState = state;
            new Thread(new ChangeFrontSizeThread()).start();
        }
    }

    /**
     * 设置视频分段:前置后置
     */
    public int setRecordInterval(int seconds) {
        return (recorderFront != null) ? recorderFront.setVideoSeconds(seconds)
                : -1;
    }

    /**
     * 设置视频重叠
     */
    public int setFrontOverlap(int seconds) {
        return (recorderFront != null) ? recorderFront.setVideoOverlap(seconds)
                : -1;
    }

    /**
     * 停止录像
     */
    public int stopFrontRecorder() {
        if (recorderFront != null) {
            MyLog.d("Front.StopRecorder");
            // 停车守卫不播放声音
            HintUtil.playAudio(getApplicationContext(),
                    com.tchip.tachograph.TachographCallback.FILE_TYPE_VIDEO,
                    MyApp.isAccOn);
            return recorderFront.stop();
        }
        return -1;
    }

    private void setupFrontRecorder() {
        MyLog.v("Front.SetupRecorder");
        releaseFrontRecorder();
        try {
            recorderFront = new TachographRecorder();
            recorderFront.setTachographCallback(new FrontTachographCallback());
            recorderFront.setCamera(cameraFront);
            // 前缀，后缀
            recorderFront.setMediaFilenameFixs(
                    TachographCallback.FILE_TYPE_VIDEO, "", "_0");
            recorderFront.setMediaFilenameFixs(
                    TachographCallback.FILE_TYPE_SHARE_VIDEO, "", "");
            recorderFront.setMediaFilenameFixs(
                    TachographCallback.FILE_TYPE_IMAGE, "", "");
            // 路径
            recorderFront.setMediaFileDirectory(
                    TachographCallback.FILE_TYPE_VIDEO, "VideoFront");
            recorderFront.setMediaFileDirectory(
                    TachographCallback.FILE_TYPE_SHARE_VIDEO, "Share");
            recorderFront.setMediaFileDirectory(
                    TachographCallback.FILE_TYPE_IMAGE, "Image");
            recorderFront.setClientName(this.getPackageName());
            if (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_1080P) {
                recorderFront.setVideoSize(1920, 1080); // 分辨率
                recorderFront
                        .setVideoFrameRate(Constant.Record.FRONT_FRAME_1080P);
                recorderFront
                        .setVideoBiteRate(Constant.Record.FRONT_BITRATE_1080P);
            } else {
                recorderFront.setVideoSize(1280, 720);
                recorderFront
                        .setVideoFrameRate(Constant.Record.FRONT_FRAME_720P);
                recorderFront
                        .setVideoBiteRate(Constant.Record.FRONT_BITRATE_720P);
            }
            if (intervalState == Constant.Record.STATE_INTERVAL_3MIN) { // 分段
                recorderFront.setVideoSeconds(3 * 60);
            } else {
                recorderFront.setVideoSeconds(1 * 60);
            }
            recorderFront.setVideoOverlap(0); // 重叠
            if (null != sharedPreferences) { // 录音
                recorderFront.setMute(sharedPreferences.getBoolean("videoMute",
                        Constant.Record.muteDefault));
            } else {
                recorderFront.setMute(true);
            }
            recorderFront.setAudioSampleRate(48000);
            recorderFront.prepare();
        } catch (Exception e) {
            MyLog.e("setupRecorder: Catch Exception：" + e.toString());
        }
    }

    /**
     * 释放Recorder
     */
    private void releaseFrontRecorder() {
        MyLog.v("release Front Recorder");
        try {
            if (recorderFront != null) {
                recorderFront.stop();
                ProviderUtil.setValue(context, Name.REC_FRONT_STATE, "0");
                recorderFront.close();
                recorderFront.release();
                recorderFront = null;
                MyLog.d("Record Release");
            }
        } catch (Exception e) {
            MyLog.e("releaseRecorder: Catch Exception!");
        }
    }

    class FrontTachographCallback implements TachographCallback {

        @Override
        public void onError(int error) {
            switch (error) {
                case TachographCallback.ERROR_SAVE_VIDEO_FAIL:
                    String strSaveVideoErr = getResources().getString(
                            R.string.hint_save_video_error);
                    HintUtil.showToast(MainActivity.this, strSaveVideoErr);
                    MyLog.e("Front Record Error : ERROR_SAVE_VIDEO_FAIL");
                    // 视频保存失败，原因：存储空间不足，清空文件夹，视频被删掉
                    // resetRecordTimeText();
                    // MyLog.v("[onError]stopRecorder()");
                    // if (stopRecorder() == 0) {
                    // setRecordState(false);
                    // }
                    speakVoice(strSaveVideoErr);
                    StartCheckErrorFileThread();
                    break;

                case TachographCallback.ERROR_SAVE_IMAGE_FAIL:
                    HintUtil.showToast(MainActivity.this,
                            getResources()
                                    .getString(R.string.hint_save_photo_error));
                    MyLog.e("Front Record Error : ERROR_SAVE_IMAGE_FAIL");
                    StartCheckErrorFileThread();
                    break;

                case TachographCallback.ERROR_RECORDER_CLOSED:
                    MyLog.e("Front Record Error : ERROR_RECORDER_CLOSED");
                    break;

                default:
                    break;
            }
        }

        /**
         * 文件保存回调，注意：存在延时，不能用作重置录像跑秒时间
         *
         * @param type 0-图片 1-视频
         * @param path 视频：/storage/sdcard1/DrivingRecord/VideoFront/2016-05-
         *             04_155010_0.mp4
         *             图片:/storage/sdcard1/DrivingRecord/Image/2015-
         *             07-01_105536.jpg
         */
        @Override
        public void onFileSave(int type, String path) {
            try {
                if (type == 1) { // 视频
                    new Thread(new ReleaseFrontStorageThread()).start();

                    String videoName = path.split("/")[5];
                    int videoResolution = (MyApp.resolutionState == Constant.Record.STATE_RESOLUTION_720P) ? 720
                            : 1080;
                    int videoLock = 0;

                    if (MyApp.isFrontLock) {
                        videoLock = 1;
                        MyApp.isFrontLock = false; // 还原
                        StorageUtil.lockVideo(true, videoName);

                        if (MyApp.isFrontRecording && MyApp.isFrontLockSecond) {
                            MyApp.isFrontLock = true;
                            MyApp.isFrontLockSecond = false; // 不录像时修正加锁图标
                        }
                    }
                    Flash2SDUtil.moveVideoToSD(context, true, 1 == videoLock,
                            videoName);

                    setupFrontViews(); // 更新录制按钮状态
                    // DriveVideo driveVideo = new DriveVideo(videoName,
                    // videoLock, videoResolution, 0);
                    // frontVideoDb.addDriveVideo(driveVideo);

                    Flash2SDUtil.deleteFlashDotFile();
                    StartCheckErrorFileThread(); // 执行onFileSave时，此file已经不隐藏，下个正在录的为隐藏
                } else { // 图片
                    HintUtil.showToast(MainActivity.this, getResources()
                            .getString(R.string.hint_photo_save));

                    if (Constant.Record.flashToCard) {
                        String imageName = path.split("/")[5];
                        new Thread(new MoveImageThread(context, imageName))
                                .start();
                    } else {
                        new Thread(new WriteImageExifThread(path)).start();
                        if (MyApp.shouldSendPathToDSA) { // 停车守卫拍照
                            MyApp.shouldSendPathToDSA = false;
                            String[] picPaths = new String[2]; // 第一张保存前置的图片路径
                            picPaths[0] = path;
                            picPaths[1] = "";
                            Intent intent = new Intent(
                                    Constant.Broadcast.SEND_PIC_PATH);
                            intent.putExtra("picture", picPaths);
                            sendBroadcast(intent);
                            MyLog.v("SendDSA,Path:" + picPaths[0]);
                        }

                        if (MyApp.shouldSendPathToDSAUpload) { // 语音拍照上传
                            MyApp.shouldSendPathToDSAUpload = false;
                            Intent intentDsaUpload = new Intent(
                                    Constant.Broadcast.SEND_DSA_UPLOAD_PATH);
                            intentDsaUpload.putExtra("share_picture", path);
                            sendBroadcast(intentDsaUpload);
                            MyLog.v("SendDSAUpload,Path:" + path);
                        }
                        // 通知语音
                        Intent intentImageSave = new Intent(
                                Constant.Broadcast.ACTION_IMAGE_SAVE);
                        intentImageSave.putExtra("path", path);
                        sendBroadcast(intentImageSave);
                    }
                }
                // 更新Media Database
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"
                                + (Constant.Record.flashToCard ? path.replace(
                                "sdcard0", "sdcard1") : path))));
                MyLog.d("Front.onFileSave.Type="
                        + type
                        + ",Save path:"
                        + (Constant.Record.flashToCard ? path.replace(
                        "sdcard0", "sdcard1") : path));
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.e("Front.onFileSave.catch Exception:" + e.toString());
            }
        }

        @Override
        public void onFileStart(int type, String path) {
            if (type == 1) {
                MyApp.nowRecordVideoName = path.split("/")[5];
                Flash2SDUtil.moveOldFrontVideoToSD();
            }
            Flash2SDUtil.moveOldImageToSD();
            MyLog.v("Front.onFileStart.Path:" + path);
        }

    }

    private void hintCardEject() {
        if (!ClickUtil.isHintSdEjectTooQuick(2000)) {
            String strVideoCardEject = getResources().getString(
                    R.string.hint_sd_remove_badly);
            HintUtil.showToast(MainActivity.this, strVideoCardEject);
            // speakVoice(strVideoCardEject);
            MyLog.e("showCardEjectMessage");
        }
    }

    public boolean isFrontRecord() {
        if (recorderFront != null) {
            int intFrontRecording = recorderFront.isRecording();
            MyLog.d("Tachograph.isFrontRecord:" + intFrontRecording);
            return 1 == intFrontRecording;
        } else
            return false;
    }

}
