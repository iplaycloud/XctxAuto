package com.tchip.carlauncher.ui.activity;

import com.tchip.carlauncher.R;
import com.tchip.carlauncher.util.OpenUtil;
import com.tchip.carlauncher.util.OpenUtil.MODULE_TYPE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MultimediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_multimedia);
		initLayout();
	}

	private void initLayout() {
		// 图片
		ImageView imageGallery = (ImageView) findViewById(R.id.imageGallery);
		imageGallery.setOnClickListener(new MyOnClickListener());

		TextView textGallery = (TextView) findViewById(R.id.textGallery);
		textGallery.setOnClickListener(new MyOnClickListener());

		// 视频
		ImageView imageVideo = (ImageView) findViewById(R.id.imageVideo);
		imageVideo.setOnClickListener(new MyOnClickListener());

		TextView textVideo = (TextView) findViewById(R.id.textVideo);
		textVideo.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.textGallery:
			case R.id.imageGallery:
				OpenUtil.openModule(MultimediaActivity.this,
						MODULE_TYPE.GALLERY);
				break;

			case R.id.textVideo:
			case R.id.imageVideo:
				OpenUtil.openModule(MultimediaActivity.this, MODULE_TYPE.VIDEO);
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToMain();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToMain() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	@Override
	protected void onResume() {
		super.onResume();
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		// 更新MediaStore
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + "/mnt/sdcard/tachograph")));
	}
}
