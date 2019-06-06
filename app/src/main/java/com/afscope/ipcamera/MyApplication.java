package com.afscope.ipcamera;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.LruCache;

import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.utils.Log;
import com.afscope.ipcamera.utils.Toast;
import com.tencent.bugly.Bugly;

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
        INSTANCE = this;
        Toast.init();
//        initDateBase();
        Bugly.init(getApplicationContext(), "5715953de1", true);
        Log.init(true);
    }

    //    private void initDateBase() {
//        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"sssDb",null);
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//        DaoMaster dm=new DaoMaster(writableDatabase);
//        mDaoSession = dm.newSession();
//    }
//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }
    public static final MyApplication getInstance() {
        return INSTANCE;
    }

    public ParametersBean getParametersBean() {
        return parametersBean;
    }

    public void setParametersBean(ParametersBean bean) {
        parametersBean = bean;
    }
}
