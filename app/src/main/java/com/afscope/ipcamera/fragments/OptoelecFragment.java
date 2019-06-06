package com.afscope.ipcamera.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.LoginBean;
import com.afscope.ipcamera.common.MessageEvent;
import com.afscope.sloptoelectronic.GLSurfaceViewEx;
import com.afscope.sloptoelectronic.JfGLSurfaceView;
import com.afscope.sloptoelectronic.OptoelecJinV2;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import static android.opengl.GLES20.glViewport;
import static com.afscope.ipcamera.common.EventFiledInterface.VIDEO_DISPLAY;

public class OptoelecFragment extends Fragment {
    private  final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.gl)
    JfGLSurfaceView surfaceView;
    Unbinder unbinder;
    boolean isVideoDisplay = true;
    private OptoelecJinV2 optoelecJinV2;
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_optoelec, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        optoelecJinV2 = new OptoelecJinV2();
        int i = optoelecJinV2.decOpen();
        int i1 = optoelecJinV2.decBind();
        optoelecJinV2.streamRequest();
        Log.i("lixiang","onCreate: " + "decopen=" + i + "decbind=" + i1 );
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (this) {
                    if (null!=surfaceView)
                        surfaceView.getYuvMediaData();
                }
            }
        };
        timer.schedule(task,0,15);
//        surfaceView.setEGLContextClientVersion(2);
//        // Assign our renderer.
//        surfaceView.setRenderer(new GLSurfaceViewEx.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//                optoelecJinV2.openWnd();
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                glViewport(0, 0, width, height);
//            }
//
//            @Override
//            public boolean onDrawFrame(GL10 gl) {
//                int render = optoelecJinV2.render();
////                Log.i(TAG, "onDrawFrame: "+render);
//                postVideoDisplay(render);
//                return render == 0 ? true : false;
//            }
//        });
//    }
//    private void postVideoDisplay(int render) {
//        if (!isVideoDisplay){
//            return;
//        }
//        if (isVideoDisplay&&render==0) {
//            isVideoDisplay = false;
//            EventBus.getDefault().post(new MessageEvent(VIDEO_DISPLAY));
//        }
    }

    public byte[] requestPicStream() {
         optoelecJinV2.requestPicStream();
        byte[] picByte = optoelecJinV2.getPicStream();
        return picByte;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        timer.cancel();
    }
}

