package com.tchip.gaodenavi.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.iflytek.cloud.SpeechUtility;
import com.tchip.gaodenavi.Constant;
import com.tchip.gaodenavi.R;
import com.tchip.gaodenavi.adapter.NaviResultAdapter;
import com.tchip.gaodenavi.model.NaviResultInfo;
import com.tchip.gaodenavi.service.SpeakService;
import com.tchip.gaodenavi.util.AMapUtil;
import com.tchip.gaodenavi.util.MyLog;
import com.tchip.gaodenavi.util.NetworkUtil;
import com.tchip.gaodenavi.view.ActionSheet;

/**
 * AMapV2地图中介绍定位三种模式的使用，包括定位，追随，旋转
 */
public class MainActivity extends Activity implements LocationSource,
		AMapLocationListener, OnCheckedChangeListener, InfoWindowAdapter,
		OnPoiSearchListener, TextWatcher, OnMarkerClickListener,
		AMapNaviListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private RadioGroup mGPSModeGroup;

	private AutoCompleteTextView searchText;// 输入搜索关键字
	private String keyWord = "";// 要输入的poi搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private EditText editCity;// 要输入的城市名字或者城市区号
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索

	// 实时导航按钮
	private Button mStartNaviButton;

	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态

	// 当前位置
	private LatLng nowLatLng, listChoiceLatLng;
	private boolean isLocated = false;

	private boolean isSimulate = false;

	private ListView listResult;
	private ArrayList<NaviResultInfo> naviArray;
	private NaviResultAdapter naviResultAdapter;

	// 设置地图UI组件
	private UiSettings uiSettings;

	// 导航界面
	private RelativeLayout layoutNavi;

	private SharedPreferences preference;
	private Editor editor;

	private boolean hasLastLoc = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		SpeechUtility
				.createUtility(this, "appid=" + getString(R.string.app_id));

		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preference.edit();

		// 从SharedPreference读取上次定位数据
		double lastLocLat = Double.parseDouble(preference.getString("locLat",
				"0.0"));
		double lastLocLng = Double.parseDouble(preference.getString("locLng",
				"0.0"));
		if (!"0.0".equals(lastLocLat) && !"".equals(lastLocLng)) {
			hasLastLoc = true;
		}
		nowLatLng = new LatLng(lastLocLat, lastLocLng);

		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();

		// 语音播报开始
		initView();
	}

	/**
	 * 初始化地图
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();

			if (hasLastLoc) {
				// 更新地图中心点为上次定位
				/**
				 * CameraPosition((LatLng target, float zoom, float tilt, float
				 * bearing)
				 * 
				 * 1:目标位置的屏幕中心点经纬度坐标。
				 * 
				 * 2:目标可视区域的缩放级别。
				 * 
				 * 3:目标可视区域的倾斜度，以角度为单位。
				 * 
				 * 4:可视区域指向的方向 ，以角度为单位，正北方向到地图方向逆时针旋转的角度，范围从0度到360度
				 */
				CameraUpdate cameraUpdate = CameraUpdateFactory
						.newCameraPosition(new CameraPosition(nowLatLng, 17, 0,
								0));
				aMap.moveCamera(cameraUpdate);
			}

			aMap.setLocationSource(this);// 设置定位监听
			aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
			aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

			/**
			 * 设置页面监听
			 */
			ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
			btnSearch.setOnClickListener(new MyOnClickListener());
			Button nextButton = (Button) findViewById(R.id.nextButton);
			nextButton.setOnClickListener(new MyOnClickListener());
			searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
			searchText.addTextChangedListener(this); // 添加文本输入框监听事件
			editCity = (EditText) findViewById(R.id.city);
			aMap.setOnMarkerClickListener(this); // 添加点击marker监听事件
			aMap.setInfoWindowAdapter(this); // 添加显示infowindow监听事件

			uiSettings = aMap.getUiSettings();
			uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM); // 缩放放到右下位置
			uiSettings.setScaleControlsEnabled(true); // 显示比例尺
			uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT); // Logo和比例尺放到左下位置
		}
		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setOnCheckedChangeListener(this);
	}

	private void initView() {

		mRouteCalculatorProgressDialog = new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);

		AMapNavi.getInstance(this).setAMapNaviListener(this);

		// 搜索结果列表
		listResult = (ListView) findViewById(R.id.listResult);

		RelativeLayout layoutNaviTitle = (RelativeLayout) findViewById(R.id.layoutNaviTitle);
		layoutNaviTitle.setOnClickListener(new MyOnClickListener());

		layoutNavi = (RelativeLayout) findViewById(R.id.layoutNavi);
		setTabNaviShow(false);

		RelativeLayout layoutBackNavi = (RelativeLayout) findViewById(R.id.layoutBackNavi);
		layoutBackNavi.setOnClickListener(new MyOnClickListener());

		Button btnBackNavi = (Button) findViewById(R.id.btnBackNavi);
		btnBackNavi.setOnClickListener(new MyOnClickListener());
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.gps_locate_button:
			// 设置定位的类型为定位模式
			aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			break;

		case R.id.gps_follow_button:
			// 设置定位的类型为 跟随模式
			aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
			break;

		case R.id.gps_rotate_button:
			// 设置定位的类型为根据地图面向方向旋转
			aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
			break;
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();

		// 删除导航监听
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (mListener != null && location != null) {
			double locateLat = location.getLatitude();
			double locateLng = location.getLongitude();

			if (0.0 == locateLat || 0.0 == locateLng) {
				MyLog.e("[Location]Error Location, not update map");
			} else {
				float accuracy = location.getAccuracy(); // 精确度：
				if (accuracy > 0.0f && accuracy < 50.0f) {

					isLocated = true;
					nowLatLng = new LatLng(locateLat, locateLng);
					mListener.onLocationChanged(location); // 显示系统小蓝点

					// 获取地址
					String address = location.getAddress();
					String city = location.getCity();
					String poiName = location.getPoiName();
					String province = location.getProvince();
					float speed = location.getSpeed();
					long time = location.getTime();

					editor.putString("locLat", "" + locateLat);
					editor.putString("locLng", "" + locateLng);
					editor.putString("locCity", city);
					editor.commit();

					MyLog.v("[GaodeNavi]onLocationChanged:Address" + address
							+ ",City:" + city + ",PoiName:" + poiName
							+ ",Province:" + province + ",Speed:" + speed
							+ ",Time:" + time);

				}
			}

			MyLog.v("[Location]Lat:" + locateLat + ",Lng:" + locateLng);
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
				null);
		TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
		textTitle.setText(marker.getTitle());

		TextView textAddress = (TextView) view.findViewById(R.id.textAddress);
		textAddress.setText(marker.getSnippet());
		ImageButton btnNavi = (ImageButton) view.findViewById(R.id.btnNavi);

		btnNavi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 导航
				// 起点终点列表
				ArrayList<NaviLatLng> startPoints = new ArrayList<NaviLatLng>();
				ArrayList<NaviLatLng> endPoints = new ArrayList<NaviLatLng>();

				LatLng endLatLng = marker.getPosition();
				NaviLatLng endNaviLatLng = new NaviLatLng(endLatLng.latitude,
						endLatLng.longitude);
				endPoints.add(endNaviLatLng);

				NaviLatLng startNaviLatLng = new NaviLatLng(nowLatLng.latitude,
						nowLatLng.longitude);
				startPoints.add(startNaviLatLng);

				isSimulate = false;
				// DrivingSaveMoney--省钱
				// DrivingShortDistance--最短距离
				// DrivingNoExpressways--不走高速
				// DrivingFastestTime--最短时间
				// DrivingAvoidCongestion--避免拥堵
				AMapNavi.getInstance(MainActivity.this).calculateDriveRoute(
						startPoints, endPoints, null, AMapNavi.DrivingDefault);
				mRouteCalculatorProgressDialog.show();
			}
		});

		ImageButton btnSimulate = (ImageButton) view
				.findViewById(R.id.btnSimulate);
		btnSimulate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 导航
				// 起点终点列表
				ArrayList<NaviLatLng> startPoints = new ArrayList<NaviLatLng>();
				ArrayList<NaviLatLng> endPoints = new ArrayList<NaviLatLng>();

				LatLng endLatLng = marker.getPosition();
				NaviLatLng endNaviLatLng = new NaviLatLng(endLatLng.latitude,
						endLatLng.longitude);
				endPoints.add(endNaviLatLng);

				NaviLatLng startNaviLatLng = new NaviLatLng(nowLatLng.latitude,
						nowLatLng.longitude);
				startPoints.add(startNaviLatLng);

				isSimulate = true;
				// DrivingSaveMoney--省钱
				// DrivingShortDistance--最短距离
				// DrivingNoExpressways--不走高速
				// DrivingFastestTime--最短时间
				// DrivingAvoidCongestion--避免拥堵
				AMapNavi.getInstance(MainActivity.this).calculateDriveRoute(
						startPoints, endPoints, null, AMapNavi.DrivingDefault);
				mRouteCalculatorProgressDialog.show();
			}
		});

		return view;
	}

	/**
	 * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
	 */
	public void startAMapNavi(Marker marker) {
		// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// 调起高德地图导航
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
	}

	/**
	 * 获取当前app的应用名字
	 */
	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		// ToastUtil.show(PoiKeywordSearchActivity.this, infomation);
		MyLog.v("[showSuggestCity]infomation:" + infomation);

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(MainActivity.this,
				new InputtipsListener() {

					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode == 0) {// 正确返回
							List<String> listString = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								listString.add(tipList.get(i).getName());
							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.route_inputs, listString);
							searchText.setAdapter(aAdapter);
							aAdapter.notifyDataSetChanged();
						}
					}
				});
		try {
			// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
			inputTips.requestInputtips(newText, editCity.getText().toString());

		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * POI详情查询回调方法
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int rCode) {

	}

	/**
	 * POI信息查询回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		MyLog.i("[onPoiSearched]Start,rCode=" + rCode);
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					MyLog.i("[MainActivity]onPoiSearched,result.getPois().size():"
							+ poiItems.size());

					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// 清理之前的图标
						PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();

						// 显示到列表
						naviArray = new ArrayList<NaviResultInfo>();
						for (int i = 0; i < poiItems.size(); i++) {

							PoiItem poiItem = poiItems.get(i);
							String title = poiItem.getTitle();
							String address = poiItem.getSnippet();
							double latitude = poiItem.getLatLonPoint()
									.getLatitude();
							double longitude = poiItem.getLatLonPoint()
									.getLongitude();

							double distance = AMapUtil.Distance(longitude,
									latitude, nowLatLng.longitude,
									nowLatLng.latitude);

							NaviResultInfo naviResultInfo = new NaviResultInfo(
									i, title, address, longitude, latitude,
									distance);
							naviArray.add(naviResultInfo);
						}

						naviResultAdapter = new NaviResultAdapter(
								getApplicationContext(), naviArray);

						listResult.setAdapter(naviResultAdapter);

						listResult
								.setOnItemClickListener(new AdapterView.OnItemClickListener() {

									public void onItemClick(
											android.widget.AdapterView<?> parent,
											android.view.View view,
											int position, long id) {
										// 弹出导航方式选择对话框
										NaviResultInfo naviResultInfo = naviArray
												.get(position);
										// 暂存选中位置经纬度，供ActionSheet使用
										listChoiceLatLng = new LatLng(
												naviResultInfo.getLatitude(),
												naviResultInfo.getLongitude());

										ActionSheet.showSheet(
												MainActivity.this,
												new MyOnActionSheetSelected(),
												new MyOnCancelListener(),
												"导航到："
														+ naviArray.get(
																position)
																.getName()
														+ "？");

									}
								});

					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						// 没有结果
						Toast.makeText(getApplicationContext(), "没有结果",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "什么鬼?!",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				// 没有结果
				Toast.makeText(getApplicationContext(), "没有结果",
						Toast.LENGTH_SHORT).show();
			}
		} else if (rCode == 27) {
			// 网络错误
			Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT)
					.show();
			MyLog.e("[onPoiSearched]网络错误");
		} else if (rCode == 32) {
			// Key错误
			MyLog.e("[onPoiSearched]Key错误");
		} else {
			// 其它错误：rCode
			MyLog.e("[onPoiSearched]错误:" + rCode);
		}

	}

	class MyOnActionSheetSelected implements ActionSheet.OnActionSheetSelected {

		@Override
		public void onClick(int whichButton) {

			switch (whichButton) {
			case 0:
				// 模拟导航
				Toast.makeText(getApplicationContext(), "模拟导航",
						Toast.LENGTH_SHORT).show();

				// 起点终点列表
				ArrayList<NaviLatLng> startPointsSim = new ArrayList<NaviLatLng>();
				ArrayList<NaviLatLng> endPointsSim = new ArrayList<NaviLatLng>();

				NaviLatLng endNaviLatLngSim = new NaviLatLng(
						listChoiceLatLng.latitude, listChoiceLatLng.longitude);
				endPointsSim.add(endNaviLatLngSim);

				NaviLatLng startNaviLatLngSim = new NaviLatLng(
						nowLatLng.latitude, nowLatLng.longitude);
				startPointsSim.add(startNaviLatLngSim);

				// DrivingSaveMoney--省钱
				// DrivingShortDistance--最短距离
				// DrivingNoExpressways--不走高速
				// DrivingFastestTime--最短时间
				// DrivingAvoidCongestion--避免拥堵
				isSimulate = true;
				AMapNavi.getInstance(MainActivity.this).calculateDriveRoute(
						startPointsSim, endPointsSim, null,
						AMapNavi.DrivingDefault);
				mRouteCalculatorProgressDialog.show();

				break;

			case 1:
				// 实况导航
				Toast.makeText(getApplicationContext(), "实况导航",
						Toast.LENGTH_SHORT).show();

				// 起点终点列表
				ArrayList<NaviLatLng> startPoints = new ArrayList<NaviLatLng>();
				ArrayList<NaviLatLng> endPoints = new ArrayList<NaviLatLng>();

				NaviLatLng endNaviLatLng = new NaviLatLng(
						listChoiceLatLng.latitude, listChoiceLatLng.longitude);
				endPoints.add(endNaviLatLng);

				NaviLatLng startNaviLatLng = new NaviLatLng(nowLatLng.latitude,
						nowLatLng.longitude);
				startPoints.add(startNaviLatLng);

				// DrivingSaveMoney--省钱
				// DrivingShortDistance--最短距离
				// DrivingNoExpressways--不走高速
				// DrivingFastestTime--最短时间
				// DrivingAvoidCongestion--避免拥堵
				isSimulate = false;
				AMapNavi.getInstance(MainActivity.this).calculateDriveRoute(
						startPoints, endPoints, null, AMapNavi.DrivingDefault);
				mRouteCalculatorProgressDialog.show();

				break;

			case 2:
				// 取消

				break;

			default:
				break;
			}
		}

	}

	class MyOnCancelListener implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
		}

	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog(); // 显示进度框
		currentPage = 0; // 重置页码

		// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query = new PoiSearch.Query(keyWord, "", editCity.getText().toString());
		query.setPageSize(11); // 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索:\n" + keyWord);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btnSearch:
				keyWord = AMapUtil.checkEditText(searchText);
				if ("".equals(keyWord)) {
					// 请输入搜索关键字
					return;
				} else if (!NetworkUtil
						.isNetworkConnected(getApplicationContext())) {
					NetworkUtil.noNetworkHint(getApplicationContext());
				} else {
					doSearchQuery();
				}

				break;

			case R.id.nextButton:
				if (query != null && poiSearch != null && poiResult != null) {
					if (poiResult.getPageCount() - 1 > currentPage) {
						currentPage++;
						query.setPageNum(currentPage);// 设置查后一页
						poiSearch.searchPOIAsyn();
					} else {
						// 没有结果
					}
				}
				break;

			case R.id.layoutNaviTitle:
				setTabNaviShow(true);
				break;

			case R.id.btnBackNavi:
			case R.id.layoutBackNavi:
				setTabNaviShow(false);
				break;

			default:
				break;
			}
		}
	}

	private boolean isTabNaviShow = false;

	/**
	 * 显示或隐藏导航面板
	 */
	private void setTabNaviShow(boolean show) {
		if (show) {
			isTabNaviShow = true;
			layoutNavi.setVisibility(View.VISIBLE);
		} else {
			isTabNaviShow = false;
			layoutNavi.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isTabNaviShow) {
				setTabNaviShow(false);
			} else {
				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		MyLog.v("onMarkerClick");
		marker.showInfoWindow();
		return false;
	}

	/**
	 * 到达目的地后回调函数
	 */
	@Override
	public void onArriveDestination() {
		startSpeak("到达目的地");
	}

	/**
	 * 驾车路径导航到达某个途经点的回调函数。
	 * 
	 * wayID - 到达途径点的编号，标号从1开始，依次累加。
	 */
	@Override
	public void onArrivedWayPoint(int wayId) {
		// TODO Auto-generated method stub
		MyLog.v("onArrivedWayPoint");
	}

	/**
	 * 步行或者驾车路径规划失败后的回调函数
	 */
	@Override
	public void onCalculateRouteFailure(int errCode) {
		mRouteCalculatorProgressDialog.dismiss();

	}

	/**
	 * 步行或者驾车路径规划成功后的回调函数。
	 */
	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		Intent intent = new Intent(MainActivity.this, SimpleNaviActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(AMapUtil.ACTIVITYINDEX, AMapUtil.SIMPLEGPSNAVI);
		bundle.putBoolean(AMapUtil.ISEMULATOR, isSimulate);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 模拟导航停止后回调函数
	 */
	@Override
	public void onEndEmulatorNavi() {
		MyLog.v("onEndEmulatorNavi");
		startSpeak("模拟导航结束");
	}

	/**
	 * 导航播报信息回调函数
	 */
	@Override
	public void onGetNavigationText(int type, String text) {
		// TODO Auto-generated method stub
		startSpeak(text);
		MyLog.v("[onGetNavigationText]type:" + type + ",text:" + text);
	}

	/**
	 * 用户手机GPS设置是否开启的回调函数
	 */
	@Override
	public void onGpsOpenStatus(boolean enabled) {
		// TODO Auto-generated method stub
		MyLog.v("onGpsOpenStatus:" + enabled);
	}

	/**
	 * 导航创建失败时的回调函数
	 */
	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub
		MyLog.v("onInitNaviFailure");
	}

	/**
	 * 导航创建成功时的回调函数。
	 */
	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub
		MyLog.v("onInitNaviSuccess");

	}

	/**
	 * 当GPS位置有更新时的回调函数。
	 */
	@Override
	public void onLocationChange(AMapNaviLocation location) {
		// TODO Auto-generated method stub
		MyLog.v("onLocationChange");
	}

	/**
	 * 导航引导信息回调 naviinfo 是导航信息类
	 * 
	 * 
	 */
	@Override
	public void onNaviInfoUpdate(NaviInfo naviInfo) {
		// TODO Auto-generated method stub
		MyLog.v("[onNaviInfoUpdate]");

		// 获取电子眼距离。
		int cameraDistance = naviInfo.getCameraDistance();

		// 获取电子眼类型。
		naviInfo.getCameraType();

		// 获取摄像头经纬度。
		naviInfo.getCameraCoord();

		// 获取自车经纬度。
		naviInfo.getCoord();

		// 获取自车所在小路段。
		naviInfo.getCurLink();

		// 获取当前位置前一个形状点索引。
		naviInfo.getCurPoint();

		// 获取当前路线名称。
		naviInfo.getCurrentRoadName();

		// 获取当前大路段索引。
		naviInfo.getCurStep();

		// 获取当前路段剩余距离。
		naviInfo.getCurStepRetainDistance();

		// 获取当前路段剩余时间。
		naviInfo.getCurStepRetainTime();

		// 自车方向（单位度）。
		naviInfo.getDirection();

		// 获取导航转向图标。
		naviInfo.getIconType();

		// 获取电子眼限速。
		naviInfo.getLimitSpeed();

		// 导航类型:1 GPS导航更新,2 模拟导航更新。
		naviInfo.getNaviType();

		// 获取下一条路名。
		naviInfo.getNextRoadName();

		// 获取路线剩余距离。
		naviInfo.getPathRetainDistance();

		// 获取路线剩余时间。
		naviInfo.getPathRetainTime();

		// 获取距服务站距离。
		naviInfo.getServiceAreaDistance();

		startSpeak("前方" + cameraDistance + "米有测速摄像头");
		// AMapNavi.getInstance(this).readNaviInfo();

	}

	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub
		MyLog.v("onNaviInfoUpdated");

	}

	/**
	 * 驾车导航时，如果前方遇到拥堵时需要重新计算路径的回调。
	 */
	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub
		MyLog.v("onReCalculateRouteForTrafficJam");

	}

	/**
	 * 步行或驾车导航时,出现偏航后需要重新计算路径的回调函数。
	 */
	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

		MyLog.v("onReCalculateRouteForYaw");
	}

	/**
	 * 启动导航后回调函数。
	 * 
	 * naviType - 导航类型，1 ： 实时导航，2 ：模拟导航。
	 */
	@Override
	public void onStartNavi(int naviType) {
		// TODO Auto-generated method stub
		startSpeak("导航开始");

	}

	/**
	 * 当前方路况光柱信息有更新时回调函数。
	 */
	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub
		MyLog.v("onTrafficStatusUpdate");

	}

	private void startSpeak(String content) {
		Intent intent = new Intent(getApplicationContext(), SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

}