package com.tchip.autoplayer.util;

import android.util.Log;

import com.tchip.autoplayer.air.Constant;


public class MyLog {
    private static String MODULE = "";

    public static void e(String log) {
        if (Constant.isDebug)
            Log.e(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
    }

    public static void v(String log) {
        if (Constant.isDebug)
            Log.v(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
    }

    public static void d(String log) {
        if (Constant.isDebug)
            Log.d(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
    }

    public static void i(String log) {
        if (Constant.isDebug)
            Log.i(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
    }

    public static void w(String log) {
        if (Constant.isDebug)
            Log.w(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
    }

}
