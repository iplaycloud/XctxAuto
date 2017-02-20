package com.xctx.baidunavi.view;

import com.xctx.baidunavi.R;
import com.xctx.baidunavi.util.MyLog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AudioRecordDialog {
	private Dialog dialog;
	private ImageView imageVolume, imageRecord, imageError;
	private TextView textHint;
	private ProgressBar progressLoad;

	private Context context;

	public AudioRecordDialog(Context context) {
		this.context = context;
		dialog = new Dialog(context, R.style.Theme_RecordDialog);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_audio_record, null);
		dialog.setContentView(view);

		imageVolume = (ImageView) dialog.findViewById(R.id.imageVolume);
		imageRecord = (ImageView) dialog.findViewById(R.id.imageRecord);
		imageError = (ImageView) dialog.findViewById(R.id.imageError);
		textHint = (TextView) dialog.findViewById(R.id.textHint);

		progressLoad = (ProgressBar) dialog.findViewById(R.id.progressLoad);
	}

	/**
	 * 显示音量框
	 */
	public void showVoiceDialog() {
		imageVolume.setVisibility(View.VISIBLE);
		imageRecord.setVisibility(View.VISIBLE);
		imageError.setVisibility(View.INVISIBLE);
		progressLoad.setVisibility(View.INVISIBLE);
		textHint.setText("请说话");
		if (dialog != null) {
			dialog.show();
		}
	}

	public void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			MyLog.e("[AudioRecordDialog]dismissDialog catch exception:"
					+ e.toString());
		}
	}

	/**
	 * 显示加载对话框
	 */
	public void showLoadDialog() {
		textHint.setText("加载中");
		imageVolume.setVisibility(View.INVISIBLE);
		imageRecord.setVisibility(View.INVISIBLE);
		imageError.setVisibility(View.INVISIBLE);
		progressLoad.setVisibility(View.VISIBLE);
		if (dialog != null) {
			dialog.show();
		}
	}

	/**
	 * 显示错误对话框
	 */
	public void showErrorDialog(String text) {
		textHint.setText(text);
		imageVolume.setVisibility(View.INVISIBLE);
		imageRecord.setVisibility(View.INVISIBLE);
		imageError.setVisibility(View.VISIBLE);
		progressLoad.setVisibility(View.INVISIBLE);
		if (dialog != null) {
			dialog.show();
		}
	}

	/**
	 * 更新音量
	 * 
	 * @param volume
	 *            0-30
	 */
	public void updateVolumeLevel(int volume) {
		if (dialog != null && dialog.isShowing()) {
			int level = 7 * volume / 31 + 1;
			int volumeResId = context.getResources().getIdentifier(
					"icon_volume_" + level, "drawable",
					context.getPackageName());
			imageVolume.setImageResource(volumeResId);
		}
	}

}
