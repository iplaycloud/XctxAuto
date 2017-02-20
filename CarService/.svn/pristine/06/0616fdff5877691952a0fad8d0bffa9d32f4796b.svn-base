package com.xctx.carservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xctx.carservice.service.CarService;

public class LaunchReceiver extends BroadcastReceiver {
    public LaunchReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, CarService.class);

        // 启动指定Server
        context.startService(intent1);
    }
}
