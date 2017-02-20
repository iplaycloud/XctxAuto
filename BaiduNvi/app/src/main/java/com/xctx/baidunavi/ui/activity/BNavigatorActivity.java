package com.xctx.baidunavi.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRouteGuideManager.CustomizedLayerItem;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;

/**
 * 诱导界面
 */
public class BNavigatorActivity extends Activity {

	private BNRoutePlanNode mBNRoutePlanNode = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 隐藏导航栏和状态栏
		// View decorView = getWindow().getDecorView();
		// int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		// | View.SYSTEM_UI_FLAG_FULLSCREEN;
		// decorView.setSystemUiVisibility(uiOptions);

		createHandler();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		}
		View view = BNRouteGuideManager.getInstance().onCreate(this,
				new OnNavigationListener() {

					@Override
					public void onNaviGuideEnd() {
						finish();
					}

					@Override
					public void notifyOtherAction(int actionType, int arg1,
							int arg2, Object obj) {

						MyLog.v("[BNavigatorActivity]notifyOtherAction: actionType"
								+ actionType
								+ ",arg1:"
								+ arg1
								+ ",arg2:"
								+ arg2);


					}
				});

		if (view != null) {
			setContentView(view);
		}

		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				mBNRoutePlanNode = (BNRoutePlanNode) bundle
						.getSerializable(MainActivity.ROUTE_PLAN_NODE);
			}
		}
	}

	@Override
	protected void onResume() {
		MyLog.v("[BNavigatorActivity]onResume");
		BNRouteGuideManager.getInstance().onResume();

		// sendBroadcast(new Intent("com.tchip.UNSHOW_NAVBAR"));
		super.onResume();
		hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
	}

	protected void onPause() {
		MyLog.v("[BNavigatorActivity]onPause");

		// sendBroadcast(new Intent("com.tchip.SHOW_NAVBAR"));

		super.onPause();
		BNRouteGuideManager.getInstance().onPause();
	};

	@Override
	protected void onDestroy() {
		MyLog.v("[BNavigatorActivity]onDestroy");

		BNRouteGuideManager.getInstance().onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		MyLog.v("[BNavigatorActivity]onStop");
		BNRouteGuideManager.getInstance().onStop();
		super.onStop();
	}

	// @Override
	// public void finish() {
	// // TODO Auto-generated method stub
	// // super.finish();
	// moveTaskToBack(true); //设置该activity永不过期，即不执行onDestroy()
	// }

	@Override
	public void onBackPressed() {
		// onBackPressed(boolean showQuitDialog)

		BNRouteGuideManager.getInstance().onBackPressed(true);
		MyLog.v("[BNavigatorActivity]onBackPressed");
	}

	// public void onConfigurationChanged(
	// android.content.res.Configuration newConfig) {
	// BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
	// super.onConfigurationChanged(newConfig);
	// };

	private void addCustomizedLayerItems() {
		List<CustomizedLayerItem> items = new ArrayList<CustomizedLayerItem>();
		CustomizedLayerItem item1 = null;
		if (mBNRoutePlanNode != null) {
			item1 = new CustomizedLayerItem(mBNRoutePlanNode.getLongitude(),
					mBNRoutePlanNode.getLatitude(),
					mBNRoutePlanNode.getCoordinateType(), getResources()
							.getDrawable(R.drawable.ic_launcher),
					CustomizedLayerItem.ALIGN_CENTER);
			items.add(item1);

			BNRouteGuideManager.getInstance().setCustomizedLayerItems(items);
		}
		BNRouteGuideManager.getInstance().showCustomizedLayer(true);
	}

	private static final int MSG_SHOW = 1;
	private static final int MSG_HIDE = 2;
	private Handler hd = null;

	private void createHandler() {
		if (hd == null) {
			hd = new Handler(getMainLooper()) {
				public void handleMessage(android.os.Message msg) {
					if (msg.what == MSG_SHOW) {
						// addCustomizedLayerItems();
						// hd.sendEmptyMessageDelayed(MSG_HIDE, 5000);
					} else if (msg.what == MSG_HIDE) {
						BNRouteGuideManager.getInstance().showCustomizedLayer(
								false);
						// hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
					}

				};
			};
		}
	}
}
