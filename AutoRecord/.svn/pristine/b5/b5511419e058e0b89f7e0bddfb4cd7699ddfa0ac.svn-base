package com.tchip.tachograph;

import android.view.Surface;
import android.hardware.Camera;
import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class TachographRecorder {

	static {
		System.loadLibrary("tachograph_recorder_jni");
		native_init();
	}
	private static final String TAG = "JavaTachographRecorder";

	public TachographRecorder() {
		Looper looper;
		if ((looper = Looper.myLooper()) != null) {
			mEventHandler = new EventHandler(this, looper);
		} else if ((looper = Looper.getMainLooper()) != null) {
			mEventHandler = new EventHandler(this, looper);
		} else {
			mEventHandler = null;
		}
		native_setup(new WeakReference<TachographRecorder>(this));
	}

	@Override
	protected void finalize() {
		native_finalize();
	}

	/** 设置保存路径 */
	public native int setDirectory(String path);

	/** 前缀，后缀 */
	public native int setMediaFilenameFixs(int filetype, String prefix,
			String suffix);

	/** 路径 */
	public native int setMediaFileDirectory(int filetype, String dir);

	/** setCamera */
	public native int setCamera(Camera camera);

	// public native int setVideoCameraId(int cameraId);
	public native int setPreviewSurface(Surface sv);

	/** 设置名称 */
	public native int setClientName(String clientName);

	/** 设置码率：影响体积，与体积成正比：码率越大，体积越大；码率越小，体积越小。采样频率 */
	public native int setVideoBiteRate(int bps);

	/** 设置分辨率 */
	public native int setVideoSize(int width, int height);

	/** 设置帧率 */
	public native int setVideoFrameRate(int fps);

	/** 设置视频重叠 */
	public native int setVideoOverlap(int sec);

	/*
	 * public native int setSecondaryVideoEnable(boolean enable); public native
	 * int setSecondaryVideoBiteRate(int bps); public native int
	 * setSecondaryVideoSize(int width , int height ); public native int
	 * setSecondaryVideoFrameRate(int fps);
	 */
	/** 拍照 */
	public native int takePicture();

	/** 设置视频分段：每段视频多久 */
	public native int setVideoSeconds(int s);

	public native int setAudioSourceType(int ast);

	/** 音频采样率 */
	public native int setAudioSampleRate(int asr);

	public native int setAudioChannelCount(int anc);

	/** 设置是否静音 */
	public native int setMute(boolean mute);

	public native int setScaledStreamEnable(boolean enable, int width, int height);

	public native int setShareVideoEnable(boolean enable, int width,
			int height, int bps, int befsec, int aftsec);

	public native int recordShareVideo();

	private native int stopShareVideo();

	/** 准备好了 */
	public native int prepare();

	public native int start();

	/** 停止录像 */
	public native int stop();

	/** 关闭 */
	public native int close();

	/** 释放 */
	public native int release();

	/** 是否在录制 */
	public native int isRecording();

	public native int getVersionCode();

	public native String getVersionName();

	private static native final void native_init();

	private long mNativeContext;
	private long mNativeCallback;

	private native final void native_setup(Object mediarecorder_this);

	private native final void native_finalize();

	private static void postEventFromNative(Object cc_ref, int msg, int arg1,
			int arg2, Object obj) {
		TachographRecorder c = (TachographRecorder) ((WeakReference) cc_ref)
				.get();
		if (c == null)
			return;

		if (c.mEventHandler != null) {
			Message m = c.mEventHandler.obtainMessage(msg, arg1, arg2, obj);
			c.mEventHandler.sendMessage(m);
		}
	}

	private EventHandler mEventHandler;

	private class EventHandler extends Handler {
		private TachographRecorder mTachographRecorder;

		public EventHandler(TachographRecorder tr, Looper looper) {
			super(looper);
			mTachographRecorder = tr;
		}

		@Override
		public void handleMessage(Message msg) {
			/*
			 * Log.d(TAG,"msg.waht="+msg.what); Log.d(TAG,"msg.arg1="+msg.arg1);
			 * Log.d(TAG,"msg.arg2="+msg.arg2); Log.d(TAG,"msg.obj="+msg.obj);
			 */
			String str;
			int type;
			switch (msg.what) {
			case 0:
				int err = msg.arg1;
				onError(err);
				break;
			case 1:
				str = (String) msg.obj;
				type = msg.arg1;
				onFileSave(type, str);
				break;
			case 2:
				byte[] data = (byte[]) msg.obj;
				int width = msg.arg1;
				int height = msg.arg2;
				onScaledStream(data, width, height);
				break;
			case 3:
				str = (String) msg.obj;
				type = msg.arg1;
				onFileStart(type, str);
				break;
			default:
				break;
			}
		}
	}

	private void onFileSave(int type, String path) {
		if (mTachographCallback != null) {
			mTachographCallback.onFileSave(type, path);
		}
	}

	private void onError(int err) {
		if (mTachographCallback != null) {
			mTachographCallback.onError(err);
		}
	}

	private void onFileStart(int type, String path) {
		if (mTachographCallback != null) {
			mTachographCallback.onFileStart(type, path);
		}
	}

	private TachographCallback mTachographCallback;

	/** 回调方法 */
	public void setTachographCallback(TachographCallback tc) {
		mTachographCallback = tc;
	}

	private void onScaledStream(byte[] data, int width, int height) {
		if (mScaledStreamCallback != null) {
			mScaledStreamCallback.onScaledStream(data, width, height);
		}
	}

	private ScaledStreamCallback mScaledStreamCallback;

	public void setScaledStreamCallback(ScaledStreamCallback ssc) {
		mScaledStreamCallback = ssc;
	}
}
