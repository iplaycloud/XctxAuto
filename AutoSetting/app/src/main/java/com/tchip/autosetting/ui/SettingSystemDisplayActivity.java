package com.tchip.autosetting.ui;

import java.util.Calendar;

import com.tchip.autosetting.Constant;
import com.tchip.autosetting.Constant.Setting;
import com.tchip.autosetting.R;
import com.tchip.autosetting.util.MyLog;
import com.tchip.autosetting.util.ProviderUtil;
import com.tchip.autosetting.util.ProviderUtil.Name;
import com.tchip.autosetting.util.SettingUtil;
import com.tchip.autosetting.util.TypefaceUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class SettingSystemDisplayActivity extends Activity {

	private Switch nightSwitch;
	private Context context;
	private RadioGroup screenOffGroup;
	private RadioButton screenOff30Second, screenOff1min, screenOff2min,
			screenOff10min, screenOffNone;

	private SharedPreferences sharedPreferences;
	private Editor editor;

	private Switch switchAutolight;
	private SeekBar brightSeekBar;
	private RelativeLayout layoutSeekBar;
	private RelativeLayout layoutAutoLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting_system_display);

		context = getApplicationContext();

		sharedPreferences = getSharedPreferences(Constant.MySP.NAME,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		// 时钟
		TextClock textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setTypeface(TypefaceUtil.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		// 亮度SeekBar
		brightSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);
		brightSeekBar.setMax(Constant.Setting.MAX_BRIGHTNESS);
		brightSeekBar.setProgress(SettingUtil.getBrightness(context));
		brightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
				SettingUtil.setBrightness(context, progress, true);
			}
		});

		// 屏幕关闭RadioGroup
		iniRadioGroup();

		layoutAutoLight = (RelativeLayout) findViewById(R.id.layoutAutoLight);
		layoutAutoLight
				.setVisibility(Constant.Module.hasAutoLight ? View.VISIBLE
						: View.GONE);

		boolean isAutoLightSwitchOn = isAutoLightSwitchOn();
		layoutSeekBar = (RelativeLayout) findViewById(R.id.layoutSeekBar);
		hideOrShowSeekBarLayout(isAutoLightSwitchOn);

		// 亮度自动调节开关
		switchAutolight = (Switch) findViewById(R.id.switchAutolight);
		switchAutolight.setChecked(isAutoLightSwitchOn);
		switchAutolight
				.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						ProviderUtil.setValue(context,
								Name.SET_AUTO_LIGHT_STATE, isChecked ? "1"
										: "0");
						hideOrShowSeekBarLayout(isChecked);

						if (isChecked) {
							Calendar calendar = Calendar.getInstance(); // 获取时间
							int hour = calendar.get(Calendar.HOUR_OF_DAY);
							if (hour >= 6 && hour < 18) {
								SettingUtil.setBrightness(
										getApplicationContext(),
										Setting.AUTO_BRIGHT_DAY - 1, false);
								SettingUtil.setBrightness(
										getApplicationContext(),
										Setting.AUTO_BRIGHT_DAY, false);
							} else {
								SettingUtil.setBrightness(
										getApplicationContext(),
										Setting.AUTO_BRIGHT_NIGHT + 1, false);
								SettingUtil.setBrightness(
										getApplicationContext(),
										Setting.AUTO_BRIGHT_NIGHT, false);
							}
						} else { // 关闭自动亮度调节，重设亮度值
							int manulLightValue = sharedPreferences.getInt(
									"manulLightValue",
									Constant.Setting.DEFAULT_BRIGHTNESS);
							MyLog.v("SettingSystemDisplay.manulLightValue:"
									+ manulLightValue);
							SettingUtil.setBrightness(getApplicationContext(),
									manulLightValue - 1, true);

							SettingUtil.setBrightness(getApplicationContext(),
									manulLightValue + 1, true);

							SettingUtil.setBrightness(getApplicationContext(),
									manulLightValue, true);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		brightSeekBar.setProgress(SettingUtil.getBrightness(context));
		boolean isAutoLightSwitchOn = isAutoLightSwitchOn();
		switchAutolight.setChecked(isAutoLightSwitchOn);
		hideOrShowSeekBarLayout(isAutoLightSwitchOn);
		super.onResume();
	}

	private void hideOrShowSeekBarLayout(boolean isAutoLightSwitchOn) {
		if (!isAutoLightSwitchOn) {
			layoutSeekBar.setVisibility(View.VISIBLE);
		} else {
			layoutSeekBar.setVisibility(View.GONE);
		}
	}

	private boolean isAutoLightSwitchOn() {
		String strAutoLight = ProviderUtil.getValue(context,
				Name.SET_AUTO_LIGHT_STATE, "0");
		return "1".equals(strAutoLight);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			default:
				break;
			}
		}

	}

	private void iniRadioGroup() {
		screenOffGroup = (RadioGroup) findViewById(R.id.screenOffGroup);
		screenOffGroup
				.setOnCheckedChangeListener(new MyRadioOnCheckedListener());
		screenOff30Second = (RadioButton) findViewById(R.id.screenOff30Second);
		screenOff1min = (RadioButton) findViewById(R.id.screenOff1min);
		screenOff2min = (RadioButton) findViewById(R.id.screenOff2min);
		screenOff10min = (RadioButton) findViewById(R.id.screenOff10min);
		screenOffNone = (RadioButton) findViewById(R.id.screenOffNone);

		int nowScreenOffTime = SettingUtil.getScreenOffTime(context);
		switch (nowScreenOffTime) {
		case 30000:
			screenOff30Second.setChecked(true);
			break;

		case 60000:
			screenOff1min.setChecked(true);
			break;

		case 120000:
			screenOff2min.setChecked(true);
			break;

		case 600000:
			screenOff10min.setChecked(true);
			break;

		default:
			screenOffNone.setChecked(true);
			break;
		}
	}

	class MyRadioOnCheckedListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.screenOff30Second:
				SettingUtil.setScreenOffTime(context,
						Constant.Setting.SCREEN_OFF_30S);
				break;
			case R.id.screenOff1min:
				SettingUtil.setScreenOffTime(context,
						Constant.Setting.SCREEN_OFF_1M);
				break;
			case R.id.screenOff2min:
				SettingUtil.setScreenOffTime(context,
						Constant.Setting.SCREEN_OFF_2M);
				break;
			case R.id.screenOff10min:
				SettingUtil.setScreenOffTime(context,
						Constant.Setting.SCREEN_OFF_10M);
				break;
			case R.id.screenOffNone:
				SettingUtil.setScreenOffTime(context,
						Constant.Setting.SCREEN_OFF_NEVER);
				break;

			default:
				break;
			}
		}
	}

}
