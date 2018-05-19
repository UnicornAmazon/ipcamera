package com.afscope.ipcamera;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.afscope.ipcamera.utils.Toast;

import java.io.File;

/**
 * Created by Administrator on 2018/5/19 0019.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication INSTANCE;

//    public static SQLiteDatabase sDB;
//    public static String sPicturePath;
//    public static String sMoviePath;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Toast.init();

//        sPicturePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/ip_camera";
//        sMoviePath = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/ip_camera";
//        File file = new File(sPicturePath);
//        if (!file.exists() && !file.mkdirs()){
//            Log.e(TAG, "initData: failed to mk sPicturePath dirs");
//        }
//        file = new File(sMoviePath);
//        if (!file.exists() && !file.mkdirs()){
//            Log.e(TAG, "initData: failed to mk sMoviePath dirs");
//        }
    }

    public static final MyApplication getInstance(){
        return INSTANCE;
    }
}
