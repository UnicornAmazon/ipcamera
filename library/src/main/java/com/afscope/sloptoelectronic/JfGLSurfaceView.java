package com.afscope.sloptoelectronic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class JfGLSurfaceView extends GLSurfaceView {

    private JfRender jfRender;
    public JfGLSurfaceView(Context context) {
        this(context,null);
    }

    public JfGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        jfRender = new JfRender(context);
        setRenderer(jfRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//requestRender()时不会重复渲染
    }
    /**
     *
     * @param width
     * @param height
     * @param y
     * @param u
     * @param v
     */
    public void setYUVData(int width,int height,byte[] y,byte[] u,byte[] v){
        if (jfRender != null) {
            jfRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }
    public native int getYuvMediaData();
}
