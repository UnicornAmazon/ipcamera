package com.afscope.ipcamera.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.afscope.ipcamera.MyApplication;

import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2017/11/8 0008.
 */
public class Toast {
    public static Context mContext;
    public static Handler mHandler;

    public static void init(){
        mContext = MyApplication.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static void toast(final String content){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.widget.Toast.makeText(mContext, content, android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public static void toastLong(final String content){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.widget.Toast.makeText(mContext, content, android.widget.Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
