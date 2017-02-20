package com.xctx.iplay.autobtdialer.jni;

/**
 * Description :
 * Created by iplay on 2016/12/23.
 * E-mail : iplaycloud@gmail.com
 */

public class BtModuleUitls {

    public static final String btModulePath = "/dev/ttyMT1";

    public static final String startConnectMode = "AT#CA\r\n";

    public static final String inquiryState = "AT#CY\r\n";

    public static final String exitConnectMode = "AT#CB\r\n";

    public static final String acceptComeInPhone = "AT#CE\r\n";

    public static final String refuseComeInPhone = "AT#CF\r\n";

    public static final String hangupPhone = "AT#CG\r\n"; //挂断电话

    public static final String redialPhone = "AT#CH\r\n";

    public static final String dialPhone = "AT#CW";  //拨打电话

    public static final String connectPhone = "MC";  //免提语音建立连接

    public static final String breakPhone = "MD";  //免提语音连接断开

    public static final String mg = "MG";  //报告HF状态

    public static final String iv = "IV"; //正在连接蓝牙免提

    public static final String ib = "IB"; //连接蓝牙成功

    public static final String ia = "IA"; //蓝牙免提断开

    public static final String II = "II"; //进入配对模式

    public static final String my = "MY"; //A2DB断开

    public static final String pb = "PB"; //联系人或通话记录

    public static final String pc = "PC"; //联系人或通话记录同步完成

    public static final String sync_contacts1 = "AT#PA1,0,100\r\n"; //同步联系人phone

    public static final String sync_contacts0 = "AT#PA0,0,100\r\n"; //同步联系人sim
}






















