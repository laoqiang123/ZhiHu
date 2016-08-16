package com.example.administrator.zhihu.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.fragment.MainFragment;

/**
 * @author laoqiang
 *
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    private SwipeRefreshLayout swiprrefresh;
    private DrawerLayout drawerlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ininView();
        loadLasted();
    }

    /**
     * 初始化view。
     */
    private void ininView() {
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        swiprrefresh = (SwipeRefreshLayout) findViewById(R.id.swiprrefresh);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
      //  getSupportActionBar().setTitle("");
        /**
         * 设置下拉刷新的刷新的图标几种变化颜色
         */
        swiprrefresh.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.
                color.holo_green_light, android.R.color.holo_orange_light, android.R
                .color.holo_red_light);
        swiprrefresh.setOnRefreshListener(this);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerlayout,toolbar,R.string.app_name,R.string.app_name);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     *刷新操作
     */
    @Override
    public void onRefresh() {

    }
    public void setToolBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    public void loadLasted(){
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.in_right,R.anim.out_left).replace(R.id.container,new MainFragment()).commit();

    }
}
