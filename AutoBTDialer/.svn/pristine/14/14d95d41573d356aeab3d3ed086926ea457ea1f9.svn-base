package com.xctx.iplay.autobtdialer.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xctx.iplay.autobtdialer.IBtAidlInterface;
import com.xctx.iplay.autobtdialer.jni.NativeBtLib;

public class BtService extends Service {

    NativeBtLib mNativeBtLib = new NativeBtLib();
    private int fd;

    public BtService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mBinder;
    }

    @Override
    public void onCreate() {

        //打开串口
        fd = mNativeBtLib.openUart();

        Log.i("BtService", "fd = " + fd);

        if (fd > 0) {
            new UartReadThread(fd).start();
        }
    }


    @Override
    public void onDestroy() {
        //关闭串口
        mNativeBtLib.closeUart(fd);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {

        return true;
    }

    @Override
    public void unbindService(ServiceConnection conn) {

    }

    private final IBtAidlInterface.Stub mBinder = new IBtAidlInterface.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public boolean sendAtCmd(String cmd) throws RemoteException {

            int ret = -1;

            if(fd > 0)
                ret = mNativeBtLib.writeUart(fd, cmd.getBytes(), cmd.length());

            return ret == 0 ? true : false;
        }

        @Override
        public String readBigData() throws RemoteException {
            return null;
        }
    };

    class UartReadThread extends Thread {

        private int fd;

        UartReadThread(int fd) {
            this.fd = fd;
        }

        @Override
        public void run() {

            byte[] buf = null;

            while (true) {
                //读取串口
                buf = mNativeBtLib.readUart(fd, 0);
                if (buf == null)
                    continue;

                String bufS = new String(buf, 0, buf.length);
                //解析AT指令,结果发送给UI处理
//                Logger.i(bufS);bufS
//                if(!StringUtil.checkNull(bufS)) {
//                    bufS = bufS.trim();
//                    sendUIMsg(DataUtils.fragmentUI,bufS);
//                }
            }
        }
    }

    public void sendUIMsg(String order, String data) {
        // 发送广播
        Intent intent = new Intent();
        intent.putExtra("order", order);
        intent.putExtra("data", data);
        intent.setAction("");
        sendBroadcast(intent);
    }
}
