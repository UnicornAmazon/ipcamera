package com.afscope.ipcamera.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.fragments.PlayFragment;


/**
 * 作者：Administrator on 2016/6/22 0022 11:07
 * 功能：欢迎页+广告页
 * 说明：展示欢迎页+广告页
 */
public class SplashAty extends BaseActivity {

    /**
     * 跳过
     */
    private TextView mTxtjumpTo;

    /**
     * 启动页背景
     */
    private ImageView mImgBg;

    /**
     * 界面广告停留时间
     */
    private int recLen;

    /**
     * 倒计时类
     */
    private MyCount mc;

    //是否点击广告
    private boolean isClickAdv;
    //时间是否结束
    private boolean isTimeOver;
    private boolean enforceUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        getWindow().setBackgroundDrawable(null);
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        mImgBg =  findViewById(R.id.id_start_page_bg_img);
//        PlayFragment playFragment=new PlayFragment();
//        playFragment.setUrl("rtsp://192.168.7.1:8553/PSIA/Streaming/channels/0?videoCodecType=H.264");
        // 判断用户是否是第一次安装
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashAty.this, CameraActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 2000);
    }
//    private void initV() throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        long currentTime = System.currentTimeMillis();
//        String format = sdf.format(new Date(currentTime));
//        //上刊日期
//        String adv_upIngDate = adBean.getStartDate();
//        String adv_exDate = adBean.getEndDate();
//        if (!StringUtil.isEmpty(adv_upIngDate) && !StringUtil.isEmpty(adv_exDate)) {
//            long startTime = sdf.parse(adv_upIngDate).getTime();
//            long endTime = sdf.parse(adv_exDate).getTime();
//            //过期日期
//            if (currentTime >= startTime && currentTime < endTime) {
//                recLen = 1000 * 5;
//                if (Protect.checkLoadImageStatus(aty)) {
//                    Glide.with(aty).load(adBean.getImgUrl()).placeholder(R.drawable.page_bg_qq_new).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImgBg);
//                }
//
//                if (!StringUtils.isEmpty(adBean.getAdvUrl())) {
//                    mImgBg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            isClickAdv = true;
//                            LinkHelper.bannerJump(adBean);
//                        }
//                    });
//                }
//            } else {
//                jumpTo();
//            }
//        } else {
//            jumpTo();
//            return;
//        }
//
//        mTxtjumpTo = (TextView) findViewById(R.id.id_jump_txt);
//        mTxtjumpTo.setVisibility(View.VISIBLE);
//        mTxtjumpTo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isTimeOver = true;
//                jumpTo();
//            }
//        });
//        //界面广告停留时间
//        mc = new MyCount(recLen, 1000);
//        mc.start();
//    }


    public static boolean isNetUrl(String s) {
        try {
            return Patterns.WEB_URL.matcher(s).matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickAdv = false;
        if (isTimeOver && !isFinishing()) {
            jumpTo();
        }
    }

    /**
     * 跳转
     */
    private void jumpTo() {
        if (enforceUpdate) {
            return;
        }
        if (!isClickAdv) {
//            skipActivity(aty, CameraActivity.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * 倒计时类
     */
    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
//            mTxtjumpTo.setText(getString(R.string.start_page_jump) + "(" + l / 1000
//                    + getString(R.string.second) + ")");
        }

        @Override
        public void onFinish() {
            isTimeOver = true;
            jumpTo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mc) {
            mc.cancel();
        }
    }

}
