package com.example.administrator.zhihu.utils;

/**
 * Created by Administrator on 2016/8/8 0008.
 * @author laoqiang
 * 关于网络请求的回调接口
 */
public interface HttpCallableListener {
    void onScuess(String response);
    void onFailure(Exception e);
}
