package com.afscope.ipcamera.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getName());
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData(savedInstanceState);
    }

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected void initData(Bundle savedInstanceState){}
}
