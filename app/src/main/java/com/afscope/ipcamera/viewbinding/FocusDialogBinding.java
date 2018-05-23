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
public class FocusDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WhiteBalanceBinding";

    @BindView(R.id.tv_position_value)
    TextView tv_position_value;
    @BindView(R.id.tv_horizontal_value)
    TextView tv_horizontal_value;
    @BindView(R.id.tv_vertical_value)
    TextView tv_vertical_value;

    @BindView(R.id.sb_position)
    SeekBar sb_position;
    @BindView(R.id.sb_horizontal)
    SeekBar sb_horizontal;
    @BindView(R.id.sb_vertical)
    SeekBar sb_vertical;

    private FocusAreaChangedListener areaChangedListener;

    public FocusDialogBinding(@NonNull ParametersBean bean, @NonNull FocusAreaChangedListener listener) {
        super(bean);
        areaChangedListener = listener;
    }

    @Override
    protected void onBound() {
        sb_position.setOnSeekBarChangeListener(this);
        sb_horizontal.setOnSeekBarChangeListener(this);
        sb_vertical.setOnSeekBarChangeListener(this);
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
            bean.setWhiteBalanceRed(sb_position.getProgress());
            bean.setWhiteBalanceGreen(sb_horizontal.getProgress());
            bean.setWhiteBalanceBlue(sb_vertical.getProgress());
            return CmdAndParamsCodec.getWhiteBalanceParamsCmd(bean);
        }
        return null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_position:
                tv_position_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_horizontal:
                tv_horizontal_value.setText(Integer.toString(progress));
                break;
            case R.id.sb_vertical:
                tv_vertical_value.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStopTrackingTouch: ");
        areaChangedListener.onFocusAreaChanged(40, 23);
    }

    public interface FocusAreaChangedListener {
        void onFocusAreaChanged(int width, int height);
    }
}
