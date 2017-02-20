package com.xctx.baidunavi.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.xctx.baidunavi.Constant;
import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OfflineBaiduMapUpdateActivity extends Activity {

	private final static String[] pathFrom = {
			"/storage/sdcard1/BaiduMapSDK/vmp/l/",
			"/storage/sdcard1/BaiduMap/vmp/l/",
			"/storage/sdcard2/BaiduMapSDK/vmp/l/",
			"/storage/sdcard2/BaiduMap/vmp/l/" };

	private ProgressBar progressCopy;
	private TextView textHint, textDetail;
	private RelativeLayout layoutBack, layoutUpdateOnline;
	private Button btnStart, btnScan;

	private boolean hasOfflineMap = false;

	private boolean isActivityShow = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_update_map);

		initialLayout();

		// statrtScanThread();
		setMapNumberText();

		mOffline = new MKOfflineMap();
		mOffline.init(new MyMKOfflineMapListener());
	}

	private void initialLayout() {
		progressCopy = (ProgressBar) findViewById(R.id.progressCopy);
		progressCopy.setVisibility(View.GONE);

		textHint = (TextView) findViewById(R.id.textHint);
		// textHint.setText("正在扫描SD卡是否有离线地图");

		textDetail = (TextView) findViewById(R.id.textDetail);
		layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
		layoutBack.setOnClickListener(new MyOnClickListener());

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new MyOnClickListener());
		btnStart.setVisibility(View.VISIBLE);

		btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new MyOnClickListener());
		btnScan.setVisibility(View.VISIBLE);

		layoutUpdateOnline = (RelativeLayout) findViewById(R.id.layoutUpdateOnline);
		layoutUpdateOnline.setOnClickListener(new MyOnClickListener());
	}

	private void startImportThread() {
		// 判断是否需要拷贝
		new Thread(new CopyThread()).start();
	}

	private void minimumWindow() {
		finish();
	}

	private void setMapNumberText() {
		int mapNumber = getSDMapCount();
		textHint.setVisibility(View.VISIBLE);
		if (mapNumber > 0) {
			textHint.setText(String.format(
					getResources().getString(R.string.sd_has_map), mapNumber));
			hasOfflineMap = true;
		} else {
			hasOfflineMap = false;
			textHint.setText(getResources().getString(R.string.sd_has_no_map));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			minimumWindow();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutBack:
				minimumWindow();
				break;

			case R.id.btnStart:
				if (hasOfflineMap) {
					AlertDialog.Builder builder = new Builder(
							OfflineBaiduMapUpdateActivity.this);
					builder.setMessage(getResources().getString(
							R.string.hint_import_is_cost_time));
					builder.setTitle(getResources().getString(R.string.hint));
					builder.setPositiveButton(
							getResources().getString(R.string.confirm),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									startImportThread();
								}
							});
					builder.setNegativeButton(
							getResources().getString(R.string.cancel),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					// SD卡未扫描到离线地图
				}

				break;

			case R.id.btnScan:
				setMapNumberText();
				break;

			case R.id.layoutUpdateOnline:
				Intent intentUpdate = new Intent(
						OfflineBaiduMapUpdateActivity.this,
						OfflineBaiduMapActivity.class);
				startActivity(intentUpdate);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 获取SD卡离线地图数目
	 * 
	 * @return
	 */
	private int getSDMapCount() {

		int fileCount1 = checkFileNumber(pathFrom[0]);
		int fileCount2 = checkFileNumber(pathFrom[1]);
		int fileCount3 = checkFileNumber(pathFrom[2]);
		int fileCount4 = checkFileNumber(pathFrom[3]);

		if (fileCount1 > 0)
			return fileCount1;
		else if (fileCount2 > 0)
			return fileCount2;
		else if (fileCount3 > 0)
			return fileCount3;
		else if (fileCount4 > 0)
			return fileCount4;
		else
			return -1;

	}

	private int checkFileNumber(String path) {

		try {
			File a = new File(path);
			String[] file = a.list();
			progressCopy.setMax(file.length);
			if (file.length > 0) {
				return file.length;
			} else
				return -1;
		} catch (Exception e) {
			return -1;
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public boolean copyFolder(String oldPath, String newPath) {
		boolean isok = true;

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			progressCopy.setMax(file.length);
			if (file.length > 0) {
				Message message = new Message();
				message.what = 2;
				copyHandler.sendMessage(message);
			}

			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (isActivityShow) {
					if (oldPath.endsWith(File.separator)) {
						temp = new File(oldPath + file[i]);
					} else {
						temp = new File(oldPath + File.separator + file[i]);
					}

					if (temp.isFile()) {
						FileInputStream input = new FileInputStream(temp);
						FileOutputStream output = new FileOutputStream(newPath
								+ "/" + (temp.getName()).toString());
						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					// if (temp.isDirectory()) {// 如果是子文件夹
					// copyFolder(oldPath + "/" + file[i], newPath + "/" +
					// file[i]);
					// }
					MyLog.v("Copy:" + file[i] + " Success");
					progressCopy.setProgress(i);
					Message message = new Message();
					message.what = 3;
					message.arg1 = i + 1;
					message.arg2 = progressCopy.getMax();
					copyHandler.sendMessage(message);
				} else {
					// 跳出循环，不进行后台拷贝
					break;
				}
			}

			// 更新Media Database
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse("file://" + Constant.Path.BAIDU_OFFLINE_SUB)));

			Message message = new Message();
			message.what = 1;
			copyHandler.sendMessage(message);
		} catch (Exception e) {
			Log.e(Constant.TAG,
					"Copy Map form SD " + oldPath + " Error:" + e.toString());
			isok = false;
			Message message = new Message();
			message.what = 0;
			copyHandler.sendMessage(message);
		}

		return isok;
	}

	final Handler copyHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.e(Constant.TAG, "There is no offline map!");
				textDetail.setVisibility(View.GONE);
				textHint.setText(getResources().getString(
						R.string.sd_has_no_map));
				progressCopy.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
				btnScan.setVisibility(View.VISIBLE);
				// finish();
				break;

			case 1:
				Log.e(Constant.TAG, "copy data success!");
				Intent i = new Intent();
				setResult(RESULT_OK, i);

				textHint.setText(getResources().getString(
						R.string.import_sd_success));
				textDetail.setVisibility(View.GONE);
				progressCopy.setVisibility(View.GONE);
				btnStart.setVisibility(View.VISIBLE);
				btnScan.setVisibility(View.VISIBLE);
				break;

			case 2:
				textHint.setText(getResources().getString(
						R.string.importing_not_exist));
				progressCopy.setVisibility(View.VISIBLE);
				btnStart.setVisibility(View.GONE);
				btnScan.setVisibility(View.GONE);
				break;

			case 3:
				int now = msg.arg1;
				int total = msg.arg2;
				textDetail.setVisibility(View.VISIBLE);
				textDetail.setText(now + "/" + total);
				break;

			case 4:
				// textHint.setText("正在检测SD卡是否有离线地图");
				break;
			}
			super.handleMessage(msg);
		}
	};

	public class CopyThread implements Runnable {

		@Override
		public void run() {

			for (int i = 0; i < pathFrom.length; i++) {
				if (!copyFolder(pathFrom[i], Constant.Path.BAIDU_OFFLINE_SUB)) {

				} else {
					Message message = new Message();
					message.what = 4;
					copyHandler.sendMessage(message);
					Log.v(Constant.TAG, "Copy Map form SD Success, From:"
							+ pathFrom[i]);
					break;
				}
			}

			mOffline.importOfflineData();
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
			MyLog.v("[ImportOfflineMapThread]Import map number:" + mapNum);
		}
	}

	/**
	 * 导入离线地图包数据
	 */
	private MKOfflineMap mOffline = null;

	class MyMKOfflineMapListener implements MKOfflineMapListener {

		@Override
		public void onGetOfflineMapState(int type, int state) {
			switch (type) {
			case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
				MKOLUpdateElement update = mOffline.getUpdateInfo(state);
				// 处理下载进度更新提示
				if (update != null) {
					// stateView.setText(String.format("%s : %d%%",
					// update.cityName,
					// update.ratio));
					// updateView();
				}
				break;
			case MKOfflineMap.TYPE_NEW_OFFLINE:
				// 有新离线地图安装
				break;
			case MKOfflineMap.TYPE_VER_UPDATE:
				// 版本更新提示
				// MKOLUpdateElement e = mOffline.getUpdateInfo(state);
				break;
			}
		}
	}

	@Override
	protected void onStop() {
		isActivityShow = false;
		super.onStop();
	}

}
