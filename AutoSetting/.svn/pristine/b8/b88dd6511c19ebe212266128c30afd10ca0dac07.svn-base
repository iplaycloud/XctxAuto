package com.tchip.autosetting.ui;

import java.util.Calendar;

import com.tchip.autosetting.Constant;
import com.tchip.autosetting.R;
import com.tchip.autosetting.Constant.Setting;
import com.tchip.autosetting.util.HintUtil;
import com.tchip.autosetting.util.MyLog;
import com.tchip.autosetting.util.OpenUtil;
import com.tchip.autosetting.util.ProviderUtil;
import com.tchip.autosetting.util.ProviderUtil.Name;
import com.tchip.autosetting.util.SettingUtil;
import com.tchip.autosetting.util.TelephonyUtil;
import com.tchip.autosetting.util.OpenUtil.MODULE_TYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class QuickSettingActivity extends Activity {
	private Context context;

	private AudioManager audioManager;
	private WifiManager wifiManager;
	private int secondCount = 1;

	private ImageView imageWifi;
	private ImageView imageData;
	private ImageView imageBluetooth;
	private ImageView imageLocation;
	private ImageView imageAirplane;
	private ImageView imageSetting;
	private ImageView imageBrightness;
	private SeekBar seekBarBright;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_quick_setting);

		initialLayout();
		new Thread(new AutoFinishThread()).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		MyOnLongClickListener myOnLongClickListener = new MyOnLongClickListener();
		// 亮度SeekBar
		imageBrightness = (ImageView) findViewById(R.id.imageBrightness);
		imageBrightness.setOnClickListener(myOnClickListener);
		seekBarBright = (SeekBar) findViewById(R.id.seekBarBright);
		if (Constant.Module.hasAutoLight) {
			String strAutoLight = ProviderUtil.getValue(context,
					Name.SET_AUTO_LIGHT_STATE, "0");
			if ("1".equals(strAutoLight)) {
				seekBarBright.setEnabled(false);
				imageBrightness.setImageDrawable(getResources().getDrawable(
						R.drawable.quick_setting_brightness_auto, null));
			} else {
				imageBrightness.setImageDrawable(getResources().getDrawable(
						R.drawable.quick_setting_brightness, null));
				seekBarBright.setEnabled(true);
			}
		}

		seekBarBright.setMax(Constant.Setting.MAX_BRIGHTNESS);
		seekBarBright.setProgress(SettingUtil.getBrightness(context));
		seekBarBright.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SettingUtil.setBrightness(context, seekBar.getProgress(), true);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				secondCount = 1;
				SettingUtil.setBrightness(context, progress, true);
			}
		});

		// 媒体音量SeekBar
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		SeekBar seekBarVolume = (SeekBar) findViewById(R.id.seekBarVolume);
		seekBarVolume.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekBarVolume.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						seekBar.getProgress(), 0);
				audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
						seekBar.getProgress(), 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				secondCount = 1;
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
				audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
						progress, 0);
			}
		});

		imageWifi = (ImageView) findViewById(R.id.imageWifi);
		imageWifi.setOnClickListener(myOnClickListener);
		imageWifi.setOnLongClickListener(myOnLongClickListener);
		imageData = (ImageView) findViewById(R.id.imageData);
		imageData.setOnClickListener(myOnClickListener);
		imageData.setOnLongClickListener(myOnLongClickListener);
		imageBluetooth = (ImageView) findViewById(R.id.imageBluetooth);
		imageBluetooth.setOnClickListener(myOnClickListener);
		imageBluetooth.setOnLongClickListener(myOnLongClickListener);
		imageLocation = (ImageView) findViewById(R.id.imageLocation);
		imageLocation.setOnClickListener(myOnClickListener);
		imageAirplane = (ImageView) findViewById(R.id.imageAirplane);
		imageAirplane.setOnClickListener(myOnClickListener);
		imageSetting = (ImageView) findViewById(R.id.imageSetting);
		imageSetting.setOnClickListener(myOnClickListener);

		updateIconState();
	}

	private void updateIconState() {
		// Wi-Fi
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		imageWifi.setImageDrawable(getResources().getDrawable(
				wifiManager.isWifiEnabled() ? R.drawable.quick_setting_wifi_on
						: R.drawable.quick_setting_wifi_off, null));
		// 数据流量
		if (TelephonyUtil.isAirplaneModeOn(context)) {
			imageData.setImageDrawable(getResources().getDrawable(
					R.drawable.quick_setting_data_off, null));
		} else {
			imageData
					.setImageDrawable(getResources()
							.getDrawable(
									TelephonyUtil.isMobileDataEnable(context) ? R.drawable.quick_setting_data_on
											: R.drawable.quick_setting_data_off,
									null));
		}
		// 蓝牙
		boolean isBluetoothEnable = "1".equals(ProviderUtil.getValue(context,
				Name.ACC_STATE, "0"))
				&& "1".equals(Settings.System.getString(getContentResolver(),
						"bt_enable"));
		imageBluetooth.setImageDrawable(getResources().getDrawable(
				isBluetoothEnable ? R.drawable.quick_setting_bluetooth_on
						: R.drawable.quick_setting_bluetooth_off, null));
		// GPS
		imageLocation.setImageDrawable(getResources().getDrawable(
				SettingUtil.isGpsOn(context) ? R.drawable.quick_setting_gps_on
						: R.drawable.quick_setting_gps_off, null));
		// Airplane Mode
		imageAirplane
				.setImageDrawable(getResources()
						.getDrawable(
								TelephonyUtil.isAirplaneModeOn(context) ? R.drawable.quick_setting_airplane_on
										: R.drawable.quick_setting_airplane_off,
								null));
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			secondCount = 1;
			switch (v.getId()) {
			case R.id.imageWifi:
				wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
				break;

			case R.id.imageData:
				if (TelephonyUtil.isAirplaneModeOn(context)) {
					HintUtil.showToast(
							context,
							getResources().getString(
									R.string.quick_setting_airplane_on));
				} else {
					TelephonyUtil.setMobileDataEnable(context,
							!TelephonyUtil.isMobileDataEnable(context));
				}
				break;

			case R.id.imageBluetooth:
				if ("1".equals(ProviderUtil.getValue(context, Name.ACC_STATE,
						"0"))) {
					sendBroadcast(new Intent(
							Constant.Broadcast.BT_STATUS_CHANGE));
				}
				break;

			case R.id.imageLocation:
				SettingUtil.setGpsOn(context, !SettingUtil.isGpsOn(context));
				break;

			case R.id.imageAirplane:
				TelephonyUtil.setAirplaneMode(context,
						!TelephonyUtil.isAirplaneModeOn(context));
				break;

			case R.id.imageSetting:
				finish();
				Intent intentSetting = new Intent(QuickSettingActivity.this,
						MainActivity.class);
				startActivity(intentSetting);
				break;

			case R.id.imageBrightness:
				if (Constant.Module.hasAutoLight) {
					String strAutoLight = ProviderUtil.getValue(context,
							Name.SET_AUTO_LIGHT_STATE, "0");
					if ("1".equals(strAutoLight)) {
						ProviderUtil.setValue(context,
								Name.SET_AUTO_LIGHT_STATE, "0");
						// SettingUtil.setAutoLight(false);
						seekBarBright.setEnabled(true);
						imageBrightness.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.quick_setting_brightness,
										null));
						SharedPreferences sharedPreferences = getSharedPreferences(
								Constant.MySP.NAME, Context.MODE_PRIVATE);
						int manulLightValue = sharedPreferences.getInt(
								"manulLightValue",
								Constant.Setting.DEFAULT_BRIGHTNESS);
						SettingUtil.setBrightness(getApplicationContext(),
								manulLightValue - 1, true);

						SettingUtil.setBrightness(getApplicationContext(),
								manulLightValue + 1, true);

						SettingUtil.setBrightness(getApplicationContext(),
								manulLightValue, true);
					} else {
						ProviderUtil.setValue(context,
								Name.SET_AUTO_LIGHT_STATE, "1");
						// SettingUtil.setAutoLight(true);
						Calendar calendar = Calendar.getInstance(); // 获取时间
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						if (hour >= 6 && hour < 18) {
							SettingUtil.setBrightness(getApplicationContext(),
									Setting.AUTO_BRIGHT_DAY - 1, false);
							SettingUtil.setBrightness(getApplicationContext(),
									Setting.AUTO_BRIGHT_DAY, false);
						} else {
							SettingUtil.setBrightness(getApplicationContext(),
									Setting.AUTO_BRIGHT_NIGHT + 1, false);
							SettingUtil.setBrightness(getApplicationContext(),
									Setting.AUTO_BRIGHT_NIGHT, false);
						}

						seekBarBright.setEnabled(false);
						imageBrightness
								.setImageDrawable(getResources()
										.getDrawable(
												R.drawable.quick_setting_brightness_auto,
												null));
					}
				}
				break;

			default:
				break;
			}
		}

	}

	class MyOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			secondCount = 1;
			switch (v.getId()) {
			case R.id.imageWifi:
				OpenUtil.openModule(QuickSettingActivity.this, MODULE_TYPE.WIFI);
				finish();
				break;

			case R.id.imageBluetooth:
				OpenUtil.openModule(QuickSettingActivity.this,
						MODULE_TYPE.DIALER);
				finish();
				break;

			case R.id.imageData:
				OpenUtil.openModule(QuickSettingActivity.this,
						MODULE_TYPE.DATA_USAGE);
				finish();
				break;

			default:
				break;
			}
			return false;
		}

	}

	/**
	 * 无操作3秒后关闭音量调节界面
	 */
	class AutoFinishThread implements Runnable {

		@Override
		public void run() {
			while (secondCount < 10) {
				try {
					Thread.sleep(1000);
					secondCount++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (secondCount >= 8) {
					Message messageFinish = new Message();
					messageFinish.what = 1;
					autoFinishHandler.sendMessage(messageFinish);
				} else {
					Message messageFinish = new Message();
					messageFinish.what = 2;
					autoFinishHandler.sendMessage(messageFinish);
				}
			}

		}
	}

	final Handler autoFinishHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				finish();
				break;

			case 2: // Update Icon
				updateIconState();
				break;

			default:
				break;
			}
		}
	};

}
