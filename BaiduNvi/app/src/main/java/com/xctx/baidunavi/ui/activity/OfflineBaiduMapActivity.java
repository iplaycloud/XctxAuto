package com.xctx.baidunavi.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;
import com.xctx.baidunavi.util.NetworkUtil;

/**
 * 安装后搜索仍需联网，但会节约达90%的流量
 * 
 * 低分屏：L 高分屏：H (854x480 5' 属于低分屏)
 */
public class OfflineBaiduMapActivity extends Activity implements
		MKOfflineMapListener {

	private MKOfflineMap mOffline = null;
	private TextView cidView;
	private TextView stateView;
	private EditText cityNameView;

	private Button btnSearch;
	private Button btnDeleteAllMap;
	private RelativeLayout layoutBack, layoutDownload, layoutCity,
			layoutImport;

	private LinearLayout layoutSingleDownload;
	LinearLayout layoutAllMap; // 所有离线地图
	RelativeLayout layoutLocalMap; // 本地离线地图

	private boolean isSingleDownShow = false;

	/**
	 * 已下载的离线地图信息列表
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private LocalMapAdapter lAdapter = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_offline_baidumap);

		mOffline = new MKOfflineMap();
		mOffline.init(this);
		initialLayout();
	}

	private void initialLayout() {
		layoutAllMap = (LinearLayout) findViewById(R.id.layoutAllMap);
		layoutLocalMap = (RelativeLayout) findViewById(R.id.layoutLocalMap);

		btnDeleteAllMap = (Button) findViewById(R.id.btnDeleteAllMap);
		btnDeleteAllMap.setOnClickListener(new MyOnClickListener());

		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new MyOnClickListener());

		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		layoutDownload = (RelativeLayout) findViewById(R.id.layoutDownload);
		layoutDownload.setOnClickListener(new MyOnClickListener());

		layoutCity = (RelativeLayout) findViewById(R.id.layoutCity);
		layoutCity.setOnClickListener(new MyOnClickListener());

		layoutImport = (RelativeLayout) findViewById(R.id.layoutImport);
		layoutImport.setOnClickListener(new MyOnClickListener());

		layoutSingleDownload = (LinearLayout) findViewById(R.id.layoutSingleDownload);
		layoutSingleDownload.setVisibility(View.GONE);

		cidView = (TextView) findViewById(R.id.cityid);
		cityNameView = (EditText) findViewById(R.id.textCity);
		stateView = (TextView) findViewById(R.id.state);

		// ListView hotCityList = (ListView) findViewById(R.id.hotcitylist);
		// ArrayList<String> hotCities = new ArrayList<String>();
		// // 获取热闹城市列表
		ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
		// if (records1 != null) {
		// for (MKOLSearchRecord r : records1) {
		// hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
		// + this.formatDataSize(r.size));
		// }
		// }
		// ListAdapter hAdapter = (ListAdapter) new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, hotCities);
		// hotCityList.setAdapter(hAdapter);

		ListView allCityList = (ListView) findViewById(R.id.allcitylist);
		// 获取所有支持离线地图的城市
		final ArrayList<String> allCities = new ArrayList<String>();
		ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		ListAdapter aAdapter = (ListAdapter) new ArrayAdapter<String>(this,
				R.layout.offline_map_all_city_list_item, R.id.textAllCityList,
				allCities);
		allCityList.setAdapter(aAdapter);
		allCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
					final int startIndex = allCities.get(position).indexOf("(") + 1;
					int endIndex = allCities.get(position).indexOf(")");
					final String clickId = allCities.get(position).substring(
							startIndex, endIndex);
					MyLog.v("Offline Map id:" + clickId);

					AlertDialog.Builder builder = new Builder(
							OfflineBaiduMapActivity.this);
					builder.setMessage(getResources().getString(
							R.string.start_download)
							+ allCities.get(position).substring(0,
									startIndex - 1) + "?");
					builder.setTitle(getResources().getString(R.string.hint));
					builder.setPositiveButton(
							getResources().getString(R.string.confirm),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										android.content.DialogInterface dialog,
										int which) {

									mOffline.start(Integer.parseInt(clickId));
									Toast.makeText(
											getApplicationContext(),
											getResources().getString(
													R.string.start_download)
													+ allCities
															.get(position)
															.substring(
																	0,
																	startIndex - 1),
											Toast.LENGTH_SHORT).show();
								}
							});
					builder.setNegativeButton(
							getResources().getString(R.string.cancel),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										android.content.DialogInterface dialog,
										int which) {
								}
							});
					builder.create().show();

				} else {
					NetworkUtil.noNetworkHint(getApplicationContext());
				}
			}
		});

		layoutLocalMap.setVisibility(View.GONE);
		layoutAllMap.setVisibility(View.VISIBLE);

		ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
		lAdapter = new LocalMapAdapter(OfflineBaiduMapActivity.this);

		// 获取已下过的离线地图信息
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		} else {
			// 删除未完成下载的离线地图
			for (int i = 0; i < localMapList.size(); i++) {
				MKOLUpdateElement element = localMapList.get(i);
				int ratio = element.ratio;
				int cityId = element.cityID;
				if (ratio < 100) {
					MyLog.v("[initialLayout]cityId:" + cityId + ",cityName:"
							+ element.cityName + ",ratio:" + ratio
							+ ",Restart Download!");
					// mOffline.remove(cityId);
					mOffline.start(cityId);
				} else {
					MyLog.v("[initialLayout]cityId:" + cityId + ",cityName:"
							+ element.cityName + ",ratio:" + ratio);
				}
			}
			localMapList = mOffline.getAllUpdateInfo();
			lAdapter.notifyDataSetChanged();
		}

		localMapListView.setAdapter(lAdapter);

	}

	/**
	 * 设置下载单个城市离线地图是否可见
	 * 
	 * @param isShow
	 */
	private void setSingleDownShow(boolean isShow) {
		if (!isShow) {
			layoutSingleDownload.setVisibility(View.GONE);
			isSingleDownShow = false;
		} else {
			layoutSingleDownload.setVisibility(View.VISIBLE);
			isSingleDownShow = true;
		}
	}

	private class DeleteAllMapThread implements Runnable {

		@Override
		public void run() {
			localMapList = mOffline.getAllUpdateInfo();
			if (localMapList == null) {
				localMapList = new ArrayList<MKOLUpdateElement>();
			} else {
				for (int i = 0; i < localMapList.size(); i++) {
					int cityId = localMapList.get(i).cityID;
					mOffline.remove(cityId);
					MyLog.v("[Delete All Map]cityID:" + cityId);
				}

				// TODO:Handle Exception Here
				localMapList.clear();
			}

			Message messageDeleteAllMap = new Message();
			messageDeleteAllMap.what = 1;
			deleteAllMapHandler.sendMessage(messageDeleteAllMap);
		}

	}

	final Handler deleteAllMapHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				lAdapter = new LocalMapAdapter(OfflineBaiduMapActivity.this);
				lAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};

	class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnDeleteAllMap:
				// 删除所有的离线地图
				new Thread(new DeleteAllMapThread()).start();
				break;

			case R.id.btnSearch:
				// 搜索离线城市
				if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
					ArrayList<MKOLSearchRecord> records = mOffline
							.searchCity(cityNameView.getText().toString());
					if (records == null || records.size() != 1) {
						Toast.makeText(
								getApplicationContext(),
								getResources()
										.getString(R.string.no_match_city),
								Toast.LENGTH_SHORT).show();
						return;
					}
					setSingleDownShow(true);
					cidView.setText(String.valueOf(records.get(0).cityID));
				} else {
					NetworkUtil.noNetworkHint(getApplicationContext());
				}
				break;

			case R.id.layoutBack:
				onBackKeyPressed();
				break;

			case R.id.layoutDownload:
				// 下载管理列表
				layoutLocalMap.setVisibility(View.VISIBLE);
				layoutAllMap.setVisibility(View.GONE);
				setSingleDownShow(false);
				break;

			case R.id.layoutCity:
				// 城市列表
				layoutLocalMap.setVisibility(View.GONE);
				layoutAllMap.setVisibility(View.VISIBLE);
				setSingleDownShow(false);
				break;

			case R.id.layoutImport:
				importFromSDCard();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void start(View view) {
		if (NetworkUtil.isNetworkConnected(getApplicationContext())) {

			Toast.makeText(this,
					getResources().getString(R.string.offline_download_start),
					Toast.LENGTH_SHORT).show();

			startDownloadCityId = Integer
					.parseInt(cidView.getText().toString());
			new Thread(new StartDownloadThread()).start();

		} else {
			NetworkUtil.noNetworkHint(getApplicationContext());
		}
	}

	private int startDownloadCityId = 1; // 要下载的城市ID

	/**
	 * 下载地图线程
	 */
	private class StartDownloadThread implements Runnable {

		@Override
		public void run() {
			// 启动下载指定城市ID的离线地图,离线地图下载服务仅当wifi连接正常时可用
			mOffline.start(startDownloadCityId);
			startDownloadCityId = 1; // 重置

			Message messageStartDownload = new Message();
			messageStartDownload.what = 1;
			startDownloadHandler.sendMessage(messageStartDownload);
		}

	}

	final Handler startDownloadHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				updateView();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 暂停下载
	 * 
	 * @param view
	 */
	public void stop(View view) {
		int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.pause(cityid);
		Toast.makeText(this,
				getResources().getString(R.string.offline_download_pause),
				Toast.LENGTH_SHORT).show();
		updateView();
	}

	/**
	 * 删除离线地图
	 * 
	 * @param view
	 */
	public void deleteMapByCityId(int cityId) {
		deleteMapId = cityId;
		new Thread(new DeleteMapThread()).start();

		Toast.makeText(this,
				getResources().getString(R.string.offline_download_delete),
				Toast.LENGTH_SHORT).show();

		updateView();
	}

	private int deleteMapId = 1; // 要删除的地图ID

	/**
	 * 删除地图线程
	 */
	private class DeleteMapThread implements Runnable {

		@Override
		public void run() {
			mOffline.remove(deleteMapId);
			deleteMapId = 1;
		}

	}

	/**
	 * 从SD卡导入离线地图安装包
	 * 
	 * 存放位置：USB存储器/BaiduMapSDK/vmp/l/zhongshan_187.dat
	 * 
	 */
	public void importFromSDCard() {
		new Thread(new ImportOfflineMapThread()).start();
	}

	private class ImportOfflineMapThread implements Runnable {

		@Override
		public void run() {
			int mapNum = mOffline.importOfflineData();

			String msg = "";
			if (mapNum == 0) {
				Message messageNoMap = new Message();
				messageNoMap.what = 1;
				importOfflineMapHandler.sendMessage(messageNoMap);
			} else {
				Message messageImport = new Message();
				messageImport.what = 2;
				importOfflineMapHandler.sendMessage(messageImport);
			}

		}

	}

	final Handler importOfflineMapHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1: // 未导入离线地图包

				AlertDialog.Builder builder = new Builder(
						OfflineBaiduMapActivity.this);
				builder.setMessage(getResources().getString(
						R.string.not_import_offline_map));
				builder.setTitle(getResources().getString(R.string.hint));
				builder.setPositiveButton(
						getResources().getString(R.string.confirm),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();

				break;

			case 2:
				AlertDialog.Builder builderImport = new Builder(
						OfflineBaiduMapActivity.this);
				builderImport.setMessage(getResources().getString(
						R.string.import_success_without_num));
				builderImport.setTitle(getResources().getString(R.string.hint));
				builderImport.setPositiveButton(
						getResources().getString(R.string.confirm),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builderImport.create().show();

				updateView();

				break;

			default:
				break;
			}
		}
	};

	/**
	 * 更新状态显示
	 */
	public void updateView() {
		new Thread(new UpdateViewThread()).start();
	}

	private class UpdateViewThread implements Runnable {

		@Override
		public void run() {
			localMapList = mOffline.getAllUpdateInfo();
			if (localMapList == null) {
				localMapList = new ArrayList<MKOLUpdateElement>();
			}

			Message messageUpdate = new Message();
			messageUpdate.what = 1;
			updateViewHandler.sendMessage(messageUpdate);
		}

	}

	final Handler updateViewHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				lAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackKeyPressed();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void onBackKeyPressed() {
		if (isSingleDownShow) {
			setSingleDownShow(false);
		} else {
			finish();
		}
	}

	@Override
	protected void onPause() {
		// onPause不暂停下载
		// int cityid = Integer.parseInt(cidView.getText().toString());
		// MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
		// if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
		// mOffline.pause(cityid);
		// }
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				stateView.setText(String.format("%s : %d%%", update.cityName,
						update.ratio));
				updateView();
			}
		}
			break;

		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;

		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}

	}

	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public LocalMapAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		class MyViewHolder {
			Button display;
			Button remove;
			Button btnUpdate;

			TextView title;
			TextView update;
			TextView ratio;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			final MyViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.offline_baidumap_locallist_item, null);

				viewHolder.display = (Button) convertView
						.findViewById(R.id.btnDisplay);
				viewHolder.remove = (Button) convertView
						.findViewById(R.id.remove);
				viewHolder.btnUpdate = (Button) convertView
						.findViewById(R.id.btnUpdate);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.title);
				viewHolder.update = (TextView) convertView
						.findViewById(R.id.update);
				viewHolder.ratio = (TextView) convertView
						.findViewById(R.id.ratio);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyViewHolder) convertView.getTag();
			}

			final MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);

			viewHolder.ratio.setText(e.ratio + "%");
			viewHolder.title.setText(e.cityName);
			if (e.update) {
				// 可更新
				viewHolder.update.setText(getResources().getString(
						R.string.has_update));
				viewHolder.update.setTextColor(Color.YELLOW);
				viewHolder.btnUpdate.setText(getResources().getString(
						R.string.update));
			} else {
				// 已最新
				viewHolder.update.setText(getResources().getString(
						R.string.no_update));
				viewHolder.update.setTextColor(Color.WHITE);
				viewHolder.btnUpdate.setText(getResources().getString(
						R.string.re_download));
			}
			if (e.ratio != 100) {
				viewHolder.display.setEnabled(false);
			} else {
				viewHolder.display.setEnabled(true);
			}
			viewHolder.remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			viewHolder.display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(OfflineBaiduMapActivity.this,
							OfflineBaiduMapShowActivity.class);
					startActivity(intent);
				}
			});

			viewHolder.btnUpdate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
						mOffline.update(e.cityID);
						updateView();
					} else {
						NetworkUtil.noNetworkHint(getApplicationContext());
					}
				}
			});

			// convertView = View.inflate(OfflineBaiduMapActivity.this,
			// R.layout.offline_baidumap_locallist_item, null);
			// initViewItem(convertView, e);
			return convertView;
		}

		void initViewItem(View view, final MKOLUpdateElement e) {
			Button display = (Button) view.findViewById(R.id.btnDisplay);
			Button remove = (Button) view.findViewById(R.id.remove);
			Button btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				// 可更新
				update.setText(getResources().getString(R.string.has_update));
				update.setTextColor(Color.YELLOW);
				btnUpdate.setText(getResources().getString(R.string.update));
			} else {
				// 已最新
				update.setText(getResources().getString(R.string.no_update));
				update.setTextColor(Color.WHITE);
				btnUpdate.setText(getResources()
						.getString(R.string.re_download));
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(OfflineBaiduMapActivity.this,
							OfflineBaiduMapShowActivity.class);
					startActivity(intent);
				}
			});

			btnUpdate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
						mOffline.update(e.cityID);
						updateView();
					} else {
						NetworkUtil.noNetworkHint(getApplicationContext());
					}
				}
			});
		}

	}

}