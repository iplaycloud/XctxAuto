package com.xctx.baidunavi.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.xctx.baidunavi.Constant;
import com.xctx.baidunavi.MyApplication;
import com.xctx.baidunavi.R;
import com.xctx.baidunavi.adapter.NaviHistoryAdapter;
import com.xctx.baidunavi.adapter.NaviResultAdapter;
import com.xctx.baidunavi.model.NaviHistory;
import com.xctx.baidunavi.model.NaviHistoryDbHelper;
import com.xctx.baidunavi.model.NaviResultInfo;
import com.xctx.baidunavi.service.SpeakService;
import com.xctx.baidunavi.util.ClickUtil;
import com.xctx.baidunavi.util.MyLog;
import com.xctx.baidunavi.util.NetworkUtil;
import com.xctx.baidunavi.view.ActionSheet;
import com.xctx.baidunavi.view.AudioRecordDialog;

import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanPreference;

public class MainActivity extends FragmentActivity implements
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private BaiduMap baiduMap = null;

	private ArrayAdapter<String> sugAdapter = null;

	private double mLatitude, mLongitude, naviLat, naviLng;
	private LatLng nowLatLng;

	private EditText etHistoryWhere, etHistoryCity;
	private LinearLayout layoutNearAdvice, layoutShowHistory,
			layoutStarContent, layoutStarEditWork, layoutStarEditHome;
	private RelativeLayout layoutNaviVoice, layoutNear, layoutHistory,
			layoutHistoryBack, layoutRoadCondition, layoutStar,
			layoutStarNaviWork, layoutStarNaviHome, layoutResult,
			layoutPagePriv, layoutPageNext, layoutDeleteAllHistory,
			layoutClearText;

	private RelativeLayout layoutRoutePlanPref;
	private RelativeLayout layoutLocateMode;
	private TextView textLocateMode;
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode currentMode;

	// 卫星图
	private RelativeLayout layoutSatellite;
	private ImageView imageSatellite;

	private AudioRecordDialog audioRecordDialog;

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;

	private ListView listResult, listHistory;
	private ArrayList<NaviResultInfo> naviArray;
	private NaviResultAdapter naviResultAdapter;

	private boolean isResultListShow = false;
	private boolean isNearLayoutShow = false;
	private boolean isHistoryLayoutShow = false;
	private boolean isStarPannelShow = false;

	private Button btnHistoryNavi, btnCloseHistory;

	private Button btnClearText;

	private NaviHistoryDbHelper naviDb;
	private NaviHistoryAdapter naviHistoryAdapter;
	private ArrayList<NaviHistory> naviHistoryArray;

	private ImageView imageRoadCondition, imgVoiceSearch;

	private ProgressBar progressVoice;

	private MapView mMapView;
	private LocationClient mLocationClient;
	private SharedPreferences preference;
	private Editor editor;
	private String naviDesFromVoice = "";
	private boolean isVoiceIntentInTime = false;

	private boolean isFirstLoc = true;

	private String strAuthFail, strInitFail;

	/**
	 * 设置每页容量，默认为每页条数
	 */
	private int pageCapacity = 5;
	private int pageIndex = 0;

	private String doingWhere; // 下一页搜索
	private LatLng doingLatLng;
	private boolean doingIsNear;
	private boolean isClick = false;

	// 点击地图，开始导航
	private LatLng clickLatLng; // 点击的LatLng
	private String clickName; // 点击位置的Name

	// 是否是被语音调起
	private boolean isFromVoice = false;

	// 语音聆听动画
	private ImageView imageVoiceListen;

	/** 无导航搜索历史记录 **/
	private TextView textNoSearchRecord;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// 导航实例
		initialBaiduNaviInstance();

		preference = getSharedPreferences(Constant.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		editor = preference.edit();

		strAuthFail = getResources().getString(R.string.hint_navi_auth_fail);
		strInitFail = getResources().getString(R.string.hint_navi_init_fail);

		naviDb = new NaviHistoryDbHelper(getApplicationContext());

		audioRecordDialog = new AudioRecordDialog(MainActivity.this);

		// 获取当前经纬度
		mLatitude = Double
				.parseDouble(preference.getString("latitude", "0.00"));
		mLongitude = Double.parseDouble(preference.getString("longitude",
				"0.00"));

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		Button btnToNearFromResult = (Button) findViewById(R.id.btnToNearFromResult);
		btnToNearFromResult.setOnClickListener(new MyOnClickListener());

		initialLayout();

	}

	/**
	 * 接收思必驰发送的语音
	 */
	private VoiceCommandReceiver voiceCommandReceiver;

	public class VoiceCommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.tchip.choiceCommand")) {
				String command = intent.getStringExtra("command");
				MyLog.v("[VoiceCommandReceiver]Command:" + command);
				if (command != null && command.trim().length() > 0) {
					if (isFromVoice) {
						int commandId = getVoiceCommandId(command);
						MyLog.v("[VoiceCommandReceiver]commandId:" + commandId);
						if (commandId == -1) {
							// 错误指令
							sendBroadcast(new Intent("com.tchip.restartAIAsr"));
							imageVoiceListen.setVisibility(View.VISIBLE);
						} else {
							// Normal Mode
							if (commandId >= 0 && commandId <= 4) {
								// 1-5个，需考虑结果不足5个情况

								if (!MyApplication.isBaiduNaviAuthSuccess) {
									Log.e(Constant.TAG, "Navigation:Auth Fail");
									Toast.makeText(getApplicationContext(),
											strAuthFail, Toast.LENGTH_SHORT)
											.show();
									showFailedDialog(strAuthFail);
								} else if (!MyApplication.isBaiduNaviInitialSuccess) {
									Log.e(Constant.TAG,
											"Navigation:Initial Fail");
									Toast.makeText(getApplicationContext(),
											strInitFail, Toast.LENGTH_SHORT)
											.show();
									showFailedDialog(strInitFail);
								} else {
									int naviArraySize = naviArray.size();
									MyLog.v("[VoiceCommandReceiver]naviArraySize:"
											+ naviArraySize);
									if (commandId + 1 <= naviArraySize) {

										// 隐藏搜索列表界面
										layoutResult.setVisibility(View.GONE);
										pageIndex = 0; // 重设搜索结果页码为0
										isResultListShow = false;
										setLayoutHistoryVisibility(false);

										// 开始导航
										routeplanToNavi(
												CoordinateType.GCJ02,
												nowLatLng.latitude,
												nowLatLng.longitude,
												getResources().getString(
														R.string.location_here),
												naviArray.get(commandId)
														.getLatitude(),
												naviArray.get(commandId)
														.getLongitude(),
												naviArray.get(commandId)
														.getName());

									} else {
										String strNoSuchNumber = "没有该序号，请重选";
										Toast.makeText(context,
												strNoSuchNumber,
												Toast.LENGTH_SHORT).show();
										// startSpeak(strNoSuchNumber);

										// 重新聆听
										sendBroadcast(new Intent(
												"com.tchip.restartAIAsr")
												.putExtra("speak",
														strNoSuchNumber));
										imageVoiceListen
												.setVisibility(View.VISIBLE);
									}
								}

							} else if (commandId == 5) {
								// 上一页
								changePage(-1);
								Toast.makeText(getApplicationContext(), "上一页",
										Toast.LENGTH_SHORT).show();

								String strHint = "选择或取消";
								sendBroadcast(new Intent(
										"com.tchip.restartAIAsr").putExtra(
										"speak", strHint));
								imageVoiceListen.setVisibility(View.VISIBLE);

							} else if (commandId == 6) {
								// 下一页
								Toast.makeText(getApplicationContext(), "下一页",
										Toast.LENGTH_SHORT).show();
								changePage(1);

								String strHint = "选择或取消";

								sendBroadcast(new Intent(
										"com.tchip.restartAIAsr").putExtra(
										"speak", strHint));

								imageVoiceListen.setVisibility(View.VISIBLE);
							} else if (commandId == 8) {
								// 取消
								Toast.makeText(getApplicationContext(),
										command, Toast.LENGTH_SHORT).show();

								// 隐藏搜索列表界面
								layoutResult.setVisibility(View.GONE);
								pageIndex = 0; // 重设搜索结果页码为0
								isResultListShow = false;
								setLayoutHistoryVisibility(false);
							} else {
								String strHint = "选择或取消";
								sendBroadcast(new Intent(
										"com.tchip.restartAIAsr").putExtra(
										"speak", strHint));
							}
						}

					} else {
						MyLog.v("[VoiceCommandReceiver]isFromVoice:false");
					}

				} else {
					// 错误指令：空指令
					MyLog.v("[VoiceCommandReceiver]Command is null");
				}

			}
		}
	}

	/**
	 * 获取语音命令在string-array中的位置
	 * 
	 * @param command
	 * @return
	 */
	private int getVoiceCommandId(String command) {

		int position = -1;

		String[] arrayCommand1 = getResources().getStringArray(
				R.array.array_voice_command_1);
		for (int i = 0; i < arrayCommand1.length; i++) {
			if (arrayCommand1[i].equals(command)) {
				return i;
			}
		}

		String[] arrayCommand2 = getResources().getStringArray(
				R.array.array_voice_command_2);
		for (int i = 0; i < arrayCommand2.length; i++) {
			if (arrayCommand2[i].equals(command)) {
				return i;
			}
		}

		String[] arrayCommand3 = getResources().getStringArray(
				R.array.array_voice_command_3);
		for (int i = 0; i < arrayCommand3.length; i++) {
			if (arrayCommand3[i].equals(command)) {
				return i;
			}
		}

		return position;
	}

	private void scaleMap(boolean isBigger) {
		float nowZoomLevel = baiduMap.getMapStatus().zoom;
		MyLog.v("[scaleMap]nowZoomLevel:" + nowZoomLevel);
		if (isBigger) {
			// 放大
			if (nowZoomLevel + 1 >= 21.0f) {
				Toast.makeText(MainActivity.this, "已最大", Toast.LENGTH_SHORT)
						.show();
			} else {
				MapStatusUpdate msu = MapStatusUpdateFactory
						.zoomTo(nowZoomLevel + 1);
				baiduMap.animateMapStatus(msu);
				Toast.makeText(MainActivity.this, "地图已放大", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			if (nowZoomLevel - 1 <= 2.0f) {
				Toast.makeText(MainActivity.this, "已最小", Toast.LENGTH_SHORT)
						.show();
			} else {
				MapStatusUpdate msu = MapStatusUpdateFactory
						.zoomTo(nowZoomLevel - 1);
				baiduMap.animateMapStatus(msu);
				Toast.makeText(MainActivity.this, "地图已缩小", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void initialLayout() {
		layoutShowHistory = (LinearLayout) findViewById(R.id.layoutShowHistory);
		layoutShowHistory.setOnClickListener(new MyOnClickListener());

		layoutNaviVoice = (RelativeLayout) findViewById(R.id.layoutNaviVoice);
		layoutNaviVoice.setOnClickListener(new MyOnClickListener());

		layoutNear = (RelativeLayout) findViewById(R.id.layoutNear);
		layoutNear.setOnClickListener(new MyOnClickListener());

		// 周边搜索
		layoutNearAdvice = (LinearLayout) findViewById(R.id.layoutNearAdvice);
		RelativeLayout layoutNearOilStation = (RelativeLayout) findViewById(R.id.layoutNearOilStation);
		layoutNearOilStation.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearParking = (RelativeLayout) findViewById(R.id.layoutNearParking);
		layoutNearParking.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNear4s = (RelativeLayout) findViewById(R.id.layoutNear4s);
		layoutNear4s.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearBank = (RelativeLayout) findViewById(R.id.layoutNearBank);
		layoutNearBank.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutShop = (RelativeLayout) findViewById(R.id.layoutShop);
		layoutShop.setOnClickListener(new MyOnClickListener());

		RelativeLayout layoutNearHotel = (RelativeLayout) findViewById(R.id.layoutNearHotel);
		layoutNearHotel.setOnClickListener(new MyOnClickListener());

		listResult = (ListView) findViewById(R.id.listResult);

		// 清空历史记录
		layoutDeleteAllHistory = (RelativeLayout) findViewById(R.id.layoutDeleteAllHistory);
		layoutDeleteAllHistory.setOnClickListener(new MyOnClickListener());
		textNoSearchRecord = (TextView) findViewById(R.id.textNoSearchRecord);

		mMapView = (MapView) findViewById(R.id.map);

		// 去掉百度Logo
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView) {
				child.setVisibility(View.INVISIBLE);
			}
		}
		baiduMap = mMapView.getMap();

		// 地图加载完毕回调
		baiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				UiSettings uiSettings = baiduMap.getUiSettings();
				// uiSettings.setCompassEnabled(false); // 是否显示指南针
				// 设置指南针的位置，在 onMapLoadFinish 后生效
				uiSettings.setCompassPosition(new Point(30, 380));
				// uiSettings.setOverlookingGesturesEnabled(false); //设置是否允许俯视手势
				// uiSettings.setRotateGesturesEnabled(false); //设置是否允许旋转手势
				// uiSettings.setScrollGesturesEnabled(false); //设置是否允许拖拽手势
				// uiSettings.setZoomGesturesEnabled(false); //设置是否允许缩放手势

			}
		});

		// 开启定位图层
		baiduMap.setMyLocationEnabled(true);

		// 初始化地图位置,设置nowLoction数据以防NullPointer
		nowLatLng = new LatLng(mLatitude, mLongitude);
		// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(nowLatLng);
		// baiduMap.animateMapStatus(u);

		// 默认打开路况
		baiduMap.setTrafficEnabled(false);

		// 设置地图放大级别 0-19
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14);
		baiduMap.animateMapStatus(msu);

		baiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				setStarPannelVisibility(false);
				clickLatLng = mapPoi.getPosition();
				MyLog.v("[onMapPoiClick]Lat:" + clickLatLng.latitude + ",Lng:"
						+ clickLatLng.longitude + ",Name" + mapPoi.getName());
				addMakerToMap(clickLatLng);
				isClick = true;
				ActionSheet.showSheet(MainActivity.this,
						new MyOnActionSheetSelected(),
						new MyOnCancelListener(), "导航到选中地点："
								+ mapPoi.getName().replace("\\", "") + "？");
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				setStarPannelVisibility(false);

				clickLatLng = latLng;
				addMakerToMap(clickLatLng);

				MyLog.v("[onMapClick]Lat:" + clickLatLng.latitude + ",Lng:"
						+ clickLatLng.longitude);
				ActionSheet.showSheet(MainActivity.this,
						new MyOnActionSheetSelected(),
						new MyOnCancelListener(), "导航到选中地点？");
			}
		});

		// 导航搜索历史记录
		layoutHistory = (RelativeLayout) findViewById(R.id.layoutHistory);
		layoutHistoryBack = (RelativeLayout) findViewById(R.id.layoutHistoryBack);
		layoutHistoryBack.setOnClickListener(new MyOnClickListener());
		btnCloseHistory = (Button) findViewById(R.id.btnCloseHistory);
		btnCloseHistory.setOnClickListener(new MyOnClickListener());

		etHistoryWhere = (EditText) findViewById(R.id.etHistoryWhere);
		etHistoryWhere.addTextChangedListener(new TextWatcher() {

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
				int length = s.length();
				if (length > 0) {
					btnClearText.setVisibility(View.VISIBLE);
				} else {
					btnClearText.setVisibility(View.GONE);
				}
			}
		});

		etHistoryCity = (EditText) findViewById(R.id.etHistoryCity);
		btnHistoryNavi = (Button) findViewById(R.id.btnHistoryNavi);
		btnHistoryNavi.setOnClickListener(new MyOnClickListener());

		listHistory = (ListView) findViewById(R.id.listHistory);

		// 路况
		layoutRoadCondition = (RelativeLayout) findViewById(R.id.layoutRoadCondition);
		layoutRoadCondition.setOnClickListener(new MyOnClickListener());

		imageRoadCondition = (ImageView) findViewById(R.id.imageRoadCondition);

		// 语音识别进度
		progressVoice = (ProgressBar) findViewById(R.id.progressVoice);
		imgVoiceSearch = (ImageView) findViewById(R.id.imgVoiceSearch);

		// 收藏
		layoutStarContent = (LinearLayout) findViewById(R.id.layoutStarContent);

		layoutStar = (RelativeLayout) findViewById(R.id.layoutStar);
		layoutStar.setOnClickListener(new MyOnClickListener());

		layoutStarNaviWork = (RelativeLayout) findViewById(R.id.layoutStarNaviWork);
		layoutStarNaviWork.setOnClickListener(new MyOnClickListener());

		layoutStarEditWork = (LinearLayout) findViewById(R.id.layoutStarEditWork);
		layoutStarEditWork.setOnClickListener(new MyOnClickListener());

		layoutStarEditHome = (LinearLayout) findViewById(R.id.layoutStarEditHome);
		layoutStarEditHome.setOnClickListener(new MyOnClickListener());

		layoutStarNaviHome = (RelativeLayout) findViewById(R.id.layoutStarNaviHome);
		layoutStarNaviHome.setOnClickListener(new MyOnClickListener());

		// 更新离线地图
		RelativeLayout layoutOffline = (RelativeLayout) findViewById(R.id.layoutOffline);
		layoutOffline.setOnClickListener(new MyOnClickListener());

		// 回到当前位置
		RelativeLayout layoutLocate = (RelativeLayout) findViewById(R.id.layoutLocate);
		layoutLocate.setOnClickListener(new MyOnClickListener());

		// 路线规划策略
		RelativeLayout layoutRoutePlanPref = (RelativeLayout) findViewById(R.id.layoutRoutePlanPref);
		layoutRoutePlanPref.setOnClickListener(new MyOnClickListener());

		layoutResult = (RelativeLayout) findViewById(R.id.layoutResult);
		layoutResult.setOnClickListener(new MyOnClickListener());

		layoutPagePriv = (RelativeLayout) findViewById(R.id.layoutPagePriv);
		layoutPagePriv.setOnClickListener(new MyOnClickListener());

		layoutPageNext = (RelativeLayout) findViewById(R.id.layoutPageNext);
		layoutPageNext.setOnClickListener(new MyOnClickListener());

		btnClearText = (Button) findViewById(R.id.btnClearText);
		btnClearText.setOnClickListener(new MyOnClickListener());
		layoutClearText = (RelativeLayout) findViewById(R.id.layoutClearText);
		layoutClearText.setOnClickListener(new MyOnClickListener());

		// 语音聆听提示动画
		imageVoiceListen = (ImageView) findViewById(R.id.imageVoiceListen);
		imageVoiceListen.setVisibility(View.GONE);

		// 普通，跟随，罗盘模式
		layoutLocateMode = (RelativeLayout) findViewById(R.id.layoutLocateMode);
		layoutLocateMode.setOnClickListener(new MyOnClickListener());
		textLocateMode = (TextView) findViewById(R.id.textLocateMode);

		// 卫星图
		layoutSatellite = (RelativeLayout) findViewById(R.id.layoutSatellite);
		layoutSatellite.setOnClickListener(new MyOnClickListener());
		imageSatellite = (ImageView) findViewById(R.id.imageSatellite);
	}

	class MyOnActionSheetSelected implements ActionSheet.OnActionSheetSelected {

		@Override
		public void onClick(int whichButton) {

			switch (whichButton) {
			case 0:
				// 开始导航
				routeplanToNavi(CoordinateType.GCJ02, nowLatLng.latitude,
						nowLatLng.longitude,
						getResources().getString(R.string.location_here),
						clickLatLng.latitude, clickLatLng.longitude, "");
				break;

			case 1:
				// 取消导航

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

	private void addMakerToMap(LatLng latLng) {
		baiduMap.clear();
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.ui_marker_select);

		OverlayOptions ooA = new MarkerOptions().position(latLng)
				.icon(bitmapDescriptor).zIndex(9).draggable(true);
		baiduMap.addOverlay(ooA);
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
		option.setOpenGps(true); // 打开gps
		mLocationClient.setLocOption(option);

		mLocationClient.start();
	}

	private void whereAmI(LatLng latLng) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		baiduMap.animateMapStatus(u);
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// MapView 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(0)
						// accuracy设为0去掉蓝色精度圈，RAW:.accuracy(location.getRadius())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				baiduMap.setMyLocationData(locData);

				// 更新当前位置用作导航起点
				nowLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				// 初次定位和移动过程，更新当前位置到地图中心
				if (isFirstLoc || location.getSpeed() > 0) {
					isFirstLoc = false;
					MapStatusUpdate u = MapStatusUpdateFactory
							.newLatLng(nowLatLng);
					baiduMap.animateMapStatus(u);
				}

				// 存储位置到SharedPreference
				editor.putString("latitude", "" + location.getLatitude());
				editor.putString("longitude", "" + location.getLongitude());
				editor.commit();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutSatellite:
				int nowMaptype = baiduMap.getMapType();
				switch (nowMaptype) {
				case BaiduMap.MAP_TYPE_NORMAL:
					baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					imageSatellite.setImageDrawable(getResources().getDrawable(
							R.drawable.main_map_icon_satellite_on));
					break;

				case BaiduMap.MAP_TYPE_SATELLITE:
					baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					imageSatellite.setImageDrawable(getResources().getDrawable(
							R.drawable.main_map_icon_satellite_off));
					break;

				default:
					break;
				}
				break;

			case R.id.layoutLocateMode:
				switch (currentMode) {
				case NORMAL:
					textLocateMode.setText("跟随");
					currentMode = LocationMode.FOLLOWING;
					baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
							currentMode, true, null));
					break;

				case COMPASS:
					textLocateMode.setText("普通");
					currentMode = LocationMode.NORMAL;
					baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
							currentMode, true, null));
					break;

				case FOLLOWING:
					textLocateMode.setText("罗盘");
					currentMode = LocationMode.COMPASS;
					baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
							currentMode, true, null));
					break;
				}
				break;

			case R.id.layoutDeleteAllHistory:
				// 清空历史记录
				naviDb.deleteAllNaviHistory();
				setLayoutHistoryVisibility(false);
				setLayoutHistoryVisibility(true);
				break;

			case R.id.btnClearText:
			case R.id.layoutClearText:
				etHistoryWhere.setText("");
				break;

			case R.id.btnToNearFromResult:
				onBackKeyPressed();
				break;

			case R.id.layoutNaviVoice:
				isNearLayoutShow = false;
				layoutNearAdvice.setVisibility(View.GONE);

				startVoiceUnderstand();
				break;

			case R.id.layoutShowHistory:
				setLayoutHistoryVisibility(!isHistoryLayoutShow);
				break;

			// 周边搜索
			case R.id.layoutNear:
				setLayoutNearVisibility(!isNearLayoutShow);
				break;

			case R.id.layoutNearOilStation:
				searchNear(getResources().getString(R.string.near_oil_station));
				break;

			case R.id.layoutNearParking:
				searchNear(getResources().getString(R.string.near_parking));
				break;

			case R.id.layoutNear4s:
				searchNear(getResources().getString(R.string.near_4s));
				break;

			case R.id.layoutNearBank:
				searchNear(getResources().getString(R.string.near_atm));
				break;

			case R.id.layoutShop:
				searchNear(getResources().getString(R.string.near_market));
				break;

			case R.id.layoutNearHotel:
				searchNear(getResources().getString(R.string.near_hotel));
				break;

			// 历史记录
			case R.id.layoutHistoryBack:
			case R.id.btnCloseHistory:
				setLayoutHistoryVisibility(false);
				break;

			case R.id.btnHistoryNavi:
				String strContent = etHistoryWhere.getText().toString();
				if (strContent.trim().length() > 0 && strContent != null) {
					isNearLayoutShow = false;
					layoutNearAdvice.setVisibility(View.GONE);

					pageIndex = 0; // 重置页码
					startSearchPlace(strContent, nowLatLng, false, false);
				}
				break;

			case R.id.layoutRoadCondition:
				openOrCloseRoadCondition();
				break;

			case R.id.layoutStar:
				setStarPannelVisibility(!isStarPannelShow);
				break;

			case R.id.layoutStarNaviWork:
				naviWork();
				break;

			case R.id.layoutStarEditWork:
				setStarPlace(StarType.TYPE_WORK);
				break;

			case R.id.layoutStarNaviHome:
				// 导航回家
				naviHome();
				break;

			case R.id.layoutStarEditHome:
				setStarPlace(StarType.TYPE_HOME);
				break;

			case R.id.layoutOffline:
				Intent intentOffline = new Intent(MainActivity.this,
						OfflineBaiduMapUpdateActivity.class);
				startActivity(intentOffline);
				break;

			case R.id.layoutLocate:
				whereAmI(nowLatLng);
				break;

			case R.id.layoutRoutePlanPref:
				// 路线规划策略
				Intent intentRoutePlanPref = new Intent(MainActivity.this,
						SetRoutePlanPreferenceActivity.class);
				startActivity(intentRoutePlanPref);
				break;

			case R.id.layoutPagePriv:
				changePage(-1);
				break;

			case R.id.layoutPageNext:
				changePage(1);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 导航去公司
	 */
	private void naviWork() {
		// 导航到公司
		setStarPannelVisibility(false);

		if (preference.getBoolean("workSet", false)) {
			String workAddress = preference.getString("workAddress", "");

			double workLat = Double.parseDouble(preference.getString("workLat",
					"0.00"));
			double workLng = Double.parseDouble(preference.getString("workLng",
					"0.00"));
			if (!MyApplication.isBaiduNaviAuthSuccess) {
				MyLog.e("Navigation:Auth Fail");
				Toast.makeText(getApplicationContext(), strAuthFail,
						Toast.LENGTH_SHORT).show();
				showFailedDialog(strAuthFail);
			} else if (!MyApplication.isBaiduNaviInitialSuccess) {
				MyLog.e("Navigation:Initial Fail");
				Toast.makeText(getApplicationContext(), strInitFail,
						Toast.LENGTH_SHORT).show();
				showFailedDialog(strInitFail);
			} else {
				routeplanToNavi(CoordinateType.GCJ02, nowLatLng.latitude,
						nowLatLng.longitude,
						getResources().getString(R.string.location_here),
						workLat, workLng, workAddress);
			}
		} else {
			// 未设置，跳转到设置界面
			String strSetWorkLocation = getResources().getString(
					R.string.set_location_company);
			startSpeak(strSetWorkLocation);
			Toast.makeText(getApplicationContext(), strSetWorkLocation,
					Toast.LENGTH_SHORT).show();
			setStarPlace(StarType.TYPE_WORK);
		}
	}

	/**
	 * 导航回家
	 */
	private void naviHome() {
		setStarPannelVisibility(false);
		if (preference.getBoolean("homeSet", false)) {
			String homeAddress = preference.getString("homeAddress", "");

			double homeLat = Double.parseDouble(preference.getString("homeLat",
					"0.00"));
			double homeLng = Double.parseDouble(preference.getString("homeLng",
					"0.00"));

			if (!MyApplication.isBaiduNaviAuthSuccess) {
				MyLog.e("Navigation:Auth Fail");
				Toast.makeText(getApplicationContext(), strAuthFail,
						Toast.LENGTH_SHORT).show();
				showFailedDialog(strAuthFail);
				// audioRecordDialog.showErrorDialog(strAuthFail);
				// new Thread(new dismissDialogThread()).start();

			} else if (!MyApplication.isBaiduNaviInitialSuccess) {
				MyLog.e("Navigation:Initial Fail");
				Toast.makeText(getApplicationContext(), strInitFail,
						Toast.LENGTH_SHORT).show();
				showFailedDialog(strInitFail);
				// audioRecordDialog.showErrorDialog(strInitFail);
				// new Thread(new dismissDialogThread()).start();
			} else {
				routeplanToNavi(CoordinateType.GCJ02, nowLatLng.latitude,
						nowLatLng.longitude,
						getResources().getString(R.string.location_here),
						homeLat, homeLng, homeAddress);
			}
		} else {
			// 未设置，跳转到设置界面
			String strSetHomeLocation = getResources().getString(
					R.string.set_location_home);
			startSpeak(strSetHomeLocation);
			Toast.makeText(getApplicationContext(), strSetHomeLocation,
					Toast.LENGTH_SHORT).show();
			setStarPlace(StarType.TYPE_HOME);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackKeyPressed();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/**
	 * 返回
	 */
	private void onBackKeyPressed() {
		if (isStarPannelShow || isNearLayoutShow || isResultListShow
				|| isHistoryLayoutShow) {
			setStarPannelVisibility(false);
			setLayoutNearVisibility(false);
			setLayoutHistoryVisibility(false);
		} else {
			backToMain();
		}
	}

	private void backToMain() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	public void exitApp() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public class dismissDialogThread implements Runnable {
		@Override
		public void run() {
			synchronized (dismissDialogHandler) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message messageEject = new Message();
				messageEject.what = 1;
				dismissDialogHandler.sendMessage(messageEject);
			}
		}
	}

	final Handler dismissDialogHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				audioRecordDialog.dismissDialog();
				break;

			default:
				break;
			}
		}
	};

	enum StarType {
		TYPE_HOME, TYPE_WORK
	}

	private void setStarPlace(StarType type) {
		Intent intent = new Intent(MainActivity.this,
				SetStarPlaceActivity.class);
		if (type == StarType.TYPE_HOME) {
			intent.putExtra("starType", 1);
		} else if (type == StarType.TYPE_WORK) {
			intent.putExtra("starType", 0);
		}
		startActivity(intent);
		setStarPannelVisibility(false);
	}

	private void searchNear(String content) {
		isNearLayoutShow = false;
		layoutNearAdvice.setVisibility(View.GONE);

		pageIndex = 0; // 重设搜索结果页码为0
		startSearchPlace(content, nowLatLng, true, false);
	}

	/**
	 * 显示或关闭收藏界面
	 */
	private void setStarPannelVisibility(boolean isShow) {
		if (isShow) {
			setLayoutNearVisibility(false);
			setLayoutHistoryVisibility(false);

			isStarPannelShow = true;
			layoutStarContent.setVisibility(View.VISIBLE);
		} else {
			isStarPannelShow = false;
			layoutStarContent.setVisibility(View.GONE);
		}
	}

	/**
	 * 打开或关闭路况显示
	 */
	private void openOrCloseRoadCondition() {
		if (baiduMap.isTrafficEnabled()) {
			baiduMap.setTrafficEnabled(false);
			imageRoadCondition.setImageDrawable(getResources().getDrawable(
					R.drawable.main_icon_roadcondition_off));
		} else {
			baiduMap.setTrafficEnabled(true);
			imageRoadCondition.setImageDrawable(getResources().getDrawable(
					R.drawable.main_icon_roadcondition_on));
		}
	}

	/**
	 * 显示或隐藏历史记录
	 */
	private void setLayoutHistoryVisibility(boolean isShow) {
		if (isShow) {
			// 缩放地图模式

			setStarPannelVisibility(false);
			setLayoutNearVisibility(false);

			isHistoryLayoutShow = true;
			layoutHistory.setVisibility(View.VISIBLE);
			// listHistory.setVisibility(View.VISIBLE);
			naviHistoryArray = naviDb.getAllNaviHistory();
			if (naviHistoryArray.size() < 1) {
				layoutDeleteAllHistory.setVisibility(View.INVISIBLE);
				textNoSearchRecord.setVisibility(View.VISIBLE);
			} else {
				layoutDeleteAllHistory.setVisibility(View.VISIBLE);
				textNoSearchRecord.setVisibility(View.INVISIBLE);
			}

			naviHistoryAdapter = new NaviHistoryAdapter(
					getApplicationContext(), naviHistoryArray);
			listHistory.setAdapter(naviHistoryAdapter);

			naviHistoryAdapter.registerDataSetObserver(new MyDataSetObserver());

			listHistory
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// 将内容更新到EditText
							String strHistory = naviHistoryArray.get(position)
									.getKey();
							etHistoryWhere.setText(strHistory);

							String strCity = naviHistoryArray.get(position)
									.getCity();
							etHistoryCity.setText(strCity);

							pageIndex = 0; // 重设搜索结果页码为0
							startSearchPlace(strHistory, nowLatLng, false,
									false);
						}
					});
		} else {
			isHistoryLayoutShow = false;
			isResultListShow = false;
			layoutHistory.setVisibility(View.GONE);

			layoutResult.setVisibility(View.GONE);

			// 取消语音聆听
			// if (isFromVoice) {
			sendBroadcast(new Intent("com.tchip.iflyTTSCompletedMessage")
					.putExtra("back", true));
			isFromVoice = false;
			imageVoiceListen.setVisibility(View.GONE);
			// }

		}
	}

	class MyDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			super.onChanged();
			MyLog.v("[MyDataSetObserver]onChanged");
			setLayoutHistoryVisibility(isHistoryLayoutShow);
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			MyLog.v("[MyDataSetObserver]onInvalidated");
		}

	}

	/**
	 * 显示或隐藏周边搜索
	 */
	private void setLayoutNearVisibility(boolean isShow) {
		if (isShow) {
			setStarPannelVisibility(false);
			setLayoutHistoryVisibility(false);

			layoutNearAdvice.setVisibility(View.VISIBLE);
			isNearLayoutShow = true;
		} else {
			isNearLayoutShow = false;
			layoutNearAdvice.setVisibility(View.GONE);
		}
	}

	/**
	 * 初次启动若导航实例未初始化，则无法搜搜
	 * 
	 * MyApplication.isBaiduNaviInitialSuccess为false
	 * 
	 */
	class StartSearchKeyFromVoiceThread implements Runnable {

		@Override
		public void run() {

			try {
				Thread.sleep(2000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message messageSearch = new Message();
			messageSearch.what = 1;
			startSearchKeyFromVoiceHandler.sendMessage(messageSearch);
		}

	}

	final Handler startSearchKeyFromVoiceHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				audioRecordDialog.dismissDialog();
				startSearchPlace(naviDesFromVoice, nowLatLng, false, false);
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onPause() {
		Log.v(Constant.TAG, "NavigationActivity:onPause");
		mMapView.onPause();

		// 停止语音监听
		if (isFromVoice) {
			sendBroadcast(new Intent("com.tchip.iflyTTSCompletedMessage")
					.putExtra("back", true));
			isFromVoice = false;
			imageVoiceListen.setVisibility(View.GONE);
		}

		if (voiceCommandReceiver != null) {
			unregisterReceiver(voiceCommandReceiver);
		}

		// 销毁定位
		mLocationClient.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.v(Constant.TAG, "NavigationActivity:onResume");
		mMapView.onResume();

		// LocationMode 跟随：FOLLOWING 普通：NORMAL 罗盘：COMPASS
		currentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				currentMode, true, null));
		InitLocation(
				com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy,
				"bd09ll", 3000, true);

		// 注册语音监听
		voiceCommandReceiver = new VoiceCommandReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.tchip.choiceCommand");
		registerReceiver(voiceCommandReceiver, filter);

		// 接收来自语音的导航目的地
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			naviDesFromVoice = extras.getString("destionation");
			long sendTime = extras.getLong("time");

			MyLog.v("[onResume]Voice:" + naviDesFromVoice);
			isVoiceIntentInTime = ClickUtil.isVoiceIntentInTime(sendTime);

			if (isVoiceIntentInTime) {
				MyLog.v("[onResume]Voice:" + naviDesFromVoice);

				if (naviDesFromVoice.trim().length() > 0
						&& naviDesFromVoice != null) {

					if ("家".equals(naviDesFromVoice)) {
						naviHome();
						sendBroadcast(new Intent(
								"com.tchip.iflyTTSCompletedMessage").putExtra(
								"back", true));
					} else if ("公司".equals(naviDesFromVoice)) {
						naviWork();
						sendBroadcast(new Intent(
								"com.tchip.iflyTTSCompletedMessage").putExtra(
								"back", true));
					} else {
						isFromVoice = true;
						setLayoutHistoryVisibility(true);
						setDestinationText(naviDesFromVoice);

						pageIndex = 0; // 重设搜索结果页码为0
						if (MyApplication.isBaiduNaviInitialSuccess) {
							startSearchPlace(naviDesFromVoice, nowLatLng,
									false, false);
						} else {
							audioRecordDialog.showLoadDialog();
							new Thread(new StartSearchKeyFromVoiceThread())
									.start();
						}
					}
				}
			} else {
				MyLog.v("[onResume]Voice:Voice intent is old");
			}
		} else {
			isFromVoice = false;
			Log.v(Constant.TAG, "[Voice]extras == null");
		}

		super.onResume();
	}

	/**
	 * 初始化导航实例
	 */
	private String mSDCardPath = null;
	private static final String APP_FOLDER_NAME = "CarLauncher";

	private void initialBaiduNaviInstance() {
		if (initDirs()) {
		}
		initBaiduNavi();
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	String authinfo = null;
	public static final String ROUTE_PLAN_NODE = "routePlanNode";

	private void initBaiduNavi() {
		MyLog.v("Navi Instance is Initialing...");
		BaiduNaviManager.getInstance().setNativeLibraryPath(
				mSDCardPath + "/BaiduNaviSDK_SO");
		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
				new NaviInitListener() {
					@Override
					public void onAuthResult(int status, String msg) {
						if (0 == status) {
							authinfo = "Success!";
							MyApplication.isBaiduNaviAuthSuccess = true;
						} else {
							authinfo = "Fail:" + msg;
							MyApplication.isBaiduNaviAuthSuccess = false;
						}

						MyLog.v("Baidu Navi:Key auth " + authinfo);
					}

					public void initSuccess() {
						// 导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
						MyApplication.isBaiduNaviInitialSuccess = true;
						MyLog.v("Baidu Navi:Initial Success!");
					}

					public void initStart() {
						MyLog.v("Baidu Navi:Initial Start!");
					}

					public void initFailed() {
						MyApplication.isBaiduNaviInitialSuccess = false;
						MyLog.v("Baidu Navi:Initial Fail!");
					}
				}, /* null */mTTSCallback);
	}

	BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

		@Override
		public void stopTTS() {

		}

		@Override
		public void resumeTTS() {

		}

		@Override
		public void releaseTTSPlayer() {

		}

		/**
		 * text - 播报内容
		 * 
		 * bPreempt - 是否抢占播报
		 */
		@Override
		public int playTTSText(String text, int bPreempt) {
			speakNaviText(text);
			MyLog.v("[playTTSText]text:" + text + ",bPreempt:" + bPreempt);
			// Toast.makeText(MainActivity.this, text,
			// Toast.LENGTH_SHORT).show();
			if (text.contains("左转") && text.contains("右转")) {

			} else if (text.contains("左转")) {

			} else if (text.contains("右转")) {

			}
			return 0;
		}

		/**
		 * 挂电话，应该恢复TTS播报
		 */
		@Override
		public void phoneHangUp() {

		}

		/**
		 * 电话铃声响起, 应该关闭TTS播报
		 */
		@Override
		public void phoneCalling() {

		}

		@Override
		public void pauseTTS() {

		}

		@Override
		public void initTTSPlayer() {

		}

		@Override
		public int getTTSState() {
			return 1;
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();

		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * 解析出目的地中的城市
	 * 
	 * @param content
	 * @return
	 */
	private String[] formatDestination(String destination) {
		String[] desExtractCity = new String[2];
		desExtractCity[0] = "";
		desExtractCity[1] = destination;

		if (destination != null && destination.trim().length() >= 2) {
			if (destination.endsWith("大学") || destination.endsWith("学院")) {
				String[] arrayProvinceUniversityName = getResources()
						.getStringArray(R.array.university_of_province_name);

				String[] arrayProvinceUniversityCity = getResources()
						.getStringArray(R.array.university_of_province_city);
				for (int i = 0; i < arrayProvinceUniversityName.length; i++) {
					if (destination.equals(arrayProvinceUniversityName[i])) {
						desExtractCity[0] = arrayProvinceUniversityCity[i];
						desExtractCity[1] = destination;
						return desExtractCity;
					}
				}
			}
		}

		int desLength = destination.trim().length();
		if (desLength > 5) {
			String[] cityArray6 = getResources().getStringArray(
					R.array.city_array_6);
			for (int i = 0; i < cityArray6.length; i++) {
				if (destination.startsWith(cityArray6[i])) {
					desExtractCity[0] = destination.substring(0, 6);
					desExtractCity[1] = destination.substring(6, desLength);

					String[] keyNotExtract = getResources().getStringArray(
							R.array.key_not_extract);
					for (int j = 0; j < keyNotExtract.length; j++) {
						if (keyNotExtract[j].equals(desExtractCity[1])) {
							desExtractCity[1] = destination;
						}
					}

					String[] cityNotExtract = getResources().getStringArray(
							R.array.city_not_extract);
					for (int n = 0; n < cityNotExtract.length; n++) {
						if (desExtractCity[1].endsWith(cityNotExtract[n])) {
							desExtractCity[0] = "";
						}
					}

					return desExtractCity;
				}
			}
		}

		if (desLength > 4) {
			String[] cityArray5 = getResources().getStringArray(
					R.array.city_array_5);
			for (int i = 0; i < cityArray5.length; i++) {
				if (destination.startsWith(cityArray5[i])) {
					desExtractCity[0] = destination.substring(0, 5);
					desExtractCity[1] = destination.substring(5, desLength);

					String[] keyNotExtract = getResources().getStringArray(
							R.array.key_not_extract);
					for (int j = 0; j < keyNotExtract.length; j++) {
						if (keyNotExtract[j].equals(desExtractCity[1])) {
							desExtractCity[1] = destination;
						}
					}

					String[] cityNotExtract = getResources().getStringArray(
							R.array.city_not_extract);
					for (int n = 0; n < cityNotExtract.length; n++) {
						if (desExtractCity[1].endsWith(cityNotExtract[n])) {
							desExtractCity[0] = "";
						}
					}

					return desExtractCity;
				}
			}
		}

		if (desLength > 3) {
			String[] cityArray4 = getResources().getStringArray(
					R.array.city_array_4);
			for (int i = 0; i < cityArray4.length; i++) {
				if (destination.startsWith(cityArray4[i])) {
					desExtractCity[0] = destination.substring(0, 4);
					desExtractCity[1] = destination.substring(4, desLength);

					String[] keyNotExtract = getResources().getStringArray(
							R.array.key_not_extract);
					for (int j = 0; j < keyNotExtract.length; j++) {
						if (keyNotExtract[j].equals(desExtractCity[1])) {
							desExtractCity[1] = destination;
						}
					}

					String[] cityNotExtract = getResources().getStringArray(
							R.array.city_not_extract);
					for (int n = 0; n < cityNotExtract.length; n++) {
						if (desExtractCity[1].endsWith(cityNotExtract[n])) {
							desExtractCity[0] = "";
						}
					}

					return desExtractCity;
				}
			}
		}

		if (desLength > 2) {
			String[] cityArray3 = getResources().getStringArray(
					R.array.city_array_3);
			for (int i = 0; i < cityArray3.length; i++) {
				if (destination.startsWith(cityArray3[i])) {
					desExtractCity[0] = destination.substring(0, 3);
					desExtractCity[1] = destination.substring(3, desLength);

					String[] keyNotExtract = getResources().getStringArray(
							R.array.key_not_extract);
					for (int j = 0; j < keyNotExtract.length; j++) {
						if (keyNotExtract[j].equals(desExtractCity[1])) {
							desExtractCity[1] = destination;
						}
					}

					String[] cityNotExtract = getResources().getStringArray(
							R.array.city_not_extract);
					for (int n = 0; n < cityNotExtract.length; n++) {
						if (desExtractCity[1].endsWith(cityNotExtract[n])) {
							desExtractCity[0] = "";
						}
					}

					return desExtractCity;
				}
			}
		}

		if (desLength > 1) {
			String[] cityArray2 = getResources().getStringArray(
					R.array.city_array_2);
			for (int i = 0; i < cityArray2.length; i++) {
				if (destination.startsWith(cityArray2[i])) {
					desExtractCity[0] = destination.substring(0, 2);
					desExtractCity[1] = destination.substring(2, desLength);

					String[] keyNotExtract = getResources().getStringArray(
							R.array.key_not_extract);
					for (int j = 0; j < keyNotExtract.length; j++) {
						if (keyNotExtract[j].equals(desExtractCity[1])) {
							desExtractCity[1] = destination;
						}
					}

					String[] cityNotExtract = getResources().getStringArray(
							R.array.city_not_extract);
					for (int n = 0; n < cityNotExtract.length; n++) {
						if (desExtractCity[1].endsWith(cityNotExtract[n])) {
							desExtractCity[0] = "";
						}
					}

					return desExtractCity;
				}
			}
		}

		return desExtractCity;
	}

	private void changePage(int set) {
		pageIndex += set;
		if (pageIndex < 0) {
			pageIndex = 0;
		}
		startSearchPlace(doingWhere, doingLatLng, doingIsNear, true);
	}

	public void startSearchPlace(String where, LatLng centerLatLng,
			boolean isNear, boolean isPage) {

		MyLog.v("[MainActivity]MyApplication.isBaiduNaviInitialSuccess:"
				+ MyApplication.isBaiduNaviInitialSuccess
				+ ",startSearchPlace, pageIndex=" + pageIndex);
		doingWhere = where;
		doingLatLng = centerLatLng;
		doingIsNear = isNear;
		Log.v(Constant.TAG, "startSearchPlace:" + where + ",Lat:"
				+ centerLatLng.latitude + ",Lng:" + centerLatLng.longitude
				+ ",isNear:" + isNear);

		if (pageIndex < 1) {
			layoutPagePriv.setVisibility(View.INVISIBLE);
		} else {
			layoutPagePriv.setVisibility(View.VISIBLE);
		}
		if (where != null && where.trim().length() > 0) {
			if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
				NetworkUtil.noNetworkHint(getApplicationContext());
			} else {
				String textCity = etHistoryCity.getText().toString();
				boolean isInputCity = textCity != null
						&& textCity.trim().length() > 0;
				if (isNear) {
					// 周边搜索
					if (!isPage) {
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.poi_search_near)
										+ where, Toast.LENGTH_SHORT).show();
					}

					PoiNearbySearchOption poiOption = new PoiNearbySearchOption();
					poiOption.keyword(where);
					poiOption.location(centerLatLng);
					poiOption.radius(15 * 1000 * 1000); // 检索半径，单位:m
					poiOption.sortType(PoiSortType.distance_from_near_to_far); // 按距离排序
					// poiOption.sortType(PoiSortType.comprehensive); // 按综合排序
					poiOption.pageNum(pageIndex); // 分页编号
					poiOption.pageCapacity(pageCapacity);
					try {
						mPoiSearch.searchNearby(poiOption);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 未输入城市
					if (!isInputCity) {
						String[] desExtractCity = formatDestination(where);
						String strCity = desExtractCity[0];
						String strKey = desExtractCity[1];
						if (strCity != null && strCity.trim().length() > 0) {
							// 全国搜索
							if (!isPage) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.poi_in_city)
												+ strCity
												+ getResources().getString(
														R.string.poi_search)
												+ strKey, Toast.LENGTH_SHORT)
										.show();
							}
							PoiCitySearchOption poiOption = new PoiCitySearchOption();
							poiOption.city(strCity);
							poiOption.keyword(strKey);
							poiOption.pageNum(pageIndex);
							poiOption.pageCapacity(pageCapacity);
							mPoiSearch.searchInCity(poiOption);
						} else {
							if (!isPage) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.poi_search_near)
												+ where, Toast.LENGTH_SHORT)
										.show();
							}

							PoiNearbySearchOption poiOption = new PoiNearbySearchOption();
							poiOption.keyword(where);
							poiOption.location(centerLatLng);
							poiOption.radius(15 * 1000 * 1000); // 检索半径，单位:m
							poiOption
									.sortType(PoiSortType.distance_from_near_to_far); // 按距离排序
							// poiOption.sortType(PoiSortType.comprehensive); //
							// 按综合排序
							poiOption.pageNum(pageIndex); // 分页编号
							poiOption.pageCapacity(pageCapacity);
							try {
								mPoiSearch.searchNearby(poiOption);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						// 全国搜索
						if (!isPage) {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.poi_in_city)
											+ textCity
											+ getResources().getString(
													R.string.poi_search)
											+ where, Toast.LENGTH_SHORT).show();
						}
						PoiCitySearchOption poiOption = new PoiCitySearchOption();
						poiOption.city(textCity);
						poiOption.keyword(where);
						poiOption.pageNum(pageIndex);
						poiOption.pageCapacity(pageCapacity);
						mPoiSearch.searchInCity(poiOption);
					}

					// 存储搜索历史到数据库,周边不需要
					int existId = naviDb.getNaviIdByKey(where);
					if (existId != -1) {
						naviDb.deleteNaviHistoryById(existId);
					}
					NaviHistory naviHistory = new NaviHistory(where, textCity);
					naviDb.addNaviHistory(naviHistory);
					naviHistoryAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	class MyOnGetSuggestionResultListener implements
			OnGetSuggestionResultListener {

		@Override
		public void onGetSuggestionResult(SuggestionResult result) {
			if (result == null || result.getAllSuggestions() == null) {
				Toast.makeText(getApplicationContext(), "无结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			sugAdapter.clear();
			// for (SuggestionResult.SuggestionInfo info :
			// res.getAllSuggestions()) {
			// if (info.key != null){
			// sugAdapter.add(info.key);

			naviArray = new ArrayList<NaviResultInfo>();
			Log.v(Constant.TAG, "result.getAllSuggestions().size():"
					+ result.getAllSuggestions().size());
			for (int i = 0; i < result.getAllSuggestions().size(); i++) {
				// PoiInfo poiInfo = result.getAllPoi().get(i);
				//
				SuggestionInfo info = result.getAllSuggestions().get(i);
				double distance = DistanceUtil.getDistance(nowLatLng, info.pt);

				NaviResultInfo naviResultInfo = new NaviResultInfo(i, info.key,
						info.city + info.district, info.pt.longitude,
						info.pt.latitude, distance);
				naviArray.add(naviResultInfo);
			}

			naviResultAdapter = new NaviResultAdapter(getApplicationContext(),
					naviArray, isFromVoice);

			layoutResult.setVisibility(View.VISIBLE);
			isResultListShow = true;

			// 启动语音聆听
			if (isFromVoice) {
				String strHint = "选择或取消";
				sendBroadcast(new Intent("com.tchip.restartAIAsr").putExtra(
						"speak", strHint));
				imageVoiceListen.setVisibility(View.VISIBLE);
			}

			listResult.setAdapter(naviResultAdapter);
			// }
			// sugAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 地址转经纬度监听
	 */
	class MyOnGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			LatLng thisLatLng = result.getLocation();
			if (thisLatLng != null) {
				startSearchPlace(etHistoryWhere.getText().toString(),
						thisLatLng, false, false);
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		}
	}

	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.poi_no_result),
					Toast.LENGTH_LONG).show();
			pageIndex--;
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			baiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(baiduMap);
			baiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			// 添加结果
			LatLng llStart = nowLatLng; // 当前位置
			naviArray = new ArrayList<NaviResultInfo>();
			int arraySize = result.getAllPoi().size();
			if (arraySize < pageCapacity) {
				layoutPageNext.setVisibility(View.INVISIBLE);
			} else {
				layoutPageNext.setVisibility(View.VISIBLE);
			}
			Log.v(Constant.TAG, "result.getAllPoi().size():" + arraySize);
			for (int i = 0; i < arraySize; i++) {
				PoiInfo poiInfo = result.getAllPoi().get(i);

				double distance = DistanceUtil.getDistance(llStart,
						poiInfo.location);

				NaviResultInfo naviResultInfo = new NaviResultInfo(i,
						poiInfo.name, poiInfo.address,
						poiInfo.location.longitude, poiInfo.location.latitude,
						distance);
				naviArray.add(naviResultInfo);
			}

			naviResultAdapter = new NaviResultAdapter(getApplicationContext(),
					naviArray, isFromVoice);
			layoutResult.setVisibility(View.VISIBLE);
			// setLayoutHistoryVisibility(false);
			// layoutHistory.setVisibility(View.GONE);
			// isHistoryLayoutShow = false;
			isResultListShow = true;

			// 启动语音聆听
			if (isFromVoice) {
				String strHint = ""; // 请选择
				sendBroadcast(new Intent("com.tchip.restartAIAsr").putExtra(
						"speak", strHint));
				imageVoiceListen.setVisibility(View.VISIBLE);
			}

			listResult.setAdapter(naviResultAdapter);

			listResult
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(
								android.widget.AdapterView<?> parent,
								android.view.View view, int position, long id) {
							// 开始导航
							layoutResult.setVisibility(View.GONE);
							pageIndex = 0; // 重设搜索结果页码为0
							isResultListShow = false;

							setLayoutHistoryVisibility(false);

							if (!MyApplication.isBaiduNaviAuthSuccess) {
								Log.e(Constant.TAG, "Navigation:Auth Fail");
								Toast.makeText(getApplicationContext(),
										strAuthFail, Toast.LENGTH_SHORT).show();
								showFailedDialog(strAuthFail);
							} else if (!MyApplication.isBaiduNaviInitialSuccess) {
								Log.e(Constant.TAG, "Navigation:Initial Fail");
								Toast.makeText(getApplicationContext(),
										strInitFail, Toast.LENGTH_SHORT).show();
								showFailedDialog(strInitFail);
							} else {
								routeplanToNavi(
										CoordinateType.GCJ02,
										nowLatLng.latitude,
										nowLatLng.longitude,
										getResources().getString(
												R.string.location_here),
										naviArray.get(position).getLatitude(),
										naviArray.get(position).getLongitude(),
										naviArray.get(position).getName());
							}
						}
					});

			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			// String strInfo = "在";
			// for (CityInfo cityInfo : result.getSuggestCityList()) {
			// strInfo += cityInfo.city;
			// strInfo += ",";
			// }
			// strInfo += "找到结果";
			// Toast.makeText(NavigationActivity.this, strInfo,
			// Toast.LENGTH_LONG)
			// .show();
			//

		}
	}

	/**
	 * 显示退出对话框
	 * 
	 * @param message
	 */
	private void showFailedDialog(String message) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setTitle("提示")
				.setIcon(
						getResources().getDrawable(
								R.drawable.ui_icon_dialog_warnning))
				.setPositiveButton(R.string.close_navi, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Intent intent = new Intent(Intent.ACTION_MAIN);
						// intent.addCategory(Intent.CATEGORY_HOME);
						// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				}).setCancelable(false).show();
	}

	private void routeplanToNavi(CoordinateType coType, double startLatitude,
			double startLongitude, String startName, double endLatitude,
			double endLongitude, String endName) {
		// 打开GPS
		sendBroadcast(new Intent("tchip.intent.action.ACTION_GPS_ON"));

		audioRecordDialog.showLoadDialog();

		if (MyApplication.isRouteComputing) {
			String strWaitLastCompute = "请等待上次路线规划完成";
			Toast.makeText(getApplicationContext(), strWaitLastCompute,
					Toast.LENGTH_SHORT).show();
			startSpeak(strWaitLastCompute);
		} else {
			Toast.makeText(getApplicationContext(), "开始规划路线",
					Toast.LENGTH_SHORT).show();
			MyApplication.isRouteComputing = true;
			editor.putBoolean("naviResume", true);
			editor.putString("naviLat", "" + endLatitude);
			editor.putString("naviLng", "" + endLongitude);
			editor.putString("naviName", endName);
			editor.commit();

			BNRoutePlanNode sNode = null;
			BNRoutePlanNode eNode = null;
			// 需要将bd09ll转成BD09_MC,GCJ02,WGS84
			BDLocation bdLocStartBefore = new BDLocation();
			bdLocStartBefore.setLatitude(startLatitude);
			bdLocStartBefore.setLongitude(startLongitude);
			BDLocation bdLocStartAfter = LocationClient
					.getBDLocationInCoorType(bdLocStartBefore,
							BDLocation.BDLOCATION_BD09LL_TO_GCJ02);

			BDLocation bdLocEndBefore = new BDLocation();
			bdLocEndBefore.setLatitude(endLatitude);
			bdLocEndBefore.setLongitude(endLongitude);
			BDLocation bdLocEndAfter = LocationClient.getBDLocationInCoorType(
					bdLocEndBefore, BDLocation.BDLOCATION_BD09LL_TO_GCJ02);

			sNode = new BNRoutePlanNode(bdLocStartAfter.getLongitude(),
					bdLocStartAfter.getLatitude(), startName, null,
					CoordinateType.GCJ02);
			eNode = new BNRoutePlanNode(bdLocEndAfter.getLongitude(),
					bdLocEndAfter.getLatitude(), endName, null,
					CoordinateType.GCJ02);

			if (sNode != null && eNode != null) {
				List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
				list.add(sNode);
				list.add(eNode);

				/**
				 * activity - 建议是应用的主Activity
				 * 
				 * nodes - 传入的算路节点，顺序是起点、途经点、终点，其中途经点最多三个，参考 BNRoutePlanNode
				 * 
				 * preference - 算路偏好，参考BaiduNaviManager.RoutePlanPreference:
				 * -----------------------------------------------------------
				 * 1-ROUTE_PLAN_MOD_RECOMMEND 推荐
				 * 
				 * 2-ROUTE_PLAN_MOD_MIN_TIME 高速优先
				 * 
				 * 4-ROUTE_PLAN_MOD_MIN_DIST 少走高速
				 * 
				 * 8-ROUTE_PLAN_MOD_MIN_TOLL 少收费
				 * 
				 * 16-ROUTE_PLAN_MOD_AVOID_TAFFICJAM 躲避拥堵
				 * -----------------------------------------------------------
				 * 
				 * isGPSNav - true表示真实GPS导航，false表示模拟导航
				 * 
				 * listener - 开始导航回调监听器，在该监听器里一般是进入导航过程页面
				 */
				int routePlanMode = preference.getInt(
						Constant.SPString.ROUTE_PLAN_PREF,
						RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND);
				MyLog.v("[routeplanToNavi]routePlanMode:" + routePlanMode);
				boolean isLaunchSuccess = BaiduNaviManager.getInstance()
						.launchNavigator(this, list, routePlanMode, true,
								new DemoRoutePlanListener(sNode));

				MyLog.v("[launchNavigator]isLaunchSuccess:" + isLaunchSuccess);

			}
		}
	}

	private class ChoiceOnClickListener implements
			DialogInterface.OnClickListener {

		private int which = 0;

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			this.which = which;
		}

		public int getWhich() {
			return which;
		}
	}

	public class DemoRoutePlanListener implements RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public DemoRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			MyApplication.isRouteComputing = false;
			audioRecordDialog.dismissDialog();
			Intent intent = new Intent(MainActivity.this,
					BNavigatorActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(MainActivity.ROUTE_PLAN_NODE,
					(BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
			Log.v(Constant.TAG, "RoutePlanListener:onJumpToNavigator");
		}

		@Override
		public void onRoutePlanFailed() {
			MyApplication.isRouteComputing = false;
			audioRecordDialog.dismissDialog();
			Toast.makeText(getApplicationContext(), "网络质量不佳，路线规划失败",
					Toast.LENGTH_SHORT).show();
			Log.e(Constant.TAG, "Baidu Navi:Route Plan Failed!");
		}
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (!ClickUtil.isQuickClick(1500)) {
			if (result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.poi_no_result),
						Toast.LENGTH_SHORT).show();
			} else {
				// 点击地图上搜索结果气球进入导航
				if (!MyApplication.isBaiduNaviAuthSuccess) {
					Log.e(Constant.TAG, "Navigation:Auth Fail");
					Toast.makeText(getApplicationContext(), strAuthFail,
							Toast.LENGTH_SHORT).show();
					showFailedDialog(strAuthFail);
				} else if (!MyApplication.isBaiduNaviInitialSuccess) {
					Log.e(Constant.TAG, "Navigation:Initial Fail");
					Toast.makeText(getApplicationContext(), strInitFail,
							Toast.LENGTH_SHORT).show();
					showFailedDialog(strInitFail);
				} else {
					routeplanToNavi(CoordinateType.GCJ02, nowLatLng.latitude,
							nowLatLng.longitude,
							getResources().getString(R.string.poi_no_result),
							result.getLocation().latitude,
							result.getLocation().longitude, result.getAddress());
				}
			}
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	// @Override
	// protected void onResumeFragments() {
	// super.onResumeFragments();
	// View decorView = getWindow().getDecorView();
	// decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	// }

	// ==========================
	int ret = 0;// 函数调用返回值

	public void startVoiceUnderstand() {
		if (-1 == NetworkUtil.getNetworkType(getApplicationContext())) {
			NetworkUtil.noNetworkHint(getApplicationContext());
		} else {
			// 初始化对象
			mSpeechUnderstander = SpeechUnderstander.createUnderstander(
					MainActivity.this, speechUnderstanderListener);
			setParam();

			if (mSpeechUnderstander.isUnderstanding()) { // 开始前检查状态
				mSpeechUnderstander.stopUnderstanding(); // 停止录音
			} else {
				ret = mSpeechUnderstander
						.startUnderstanding(mRecognizerListener);
				if (ret != 0) {
					// 语义理解失败,错误码:ret
				} else {
					// showTip(getString(R.string.text_begin));
				}
			}
		}
	}

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码：code
			}
		}
	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		String lag = preference.getString("voiceAccent", "mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,
				preference.getString("voiceBos", "4000"));
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				preference.getString("voiceEos", "1000"));
		// 设置是否有标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				preference.getString("understander_punc_preference", "0"));
		// 语音输入超时时间,单位：ms，默认60000
		// mSpeechUnderstander.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT,"");

		// 识别句子级多候选结果，如asr_nbest=3,注：设置多候选会影响性能，响应时间延迟200ms左右
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_NBEST, "1");

		// 网络连接超时时间,单位：ms，默认20000
		mSpeechUnderstander.setParameter(SpeechConstant.NET_TIMEOUT, "5000");
		// 设置音频保存路径
		mSpeechUnderstander.setParameter(
				SpeechConstant.ASR_AUDIO_PATH,
				preference.getString("voicePath",
						Environment.getExternalStorageDirectory()
								+ "/iflytek/wavaudio.pcm"));
	}

	/**
	 * 设置目的地EditText的内容
	 */
	private void setDestinationText(String text) {
		if (!TextUtils.isEmpty(text)) {
			progressVoice.setVisibility(View.GONE);
			imgVoiceSearch.setVisibility(View.VISIBLE);
			String[] naviIntent = getResources().getStringArray(
					R.array.navi_intent);
			if (text.startsWith(naviIntent[0])
					|| text.startsWith(naviIntent[1])
					|| text.startsWith(naviIntent[2])
					|| text.startsWith(naviIntent[3])) {
				text = text.substring(3, text.length());
			} else if (text.startsWith(naviIntent[4])) {
				text = text.substring(2, text.length());
			} else if (text.startsWith(naviIntent[5])
					|| text.startsWith(naviIntent[6])
					|| text.startsWith(naviIntent[7])) {
				text = text.substring(1, text.length());
			}

			if ("家".equals(text)) {
				naviHome();
			} else if ("公司".equals(text)) {
				naviWork();
			} else {
				etHistoryWhere.setText(text);
				pageIndex = 0; // 重置页码
				startSearchPlace(text, nowLatLng, false, false);
			}
		}
	}

	/**
	 * 识别回调。
	 */
	private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (null != result) {
						// 显示
						String text = result.getResultString();
						MyLog.v("[SpeechUnderstanderListener]onResult:" + text);

						try {
							JSONObject jsonObject = new JSONObject(text);

							boolean isLastSentence = jsonObject
									.getBoolean("ls");
							if (!isLastSentence) {
								JSONArray worldsArray = jsonObject
										.getJSONArray("ws");
								String strContent = "";

								for (int i = 0; i < worldsArray.length(); i++) {
									JSONArray cwArray = worldsArray
											.getJSONObject(i)
											.getJSONArray("cw");
									JSONObject wObject = cwArray
											.getJSONObject(0);
									strContent = strContent
											+ wObject.getString("w");
								}

								MyLog.v("[SpeechUnderstanderListener]strContent:"
										+ strContent);
								setDestinationText(strContent);

							} else {
								// 第二次识别
								progressVoice.setVisibility(View.GONE);
								imgVoiceSearch.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							MyLog.e("[SpeechUnderstanderListener]onResult Exception:"
									+ e.toString());
						}
					} else {
						MyLog.v("[SpeechUnderstanderListener]onResult:null");
						// 识别结果不正确
						progressVoice.setVisibility(View.GONE);
						imgVoiceSearch.setVisibility(View.VISIBLE);
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int volume, byte[] arg1) {
			audioRecordDialog.updateVolumeLevel(volume);
		}

		@Override
		public void onEndOfSpeech() {
			// showTip("onEndOfSpeech");
			audioRecordDialog.dismissDialog();

			// 开始识别
			imgVoiceSearch.setVisibility(View.GONE);
			progressVoice.setVisibility(View.VISIBLE);
		}

		@Override
		public void onBeginOfSpeech() {
			// showTip("onBeginOfSpeech");
			audioRecordDialog.showVoiceDialog();
		}

		@Override
		public void onError(SpeechError error) {
			// showTip("onError Code：" + error.getErrorCode());
			startSpeak(error.getErrorDescription());

			imgVoiceSearch.setVisibility(View.VISIBLE);
			progressVoice.setVisibility(View.GONE);
			Log.e(Constant.TAG, "SpeechError:" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

	private void startSpeak(String content) {
		Intent intent = new Intent(MainActivity.this, SpeakService.class);
		intent.putExtra("content", content);
		startService(intent);
	}

	/**
	 * 播报导航语音并广播出去
	 * 
	 * @param content
	 */
	private void speakNaviText(String content) {
		startSpeak(content);
		sendBroadcast(new Intent("tchip.intent.action.NAVI_SPEAK").putExtra(
				"content", content));
	}

}
