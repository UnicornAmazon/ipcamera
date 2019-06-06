package com.afscope.sloptoelectronic;

public class OptoelecJinV2 {
    static {
        System.loadLibrary("native-lib");
    }
    public native int login(String s);
    public native int decOpen();
    public native int decBind();
    public native int openWnd();
    public native int render();
    public native int streamRequest();
    public native int requestPicStream();
    public native byte[] getPicStream();
    public native String getDeviceIp();
    public native int multicastOpen();
    public native int multicastScanner();
    public native int multicastClose();
    public native int sdkInit();
    public native void sdkQuit();
}
