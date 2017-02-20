package com.tchip.autorecord.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.WindowManager;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.R;
import com.tchip.autorecord.view.FlashCleanDialog;

public class FileUtil {

	/**
	 * 遍历目录大小
	 * 
	 * @return 剩余空间，单位：字节B
	 */
	public static long getTotalSizeOfFilesInDir(File file) {
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children) {
				total += getTotalSizeOfFilesInDir(child);
			}
		return total;
	}

	/**
	 * 获取不同分辨率下，前后录像所占空间比率
	 * 
	 * @param resolution
	 * @param isFront
	 * @return
	 */
	private static float getVideoRate(int resolution, boolean isFront) {
		switch (resolution) {
		case Constant.Record.STATE_RESOLUTION_1080P:
			return isFront ? 194.0f / 219 : 25.0f / 219;

		case Constant.Record.STATE_RESOLUTION_720P:
		default:
			return isFront ? 137.0f / 162 : 25.0f / 162;
		}
	}

	/**
	 * [双录到同一张SD卡]空间是否不足，需要删除旧视频
	 * 
	 * 前录路径：/storage/sdcard1/DrivingRecord/VideoFront/ *.mp4
	 * 
	 * 后录路径：/storage/sdcard1/tachograph_back/DrivingRecord/unlock/
	 * 
	 */
	public static boolean isFrontStorageLess() {
		// float sdTotal =
		// StorageUtil.getSDTotalSize(Constant.Path.RECORD_SDCARD); // SD卡总空间
		float sdFree = StorageUtil
				.getSDAvailableSize(Constant.Path.RECORD_SDCARD); // SD剩余空间
		float frontUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.VIDEO_FRONT_SD)); // 前置已用空间
		float backUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.VIDEO_BACK_SD)); // 后置已用空间

		float recordTotal = sdFree + frontUse + backUse; // 录像可用空间
		float frontTotal = recordTotal
				* getVideoRate(MyApp.resolutionState, true); // 前置归属空间
		float frontFree = frontTotal - frontUse; // 前置剩余空间
		long intFrontFree = (long) frontFree;
		long intSdFree = (long) sdFree;

		boolean isStorageLess = intFrontFree < Constant.Record.FRONT_MIN_FREE_STORAGE
				|| intSdFree < Constant.Record.FRONT_MIN_FREE_STORAGE;
		MyLog.v("FileUtil.isFrontStorageLess:" + isStorageLess);
		return isStorageLess;
	}

	public static boolean isFrontLockLess() {
		float sdTotal = StorageUtil.getSDTotalSize(Constant.Path.RECORD_SDCARD); // SD卡总空间
		float frontLockUse = (float) FileUtil
				.getTotalSizeOfFilesInDir(new File(
						Constant.Path.VIDEO_FRONT_SD_LOCK)); // 前置加锁已用空间
		long intFrontLockUse = (long) frontLockUse;
		long intFrontLockMax = (long) (sdTotal * Constant.Record.FRONT_LOCK_MAX_PERCENT);

		boolean isFrontLockLess = intFrontLockUse > intFrontLockMax;
		MyLog.v("FileUtil.isFrontLockLess:" + isFrontLockLess + ",USE:"
				+ intFrontLockUse + ",MAX:" + intFrontLockMax);
		return isFrontLockLess;
	}

	/** 本机内部存储可用空间低于设定阈值 */
	public static boolean isFlashStorageLess() {
		if (Constant.Record.flashToCard) {
			float flashFree = StorageUtil
					.getSDAvailableSize(Constant.Path.SDCARD_0);
			long intFlashFree = (int) flashFree;
			boolean isFlashStorageLess = intFlashFree < Constant.Record.FLASH_MIN_FREE_STORAGE;
			MyLog.v("FileUtil.isFlashStorageLess:" + isFlashStorageLess);
			return isFlashStorageLess;
		} else {
			return false;
		}
	}

	public static void showFlashCleanDialog(final Context context) {
		FlashCleanDialog.Builder builder = new FlashCleanDialog.Builder(
				context.getApplicationContext());
		builder.setMessage(context.getResources().getString(
				R.string.dialog_flash_clean_content));
		builder.setTitle(context.getResources().getString(
				R.string.dialog_flash_clean_title));
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ComponentName componentFileMtk = new ComponentName(
						"com.mediatek.filemanager",
						"com.mediatek.filemanager.FileManagerOperationActivity");
				Intent intentFileMtk = new Intent();
				intentFileMtk.setComponent(componentFileMtk);
				intentFileMtk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				context.startActivity(intentFileMtk);
			}
		});

		FlashCleanDialog flashCleanDialog = builder.create();
		flashCleanDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		flashCleanDialog.setCanceledOnTouchOutside(false);
		if (!flashCleanDialog.isShowing()) {
			flashCleanDialog.show();
		}
	}

	public static boolean isBackStorageLess() {
		// float sdTotal =
		// StorageUtil.getSDTotalSize(Constant.Path.RECORD_SDCARD); // SD卡总空间
		float sdFree = StorageUtil
				.getSDAvailableSize(Constant.Path.RECORD_SDCARD); // SD剩余空间
		float frontUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.VIDEO_FRONT_SD)); // 前置已用空间
		float backUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.VIDEO_BACK_SD)); // 后置已用空间

		float recordTotal = sdFree + frontUse + backUse; // 录像可用空间
		float backTotal = recordTotal
				* getVideoRate(MyApp.resolutionState, false); // 后置归属空间
		float backFree = backTotal - backUse; // 后置剩余空间
		int intBackFree = (int) backFree;
		int intSdFree = (int) sdFree;

		boolean isStorageLess = intBackFree < Constant.Record.BACK_MIN_FREE_STORAGE
				|| intSdFree < Constant.Record.FRONT_MIN_FREE_STORAGE;
		MyLog.v("FileUtil.isBackStorageLess:" + isStorageLess);
		return isStorageLess;
	}

	public static boolean isBackLockLess() {
		float sdTotal = StorageUtil.getSDTotalSize(Constant.Path.RECORD_SDCARD); // SD卡总空间
		float backLockUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.VIDEO_BACK_SD_LOCK)); // 前置加锁已用空间
		long intBackLockUse = (long) backLockUse;
		long intBackLockMax = (long) (sdTotal * Constant.Record.BACK_LOCK_MAX_PERCENT);

		boolean isBackLockLess = intBackLockUse > intBackLockMax;
		MyLog.v("FileUtil.isBackLockLess:" + isBackLockLess + ",USE:"
				+ intBackLockUse + ",MAX:" + intBackLockMax);
		return isBackLockLess;
	}

	// ****************************************Below is OLD

	public static final int SIZETYPE_B = 1; // 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2; // 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3; // 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4; // 获取文件大小单位为GB的double值

	public FileUtil() {
	}

	/**
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @param sizeType
	 *            获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("FileUtil.getFileOrFilesSize:FAIL");
		}
		return FormetFileSize(blockSize, sizeType);
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("FileUtil.getAutoFileOrFilesSize:FAIL");
		}
		return FormetFileSize(blockSize);
	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
			fis.close();
		} else {
			file.createNewFile();
			MyLog.e("FileUtil.getFileSize:FAIL");

		}
		return size;
	}

	/**
	 * 获取指定文件夹
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 * 
	 * @param fileS
	 * @param sizeType
	 * @return
	 */
	private static double FormetFileSize(long fileS, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
		case SIZETYPE_B:
			fileSizeLong = Double.valueOf(df.format((double) fileS));
			break;
		case SIZETYPE_KB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
			break;
		case SIZETYPE_MB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
			break;
		case SIZETYPE_GB:
			fileSizeLong = Double.valueOf(df
					.format((double) fileS / 1073741824));
			break;
		default:
			break;
		}
		return fileSizeLong;
	}

}