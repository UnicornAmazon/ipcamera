package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindInt;
import butterknife.BindView;

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
        if (bean.isWhiteBalanceAutoMode()){
            sb_red.setProgress(0);
            sb_green.setProgress(0);
            sb_blue.setProgress(0);
        } else {
            sb_red.setProgress(bean.getWhiteBalanceRed());
            sb_green.setProgress(bean.getWhiteBalanceGreen());
            sb_blue.setProgress(bean.getWhiteBalanceBlue());
        }
    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
            bean.setWhiteBalanceRed(rgbDefaultValue);
            bean.setWhiteBalanceGreen(rgbDefaultValue);
            bean.setWhiteBalanceBlue(rgbDefaultValue);
            sb_red.setProgress(rgbDefaultValue);
            sb_green.setProgress(rgbDefaultValue);
            sb_blue.setProgress(rgbDefaultValue);
        }
        return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
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
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Log.i(TAG, "onStopTrackingTouch: progress = " + progress);
        bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
        switch (seekBar.getId()){
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
