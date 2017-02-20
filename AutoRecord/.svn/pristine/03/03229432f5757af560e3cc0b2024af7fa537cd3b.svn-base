package com.tchip.autorecord.util;

import java.io.File;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.R;
import com.tchip.autorecord.db.BackVideoDbHelper;
import com.tchip.autorecord.db.DriveVideo;
import com.tchip.autorecord.db.FrontVideoDbHelper;

import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.util.Log;

public class StorageUtil {

	/**
	 * 获得SD卡总大小
	 * 
	 * @return 总大小，单位：字节B
	 */
	public static long getSDTotalSize(String SDCardPath) {
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return 剩余空间，单位：字节B
	 */
	public static long getSDAvailableSize(String SDCardPath) {
		// StatFs stat = new StatFs("/storage/sdcard1");
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/** 录像SD卡是否存在 */
	public static boolean isFrontCardExist() {
		boolean isVideoCardExist = false;
		try {
			String pathVideo = Constant.Path.VIDEO_FRONT_SD;
			File fileVideo = new File(pathVideo);
			boolean isSuccess = fileVideo.mkdirs();
			MyLog.v("StorageUtil.isVideoCardExists,mkdirs isSuccess:"
					+ isSuccess);
			File file = new File(pathVideo);
			if (!file.exists()) {
				isVideoCardExist = false;
			} else {
				isVideoCardExist = true;
			}
		} catch (Exception e) {
			MyLog.e("StorageUtil.isVideoCardExists:Catch Exception!");
			isVideoCardExist = false;
		}
		MyLog.v("StorageUtil.isVideoCardExists:" + isVideoCardExist);
		return isVideoCardExist;
	}

	/** 录像SD卡是否存在 */
	public static boolean isBackCardExist() {
		boolean isVideoCardExist = false;
		try {
			String pathVideo = Constant.Path.VIDEO_BACK_SD;
			File fileVideo = new File(pathVideo);
			boolean isSuccess = fileVideo.mkdirs();
			MyLog.v("StorageUtil.isVideoCardExists,mkdirs isSuccess:"
					+ isSuccess);
			File file = new File(pathVideo);
			if (!file.exists()) {
				isVideoCardExist = false;
			} else {
				isVideoCardExist = true;
			}
		} catch (Exception e) {
			MyLog.e("StorageUtil.isVideoCardExists:Catch Exception!");
			isVideoCardExist = false;
		}
		MyLog.v("StorageUtil.isVideoCardExists:" + isVideoCardExist);
		return isVideoCardExist;
	}

	/** 创建前后录像存储卡目录 */
	public static void createRecordDirectory() {
		try {
			if (Constant.Record.flashToCard) {
				new File(Constant.Path.VIDEO_FRONT_FLASH).mkdirs();
				new File(Constant.Path.VIDEO_BACK_FLASH).mkdirs();
			}
			new File(Constant.Path.VIDEO_FRONT_SD).mkdirs();
			new File(Constant.Path.VIDEO_BACK_SD).mkdirs();
		} catch (Exception e) {
		}
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void RecursionDeleteFile(File file) {
		try {
			if (file.isFile()) {
				if (!file.getName().startsWith(".")) { // 不删除正在录制的视频
					file.delete();
				}
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					RecursionDeleteFile(f);
				}
				file.delete();
			}
		} catch (Exception e) {
			MyLog.e("StorageUtil.RecursionDeleteFile:Catch Exception!");
		}
	}

	/** 将加锁视频移动到加锁文件夹 */
	public static void lockVideo(boolean isFront, String videoName) {
		if (!Constant.Record.flashToCard) {
			String rawPath = isFront ? Constant.Path.VIDEO_FRONT_SD
					: Constant.Path.VIDEO_BACK_SD;
			String lockPath = isFront ? Constant.Path.VIDEO_FRONT_SD_LOCK
					: Constant.Path.VIDEO_BACK_SD_LOCK;
			File rawFile = new File(rawPath + videoName);
			if (rawFile.exists() && rawFile.isFile()) {
				File lockDir = new File(lockPath);
				if (!lockDir.exists()) {
					lockDir.mkdirs();
				}
				File lockFile = new File(lockDir + File.separator + videoName);
				rawFile.renameTo(lockFile);
				MyLog.v("StorageUtil.lockVideo:" + videoName);
			}
		}
	}

	/**
	 * 删除最旧视频，调用此函数的地方：
	 * 
	 * 1.开启录像 {@link MainActivity#startRecordTask}
	 * 
	 * 2.文件保存回调{@link MainActivity#onFileSave}
	 */
	public static boolean releaseFrontStorage(Context context) {
		if (!StorageUtil.isFrontCardExist()) {
			MyLog.e("Storageutil.deleteOldestUnlockVideo:No Video Card");
			return false;
		}
		try {
			FrontVideoDbHelper frontvideoDb = new FrontVideoDbHelper(context); // 视频数据库
			while (FileUtil.isFrontStorageLess()) {
				int oldestUnlockVideoId = frontvideoDb.getOldestUnlockVideoId();
				if (oldestUnlockVideoId != -1) { // 删除较旧未加锁视频文件
					String oldestUnlockVideoName = frontvideoDb
							.getVideNameById(oldestUnlockVideoId);
					File file = new File(Constant.Path.VIDEO_FRONT_SD
							+ oldestUnlockVideoName);
					if (file.exists() && file.isFile()) {
						MyLog.d("StorageUtil.Delete Old Unlock Video:"
								+ file.getPath());
						int i = 0;
						while (!file.delete() && i < 3) {
							i++;
							MyLog.d("StorageUtil.Delete Old Unlock Video:"
									+ file.getName() + " Filed!!! Try:" + i);
						}
						// 删除同时段后录视频
						deleteBackByFront(context, oldestUnlockVideoName);
					}
					frontvideoDb.deleteDriveVideoById(oldestUnlockVideoId); // 删除数据库记录
				} else { // 不存在未加锁视频
					int oldestVideoId = frontvideoDb.getOldestVideoId();
					if (oldestVideoId == -1) {
						if (FileUtil.isFrontStorageLess()) { // 此时若空间依然不足,提示用户清理存储（已不是行车视频的原因）
							MyLog.e("StorageUtil:Storage is full...");
							// TODO:显示格式化对话框
							speakVoice(context, context.getResources()
									.getString(R.string.sd_storage_too_low));
							return false;
						}
					} else { // 删除加锁视频
						String oldestVideoName = frontvideoDb
								.getVideNameById(oldestVideoId);
						File file = new File(Constant.Path.VIDEO_FRONT_SD_LOCK
								+ File.separator + oldestVideoName);
						MyLog.d("StorageUtil.Delete Old lock Front Video:"
								+ file.getPath());
						int i = 0;
						while (file.exists() && file.isFile() && !file.delete()
								&& i < 3) {
							i++;
							MyLog.d("StorageUtil.Delete Old lock Front Video:"
									+ file.getName() + " Filed!!! Try:" + i);
						}
						frontvideoDb.deleteDriveVideoById(oldestVideoId); // 删除数据库记录
					}
				}
			}
			// 前置加锁
			while (FileUtil.isFrontLockLess()) {
				int oldestLockVideoId = frontvideoDb.getOldestLockVideoId();
				String oldestVideoName = frontvideoDb
						.getVideNameById(oldestLockVideoId);
				File file = new File(Constant.Path.VIDEO_FRONT_SD_LOCK
						+ File.separator + oldestVideoName);
				MyLog.d("StorageUtil.Delete Old lock Front Video:"
						+ file.getPath());
				int i = 0;
				while (file.exists() && file.isFile() && !file.delete()
						&& i < 3) {
					i++;
					MyLog.d("StorageUtil.Delete Old lock Front Video:"
							+ file.getName() + " Filed!!! Try:" + i);
				}
				frontvideoDb.deleteDriveVideoById(oldestLockVideoId); // 删除数据库记录
			}

			return true;
		} catch (Exception e) {
			/* 异常原因：1.文件由用户手动删除 */
			MyLog.e("StorageUtil.deleteOldestUnlockVideo:Catch Exception:"
					+ e.toString());
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 删除前录文件，同时删除对应时段后录视频
	 * 
	 * @param context
	 * @param frontVideoName
	 */
	private static void deleteBackByFront(Context context, String frontVideoName) {
		try {
			if (null != frontVideoName && frontVideoName.trim().length() > 19
					&& !frontVideoName.startsWith(".")) {
				String videoPrefix = frontVideoName.substring(0, 15);
				MyLog.v("[deleteBackByFront]videoPrefix:" + videoPrefix);
				File dirBack = new File(Constant.Path.VIDEO_BACK_SD);
				File[] childFiles = dirBack.listFiles();
				for (File childFile : childFiles) {
					if (childFile.getName().startsWith(videoPrefix)
							&& childFile.exists()) {
						childFile.delete();
						MyLog.w("[deleteBackByFront]Delte Back:"
								+ childFile.getPath());
						for (File childFileOld : childFiles) { // 比删除视频的日期旧的视频也删除
							// 2016-08-22_203957_1.mp4
							// 0123456789A12345678
							String childFileName = childFileOld.getName();
							if (null != childFileName
									&& childFileName.trim().length() > 19
									&& !childFileName.startsWith(".")) {
								int childYear = Integer.parseInt(childFileName
										.substring(0, 4));
								int thisYear = Integer.parseInt(frontVideoName
										.substring(0, 4));
								if (childYear < thisYear) {
									if (childFileOld.exists()) {
										MyLog.w("[deleteBackByFront]Delte Back OLD:"
												+ childFileOld.getPath());
										childFileOld.delete();
									}
								} else if (childYear == thisYear) {
									int childMonth = Integer
											.parseInt(childFileName.substring(
													5, 7));
									int thisMonth = Integer
											.parseInt(frontVideoName.substring(
													5, 7));
									if (childMonth < thisMonth) {
										if (childFileOld.exists()) {
											MyLog.w("[deleteBackByFront]Delte Back OLD:"
													+ childFileOld.getPath());
											childFileOld.delete();
										}
									} else if (childMonth == thisMonth) {
										int childDay = Integer
												.parseInt(childFileName
														.substring(8, 10));
										int thisDay = Integer
												.parseInt(frontVideoName
														.substring(8, 10));

										if (childDay < thisDay) {
											if (childFileOld.exists()) {
												MyLog.w("[deleteBackByFront]Delte Back OLD:"
														+ childFileOld
																.getPath());
												childFileOld.delete();
											}
										} else if (childDay == thisDay) {
											int childHour = Integer
													.parseInt(childFileName
															.substring(11, 13));
											int thisHour = Integer
													.parseInt(frontVideoName
															.substring(11, 13));
											if (childHour < thisHour) {
												if (childFileOld.exists()) {
													MyLog.w("[deleteBackByFront]Delte Back OLD:"
															+ childFileOld
																	.getPath());
													childFileOld.delete();
												}
											} else if (childHour == thisHour) {
												int childMinute = Integer
														.parseInt(childFileName
																.substring(13,
																		15));
												int thisMinute = Integer
														.parseInt(frontVideoName
																.substring(13,
																		15));
												if (childMinute < thisMinute) {
													if (childFileOld.exists()) {
														MyLog.w("[deleteBackByFront]Delte Back OLD:"
																+ childFileOld
																		.getPath());
														childFileOld.delete();
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			MyLog.e("deleteBackByFront catch:" + e.toString());
		}
	}

	/**
	 * 删除最旧视频，调用此函数的地方：
	 * 
	 * 1.开启录像 {@link MainActivity#startRecordTask}
	 * 
	 * 2.文件保存回调{@link MainActivity#onFileSave}
	 */
	public static boolean releaseBackStorage(Context context) {
		if (!StorageUtil.isBackCardExist()) {
			MyLog.e("Storageutil.deleteOldestUnlockVideo:No Video Card");
			return false;
		}
		try {
			// 视频数据库
			BackVideoDbHelper backVideoDb = new BackVideoDbHelper(context);
			while (FileUtil.isBackStorageLess()) {
				int oldestUnlockVideoId = backVideoDb.getOldestUnlockVideoId();
				if (oldestUnlockVideoId != -1) { // 删除较旧未加锁视频文件
					String oldestUnlockVideoName = backVideoDb
							.getVideNameById(oldestUnlockVideoId);
					File file;
					if (oldestUnlockVideoName.endsWith("_1.mp4")) { // 后录
						file = new File(Constant.Path.VIDEO_BACK_SD
								+ oldestUnlockVideoName);
					} else {
						file = new File(Constant.Path.VIDEO_FRONT_SD
								+ oldestUnlockVideoName);
					}

					if (file.exists() && file.isFile()) {
						MyLog.d("StorageUtil.Delete Old Unlock Back Video:"
								+ file.getPath());
						int i = 0;
						while (!file.delete() && i < 3) {
							i++;
							MyLog.d("StorageUtil.Delete Old Unlock Back Video:"
									+ file.getName() + " Filed!!! Try:" + i);
						}
						// 删除同时段前录视频
						deleteFrontByBack(context, oldestUnlockVideoName);
					}
					backVideoDb.deleteDriveVideoById(oldestUnlockVideoId); // 删除数据库记录
				} else {
					int oldestVideoId = backVideoDb.getOldestVideoId();
					if (oldestVideoId == -1) {
						if (FileUtil.isFrontStorageLess()) { // 此时若空间依然不足,提示用户清理存储（已不是行车视频的原因）
							MyLog.e("StorageUtil:Storage is full...");
							// TODO
							speakVoice(context, context.getResources()
									.getString(R.string.sd_storage_too_low));
							return false;
						}
					} else { // 删除较旧的视频（加锁）
						String oldestVideoName = backVideoDb
								.getVideNameById(oldestVideoId);
						File file = new File(Constant.Path.VIDEO_BACK_SD_LOCK
								+ File.separator + oldestVideoName);

						MyLog.d("StorageUtil.Delete Old lock Back Video:"
								+ file.getPath());

						int i = 0;
						while (file.exists() && file.isFile() && !file.delete()
								&& i < 3) {
							i++;
							MyLog.d("StorageUtil.Delete Old lock Back Video:"
									+ file.getName() + " Filed!!! Try:" + i);
						}
						backVideoDb.deleteDriveVideoById(oldestVideoId); // 删除数据库记录
					}
				}
			}
			// 后置加锁视频
			while (FileUtil.isBackLockLess()) {
				int oldestLockVideoId = backVideoDb.getOldestLockVideoId();
				String oldestVideoName = backVideoDb
						.getVideNameById(oldestLockVideoId);
				File file = new File(Constant.Path.VIDEO_BACK_SD_LOCK
						+ File.separator + oldestVideoName);

				MyLog.d("StorageUtil.Delete Old lock Back Video:"
						+ file.getPath());

				int i = 0;
				while (file.exists() && file.isFile() && !file.delete()
						&& i < 3) {
					i++;
					MyLog.d("StorageUtil.Delete Old lock Back Video:"
							+ file.getName() + " Filed!!! Try:" + i);
				}
				backVideoDb.deleteDriveVideoById(oldestLockVideoId); // 删除数据库记录
			}

			return true;
		} catch (Exception e) {
			/*
			 * 异常原因：1.文件由用户手动删除
			 */
			MyLog.e("StorageUtil.deleteOldestUnlockVideo:Catch Exception:"
					+ e.toString());
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 删除后录文件，同时删除对应时段前录视频
	 * 
	 * @param context
	 * @param backVideoName
	 */
	private static void deleteFrontByBack(Context context, String backVideoName) {
		try {
			if (null != backVideoName && backVideoName.trim().length() > 19
					&& !backVideoName.startsWith(".")) {
				String videoPrefix = backVideoName.substring(0, 15);
				MyLog.v("[deleteFrontByBack]videoPrefix:" + videoPrefix);
				File dirBack = new File(Constant.Path.VIDEO_FRONT_SD);
				File[] childFiles = dirBack.listFiles();
				for (File childFile : childFiles) {
					if (childFile.getName().startsWith(videoPrefix)
							&& childFile.exists()) {
						childFile.delete();
						MyLog.w("[deleteFrontByBack]Delte Front:"
								+ childFile.getPath());
						for (File childFileOld : childFiles) { // 比删除视频的日期旧的视频也删除
							// 2016-08-22_203957_0.mp4
							// 0123456789A12345678
							String childFileName = childFileOld.getName();
							if (null != childFileName
									&& childFileName.trim().length() > 19
									&& !childFileName.startsWith(".")) {
								int childYear = Integer.parseInt(childFileName
										.substring(0, 4));
								int thisYear = Integer.parseInt(backVideoName
										.substring(0, 4));
								if (childYear < thisYear) {
									if (childFileOld.exists()) {
										MyLog.w("[deleteFrontByBack]Delte Front OLD:"
												+ childFileOld.getPath());
										childFileOld.delete();
									}
								} else if (childYear == thisYear) {
									int childMonth = Integer
											.parseInt(childFileName.substring(
													5, 7));
									int thisMonth = Integer
											.parseInt(backVideoName.substring(
													5, 7));
									if (childMonth < thisMonth) {
										if (childFileOld.exists()) {
											MyLog.w("[deleteFrontByBack]Delte Front OLD:"
													+ childFileOld.getPath());
											childFileOld.delete();
										}
									} else if (childMonth == thisMonth) {
										int childDay = Integer
												.parseInt(childFileName
														.substring(8, 10));
										int thisDay = Integer
												.parseInt(backVideoName
														.substring(8, 10));

										if (childDay < thisDay) {
											if (childFileOld.exists()) {
												MyLog.w("[deleteFrontByBack]Delte Front OLD:"
														+ childFileOld
																.getPath());
												childFileOld.delete();
											}
										} else if (childDay == thisDay) {
											int childHour = Integer
													.parseInt(childFileName
															.substring(11, 13));
											int thisHour = Integer
													.parseInt(backVideoName
															.substring(11, 13));
											if (childHour < thisHour) {
												if (childFileOld.exists()) {
													MyLog.w("[deleteFrontByBack]Delte Front OLD:"
															+ childFileOld
																	.getPath());
													childFileOld.delete();
												}
											} else if (childHour == thisHour) {
												int childMinute = Integer
														.parseInt(childFileName
																.substring(13,
																		15));
												int thisMinute = Integer
														.parseInt(backVideoName
																.substring(13,
																		15));
												if (childMinute < thisMinute) {
													if (childFileOld.exists()) {
														MyLog.w("[deleteFrontByBack]Delte Front OLD:"
																+ childFileOld
																		.getPath());
														childFileOld.delete();
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			MyLog.e("deleteFrontByBack catch:" + e.toString());
		}
	}

	/**
	 * 将数据库中不存在的视频文件导入数据库
	 * 
	 * @param file
	 */
	public static void RecursionCheckFile(Context context, File file) {

		if (file.exists()) {
			try {
				String fileName = file.getName();
				if (file.isFile()) {
					if (fileName.endsWith(".mp4")) {
						if (fileName.startsWith(".")) {
							// Delete file start with dot but not the recording
							// one

							if (!MyApp.isFrontRecording
									&& fileName.endsWith("_0.mp4")) {
								file.delete();
								MyLog.v("StorageUtil.RecursionCheckFile-Delete DOT File:"
										+ fileName);
							}
							if (!MyApp.isBackRecording
									&& fileName.endsWith("_1.mp4")) {
								file.delete();
								MyLog.v("StorageUtil.RecursionCheckFile-Delete DOT File:"
										+ fileName);
							}
						} else {
							if (fileName.endsWith("_0.mp4")) { // 前录
								FrontVideoDbHelper frontVideoDb = new FrontVideoDbHelper(
										context); // 视频数据库
								boolean isVideoExist = frontVideoDb
										.isVideoExist(fileName);
								if (!isVideoExist) {
									boolean isLock = file.getPath().contains(
											"Lock");
									DriveVideo driveVideo = new DriveVideo(
											fileName, isLock ? 1 : 0, 555, 0);
									frontVideoDb.addDriveVideo(driveVideo);
									MyLog.v("StorageUtil.RecursionCheckFile-InsertFrontVideoToDB:"
											+ file.getAbsolutePath());
								}
							} else if (fileName.endsWith("_1.mp4")) { // 后录
								BackVideoDbHelper backVideoDb = new BackVideoDbHelper(
										context);
								boolean isVideoExist = backVideoDb
										.isVideoExist(fileName);
								if (!isVideoExist) {
									boolean isLock = file.getPath().contains(
											"Lock");
									DriveVideo driveVideo = new DriveVideo(
											fileName, isLock ? 1 : 0, 555, 1);
									backVideoDb.addDriveVideo(driveVideo);
									MyLog.v("StorageUtil.RecursionCheckFile-InsertBackVideoToDB:"
											+ file.getAbsolutePath());
								}
							}
						}
						return;
					} else if (!fileName.endsWith(".jpg")
							&& !fileName.endsWith(".tmp")) {
						file.delete();
					}
				} else if (file.isDirectory()) {
					File[] childFile = file.listFiles();
					if (childFile == null || childFile.length == 0) {
						return;
					}
					for (File f : childFile) {
						RecursionCheckFile(context, f);
					}
				}
			} catch (Exception e) {
				MyLog.e("StorageUtil.RecursionCheckFile-Catch Exception:"
						+ e.toString());
			}

		}
	}

	private static void speakVoice(Context context, String content) {
		context.sendBroadcast(new Intent(Constant.Broadcast.TTS_SPEAK)
				.putExtra("content", content));
	}

}
