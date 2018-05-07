package com.indomaret.klikindomaret.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.ErrorPageActivity;
import com.indomaret.klikindomaret.activity.VirtualCategoryActivity;

/**
 * Created by USER on 4/26/2017.
 */
public class UncaughtExceptionHandler {
    Intent intent;

    public void uncaughtException(final Activity activity){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                new Thread() {
                    @Override
                    public void run() {
//                        intent = new Intent(activity, ErrorPageActivity.class);
//                        activity.startActivity(intent);
//                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }.start();

//                activity.finish();
//                System.exit(2);

//                new Thread() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//                        Toast.makeText(activity,"Terjadi kesalahan pada aplikasi", Toast.LENGTH_LONG).show();
//                        Looper.loop();
//                    }
//                }.start();
//
//                try {
//                    Thread.sleep(6000); // Let the Toast display before app will get shutdown
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                activity.finish();
//                System.exit(2);
            }
        });
    }
}
