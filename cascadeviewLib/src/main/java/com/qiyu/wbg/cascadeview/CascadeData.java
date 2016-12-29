package com.qiyu.wbg.cascadeview;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenlong on 12/9/16.
 */

public class CascadeData implements Serializable {
    @Expose
    public String id;
    @Expose
    public String name;
    @Expose
    public List<CascadeData> children;
    private int depth;


    public CascadeDialog.FragmentState getCascadeData(int level, List<Integer> select, int selectPos){
        List<CascadeData> dataList = getChildren(0,level,select,this);
        String title = dataList.get(selectPos).name;
        return new CascadeDialog.FragmentState(dataList,selectPos,level,title);
    }



    public List<CascadeData> getChildren(int depth,int level,List<Integer> select,CascadeData data){
        if(level == data.depth){
            return data.children;
        }else{
            return getChildren(depth+1,level,select,data.children.get(select.get(depth)));
        }
    }

    public List<Integer>calDepth(CascadeData data){
        List<Integer> res = new ArrayList<>();
        trace(res,0,this,data);
        return res;
    }

    private void trace(List<Integer> res,int level,CascadeData data,CascadeData selected){
        if(data == null) return;
        data.depth = level;
        if(data.children == null) {
            return;
        }
        if(selected == null){
            return;
        }
        for(int i = 0;i<data.children.size();i++){
            CascadeData it = data.children.get(i);
            if(TextUtils.equals(it.id,selected.id)){
                res.add(i);
            }
            if(selected.children == null) {
                trace(res,level+1,it,null);
            }else{
                trace(res,level+1,it,selected.children.get(0));
            }
        }
    }

    public CascadeData calResult(List<Integer> selected){
        CascadeData res = new CascadeData();
        reTrace(0,res,this,selected);
        return res;
    }

    public void reTrace(int depth,CascadeData res,CascadeData data,List<Integer> selected){
        if(data == null||data.children == null) return ;
        CascadeData selecedData = data.children.get(selected.get(depth));
        res.id = selecedData.id;
        res.name = selecedData.name;
        if(depth + 1 < selected.size()){
            res.children = new ArrayList<>();
            CascadeData next = new CascadeData();
            res.children.add(next);
            reTrace(depth+1,next,selecedData,selected);
        }
    }

    public String getTextString(){
        StringBuilder sb = new StringBuilder();
        append(sb,this);
        return sb.toString().substring(0,sb.length()-1);
    }
    public void append(StringBuilder sb,CascadeData data){
        if(data == null ) return ;
        sb.append(data.name).append("-");
        if(data.children != null){
            append(sb,data.children.get(0));
        }
    }

}
