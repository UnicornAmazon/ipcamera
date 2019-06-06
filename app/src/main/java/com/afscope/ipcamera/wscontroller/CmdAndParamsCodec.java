package com.afscope.ipcamera.wscontroller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.MonthDisplayHelper;

import com.afscope.ipcamera.beans.ParametersBean;
import com.elvishew.xlog.LogItem;


import java.security.PublicKey;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/11 0011.
 *
 * 指令的起始字符包括：A | C | D | F
 * 结束字符为：E
 *
 */
public class CmdAndParamsCodec {
    private static final String TAG = "CmdAndParamsCodec";

    //登录验证 -- 指令以A 开头
    private static final String CMD_LOGIN = "A&user=%s&pwd=%s&E";   //示例：A&user=admin&pwd=admin&E

    //参数控制 -- 指令以C 开头
    //1、聚焦模式与区域
    /**
     * 聚焦模式 -- 指令第二个字符(参数控制指令类型)为a ，后面其余字符为具体参数
     * 示例：
     * 自动聚焦命令格式：C&a&1&E
     * 一键聚焦命令格式：C&a&2&E
     * 手动聚焦命令格式：C&a&3&pos=x&E  （注：x代表聚焦位置）
     *
     * 参数说明：
     * 聚焦位置：pos
     */
    private static final String CMD_PARAMETER_FOCUS_MODE = "C&a&%s&E";
    //手动专用
    public static final String CMD_PARAMETER_FOCUS_MANUAL_MODE = "C&a&3&E";
    private static final String SUB_CMD_PARAMETER_FOCUS_MODE_MANUAL_MODE = "4&pos=%d";

    /**
     * 聚焦区域 -- 指令第二个字符为b
     * 示例：
     * C&b&area=x&lever=x&verti=x&facus=x&E
     *
     * 参数说明：
     * 1）区域大小area=x ，其中x值为：1——小；2——中；3——大
     * 2）水平值lever=x
     * 3）垂直值verti=x
     * 4）对焦框facus=x，其中x值为：1——选中；0——未选中
     */
    private static final String CMD_PARAMETER_FOCUS_AREA = "C&b&area=%d&lever=%d&verti=%d&facus=%d&E";

    //2、白平衡
    /**
     * 白平衡 -- 指令第二个字符为c
     * 示例：
     * 自动白平衡命令格式：C&c&1&E
     * 手动白平衡命令格式：C&c&2&red=x&green=x&blue=x&E
     *
     * 参数说明：
     * RGB 色值(red , green , blue)
     */
    private static final String CMD_PARAMETER_WHITE_BALANCE = "C&c&%s&E";
    private static final String SUB_CMD_PARAMETER_WHITE_BALANCE_MANUAL_MODE = "3&red=%d&green=%d&blue=%d";
    //图像调整
    private static final String CMD_PARAMETER_COLOR_MODE = "C&f&sharpness=%d&contrast=%d&gamma=%d&E";

    //3、曝光与增益(包含：光源频率)
    /**
     * 曝光与增益 -- 指令第二个字符为d
     * 示例：
     * 自动曝光命令格式：C&d&1&E
     * 手动曝光命令格式：C&d&2&bright=x&zengyi=x&E
     *
     * 参数说明：
     * 亮度：bright
     * 增益：zengyi
     */
    private static final String CMD_PARAMETER_AUTO_EXPOSURE = "C&d&1&bright=%d&E";
    private static final String CMD_PARAMETER_MANUAL_EXPOSURE = "C&d&2&zengyi=%d&E";

    //4、颜色调整


    //获取相机参数 -- 指令以D 开头
    /**
     * 示例：
     * 指令：D&e&E
     * 返回值：
     * pos=1&msqh=3&qydx=2&lever=3&verti=4&facus=5&qh=2&red=6&green=7&blue=8&qh=2&bright=9&zengyi=10&bin=1&skip=0&rate=115200&frame-rate=4800
     */
    private static final String CMD_REQUEST_PARAMETERS = "D&e&E";

    //图片传输(拍照) -- 指令以F 开头
    /**
     * 示例：
     * 指令：F&E
     * 返回值：图片的base64编码
     */
    private static final String CMD_REQUEST_SHOOT = "F&E";

    public static final String getLoginCmd(String user, String pwd){
        return String.format(CMD_LOGIN, user, pwd);
    }

    public static final boolean isLoginCmd(@NonNull String cmd){
        return /*!TextUtils.isEmpty(cmd) && */cmd.startsWith("A&");
    }

    public static final boolean isApplyParameterCmd(@NonNull String cmd){
        return /*!TextUtils.isEmpty(cmd) && */cmd.startsWith("C&");
    }

    public static final boolean isRequestParametersCmd(@NonNull String cmd){
        return /*!TextUtils.isEmpty(cmd) && */cmd.startsWith("D&");
    }

    public static final boolean isTakePhotoCmd(@NonNull String cmd){
        return /*!TextUtils.isEmpty(cmd) && */cmd.startsWith("F&");
    }

    public static final String getWhiteBalanceParamsCmd(ParametersBean bean){
        String param;
        if (ParametersBean.WHITE_BALANCE_MODE_MANUAL_WITH_PARAMETER == bean.getWhiteBalanceMode()){
            //手动调节白平衡参数
            param = String.format(SUB_CMD_PARAMETER_WHITE_BALANCE_MANUAL_MODE,
                    bean.getWhiteBalanceRed(),
                    bean.getWhiteBalanceGreen(),
                    bean.getWhiteBalanceBlue());
        } else if(ParametersBean.WHITE_BALANCE_MODE_MANUAL == bean.getWhiteBalanceMode()){
            //手动模式
            param = Integer.toString(bean.getWhiteBalanceMode());
        }else {
            //自动模式
            param = Integer.toString(bean.getWhiteBalanceMode());
        }

        return String.format(CMD_PARAMETER_WHITE_BALANCE, param);
    }

    public static final String getExposureParamsCmd(ParametersBean bean){
        String param;
        if (bean.isAutoExposureMode()){
            param = String.format(CMD_PARAMETER_AUTO_EXPOSURE,
                    bean.getExposureBright());
        } else {
            param = String.format(CMD_PARAMETER_MANUAL_EXPOSURE,
                    bean.getExposureGain());
        }
        Log.i(TAG, "getExposureParamsCmd: "+param);
        return param;
    }

    public static final String getColorParamsCmd(ParametersBean bean){
        String param;
            param = String.format(CMD_PARAMETER_COLOR_MODE,
                    bean.getColorSharpness(),
                    bean.getColorContrast(),
                    bean.getColorGamma());
        Log.i(TAG, "getColorParamsCmd: "+param);
        return param;
    }

    public static final String getFocusModeParamsCmd(ParametersBean bean){
        String param;
        if (bean.isAutoFocusMode()){
            param = Integer.toString(ParametersBean.FOCUS_MODE_AUTO);
        } else if (bean.isQuickFocusMode()){
            param = Integer.toString(ParametersBean.FOCUS_MODE_QUICK);
        } else {
            //手动聚焦
            param = String.format(SUB_CMD_PARAMETER_FOCUS_MODE_MANUAL_MODE,
                    bean.getFocusPos());
        }

        return String.format(CMD_PARAMETER_FOCUS_MODE, param);
    }

    public static final String getFocusAreaParamsCmd(ParametersBean bean){
        return String.format(CMD_PARAMETER_FOCUS_AREA,
                bean.getFocusAreaSize(),
                bean.getFocusHorizontal(),
                bean.getFocusVertical(),
                bean.getFocusFrame());
    }

    public static final String getRequestParamsCmd(){
        return CMD_REQUEST_PARAMETERS;
    }

    public static final String getRequestShootCmd(){
        return CMD_REQUEST_SHOOT;
    }

    //将获取的相机参数转换为Bean 对象
    public static final @Nullable ParametersBean decode2Bean(String params){
        if (!isValidParametersStr(params)){
            Log.e(TAG, "decode2Bean: invalid params: "+params);
            return null;
        }

        String[] keyValuePairs = params.split("&");
        String[] pair;
        int value;
        Map<String, Integer> keyValueMap = new HashMap<>();
        for (String str : keyValuePairs){
            pair = str.split("=");
            if (pair.length != 2){
                Log.e(TAG, "decode2Bean: error format pair string: "+str);
                continue;
            }
            try {
                value = Integer.parseInt(pair[1]);
                keyValueMap.put(pair[0], value);
            } catch (NumberFormatException e){
                Log.e(TAG, "decode2Bean: NumberFormatException when parse key-value pair string: " + str);
            }
        }

        if (keyValueMap.isEmpty()){
            Log.e(TAG, "decode2Bean: no key-value pair");
            return null;
        }

        ParametersBean bean = new ParametersBean();
        bean.setKeyValuePairs(keyValueMap);
        return bean;
    }

    public static final boolean isValidParametersStr(String params){
        if (TextUtils.isEmpty(params)) return false;
//        if (!params.matches("([a-zA-Z\\-]+=[0-9]+)(&([a-zA-Z\\-]+=[0-9]+))?&")) return false;
//        if (!params.matches("([a-zA-Z\\-]+=[0-9]+&)+")) return false;
        return true;
    }

    public static final @Nullable byte[] decodeBase64StrToBytes(String base64){
        Log.v(TAG, "decodeBase64StrToBitmap: base64 str: "+base64);
        if (TextUtils.isEmpty(base64)) return null;
        return Base64.decode(base64, Base64.NO_WRAP);
    }

    /**
     * 登录指令：A&user=admin&pwd=admin&E
     * 返回：成功：enter success!
     * 失败：user or pwd is error!
     *
     * 设置参数指令：C&d&1&E
     * 返回：set ok!
     * 如果未登录，则返回：Please log in first
     *
     * 获取参数指令：D&e&E
     * 返回：pos=0&msqh=1&qydx=1&lever=4&verti=4&facus=1&qh=1&red=3&green=3&blue=3&qh=1&bright=4&zengyi=4&bin=1&skip=0&rate=10&frame-rate=10&
     *
     * @param cmd
     * @param response
     * @return
     */
    public static final boolean isCmdResponseSuccess(@NonNull String cmd, String response){
        if (CmdAndParamsCodec.isLoginCmd(cmd)){
            if (TextUtils.isEmpty(response) || !response.contains("success")){
                return false;
            }
        } else if (CmdAndParamsCodec.isApplyParameterCmd(cmd)){
            if (cmd.equals("C&c&2&E")||cmd.equals("C&a&3&E")){
                //白平衡和聚焦自动有返回参数直接 return true
                return true;
            }
            if (TextUtils.isEmpty(response) || !response.contains("ok")){
                return false;
            }
        } else if (CmdAndParamsCodec.isRequestParametersCmd(cmd)){
            return CmdAndParamsCodec.isValidParametersStr(response);
        } else if (CmdAndParamsCodec.isTakePhotoCmd(cmd)){
            if (TextUtils.isEmpty(response) || response.contains("invalid")){
                return false;
            }
        }
        return true;
    }
}
