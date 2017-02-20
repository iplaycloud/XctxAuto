package com.tchip.autorecord.thread;

import android.media.ExifInterface;

import com.tchip.autorecord.util.MyLog;

/**
 * 写入照片EXIF信息
 */
public class WriteImageExifThread extends Thread {
	String imagePath;

	public WriteImageExifThread(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public void run() {
		super.run();
		try { // Android Way
			if (null != imagePath && imagePath.trim().length() > 0) {
				ExifInterface exif = new ExifInterface(imagePath);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""
						+ ExifInterface.ORIENTATION_NORMAL);
				exif.setAttribute(ExifInterface.TAG_MAKE, "TQ"); // 品牌
				exif.setAttribute(ExifInterface.TAG_MODEL, "X2"); // 型号/机型
				exif.setAttribute(ExifInterface.TAG_FLASH, "0"); // 闪光灯：关闭
				exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, ""
						+ ExifInterface.WHITEBALANCE_AUTO); // 白平衡：自动
				exif.setAttribute(ExifInterface.TAG_ISO, "100"); // ISO感光度
				exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "1/30"); // 曝光时间
				exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "7/2"); // 焦距：3.5mm
				exif.saveAttributes();
			}
		} catch (Exception e) {
			MyLog.e("writeImageExif.Set Attribute Catch Exception:"
					+ e.toString());
			e.printStackTrace();
		}
	}
}