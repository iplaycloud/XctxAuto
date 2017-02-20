package com.tchip.autorecord.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;

import com.tchip.autorecord.Constant;
import com.tchip.autorecord.MyApp;
import com.tchip.autorecord.util.Flash2SDUtil;
import com.tchip.autorecord.util.MyLog;

public class MoveImageThread extends Thread {

	Context context;
	String imageName;

	public MoveImageThread(Context context, String imageName) {
		this.context = context;
		this.imageName = imageName;
	}

	@Override
	public void run() {
		super.run();
		if (Constant.Record.flashToCard) {
			try {
				String toPath = Constant.Path.IMAGE_SD + imageName;
				File oldFile = new File(Constant.Path.IMAGE_FLASH + imageName);
				File newFile = new File(toPath);
				if (!newFile.getParentFile().exists()) {
					newFile.getParentFile().mkdirs();
				}
				if (oldFile.exists() && oldFile.isFile()) {
					FileInputStream input = new FileInputStream(oldFile);
					FileOutputStream output = new FileOutputStream(newFile);
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				oldFile.delete(); // 删除内部存储文件
				new Thread(new WriteImageExifThread(toPath)).start(); // 写入EXIF信息

				if (MyApp.shouldSendPathToDSA) { // 停车守卫拍照
					MyApp.shouldSendPathToDSA = false;
					String[] picPaths = new String[2]; // 第一张保存前置的图片路径
					picPaths[0] = toPath;
					picPaths[1] = "";
					Intent intent = new Intent(Constant.Broadcast.SEND_PIC_PATH);
					intent.putExtra("picture", picPaths);
					context.sendBroadcast(intent);
					MyLog.v("SendDSA,Path:" + toPath);
				}

				if (MyApp.shouldSendPathToDSAUpload) { // 语音拍照上传
					MyApp.shouldSendPathToDSAUpload = false;
					Intent intentDsaUpload = new Intent(
							Constant.Broadcast.SEND_DSA_UPLOAD_PATH);
					intentDsaUpload.putExtra("share_picture", toPath);
					context.sendBroadcast(intentDsaUpload);
					MyLog.v("SendDSAUpload,Path:" + toPath);
				}
				// 通知语音
				Intent intentImageSave = new Intent(
						Constant.Broadcast.ACTION_IMAGE_SAVE);
				intentImageSave.putExtra("path", toPath);
				context.sendBroadcast(intentImageSave);

			} catch (Exception e) {
			}
			Flash2SDUtil.moveOldImageToSD();
		}

	}
}