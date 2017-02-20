package com.tchip.tachograph;

import android.view.Surface;
import android.hardware.Camera;
import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
    protected void finalize() { native_finalize(); }
    
    public native int setDirectory(String path);
    public native int setMediaFilenameFixs(int filetype,String prefix,String suffix);
    public native int setMediaFileDirectory(int filetype,String dir);
	
	public native int setCamera(Camera camera);
	//public native int setVideoCameraId(int cameraId);
	public native int setPreviewSurface(Surface sv);
	public native int setClientName(String clientName);
	
	public native int setVideoBiteRate(int bps);
	public native int setVideoSize(int width , int height );
	public native int setVideoFrameRate(int fps);
	
	public native int setVideoOverlap(int sec);
	public native int setSpeed(int speed);
	public native int setLat(String mlat);
	public native int setLong(String mlong);
	
	
	public native int setSecondaryVideoEnable(boolean enable);
	public native int setSecondaryVideoBiteRate(int bps);
	public native int setSecondaryVideoSize(int width , int height );
	public native int setSecondaryVideoFrameRate(int fps);
	
	
	public native int takePicture();
	public native int setVideoSeconds(int s);
	public native int setAudioSourceType(int ast);
	public native int setAudioSampleRate(int asr);
	public native int setAudioChannelCount(int anc);
	public native int setMute(boolean mute);
	public native int setScaledStreamEnable(boolean enable, int width , int height);
	public native int setShareVideoEnable(boolean enable , int width , int height , int bps , int befsec , int aftsec);
	public native int recordShareVideo();
	private native int stopShareVideo();
	
	public native int prepare();
	public native int start();
	public native int stop();
	public native int close();
	public native int release();
	
	public native int isRecording();
	public native int getVersionCode();
	public native String getVersionName();
	
	private static native final void native_init();
	private long mNativeContext;
	private long mNativeCallback;
	
	private native final void native_setup(Object mediarecorder_this);
	private native final void native_finalize();
	
	private static void postEventFromNative(Object cc_ref, int msg , int arg1 , int arg2 , Object obj)
    {
        TachographRecorder c = (TachographRecorder)((WeakReference)cc_ref).get();
        if (c == null)
            return;

        if (c.mEventHandler != null) {
            Message m = c.mEventHandler.obtainMessage(msg, arg1, arg2, obj);
            c.mEventHandler.sendMessage(m);
        }
    }
	
	private EventHandler mEventHandler;
    private class EventHandler extends Handler
    {
        private TachographRecorder mTachographRecorder;

        public EventHandler(TachographRecorder tr, Looper looper) {
            super(looper);
            mTachographRecorder = tr;
        }
        
        @Override
        public void handleMessage(Message msg) {
        	/*
        	Log.d(TAG,"msg.waht="+msg.what);
        	Log.d(TAG,"msg.arg1="+msg.arg1);
        	Log.d(TAG,"msg.arg2="+msg.arg2);
        	Log.d(TAG,"msg.obj="+msg.obj);
        	*/
        	String str;
        	int type;
        	switch(msg.what) {
        	case 0 :
        		int err = msg.arg1;
        		onError(err);
        		break;
        	case 1 :
        		str = (String)msg.obj;
        		type = msg.arg1;
        		onFileSave(type,str);
        		break;
        	case 2 :
        	    byte[] data = (byte[])msg.obj;
        	    int width = msg.arg1;
        	    int height = msg.arg2;
        	    onScaledStream(data,width,height);
        	    break;
        	case 3 :
        		str = (String)msg.obj;
        		type = msg.arg1;
        		onFileStart(type,str);
        		break;
        	default :
        		break;
        	}
        }
    }
    
    private void onFileSave(int type , String path){
    	if(mTachographCallback!=null){
			mTachographCallback.onFileSave(type, path);
		}
    }
    private void onError(int err){
    	if(mTachographCallback!=null){
			mTachographCallback.onError(err);
		}
    }
    private void onFileStart(int type , String path){
    	if(mTachographCallback!=null){
			mTachographCallback.onFileStart(type, path);
		}
    }
    private TachographCallback mTachographCallback;
	public void setTachographCallback(TachographCallback tc){
		mTachographCallback = tc ;
	}
	
	private void onScaledStream(byte[] data,int width , int height){
	    if(mScaledStreamCallback!=null){
	        mScaledStreamCallback.onScaledStream(data,width,height);
	    }
	}
	private ScaledStreamCallback mScaledStreamCallback;
	public void setScaledStreamCallback(ScaledStreamCallback ssc){
	    mScaledStreamCallback = ssc;
	}
}

