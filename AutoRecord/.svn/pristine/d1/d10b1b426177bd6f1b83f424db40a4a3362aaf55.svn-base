package com.tchip.autorecord.service;

import com.tchip.autorecord.R;
import com.tchip.autorecord.util.MyLog;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FloatWindowService extends Service {

	LinearLayout layoutFloat; // 定义浮动窗口布局
	WindowManager.LayoutParams wmParams; // 创建浮动窗口设置布局参数的对象
	WindowManager windowManager;

	ImageView imageRecordingHint;

	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.i("FloatWindowService.onCreate");
		createFloatView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		windowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		wmParams.type = LayoutParams.TYPE_PHONE; // 设置Window Type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.CENTER | Gravity.TOP; // 顶部中央
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		layoutFloat = (LinearLayout) inflater.inflate(R.layout.record_hint_float_window,
				null);
		windowManager.addView(layoutFloat, wmParams); // 添加mFloatLayout
		// 浮动窗口按钮
		imageRecordingHint = (ImageView) layoutFloat
				.findViewById(R.id.imageRecordingHint);

		layoutFloat.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (layoutFloat != null) {
			windowManager.removeView(layoutFloat); // 移除悬浮窗口
		}
	}

}