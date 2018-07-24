package com.afscope.ipcamera.viewbinding;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.ParametersBean;
import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import static com.afscope.ipcamera.beans.ParametersBean.FOCUS_FRAME_CHECK;
import static com.afscope.ipcamera.beans.ParametersBean.FOCUS_FRAME_UNCHECK;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class FocusDialogBinding extends ParamsDialogBinding implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "FocusDialogBinding";

    @BindView(R.id.tv_position_value)
    TextView tv_position_value;
    @BindView(R.id.tv_horizontal_value)
    TextView tv_horizontal_value;
    @BindView(R.id.tv_vertical_value)
    TextView tv_vertical_value;

    @BindView(R.id.spinner_area_size)
    Spinner spinner_area_size;

    @BindView(R.id.sb_position)
    SeekBar sb_position;   //聚焦位置参数的取值范围为：-200~200 ，手动聚焦模式下，默认指为0
    @BindView(R.id.sb_horizontal)
    SeekBar sb_horizontal;
    @BindView(R.id.sb_vertical)
    SeekBar sb_vertical;
    @BindView(R.id.bt_focus_mode_auto)
    Button bt_focus_mode_auto;
    @BindView(R.id.bt_focus_mode_quick)
    Button bt_focus_mode_quick;
    @BindView(R.id.bt_focus_mode_manual)
    Button bt_focus_mode_manual;
    @BindView(R.id.cb_Focus_frame)
    CheckBox cb_Focus_frame;

    @BindInt(R.integer.focus_vertical_default_value_at_large_size)
    int focus_vertical_default_value_at_large_size;
    @BindInt(R.integer.focus_horizontal_default_value_at_large_size)
    int focus_horizontal_default_value_at_large_size;
    @BindInt(R.integer.focus_vertical_max_value_at_small_size)
    int focus_vertical_max_value_at_small_size;
    @BindInt(R.integer.focus_horizontal_max_value_at_small_size)
    int focus_horizontal_max_value_at_small_size;

    @BindInt(R.integer.focus_vertical_default_value_at_small_size)
    int focus_vertical_default_value_at_small_size;
    @BindInt(R.integer.focus_horizontal_default_value_at_small_size)
    int focus_horizontal_default_value_at_small_size;
    @BindInt(R.integer.focus_vertical_max_value_at_large_size)
    int focus_vertical_max_value_at_large_size;
    @BindInt(R.integer.focus_horizontal_max_value_at_large_size)
    int focus_horizontal_max_value_at_large_size;

    private final static int FOCUS_POS_PROGRESS_ALIGNMENT = 200;

    private FocusAreaChangedListener areaChangedListener;

    public FocusDialogBinding(@NonNull ParametersBean bean, @NonNull FocusAreaChangedListener listener) {
        super(bean);
        areaChangedListener = listener;
        Log.i(TAG, "FocusDialogBinding: focus params: " + bean.getFocusParams());
    }

    @Override
    protected void onBound() {
        sb_position.setOnSeekBarChangeListener(this);
        sb_horizontal.setOnSeekBarChangeListener(this);
        sb_vertical.setOnSeekBarChangeListener(this);
//        spinner_area_size.setOnItemSelectedListener(this);
        cb_Focus_frame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    bean.setFocusFrame(b?FOCUS_FRAME_CHECK:FOCUS_FRAME_UNCHECK);

            }
        });
    }

    @Override
    protected ParametersBean getDefaultParams() {
        return null;
    }

    @Override
    public void refreshParams(ParametersBean bean) {
        Log.i(TAG, "refreshParams: " + bean.getFocusParams());
        if (bean.isManualFocusMode()){
            bt_focus_mode_manual.setSelected(true);
            sb_position.setEnabled(true);
            sb_position.setProgress(bean.getFocusPos() + FOCUS_POS_PROGRESS_ALIGNMENT);
        } else if (bean.isAutoFocusMode()){
            bt_focus_mode_auto.setSelected(true);
            sb_position.setEnabled(false);
        }else {
            bt_focus_mode_quick.setSelected(true);
            sb_position.setEnabled(false);

        }

        cb_Focus_frame.setChecked(bean.getFocusFrame()==FOCUS_FRAME_CHECK?true:false);

    }

    @Override
    protected String getParamsCmdString(boolean isDefault) {
        if (isDefault){
            //UI 更新为默认值
            if (spinner_area_size.getSelectedItemPosition() == 0){
                selectFocusAreaSize(0);
            } else {
                spinner_area_size.setSelection(0);
                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_SMALL);
                bean.setFocusHorizontal(focus_horizontal_default_value_at_small_size);
                bean.setFocusVertical(focus_vertical_default_value_at_small_size);
            }
        }
        Log.i(TAG, "getParamsCmdString: isDefault ? " + isDefault
                +", focus params: " + bean.getFocusParams());
        return CmdAndParamsCodec.getFocusAreaParamsCmd(bean);
    }

    @OnClick(R.id.bt_focus_mode_auto)
    void selectModeAuto(){
        Log.i(TAG, "selectModeAuto: ");
        sb_position.setEnabled(false);
        bt_focus_mode_auto.setSelected(true);
        bt_focus_mode_quick.setSelected(false);
        bt_focus_mode_manual.setSelected(false);
        bean.setFocusMode(ParametersBean.FOCUS_MODE_AUTO);
        //发送指令
        applyFocusModeParams();
    }

    @OnClick(R.id.bt_focus_mode_quick)
    void selectModeQuick(){
        Log.i(TAG, "selectModeQuick: ");
        sb_position.setEnabled(false);
        bt_focus_mode_quick.setSelected(true);
        bt_focus_mode_auto.setSelected(false);
        bt_focus_mode_manual.setSelected(false);
        bean.setFocusMode(ParametersBean.FOCUS_MODE_QUICK);
        //发送指令
        applyFocusModeParams();
    }

    @OnClick(R.id.bt_focus_mode_manual)
    void selectModeManual(){
        Log.i(TAG, "selectModeManual: ");
        sb_position.setEnabled(true);
        bt_focus_mode_manual.setSelected(true);
        bt_focus_mode_auto.setSelected(false);
        bt_focus_mode_quick.setSelected(false);
        bean.setFocusMode(ParametersBean.FOCUS_MODE_MANUAL);
        //发送指令
        posSendCommand();
    }

    private void posSendCommand() {
        if (wsController.isLoggedIn()) {
            wsController.sendCommand(CmdAndParamsCodec.CMD_PARAMETER_FOCUS_MANUAL_MODE, new Callback<Callback.Result>() {
                @Override
                public void onResult(Callback.Result result) {
                    if (result.result) {
                        String[] posArr = result.msg.split("&");
                        String[] pos = posArr[0].split("=");
                        sb_position.setProgress(Integer.parseInt(pos[1])+FOCUS_POS_PROGRESS_ALIGNMENT);
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

    private void applyFocusModeParams(){
        String cmd = CmdAndParamsCodec.getFocusModeParamsCmd(bean);
        Log.i(TAG, "applyFocusModeParams, cmd: "+cmd);
        wsController.sendCommand(cmd);
    }

    private void applyFocusAreaParams(){
        String cmd = CmdAndParamsCodec.getFocusAreaParamsCmd(bean);
        Log.i(TAG, "applyFocusAreaParams, cmd: "+cmd);
        wsController.sendCommand(cmd);
    }

    @OnItemSelected(R.id.spinner_area_size)
    void selectFocusAreaSize(int index){
        Log.i(TAG, "selectFocusAreaSize: index: " + index);
        switch (index){
            case 0:
                sb_horizontal.setMax(focus_horizontal_max_value_at_small_size);
                sb_vertical.setMax(focus_vertical_max_value_at_small_size);
                sb_horizontal.setProgress(focus_horizontal_default_value_at_small_size);
                sb_vertical.setProgress(focus_vertical_default_value_at_small_size);

                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_SMALL);
                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_SMALL);
                bean.setFocusHorizontal(focus_horizontal_default_value_at_small_size);
                bean.setFocusVertical(focus_vertical_default_value_at_small_size);
                break;
            case 1:
                sb_horizontal.setMax(focus_horizontal_max_value_at_large_size);
                sb_vertical.setMax(focus_vertical_max_value_at_large_size);
                sb_horizontal.setProgress(focus_horizontal_default_value_at_large_size);
                sb_vertical.setProgress(focus_vertical_default_value_at_large_size);

                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_LARGE);
                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_LARGE);
                bean.setFocusHorizontal(focus_horizontal_default_value_at_large_size);
                bean.setFocusVertical(focus_vertical_default_value_at_large_size);
                break;
        }

//        applyFocusAreaParams();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.v(TAG, "onProgressChanged: progress = " + progress);
        switch (seekBar.getId()){
            case R.id.sb_position:
                tv_position_value.setText(Integer.toString(progress - FOCUS_POS_PROGRESS_ALIGNMENT));
                bean.setFocusPos(progress - FOCUS_POS_PROGRESS_ALIGNMENT);
                break;
            case R.id.sb_horizontal:
                tv_horizontal_value.setText(Integer.toString(progress));
                bean.setFocusHorizontal(progress);
                break;
            case R.id.sb_vertical:
                tv_vertical_value.setText(Integer.toString(progress));
                bean.setFocusVertical(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.v(TAG, "onStartTrackingTouch: progress = " );
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.v(TAG, "onStopTrackingTouch: progress = ");
        switch (seekBar.getId()){
            case R.id.sb_position:
                applyFocusModeParams();
                break;
        }
//        Log.i(TAG, "onStopTrackingTouch: ");
//        areaChangedListener.onFocusAreaChanged(40, 23);
//        if (seekBar.getId() != R.id.sb_position){
//            applyFocusAreaParams();
//        }
    }

//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
//        switch (index){
//            case 0:
//                sb_horizontal.setMax(focus_horizontal_max_value_at_small_size);
//                sb_vertical.setMax(focus_vertical_max_value_at_small_size);
//                sb_horizontal.setProgress(focus_horizontal_default_value_at_small_size);
//                sb_vertical.setProgress(focus_vertical_default_value_at_small_size);
//
//                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_SMALL);
//                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_SMALL);
//                bean.setFocusHorizontal(focus_horizontal_default_value_at_small_size);
//                bean.setFocusVertical(focus_vertical_default_value_at_small_size);
//                break;
//            case 1:
//                sb_horizontal.setMax(focus_horizontal_max_value_at_large_size);
//                sb_vertical.setMax(focus_vertical_max_value_at_large_size);
//                sb_horizontal.setProgress(focus_horizontal_default_value_at_large_size);
//                sb_vertical.setProgress(focus_vertical_default_value_at_large_size);
//
//                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_LARGE);
//                bean.setFocusAreaSize(ParametersBean.FOCUS_AREA_SIZE_LARGE);
//                bean.setFocusHorizontal(focus_horizontal_default_value_at_large_size);
//                bean.setFocusVertical(focus_vertical_default_value_at_large_size);
//                break;
//        }
//
////        applyFocusAreaParams();
//
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

    public interface FocusAreaChangedListener {
        void onFocusAreaChanged(int width, int height);
    }
}
