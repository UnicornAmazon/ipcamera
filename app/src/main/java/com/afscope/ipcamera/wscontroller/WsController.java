package com.afscope.ipcamera.wscontroller;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.MonthDisplayHelper;

import com.afscope.ipcamera.common.Callback;
import com.afscope.ipcamera.common.Constants;

import java.util.IllegalFormatCodePointException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CLOSED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CLOSING;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CONNECTED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CONNECTING;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_DISCONNECTED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_FAILURE;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_LOGGED_IN;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_LOGIN_FAILED;

/**
 * Created by Administrator on 2018/5/15 0015.
 *
 * websocket 测试地址：ws://echo.websocket.org
 *
 * 开发板websocket 地址：ws://192.168.0.225:1234
 *
 */
public class WsController {
    private static final String TAG = "WsController";

    private static final int NORMAL_CLOSURE_CODE = 1000;

    private static final int DEFAULT_SEND_CMD_TIMEOUT = 5*1000;

    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private WsListener mWsListener;
    private WebSocket mWebSocket;
    private Listener mStatusListener;
    private volatile Status mStatus = STATUS_DISCONNECTED;

    private HandlerThread cmdThread;
    private Handler cmdHandler;
    private Lock cmdLock;
    private Condition cmdResponseCondition;

    private String cmdResponse;

    private WsController(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
//                .connectTimeout(10, TimeUnit.SECONDS)
                .pingInterval(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        mWsListener = new WsListener();

        cmdThread = new HandlerThread("ws_cmd_thread");
        cmdThread.start();
        cmdHandler = new Handler(cmdThread.getLooper());

        cmdLock = new ReentrantLock();
        cmdResponseCondition = cmdLock.newCondition();
    }

    public final static WsController getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private interface InstanceHolder {
        WsController INSTANCE = new WsController();
    }

    public void setListener(@NonNull Listener listener){
        mStatusListener = listener;
    }

    public void connect(String url){
        Log.i(TAG, "connect: url: " + url);
        mStatus = STATUS_CONNECTING;
        mRequest = new Request.Builder()
                .get()
                .url(url)
                .addHeader("Origin", "")
                .build();
        if (mWebSocket != null){
            Log.w(TAG, "connect when current mWebSocket is not null");
            mWebSocket.close(NORMAL_CLOSURE_CODE, null);
        }
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWsListener);
    }

    public void disconnect(){
        Log.i(TAG, "disconnect: ");
        mStatus = STATUS_DISCONNECTED;
        if (mWebSocket != null){
            mWebSocket.close(NORMAL_CLOSURE_CODE, null);
        }
    }

    public void release(){
        Log.i(TAG, "release: ");
        mStatus = STATUS_DISCONNECTED;
        cmdHandler.removeCallbacksAndMessages(null);
        cmdThread.quitSafely();
        if (mOkHttpClient != null){
            mOkHttpClient.dispatcher().cancelAll();
            mOkHttpClient.dispatcher().executorService().shutdown();
        }
    }

    public Status getStatus(){
        return mStatus;
    }

    public String getStatusStr(){
        return Status.toString(mStatus);
    }

    public boolean isConnected(){
        return mStatus == STATUS_LOGGED_IN
                || mStatus == STATUS_CONNECTED
                || mStatus == STATUS_LOGIN_FAILED;
    }

    public boolean isConnecting(){
        return mStatus == STATUS_CONNECTING;
    }

    public boolean isLoggedIn(){
        return mStatus == STATUS_LOGGED_IN;
    }

    //仅发送指令，不管结果
    public boolean sendCommand(@NonNull String cmd){
        if (mWebSocket == null){
            Log.e(TAG, "sendCommand: mWebSocket is null");
            return false;
        }
        Log.i(TAG, "sendCommand: " + cmd);

        mWebSocket.send(cmd);
        return true;
    }

    public void sendCommand(@NonNull String cmd, Callback<Callback.Result> callback){
        sendCommand(cmd, DEFAULT_SEND_CMD_TIMEOUT, callback);
    }

    /**
     *
     * @param cmd
     * @param timeout
     * @param callback  如果结果正常，Result 中result 为true ，msg 为返回内容；如果失败，为false ，msg 为出错信息
     */
    public void sendCommand(@NonNull final String cmd, final long timeout, final Callback<Callback.Result> callback){
        if (mWebSocket == null){
            callback.onResult(new Callback.Result(false, "mWebSocket is null, may not connected"));
            return;
        }
        cmdHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "sendCommand: cmd: "+cmd);
                    cmdLock.lock();
                    mWebSocket.send(cmd);
                    boolean result = cmdResponseCondition.await(timeout, TimeUnit.MILLISECONDS);
                    if (!result){
                        callback.onResult(new Callback.Result(false, "send cmd time out"));
                    } else {
                        //检查返回字符串
                        callback.onResult(new Callback.Result(
                                CmdAndParamsCodec.isCmdResponseSuccess(cmd, cmdResponse),
                                cmdResponse));
                    }

                    cmdResponse = null;
                } catch (InterruptedException e) {
                    Log.i(TAG, "sendCommand: InterruptedException when await cmd response condition");
                    callback.onResult(new Callback.Result(false, "exception happened"));
                } finally {
                    cmdLock.unlock();
                }

            }
        });
    }

//    public void sendMessage(String msg){
//        if (mWebSocket != null){
//            mWebSocket.send(msg);
//        }
//    }

    private class WsListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.i(TAG, "onOpen: ");
            mStatus = STATUS_CONNECTED;
            if (mStatusListener != null){
                mStatusListener.onStatusChanged(STATUS_CONNECTED);
            }

            //登录
            sendCommand(CmdAndParamsCodec.getLoginCmd(
                    Constants.DEFAULT_WEBSOCKET_LOGIN_USER, Constants.DEFAULT_WEBSOCKET_LOGIN_PWD),
                    new Callback<Callback.Result>(){
                        @Override
                        public void onResult(Result result) {
                            Log.i(TAG, "onOpen: log in result: " + result);
                            if (result.result){
                                mStatus = STATUS_LOGGED_IN;
                                if (mStatusListener != null){
                                    mStatusListener.onStatusChanged(STATUS_LOGGED_IN);
                                }
                            } else {
                                mStatus = STATUS_LOGIN_FAILED;
                                if (mStatusListener != null){
                                    mStatusListener.onStatusChanged(STATUS_LOGIN_FAILED);
                                }
                            }
                        }
                    });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if (text.length() < 64){
                Log.i(TAG, "onMessage: text: "+text);
            } else {
                Log.i(TAG, "onMessage: text is too long, may be photo base64 str ");
            }
//            if (mStatusListener != null){
//                mStatusListener.onMessage(text);
//            }

            try {
                cmdLock.lock();
                cmdResponse = text;
                cmdResponseCondition.signalAll();
            } finally {
                cmdLock.unlock();
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "onClosing: ");
            mStatus = STATUS_CLOSING;
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "onClosed: ");
            mStatus = STATUS_CLOSED;
            if (mStatusListener != null){
                mStatusListener.onStatusChanged(STATUS_CLOSED);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.e(TAG, "onFailure: Response: "+response+", Throwable: ", t);
            mStatus = STATUS_FAILURE;
            if (mStatusListener != null){
                mStatusListener.onStatusChanged(STATUS_FAILURE);
            }
        }

    }

    public enum Status {
        STATUS_CONNECTING,
        STATUS_CONNECTED,
        STATUS_FAILURE,
        STATUS_CLOSING,
        STATUS_CLOSED,
        STATUS_DISCONNECTED,
        STATUS_LOGGED_IN,
        STATUS_LOGIN_FAILED;

        public final static String toString(Status status) {
            switch (status){
                case STATUS_CONNECTING:
                    return "STATUS_CONNECTING";
                case STATUS_CONNECTED:
                    return "STATUS_CONNECTED";
                case STATUS_FAILURE:
                    return "STATUS_FAILURE";
                case STATUS_CLOSING:
                    return "STATUS_CLOSING";
                case STATUS_CLOSED:
                    return "STATUS_CLOSED";
                case STATUS_DISCONNECTED:
                    return "STATUS_DISCONNECTED";
                case STATUS_LOGGED_IN:
                    return "STATUS_LOGGED_IN";
                case STATUS_LOGIN_FAILED:
                    return "STATUS_LOGIN_FAILED";
                default:
                    return "ERROR_STATUS";
            }
        }
    }

    public interface Listener {
        void onStatusChanged(Status status);
        void onMessage(String msg);
    }
}
