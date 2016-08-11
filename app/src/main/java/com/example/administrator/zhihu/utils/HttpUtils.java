package com.example.administrator.zhihu.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/7 0007.
 * @author laoqiang
 */


public class HttpUtils {
    /**
     * @return 网络是否连接上
     */
    public static boolean isNetWorkConnected() {
        ConnectivityManager cm = (ConnectivityManager) ApplicationUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            boolean flag = info.isAvailable();
            return flag;
        }
        return false;
    }

    /**
     *
     * @param address  网络请求的url
     * @param listener  监听回调接口
     */
    public static void getRequest(final String address, final HttpCallableListener listener) {
          new Thread(new Runnable() {
              @Override
              public void run() {
                  try {
                      URL url = new URL(address);
                      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                      httpURLConnection.setRequestMethod("GET");
                      httpURLConnection.setReadTimeout(5000);
                      httpURLConnection.setConnectTimeout(5000);
                      httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                      InputStream in = httpURLConnection.getInputStream();
                      BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                      StringBuilder sb = new StringBuilder();
                      String line = null;
                      while ((line = bf.readLine()) != null) {
                          sb.append(line);
                      }
                      if (listener != null) {
                          listener.onScuess(sb.toString());
                      }

                  } catch (MalformedURLException e) {
                      e.printStackTrace();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              }
          }).start();


      }
          public static  void getImage(final String address, final HttpImageCallableListener listener){
              new Thread(new Runnable() {
          @Override
          public void run() {
              ByteArrayOutputStream bos = null;
              try {
                  URL url= new URL(address);
                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                  connection.setRequestMethod("GET");
                  connection.setReadTimeout(5000);
                  connection.setConnectTimeout(5000);
                  connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                  InputStream in = connection.getInputStream();
                 bos= new ByteArrayOutputStream();
                  byte[] b = new byte[1024];
                  int length = 0;
                  while((length=in.read(b))!=-1){
                      bos.write(b,0,length);
                  }
                  if(listener!=null){
                      listener.onScuess(bos.toByteArray());
                  }

              } catch (MalformedURLException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }finally {
                  try {
                      bos.close();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
      }).start();
    }
}
