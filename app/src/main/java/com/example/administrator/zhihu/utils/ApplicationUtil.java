package com.example.administrator.zhihu.utils;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2016/8/7 0007.
 */
public class ApplicationUtil  extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化网络框架
        ImageLoaderConfiguration configuration =new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);

    }
    public static  Context getContext(){

        return  context;
    }

}
