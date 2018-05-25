package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.wscontroller.WsController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/22 0022.
 *
 */
public abstract class ParamsDialogBinding {
    private static final String TAG = "ParamsDialogBinding";
//    private ViewGroup containerView;
    protected ParametersBean bean;
    private WsController wsController;

    public ParamsDialogBinding(@NonNull ParametersBean bean){
        this.bean = bean;
        wsController = WsController.getInstance();
    }

    public void bind(@NonNull View view){
        Log.i(TAG, "bind: ");
        ButterKnife.bind(this, view);
//        containerView = (ViewGroup) view;
        onBound();
    }

    @OnClick(R.id.bt_save_params)
    public void saveParams(){
        String cmd = getParamsCmdString(false);
        Log.i(TAG, "saveParams: " + getClass()
                + "\n cmd: " + cmd);
        if (wsController.isLoggedIn()){
            wsController.sendCommand(cmd, new Callback<Callback.Result>() {
                @Override
                public void onResult(Result result) {
                    if (result.result){
                        Toast.toast("设置成功！");
                    } else {
                        Log.e(TAG, "saveParams, onResult failed, msg: " + result.msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "saveParams: error websocket status "+ wsController.getStatusStr());
            Toast.toast("未连接到摄像头或未登录，无法进行设置！");
        }
    }

    @OnClick(R.id.bt_recovery_to_default)
    public void recoveryToDefault(){
        Log.i(TAG, "recoveryToDefault: ");
//        ParametersBean bean = getDefaultParams();
//        refreshParams(bean);
        String cmd = getParamsCmdString(true);
        if (wsController.isLoggedIn()){
            wsController.sendCommand(cmd, new Callback<Callback.Result>() {
                @Override
                public void onResult(Result result) {
                    if (result.result){
                        Toast.toast("设置成功！");
                    } else {
                        Log.e(TAG, "saveParams, onResult failed, msg: " + result.msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "saveParams: error websocket status "+ wsController.getStatusStr());
            Toast.toast("未连接到摄像头或未登录，无法进行设置！");
        }
    }

    protected abstract void onBound();
    protected abstract ParametersBean getDefaultParams();
    public abstract void refreshParams(ParametersBean bean);
    protected abstract String getParamsCmdString(boolean isDefault);
}
