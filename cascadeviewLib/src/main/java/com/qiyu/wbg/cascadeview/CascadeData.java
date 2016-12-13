package com.qiyu.wbg.cascadeview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlong on 12/9/16.
 */

public class CascadeData implements Serializable {
    public String id;
    public String content;
    public List<CascadeData> children;


    public CascadeDialog.FragmentState getCascadeData(int level, List<Integer> select, int selectPos){
        String title = "未选择";
        List<CascadeData> dataList = new ArrayList<>();
        switch (level){
            case 0:
                title = children.get(selectPos).content;
                dataList = children;
                break;
            case 1:
                title = children.get(select.get(0)).children.get(selectPos).content;
                dataList = children.get(select.get(0)).children;
                break;
            case 2:
                title = children.get(select.get(0)).children.get(select.get(1)).children.get(selectPos).content;
                dataList = children.get(select.get(0)).children.get(select.get(1)).children;
                break;
        }
        return new CascadeDialog.FragmentState(dataList,selectPos,level,title);
    }

}
