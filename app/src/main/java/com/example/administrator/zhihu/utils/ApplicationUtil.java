package com.example.administrator.zhihu.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/8/7 0007.
 */
public class ApplicationUtil  extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static  Context getContext(){

        return  context;
    }
}
