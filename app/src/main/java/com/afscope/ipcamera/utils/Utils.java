package com.afscope.ipcamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.afscope.ipcamera.common.Constants.COMPANY_NAME;
import static com.afscope.ipcamera.common.Constants.LOG_FILES_DIR_NAME;
import static com.afscope.ipcamera.common.Constants.MEDIA_FILES_DIR_NAME;

/**
 * Created by Administrator on 2018/5/19 0019.
 *
 *
 */
public class Utils {


	public static boolean isVersionLessThanM(){
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
	}

	public static boolean isPermissionGranted(Context context, String permission){
		if (isVersionLessThanM()){return  true;}

		return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

    public static boolean isLandscape(Activity activity) {
        return activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
    }

    public static boolean isWifiConnected(@NonNull Context context){
		if (connectivityManager == null){
			connectivityManager =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isConnected();
	}

    private static ConnectivityManager connectivityManager;
    public static boolean isNetworkConnected(@NonNull Context context){
		if (connectivityManager == null){
			connectivityManager =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

        return false;
    }

    public static final File getAppExternalFilesDir(){
		return new File(Environment.getExternalStorageDirectory(), COMPANY_NAME);
	}

    public static final File getMediaFilesDir(){
		return new File(getAppExternalFilesDir(), MEDIA_FILES_DIR_NAME);
	}

	public static final File getLogFilesDir(){
		return new File(getAppExternalFilesDir(), LOG_FILES_DIR_NAME);
	}

	//删除文件或目录
	public static boolean deleteFile(File file){
		if (!file.exists()) {
			return true;
		}

		boolean result = true;

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (!deleteFile(f)) {
					result = false;
				}
			}
		} else {
			result = file.delete();
		}

		return result;
	}

	//将字节数组转换成十六进制字符串形式
	public final static String bytesToHexStr(byte[] bytes){
		StringBuilder builder = new StringBuilder();
		String str = null;
		for (byte b:bytes){
			str = String.format("%02X ", b);
			builder.append(str);
		}
		return builder.toString();
	}
	public static <T> List<T> getObjectList(String jsonString, Class<T> cls){
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
			for (JsonElement jsonElement : arry) {
				list.add(gson.fromJson(jsonElement, cls));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
