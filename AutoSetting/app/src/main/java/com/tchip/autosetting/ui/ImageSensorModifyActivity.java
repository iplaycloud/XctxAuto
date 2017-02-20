package com.tchip.autosetting.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tchip.autosetting.R;
import com.tchip.autosetting.util.HintUtil;
import com.tchip.autosetting.util.MyLog;
import com.tchip.autosetting.util.SettingUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ImageSensorModifyActivity extends Activity {

	private Context context;
	private static final File NODE_LIGHT_DAY = new File(
			"/sys/bus/i2c/devices/0-007f/light_day");
	private static final File NODE_LIGHT_NIGHT_1 = new File(
			"/sys/bus/i2c/devices/0-007f/light_night");
	private static final File NODE_LIGHT_NIGHT_2 = new File(
			"/sys/bus/i2c/devices/0-007f/light_night2");
	private static final File NODE_CONTRAST_DAY = new File(
			"/sys/bus/i2c/devices/0-007f/contrast_day");
	private static final File NODE_CONTRAST_NIGHT = new File(
			"/sys/bus/i2c/devices/0-007f/contrast_night");

	private SeekBar seekBarLightDay, seekBarLightNight, seekBarLightNight2;
	private TextView textLightDay, textLightNight, textLightNight2;
	private SeekBar seekBarContrastDay, seekBarContrastNight;
	private TextView textContrastDay, textContrastNight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image_sensor_modify);
		setTitle(getResources().getString(R.string.magic_open_img_sensor));

		initialLayout();
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
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initialLayout() {
		textLightDay = (TextView) findViewById(R.id.textLightDay);
		textLightNight = (TextView) findViewById(R.id.textLightNight);
		textLightNight2 = (TextView) findViewById(R.id.textLightNight2);
		textContrastDay = (TextView) findViewById(R.id.textContrastDay);
		textContrastNight = (TextView) findViewById(R.id.textContrastNight);

		// 亮度(白天)
		seekBarLightDay = (SeekBar) findViewById(R.id.seekBarLightDay);
		int intLightDay = getFileContentToInt(NODE_LIGHT_DAY);
		if (intLightDay >= 0 && intLightDay <= 255) {
			seekBarLightDay.setProgress(intLightDay);
			textLightDay.setText("" + intLightDay);
		} else {
			seekBarLightDay.setProgress(0);
			textLightDay.setText("0");
		}
		seekBarLightDay
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int intLight = seekBar.getProgress();
						if (intLight >= 0 && intLight <= 255) {
							SettingUtil.SaveFileToNode(NODE_LIGHT_DAY, ""
									+ intLight);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						textLightDay.setText("" + progress);
					}
				});
		// 亮度(晚上1)
		seekBarLightNight = (SeekBar) findViewById(R.id.seekBarLightNight);
		int intLightNight = getFileContentToInt(NODE_LIGHT_NIGHT_1);
		if (intLightNight >= 0 && intLightNight <= 255) {
			seekBarLightNight.setProgress(intLightNight);
			textLightNight.setText("" + intLightNight);
		} else {
			seekBarLightNight.setProgress(0);
			textLightNight.setText("0");
		}
		seekBarLightNight
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int intLight = seekBar.getProgress();
						if (intLight >= 0 && intLight <= 255) {
							SettingUtil.SaveFileToNode(NODE_LIGHT_NIGHT_1, ""
									+ intLight);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						textLightNight.setText("" + progress);
					}
				});

		// 亮度(晚上2)
		seekBarLightNight2 = (SeekBar) findViewById(R.id.seekBarLightNight2);
		int intLightNight2 = getFileContentToInt(NODE_LIGHT_NIGHT_2);
		if (intLightNight2 >= 0 && intLightNight2 <= 255) {
			seekBarLightNight2.setProgress(intLightNight2);
			textLightNight2.setText("" + intLightNight2);
		} else {
			seekBarLightNight2.setProgress(0);
			textLightNight2.setText("0");
		}
		seekBarLightNight2
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int intLight = seekBar.getProgress();
						if (intLight >= 0 && intLight <= 255) {
							SettingUtil.SaveFileToNode(NODE_LIGHT_NIGHT_2, ""
									+ intLight);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						textLightNight2.setText("" + progress);
					}
				});

		// 对比度(白天)
		seekBarContrastDay = (SeekBar) findViewById(R.id.seekBarContrastDay);
		int intContrastDay = getFileContentToInt(NODE_CONTRAST_DAY);
		if (intContrastDay >= 0 && intContrastDay <= 255) {
			seekBarContrastDay.setProgress(intContrastDay);
			textContrastDay.setText("" + intContrastDay);
		} else {
			seekBarContrastDay.setProgress(0);
			textContrastDay.setText("0");
		}
		seekBarContrastDay
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int intContrast = seekBar.getProgress();
						if (intContrast >= 0 && intContrast <= 255) {
							SettingUtil.SaveFileToNode(NODE_CONTRAST_DAY, ""
									+ intContrast);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						textContrastDay.setText("" + progress);
					}
				});

		// 对比度(晚上)
		seekBarContrastNight = (SeekBar) findViewById(R.id.seekBarContrastNight);
		int intContrastNight = getFileContentToInt(NODE_CONTRAST_NIGHT);
		if (intContrastNight >= 0 && intContrastNight <= 255) {
			seekBarContrastNight.setProgress(intContrastNight);
			textContrastNight.setText("" + intContrastNight);
		} else {
			seekBarContrastNight.setProgress(0);
			textContrastNight.setText("0");
		}
		seekBarContrastNight
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

						int intContrast = seekBar.getProgress();
						if (intContrast >= 0 && intContrast <= 255) {
							SettingUtil.SaveFileToNode(NODE_CONTRAST_NIGHT, ""
									+ intContrast);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						textContrastNight.setText("" + progress);
					}
				});
	}

	private int getFileContentToInt(File file) {
		int fileValue = 0;
		String strValue = "";
		if (file.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					strValue += lineTXT.toString();
				}
				read.close();

				fileValue = Integer.parseInt(strValue);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("getFileContentToInt: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("getFileContentToInt: IOException");
			}
		}
		MyLog.v("getFileContentToInt,fileValue:" + fileValue);
		return fileValue;
	}

}
