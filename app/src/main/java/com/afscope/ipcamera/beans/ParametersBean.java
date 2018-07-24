package com.afscope.ipcamera.beans;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/11 0011.
 * <p>
 * 相机参数
 * <p>
 * 1、白平衡
 * 2、曝光与增益(包含：光源频率)
 * 3、颜色调整
 * 4、聚焦模式与区域
 */
public class ParametersBean {
    private static final String TAG = "ParametersBean";

    //1、白平衡
    // 自动白平衡命令格式：C&c&1&E
    //    自动切手动命令格式: C&c&2&E
    //    返回值：int rgb[3];
    //手动白平衡命令格式：C&c&3&red=x&green=x&blue=x&E
    public static final int WHITE_BALANCE_MODE_AUTO = 1;
    public static final int WHITE_BALANCE_MODE_MANUAL = 2;

    public static final int WHITE_BALANCE_MODE_MANUAL_WITH_PARAMETER = 3;


    private int whiteBalanceMode = WHITE_BALANCE_MODE_AUTO;
    private int whiteBalanceRed;
    private int whiteBalanceGreen;
    private int whiteBalanceBlue;

    //2、曝光与增益
    public static final int EXPOSURE_MODE_AUTO = 1;
    public static final int EXPOSURE_MODE_MANUAL = 2;
    private int exposureMode = EXPOSURE_MODE_AUTO;    //曝光模式
    private int exposureBright;   //曝光亮度
    private int exposureGain;    //曝光增益

    //3、颜色调整
    private int colorHue;    //色调
    private int colorSaturation;    //饱和度
    private int colorBrightness;    //亮度
    private int colorSharpness;     //锐度
    private int colorContrast;    //对比度
    private int colorGamma;    //伽玛

    //4、聚焦模式与区域
    public static final int FOCUS_MODE_AUTO = 1;     //自动聚焦
    public static final int FOCUS_MODE_QUICK = 2;     //一键聚焦(todo：该配置项只是一次性的，不需要保存？)
    public static final int FOCUS_MODE_MANUAL = 3;   //手动聚焦
    private int focusMode = FOCUS_MODE_AUTO;
    private int focusPos;
    //区域
    public static final int FOCUS_AREA_SIZE_SMALL = 0;
    public static final int FOCUS_AREA_SIZE_LARGE = 1;
    //聚焦框
    public static final int FOCUS_FRAME_CHECK = 1;
    public static final int FOCUS_FRAME_UNCHECK = 0;
    private int focusAreaSize = FOCUS_AREA_SIZE_SMALL;       //1——小；2——中；3——大
    private int focusHorizontal;
    private int focusVertical;
    private int focusFrame = FOCUS_FRAME_CHECK;
    //相机设备是否有sd卡
    public static final int UN_HAVA_SDCARD = 0;
    private int sdCard = UN_HAVA_SDCARD;

    public int getSdCard() {
        return sdCard;
    }

    public void setSdCard(int sdCard) {
        this.sdCard = sdCard;
    }

    public boolean isHavaSdCard() {
        return getSdCard() != UN_HAVA_SDCARD;
    }

    public int getWhiteBalanceMode() {
        return whiteBalanceMode;
    }

    public boolean isWhiteBalanceAutoMode() {
        return whiteBalanceMode == WHITE_BALANCE_MODE_AUTO;
    }

    public int getWhiteBalanceRed() {
        return whiteBalanceRed;
    }

    public int getWhiteBalanceGreen() {
        return whiteBalanceGreen;
    }

    public int getWhiteBalanceBlue() {
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

    public boolean isAutoExposureMode() {
        return EXPOSURE_MODE_AUTO == exposureMode;
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

    public int getColorSharpness() {
        return colorSharpness;
    }

    public void setColorSharpness(int colorSharpness) {
        this.colorSharpness = colorSharpness;
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

    public boolean isAutoFocusMode() {
        return FOCUS_MODE_AUTO == focusMode;
    }

    public boolean isManualFocusMode() {
        return FOCUS_MODE_MANUAL == focusMode;
    }

    public boolean isQuickFocusMode() {
        return FOCUS_MODE_QUICK == focusMode;
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

    public int getFocusFrame() {
        return focusFrame;
    }

    public void setFocusFrame(int focusFrame) {
        this.focusFrame = focusFrame;
    }

    public String getWhiteBalanceParams() {
        StringBuilder builder = new StringBuilder()
                .append("whiteBalanceMode: ").append(whiteBalanceMode)
                .append("\n whiteBalanceRed: ").append(whiteBalanceRed)
                .append("\n whiteBalanceGreen: ").append(whiteBalanceGreen)
                .append("\n whiteBalanceBlue: ").append(whiteBalanceBlue);
        return builder.toString();
    }

    public String getExposureParams() {
        StringBuilder builder = new StringBuilder()
                .append("exposureMode: ").append(exposureMode)
                .append("\n exposureBright: ").append(exposureBright)
                .append("\n exposureGain: ").append(exposureGain);
        return builder.toString();
    }

    public String getColorParams() {
        StringBuilder builder = new StringBuilder()
                .append("colorHue: ").append(colorHue)
                .append("\n colorSaturation: ").append(colorSaturation)
                .append("\n colorBrightness: ").append(colorBrightness)
                .append("\n colorContrast: ").append(colorContrast)
                .append("\n colorGamma: ").append(colorGamma);
        return builder.toString();
    }

    public String getFocusParams() {
        StringBuilder builder = new StringBuilder()
                .append("focusMode: ").append(focusMode)
                .append("\n focusPos: ").append(focusPos)
                .append("\n focusAreaSize: ").append(focusAreaSize)
                .append("\n focusHorizontal: ").append(focusHorizontal)
                .append("\n focusVertical: ").append(focusVertical)
                .append("\n focusFrame: ").append(focusFrame);
        return builder.toString();
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
                .append("\n colorHue: ").append(colorHue)
                .append("\n colorSaturation: ").append(colorSaturation)
                .append("\n colorBrightness: ").append(colorBrightness)
                .append("\n colorContrast: ").append(colorContrast)
                .append("\n colorGamma: ").append(colorGamma)
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
            Field focusFrame = ParametersBean.class.getDeclaredField("focusFrame");

            Field colorContrast = ParametersBean.class.getDeclaredField("colorContrast");
            Field colorSharpness = ParametersBean.class.getDeclaredField("colorSharpness");
            Field colorGamma = ParametersBean.class.getDeclaredField("colorGamma");

            Field sdCard = ParametersBean.class.getDeclaredField("sdCard");

//白平衡
            keyFieldMap.put("sdqh", whiteBalanceMode);
            keyFieldMap.put("red", whiteBalanceRed);
            keyFieldMap.put("green", whiteBalanceGreen);
            keyFieldMap.put("blue", whiteBalanceBlue);
//曝光
            keyFieldMap.put("bright", exposureBright);
            keyFieldMap.put("zengyi", exposureGain);
            keyFieldMap.put("bhdqh", exposureMode);
//聚焦模式
            keyFieldMap.put("lever", focusHorizontal);
            keyFieldMap.put("verti", focusVertical);
            keyFieldMap.put("facus", focusFrame);
            keyFieldMap.put("pos", focusPos);
            keyFieldMap.put("msqh", focusMode);
            //图像调整
            keyFieldMap.put("contrast", colorContrast);
            keyFieldMap.put("sharpness", colorSharpness);
            keyFieldMap.put("gamma", colorGamma);
            //SD
            keyFieldMap.put("sd", sdCard);

        } catch (NoSuchFieldException e) {
            Log.e(TAG, "static initializer: error: " + e);
        }
    }


    public void setKeyValuePairs(Map<String, Integer> keyValuePairs) {
        try {
            Field field;
            for (Map.Entry<String, Integer> entry : keyValuePairs.entrySet()) {
                field = keyFieldMap.get(entry.getKey());
                if (field != null) {
                    field.set(this, entry.getValue());
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "setKeyValuePairs: error: " + e);
        }

    }
}
