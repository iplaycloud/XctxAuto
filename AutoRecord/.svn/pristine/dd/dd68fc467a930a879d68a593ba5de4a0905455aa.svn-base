package com.tchip.autorecord.view;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

public class BackLineView extends View {

	private Context context;
	private SharedPreferences sharedPreferences;
	private Editor editor;

	private String model = "TX2";

	// TX2-6.76
	private static final int DEFALT_1X = 150;
	private static final int DEFALT_1Y = 480;
	private static final int DEFALT_2X = 206;
	private static final int DEFALT_2Y = 400;
	private static final int DEFALT_3X = 292;
	private static final int DEFALT_3Y = 318;
	private static final int DEFALT_4X = 345;
	private static final int DEFALT_4Y = 227;
	private static final int DEFALT_5X = 820;
	private static final int DEFALT_5Y = DEFALT_4Y;
	private static final int DEFALT_6X = 914;
	private static final int DEFALT_6Y = DEFALT_3Y;
	private static final int DEFALT_7X = 977;
	private static final int DEFALT_7Y = DEFALT_2Y;
	private static final int DEFALT_8X = 1034;
	private static final int DEFALT_8Y = DEFALT_1Y;

	private static final int MIN_1X = DEFALT_1X;
	private static final int MAX_1X = DEFALT_8X;
	private static final int MIN_1Y = DEFALT_1Y - 30;
	private static final int MAX_1Y = DEFALT_1Y;
	private static final int MIN_8Y = MIN_1Y;

	// TX2S-9.76-全屏
	private static final int DEFALT_1X_X2S_FULL = 470;
	private static final int DEFALT_1Y_X2S_FULL = 445;
	private static final int DEFALT_2X_X2S_FULL = 526;
	private static final int DEFALT_2Y_X2S_FULL = 365;
	private static final int DEFALT_3X_X2S_FULL = 612;
	private static final int DEFALT_3Y_X2S_FULL = 285;
	private static final int DEFALT_4X_X2S_FULL = 665;
	private static final int DEFALT_4Y_X2S_FULL = 212;
	private static final int DEFALT_5X_X2S_FULL = 1220;
	private static final int DEFALT_5Y_X2S_FULL = DEFALT_4Y_X2S_FULL;
	private static final int DEFALT_6X_X2S_FULL = 1324;
	private static final int DEFALT_6Y_X2S_FULL = DEFALT_3Y_X2S_FULL;
	private static final int DEFALT_7X_X2S_FULL = 1387;
	private static final int DEFALT_7Y_X2S_FULL = DEFALT_2Y_X2S_FULL;
	private static final int DEFALT_8X_X2S_FULL = 1444;
	private static final int DEFALT_8Y_X2S_FULL = DEFALT_1Y_X2S_FULL;
	private static final int MIN_1X_X2S_FULL = DEFALT_1X_X2S_FULL - 200;
	private static final int MAX_1X_X2S_FULL = DEFALT_8X_X2S_FULL + 200;

	// TX2S-9.76
	private static final int DEFALT_1X_X2S = 150;
	private static final int DEFALT_1Y_X2S = 445;
	private static final int DEFALT_2X_X2S = 206;
	private static final int DEFALT_2Y_X2S = 365;
	private static final int DEFALT_3X_X2S = 292;
	private static final int DEFALT_3Y_X2S = 285;
	private static final int DEFALT_4X_X2S = 345;
	private static final int DEFALT_4Y_X2S = 212;
	private static final int DEFALT_5X_X2S = 910;
	private static final int DEFALT_5Y_X2S = DEFALT_4Y_X2S;
	private static final int DEFALT_6X_X2S = 1004;
	private static final int DEFALT_6Y_X2S = DEFALT_3Y_X2S;
	private static final int DEFALT_7X_X2S = 1067;
	private static final int DEFALT_7Y_X2S = DEFALT_2Y_X2S;
	private static final int DEFALT_8X_X2S = 1124;
	private static final int DEFALT_8Y_X2S = DEFALT_1Y_X2S;

	private static final int MIN_1X_X2S = DEFALT_1X_X2S;
	private static final int MAX_1X_X2S = DEFALT_8X_X2S;
	private static final int MIN_1Y_X2S = DEFALT_1Y_X2S - 30;
	private static final int MIN_8Y_X2S = MIN_1Y_X2S;
	private static final int MAX_1Y_X2S = DEFALT_1Y_X2S;

	// TX3-7.84
	private static final int DEFALT_1X_X3 = 150;
	private static final int DEFALT_1Y_X3 = 400;
	private static final int DEFALT_2X_X3 = 206;
	private static final int DEFALT_2Y_X3 = 320;
	private static final int DEFALT_3X_X3 = 292;
	private static final int DEFALT_3Y_X3 = 238;
	private static final int DEFALT_4X_X3 = 345;
	private static final int DEFALT_4Y_X3 = 167;
	private static final int DEFALT_5X_X3 = 820;
	private static final int DEFALT_5Y_X3 = DEFALT_4Y_X3;
	private static final int DEFALT_6X_X3 = 914;
	private static final int DEFALT_6Y_X3 = DEFALT_3Y_X3;
	private static final int DEFALT_7X_X3 = 977;
	private static final int DEFALT_7Y_X3 = DEFALT_2Y_X3;
	private static final int DEFALT_8X_X3 = 1034;
	private static final int DEFALT_8Y_X3 = DEFALT_1Y_X3;

	private static final int MIN_1X_X3 = DEFALT_1X_X3;
	private static final int MAX_1X_X3 = DEFALT_8X_X3;
	private static final int MIN_1Y_X3 = DEFALT_1Y_X3 - 30;
	private static final int MAX_1Y_X3 = DEFALT_1Y_X3;
	private static final int MIN_8Y_X3 = MIN_1Y_X3;

	/** 点1最小X坐标 */
	private int min1x = MIN_1X;
	/** 点1最大X坐标 */
	private int max1x = MAX_1X;
	/** 点1最小Y坐标 */
	private int min1y = MIN_1Y;
	/** 点1最大X坐标 **/
	private int max1y = MAX_1Y;
	/** 点8最小Y坐标 */
	private int min8y = MIN_8Y;

	private int colorRed = Color.RED;
	private int colorYellow = Color.YELLOW;
	private int colorGreen = Color.GREEN;

	int[] point1 = { DEFALT_1X, DEFALT_1Y };
	int[] point2 = { DEFALT_2X, DEFALT_2Y };
	int[] point3 = { DEFALT_3X, DEFALT_3Y };
	int[] point4 = { DEFALT_4X, DEFALT_4Y };
	int[] point5 = { DEFALT_5X, DEFALT_5Y };
	int[] point6 = { DEFALT_6X, DEFALT_6Y };
	int[] point7 = { DEFALT_7X, DEFALT_7Y };
	int[] point8 = { DEFALT_8X, DEFALT_8Y };

	/** 线短粗细 */
	private int LINE_WIDTH = 8;
	/** 编辑指示图半径 */
	private int POINT_HINT_RADIUS = 25;
	/** 是否是编辑模式 */
	private boolean isModifyMode = false;
	/** 手指是否按下 */
	private boolean isTouchDown = false;
	/** 当前正在编辑的点 */
	private int nowModifyPoint = 0;

	public BackLineView(Context context) {
		super(context);
		this.context = context;
		sharedPreferences = context.getSharedPreferences("BackLine",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		model = Build.MODEL;
		if ("TX2S".equals(model)) { // TX2S-9.76
			if (Constant.Module.isTX2SBackFull) {
				point1[0] = DEFALT_1X_X2S_FULL;
				point1[1] = DEFALT_1Y_X2S_FULL;
				point2[0] = DEFALT_2X_X2S_FULL;
				point2[1] = DEFALT_2Y_X2S_FULL;
				point3[0] = DEFALT_3X_X2S_FULL;
				point3[1] = DEFALT_3Y_X2S_FULL;
				point4[0] = DEFALT_4X_X2S_FULL;
				point4[1] = DEFALT_4Y_X2S_FULL;
				point5[0] = DEFALT_5X_X2S_FULL;
				point5[1] = DEFALT_5Y_X2S_FULL;
				point6[0] = DEFALT_6X_X2S_FULL;
				point6[1] = DEFALT_6Y_X2S_FULL;
				point7[0] = DEFALT_7X_X2S_FULL;
				point7[1] = DEFALT_7Y_X2S_FULL;
				point8[0] = DEFALT_8X_X2S_FULL;
				point8[1] = DEFALT_8Y_X2S_FULL;

				min1x = MIN_1X_X2S_FULL;
				max1x = MAX_1X_X2S_FULL;
				min1y = MIN_1Y_X2S;
				max1y = MAX_1Y_X2S;
				min8y = MIN_8Y_X2S;
			} else {
				point1[0] = DEFALT_1X_X2S;
				point1[1] = DEFALT_1Y_X2S;
				point2[0] = DEFALT_2X_X2S;
				point2[1] = DEFALT_2Y_X2S;
				point3[0] = DEFALT_3X_X2S;
				point3[1] = DEFALT_3Y_X2S;
				point4[0] = DEFALT_4X_X2S;
				point4[1] = DEFALT_4Y_X2S;
				point5[0] = DEFALT_5X_X2S;
				point5[1] = DEFALT_5Y_X2S;
				point6[0] = DEFALT_6X_X2S;
				point6[1] = DEFALT_6Y_X2S;
				point7[0] = DEFALT_7X_X2S;
				point7[1] = DEFALT_7Y_X2S;
				point8[0] = DEFALT_8X_X2S;
				point8[1] = DEFALT_8Y_X2S;

				min1x = MIN_1X_X2S;
				max1x = MAX_1X_X2S;
				min1y = MIN_1Y_X2S;
				max1y = MAX_1Y_X2S;
				min8y = MIN_8Y_X2S;
			}
			colorRed = Color.RED;
			colorYellow = 0xFFfbfb28;
			colorGreen = 0xFF089908;
		} else if ("TX3".equals(model)) { // TX3-7.84
			point1[0] = DEFALT_1X_X3;
			point1[1] = DEFALT_1Y_X3;
			point2[0] = DEFALT_2X_X3;
			point2[1] = DEFALT_2Y_X3;
			point3[0] = DEFALT_3X_X3;
			point3[1] = DEFALT_3Y_X3;
			point4[0] = DEFALT_4X_X3;
			point4[1] = DEFALT_4Y_X3;
			point5[0] = DEFALT_5X_X3;
			point5[1] = DEFALT_5Y_X3;
			point6[0] = DEFALT_6X_X3;
			point6[1] = DEFALT_6Y_X3;
			point7[0] = DEFALT_7X_X3;
			point7[1] = DEFALT_7Y_X3;
			point8[0] = DEFALT_8X_X3;
			point8[1] = DEFALT_8Y_X3;

			min1x = MIN_1X_X3;
			max1x = MAX_1X_X3;
			min1y = MIN_1Y_X3;
			max1y = MAX_1Y_X3;
			min8y = MIN_8Y_X3;

			colorRed = Color.RED;
			colorYellow = Color.YELLOW;
			colorGreen = Color.GREEN;
		} else { // TX2-6.86
			point1[0] = DEFALT_1X;
			point1[1] = DEFALT_1Y;
			point2[0] = DEFALT_2X;
			point2[1] = DEFALT_2Y;
			point3[0] = DEFALT_3X;
			point3[1] = DEFALT_3Y;
			point4[0] = DEFALT_4X;
			point4[1] = DEFALT_4Y;
			point5[0] = DEFALT_5X;
			point5[1] = DEFALT_5Y;
			point6[0] = DEFALT_6X;
			point6[1] = DEFALT_6Y;
			point7[0] = DEFALT_7X;
			point7[1] = DEFALT_7Y;
			point8[0] = DEFALT_8X;
			point8[1] = DEFALT_8Y;

			min1x = MIN_1X;
			max1x = MAX_1X;
			min1y = MIN_1Y;
			max1y = MAX_1Y;
			min8y = MIN_8Y;

			colorRed = Color.RED;
			colorYellow = Color.YELLOW;
			colorGreen = Color.GREEN;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		loadPointConfig();
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);// 空心
		paint.setAntiAlias(true); // 抗锯齿
		paint.setStrokeWidth(LINE_WIDTH); // 粗细
		Path path = new Path();

		// Line 3-4 5-6
		paint.setColor(colorGreen);
		path.moveTo(point3[0], point3[1]); // 3
		path.lineTo(point4[0], point4[1]); // 4
		path.moveTo(point5[0], point5[1]); // 5
		path.lineTo(point6[0], point6[1]); // 6
		canvas.drawPath(path, paint);

		// Line 2-3 6-7
		paint.setColor(colorYellow);
		path.reset();
		path.moveTo(point2[0], point2[1]); // 2
		path.lineTo(point3[0], point3[1]); // 3
		path.moveTo(point6[0], point6[1]); // 6
		path.lineTo(point7[0], point7[1]); // 7
		canvas.drawPath(path, paint);

		// Line 1-2 7-8
		paint.setColor(colorRed);
		path.reset();
		path.moveTo(point1[0], point1[1]); // 1
		path.lineTo(point2[0], point2[1]); // 2
		path.moveTo(point7[0], point7[1]); // 7
		path.lineTo(point8[0], point8[1]); // 8
		canvas.drawPath(path, paint);

		PathEffect effect = new DashPathEffect(new float[] { 14, 14, 14, 14 },
				1); // {实线,空白,实线,空白:偶数}
		paint.setPathEffect(effect);
		// 4--5
		path.reset();
		paint.setColor(colorGreen);
		path.moveTo(point4[0], point4[1]); // 4
		path.lineTo(point5[0], point5[1]); // 5
		canvas.drawPath(path, paint);
		// 3--6
		path.reset();
		paint.setColor(colorYellow);
		path.moveTo(point3[0], point3[1]); // 3
		path.lineTo(point6[0], point6[1]); // 6
		canvas.drawPath(path, paint);
		// 2--7
		path.reset();
		paint.setColor(colorRed);
		path.moveTo(point2[0], point2[1]); // 2
		path.lineTo(point7[0], point7[1]); // 7
		canvas.drawPath(path, paint);

		if (isModifyMode) { // 绘制指示点
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.back_line_point);
			canvas.drawBitmap(bitmap, point1[0] - POINT_HINT_RADIUS, point1[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point2[0] - POINT_HINT_RADIUS, point2[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point3[0] - POINT_HINT_RADIUS, point3[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point4[0] - POINT_HINT_RADIUS, point4[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point5[0] - POINT_HINT_RADIUS, point5[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point6[0] - POINT_HINT_RADIUS, point6[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point7[0] - POINT_HINT_RADIUS, point7[1]
					- POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(bitmap, point8[0] - POINT_HINT_RADIUS, point8[1]
					- POINT_HINT_RADIUS, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchDown = true;
			nowModifyPoint = whichPoint(x, y);
			break;

		case MotionEvent.ACTION_MOVE:
			if (isModifyMode && isTouchDown && nowModifyPoint != 0) {
				setPointLocation(nowModifyPoint, (int) x, (int) y);
			}
			break;

		case MotionEvent.ACTION_UP:
			isTouchDown = false;
			break;
		}
		return true;
	}

	public void setModifyMode(boolean isModifyMode) {
		this.isModifyMode = isModifyMode;
	}

	public boolean getModifyMode() {
		return isModifyMode;
	}

	/** 从SP加载点坐标 */
	private void loadPointConfig() {
		point1[0] = sharedPreferences.getInt("point1x", point1[0]);
		point1[1] = sharedPreferences.getInt("point1y", point1[1]);
		point4[0] = sharedPreferences.getInt("point4x", point4[0]);
		point4[1] = sharedPreferences.getInt("point4y", point4[1]);

		point5[0] = sharedPreferences.getInt("point5x", point5[0]);
		point5[1] = sharedPreferences.getInt("point5y", point5[1]);
		point8[0] = sharedPreferences.getInt("point8x", point8[0]);
		point8[1] = sharedPreferences.getInt("point8y", point8[1]);

		point2[1] = sharedPreferences.getInt("point2y", point2[1]);
		point2[0] = (point2[1] - point1[1]) * (point1[0] - point4[0])
				/ (point1[1] - point4[1]) + point1[0];

		point3[1] = sharedPreferences.getInt("point3y", point3[1]);
		point3[0] = (point3[1] - point1[1]) * (point1[0] - point4[0])
				/ (point1[1] - point4[1]) + point1[0];

		point6[1] = sharedPreferences.getInt("point6y", point6[1]);
		point6[0] = (point6[1] - point5[1]) * (point5[0] - point8[0])
				/ (point5[1] - point8[1]) + point5[0];

		point7[1] = sharedPreferences.getInt("point7y", point7[1]);
		point7[0] = (point7[1] - point5[1]) * (point5[0] - point8[0])
				/ (point5[1] - point8[1]) + point5[0];
	}

	public void clearPonitConfig() {
		if ("TX2S".equals(model)) { // TX2S

			if (Constant.Module.isTX2SBackFull) {
				editor.putInt("point1x", DEFALT_1X_X2S_FULL);
				editor.putInt("point1y", DEFALT_1Y_X2S_FULL);
				editor.putInt("point2x", DEFALT_2X_X2S_FULL);
				editor.putInt("point2y", DEFALT_2Y_X2S_FULL);
				editor.putInt("point3x", DEFALT_3X_X2S_FULL);
				editor.putInt("point3y", DEFALT_3Y_X2S_FULL);
				editor.putInt("point4x", DEFALT_4X_X2S_FULL);
				editor.putInt("point4y", DEFALT_4Y_X2S_FULL);
				editor.putInt("point5x", DEFALT_5X_X2S_FULL);
				editor.putInt("point5y", DEFALT_5Y_X2S_FULL);
				editor.putInt("point6x", DEFALT_6X_X2S_FULL);
				editor.putInt("point6y", DEFALT_6Y_X2S_FULL);
				editor.putInt("point7x", DEFALT_7X_X2S_FULL);
				editor.putInt("point7y", DEFALT_7Y_X2S_FULL);
				editor.putInt("point8x", DEFALT_8X_X2S_FULL);
				editor.putInt("point8y", DEFALT_8Y_X2S_FULL);
				editor.commit();
			} else {
				editor.putInt("point1x", DEFALT_1X_X2S);
				editor.putInt("point1y", DEFALT_1Y_X2S);
				editor.putInt("point2x", DEFALT_2X_X2S);
				editor.putInt("point2y", DEFALT_2Y_X2S);
				editor.putInt("point3x", DEFALT_3X_X2S);
				editor.putInt("point3y", DEFALT_3Y_X2S);
				editor.putInt("point4x", DEFALT_4X_X2S);
				editor.putInt("point4y", DEFALT_4Y_X2S);
				editor.putInt("point5x", DEFALT_5X_X2S);
				editor.putInt("point5y", DEFALT_5Y_X2S);
				editor.putInt("point6x", DEFALT_6X_X2S);
				editor.putInt("point6y", DEFALT_6Y_X2S);
				editor.putInt("point7x", DEFALT_7X_X2S);
				editor.putInt("point7y", DEFALT_7Y_X2S);
				editor.putInt("point8x", DEFALT_8X_X2S);
				editor.putInt("point8y", DEFALT_8Y_X2S);
				editor.commit();
			}
		} else if ("TX3".equals(model)) { // TX3
			editor.putInt("point1x", DEFALT_1X_X3);
			editor.putInt("point1y", DEFALT_1Y_X3);
			editor.putInt("point2x", DEFALT_2X_X3);
			editor.putInt("point2y", DEFALT_2Y_X3);
			editor.putInt("point3x", DEFALT_3X_X3);
			editor.putInt("point3y", DEFALT_3Y_X3);
			editor.putInt("point4x", DEFALT_4X_X3);
			editor.putInt("point4y", DEFALT_4Y_X3);
			editor.putInt("point5x", DEFALT_5X_X3);
			editor.putInt("point5y", DEFALT_5Y_X3);
			editor.putInt("point6x", DEFALT_6X_X3);
			editor.putInt("point6y", DEFALT_6Y_X3);
			editor.putInt("point7x", DEFALT_7X_X3);
			editor.putInt("point7y", DEFALT_7Y_X3);
			editor.putInt("point8x", DEFALT_8X_X3);
			editor.putInt("point8y", DEFALT_8Y_X3);
			editor.commit();
		} else { // TX2
			editor.putInt("point1x", DEFALT_1X);
			editor.putInt("point1y", DEFALT_1Y);
			editor.putInt("point2x", DEFALT_2X);
			editor.putInt("point2y", DEFALT_2Y);
			editor.putInt("point3x", DEFALT_3X);
			editor.putInt("point3y", DEFALT_3Y);
			editor.putInt("point4x", DEFALT_4X);
			editor.putInt("point4y", DEFALT_4Y);
			editor.putInt("point5x", DEFALT_5X);
			editor.putInt("point5y", DEFALT_5Y);
			editor.putInt("point6x", DEFALT_6X);
			editor.putInt("point6y", DEFALT_6Y);
			editor.putInt("point7x", DEFALT_7X);
			editor.putInt("point7y", DEFALT_7Y);
			editor.putInt("point8x", DEFALT_8X);
			editor.putInt("point8y", DEFALT_8Y);
			editor.commit();
		}
	}

	private void setPointLocation(int point, int x, int y) {
		if (x < min1x) { // x:0-1184
			x = min1x;
		} else if (x > max1x) {
			x = max1x;
		}
		if (y < 50) { // y:0-480
			y = 50;
		} else if (y > max1y) {
			y = max1y;
		}
		switch (point) {
		case 1:
			if (x < point8[0] - POINT_HINT_RADIUS * 2) {
				point1[0] = x;
				editor.putInt("point1x", x);
			} else {
				point1[0] = point8[0] - POINT_HINT_RADIUS * 2;
				editor.putInt("point1x", point8[0] - POINT_HINT_RADIUS * 2);
			}
			if (y > min1y) {
				point1[1] = y;
				editor.putInt("point1y", y);
			} else {
				point1[1] = min1y;
				editor.putInt("point1y", min1y);
			}
			break;

		case 2:
			if (y > point1[1] - POINT_HINT_RADIUS * 2) {
				point2[0] = x;
				point2[1] = point1[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point2x", x);
				editor.putInt("point2y", point1[1] - POINT_HINT_RADIUS * 2);
			} else if (y > point3[1] + POINT_HINT_RADIUS * 2 && y < point1[1]) {
				point2[0] = x;
				point2[1] = y;
				editor.putInt("point2x", x);
				editor.putInt("point2y", y);
			} else {
				point2[0] = x;
				point2[1] = point3[1] + POINT_HINT_RADIUS * 2;
				editor.putInt("point2x", x);
				editor.putInt("point2y", point3[1] + POINT_HINT_RADIUS * 2);
			}
			break;

		case 3:
			if (y < point4[1] + POINT_HINT_RADIUS * 2) {
				point3[0] = x;
				point3[1] = point4[1] + POINT_HINT_RADIUS * 2;
				editor.putInt("point3x", x);
				editor.putInt("point3y", point4[1] + POINT_HINT_RADIUS * 2);
			} else if (y > point2[1] - POINT_HINT_RADIUS * 2) {
				point3[0] = x;
				point3[1] = point2[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point3x", x);
				editor.putInt("point3y", point2[1] - POINT_HINT_RADIUS * 2);
			} else {
				point3[0] = x;
				point3[1] = y;
				editor.putInt("point3x", x);
				editor.putInt("point3y", y);
			}
			break;

		case 4:
			if (y > point3[1] - POINT_HINT_RADIUS * 2) {
				point4[1] = point3[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point4y", point3[1] - POINT_HINT_RADIUS * 2);
			} else {
				point4[1] = y;
				editor.putInt("point4y", y);
			}

			if (x > point5[0] - POINT_HINT_RADIUS * 2) {
				point4[0] = x;
				editor.putInt("point4x", point5[0] - POINT_HINT_RADIUS * 2);
			} else {
				point4[0] = x;
				editor.putInt("point4x", x);
			}
			break;

		case 5:
			if (y > point6[1] - POINT_HINT_RADIUS * 2) {
				point5[1] = point6[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point5y", point6[1] - POINT_HINT_RADIUS * 2);
			} else {
				point5[1] = y;
				editor.putInt("point5y", y);
			}

			if (x < point4[0] + POINT_HINT_RADIUS * 2) {
				point5[0] = x;
				editor.putInt("point5x", point4[0] + POINT_HINT_RADIUS * 2);
			} else {
				point5[0] = x;
				editor.putInt("point5x", x);
			}

			break;

		case 6:
			if (y < point5[1] + POINT_HINT_RADIUS * 2) {
				point6[0] = x;
				point6[1] = point5[1] + POINT_HINT_RADIUS * 2;
				editor.putInt("point6x", x);
				editor.putInt("point6y", point5[1] + POINT_HINT_RADIUS * 2);
			} else if (y > point7[1] - POINT_HINT_RADIUS * 2) {
				point6[0] = x;
				point6[1] = point7[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point6x", x);
				editor.putInt("point6y", point7[1] - POINT_HINT_RADIUS * 2);
			} else {
				point6[0] = x;
				point6[1] = y;
				editor.putInt("point6x", x);
				editor.putInt("point6y", y);
			}
			break;

		case 7:
			if (y > point8[1] - POINT_HINT_RADIUS * 2) {
				point7[0] = x;
				point7[1] = point8[1] - POINT_HINT_RADIUS * 2;
				editor.putInt("point7x", x);
				editor.putInt("point7y", point8[1] - POINT_HINT_RADIUS * 2);
			} else if (y > point6[1] + POINT_HINT_RADIUS * 2 && y < point8[1]) {
				point7[0] = x;
				point7[1] = y;
				editor.putInt("point7x", x);
				editor.putInt("point7y", y);
			} else {
				point7[0] = x;
				point7[1] = point6[1] + POINT_HINT_RADIUS * 2;
				editor.putInt("point7x", x);
				editor.putInt("point7y", point6[1] + POINT_HINT_RADIUS * 2);
			}
			break;

		case 8:
			if (x > point1[0] + POINT_HINT_RADIUS * 2) {
				point8[0] = x;
				editor.putInt("point8x", x);
			} else {
				point8[0] = point1[0] + POINT_HINT_RADIUS * 2;
				editor.putInt("point8x", point1[0] + POINT_HINT_RADIUS * 2);
			}
			if (y > min8y) {
				point8[1] = y;
				editor.putInt("point8y", y);
			} else {
				point8[1] = min8y;
				editor.putInt("point8y", min8y);
			}
			break;

		default:
			break;
		}
		editor.commit();
		postInvalidate();
	}

	private int SPAN_ZONE = 25;

	private int whichPoint(float x, float y) {
		if (x - SPAN_ZONE < point1[0] && point1[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point1[1] && point1[1] < y + SPAN_ZONE) {
			return 1;
		} else if (x - SPAN_ZONE < point2[0] && point2[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point2[1] && point2[1] < y + SPAN_ZONE) {
			return 2;
		} else if (x - SPAN_ZONE < point3[0] && point3[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point3[1] && point3[1] < y + SPAN_ZONE) {
			return 3;
		} else if (x - SPAN_ZONE < point4[0] && point4[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point4[1] && point4[1] < y + SPAN_ZONE) {
			return 4;
		} else if (x - SPAN_ZONE < point5[0] && point5[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point5[1] && point5[1] < y + SPAN_ZONE) {
			return 5;
		} else if (x - SPAN_ZONE < point6[0] && point6[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point6[1] && point6[1] < y + SPAN_ZONE) {
			return 6;
		} else if (x - SPAN_ZONE < point7[0] && point7[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point7[1] && point7[1] < y + SPAN_ZONE) {
			return 7;
		} else if (x - SPAN_ZONE < point8[0] && point8[0] < x + SPAN_ZONE
				&& y - SPAN_ZONE < point8[1] && point8[1] < y + SPAN_ZONE) {
			return 8;
		} else
			return 0;
	}
}
