package com.afscope.ipcamera;

import android.app.Application;

import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.utils.Log;
import com.afscope.ipcamera.utils.Toast;

/**
 * Created by Administrator on 2018/5/19 0019.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication INSTANCE;
    private volatile ParametersBean parametersBean = new ParametersBean();


    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.i(TAG, "onCreate: ");
        INSTANCE = this;
        Toast.init();
        Log.init(true);
    }

    public static final MyApplication getInstance(){
        return INSTANCE;
    }

    public ParametersBean getParametersBean(){
        return parametersBean;
    }

    public void setParametersBean(ParametersBean bean){
        parametersBean = bean;
    }
}
