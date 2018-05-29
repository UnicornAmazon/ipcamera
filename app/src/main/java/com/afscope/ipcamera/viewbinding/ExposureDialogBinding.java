package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class ExposureDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "ExposureBinding";

    @BindView(R.id.switch_exposure_mode)
    Switch switch_exposure_mode;

    @BindView(R.id.tv_exposure_target_value)
    TextView tv_exposure_target_value;
//    @BindView(R.id.tv_exposure_time_value)
//    TextView tv_exposure_time_value;
    @BindView(R.id.tv_gain_value)
    TextView tv_gain_value;

    //曝光目标亮度
    @BindView(R.id.sb_exposure_target)
    SeekBar sb_exposure_target;
//    @BindView(R.id.sb_exposure_time)
//    SeekBar sb_exposure_time;
    @BindView(R.id.sb_gain)
    SeekBar sb_gain;

    @BindInt(R.integer.exposure_brightness_default_value)
    int defaultBrightness;
    @BindInt(R.integer.exposure_gain_default_value)
    int defaultGain;

    public ExposureDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
        Log.i(TAG, "ExposureDialogBinding: exposure params: " + bean.getExposureParams());
    }

    @Override
    protected void onBound() {
        sb_exposure_target.setOnSeekBarChangeListener(this);
//        sb_exposure_time.setOnSeekBarChangeListener(this);
        sb_gain.setOnSeekBarChangeListener(this);
    }

    @Override
    protected ParametersBean getDefaultParams() {
        return null;
    }

    @Override
    public void refreshParams(ParametersBean bean) {
        Log.i(TAG, "refreshParams: " + bean.getExposureParams());
        switch_exposure_mode.setChecked(bean.isAutoExposureMode());
        sb_exposure_target.setProgress(bean.getExposureBright());
        sb_gain.setProgress(bean.getExposureGain());
    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){
            switch_exposure_mode.setChecked(true);
            sb_exposure_target.setProgress(defaultBrightness);
            sb_gain.setProgress(defaultGain);
            bean.setExposureBright(defaultBrightness);
            bean.setExposureGain(defaultGain);
        }

        return CmdAndParamsCodec.getExposureParamsCmd(bean);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_exposure_target:
                tv_exposure_target_value.setText(Integer.toString(progress));
                break;
//            case R.id.sb_exposure_time:
//                tv_exposure_time_value.setText(Integer.toString(progress));
//                break;
            case R.id.sb_gain:
                tv_gain_value.setText(Integer.toString(progress));
                break;
        }
    }

    @OnCheckedChanged(R.id.switch_exposure_mode)
    void switchExposureMode(boolean isChecked){
        Log.i(TAG, "switchExposureMode: isChecked ? " + isChecked);
        if (isChecked){
            //自动曝光模式
            bean.setExposureMode(ParametersBean.EXPOSURE_MODE_AUTO);
//            bean.setExposureBright(defaultBrightness);
//            sb_exposure_target.setProgress(defaultBrightness);
        } else {
            bean.setExposureMode(ParametersBean.EXPOSURE_MODE_MANUAL);
//            bean.setExposureGain(defaultGain);
//            sb_gain.setProgress(defaultGain);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
