package com.qiyu.wbg.cascadeview;

import java.util.List;

/**
 * Created by chenlong on 12/9/16.
 */

interface CascadeCallback {
    void onChoose(int level,int position,String navTitle,List<CascadeData> next);
}
