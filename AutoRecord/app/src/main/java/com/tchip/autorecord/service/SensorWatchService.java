package com.tchip.autorecord.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.R;
import com.tchip.autorecord.util.HintUtil;
import com.tchip.autorecord.util.MyLog;
import com.tchip.autorecord.util.SettingUtil;

/**
 * Created by AlexZhou on 2015/3/26. 11:06
 */
public class SensorWatchService extends Service {
	private final static int AXIS_X = 0;
	private final static int AXIS_Y = 1;
	private final static int AXIS_Z = 2;

	private float valueX = 0f;
	private float valueY = 0f;
	private float valueZ = 0f;

	private float LIMIT_X, LIMIT_Y, LIMIT_Z;

	private int[] crashFlag = { 0, 0, 0 }; // {X-Flag, Y-Flag, Z-Flag}
	private boolean isCrash = false;

	private SensorManager sensorManager;
	private SensorEventListener sensorEventListener;
	private Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		MyLog.v("SensorWatchService.onCreate");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Using TYPE_ACCELEROMETER first if exit, then TYPE_GRAVITY
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				if (MyApp.isAccOn) {
					if (MyApp.isCrashOn) {
						LIMIT_X = LIMIT_Y = LIMIT_Z = SettingUtil
								.getGravityVauleBySensitive(MyApp.crashSensitive);
						valueX = event.values[AXIS_X];
						valueY = event.values[AXIS_Y];
						valueZ = event.values[AXIS_Z];

						if (valueX > LIMIT_X || valueX < -LIMIT_X) {
							crashFlag[0] = 1;
							isCrash = true;
						}
						if (valueZ > LIMIT_Z || valueZ < -LIMIT_Z) {
							crashFlag[2] = 1;
							isCrash = true;
						}
						if (isCrash) {
							// 当前录制视频加锁
							if (MyApp.isFrontRecording && !MyApp.isFrontLock) {
								MyApp.isFrontLock = true;
								MyApp.isFrontCrashed = true;

								HintUtil.showToast(
										context,
										getResources().getString(
												R.string.hint_video_lock_front));
								MyLog.v("SensorWarchService.Crashed->FrontVideoLock;LIMIT:"
										+ LIMIT_X
										+ ",X:"
										+ valueX
										+ ",Y:"
										+ valueY + ",Z:" + valueZ);
							}

							if (MyApp.isBackRecording && !MyApp.isBackLock) {
								MyApp.isBackLock = true;
								MyApp.isBackCrashed = true;

								HintUtil.showToast(
										context,
										getResources().getString(
												R.string.hint_video_lock_back));
								MyLog.v("SensorWarchService.Crashed->BackVideoLock;LIMIT:"
										+ LIMIT_X
										+ ",X:"
										+ valueX
										+ ",Y:"
										+ valueY + ",Z:" + valueZ);
							}

							// 重置碰撞标志位
							isCrash = false;
						}
					}
				} else {
					stopSelf();
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		// 0:000000μs-SENSOR_DELAY_FASTEST
		// 1:020000μs-SENSOR_DELAY_GAME
		// 2:060000μs-SENSOR_DELAY_UI
		// 3:200000μs-SENSOR_DELAY_NORMAL
		// 尽量使用比较低的传感器采样率，这样可以让系统的负荷比较小，同时可以省电
		sensorManager.registerListener(sensorEventListener, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(sensorEventListener);
		MyLog.v("SensorWatchService.onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public String getTime() {
		long nowTime = System.currentTimeMillis();
		Date date = new Date(nowTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(date);
	}

	private void speakVoice(String content) {
		sendBroadcast(new Intent(Constant.Broadcast.TTS_SPEAK).putExtra(
				"content", content));
	}

	public float getValue(int axis) {
		switch (axis) {
		case AXIS_X:
			return valueX;

		case AXIS_Y:
			return valueY;

		case AXIS_Z:
			return valueZ;

		default:
			return 0f;
		}
	}
}
