package com.example.administrator.zhihu.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class ActivityUtils {
    /**
     *
     * @param context 上下文
     * @param c
     */
    public static  void startActivity(Context context,Class c){
        Intent intent = new Intent(context,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
    public static  void shortToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

    }
}
