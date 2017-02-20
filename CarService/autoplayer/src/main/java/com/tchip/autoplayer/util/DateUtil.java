package com.tchip.autoplayer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getWeekStrByInt(int week) {
        if (week > 7)
            week = week % 7;
        switch (week) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "星期日";
        }
    }

    /**
     * 获取格式化系统时间
     *
     * @param patten 格式，如："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getTimeStr(String patten) {
        long nowTime = System.currentTimeMillis();
        Date date = new Date(nowTime);
        String strs = "";
        try {
            strs = new SimpleDateFormat(patten, Locale.US).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * 获取格式化时间
     *
     * @param patten
     * @param time
     * @return
     */
    public static String getTimeStr(String patten, long time) {
        Date date = new Date(time);
        String strs = "";
        try {
            strs = new SimpleDateFormat(patten, Locale.US).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    public static String getAlarmWeekString(String number) {
        if (number != null && number.trim().length() > 0) {
            return number.replace(",", " ").replace("0", "日").replace("1", "一").replace("2", "二")
                    .replace("3", "三").replace("4", "四").replace("5", "五").replace("6", "六");
        } else
            return "";
    }

    public static Date getDateByString(String strTime) {
        long time = Long.parseLong(strTime);
        return new Date(time);
    }

    /**
     * 获取日期的前、后一天
     *
     * @param nowDate  yyyy-MM-dd
     * @param isBefore
     * @return
     */
    public static String changeDate(String nowDate, boolean isBefore) {
        MyLog.v("[SettingUtil.changeDate]MyApp.nowDate:" + nowDate + ",isBefore:" + isBefore);
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(nowDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, isBefore ? day - 1 : day + 1);

        String dayChange = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                .format(c.getTime());
        return dayChange;
    }
}
