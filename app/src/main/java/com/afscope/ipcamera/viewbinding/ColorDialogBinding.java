package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;
import com.afscope.ipcamera.wscontroller.WsController;

import butterknife.BindInt;
import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class ColorDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "ColorDialogBinding";

//    @BindView(R.id.tv_hue_value)
//    TextView tv_hue_value;
//    @BindView(R.id.tv_saturation_value)
//    TextView tv_saturation_value;
//    @BindView(R.id.tv_brightness_value)
//    TextView tv_brightness_value;
    @BindView(R.id.tv_sharpness_value)
    TextView tv_sharpness_value;
    @BindView(R.id.tv_contrast_value)
    TextView tv_contrast_value;
    @BindView(R.id.tv_gamma_value)
    TextView tv_gamma_value;

//    @BindView(R.id.sb_hue)
//    SeekBar sb_hue;
//    @BindView(R.id.sb_saturation)
//    SeekBar sb_saturation;
//    @BindView(R.id.sb_brightness)
//    SeekBar sb_brightness;
    @BindView(R.id.sb_sharpness)
    SeekBar sb_sharpness;
    @BindView(R.id.sb_contrast)
    SeekBar sb_contrast;
    @BindView(R.id.sb_gamma)
    SeekBar sb_gamma;

    @BindInt(R.integer.color_sharpness_default_value)
    int color_sharpness_default_value;
    @BindInt(R.integer.color_contrast_default_value)
    int color_contrast_default_value;
    @BindInt(R.integer.color_gamma_default_value)
    int color_gamma_default_value;

    public ColorDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
        Log.i(TAG, "ColorDialogBinding: color params: " + bean.getColorParams());
    }

    @Override
    protected void onBound() {
//        sb_hue.setOnSeekBarChangeListener(this);
//        sb_saturation.setOnSeekBarChangeListener(this);
//        sb_brightness.setOnSeekBarChangeListener(this);
        sb_sharpness.setOnSeekBarChangeListener(this);
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
        sb_sharpness.setProgress(bean.getColorSharpness());
        sb_contrast.setProgress(bean.getColorContrast());
        sb_gamma.setProgress(bean.getColorGamma());
    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){
            sb_sharpness.setProgress(color_sharpness_default_value);
            sb_contrast.setProgress(color_contrast_default_value);
            sb_gamma.setProgress(color_gamma_default_value);
            bean.setColorGamma(color_gamma_default_value);
            bean.setColorContrast(color_contrast_default_value);
            bean.setColorSharpness(color_sharpness_default_value);
        }
        return CmdAndParamsCodec.getColorParamsCmd(bean);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
//            case R.id.sb_hue:
//                tv_hue_value.setText(Integer.toString(progress));
//                break;
//            case R.id.sb_saturation:
//                tv_saturation_value.setText(Integer.toString(progress));
//                break;
//            case R.id.sb_brightness:
//                tv_brightness_value.setText(Integer.toString(progress));
//                break;
            case R.id.sb_sharpness:
                tv_sharpness_value.setText(Integer.toString(progress));
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
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Log.v(TAG, "onStopTrackingTouch: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_sharpness:
                bean.setColorSharpness(progress);
                break;
            case R.id.sb_contrast:
                bean.setColorContrast(progress);
                break;
            case R.id.sb_gamma:
                bean.setColorGamma(progress);
                break;
        }
//        wsController.sendCommand(CmdAndParamsCodec.getColorParamsCmd(bean));
        sendCommand(CmdAndParamsCodec.getColorParamsCmd(bean));
    }
}
