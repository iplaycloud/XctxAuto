package com.tchip.gaodenavi;

import java.util.ArrayList;

import com.iflytek.cloud.SpeechUtility;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {

	private ArrayList<Activity> activities = new ArrayList<Activity>();
	private static MyApplication instance;

	private MyApplication() {
	}

	// 单例模式中获取唯一的MyApplication实例
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public void deleteActivity(Activity activity) {
		activities.remove(activity);
	}

	// finish
	public void exit() {
		for (Activity activity : activities) {
			activity.finish();
		}
		activities.clear();

	}
}
