package com.afscope.ipcamera.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.afscope.ipcamera.viewbinding.ParamsDialogBinding;

/**
 * Created by Administrator on 2018/5/21 0021.
 *
 */
public class ParamsDialog extends Dialog {
    private static final String TAG = "ParamsDialog";

    private OnShowListener outerOnShowListener;
    private OnDismissListener outerOnDismissListener;

    //显示Dialog 时的参考按钮View ，需要参考其底部的中点坐标来显示Dialog 的位置
    private View referredView;

    private int layoutId;
    private ParamsDialogBinding dialogBinding;

//    public ParamsDialog(@NonNull Context context, View referredView, int layoutId) {
//        super(context);
//        this.referredView = referredView;
//        this.layoutId = layoutId;
//    }

    public ParamsDialog(@NonNull Context context, View referredView, int layoutId,
                        @NonNull ParamsDialogBinding binding) {
        super(context);
        this.referredView = referredView;
        this.layoutId = layoutId;
        dialogBinding = binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        View view = ((ViewGroup)getWindow().getDecorView()).getChildAt(0);
        dialogBinding.bind(view);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.i(TAG, "onShow: " );
//                View view = ((Dialog)dialog).findViewById(R.id.ll_white_balance_params);
                //弹出动画
                View view = ((ViewGroup)getWindow().getDecorView()).getChildAt(0);
//                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0.0f, 200.0f, 0f);
//                animator.setDuration(5000);
//                animator.start();

                if (outerOnShowListener != null){
                    outerOnShowListener.onShow(dialog);
                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i(TAG, "onDismiss: ");
                //隐藏动画

                if (outerOnDismissListener != null){
                    outerOnDismissListener.onDismiss(dialog);
                }
            }
        });
    }

    public ParamsDialog setOuterOnShowListener(OnShowListener outerOnShowListener) {
        this.outerOnShowListener = outerOnShowListener;
        return this;
    }

    public ParamsDialog setOuterOnDismissListener(OnDismissListener outerOnDismissListener) {
        this.outerOnDismissListener = outerOnDismissListener;
        return this;
    }

    @Override
    public void onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow: ");
        getWindow().getDecorView().getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                View decorView = getWindow().getDecorView();
                if (decorView.getViewTreeObserver().isAlive())
                    decorView.getViewTreeObserver().removeOnPreDrawListener(this);

                // put your code here
                setWindowPosition(getContentViewWidth());
                return false;
            }
        });
    }

    private int getContentViewWidth() {
        View contentView = ((ViewGroup)getWindow().getDecorView()).getChildAt(0);
        return contentView.getWidth();
    }

    //确定Dialog 的显示位置
    private void setWindowPosition(int width){
        int referredX = (int) referredView.getX() + referredView.getWidth() / 2;
        int referredY = (int) referredView.getY() + referredView.getHeight();

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        int x = referredX - width / 2;
        int screenWidth = getWindow().getDecorView().getResources().getDisplayMetrics().widthPixels;
        if (x < 0){
            x = 10;
        } else if (x + width > screenWidth) {
            x = screenWidth - width - 10;
        }

        wlp.x = x;
        wlp.y = referredY + 20;
        Log.i(TAG, "setWindowPosition: screenWidth = " + screenWidth
                + ", window width = " + width /*+ ", height = " + height*/
                + ", referredX = " + referredX + ", referredY = " + referredY
                + ", window x = " + wlp.x + ", y = " + wlp.y);
        wlp.gravity = Gravity.LEFT|Gravity.TOP;
        window.setAttributes(wlp);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "onConfigurationChanged: ORIENTATION_LANDSCAPE");
        } else {
            Log.i(TAG, "onConfigurationChanged: ORIENTATION_PORTRAIT");
        }
        referredView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (referredView.getViewTreeObserver().isAlive()){
                    referredView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                setWindowPosition(getContentViewWidth());
                return false;
            }
        });
    }
}
