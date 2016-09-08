package com.example.administrator.zhihu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2016/8/12 0012.
 */
public class SaveUtils {
    private  static  SharedPreferences sp =ApplicationUtil.getContext().getSharedPreferences("zhihu.txt",Context.MODE_PRIVATE);;
    public static  void saveString(String key,String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
       boolean flag =  editor.commit();
        Log.d("tag12",flag+"是否成功写入");
    }
    public static  String getString(String key){
        String value = sp.getString(key, "");
        return value;
    }
    public static  void saveBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static boolean getBoolean(String key){
        Boolean value = sp.getBoolean(key,true);
        return  value;

    }




}
