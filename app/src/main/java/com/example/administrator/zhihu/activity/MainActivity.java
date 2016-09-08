package com.example.administrator.zhihu.activity;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.db.CacheOpenHelper;
import com.example.administrator.zhihu.fragment.MainFragment;
import com.example.administrator.zhihu.fragment.MenuFragment;
import com.example.administrator.zhihu.fragment.NewFragment;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.SaveUtils;

/**
 * @author laoqiang
 *
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    private SwipeRefreshLayout swiprrefresh;
    private DrawerLayout drawerlayout;
    private String refreshtag;//用来标记刷新今日热文，还是其他主题刷新。
    private int id;
    private String titlecontent;
    public boolean islight;//这个布尔值设置白天或者晚上模式，true为白天,false为晚上.
    private NewFragment nf;
    private MainFragment mf;
    private boolean tag;
    private long firstime;//用来记录退出的时候，第一次按下去的时间。
    private FrameLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ininView();
        loadLasted();
    }

    /**
     *
     * @return  数据库操作对象
     */
    public CacheOpenHelper getCacheOpenHelper(){
        CacheOpenHelper openHelper = new CacheOpenHelper(ApplicationUtil.getContext());
        return openHelper;
    }
    /**
     * 菜单初始化
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        /**
         * 如果在这不进行这个操作，你如果夜间保存，退出程序，
         * 下次进入程序，菜单条目上的内容还是你最初默认的日间模式
         */
        if(islight){
            menu.getItem(0).setTitle("夜间模式");
        }else{
            menu.getItem(0).setTitle("日间模式");
        }
        return true;
    }

    /**
     * 菜单每个条目操作
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.model:
                if(item.getTitle().equals("日间模式")) {
                    item.setTitle("夜间模式");
                    islight = true;
                }else if(item.getTitle().equals("夜间模式")){
                    item.setTitle("日间模式");
                    islight = false;
                }
                SaveUtils.saveBoolean("LIGHT", islight);
                updateTheme();
                ((MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_fragment)).updateTheme(islight);
                if(tag==false) {
                    ((MainFragment) getSupportFragmentManager().findFragmentByTag("main")).updateTheme(islight);
                }else{
                    ((NewFragment)getSupportFragmentManager().findFragmentByTag("news")).updateTheme(islight);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getRefreshtag() {
        return refreshtag;
    }

    public void setRefreshtag(String refreshtag) {
        this.refreshtag = refreshtag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlecontent() {
        return titlecontent;
    }

    public void setTitlecontent(String titlecontent) {
        this.titlecontent = titlecontent;
    }

    /**
     * 设置是否显示下拉刷新。
     * @param flag
     */
    public void setSwiprrefresh(boolean flag){
        swiprrefresh.setEnabled(flag);
    }

    /**
     * 初始化view。
     */
    private void ininView() {
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        swiprrefresh = (SwipeRefreshLayout) findViewById(R.id.swiprrefresh);
        container = (FrameLayout) findViewById(R.id.container);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        //尽量不要用toolbar设置标题，然后
        islight = SaveUtils.getBoolean("LIGHT");
        updateTheme();
        /**
         * 设置下拉刷新的刷新的图标几种变化颜色
         */
        swiprrefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.
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
     * 刷新分，今日热闻刷新，结合其他主题下数据的刷新。
     */
    @Override
    public void onRefresh() {
        swiprrefresh.setRefreshing(true);
            if (getRefreshtag().equals("main")) {
                loadLasted();
            } else if (getRefreshtag().equals("item")) {
                loadThemeLasted();
            }


        // SwipeRefreshLayout布局中目前只能包含一个子布局，
        // 使用侦听机制来通知刷新事件。例如当用户使用下拉手势时，
        // SwipeRefreshLayout会触发OnRefreshListener，然后刷新事
        // 件会在onRefresh()方法中进行处理。当需要结束刷新的时候，
        // 可以调用setRefreshing(false)。如果要禁用手势和进度动画，
        // 调用setEnabled(false)即可。
    }

    /**
     * 设置toolbar标题。
     * @param title
     */
    public void setToolBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    /**
     * 加载最新今日热闻页面
     */
    public void loadLasted(){
                FragmentManager fm = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                mf = new MainFragment();
                ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).replace(R.id.container,mf,"main").commit();//fragment切换动画
                fm.executePendingTransactions();
                setRefreshtag("main");//设置今日热文的刷新。
                tag = false;
                swiprrefresh.setRefreshing(false);

    }

    /**
     * 加载最新的其他主题的新闻页面
     */
    public void loadThemeLasted(){
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        nf = new NewFragment();
        ft.setCustomAnimations(R.anim.slide_in_from_right,R.anim.slide_out_to_left).
                replace(R.id.container, nf,"news").commit();
        fm.executePendingTransactions();
        setRefreshtag("item");//设置其他主题的刷新
        ((NewFragment)getSupportFragmentManager().findFragmentByTag("news")).updateTheme(islight);
        tag = true;
        swiprrefresh.setRefreshing(false);
    }
    /**
     * 关闭侧滑
     */
    public void closeDrawLayout(){
            drawerlayout.closeDrawers();
    }

    /**
     * 跟换主题
     */
    public void updateTheme(){
        toolbar.setBackgroundColor(getResources().getColor(islight ? R.color.light_toolbar : R.color.dark_toolbar));
        //这个表达式，就是相当于if else ，如果前面的布尔值，true就是取第一个值，否则就是第二个。
    }

    /**
     * 返回键操作
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();不能加这个，这个是继承父类，直finish，就看不到下面的效果。
        long secondtime =System.currentTimeMillis();
        /**
         * 实现连续点击两次退出。
         */
        if(secondtime-firstime>2000){
            Snackbar snackbar = Snackbar.make(container,"在按一次退出",Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();//一定要show，否则不显示。
            firstime = secondtime;
        }else{
            finish();
        }
    }
}
