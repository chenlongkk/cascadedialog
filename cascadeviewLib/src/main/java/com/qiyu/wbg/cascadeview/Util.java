package com.qiyu.wbg.cascadeview;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by chenlong on 12/9/16.
 */

public class Util {
    public static int dp2px(Context context,int dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }


}
