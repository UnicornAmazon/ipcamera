package com.afscope.ipcamera.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.afscope.ipcamera.R;

/**
 * Created by Administrator on 2018/5/21 0021.
 *
 */
public class CameraContentLayout extends RelativeLayout {
    private static final String TAG = "CameraContentLayout";

    public CameraContentLayout(Context context) {
        super(context);
    }

    public CameraContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = findViewById(R.id.ll_white_balance_params);
        Log.i(TAG, "onFinishInflate, ll_white_balance_params: "+view);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()){
            Log.i(TAG, "onTouchEvent: ACTION_UP not consumed");

        }
        return super.onTouchEvent(event);
    }
}
