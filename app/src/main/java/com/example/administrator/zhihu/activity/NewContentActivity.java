package com.example.administrator.zhihu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.View.RevealBackgroundView;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/21 0021.
 * @author laoqiang
 * 新详细页面
 */
public class NewContentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webview;
    private RevealBackgroundView revealbackgroundview;
    private int urlid;
    private int[] position;
    private Handler handler;
    private  String contenturl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        revealbackgroundview = (RevealBackgroundView) findViewById(R.id.revealbackgroundview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("享受阅读的乐趣");
         urlid = getIntent().getIntExtra("newid", 0);
        position = getIntent().getIntArrayExtra("STARTPOSITION");
        startRevealbackground();
        initData();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==123){
                    webview.loadUrl(contenturl);
                    webview.setWebViewClient(new WebViewClient());
                }
            }
        };



    }

    private void startRevealbackground() {
        revealbackgroundview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                revealbackgroundview.getViewTreeObserver().removeOnPreDrawListener(this);
                revealbackgroundview.startFromPosition(position);
                return true;
            }
        });


    }

    /**
     * 加载新闻详细内容
     */
    private void initData() {
        HttpUtils.getRequest(Contast.BASEURL + Contast.CONTENT + urlid, new HttpCallableListener() {
            @Override
            public void onScuess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                     contenturl = jsonObject.getString("share_url");
                    handler.sendEmptyMessage(123);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
