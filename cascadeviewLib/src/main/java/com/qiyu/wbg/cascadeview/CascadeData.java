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
    private int depth;


    public CascadeDialog.FragmentState getCascadeData(int level, List<Integer> select, int selectPos){
        List<CascadeData> dataList = getChildren(level,select,this);
        String title = dataList.get(selectPos).content;
        return new CascadeDialog.FragmentState(dataList,selectPos,level,title);
    }



    public List<CascadeData> getChildren(int level,List<Integer> select,CascadeData data){
        if(level == data.depth){
            return data.children;
        }else{
            return getChildren(level,select,data.children.get(select.get(level-1)));
        }
    }

    public void calDepth(){
        trace(0,this);
    }

    private void trace(int level,CascadeData data){
        data.depth = level;
        if(data.children == null) {
            return;
        }
        for(CascadeData it : data.children){
            trace(level+1,it);
        }
    }


}
