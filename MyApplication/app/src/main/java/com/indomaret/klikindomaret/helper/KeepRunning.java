package com.indomaret.klikindomaret.helper;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.app.Service;

/**
 * Created by USER on 6/14/2017.
 */
public class KeepRunning extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
