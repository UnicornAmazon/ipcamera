package com.afscope.ipcamera.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.utils.Utils;
import com.afscope.ipcamera.views.AppPhotosAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ExplorerActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener {
    private static final String TAG = "ExplorerActivity";

    @BindView(R.id.tv_files_dir)
    TextView tv_files_dir;
    @BindView(R.id.gv_photo_explorer)
    GridView gv_photo_explorer;
    private int position = 0;
    private AppPhotosAdapter mAppPhotosAdapter;
    private PopupWindow popupWindow;
    private List<File> list;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    @Override
    protected void initView() {
        initPop();
        File mediaFilesDir = Utils.getMediaFilesDir();
        tv_files_dir.setText(mediaFilesDir.getAbsolutePath());
        if (!mediaFilesDir.exists() && !mediaFilesDir.mkdirs()) {
            Log.e(TAG, "initView: media files dir not exists, and cannot be created");
            return;
        }
        File[] files = mediaFilesDir.listFiles();
        List<File> arrlist  = Arrays.asList(files);
        list = new ArrayList(arrlist);
        Log.i(TAG, "initView: " + files.length);
        mAppPhotosAdapter = new AppPhotosAdapter(
                this,
                list,
                R.layout.layout_photo_list_item);
        gv_photo_explorer.setAdapter(mAppPhotosAdapter);
        gv_photo_explorer.setOnItemLongClickListener(this);
    }

    private void initPop() {
        View layout = View.inflate(this, R.layout.dailog_del_file, null);
        layout.findViewById(R.id.btn_cancel_collection).setOnClickListener(this);
        layout.findViewById(R.id.btn_moment_Cancel).setOnClickListener(this);
        popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    @OnClick(R.id.iv_title_back)
    void back() {
        Log.d(TAG, "back: ");
        finish();
    }

    @OnItemClick(R.id.gv_photo_explorer)
    void selectMediaFile(int position) {
        File file = mAppPhotosAdapter.getItem(position);
        Log.i(TAG, "selectMediaFile: " + file);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getString(R.string.authorities), file);
            Log.d(TAG, "takePhotoImpl: contentUri:" + uri);
        } else {
            uri = Uri.fromFile(file);
            Log.d(TAG, "takePhotoImpl: sdk version smaller than N, uri:" + uri);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (file.getName().endsWith(".jpg")) {
            try {
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "selectMediaFile, error: no activity to view JPG");
            }
        } else if (file.getName().endsWith(".mp4")) {
            try {
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "selectMediaFile, error: no activity to view MP4");
            }
        } else {
            Toast.toast("不支持的文件扩展名！");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_collection:
                //删除
                delFile();
                break;
            case R.id.btn_moment_Cancel:
                popupWindow.dismiss();
                break;
        }
    }

    private void delFile() {
        File file = list.get(position);
        file.delete();
        list.remove(file);
        popupWindow.dismiss();
        mAppPhotosAdapter.notifyDataSetChanged();
    }
}
