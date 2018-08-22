package com.afscope.ipcamera.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.afscope.ipcamera.MyApplication;
import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.fragments.PlayFragment;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.utils.Utils;
import com.afscope.ipcamera.viewbinding.ColorDialogBinding;
import com.afscope.ipcamera.viewbinding.ExposureDialogBinding;
import com.afscope.ipcamera.viewbinding.FocusDialogBinding;
import com.afscope.ipcamera.viewbinding.WhiteBalanceDialogBinding;
import com.afscope.ipcamera.views.ParamsBarLayout;
import com.afscope.ipcamera.views.ParamsDialog;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;
import com.afscope.ipcamera.wscontroller.WsController;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

import static com.afscope.ipcamera.fragments.PlayFragment.RESULT_REND_VIDEO_DISPLAYED;

/**
 * 1、检查wifi 连接
 * 2、检查权限：网络权限、SDCard 读写权限
 * 3、查找并连接IP 摄像头
 * 4、读取摄像头参数
 */
public class CameraActivity extends BaseActivity implements PlayFragment.OnStateChangedListener,
        WsController.Listener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CameraActivity";

    private static final int REQUEST_CODE_REQUEST_STORAGE_PERMISSION = 335;

    @BindView(R.id.render_holder)
    FrameLayout render_holder;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.ll_header)
    ParamsBarLayout ll_header;
    @BindView(R.id.switch_capture_or_record)
    Switch switch_capture_or_record;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private PlayFragment playFragment;
    private WsController wsController;

    private HashMap<String, ParamsDialog> paramsDialogMap = new HashMap<>();
//    private ParamsDialog whiteBalanceParamsDialog;
//    private ParamsDialog exposureParamsDialog;
//    private ParamsDialog colorParamsDialog;
//    private ParamsDialog focusParamsDialog;

    @BindView(R.id.iv_explore)
    ImageView iv_explore;
    @BindView(R.id.iv_capture_or_record)
    ImageView iv_capture_or_record;
    @BindView(R.id.chronometer_record_timer)
    Chronometer chronometer_record_timer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        switch_capture_or_record.setOnCheckedChangeListener(this);
        render_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "render_holder, onClick: ");
                ll_header.showOrHide();
            }
        });
        initImage();
    }

    private void initImage() {
        File[] files = Utils.getMediaFilesDir().listFiles();
        if (files != null && files.length > 0)
            Glide.with(this).load(files[files.length - 1]).into(iv_explore);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Log.i(TAG, "initData: ");
        if (savedInstanceState == null) {
            playFragment = new PlayFragment();
            getFragmentManager().beginTransaction().add(R.id.render_holder, playFragment).commit();
        } else {
            playFragment = (PlayFragment) getFragmentManager().findFragmentById(R.id.render_holder);
        }
        playFragment.setOnStateChangedListener(this);
        //for test
//        playFragment.setUrl("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
        playFragment.setUrl("rtsp://192.168.7.1:8553/PSIA/Streaming/channels/0?videoCodecType=H.264");

        if (wsController == null) {
            wsController = WsController.getInstance();
            wsController.setListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查wifi 连接
        if (!Utils.isWifiConnected(this)) {
            Log.e(TAG, "onResume: wifi is not connected");
            Toast.toast("请检查Wifi 是否已连接！");
            return;
        }

        //检查WebSocket 连接状态
        if (!wsController.isConnected()) {
//            wsController.connect("ws://echo.websocket.org");
            wsController.connect("ws://192.168.7.1:1234");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        wsController.release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: newConfig: " + newConfig);
        for (Map.Entry<String, ParamsDialog> entry : paramsDialogMap.entrySet()) {
            entry.getValue().onConfigurationChanged(newConfig);
        }
    }

    @OnClick(R.id.iv_capture_or_record)
    void takePhotoOrRecord() {
        WindowManager windowManager = getWindowManager();
        if (!Utils.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e(TAG, "takePhotoOrRecord: no storage permission");
            requestStoragePermission();
            return;
        }
        if (switch_capture_or_record.isChecked()) {   //录像模式
            Log.i(TAG, "takePhotoOrRecord: record mode");
            startOrStopRecord();
        } else {   //拍照模式
            Log.i(TAG, "takePhotoOrRecord: takePhoto mode");
            takePhoto();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        iv_capture_or_record.setImageResource(!isChecked ? R.drawable.ic_take_photo : R.mipmap.ic_action_record);
        if (!isChecked) {
            //切换到拍照 -- 可能在视频转录过程中切换
            chronometer_record_timer.stop();
            chronometer_record_timer.setVisibility(View.GONE);
            if (playFragment.isRecording()) {
                try {
                    playFragment.startOrStopRecord();
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "switchMode: error: " + e);
                }
            }
        }
    }

    private void takePhoto() {
        Log.i(TAG, "takePhoto: ");
        File mediaFilesDir = Utils.getMediaFilesDir();
        if (!mediaFilesDir.exists() && !mediaFilesDir.mkdirs()) {
            Log.e(TAG, "takePhoto:  media files dir not exists, and cannot be created");
            return;
        }
        if (!MyApplication.getInstance().getParametersBean().isHavaSdCard()) {
            //没有sd卡
            byte[] bytes = playFragment.takePicture();
            if (bytes == null) {
                Toast.toast("未连接到相机！");
                return;
            }
            Glide.with(CameraActivity.this).load(bytes).into(iv_explore);
            saveImage(bytes);
            return;
        }
        if (wsController.isConnected()) {
            wsController.sendCommand(CmdAndParamsCodec.getRequestShootCmd(), new Callback<Callback.Result>() {
                @Override
                public void onResult(Result result) {
                    if (result.result) {
                        Log.i(TAG, "takePhoto, onResult: success");
                        //保存图像
                        final byte[] bytes = CmdAndParamsCodec.decodeBase64StrToBytes(result.msg);
                        if (bytes != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(CameraActivity.this).load(bytes).into(iv_explore);
                                }
                            });
                            saveImage(bytes);
                        } else {
                            Log.e(TAG, "takePhoto, onResult: decode bitmap failed");
                            Toast.toast("解析图片失败！");
                        }
                    } else {
                        Log.e(TAG, "takePhoto, onResult: " + result);
                        Toast.toast("拍照出现异常：" + result.msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "takePhoto: error websocket status: " + wsController.getStatusStr());
            Toast.toast("未连接到相机！");
        }
    }

    public void saveImage(byte[] bmp) {
        File appDir = new File(new File(Environment.getExternalStorageDirectory(), "afscope"), "media");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = dateFormat.format(new Date()) + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getPath());
            Bitmap bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        playFragment.saveBitmapInFile(file.getPath());
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(bmp);
////            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void startOrStopRecord() {
        if (!Utils.isWifiConnected(this)) {
            Log.e(TAG, "startOrStopRecord: wifi is not connected");
            Toast.toast("请检查手机Wifi 连接！");
            return;
        }
//        boolean connecting = wsController.isConnecting();
        try {
            Log.i(TAG, "startOrStopRecord: ");
            boolean recording = playFragment.startOrStopRecord();
            iv_capture_or_record.setImageResource(recording ? R.mipmap.ic_action_record_checked : R.mipmap.ic_action_record);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "startOrStopRecord: error: " + e);
        }
    }

    @OnClick(R.id.iv_explore)
    void explore() {
        Log.i(TAG, "explore: ");
        if (!Utils.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e(TAG, "explore: no storage permission");
            requestStoragePermission();
            return;
        }

        //跳转
        Intent intent = new Intent(this, ExplorerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_white_balance)
    void showWhiteBalanceSettings(View view) {
        Log.i(TAG, "showWhiteBalanceSettings: ");
        ParamsDialog whiteBalanceParamsDialog = paramsDialogMap.get("white_balance");
        if (whiteBalanceParamsDialog == null) {
            whiteBalanceParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_while_balance_dialog,
                    new WhiteBalanceDialogBinding(MyApplication.getInstance().getParametersBean()));
            whiteBalanceParamsDialog.setOuterOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ll_header.showForever();
                }
            }).setOuterOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ll_header.hideAfterAWhile();
                }
            });

            paramsDialogMap.put("white_balance", whiteBalanceParamsDialog);
        }
        whiteBalanceParamsDialog.show();
    }

    @OnClick(R.id.iv_exposure_and_gain)
    void showExposureSettings(View view) {
        Log.i(TAG, "showExposureSettings: ");
        ParamsDialog exposureParamsDialog = paramsDialogMap.get("exposure");
        if (exposureParamsDialog == null) {
            exposureParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_exposure_params_dialog,
                    new ExposureDialogBinding(MyApplication.getInstance().getParametersBean()));
            exposureParamsDialog.setOuterOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ll_header.showForever();
                }
            }).setOuterOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ll_header.hideAfterAWhile();
                }
            });

            paramsDialogMap.put("exposure", exposureParamsDialog);
        }
        exposureParamsDialog.show();
    }

    @OnClick(R.id.iv_color_adjust)
    void showColorSettings(View view) {
        Log.i(TAG, "showColorSettings: ");
        ParamsDialog colorParamsDialog = paramsDialogMap.get("color");
        if (colorParamsDialog == null) {
            colorParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_color_params_dialog,
                    new ColorDialogBinding(MyApplication.getInstance().getParametersBean()));
            colorParamsDialog.setOuterOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ll_header.showForever();
                }
            }).setOuterOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ll_header.hideAfterAWhile();
                }
            });

            paramsDialogMap.put("color", colorParamsDialog);
        }
        colorParamsDialog.show();
    }

    @OnClick(R.id.iv_focus)
    void showFocusSettings(View view) {
        Log.i(TAG, "showFocusSettings: ");
        ParamsDialog focusParamsDialog = paramsDialogMap.get("focus");
        if (focusParamsDialog == null) {
            focusParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_focus_params_dialog,
                    new FocusDialogBinding(
                            MyApplication.getInstance().getParametersBean(),
                            new FocusDialogBinding.FocusAreaChangedListener() {
                                @Override
                                public void onFocusAreaChanged(int width, int height) {
                                    Log.i(TAG, "onFocusAreaChanged: ");

                                }
                            }));
            focusParamsDialog.setOuterOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ll_header.showForever();
                }
            }).setOuterOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ll_header.hideAfterAWhile();
                }
            });

            paramsDialogMap.put("focus", focusParamsDialog);
        }
        focusParamsDialog.show();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(TAG, "takePhoto: shouldShowRequestPermissionRationale return true");
            showRationaleDialog();
        } else {
            Log.d(TAG, "takePhoto: shouldShowRequestPermissionRationale return false");
            // permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_REQUEST_STORAGE_PERMISSION);

        }
    }

    private void showRationaleDialog() {
        Log.i(TAG, "showRationaleDialog: ");
        new AlertDialog.Builder(this)
                .setMessage("应用需要读写外置存储空间，用于读取、保存图像与视频文件！")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "showRationaleDialog: positive button onClick");
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_REQUEST_STORAGE_PERMISSION);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showRationaleDialog: Negative button onClick");
                Toast.toast("错误：无读写外置存储权限！");
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode:" + requestCode);
        if (requestCode == REQUEST_CODE_REQUEST_STORAGE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "onRequestPermissionsResult: permission[" + i + "]:" + permissions[i] +
                        ", result:" + grantResults[i]);
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                    if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        //用户同意授权

                    } else {
                        //不同意授权
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)) {
                            Log.d(TAG, "onRequestPermissionsResult:" +
                                    " shouldShowRequestPermissionRationale return true");
                            showRationaleDialog();
                        } else {
                            Log.d(TAG, "onRequestPermissionsResult:" +
                                    " shouldShowRequestPermissionRationale return false");
                            Toast.toast("错误：无读写外置存储权限！");
                        }
                    }
                }
            }
        }
    }

    //连续按两次返回键确认退出
    private long lastBackKeyDownAtTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long now = SystemClock.elapsedRealtime();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (now - lastBackKeyDownAtTime > 2 * 1000) {
                Toast.toast("再按一次返回键确认退出！");
                lastBackKeyDownAtTime = now;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRenderStateChanged(int status) {
        if (status == RESULT_REND_VIDEO_DISPLAYED) {
            //视频显示出来后关闭加载画面
            progress_bar.setVisibility(View.GONE);
        }
        Log.i(TAG, "onRenderStateChanged: status = " + status);

    }

    @Override
    public void onRecordStateChanged(int state) {
        Log.i(TAG, "onRecordStateChanged: state = " + state);
        switch (state) {
            case PlayFragment.RECORD_STATE_BEGIN:
                chronometer_record_timer.setVisibility(View.VISIBLE);
                chronometer_record_timer.setBase(SystemClock.elapsedRealtime());
                chronometer_record_timer.start();
                break;
            case PlayFragment.RECORD_STATE_END:
                chronometer_record_timer.stop();
                chronometer_record_timer.setVisibility(View.GONE);
                break;
            default:
                Log.e(TAG, "onRecordStateChanged: error state: " + state);
                break;
        }
    }

    /*----------------------------------- WebSocket 模块回调 ----------------------------------*/
    @Override
    public void onStatusChanged(WsController.Status status) {
        Log.i(TAG, "onStatusChanged: status: " + wsController.getStatusStr());
        if (status == WsController.Status.STATUS_CONNECTED) {
            Toast.toast("连接摄像头成功！");
        } else if (status == WsController.Status.STATUS_FAILURE) {
            Toast.toast("连接摄像头失败！");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, ParamsDialog> entry : paramsDialogMap.entrySet()) {
                        entry.getValue().setEnabled(false);
                    }
                }
            });
        } else if (status == WsController.Status.STATUS_LOGGED_IN) {
            //登录成功，更新摄像头参数
            wsController.sendCommand(CmdAndParamsCodec.getRequestParamsCmd(),
                    new Callback<Callback.Result>() {
                        @Override
                        public void onResult(Result result) {
                            if (result.result) {
                                Log.i(TAG, "onStatusChanged, get params command after logged in, " +
                                        "params: " + result.msg);
                                final ParametersBean bean = CmdAndParamsCodec.decode2Bean(result.msg);
                                if (bean != null) {
                                    MyApplication.getInstance().setParametersBean(bean);
                                    //更新界面
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (Map.Entry<String, ParamsDialog> entry :
                                                    paramsDialogMap.entrySet()) {
                                                entry.getValue().setEnabled(true);
                                                entry.getValue().refreshParams(bean);
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "onStatusChanged, params cannot be decoded to bean");
                                    Toast.toast("解析摄像头参数失败！");
                                }
                            } else {
                                Log.e(TAG, "onStatusChanged, get params failed: " + result);
                            }
                        }
                    });
        } else if (status == WsController.Status.STATUS_LOGIN_FAILED) {
            Toast.toast("登录失败！");
        } else if (status == WsController.Status.STATUS_DISCONNECTED) {
            Toast.toast("连接已断开！");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, ParamsDialog> entry : paramsDialogMap.entrySet()) {
                        entry.getValue().setEnabled(false);
                    }
                }
            });
        }
    }

    @Override
    public void onMessage(String msg) {
        Log.i(TAG, "onMessage: msg: " + msg);

    }
}
