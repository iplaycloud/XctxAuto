package com.tchip.carlauncher.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import com.tchip.carlauncher.Constant;

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
	 * [双录到同一张SD卡]空间是否不足，需要删除旧视频
	 * 
	 * 前录路径：/storage/sdcard2/tachograph/ *.mp4
	 * 
	 * 后录路径：/storage/sdcard2/tachograph_back/DrivingRecord/unlock/
	 * 
	 */
	public static boolean isStorageLessSingle() {
		// float sdTotal =
		// StorageUtil.getSDTotalSize(Constant.Path.RECORD_SDCARD); // SD卡总空间
		float sdFree = StorageUtil
				.getSDAvailableSize(Constant.Path.RECORD_SDCARD); // SD剩余空间
		float frontUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.RECORD_FRONT)); // 前置已用空间

		float backUse = (float) FileUtil.getTotalSizeOfFilesInDir(new File(
				Constant.Path.RECORD_BACK)); // 后置已用空间
		float frontTotal = (sdFree + frontUse + backUse) * 4 / 5; // 前置归属空间
		float frontFree = frontTotal - frontUse; // 前置剩余空间
		int intFrontFree = (int) frontFree;
		int intSdFree = (int) sdFree;

		boolean isStorageLess = intFrontFree < Constant.Record.SD_MIN_FREE_STORAGE
				|| intSdFree < Constant.Record.SD_MIN_FREE_STORAGE;
		MyLog.v("[isStroageLess]" + isStorageLess + ",sdFree:" + sdFree
				+ "\nfrontUse:" + frontUse + "\nfrontTotal:" + frontTotal
				+ "\nfrontFree" + frontFree);
		return isStorageLess;
	}

	/**
	 * [双录到两张SD卡]空间是否不足，需要删除旧视频
	 * 
	 * 前录路径：/storage/sdcard2/tachograph/ *.mp4
	 * 
	 * 后录路径：/storage/sdcard2/tachograph_back/DrivingRecord/unlock/
	 * 
	 */
	public static boolean isStorageLessDouble() {
		// float sdTotal = StorageUtil.getSDTotalSize(sdcardPath); // SD卡总空间
		float sdFree = StorageUtil
				.getSDAvailableSize(Constant.Path.RECORD_SDCARD);
		int intSdFree = (int) sdFree;
		MyLog.v("[StorageUtil]isStroageLess, sdFree:" + intSdFree);
		return intSdFree < Constant.Record.SD_MIN_FREE_STORAGE;
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
			MyLog.e("获取文件大小:获取失败!");
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
			MyLog.e("获取文件大小:获取失败!");
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
		} else {
			file.createNewFile();
			MyLog.e("获取文件大小:文件不存在!");
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