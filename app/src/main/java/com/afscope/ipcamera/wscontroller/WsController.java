package com.afscope.ipcamera.wscontroller;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CLOSED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CLOSING;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CONNECTED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_CONNECTING;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_DISCONNECTED;
import static com.afscope.ipcamera.wscontroller.WsController.Status.STATUS_FAILURE;

/**
 * Created by Administrator on 2018/5/15 0015.
 *
 * websocket 测试地址：ws://echo.websocket.org
 *
 */
public class WsController {
    private static final String TAG = "WsController";

    private static final int NORMAL_CLOSURE_CODE = 1000;

    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private WsListener mWsListener;
    private WebSocket mWebSocket;
    private Listener mStatusListener;
    private volatile Status mStatus = STATUS_DISCONNECTED;

    private WsController(){
        mOkHttpClient = new OkHttpClient.Builder()
                .pingInterval(10, TimeUnit.SECONDS)
                .build();
        mWsListener = new WsListener();
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
        mRequest = new Request.Builder().url(url).build();
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWsListener);
    }

    public void disconnect(){
        Log.i(TAG, "disconnect: ");
        if (mWebSocket != null){
            mWebSocket.close(NORMAL_CLOSURE_CODE, null);
        }
    }

    public void release(){
        Log.i(TAG, "release: ");
        if (mOkHttpClient != null){
            mOkHttpClient.dispatcher().cancelAll();
            mOkHttpClient.dispatcher().executorService().shutdown();
        }
    }

    public Status getStatus(){
        return mStatus;
    }

    public void sendMessage(String msg){
        if (mWebSocket != null){
            mWebSocket.send(msg);
        }
    }

    private class WsListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.i(TAG, "onOpen: ");
            mStatus = STATUS_CONNECTED;
            if (mStatusListener != null){
                mStatusListener.onStatusChanged(STATUS_CONNECTED);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.i(TAG, "onMessage: text: "+text);
            if (mStatusListener != null){
                mStatusListener.onMessage(text);
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
            Log.i(TAG, "onFailure: ");
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
        STATUS_DISCONNECTED;

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
