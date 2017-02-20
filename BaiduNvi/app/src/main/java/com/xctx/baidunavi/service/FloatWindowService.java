package com.xctx.baidunavi.service;

import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;

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

	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	ImageView imageRecordingHint;

	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.i("oncreat");
		createFloatView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		MyLog.i("mWindowManager--->" + mWindowManager);

		wmParams.type = LayoutParams.TYPE_PHONE; // 设置Window type
		wmParams.format = PixelFormat.RGBA_8888; // 图片格式，效果为背景透明
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.gravity = Gravity.CENTER | Gravity.TOP; // 悬浮窗显示的停靠位置为左侧置顶
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
		 * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_window,
				null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
		imageRecordingHint = (ImageView) mFloatLayout
				.findViewById(R.id.imageRecordingHint);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		MyLog.i("Width/2--->" + imageRecordingHint.getMeasuredWidth() / 2);
		MyLog.i("Height/2--->" + imageRecordingHint.getMeasuredHeight() / 2);
		// 设置监听浮动窗口的触摸移动
		/*
		 * imageRecordingHint.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { //
		 * getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标 wmParams.x = (int) event.getRawX()
		 * - imageRecordingHint.getMeasuredWidth() / 2; MyLog.i("RawX" +
		 * event.getRawX()); MyLog.i("X" + event.getX()); // 减25为状态栏的高度
		 * wmParams.y = (int) event.getRawY() -
		 * imageRecordingHint.getMeasuredHeight() / 2 - 25; MyLog.i("RawY" +
		 * event.getRawY()); MyLog.i("Y" + event.getY()); // 刷新
		 * mWindowManager.updateViewLayout(mFloatLayout, wmParams); return
		 * false; // 此处必须返回false，否则OnClickListener获取不到监听 } });
		 * 
		 * imageRecordingHint.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * Toast.makeText(FloatWindowService.this, "onClick",
		 * Toast.LENGTH_SHORT).show(); } });
		 */
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
	}

}