package com.qiyu.wbg.cascadeview;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlong on 12/9/16.
 */
public class ContentFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = ContentFragment.class.getSimpleName();
    private CascadeCallback mCallback;
    private int mLevel;
    private ListView mListView;
    private int selectedPos = -1;
    private List<CascadeData> mDataSource;
    private ContentAdapter mContentAdapter;
    private String mTitle;

    public static ContentFragment instance(CascadeDialog.FragmentState state, CascadeCallback cascadeCallback){
        ContentFragment contentFragment = new ContentFragment();
        contentFragment.setData(state);
        contentFragment.setCascadeCallback(cascadeCallback);
        return contentFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_content_layout,container,false);
        mListView = (ListView)view.findViewById(R.id.list);
        mContentAdapter = new ContentAdapter();
        mListView.setAdapter(mContentAdapter);
        mListView.setOnItemClickListener(this);
        mContentAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPos = position;
        CascadeData selectData = mDataSource.get(position);
        if(mCallback!=null){
            mTitle = selectData.content;
            mCallback.onChoose(mLevel,position,selectData.content,selectData.children);
        }
        mContentAdapter.notifyDataSetChanged();
    }

    public void setCascadeCallback(CascadeCallback cascadeCallback){
        this.mCallback = cascadeCallback;

    }

    public void setData(CascadeDialog.FragmentState state){
        this.mLevel = state.level;
        this.mDataSource = state.currentData;
        this.selectedPos = state.selectPos;
        this.mTitle = state.title;
        if(mContentAdapter!=null) mContentAdapter.notifyDataSetChanged();

    }

    public String getTitle(){
        return mTitle == null ? "未选择":mTitle;
    }

    class ContentAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mDataSource == null ? 0 : mDataSource.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView contentView;
            if(view == null){
                contentView = new TextView(getContext());
            }else{
                contentView = (TextView)view;
            }
            int padding = Util.dp2px(getContext(),20);
            contentView.setPadding(padding,padding,padding,padding);
            contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            contentView.setTextColor(Color.parseColor("#333333"));
            contentView.setText(mDataSource.get(i).content);
            return contentView;
        }
    }
}
