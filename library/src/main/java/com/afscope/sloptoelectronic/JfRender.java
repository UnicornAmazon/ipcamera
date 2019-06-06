package com.afscope.sloptoelectronic;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import org.easydarwin.video.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class JfRender implements GLSurfaceView.Renderer {
    private Context context;

    private final float[] vertexData ={//顶点坐标

            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f

    };

    private final float[] textureData ={//纹理坐标
            0f,1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program_yuv;
    private int avPosition_yuv;
    private int afPosition_yuv;

    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int[] textureId_yuv;

    //渲染用
    private int width_yuv;
    private int height_yuv;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;

    public JfRender(Context context){
        this.context = context;
        //存储顶点坐标数据
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        //存储纹理坐标
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRenderYUV();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //用黑色清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        renderYUV();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    //shader的vsh源码字符串
    private static final String vertexShaderString =
            "attribute vec4 vertexIn;" +
                    "attribute vec2 textureIn;" +
                    "varying vec2 textureOut;" +
                    "void main() {" +
                    "gl_Position = vertexIn;" +
                    "v_texPosition = textureIn;" +
                    "}";

    //shader的fsh源码字符串
    private static final String yuvFragmentShaderString =
            "precision mediump float;" +
                    "uniform sampler2D tex_y;" +
                    "uniform sampler2D tex_u;" +
                    "uniform sampler2D tex_v;" +
                    "varying vec2 textureOut;" +
                    "void main() {" +
                    "vec4 c = vec4((texture2D(tex_y, textureOut).r - 16./255.) * 1.164);" +
                    "vec4 U = vec4(texture2D(tex_u, textureOut).r - 128./255.);" +
                    "vec4 V = vec4(texture2D(tex_v, textureOut).r - 128./255.);" +
                    "c += V * vec4(1.596, -0.813, 0, 0);" +
                    "c += U * vec4(0, -0.392, 2.017, 0);" +
                    "c.a = 1.0;" +
                    "gl_FragColor = c;" +
                    "}";
    /**
     * 初始化
     */
    private void initRenderYUV(){
        String vertexSource = JfShaderUtil.readRawTxt(context, R.raw.vertex_shader);
        String fragmentSource = JfShaderUtil.readRawTxt(context, R.raw.fragment_shader);
        //创建一个渲染程序
        program_yuv = JfShaderUtil.createProgram(vertexSource,fragmentSource);

        //得到着色器中的属性
        avPosition_yuv = GLES20.glGetAttribLocation(program_yuv,"av_Position");
        afPosition_yuv = GLES20.glGetAttribLocation(program_yuv,"af_Position");



        sampler_y = GLES20.glGetUniformLocation(program_yuv, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(program_yuv, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(program_yuv, "sampler_v");

        //创建纹理
        textureId_yuv = new int[3];
        GLES20.glGenTextures(3, textureId_yuv, 0);

        for(int i = 0; i < 3; i++)
        {
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[i]);
            //设置环绕和过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
        JfLog.d("initRenderYUV");
    }

    public void setYUVRenderData(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        this.width_yuv = width;
        this.height_yuv = height;
        this.y = ByteBuffer.wrap(y);
        this.u = ByteBuffer.wrap(u);
        this.v = ByteBuffer.wrap(v);
    }

    /**
     * 渲染
     */
    private void renderYUV(){
        JfLog.d("渲染中");
        if(width_yuv > 0 && height_yuv > 0 && y != null && u != null && v != null){
            GLES20.glUseProgram(program_yuv);//使用源程序

            GLES20.glEnableVertexAttribArray(avPosition_yuv);//使顶点属性数组有效
            GLES20.glVertexAttribPointer(avPosition_yuv, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);//为顶点属性赋值

            GLES20.glEnableVertexAttribArray(afPosition_yuv);
            GLES20.glVertexAttribPointer(afPosition_yuv, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//激活纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[0]);//绑定纹理
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv, height_yuv, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);//

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv / 2, height_yuv / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv / 2, height_yuv / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);

            GLES20.glUniform1i(sampler_y, 0);
            GLES20.glUniform1i(sampler_u, 1);
            GLES20.glUniform1i(sampler_v, 2);

            y.clear();
            u.clear();
            v.clear();
            y = null;
            u = null;
            v = null;

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}
