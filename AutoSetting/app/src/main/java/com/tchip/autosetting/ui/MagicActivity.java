package com.tchip.autosetting.ui;

import com.tchip.autosetting.Constant;
import com.tchip.autosetting.R;
import com.tchip.autosetting.util.HintUtil;
import com.tchip.autosetting.util.OpenUtil;
import com.tchip.autosetting.util.ProviderUtil;
import com.tchip.autosetting.util.ProviderUtil.Name;
import com.tchip.autosetting.util.SettingUtil;
import com.tchip.autosetting.util.OpenUtil.MODULE_TYPE;

import android.app.Activity;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MagicActivity extends Activity {
	private Context context;
	private EditText textPass;
	private RelativeLayout layoutMagic;
	private RelativeLayout layoutRecord;

	private Switch switchFM;
	private Switch switchUVC;
	private Switch switchAccOffWake;

	private RadioButton frontBitrate6, frontBitrate7, frontBitrate8,
			frontBitrate9, frontBitrate10, frontBitrate12;

	private RadioButton backBitrate05, backBitrate1, backBitrate2,
			backBitrate3, backBitrate4;

	private EditText textInput;
	private Button btnSet;
	private Button btnOpenImageSensor;

	private final int M = 1024 * 1024;
	private final int DEFAULT_BITRATE_FRONT = 6 * M;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawable(null);
		context = getApplicationContext();
		setContentView(R.layout.activity_magic);

		initialLayout();
	}

	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		layoutMagic = (RelativeLayout) findViewById(R.id.layoutMagic);
		layoutRecord = (RelativeLayout) findViewById(R.id.layoutRecord);

		textPass = (EditText) findViewById(R.id.textPass);
		textPass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String strInput = textPass.getText().toString();
				if (Constant.Module.MagicCode.equals(strInput)) {
					layoutRecord.setVisibility(View.GONE);
					layoutMagic.setVisibility(View.VISIBLE);
				} else if (Constant.Module.RecordCode.equals(strInput)) {
					layoutMagic.setVisibility(View.GONE);
					layoutRecord.setVisibility(View.VISIBLE);
				}
			}
		});

		// Row 1
		Button btnDeviceTest = (Button) findViewById(R.id.btnDeviceTest);
		btnDeviceTest.setOnClickListener(myOnClickListener);
		Button btnEngineerMode = (Button) findViewById(R.id.btnEngineerMode);
		btnEngineerMode.setOnClickListener(myOnClickListener);
		Button btnSystemSetting = (Button) findViewById(R.id.btnSystemSetting);
		btnSystemSetting.setOnClickListener(myOnClickListener);
		Button btnMtkLogger = (Button) findViewById(R.id.btnMtkLogger);
		btnMtkLogger.setOnClickListener(myOnClickListener);
		Button btnCPUInfo = (Button) findViewById(R.id.btnCPUInfo);
		btnCPUInfo.setOnClickListener(myOnClickListener);
		// Row 2
		Button btnDeveloperSetting = (Button) findViewById(R.id.btnDeveloperSetting);
		btnDeveloperSetting.setOnClickListener(myOnClickListener);
		Button btnApplication = (Button) findViewById(R.id.btnApplication);
		btnApplication.setOnClickListener(myOnClickListener);
		Button btnCamera = (Button) findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(myOnClickListener);
		Button btnOpenCpuTemp = (Button) findViewById(R.id.btnOpenCpuTemp);
		btnOpenCpuTemp.setOnClickListener(myOnClickListener);
		Button btnCloseCpuTemp = (Button) findViewById(R.id.btnCloseCpuTemp);
		btnCloseCpuTemp.setOnClickListener(myOnClickListener);

		switchAccOffWake = (Switch) findViewById(R.id.switchAccOffWake);
		switchAccOffWake.setChecked("1".equals(ProviderUtil.getValue(context,
				Name.DEBUG_ACCOFF_WAKE, "0")));
		switchAccOffWake
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						ProviderUtil.setValue(context, Name.DEBUG_ACCOFF_WAKE,
								isChecked ? "1" : "0");
						SettingUtil.setAccOffWake(isChecked);
					}
				});

		// Row 3
		btnOpenImageSensor = (Button) findViewById(R.id.btnOpenImageSensor);
		btnOpenImageSensor.setOnClickListener(myOnClickListener);

		switchFM = (Switch) findViewById(R.id.switchFM);
		switchFM.setChecked(SettingUtil.isFMEnable());
		switchFM.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SettingUtil.setFMEnable(isChecked);
			}
		});

		switchUVC = (Switch) findViewById(R.id.switchUVC);
		switchUVC.setChecked(SettingUtil.isUVCEnable());
		switchUVC.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SettingUtil.setUVCEnable(isChecked);
			}
		});

		textInput = (EditText) findViewById(R.id.textInput);
		btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(myOnClickListener);

		RadioGroup frontBitrateGroup = (RadioGroup) findViewById(R.id.frontBitrateGroup);
		frontBitrateGroup
				.setOnCheckedChangeListener(new FrontRadioOnCheckedListener());
		frontBitrate6 = (RadioButton) findViewById(R.id.frontBitrate6);
		frontBitrate7 = (RadioButton) findViewById(R.id.frontBitrate7);
		frontBitrate8 = (RadioButton) findViewById(R.id.frontBitrate8);
		frontBitrate9 = (RadioButton) findViewById(R.id.frontBitrate9);
		frontBitrate10 = (RadioButton) findViewById(R.id.frontBitrate10);
		frontBitrate12 = (RadioButton) findViewById(R.id.frontBitrate12);

		String strFrontBitrate = ProviderUtil.getValue(context,
				Name.REC_FRONT_1080_BITRATE, "" + DEFAULT_BITRATE_FRONT);
		int intFrontBitrate = Integer.parseInt(strFrontBitrate);
		switch (intFrontBitrate) {
		case 7 * M:
			frontBitrate7.setChecked(true);
			break;

		case 8 * M:
			frontBitrate8.setChecked(true);
			break;

		case 9 * M:
			frontBitrate9.setChecked(true);
			break;

		case 10 * M:
			frontBitrate10.setChecked(true);
			break;

		case 12 * M:
			frontBitrate12.setChecked(true);
			break;

		case DEFAULT_BITRATE_FRONT:
		default:
			frontBitrate6.setChecked(true);
			break;
		}

		RadioGroup backBitrateGroup = (RadioGroup) findViewById(R.id.backBitrateGroup);
		backBitrateGroup
				.setOnCheckedChangeListener(new BackRadioOnCheckedListener());
		backBitrate05 = (RadioButton) findViewById(R.id.backBitrate05);
		backBitrate1 = (RadioButton) findViewById(R.id.backBitrate1);
		backBitrate2 = (RadioButton) findViewById(R.id.backBitrate2);
		backBitrate3 = (RadioButton) findViewById(R.id.backBitrate3);
		backBitrate4 = (RadioButton) findViewById(R.id.backBitrate4);

		String strBackBitrate = ProviderUtil.getValue(context,
				Name.REC_BACK_BITRATE, "" + 1 * 1024 * 1024);
		int intBackBitrate = Integer.parseInt(strBackBitrate);
		switch (intBackBitrate) {
		case 512 * 1024:
			backBitrate05.setChecked(true);
			break;

		case 2 * M:
			backBitrate2.setChecked(true);
			break;

		case 3 * M:
			backBitrate3.setChecked(true);
			break;

		case 4 * M:
			backBitrate4.setChecked(true);
			break;

		case 1 * M:
		default:
			backBitrate1.setChecked(true);
			break;
		}

	}

	class FrontRadioOnCheckedListener implements
			android.widget.RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.frontBitrate6:
				saveFrontBitrate(6 * M);
				break;

			case R.id.frontBitrate7:
				saveFrontBitrate(7 * M);
				break;

			case R.id.frontBitrate8:
				saveFrontBitrate(8 * M);
				break;

			case R.id.frontBitrate9:
				saveFrontBitrate(9 * M);
				break;

			case R.id.frontBitrate10:
				saveFrontBitrate(10 * M);
				break;

			case R.id.frontBitrate12:
				saveFrontBitrate(12 * M);
				break;

			default:
				break;
			}
		}

	}

	private void saveFrontBitrate(int bitrate) {
		ProviderUtil.setValue(context, Name.REC_FRONT_1080_BITRATE, ""
				+ bitrate);
	}

	class BackRadioOnCheckedListener implements
			android.widget.RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.backBitrate05:
				saveBackBitrate(512 * 1024);
				break;

			case R.id.backBitrate1:
				saveBackBitrate(1 * M);
				break;

			case R.id.backBitrate2:
				saveBackBitrate(2 * M);
				break;

			case R.id.backBitrate3:
				saveBackBitrate(3 * M);
				break;

			case R.id.backBitrate4:
				saveBackBitrate(4 * M);
				break;

			default:
				break;
			}
		}

	}

	private void saveBackBitrate(int bitrate) {
		ProviderUtil.setValue(context, Name.REC_BACK_BITRATE, "" + bitrate);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnDeviceTest:
				OpenUtil.openModule(MagicActivity.this, MODULE_TYPE.DEVICE_TEST);
				break;

			case R.id.btnEngineerMode:
				OpenUtil.openModule(MagicActivity.this,
						MODULE_TYPE.ENGINEER_MODE);
				break;

			case R.id.btnSystemSetting:
				OpenUtil.openModule(MagicActivity.this,
						MODULE_TYPE.SYSTEM_SETTING);
				break;

			case R.id.btnMtkLogger:
				OpenUtil.openModule(MagicActivity.this, MODULE_TYPE.MTK_LOGGER);
				break;

			case R.id.btnCPUInfo:
				OpenUtil.openModule(MagicActivity.this, MODULE_TYPE.CPU_INFO);
				break;

			case R.id.btnDeveloperSetting:
				OpenUtil.openModule(MagicActivity.this, MODULE_TYPE.DEV_SETTING);
				break;

			case R.id.btnApplication:
				OpenUtil.openModule(MagicActivity.this, MODULE_TYPE.APP);
				break;

			case R.id.btnOpenImageSensor:
				Intent intentImageSensor = new Intent(MagicActivity.this,
						ImageSensorModifyActivity.class);
				startActivity(intentImageSensor);
				break;

			case R.id.btnCamera:
				OpenUtil.openModule(MagicActivity.this,
						MODULE_TYPE.SYSTEM_CAMERA);
				break;

			case R.id.btnSet:
				String strContent = textInput.getText().toString();
				if (strContent.trim().length() > 0 && strContent != null) {
					SettingUtil.writeAudioNode(strContent);
				} else {
					Toast.makeText(MagicActivity.this, "请输入",
							Toast.LENGTH_SHORT).show();
				}
				break;

			case R.id.btnOpenCpuTemp:
				startUvcBackCarService(true);
				startUvcDaemonService(true);
				break;

			case R.id.btnCloseCpuTemp:
				startUvcBackCarService(false);
				startUvcDaemonService(false);
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 温度节点读取服务
	 * 
	 * @param msg
	 */
	private void startUvcBackCarService(boolean open) {
		try {
			Intent intent = new Intent();
			ComponentName comp = new ComponentName("com.android.systemui",
					"com.android.systemui.UvcBackCarService");
			intent.setComponent(comp);
			if (open)
				startService(intent);
			else
				stopService(intent);
		} catch (Exception e) {

		}
	}

	/**
	 * 温度节点监控服务
	 * 
	 * @param msg
	 */
	private void startUvcDaemonService(boolean open) {
		try {
			Intent intent = new Intent();
			ComponentName comp = new ComponentName("com.android.systemui",
					"com.android.systemui.UvcDaemonService");
			intent.setComponent(comp);
			if (open)
				startService(intent);
			else
				stopService(intent);
		} catch (Exception e) {

		}
	}

}
