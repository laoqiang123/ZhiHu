package com.example.administrator.zhihu.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.activity.MainActivity;
import com.example.administrator.zhihu.activity.NewContentActivity;
import com.example.administrator.zhihu.adapter.NewAdapter;
import com.example.administrator.zhihu.adapter.NewTitleAdapter;
import com.example.administrator.zhihu.bean.NewBean;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;
import com.example.administrator.zhihu.utils.SaveUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/13 0013.
 * @author  laoqiang
 * 主界面今日热点新闻的页面展示
 */
public class MainFragment extends Fragment implements AbsListView.OnScrollListener {
    private ListView listview;
    private Banner banner;
    private List<String> titlelist = new ArrayList<>();//存放banner标题
    private List<String> imageurllist = new ArrayList<>();//存放banner图片url
    private List<Integer> urlid = new ArrayList<>();//存放banner的详细条目的id
    private NewBean nb;
    private Handler handler;
    private List<NewBean> list = new ArrayList<>();
    private List<NewBean> cachelist = new ArrayList<>();//缓存数据存放newbean
    private NewAdapter adapter;
    private String date;
    private boolean lookmore;//是否可以查看更多，false不可以，true可以


    public MainFragment() {
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 通过mainactivity中的方法暴露出来根据fragment来设置toolbar标题.
         */
        ((MainActivity)getActivity()).setToolBarTitle("今日热闻");
        View v =inflater.inflate(R.layout.main_fragment,container,false);
        listview = (ListView) v.findViewById(R.id.listview);
        View header = inflater.inflate(R.layout.banner_layout,listview,false);
        banner = (Banner) header.findViewById(R.id.banner);
        listview.setOnScrollListener(this);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);//设置banner什么格式
        banner.setIndicatorGravity(Gravity.RIGHT);//设置指示器位置
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                /**
                 * 设置banner图片和标题
                 */
                if(msg.what==123){
                    banner.setBannerTitleList(titlelist);
                    banner.setImages(imageurllist);//在使用图片轮播使用设置banner，一定要在设置图片之前。
                }
                /**
                 * 设置新闻条目数据
                 */
                if(msg.what==234){
                    listview.setAdapter(adapter);
                    updateTheme(SaveUtils.getBoolean( "LIGHT"));
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int[] postion = new int[2];
                            view.getLocationOnScreen(postion);
                            postion[0] = view.getWidth() / 2;
                            Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                            intent.putExtra("newid", list.get(position - 1).getId());
                            intent.putExtra("STARTPOSITION", postion);
                            startActivity(intent);
                            String readinformation = SaveUtils.getString("READ");
                            String[] readarray = readinformation.split(",");
                            StringBuilder sb = new StringBuilder();
                            if (readarray.length > 200) {//删除很早保存的阅读记录。
                                for (int i = 100; i < readarray.length; i++) {
                                    sb.append(readarray[i] + ",");
                                }
                                readinformation = sb + readinformation;
                            }
                            if (!readinformation.contains(list.get(position - 1).getId() + "")) {
                                readinformation = readinformation + list.get(position - 1).getId() + ",";
                            }
                            SaveUtils.saveString( "READ", readinformation);
                            TextView tv_show = (TextView) view.findViewById(R.id.tv_show);
                            tv_show.setTextColor(getResources().getColor(R.color.grey2));
                            getActivity().overridePendingTransition(0, 0);//设置没有动画
                        }
                    });

                }
                /**
                 * 添加缓存条目到listview。
                 */
                if(msg.what==345) {
                    adapter.addData(cachelist,parseDate(date));
                    Log.d("tag", cachelist.size() + "size");
                    lookmore=false;
                    cachelist.clear();//如果不清空，为造成两次数据的重叠。
                }
            }
        };
        initData();
        adapter = new NewAdapter(list,ApplicationUtil.getContext());
        banner.setImages(imageurllist);
        listview.addHeaderView(header);//我们是把轮播图片作为listview的头布局。
        /**
         * banner跳转详情页面
         */
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void OnBannerClick(View view, int position) {
                int[] postion = new int[2];
                view.getLocationOnScreen(postion);
                postion[0] = view.getWidth() / 2;
                Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                intent.putExtra("newid", urlid.get(position - 1));
                intent.putExtra("STARTPOSITION", postion);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);//设置没有动画
            }
        });

        return  v;
    }

    /**
     * 初始化主界面数据。
     */
    private void initData() {
         if(HttpUtils.isNetWorkConnected()){
             /**
              * 请求banner图片和title
               */
            HttpUtils.getRequest(Contast.BASEURL + Contast.LATESTNEWS, new HttpCallableListener() {

            @Override
            public void onScuess(String response) {
                parseTopJson(response);
                SQLiteDatabase sqLiteDatabase = ((MainActivity)getActivity()).getCacheOpenHelper().getWritableDatabase();
                Cursor cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{Contast.FIRST_TOP_DATE}, null, null, null);
                String topjson = null;
                if(cursor.moveToNext()){
                    topjson =  cursor.getString(cursor.getColumnIndex("json"));
                    if(response.equals(topjson)){
                        Log.d("tag","banner数据已经缓存");
                    }else{
                        Log.d("tag","banner其他时候插入数据");
                        ContentValues values = new ContentValues();
                        values.put("date", Contast.FIRST_TOP_DATE);
                        values.put("json", response);
                        long id = sqLiteDatabase.insert("CacheList", null, values);//可以根据id判断值是否插入成功。
                        if (id == -1) {
                            Log.d("tag", "插入失败");
                        }
                    }
                }else {
                    ContentValues values = new ContentValues();
                    values.put("date", Contast.FIRST_TOP_DATE);
                    values.put("json", response);
                    long id = sqLiteDatabase.insert("CacheList", null, values);//可以根据id判断值是否插入成功。
                    Log.d("tag", "banner第一次插入");
                    if (id == -1) {
                        Log.d("tag", "插入失败");
                    }
                }
                sqLiteDatabase.close();
                handler.sendEmptyMessage(123);

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
             /**
              * 请求今日热闻的数据
              */
        HttpUtils.getRequest(Contast.BASEURL + Contast.LATESTNEWS, new HttpCallableListener() {
            @Override
            public void onScuess(String response) {
                parseJson(response);
                SQLiteDatabase sqLiteDatabase = ((MainActivity)getActivity()).getCacheOpenHelper().getWritableDatabase();
                Cursor cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{Contast.FIRST_DATE}, null, null, null);
                String json = null;
                if(cursor.moveToNext()) {
                    json =  cursor.getString(cursor.getColumnIndex("json"));
                    if(!response.equals(json)){
                        ContentValues values = new ContentValues();
                        values.put("date", Contast.FIRST_DATE);//第一次请求，没有时间，所以要一个数字，
                        // 这个数字最好大，否则后面被删了
                        values.put("json", response);
                        sqLiteDatabase.insert("CacheList", null, values);
                        Log.d("tag"," 今日其他时候处插入");
                    }else{
                        Log.d("tag","今日热点数据已经缓存");
                    }
                }else {
                    ContentValues values = new ContentValues();
                    values.put("date", Contast.FIRST_DATE);//第一次请求，没有时间，所以要一个数字，
                    // 这个数字最好大，否则后面被删了
                    values.put("json", response);
                    sqLiteDatabase.insert("CacheList", null, values);
                    Log.d("tag","今日热文第一次插入");
                }
                sqLiteDatabase.close();
                handler.sendEmptyMessage(234);
            }

            @Override
            public void onFailure(Exception e) {

            }
            });
         } else{
             /**
              *把今日热闻json保存到缓存的数据库中
              */
             SQLiteDatabase sqLiteDatabase = ((MainActivity)getActivity()).getCacheOpenHelper().getWritableDatabase();
             Cursor cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{Contast.FIRST_DATE}, null, null, null);
                 String json = null;
                 if(cursor.moveToNext()){
                     json =  cursor.getString(cursor.getColumnIndex("json"));
                     parseJson(json);
             }else{
                 lookmore = false;
             }
             handler.sendEmptyMessage(234);
             /**
              * 把今日热闻bannerjson数据保存到数据库中
              */
             cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{Contast.FIRST_TOP_DATE}, null, null, null);
             String topjson = null;
             if(cursor.moveToNext()){
                 topjson =  cursor.getString(cursor.getColumnIndex("json"));
                 parseTopJson(topjson);
             }

             cursor.close();
             handler.sendEmptyMessage(123);
             sqLiteDatabase.close();
         }
    }
    /**
     * 缓存分页加载
     */
    public void loadMore(String url){
        lookmore = true;
        if(HttpUtils.isNetWorkConnected()){
        HttpUtils.getRequest(url, new HttpCallableListener() {
            /**
             * 请求缓存数据有个注意的地方，20160905，请求这个时间实际请求出来是20160904数据。
             * @param response
             */
            @Override
            public void onScuess(String response) {
                SQLiteDatabase sqLiteDatabase = ((MainActivity)getActivity()).getCacheOpenHelper().getWritableDatabase();
                Cursor cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{date}, null, null, null);
                String json = null;
                if(cursor.moveToNext()) {
                    json =  cursor.getString(cursor.getColumnIndex("json"));
                    if(!response.equals(json)){
                        ContentValues values = new ContentValues();
                        values.put("date", date);//第一次请求，没有时间，所以要一个数字，
                        // 这个数字最好大，否则后面被删了
                        values.put("json", response);
                        sqLiteDatabase.insert("CacheList", null, values);
                        Log.d("tag", "有数据,更多数据已经缓存");
                    }else{
                        Log.d("tag","更多数据已经缓存");
                    }
                }else {
                    ContentValues values = new ContentValues();
                    values.put("date", date);
                    Log.d("tag","更多加载的时间"+date);
                    values.put("json", response);
                    sqLiteDatabase.insert("CacheList", null, values);
                    Log.d("tag", "更多数据第一次缓存");
                }
                sqLiteDatabase.close();
                parseBeforeJson(response);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        }
        else{
            SQLiteDatabase sqLiteDatabase = ((MainActivity)getActivity()).getCacheOpenHelper().getWritableDatabase();
            Cursor cursor =  sqLiteDatabase.query("CacheList", new String[]{"json"}, "date=?", new String[]{date}, null, null, null);
            String json = null;
            if(cursor.moveToNext()){
                json =  cursor.getString(cursor.getColumnIndex("json"));
                parseBeforeJson(json);
            }else{
                sqLiteDatabase.delete("CacheList", "date<?", new String[]{date});
                lookmore = false;
                Snackbar snackbar  = Snackbar.make(listview,"没有更多的离线内容！",Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
            cursor.close();
            sqLiteDatabase.close();
        }


    }


    /**
     *
     * @param result  热点图片轮播数据。
     */
    public void parseTopJson(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("top_stories");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String imageurl = jsonObject1.getString("image");
                String title =jsonObject1.getString("title");
                int id = jsonObject1.getInt("id");
                titlelist.add(title);
                imageurllist.add(imageurl);
                urlid.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param result 主界面json数据
     */
    public void parseJson(String  result){
        try {
            JSONObject jsonObject = new JSONObject(result);
             date = jsonObject.getString("date");
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for(int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                    String images = jsonArray1.getString(0);
                    String title = jsonObject1.getString("title");
                    int id = jsonObject1.getInt("id");
                    int type = jsonObject1.getInt("type");
                    nb = new NewBean();
                    nb.setImages(images);
                    nb.setId(id);
                    if(i==0){
                        type = Contast.TOPIC;
                    }
                    nb.setType(type);
                    nb.setTitle(title);
                    list.add(nb);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析缓存的json
     * @param result
     */
    public void parseBeforeJson(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            date = jsonObject.getString("date");
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                String images = jsonArray1.getString(0);
                String title = jsonObject1.getString("title");
                int id = jsonObject1.getInt("id");
                int type = jsonObject1.getInt("type");
                NewBean nb = new NewBean();
                nb.setImages(images);
                nb.setId(id);
                if(i==0){
                    type = Contast.TOPIC;
                }
                nb.setType(type);
                nb.setTitle(title);
                cachelist.add(nb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(345);



    }

    public void updateTheme(boolean flag){
        adapter.setIslight(flag);
        listview.setAdapter(adapter);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 这样的话,在里面直接上一个listview会和swiperefreshlayout的下拉刷新冲突,
     * 怎么样解决这个问题呢?先说一下思路,在listview向下各种滚动的过程中,可以
     * 加上一个OnScrollListener,监听listview是否滑到了最顶端的一个item,如果在
     * 最顶端,就将swiperefreshlayout设置成setEnabled(true),如果不再最顶端,就
     * 设置成setEnabled(false),这样就可以阻止冲突了~~~~~~其他的也可以模仿这种
     * 处理形式..
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(listview!=null&&listview.getChildCount()>0){
               if(listview.getFirstVisiblePosition()==0&&listview.getChildAt(0).getTop()==0){
                   ((MainActivity)getActivity()).setSwiprrefresh(true);
                   adapter.arriveTop(true);
               }else{
                   ((MainActivity)getActivity()).setSwiprrefresh(false);
               }


            /**
             * listview滑动底部触发更多加载
             */
                if(firstVisibleItem+visibleItemCount == totalItemCount&&!lookmore) {
                    loadMore(Contast.BASEURL + Contast.BEFORE + "/" + date);
                    Log.d("tag","现在请求的时间"+date);

                }

        }

    }

    /**
     * 时间格式 : 20160904
     * @param result
     * @return
     */
    public String parseDate(String result){
        String year = result.substring(0,4).toString();
        String month = result.substring(4,6);
        String day = result.substring(6,8);
        return  year+"年"+month+"月"+day+"日";
    }
}
