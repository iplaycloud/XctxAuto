package com.tchip.autosetting;

import com.tchip.autosetting.util.MyUncaughtExceptionHandler;

import android.app.Application;

public class MyApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(getApplicationContext());
	}

}
