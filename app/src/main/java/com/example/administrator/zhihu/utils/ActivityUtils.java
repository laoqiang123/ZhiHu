package com.example.administrator.zhihu.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class ActivityUtils {


    public static  void shortToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

    }
}
