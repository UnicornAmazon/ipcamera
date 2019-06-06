package com.afscope.ipcamera.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.afscope.sloptoelectronic.OptoelecJinV2;
import com.google.gson.Gson;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public abstract class BaseActivity extends Activity {
    public   final String TAG = this.getClass().getSimpleName();
    OptoelecJinV2 optoelecJinV2 = new OptoelecJinV2();
    Gson gson = new Gson();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getName());
        optoelecJinV2.sdkInit();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData(savedInstanceState);
    }

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected void initData(Bundle savedInstanceState){}
}
