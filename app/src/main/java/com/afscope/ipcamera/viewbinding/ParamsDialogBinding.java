package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;
import com.afscope.ipcamera.wscontroller.WsController;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Administrator on 2018/5/22 0022.
 *
 */
public abstract class ParamsDialogBinding {
    private static final String TAG = "ParamsDialogBinding";
    private ViewGroup containerView;
    protected ParametersBean bean;
    protected WsController wsController;

    public ParamsDialogBinding(@NonNull ParametersBean bean){
        this.bean = bean;
        wsController = WsController.getInstance();
    }

    public void bind(@NonNull View view){
        Log.i(TAG, "bind: ");
        ButterKnife.bind(this, view);
        containerView = (ViewGroup) view;
        onBound();
        refreshParams(bean);
        if (!wsController.isLoggedIn()){
            //for test 暂时注释掉
            setEnabled(false);
        }
    }

    @Optional
    @OnClick(R.id.bt_save_params)
    public void saveParams(){
        String cmd = getParamsCmdString(false);
        if (TextUtils.isEmpty(cmd)){
            Log.e(TAG, "saveParams: get null cmd for class: " + getClass());
            Toast.toast("错误：无法获取相关的参数设置指令");
            return;
        }

        Log.i(TAG, "saveParams: " + getClass() + "\n cmd: " + cmd);
        sendCommand(cmd);
    }

    @OnClick(R.id.bt_recovery_to_default)
    public void recoveryToDefault(){
        String cmd = getParamsCmdString(true);
        if (TextUtils.isEmpty(cmd)){
            Log.e(TAG, "recoveryToDefault: get null cmd for class: " + getClass());
            Toast.toast("错误：无法获取相关的默认参数设置指令");
            return;
        }

        Log.i(TAG, "recoveryToDefault: " + getClass() + "\n cmd: " + cmd);
        sendCommand(cmd);
    }

    protected void sendCommand(String cmd){
        if (wsController.isLoggedIn()){
            Log.i(TAG, "sendCommand: has logged in");
            wsController.sendCommand(cmd, new Callback<Callback.Result>() {
                @Override
                public void onResult(Result result) {
                    if (result.result){
                        Toast.toast("设置成功！");
                    } else {
                        Log.e(TAG, "sendCommand, onResult failed, msg: " + result.msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "sendCommand: error websocket status "+ wsController.getStatusStr());
            Toast.toast("未连接到摄像头或未登录，无法进行设置！");
        }
    }

    public void setEnabled(boolean enable){
        Log.i(TAG, "setEnabled: " + enable);
        enableAllChildViews(containerView, enable);
    }

    private void enableAllChildViews(ViewGroup father, boolean enable){
        int count = father.getChildCount();
        View view;
        for (int i = 0; i < count; i++){
            view = father.getChildAt(i);
            Log.v(TAG, "enableAllChildViews: view: "+view);
            if (view instanceof ViewGroup){
                enableAllChildViews((ViewGroup) view, enable);
            } else {
//                view.setFocusable(false);
                view.setEnabled(enable);
            }
        }
    }

    protected abstract void onBound();
    protected abstract ParametersBean getDefaultParams();
    public abstract void refreshParams(ParametersBean bean);
    protected abstract String getParamsCmdString(boolean isDefault);
}
