package com.example.administrator.zhihu.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.utils.ActivityUtils;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpImageCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/5 0005.
 * @author laoqiang
 */
public class SplashActivity extends AppCompatActivity {
    ImageView iv;
    Bitmap bitmap;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_layout);
        iv = (ImageView) findViewById(R.id.iv);
        initImage();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==123){
                    startActivity(ApplicationUtil.getContext(), MainActivity.class);
                }
            }
        };

    }

    /**
     * 初始化应用启动的照片。第一次从本地获取，其他时候网络获取。并保存本地文件。
     * 启动照片的动画效果。
     */
    private void initImage() {
        File dir =getFilesDir();
        final File imagefile = new File(dir,"start.jpg");
        if(imagefile.exists()){
            iv.setImageBitmap(BitmapFactory.decodeFile(imagefile .getAbsolutePath()));
        }else{
                iv.setImageResource(R.mipmap.start);
        }
        ScaleAnimation animation = new ScaleAnimation(1.0f,1.3f,1.0f,1.3f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setFillAfter(true);
        animation.setDuration(5000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (HttpUtils.isNetWorkConnected()) {
                    HttpUtils.getRequest(Contast.BASEURL + Contast.START, new HttpCallableListener() {
                        @Override
                        public void onScuess(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("tag", response + "数据");
                                String imageurl = jsonObject.getString("img");
                                Log.d("tag", imageurl + "url");
                                HttpUtils.getImage(imageurl, new HttpImageCallableListener() {
                                    @Override
                                    public void onScuess(byte[] b) {
                                        saveImage(imagefile, b);
                                     //   handler.sendEmptyMessage(123);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(ApplicationUtil.getContext(), MainActivity.class);

                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure() {
                                        handler.sendEmptyMessage(123);

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Exception e) {
                            handler.sendEmptyMessage(123);

                        }
                    });
                } else {
                    ActivityUtils.shortToast(ApplicationUtil.getContext(), "网络请求有误");
                    startActivity(ApplicationUtil.getContext(), MainActivity.class);


                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Log.d("tag", "animation");
        iv.startAnimation(animation);



    }

    /**
     *
     * @param file 文件对象
     * @param b   字节数组
     *  保存文件
     */

    public void saveImage(File file,byte[] b){
      if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = null;
        try {
             fos = new FileOutputStream(file);
                fos.write(b);
                fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     *
     * @param context  上下文
     * @param c 跳转的类。
     *  实现了activity 之间的跳转，并且带动画效果。
     */
    public   void startActivity(Context context,Class c) {
        Intent intent = new Intent(context, c);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
