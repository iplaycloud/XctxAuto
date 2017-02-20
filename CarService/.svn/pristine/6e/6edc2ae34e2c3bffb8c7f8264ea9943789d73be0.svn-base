package com.xctx.carservice.jni;

/**
 * Description :
 * Created by iplay on 2016/12/21.
 * E-mail : iplaycloud@gmail.com
 */
public class NativeBtLib {

    static {
        System.loadLibrary("native-bt-lib");
    }

    public native int openUart();

    public native byte[] readUart(int fd, int count);

    public native int writeUart(int fd, byte[] buf, int count);

    public native void closeUart(int fd);
}