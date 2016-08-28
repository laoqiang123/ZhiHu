package com.example.administrator.zhihu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
public class MainFragment extends Fragment {
    private ListView listview;
    private Banner banner;
    private List<String> titlelist = new ArrayList<>();//存放标题
    private List<String> imageurllist = new ArrayList<>();//存放图片url
    private List<Integer> urlid = new ArrayList<>();//存放banner的详细条目的id
    private NewBean nb;
    private  Handler handler;
    private Handler handler1;
    private List<NewBean> list = new ArrayList<>();

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
        initData();
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);//设置banner什么格式
        banner.setIndicatorGravity(Gravity.RIGHT);//设置指示器位置
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==123){
                    banner.setBannerTitleList(titlelist);
                    banner.setImages(imageurllist);//上面所有的设置一定要在设置图片之前。

                }
            }
        };
        banner.setImages(imageurllist);listview.addHeaderView(header);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void OnBannerClick(View view, int position) {
                int[] postion = new int[2];
                view.getLocationOnScreen(postion);
                postion[0] = view.getWidth()/2;
                Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                intent.putExtra("newid", urlid.get(position - 1));
                intent.putExtra("STARTPOSITION",postion);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);//设置没有动画
            }
        });
        final NewAdapter adapter = new NewAdapter(list,ApplicationUtil.getContext());
        handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==234){
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int[] postion = new int[2];
                            view.getLocationOnScreen(postion);
                            postion[0] = view.getWidth()/2;
                            Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                            intent.putExtra("newid", list.get(position-1).getId());
                            intent.putExtra("STARTPOSITION",postion);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0,0);//设置没有动画
                        }
                    });
                }
            }
        };
        return  v;
    }

    /**
     * 初始化主界面数据。
     */
    private void initData() {
        HttpUtils.getRequest(Contast.BASEURL + Contast.LATESTNEWS, new HttpCallableListener() {
            @Override
            public void onScuess(String response) {
                parseTopJson(response);
                handler.sendEmptyMessage(123);


            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        HttpUtils.getRequest(Contast.BASEURL + Contast.LATESTNEWS, new HttpCallableListener() {
            @Override
            public void onScuess(String response) {
                parseJson(response);
                handler1.sendEmptyMessage(234);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

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
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                String images = jsonArray1.getString(0);
                String title = jsonObject1.getString("title");
                int id = jsonObject1.getInt("id");
                nb = new NewBean();
                nb.setImages(images);
                nb.setTitle(title);
                nb.setId(id);
                list.add(nb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



}
