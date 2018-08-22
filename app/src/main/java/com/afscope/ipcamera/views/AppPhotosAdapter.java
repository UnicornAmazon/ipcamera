package com.afscope.ipcamera.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afscope.ipcamera.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class AppPhotosAdapter extends CommonAdapter<File> implements AdapterView.OnItemClickListener {
    private static final String TAG = "AppPhotosAdapter";

    private int itemWidth;
private Context mContext;
    private HashMap<Integer, Boolean> isSelected ;

    public AppPhotosAdapter(Context context, List<File> datas, int layoutId){
        super(context, datas, layoutId);
        mContext=context;
        //获取屏幕宽度
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int screenWidthPx = dm.widthPixels;
        Log.d(TAG, "AppPhotosAdapter: density:"+density+", screenWidth:"+screenWidthPx);
        itemWidth = screenWidthPx/3 ;  //每行排列三个预览图

        init();
    }

    public void init(){
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < getCount(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public void convert(ViewHolder holder, File file, int position) {
        ImageView iv_photo_item = holder.getView(R.id.iv_photo_item);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        iv_photo_item.setLayoutParams(params);
        TextView tv_file_name = holder.getView(R.id.tv_file_name);
        ImageView iv_video_mark = holder.getView(R.id.iv_video_mark);
        iv_video_mark.setVisibility(file.getName().endsWith(".jpg")?View.GONE:View.VISIBLE);
        params = (RelativeLayout.LayoutParams) tv_file_name.getLayoutParams();
        tv_file_name.setText(file.getName());
        if (file.exists()){
            Glide.with(mContext)
                    .load(file)
//                    .listener(new RequestListener<File, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            Log.e(TAG, "onException: ", e);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            Log.d(TAG, "onResourceReady: ");
//                            return false;
//                        }
//                    })
                    .centerCrop()
                    .into(iv_photo_item);
        } else {
            Log.e(TAG, "convert: file:"+file.getAbsolutePath()+" not exists !");
        }

        CheckBox cb = holder.getView(R.id.cb_select_photo);
        if (isSelected.get(position)){
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
                ViewHolder holder = (ViewHolder) view.getTag();

        boolean flag = isSelected.get(position);
        Log.d(TAG, "onItemClick: position:"+position+", flag:"+flag);
        if (!flag) {
            flag = true;
            isSelected.put(position, flag);
            Log.d(TAG, "onItemClick: set position:"+position+" selected");
        } else {
            flag = false;
            isSelected.put(position, flag);
            Log.d(TAG, "onItemClick: set position:"+position+" not selected");
        }
        ((CheckBox)holder.getView(R.id.cb_select_photo)).setChecked(flag);
    }

    public ArrayList<String> getSelectedFilePath(){
        ArrayList<String> list = new ArrayList<>();

        File file = null;
        for (int i = 0; i < getCount(); i++) {
            if(isSelected.get(i)){
                file = getItem(i);
                if (file != null){
                    list.add(file.getAbsolutePath());
                } else {
                    Log.e(TAG, "getSelectedFile: select an photo, but file is null, position:"+i);
                }
            }
        }
        return list;
    }
}
