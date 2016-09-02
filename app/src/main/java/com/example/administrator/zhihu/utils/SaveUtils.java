package com.example.administrator.zhihu.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/8/12 0012.
 */
public class SaveUtils {
    private  static  SharedPreferences sp ;
    public static  void saveString(Context context,String key,String value){
        if(sp==null){
            sp = context.getSharedPreferences("zhihu.txt",Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static  String getString(Context context,String key){
        if(sp==null){
            sp = context.getSharedPreferences("zhihu.txt",Context.MODE_PRIVATE);
        }
        String value = sp.getString(key, "");
        return value;
    }
    public static  void saveBoolean(Context context,String key,Boolean value){
        if(sp==null){
            sp = context.getSharedPreferences("zhuhu.txt",Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static boolean getBoolean(Context context,String key){
        if(sp==null){
            sp = context.getSharedPreferences("zhuhu.txt",Context.MODE_PRIVATE);
        }
        Boolean value = sp.getBoolean(key,true);
        return  value;

    }




}
