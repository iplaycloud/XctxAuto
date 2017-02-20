package com.tchip.autorecord.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MoveThread extends Thread {
	String oldFilePath;
	String newFilePath;

	public MoveThread(String oldFilePath, String newFilePath) {
		this.oldFilePath = oldFilePath;
		this.newFilePath = newFilePath;
	}

	@Override
	public void run() {
		super.run();
		try {
			File oldFile = new File(oldFilePath);
			File newFile = new File(newFilePath);
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
		} catch (Exception e) {
		}
	}
}