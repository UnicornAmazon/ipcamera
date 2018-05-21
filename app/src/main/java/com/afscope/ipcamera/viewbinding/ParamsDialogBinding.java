package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/22 0022.
 *
 */
public abstract class ParamsDialogBinding {
    private static final String TAG = "ParamsDialogBinding";
    protected ParametersBean bean;

    public ParamsDialogBinding(@NonNull ParametersBean bean){
        this.bean = bean;
    }

    public void bind(@NonNull View view){
        Log.i(TAG, "bind: ");
        ButterKnife.bind(this, view);
        onBound();
    }

    @OnClick(R.id.bt_save_params)
    public void saveParams(){
        String cmd = getParamsCmdString(false);
        Log.i(TAG, "saveParams: " + getClass()
                + "\n cmd: " + cmd);

    }

    @OnClick(R.id.bt_recovery_to_default)
    public void recoveryToDefault(){
        Log.i(TAG, "recoveryToDefault: " + getClass());
        ParametersBean bean = getDefaultParams();
        setDefaultParamsToView(bean);
    }

    protected abstract void onBound();
    protected abstract ParametersBean getDefaultParams();
    protected abstract void setDefaultParamsToView(ParametersBean bean);
    protected abstract String getParamsCmdString(boolean isDefault);
}
