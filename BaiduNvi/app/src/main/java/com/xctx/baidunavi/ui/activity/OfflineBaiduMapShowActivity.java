package com.xctx.baidunavi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.xctx.baidunavi.R;

/**
 * 演示MapView的基本用法
 */
public class OfflineBaiduMapShowActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String LTAG = OfflineBaiduMapShowActivity.class
			.getSimpleName();
	private MapView mapView;
	private BaiduMap baiduMap;
	private RelativeLayout layoutBack;
	private Button btnBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_offline_map_show);

		mapView = (MapView) findViewById(R.id.mapView);
		baiduMap = mapView.getMap();

		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle bundle = intent.getExtras();
			LatLng latLng = new LatLng(bundle.getDouble("y"),
					bundle.getDouble("x"));
			// mapView = new MapView(this,
			// new BaiduMapOptions().mapStatus(new MapStatus.Builder()
			// .target(p).build()));
			//
			// 初始化地图位置,设置nowLoction数据以防NullPointer
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
			baiduMap.animateMapStatus(u);
		} else {
			// mapView = new MapView(this, new BaiduMapOptions());
		}

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnBack:
			case R.id.layoutBack:
				finish();
				break;

			default:
				break;
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mapView.onDestroy();
	}

}
