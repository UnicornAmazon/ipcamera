package com.afscope.ipcamera.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/5/21 0021.
 *
 */
public class ParamsBarLayout extends LinearLayout {
    private static final String TAG = "ParamsBarLayout";

    private boolean isHidden = true;
    private Handler mHandler;

    public ParamsBarLayout(Context context) {
        this(context, null);
    }

    public ParamsBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
    }

    public void showOrHide(){
        Log.i(TAG, "showOrHide: isHidden ? "+isHidden);
        mHandler.removeCallbacksAndMessages(null);
        if (isHidden){
            show();
        } else {
            hide();
        }
    }

    private void show(){
        isHidden = false;
        animate().translationY(getHeight())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isHidden){
                                    hide();
                                }
                            }
                        }, 3000);
                    }
                });
    }

    private void hide(){
        isHidden = true;
        animate().translationY(0)
                .setDuration(400);
    }

    public void showForever(){
        Log.i(TAG, "showForever: ");
        if (isHidden){
            show();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void hideAfterAWhile(){
        Log.i(TAG, "hideAfterAWhile: ");
        if (!isHidden){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isHidden){
                        hide();
                    }
                }
            }, 3000);
        }
    }
}
