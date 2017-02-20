package com.tchip.carlauncher.view;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.util.MyLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyScrollView extends HorizontalScrollView {

	private MyScrollViewListener myScrollViewListener = null;
	private boolean isScroll = false;
	private int currentX = 0;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMyScrollViewListener(
			MyScrollViewListener myScrollViewListener) {
		this.myScrollViewListener = myScrollViewListener;
	}

	@Override
	protected void onScrollChanged(int currentX, int currentY, int oldX,
			int oldY) {
		super.onScrollChanged(currentX, currentY, oldX, oldY);
		this.currentX = currentX;

		if (myScrollViewListener != null) {
			myScrollViewListener.onScrollChanged(this, currentX, currentY,
					oldX, oldY);
		}
	}

	public boolean isScroll() {
		return isScroll;
	}

	public void fancyScroll(int x, boolean isSmooth) {
		if (Constant.Module.isIconAtom) {
			int showItemId = x / 185;
			int span = x % 185;
			if (isSmooth) {
				smoothScrollTo((185 + 2)
						* (span > 92 ? showItemId + 1 : showItemId), 0);
			} else {
				scrollTo((185 + 2) * (span > 92 ? showItemId + 1 : showItemId),
						0);
			}
			MyLog.v("fancyScroll,x:" + currentX + ",showItemId:" + showItemId
					+ ",showItemSpan" + span);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Constant.Module.isIconAtom) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isScroll = true;
				MyLog.v("MyScrollView: ACTION_DOWN");
				break;

			case MotionEvent.ACTION_MOVE:
				isScroll = true;
				MyLog.v("MyScrollView: ACTION_MOVE,currentX:" + currentX);
				break;

			case MotionEvent.ACTION_UP:
				isScroll = false;
				fancyScroll(currentX, false);
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(event);
	}
}
