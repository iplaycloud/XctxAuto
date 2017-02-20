package com.tchip.autorecord.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.thread.MoveThread;

public class Flash2SDUtil {

	public static void moveVideoToSD(Context context, boolean isFront,
			boolean isLock, String videoName) {
		if (Constant.Record.flashToCard) {
			String oldFilePath = isFront ? Constant.Path.VIDEO_FRONT_FLASH
					+ videoName : Constant.Path.VIDEO_BACK_FLASH + videoName;
			String newFilePath = isFront ? Constant.Path.VIDEO_FRONT_SD
					: Constant.Path.VIDEO_BACK_SD;
			if (isLock) {
				newFilePath = (isFront ? Constant.Path.VIDEO_FRONT_SD_LOCK
						: Constant.Path.VIDEO_BACK_SD_LOCK) + File.separator;
			}
			newFilePath = newFilePath + videoName;
			boolean isSuccess = copyFile(oldFilePath, newFilePath);
			MyLog.v("moveVideoToSD,name:" + videoName + ",isSuccess:"
					+ isSuccess);
		}
	}

	/**
	 * onFileStart时将拔卡导致没能移动到SD卡的文件移动到SD
	 */
	public static void moveOldFrontVideoToSD() {
		if (Constant.Record.flashToCard) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						File dirFront = new File(
								Constant.Path.VIDEO_FRONT_FLASH);
						String[] listFront = dirFront.list();
						File fileTemp = null;
						for (int i = 0; i < listFront.length; i++) {
							fileTemp = new File(Constant.Path.VIDEO_FRONT_FLASH
									+ File.separator + listFront[i]);
							if (fileTemp.isFile()
									&& !fileTemp.getName().startsWith(".")
									&& fileTemp.getName().endsWith("mp4")) {
								FileInputStream input = new FileInputStream(
										fileTemp);
								FileOutputStream output = new FileOutputStream(
										Constant.Path.VIDEO_FRONT_SD
												+ File.separator
												+ (fileTemp.getName())
														.toString());
								byte[] b = new byte[1024 * 5];
								int len;
								while ((len = input.read(b)) != -1) {
									output.write(b, 0, len);
								}
								output.flush();
								output.close();
								input.close();
								fileTemp.delete();
								MyLog.v("moveOldFrontVideoToSD:"
										+ fileTemp.getName());
							}
						}
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	public static void moveOldBackVideoToSD() {
		if (Constant.Record.flashToCard) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						File dirBack = new File(Constant.Path.VIDEO_BACK_FLASH);
						String[] listBack = dirBack.list();
						File fileTemp = null;
						for (int i = 0; i < listBack.length; i++) {
							fileTemp = new File(Constant.Path.VIDEO_BACK_FLASH
									+ File.separator + listBack[i]);
							if (fileTemp.isFile()
									&& !fileTemp.getName().startsWith(".")
									&& fileTemp.getName().endsWith("mp4")) {
								FileInputStream input = new FileInputStream(
										fileTemp);
								FileOutputStream output = new FileOutputStream(
										Constant.Path.VIDEO_BACK_SD
												+ File.separator
												+ (fileTemp.getName())
														.toString());
								byte[] b = new byte[1024 * 5];
								int len;
								while ((len = input.read(b)) != -1) {
									output.write(b, 0, len);
								}
								output.flush();
								output.close();
								input.close();
								fileTemp.delete();
								MyLog.v("moveOldBackVideoToSD:"
										+ fileTemp.getName());
							}
						}
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	public static void moveOldImageToSD() {
		if (Constant.Record.flashToCard) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						File dirImage = new File(Constant.Path.IMAGE_FLASH);
						String[] listImage = dirImage.list();
						File fileTemp = null;
						for (int i = 0; i < listImage.length; i++) {
							fileTemp = new File(Constant.Path.IMAGE_FLASH
									+ File.separator + listImage[i]);
							if (fileTemp.isFile()
									&& !fileTemp.getName().startsWith(".")
									&& fileTemp.getName().endsWith("jpg")) {
								FileInputStream input = new FileInputStream(
										fileTemp);
								FileOutputStream output = new FileOutputStream(
										Constant.Path.IMAGE_SD
												+ File.separator
												+ (fileTemp.getName())
														.toString());
								byte[] b = new byte[1024 * 5];
								int len;
								while ((len = input.read(b)) != -1) {
									output.write(b, 0, len);
								}
								output.flush();
								output.close();
								input.close();
								fileTemp.delete();
								MyLog.v("moveOldImageToSD:"
										+ fileTemp.getName());
							}
						}
					} catch (Exception e) {
					}
				}

			}).start();
		}
	}

	/** 删除Flash中的点文件 */
	public static void deleteFlashDotFile() {
		if (Constant.Record.flashToCard) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if (!MyApp.isFrontRecording) {
							File dirFront = new File(
									Constant.Path.VIDEO_FRONT_FLASH);
							String[] listFront = dirFront.list();
							File fileTemp = null;
							for (int i = 0; i < listFront.length; i++) {
								fileTemp = new File(
										Constant.Path.VIDEO_FRONT_FLASH
												+ File.separator + listFront[i]);
								if (fileTemp.isFile()
										&& fileTemp.getName().startsWith(".")
										&& !MyApp.isFrontRecording) {
									fileTemp.delete();
									MyLog.v("deleteFlashDotFile:"
											+ fileTemp.getName());
								}
							}
						}
						if (!MyApp.isBackRecording) {
							File dirBack = new File(
									Constant.Path.VIDEO_BACK_FLASH);
							String[] listBack = dirBack.list();
							File fileTemp = null;
							for (int i = 0; i < listBack.length; i++) {
								fileTemp = new File(
										Constant.Path.VIDEO_BACK_FLASH
												+ File.separator + listBack[i]);
								if (fileTemp.isFile()
										&& fileTemp.getName().startsWith(".")
										&& !MyApp.isBackRecording) {
									fileTemp.delete();
									MyLog.v("deleteFlashDotFile:"
											+ fileTemp.getName());
								}
							}
						}
					} catch (Exception e) {
					}
				}

			}).start();
		}
	}

	public static void deleteFlashDotFileForcely() {
		if (Constant.Record.flashToCard) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						File dirFront = new File(
								Constant.Path.VIDEO_FRONT_FLASH);
						String[] listFront = dirFront.list();
						File fileTempFront = null;
						for (int i = 0; i < listFront.length; i++) {
							fileTempFront = new File(
									Constant.Path.VIDEO_FRONT_FLASH
											+ File.separator + listFront[i]);
							if (fileTempFront.isFile()
									&& fileTempFront.getName().startsWith(".")
									&& !MyApp.isFrontRecording) {
								fileTempFront.delete();
								MyLog.v("deleteFlashDotFile:"
										+ fileTempFront.getName());
							}
						}
						File dirBack = new File(Constant.Path.VIDEO_BACK_FLASH);
						String[] listBack = dirBack.list();
						File fileTempBack = null;
						for (int i = 0; i < listBack.length; i++) {
							fileTempBack = new File(
									Constant.Path.VIDEO_BACK_FLASH
											+ File.separator + listBack[i]);
							if (fileTempBack.isFile()
									&& fileTempBack.getName().startsWith(".")
									&& !MyApp.isBackRecording) {
								fileTempBack.delete();
								MyLog.v("deleteFlashDotFile:"
										+ fileTempBack.getName());
							}
						}
					} catch (Exception e) {
					}
				}

			}).start();
		}
	}

	public static boolean copyFile(String oldFilePath, String newFilePath) {
		boolean isCopySuccess = true;
		new Thread(new MoveThread(oldFilePath, newFilePath)).start();
		return isCopySuccess;
	}

}
