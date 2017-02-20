package com.tchip.autosetting.ui;

import com.tchip.autosetting.R;
import com.tchip.autosetting.util.MyLog;
import com.tchip.autosetting.util.ProviderUtil;
import com.tchip.autosetting.util.ProviderUtil.Name;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingGravityActivity extends Activity {

	private Context context;
	private TextView textLow, textMiddle, textHigh;
	private SeekBar seekBarGravity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting_gravity);

		initialLayout();
	}

	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		Switch switchGravity = (Switch) findViewById(R.id.switchGravity);
		switchGravity.setChecked(isGravityOn());
		switchGravity.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				MyLog.v("SettingGravity.SET_DETECT_CRASH_STATE:" + isChecked);
				ProviderUtil.setValue(context, Name.SET_DETECT_CRASH_STATE,
						isChecked ? "1" : "0");
			}
		});

		textLow = (TextView) findViewById(R.id.textLow);
		textLow.setOnClickListener(myOnClickListener);
		textMiddle = (TextView) findViewById(R.id.textMiddle);
		textMiddle.setOnClickListener(myOnClickListener);
		textHigh = (TextView) findViewById(R.id.textHigh);
		textHigh.setOnClickListener(myOnClickListener);

		seekBarGravity = (SeekBar) findViewById(R.id.gravitySeekBar);
		seekBarGravity.setMax(2);
		seekBarGravity.setProgress(getGravityLevel());
		setTextLevelBackground(getGravityLevel());

		seekBarGravity
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						setTextLevelBackground(progress);
						int crashSensitive = seekBar.getProgress();
						MyLog.v("SettingGravity.Set crash sensitive:"
								+ crashSensitive);
						ProviderUtil.setValue(context,
								Name.SET_DETECT_CRASH_LEVEL, ""
										+ crashSensitive);
					}
				});
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.textLow:
				seekBarGravity.setProgress(0);
				break;

			case R.id.textMiddle:
				seekBarGravity.setProgress(1);
				break;

			case R.id.textHigh:
				seekBarGravity.setProgress(2);
				break;

			default:
				break;
			}

		}

	}

	private void setTextLevelBackground(int level) {
		switch (level) {
		case 0:
			textLow.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_now_bg, null));
			textMiddle.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			textHigh.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			break;

		case 1:
			textLow.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			textMiddle.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_now_bg, null));
			textHigh.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			break;

		case 2:
			textLow.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			textMiddle.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_other_bg, null));
			textHigh.setBackground(getResources().getDrawable(
					R.drawable.gravity_level_now_bg, null));
			break;

		default:
			break;
		}

	}

	/** 碰撞侦测是否打开:默认开启 */
	private boolean isGravityOn() {
		String strGravityOn = ProviderUtil.getValue(context,
				Name.SET_DETECT_CRASH_STATE, "1");
		return "1".equals(strGravityOn);
	}

	/** 获取当前设置的碰撞等级:默认1-中 */
	private int getGravityLevel() {
		String strGravityLevel = ProviderUtil.getValue(context,
				Name.SET_DETECT_CRASH_LEVEL, "1");
		if (strGravityLevel.equals("0")) {
			return 0;
		} else if (strGravityLevel.equals("2")) {
			return 2;
		} else {
			return 1;
		}
	}

}
