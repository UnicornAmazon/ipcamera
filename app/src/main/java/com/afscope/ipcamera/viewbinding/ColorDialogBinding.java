package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class ColorDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WhiteBalanceBinding";

    @BindView(R.id.tv_hue_value)
    TextView tv_hue_value;
    @BindView(R.id.tv_saturation_value)
    TextView tv_saturation_value;
    @BindView(R.id.tv_brightness_value)
    TextView tv_brightness_value;
    @BindView(R.id.tv_contrast_value)
    TextView tv_contrast_value;
    @BindView(R.id.tv_gamma_value)
    TextView tv_gamma_value;

    @BindView(R.id.sb_hue)
    SeekBar sb_hue;
    @BindView(R.id.sb_saturation)
    SeekBar sb_saturation;
    @BindView(R.id.sb_brightness)
    SeekBar sb_brightness;
    @BindView(R.id.sb_contrast)
    SeekBar sb_contrast;
    @BindView(R.id.sb_gamma)
    SeekBar sb_gamma;

    public ColorDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
    }

    @Override
    protected void onBound() {
        sb_hue.setOnSeekBarChangeListener(this);
        sb_saturation.setOnSeekBarChangeListener(this);
        sb_brightness.setOnSeekBarChangeListener(this);
        sb_contrast.setOnSeekBarChangeListener(this);
        sb_gamma.setOnSeekBarChangeListener(this);
    }

    @Override
    protected ParametersBean getDefaultParams() {
        return null;
    }

    @Override
    public void refreshParams(ParametersBean bean) {
        Log.i(TAG, "refreshParams: " + bean.getColorParams());
    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){
            bean.setExposureMode(ParametersBean.WHITE_BALANCE_MODE_AUTO);
            return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
        } else {
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
            bean.setWhiteBalanceRed(sb_hue.getProgress());
            bean.setWhiteBalanceGreen(sb_saturation.getProgress());
            bean.setWhiteBalanceBlue(sb_brightness.getProgress());
            return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_hue:
                tv_hue_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_saturation:
                tv_saturation_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_brightness:
                tv_brightness_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_contrast:
                tv_contrast_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_gamma:
                tv_gamma_value.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
