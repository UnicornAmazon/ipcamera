package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class WhiteBalanceDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WhiteBalanceBinding";

    @BindView(R.id.tv_red_value)
    TextView tv_red_value;
    @BindView(R.id.tv_green_value)
    TextView tv_green_value;
    @BindView(R.id.tv_blue_value)
    TextView tv_blue_value;
    @BindView(R.id.switch_white_mode)
    Switch switch_white_mode;
    @BindView(R.id.sb_red)
    SeekBar sb_red;
    @BindView(R.id.sb_green)
    SeekBar sb_green;
    @BindView(R.id.sb_blue)
    SeekBar sb_blue;

    @BindInt(R.integer.white_balance_rgb_default_value)
    int rgbDefaultValue;

    public WhiteBalanceDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
        Log.i(TAG, "WhiteBalanceDialogBinding: white balance params: " + bean.getWhiteBalanceParams());
    }

    @Override
    protected void onBound() {
        sb_red.setOnSeekBarChangeListener(this);
        sb_green.setOnSeekBarChangeListener(this);
        sb_blue.setOnSeekBarChangeListener(this);
    }

    @Override
    protected ParametersBean getDefaultParams() {
        return null;
    }

    @Override
    public void refreshParams(ParametersBean bean) {
        Log.i(TAG, "refreshParams: " + bean.getWhiteBalanceParams());
        switch_white_mode.setChecked(bean.isWhiteBalanceAutoMode());
        if (bean.isWhiteBalanceAutoMode()) {
            sb_red.setEnabled(false);
            sb_green.setEnabled(false);
            sb_blue.setEnabled(false);
        }
        sb_red.setProgress(bean.getWhiteBalanceRed());
        sb_green.setProgress(bean.getWhiteBalanceGreen());
        sb_blue.setProgress(bean.getWhiteBalanceBlue());
    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault) {
            switch_white_mode.setChecked(true);
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_AUTO);
            sb_red.setEnabled(false);
            sb_green.setEnabled(false);
            sb_blue.setEnabled(false);
            bean.setWhiteBalanceRed(rgbDefaultValue);
            bean.setWhiteBalanceGreen(rgbDefaultValue);
            bean.setWhiteBalanceBlue(rgbDefaultValue);
            sb_red.setProgress(rgbDefaultValue);
            sb_green.setProgress(rgbDefaultValue);
            sb_blue.setProgress(rgbDefaultValue);
        }
        return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
    }

    @OnCheckedChanged(R.id.switch_white_mode)
    void switchExposureMode(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "switchExposureMode: isChecked ? " + isChecked);
        if (!buttonView.isPressed()) {
            return;
        }
        if (isChecked) {
            //自动白平衡
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_AUTO);
            sb_red.setEnabled(false);
            sb_green.setEnabled(false);
            sb_blue.setEnabled(false);
            sendCommand(CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean));
        } else {
            sb_red.setEnabled(true);
            sb_green.setEnabled(true);
            sb_blue.setEnabled(true);
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
            sendDifCommand(CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean));
        }
    }

    //有返回值的发送命令
    private void sendDifCommand(String cmd) {
        if (wsController.isLoggedIn()) {
            Log.i(TAG, "sendCommand: has logged in");
            wsController.sendCommand(cmd, new Callback<Callback.Result>() {
                @Override
                public void onResult(Callback.Result result) {
                    if (result.result) {
                        ParametersBean bean = CmdAndParamsCodec.decode2Bean(result.msg);
                        sb_blue.setProgress(bean.getWhiteBalanceBlue());
                        sb_green.setProgress(bean.getWhiteBalanceGreen());
                        sb_red.setProgress(bean.getWhiteBalanceRed());
                        Toast.toast("设置成功！");
                    } else {
                        Log.e(TAG, "sendCommand, onResult failed, msg: " + result.msg);
                    }
                }
            });
        } else {
            Log.e(TAG, "sendCommand: error websocket status " + wsController.getStatusStr());
            Toast.toast("未连接到摄像头或未登录，无法进行设置！");
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()) {
            case R.id.sb_red:
                tv_red_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_green:
                tv_green_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_blue:
                tv_blue_value.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Log.i(TAG, "onStopTrackingTouch: progress = " + progress);
        bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL_WITH_PARAMETER);
        switch (seekBar.getId()) {
            case R.id.sb_red:
                bean.setWhiteBalanceRed(progress);
                break;
            case R.id.sb_green:
                bean.setWhiteBalanceGreen(progress);
                break;
            case R.id.sb_blue:
                bean.setWhiteBalanceBlue(progress);
                break;
        }
//        wsController.sendCommand(CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean));
        sendCommand(CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean));
    }
}
