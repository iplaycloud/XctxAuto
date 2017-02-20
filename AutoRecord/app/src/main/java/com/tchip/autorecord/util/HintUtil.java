package com.tchip.autorecord.util;

import com.tchip.autorecord.R;
import com.tchip.autorecord.service.FloatWindowService;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

public class HintUtil {

	public static void playAudio(Context context, int type, boolean playSound) {
		if (playSound) {
			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			MediaPlayer mediaPlayer;
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) > 0) {
				if (type == com.tchip.tachograph.TachographCallback.FILE_TYPE_IMAGE) {
					mediaPlayer = MediaPlayer.create(context,
							R.raw.camera_image);
				} else {
					mediaPlayer = MediaPlayer.create(context,
							R.raw.camera_video);
				}
				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
			}
		}
	}

	public static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示或隐藏录像提示悬浮窗
	 */
	public static void setRecordHintFloatWindowVisible(Context context,
			boolean isVisible) {
		MyLog.v("HintUtil.setRecordHintFloatWindowVisible:" + isVisible);
		if (isVisible) {
			Intent intentFloatWindow = new Intent(context,
					FloatWindowService.class);
			context.startService(intentFloatWindow);
		} else {
			Intent intentFloatWindow = new Intent(context,
					FloatWindowService.class);
			context.stopService(intentFloatWindow);
		}
	}

}
