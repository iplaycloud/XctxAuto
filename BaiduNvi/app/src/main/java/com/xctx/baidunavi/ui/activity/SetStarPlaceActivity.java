package com.xctx.baidunavi.ui.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xctx.baidunavi.Constant;
import com.xctx.baidunavi.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetStarPlaceActivity extends Activity {
	private MapView mapView;
	private BaiduMap baiduMap;
	private double mLatitude, mLongitude;

	private LatLng clickLatLng; // 点击的LatLng
	private LatLng locLatLng; // 定位的LatLng

	private SharedPreferences preference;
	private Editor editor;

	private TextView textHint;
	private ImageView imageHandClick;
	private Marker clickMarker;

	private RelativeLayout layoutConfirm, layoutBack;
	private Button btnConfirm, btnBack;

	private boolean isClick = false;

	/**
	 * 0-Work;1-Home
	 */
	private int starType = 0;

	private LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		setContentView(R.layout.activity_set_star_place);

		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preference.edit();

		initialLayout();

		// 接收搜索内容
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			starType = extras.getInt("starType");
			if (starType == 1) {
				// HOME
				textHint.setText("请点击地图选取家庭位置");
			} else {
				// WORK
				textHint.setText("请点击地图选取公司位置");
			}
		}

	}

	private void initialLayout() {
		mLatitude = Double
				.parseDouble(preference.getString("latitude", "0.00"));
		mLongitude = Double.parseDouble(preference.getString("longitude",
				"0.00"));

		mapView = (MapView) findViewById(R.id.mapView);
		// 去掉百度Logo
		int count = mapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mapView.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.INVISIBLE);
			}
		}

		baiduMap = mapView.getMap();
		// 初始化地图位置
		locLatLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locLatLng);
		baiduMap.animateMapStatus(u);

		// 设置地图放大级别 0-19
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17);
		baiduMap.animateMapStatus(msu);

		// 开启定位图层
		baiduMap.setMyLocationEnabled(true);
		// 自定义Maker
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_arrow_up);

		baiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				clickLatLng = mapPoi.getPosition();
				addMakerToMap(clickLatLng);
				isClick = true;

				Toast.makeText(getApplicationContext(),
						"您选中了：" + mapPoi.getName().replace("\\", ""),
						Toast.LENGTH_SHORT).show();

				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				clickLatLng = latLng;
				addMakerToMap(clickLatLng);
				isClick = true;
			}
		});

		layoutConfirm = (RelativeLayout) findViewById(R.id.layoutConfirm);
		layoutConfirm.setOnClickListener(new MyOnClickListener());
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(new MyOnClickListener());

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new MyOnClickListener());

		textHint = (TextView) findViewById(R.id.textHint);
		imageHandClick = (ImageView) findViewById(R.id.imageHandClick);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnConfirm:
			case R.id.layoutConfirm:
				if (isClick) {
					// 保存地址经纬度
					String strAddress = "";
					if (starType == 1) {
						// HOME
						strAddress = "家庭";
						editor.putBoolean("homeSet", true);
						editor.putString("homeLat", "" + clickLatLng.latitude);
						editor.putString("homeLng", "" + clickLatLng.longitude);
					} else {
						// WORK
						strAddress = "公司";
						editor.putBoolean("workSet", true);
						editor.putString("workLat", "" + clickLatLng.latitude);
						editor.putString("workLng", "" + clickLatLng.longitude);
					}
					editor.commit();
					Toast.makeText(getApplicationContext(),
							strAddress + "地址设置成功", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "请选取位置",
							Toast.LENGTH_SHORT).show();
				}
				break;

			case R.id.layoutBack:
			case R.id.btnBack:
				finish();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 
	 * @param tempMode
	 *            LocationMode.Hight_Accuracy-高精度
	 *            LocationMode.Battery_Saving-低功耗
	 *            LocationMode.Device_Sensors-仅设备
	 * @param tempCoor
	 *            gcj02-国测局加密经纬度坐标 bd09ll-百度加密经纬度坐标 bd09-百度加密墨卡托坐标
	 * @param frequence
	 *            MIN_SCAN_SPAN = 1000; MIN_SCAN_SPAN_NETWORK = 3000;
	 * @param isNeedAddress
	 *            是否需要地址
	 */
	private void InitLocation(
			com.baidu.location.LocationClientOption.LocationMode tempMode,
			String tempCoor, int frequence, boolean isNeedAddress) {

		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());
		// mGeofenceClient = new GeofenceClient(getApplicationContext());

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setCoorType(tempCoor);
		option.setScanSpan(frequence);
		option.setOpenGps(true);// 打开gps
		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// MapView 销毁后不在处理新接收的位置
			if (location == null || mapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(0)
					// accuracy设为0去掉蓝色精度圈，RAW:.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void addMakerToMap(LatLng latLng) {
		// 隐藏食指点击动画
		// imageHandClick.setVisibility(View.GONE);

		baiduMap.clear();
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.ui_marker_select);

		OverlayOptions ooA = new MarkerOptions().position(latLng)
				.icon(bitmapDescriptor).zIndex(9).draggable(true);
		baiduMap.addOverlay(ooA);
	}

	@Override
	protected void onPause() {
		Log.v(Constant.TAG, "SetStarPlaceActivity:onPause");
		mapView.onPause();

		// 销毁定位
		mLocationClient.stop();

		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.v(Constant.TAG, "SetStarPlaceActivity:onResume");
		mapView.onResume();

		// LocationMode 跟随：FOLLOWING 普通：NORMAL 罗盘：COMPASS
		com.baidu.mapapi.map.MyLocationConfiguration.LocationMode currentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				currentMode, true, null));
		InitLocation(
				com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy,
				"bd09ll", 5000, true);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

}
