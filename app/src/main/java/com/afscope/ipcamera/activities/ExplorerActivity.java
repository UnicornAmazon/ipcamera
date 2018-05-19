package com.afscope.ipcamera.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import com.afscope.ipcamera.R;
import com.afscope.ipcamera.utils.Toast;
import com.afscope.ipcamera.utils.Utils;
import com.afscope.ipcamera.views.AppPhotosAdapter;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ExplorerActivity extends BaseActivity {
    private static final String TAG = "ExplorerActivity";

    @BindView(R.id.tv_files_dir)
    TextView tv_files_dir;
    @BindView(R.id.gv_photo_explorer)
    GridView gv_photo_explorer;

    private AppPhotosAdapter mAppPhotosAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    @Override
    protected void initView() {
        File mediaFilesDir = Utils.getMediaFilesDir();
        tv_files_dir.setText(mediaFilesDir.getAbsolutePath());
        if (!mediaFilesDir.exists() && !mediaFilesDir.mkdirs()){
            Log.e(TAG, "initView: media files dir not exists, and cannot be created");
            return;
        }
        mAppPhotosAdapter = new AppPhotosAdapter(
                this,
                Arrays.asList(mediaFilesDir.listFiles()),
                R.layout.layout_photo_list_item);
        gv_photo_explorer.setAdapter(mAppPhotosAdapter);
    }

    @OnClick(R.id.iv_title_back)
    void back(){
        Log.d(TAG, "back: ");
        finish();
    }

    @OnItemClick(R.id.gv_photo_explorer)
    void selectMediaFile(int position){
        File file = mAppPhotosAdapter.getItem(position);
        Log.i(TAG, "selectMediaFile: " + file);
        Uri uri = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(this, getString(R.string.authorities), file);
            Log.d(TAG, "takePhotoImpl: contentUri:"+uri);
        } else {
            uri = Uri.fromFile(file);
            Log.d(TAG, "takePhotoImpl: sdk version smaller than N, uri:"+uri);
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
}
