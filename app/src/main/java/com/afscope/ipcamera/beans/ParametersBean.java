package com.afscope.ipcamera.beans;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/11 0011.
 *
 * 相机参数
 *
 * 1、白平衡
 * 2、曝光与增益(包含：光源频率)
 * 3、颜色调整
 * 4、聚焦模式与区域
 */
public class ParametersBean {
    private static final String TAG = "ParametersBean";

    //1、白平衡
    public static final int WHITE_BALANCE_MODE_MANUAL = 2;
    private int whiteBalanceMode;
    private int whiteBalanceRed;
    private int whiteBalanceGreen;
    private int whiteBalanceBlue;

    //2、曝光与增益
    public static final int EXPOSURE_MODE_MANUAL = 2;
    private int exposureMode;    //曝光模式
    private int exposureBright;   //曝光亮度
    private int exposureGain;    //曝光增益

    //3、颜色调整
    private int colorHue;    //色调
    private int colorSaturation;    //饱和度
    private int colorBrightness;    //亮度
    private int colorContrast;    //对比度
    private int colorGamma;    //伽玛

    //4、聚焦模式与区域
    public static final int FOCUS_MODE_MANUAL = 2;
    private int focusMode;
    private int focusPos;
    //区域
    private int focusAreaSize;
    private int focusHorizontal;
    private int focusVertical;

    public int getWhiteBalanceMode(){
        return whiteBalanceMode;
    }

    public int getWhiteBalanceRed(){
        return whiteBalanceRed;
    }

    public int getWhiteBalanceGreen(){
        return whiteBalanceGreen;
    }

    public int getWhiteBalanceBlue(){
        return whiteBalanceBlue;
    }

    public void setWhiteBalanceMode(int whiteBalanceMode) {
        this.whiteBalanceMode = whiteBalanceMode;
    }

    public void setWhiteBalanceRed(int whiteBalanceRed) {
        this.whiteBalanceRed = whiteBalanceRed;
    }

    public void setWhiteBalanceGreen(int whiteBalanceGreen) {
        this.whiteBalanceGreen = whiteBalanceGreen;
    }

    public void setWhiteBalanceBlue(int whiteBalanceBlue) {
        this.whiteBalanceBlue = whiteBalanceBlue;
    }

    public int getExposureMode() {
        return exposureMode;
    }

    public void setExposureMode(int exposureMode) {
        this.exposureMode = exposureMode;
    }

    public int getExposureBright() {
        return exposureBright;
    }

    public void setExposureBright(int exposureBright) {
        this.exposureBright = exposureBright;
    }

    public int getExposureGain() {
        return exposureGain;
    }

    public void setExposureGain(int exposureGain) {
        this.exposureGain = exposureGain;
    }

    public int getColorHue() {
        return colorHue;
    }

    public void setColorHue(int colorHue) {
        this.colorHue = colorHue;
    }

    public int getColorSaturation() {
        return colorSaturation;
    }

    public void setColorSaturation(int colorSaturation) {
        this.colorSaturation = colorSaturation;
    }

    public int getColorBrightness() {
        return colorBrightness;
    }

    public void setColorBrightness(int colorBrightness) {
        this.colorBrightness = colorBrightness;
    }

    public int getColorContrast() {
        return colorContrast;
    }

    public void setColorContrast(int colorContrast) {
        this.colorContrast = colorContrast;
    }

    public int getColorGamma() {
        return colorGamma;
    }

    public void setColorGamma(int colorGamma) {
        this.colorGamma = colorGamma;
    }

    public int getFocusMode() {
        return focusMode;
    }

    public void setFocusMode(int focusMode) {
        this.focusMode = focusMode;
    }

    public int getFocusPos() {
        return focusPos;
    }

    public void setFocusPos(int focusPos) {
        this.focusPos = focusPos;
    }

    public int getFocusAreaSize() {
        return focusAreaSize;
    }

    public void setFocusAreaSize(int focusAreaSize) {
        this.focusAreaSize = focusAreaSize;
    }

    public int getFocusHorizontal() {
        return focusHorizontal;
    }

    public void setFocusHorizontal(int focusHorizontal) {
        this.focusHorizontal = focusHorizontal;
    }

    public int getFocusVertical() {
        return focusVertical;
    }

    public void setFocusVertical(int focusVertical) {
        this.focusVertical = focusVertical;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("whiteBalanceMode: ").append(whiteBalanceMode)
                .append("\n whiteBalanceRed: ").append(whiteBalanceRed)
                .append("\n whiteBalanceGreen: ").append(whiteBalanceGreen)
                .append("\n whiteBalanceBlue: ").append(whiteBalanceBlue)
                .append("\n exposureMode: ").append(exposureMode)
                .append("\n exposureBright: ").append(exposureBright)
                .append("\n exposureGain: ").append(exposureGain)
                .append("\n focusMode: ").append(focusMode)
                .append("\n focusPos: ").append(focusPos)
                .append("\n focusAreaSize: ").append(focusAreaSize)
                .append("\n focusHorizontal: ").append(focusHorizontal)
                .append("\n focusVertical: ").append(focusVertical);

        return builder.toString();
    }

    private static final HashMap<String, Field> keyFieldMap = new HashMap<>();
    static {
        try {
            Field whiteBalanceMode = ParametersBean.class.getDeclaredField("whiteBalanceMode");
            Field whiteBalanceRed = ParametersBean.class.getDeclaredField("whiteBalanceRed");
            Field whiteBalanceGreen = ParametersBean.class.getDeclaredField("whiteBalanceGreen");
            Field whiteBalanceBlue = ParametersBean.class.getDeclaredField("whiteBalanceBlue");

            Field exposureMode = ParametersBean.class.getDeclaredField("exposureMode");
            Field exposureBright = ParametersBean.class.getDeclaredField("exposureBright");
            Field exposureGain = ParametersBean.class.getDeclaredField("exposureGain");

            Field focusMode = ParametersBean.class.getDeclaredField("focusMode");
            Field focusPos = ParametersBean.class.getDeclaredField("focusPos");
            Field focusAreaSize = ParametersBean.class.getDeclaredField("focusAreaSize");
            Field focusHorizontal = ParametersBean.class.getDeclaredField("focusHorizontal");
            Field focusVertical = ParametersBean.class.getDeclaredField("focusVertical");


            keyFieldMap.put("", whiteBalanceMode);
            keyFieldMap.put("red", whiteBalanceRed);
            keyFieldMap.put("green", whiteBalanceGreen);
            keyFieldMap.put("blue", whiteBalanceBlue);

            keyFieldMap.put("bright", exposureBright);
            keyFieldMap.put("zengyi", exposureGain);

            keyFieldMap.put("lever", focusHorizontal);
            keyFieldMap.put("verti", focusVertical);

        } catch (NoSuchFieldException e){
            Log.e(TAG, "static initializer: error: " + e);
        }
    }


    public void setKeyValuePairs(Map<String, Integer> keyValuePairs) {
        try {
            Field field;
            for (Map.Entry<String, Integer> entry : keyValuePairs.entrySet()){
                field = keyFieldMap.get(entry.getKey());
                if (field != null){
                    field.set(this, entry.getValue());
                }
            }
        } catch (IllegalAccessException e){
            Log.e(TAG, "setKeyValuePairs: error: " + e);
        }

    }
}
