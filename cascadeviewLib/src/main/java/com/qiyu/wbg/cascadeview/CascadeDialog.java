package com.qiyu.wbg.cascadeview;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlong on 12/9/16.
 */
public class CascadeDialog extends DialogFragment implements CascadeCallback,View.OnClickListener{
    private static final String TAG = CascadeDialog.class.getSimpleName();
    private static final int MAX_LEVEL = 3;
    private ContentFragmentAdapter mFragmentPagerAdapter;
    private CascadeData mCascadeData;
    private List<Integer> mSelectedData = new ArrayList<>();
    private int mLevel = 0;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    private TextView mOkBtn;
    private TextView mCancelBtn;
    private CascadeSelectListener mSelectedListener;
    private List<FragmentState> states = new ArrayList<>();

    public CascadeDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Serializable s = bundle.getSerializable(Constants.BUNDLE_KEY_CASCADE_DATA);
            if (s instanceof CascadeData) mCascadeData = (CascadeData) s;
            mLevel = bundle.getInt(Constants.BUNDLE_KEY_LEVEL);
            if (mLevel > MAX_LEVEL) mLevel = MAX_LEVEL;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.windowAnimations = R.style.DialogAnimation;
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = Util.dp2px(getContext(), 300);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFragmentPagerAdapter = new ContentFragmentAdapter(getChildFragmentManager());
        View view = inflater.inflate(R.layout.dialog_main_layout, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mOkBtn = (TextView) view.findViewById(R.id.ok);
        mCancelBtn = (TextView) view.findViewById(R.id.cancel);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);
        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        init();
        return view;
    }

    @Override
    public void onChoose(int level, int position, String navTitle, List<CascadeData> next) {
        int size = states.size();
        int nextLevel = level + 1;

        //清理已经选择的fragment状态
        List<FragmentState> tmp = new ArrayList<>();
        for (int i = 0; i < nextLevel; i++) {
            tmp.add(states.get(i));
        }
        states.clear();
        states.addAll(tmp);


        //刷新fragment选择状态
        if (level < size) {
            FragmentState state = states.get(level);
            if (state != null) {
                state.selectPos = position;
                state.title = navTitle;
            }
        }

        if (nextLevel < mLevel) states.add(new FragmentState(next, -1, nextLevel, "未选择"));

        //记录选择结果
        if(level < mSelectedData.size()){
            mSelectedData.set(level,position);
        }else{
            mSelectedData.add(position);
        }

        refreshFragment();
        mViewPager.setCurrentItem(nextLevel, true);
        mTabs.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ok){
            if (mSelectedListener != null) mSelectedListener.onSelect(mSelectedData);
        }
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    public void setSelectedListener(CascadeSelectListener listener) {
        this.mSelectedListener = listener;
    }

    public void setSelectedData(List<Integer> data) {
        if (data != null) {
            this.mSelectedData = data;
        }
    }

    private void init() {
        if (mSelectedData.isEmpty()) {
            if (mCascadeData != null && mCascadeData.children != null) {
                if (states.isEmpty()) {
                    states.add(new FragmentState(mCascadeData.children, -1, 0, "未选择"));
                }
            }
        } else {
            initWithData();
        }
        refreshFragment();
    }

    private void initWithData() {
        if (mSelectedData == null || mCascadeData == null || !states.isEmpty()) return;
        for (int i = 0; i < mSelectedData.size(); i++) {
            FragmentState state = mCascadeData.getCascadeData(i, mSelectedData, mSelectedData.get(i));
            states.add(state);
        }
    }

    private void refreshFragment() {
        if (mFragmentPagerAdapter != null) {
            mFragmentPagerAdapter.updateData(states);
        }
        if (mTabs != null) {
            mTabs.notifyDataSetChanged();
        }
    }


    class ContentFragmentAdapter extends FragmentPagerAdapter {
        private List<ContentFragment> mFragmentList;
        private FragmentManager mFragmentManager;

        public ContentFragmentAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentManager = fm;
        }

        public void updateData(List<FragmentState> dataList) {
            ArrayList<ContentFragment> fragments = new ArrayList<>();
            for (int i = 0, size = dataList.size(); i < size; i++) {
                FragmentState state = dataList.get(i);
                fragments.add(ContentFragment.instance(state, CascadeDialog.this));
            }
            setFragmentList(fragments);
        }

        private void setFragmentList(List<ContentFragment> fragmentList) {
            if (this.mFragmentList != null) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                for (Fragment f : this.mFragmentList) {
                    fragmentTransaction.remove(f);
                }
                fragmentTransaction.commit();
                mFragmentManager.executePendingTransactions();
            }
            this.mFragmentList = fragmentList;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }


        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            if (mFragmentList == null) return "未选择";
            ContentFragment contentFragment = mFragmentList.get(position);
            if (contentFragment == null) return "未选择";
            return contentFragment.getTitle();
        }
    }

    static class FragmentState {
        public List<CascadeData> currentData;
        public int selectPos;
        public int level;
        public String title;

        public FragmentState(List<CascadeData> currentData, int selectPos, int level, String title) {
            this.currentData = currentData;
            this.selectPos = selectPos;
            this.level = level;
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FragmentState state = (FragmentState) o;

            return level == state.level;

        }

        @Override
        public int hashCode() {
            return level;
        }
    }

    static public class CascadeDialogBuilder {
        private CascadeDialog dialog;
        private Bundle bundle;

        public CascadeDialogBuilder() {
            dialog = new CascadeDialog();
        }

        public void setLevel(int level) {
            bundle.putInt(Constants.BUNDLE_KEY_LEVEL, level);
        }

        public CascadeDialog build() {
            dialog.setArguments(bundle);
            return dialog;
        }
    }
}
