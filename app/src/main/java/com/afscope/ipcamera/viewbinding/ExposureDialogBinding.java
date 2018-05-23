package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class ExposureDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WhiteBalanceBinding";

    @BindView(R.id.tv_exposure_target_value)
    TextView tv_exposure_target_value;
    @BindView(R.id.tv_exposure_time_value)
    TextView tv_exposure_time_value;
    @BindView(R.id.tv_gain_value)
    TextView tv_gain_value;

    @BindView(R.id.sb_exposure_target)
    SeekBar sb_exposure_target;
    @BindView(R.id.sb_exposure_time)
    SeekBar sb_exposure_time;
    @BindView(R.id.sb_gain)
    SeekBar sb_gain;

    public ExposureDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
    }

    @Override
    protected void onBound() {
        sb_exposure_target.setOnSeekBarChangeListener(this);
        sb_exposure_time.setOnSeekBarChangeListener(this);
        sb_gain.setOnSeekBarChangeListener(this);
    }

    @Override
    protected ParametersBean getDefaultParams() {
        return null;
    }

    @Override
    protected void setDefaultParamsToView(ParametersBean bean) {

    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){

        } else {
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
            bean.setWhiteBalanceRed(sb_exposure_target.getProgress());
            bean.setWhiteBalanceGreen(sb_exposure_time.getProgress());
            bean.setWhiteBalanceBlue(sb_gain.getProgress());
            return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
        }
        return null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_exposure_target:
                tv_exposure_target_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_exposure_time:
                tv_exposure_time_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_gain:
                tv_gain_value.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
