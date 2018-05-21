package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

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

    public WhiteBalanceDialogBinding(@NonNull ParametersBean bean) {
        super(bean);
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
    protected void setDefaultParamsToView(ParametersBean bean) {

    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){

        } else {
            bean.setWhiteBalanceMode(ParametersBean.WHITE_BALANCE_MODE_MANUAL);
            bean.setWhiteBalanceRed(sb_red.getProgress());
            bean.setWhiteBalanceGreen(sb_green.getProgress());
            bean.setWhiteBalanceBlue(sb_blue.getProgress());
            return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
        }
        return null;
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
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
