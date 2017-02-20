package com.tchip.tachograph;


public interface TachographCallback {
	public static final int FILE_TYPE_IMAGE = 0 ;
	public static final int FILE_TYPE_VIDEO = 1 ;
	public static final int FILE_TYPE_SHARE_VIDEO = 2 ;
	
	public static final int ERROR_RECORDER_CLOSED = 0 ;
	public static final int ERROR_SAVE_IMAGE_FAIL = 1 ;
	public static final int ERROR_SAVE_VIDEO_FAIL = 2 ;
	public static final int ERROR_SAVE_SHARE_VIDEO_FAIL = 3 ;

	public void onError(int err);
	public void onFileSave(int type, String path);
	public void onFileStart(int type, String path);
}
