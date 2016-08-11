package com.example.administrator.zhihu.utils;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public interface HttpImageCallableListener {
    void onScuess(byte[] b);
    void onFailure();
}
