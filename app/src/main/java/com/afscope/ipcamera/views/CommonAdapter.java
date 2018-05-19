package com.afscope.ipcamera.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {

    //为了让子类访问，于是将属性设置为protected
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int layoutId; //不同的ListView的item布局可能不同，所以要把布局单独提取出来

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        if (mDatas != null){
            return mDatas.size();
        } else {
            return 0;
        }
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //初始化ViewHolder,使用通用的ViewHolder ，layoutId就是单个item的布局
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);

        convert(holder, getItem(position), position);

        View view = holder.getConvertView();

	    /* 自定义 -- Begin */
//        if (position == activatedItem) {
//            Log.d("LvAdapter2", "activatedItem:" + position);
//            view.setBackgroundColor(Color.parseColor("#8DB6CD"));
//        }
	    /* 自定义 -- End */

        return view;
    }

    //将convert方法公布出去
    protected abstract void convert(ViewHolder holder, T t, int position);



    /* 自定义 -- Begin */
//    private int activatedItem = -1;
//
//    public void setItemSelected(int position) {
//        activatedItem = position;
//        notifyDataSetChanged();
//    }
	/* 自定义 -- End */
}
