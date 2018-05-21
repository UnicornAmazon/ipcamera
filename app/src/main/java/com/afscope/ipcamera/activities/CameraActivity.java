package com.afscope.ipcamera.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.fragments.PlayFragment;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.utils.Utils;
import com.afscope.ipcamera.viewbinding.WhiteBalanceDialogBinding;
import com.afscope.ipcamera.views.ParamsBarLayout;
import com.afscope.ipcamera.views.ParamsDialog;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;
import com.afscope.ipcamera.wscontroller.WsController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 1、检查wifi 连接
 * 2、检查权限：网络权限、SDCard 读写权限
 * 3、查找并连接IP 摄像头
 * 4、读取摄像头参数
 */
public class CameraActivity extends BaseActivity implements PlayFragment.OnStateChangedListener,
        WsController.Listener {
    private static final String TAG = "CameraActivity";

    private static final int REQUEST_CODE_REQUEST_STORAGE_PERMISSION = 335;

    @BindView(R.id.render_holder)
    FrameLayout render_holder;
    @BindView(R.id.ll_header)
    ParamsBarLayout ll_header;
    @BindView(R.id.switch_capture_or_record)
    Switch switch_capture_or_record;

    private PlayFragment playFragment;
    private WsController wsController;

    private ParamsDialog whiteBalanceParamsDialog;
    private ParamsDialog exposureParamsDialog;
    private ParamsDialog colorParamsDialog;
    private ParamsDialog focusParamsDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        render_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "render_holder, onClick: ");
                ll_header.showOrHide();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Log.i(TAG, "initData: ");
        if (savedInstanceState == null){
            playFragment = new PlayFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.render_holder, playFragment).commit();
        } else {
            playFragment = (PlayFragment) getSupportFragmentManager().findFragmentById(R.id.render_holder);
        }
        playFragment.setOnStateChangedListener(this);

        if (wsController == null){
            wsController = WsController.getInstance();
            wsController.setListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查wifi 连接
        if (!Utils.isWifiConnected(this)){
            Log.e(TAG, "onResume: wifi is not connected");
            Toast.toast("请检查Wifi 是否已连接！");
            return;
        }

        //检查WebSocket 连接状态
        if (wsController.getStatus() != WsController.Status.STATUS_CONNECTED
                && wsController.getStatus() != WsController.Status.STATUS_CONNECTING){
            wsController.connect("ws://echo.websocket.org");
        }

        //for test
        playFragment.setUrl("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
        playFragment.startPlaying();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: newConfig: " + newConfig);
        if (colorParamsDialog != null){
            colorParamsDialog.onConfigurationChanged(newConfig);
        }
        if (exposureParamsDialog != null){
            exposureParamsDialog.onConfigurationChanged(newConfig);
        }
        if (colorParamsDialog != null){
            colorParamsDialog.onConfigurationChanged(newConfig);
        }
        if (focusParamsDialog != null){
            focusParamsDialog.onConfigurationChanged(newConfig);
        }
    }

    @OnClick(R.id.iv_capture_or_record)
    void takePhotoOrRecord(){
        if (!Utils.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.e(TAG, "takePhotoOrRecord: no storage permission");
            requestStoragePermission();
            return;
        }
        if (switch_capture_or_record.isChecked()){   //录像模式
            Log.i(TAG, "takePhotoOrRecord: record mode");
            startOrStopRecord();
        } else {   //拍照模式
            Log.i(TAG, "takePhotoOrRecord: takePhoto mode");
            takePhoto();
        }
    }

    @OnCheckedChanged(R.id.switch_capture_or_record)
    void switchMode(boolean isChecked){
        Log.i(TAG, "switchMode: isChecked ? " + isChecked);

    }

    private void takePhoto(){
        Log.i(TAG, "takePhoto: ");
        File mediaFilesDir = Utils.getMediaFilesDir();
        if (!mediaFilesDir.exists() && !mediaFilesDir.mkdirs()){
            Log.e(TAG, "takePhoto:  media files dir not exists, and cannot be created");
            return;
        }
        if (wsController.getStatus() == WsController.Status.STATUS_CONNECTED){
            wsController.sendMessage(CmdAndParamsCodec.getRequestParamsCmd());
        } else {
            Log.e(TAG, "takePhoto: ");
            Toast.toast("未连接到相机！");
        }
        //for test
        playFragment.takePicture(new File(
                mediaFilesDir,
                new SimpleDateFormat("HH_mm_ss").format(new Date())+".jpg").getAbsolutePath());
    }

    private void startOrStopRecord(){
        Log.i(TAG, "startOrStopRecord: ");
        try {
            playFragment.startOrStopRecord();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "startOrStopRecord: error: " + e);
        }
    }

    @OnClick(R.id.iv_explore)
    void explore(){
        Log.i(TAG, "explore: ");
        if (!Utils.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.e(TAG, "explore: no storage permission");
            requestStoragePermission();
            return;
        }

        //跳转
        Intent intent = new Intent(this, ExplorerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_white_balance)
    void showWhiteBalanceSettings(View view){
        Log.i(TAG, "showWhiteBalanceSettings: ");
        if (whiteBalanceParamsDialog == null){
            whiteBalanceParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_while_balance_dialog,
                    new WhiteBalanceDialogBinding(new ParametersBean()));
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
        }
        whiteBalanceParamsDialog.show();
    }

    @OnClick(R.id.iv_exposure_and_gain)
    void showExposureSettings(View view){
        Log.i(TAG, "showExposureSettings: ");
        if (exposureParamsDialog == null){
            exposureParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_while_balance_dialog,
                    new WhiteBalanceDialogBinding(new ParametersBean()));
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
        }
        exposureParamsDialog.show();
    }

    @OnClick(R.id.iv_color_adjust)
    void showColorSettings(View view){
        Log.i(TAG, "showColorSettings: ");
        if (colorParamsDialog == null){
            colorParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_while_balance_dialog,
                    new WhiteBalanceDialogBinding(new ParametersBean()));
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
        }
        colorParamsDialog.show();
    }

    @OnClick(R.id.iv_focus)
    void showFocusSettings(View view){
        Log.i(TAG, "showFocusSettings: ");
        if (focusParamsDialog == null){
            focusParamsDialog = new ParamsDialog(this,
                    view,
                    R.layout.layout_while_balance_dialog,
                    new WhiteBalanceDialogBinding(new ParametersBean()));
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
        }
        focusParamsDialog.show();
    }

    private void requestStoragePermission(){
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

    private void showRationaleDialog(){
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
        if (requestCode == REQUEST_CODE_REQUEST_STORAGE_PERMISSION){
            for (int i = 0; i < permissions.length; i++){
                Log.d(TAG, "onRequestPermissionsResult: permission["+i+"]:"+permissions[i]+
                        ", result:"+grantResults[i]);
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])){
                    if (PackageManager.PERMISSION_GRANTED == grantResults[i]){
                        //用户同意授权

                    } else {
                        //不同意授权
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)){
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
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (now - lastBackKeyDownAtTime > 2 * 1000){
                Toast.toast("再按一次返回键确认退出！");
                lastBackKeyDownAtTime = now;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRenderStateChanged(int status) {
        Log.i(TAG, "onRenderStateChanged: status = " + status);

    }

    @Override
    public void onRecordStateChanged(int status) {
        Log.i(TAG, "onRecordStateChanged: status = " + status);

    }

    @Override
    public void onStatusChanged(WsController.Status status) {
        Log.i(TAG, "onStatusChanged: status: "+ WsController.Status.toString(status));

    }

    @Override
    public void onMessage(String msg) {
        Log.i(TAG, "onMessage: msg: "+msg);

    }
}
