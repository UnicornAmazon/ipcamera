package com.afscope.ipcamera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.beans.GroupDiscoverBean;
import com.afscope.ipcamera.beans.Login;
import com.afscope.ipcamera.beans.LoginBean;
import com.afscope.ipcamera.common.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.afscope.ipcamera.utils.Utils.getObjectList;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.ip_address)
    EditText ipAddress;
    @BindView(R.id.account_name)
    EditText accountName;
    @BindView(R.id.password_name)
    EditText passwordName;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.port_num)
    EditText portNum;
    List<String> ipList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ipAddress.setText((String) spinner.getSelectedItem());
                ((TextView) spinner.getSelectedView()).setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, getIpList());
        //设置适配器
        spinner.setAdapter(adapter);
    }

    private List<String> getIpList() {
        String deviceIp = optoelecJinV2.getDeviceIp();
        Log.i(TAG, "groupIPs: " + deviceIp);
        List<GroupDiscoverBean> groupList = getObjectList(deviceIp, GroupDiscoverBean.class);
        for (GroupDiscoverBean groupDiscoverBean : groupList) {
            ipList.add(groupDiscoverBean.getIp());
        }
        return ipList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        isShowPb(false);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        final String parameter = setValues();
        isShowPb(true);
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                e.onNext(optoelecJinV2.login(parameter));
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "loginstate: =" + integer);
                        isShowPb(false);
                        if (integer == 0) {
                            startActivity(new Intent(LoginActivity.this, CameraActivity.class));
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void isShowPb(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private String setValues() {
        String ip = ipAddress.getText().toString().trim();
        String name = accountName.getText().toString().trim();
        String password = passwordName.getText().toString().trim();
        String portNumber = portNum.getText().toString().trim();
        Login login = new Login(name, password);
        String values = gson.toJson(new LoginBean(ip, TextUtils.isEmpty(portNumber) ? 0 : Integer.valueOf(portNumber), login));
        return values;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
