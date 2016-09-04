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
    private long firstime;
    private FrameLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ininView();
        loadLasted();
    }
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
                SaveUtils.saveBoolean(ApplicationUtil.getContext(), "LIGHT", islight);
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
      //  getSupportActionBar().setTitle("");
         islight = SaveUtils.getBoolean(ApplicationUtil.getContext(),"LIGHT");
            updateTheme();


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
    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }
    /**
     *刷新操作
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
    public void setToolBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    public void loadLasted(){
                FragmentManager fm = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                mf = new MainFragment();
               ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).replace(R.id.container,mf,"main").commit();
                fm.executePendingTransactions();
                setRefreshtag("main");//设置今日热文的刷新。
                tag = false;
                swiprrefresh.setRefreshing(false);

    }
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
    /*public void setId(String id){
    }*/

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
        toolbar.setBackgroundColor(getResources().getColor(islight? R.color.light_toolbar : R.color.dark_toolbar));
        setStatusBarColor(getResources().getColor(islight?R.color.light_toolbar:R.color.dark_toolbar));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();不能加这个，这个是继承父类，直finish，就看不到下面的效果。
        long secondtime =System.currentTimeMillis();
        if(secondtime-firstime>2000){
            Snackbar snackbar = Snackbar.make(container,"在按一次退出",Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
            firstime = secondtime;
        }else{
            finish();
        }
    }
}
