package com.afscope.ipcamera.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.utils.Utils;


import org.easydarwin.video.Client;
import org.easydarwin.video.EasyPlayerClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RTSP 测试地址：rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
 * <p>
 * 开发板RTSP 地址：rtsp://192.168.1.222:8553/PSIA/Streaming/channels/0?videoCodecType=H.264
 */
public class PlayFragment extends Fragment implements TextureView.SurfaceTextureListener {
    protected static final String TAG = "PlayFragment";

    public static final int RESULT_REND_STARTED = 1;
    public static final int RESULT_REND_VIDEO_DISPLAYED = 2;

    public static final int RESULT_REND_STOPED = -1;

    //转录视频状态
    public static final int RECORD_STATE_BEGIN = 1001;
    public static final int RECORD_STATE_END = 1002;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 等比例,最大化区域显示,不裁剪
     */
    public static final int ASPACT_RATIO_INSIDE = 1;
    /**
     * 等比例,裁剪,裁剪区域可以通过拖拽展示\隐藏
     */
    public static final int ASPACT_RATIO_CROPE_MATRIX = 2;
    /**
     * 等比例,最大区域显示,裁剪
     */
    public static final int ASPACT_RATIO_CENTER_CROPE = 3;
    /**
     * 拉伸显示,铺满全屏
     */
    public static final int FILL_WINDOW = 4;


    /* 本Key为3个月临时授权License，如需商业使用或者更改applicationId，请邮件至support@easydarwin.org申请此产品的授权。
     */
    public static final String KEY = "79393674363536526D3432415170646170576938792B5A76636D63755A57467A65575268636E64706269356C59584E356347786865575679567778576F50394C34456468646D6C754A6B4A68596D397A595541794D4445325257467A65555268636E6470626C526C5957316C59584E35";

    protected String mUrl;
    protected EasyPlayerClient mStreamRender;
    protected ResultReceiver mResultReceiver;
    protected int mWidth;
    protected int mHeight;
    protected View.OnLayoutChangeListener listener;
    protected TextureView mSurfaceView;

    private int mRatioType = ASPACT_RATIO_INSIDE;

    private OnStateChangedListener stateChangedListener;

    public interface OnStateChangedListener {
        void onRenderStateChanged(int state);

        void onRecordStateChanged(int state);
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        stateChangedListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        final View view = inflater.inflate(R.layout.fragment_play, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: ");
        mSurfaceView = (TextureView) view.findViewById(R.id.surface_view);
        mSurfaceView.setOpaque(false);
        mSurfaceView.setSurfaceTextureListener(this);
        //注册到EasyPlayerClient 的回调
        mResultReceiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                Log.i(TAG, "onReceiveResult: resultCode = " + resultCode + ", resultData: " + resultData);
                if (resultCode == EasyPlayerClient.RESULT_VIDEO_DISPLAYED) {
                    onVideoDisplayed();
                } else if (resultCode == EasyPlayerClient.RESULT_VIDEO_SIZE) {
                    mWidth = resultData.getInt(EasyPlayerClient.EXTRA_VIDEO_WIDTH);
                    mHeight = resultData.getInt(EasyPlayerClient.EXTRA_VIDEO_HEIGHT);
                    onVideoSizeChange();
                } else if (resultCode == EasyPlayerClient.RESULT_TIMEOUT) {
                    new AlertDialog.Builder(getActivity()).setMessage("试播时间到").setTitle("SORRY").setPositiveButton(android.R.string.ok, null).show();
                } else if (resultCode == EasyPlayerClient.RESULT_UNSUPPORTED_AUDIO) {
                    new AlertDialog.Builder(getActivity()).setMessage("音频格式不支持").setTitle("SORRY").setPositiveButton(android.R.string.ok, null).show();
                } else if (resultCode == EasyPlayerClient.RESULT_UNSUPPORTED_VIDEO) {
                    new AlertDialog.Builder(getActivity()).setMessage("视频格式不支持").setTitle("SORRY").setPositiveButton(android.R.string.ok, null).show();
                } else if (resultCode == EasyPlayerClient.RESULT_EVENT) {
//                    Log.i(TAG, "onReceiveResult: ");
//                    int errorcode = resultData.getInt("errorcode");
//                    if (errorcode != 0){
//                        stopRending();
//                    }
                } else if (resultCode == EasyPlayerClient.RESULT_RECORD_BEGIN) {
                    if (stateChangedListener != null) {
                        stateChangedListener.onRecordStateChanged(RECORD_STATE_BEGIN);
                    }
                } else if (resultCode == EasyPlayerClient.RESULT_RECORD_END) {
                    if (stateChangedListener != null) {
                        stateChangedListener.onRecordStateChanged(RECORD_STATE_END);
                    }
                }
            }
        };

        listener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, String.format("onLayoutChange left:%d,top:%d,right:%d,bottom:%d" +
                                "->oldLeft:%d,oldTop:%d,oldRight:%d,oldBottom:%d",
                        left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom));
                if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                    onVideoSizeChange();
                }
            }
        };
        ViewGroup parent = (ViewGroup) view.getParent();
        parent.addOnLayoutChangeListener(listener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mStreamRender != null) {
                mStreamRender.pause();
            }
        } else {
            if (mStreamRender != null) {
                mStreamRender.resume();
            }
        }
    }

    public byte[] takePicture() {

        if (mWidth <= 0 || mHeight <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mSurfaceView.getBitmap(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes=baos.toByteArray();
//        saveBitmapInFile(path, bitmap);
        return bytes;
    }

    private MediaScannerConnection mScanner;

    public void saveBitmapInFile(final String path, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (mScanner == null) {
                MediaScannerConnection connection = new MediaScannerConnection(getActivity(),
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            public void onMediaScannerConnected() {
                                mScanner.scanFile(path, null /* mimeType */);
                            }

                            public void onScanCompleted(String path1, Uri uri) {
                                if (path1.equals(path)) {
                                    mScanner.disconnect();
                                    mScanner = null;
                                }
                            }
                        });
                connection.connect();
                mScanner = connection;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        ViewGroup parent = (ViewGroup) getView().getParent();
        parent.removeOnLayoutChangeListener(listener);
        stopRending();
        super.onDestroyView();
    }

    public void setUrl(String url) {
        this.mUrl = url;
//        if (mSurfaceView.isAvailable()){
//            startRending(mSurfaceView.getSurfaceTexture());
//        }
    }

    public void startPlaying() throws IllegalStateException {
        Log.i(TAG, "startPlaying: ");
        if (mUrl == null) {   // TODO: 2018/5/19 0019 校验URL 合法性
            throw new IllegalStateException(String.format("url(%s) is null or illegal", mUrl));
        }
        if (mSurfaceView.isAvailable()) {
            startRending(mSurfaceView.getSurfaceTexture());
        } else {
            Log.i(TAG, "startPlaying: surface texture is not ready");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRending(mSurfaceView.getSurfaceTexture());
                }
            }, 600);
        }
    }

    public void stopPlaying() {
        Log.i(TAG, "stopPlaying: ");
        if (mStreamRender != null) {
            mStreamRender.stop();
        }
    }

    private void startRending(SurfaceTexture surface) {
        if (mUrl == null) {
            Log.e(TAG, "startRending: mUrl is null");
            Toast.toast("错误：未设置视频源URL地址！");
            return;
        }
        mStreamRender = new EasyPlayerClient(getActivity(), KEY, new Surface(surface), mResultReceiver);
        mStreamRender.setAudioEnable(false);

        try {
            mStreamRender.start(mUrl,
                    Client.TRANSTYPE_TCP,
                    Client.EASY_SDK_VIDEO_FRAME_FLAG /*| Client.EASY_SDK_AUDIO_FRAME_FLAG*/,
                    "",
                    "");
            notifyRenderStateChanged(RESULT_REND_STARTED);
        } catch (Exception e) {
            Log.e(TAG, "startRending: error when start rendering ", e);
            Toast.toast("播放视频时出现异常！");
        }
    }

    private void stopRending() {
        if (mStreamRender != null) {
            notifyRenderStateChanged(RESULT_REND_STOPED);
            mStreamRender.stop();
            mStreamRender = null;
        }
    }

    public long getReceivedStreamLength() {
        if (mStreamRender != null) {
            return mStreamRender.receivedDataLength();
        }
        return 0;
    }

    public boolean startOrStopRecord() throws IllegalAccessException {
        if (!mStreamRender.isRecording()) {
            File mediaFilesDir = Utils.getMediaFilesDir();
            if (!mediaFilesDir.exists() && !mediaFilesDir.mkdirs()) {
                Log.e(TAG, "startOrStopRecord: media files dir not exists, and cannot be created");
                throw new IllegalAccessException("cannot create media files dir for saving record file");
            }
            Log.i(TAG, "startOrStopRecord: start record, files dir: " + mediaFilesDir.getAbsolutePath());
            mStreamRender.startRecord(new File(mediaFilesDir,
                    dateFormat.format(new Date()) + ".mp4").getAbsolutePath());
            return true;
        } else {
            Log.i(TAG, "startOrStopRecord: stop record ");
            mStreamRender.stopRecord();
            return false;
        }
    }

    public boolean isRecording() {
        return mStreamRender.isRecording();
    }

    public void setScaleType(@IntRange(from = ASPACT_RATIO_INSIDE, to = FILL_WINDOW) int type) {
        mRatioType = type;
        if (mWidth != 0 && mHeight != 0) {
            onVideoSizeChange();
        }
    }

    protected void notifyRenderStateChanged(int resultCode) {
        if (stateChangedListener != null) {
            stateChangedListener.onRenderStateChanged(resultCode);
        }
    }

    private void onVideoSizeChange() {
        Log.i(TAG, String.format("onVideoSizeChange, RESULT_VIDEO_SIZE RECEIVED :%d*%d", mWidth, mHeight));
        if (mWidth == 0 || mHeight == 0) return;
        if (mRatioType == ASPACT_RATIO_CROPE_MATRIX) {
            ViewGroup parent = (ViewGroup) getView().getParent();
            parent.addOnLayoutChangeListener(listener);
            fixPlayerRatio(getView(), parent.getWidth(), parent.getHeight());
            mSurfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mSurfaceView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            mSurfaceView.setTransform(new Matrix());
//            int viewWidth = mSurfaceView.getWidth();
//            int viewHeight = mSurfaceView.getHeight();
            if (getView() == null) {
                return;
            }
            float ratioView = getView().getWidth() * 1.0f / getView().getHeight();
            float ratio = mWidth * 1.0f / mHeight;

            switch (mRatioType) {
                case ASPACT_RATIO_INSIDE: {

                    if (ratioView - ratio < 0) {    // 屏幕比视频的宽高比更小.表示视频是过于宽屏了.
                        // 宽为基准.
                        mSurfaceView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        mSurfaceView.getLayoutParams().height = (int) (getView().getWidth() / ratio + 0.5f);
                    } else {                          // 视频是竖屏了.
                        mSurfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        mSurfaceView.getLayoutParams().width = (int) (getView().getHeight() * ratio + 0.5f);
                    }
                }
                break;
                case ASPACT_RATIO_CENTER_CROPE: {
                    // 以更短的为基准
                    if (ratioView - ratio < 0) {    // 屏幕比视频的宽高比更小.表示视频是过于宽屏了.
                        // 宽为基准.
                        mSurfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                        mSurfaceView.getLayoutParams().width = (int) (getView().getHeight() * ratio + 0.5f);
                    } else {                          // 视频是竖屏了.
                        mSurfaceView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        mSurfaceView.getLayoutParams().height = (int) (getView().getWidth() / ratio + 0.5f);
                    }
                }
                break;
                case FILL_WINDOW: {
                    mSurfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mSurfaceView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                break;
            }
        }
        mSurfaceView.requestLayout();
    }

    private void onVideoDisplayed() {
        Log.i(TAG, String.format("VIDEO DISPLAYED!!!!%d*%d", mWidth, mHeight));
//        mSurfaceView.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mWidth != 0 && mHeight != 0) {
//                    Bitmap e = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//                    mSurfaceView.getBitmap(e);
//                    File f = new File(MyApplication.sPicturePath, "111.jpg");
//                    saveBitmapInFile(f.getPath(), e);
//                    e.recycle();
//                }
//            }
//        });
        notifyRenderStateChanged(RESULT_REND_VIDEO_DISPLAYED);
    }

    protected void fixPlayerRatio(View renderView, int widthSize, int heightSize, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        float aspectRatio = width * 1.0f / height;
        if (widthSize > heightSize * aspectRatio) {
            height = heightSize;
            width = (int) (height * aspectRatio);
        } else {
            width = widthSize;
            height = (int) (width / aspectRatio);
        }
        renderView.getLayoutParams().width = width;
        renderView.getLayoutParams().height = height;
        renderView.requestLayout();
    }

    /**
     * 高度固定，宽度可更改；
     *
     * @param renderView
     * @param maxWidth
     * @param maxHeight
     */
    protected void fixPlayerRatio(View renderView, int maxWidth, int maxHeight) {
//        fixPlayerRatio(renderView, maxWidth, maxHeight, mWidth, mHeight);
    }


    /*--------------------------  实现SurfaceTextureListener 接口  Begin  -------------------------*/
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable: ");
        if (mUrl != null) {
            startRending(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged: ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed: ");
        stopRending();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    /*--------------------------  实现SurfaceTextureListener 接口  End  -------------------------*/

    public boolean toggleAudioEnable() {
        if (mStreamRender == null) {
            return false;
        }
        mStreamRender.setAudioEnable(!mStreamRender.isAudioEnable());
        return mStreamRender.isAudioEnable();
    }

    public boolean isAudioEnable() {
        return mStreamRender != null && mStreamRender.isAudioEnable();
    }

}
