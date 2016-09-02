package com.example.administrator.zhihu.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.activity.MainActivity;
import com.example.administrator.zhihu.adapter.NewTitleAdapter;
import com.example.administrator.zhihu.bean.NewTitleBean;
import com.example.administrator.zhihu.utils.ActivityUtils;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;
import com.example.administrator.zhihu.utils.SaveUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12 0012.
 * @author laoqiang
 */
public class MenuFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<NewTitleBean> list = new ArrayList<>();
    private ListView listview;
    private NewTitleAdapter adapter;
    private Handler handler;
    private TextView tv_main;
    private LinearLayout ll_head;
    private TextView tv_login,tv_save,tv_download;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menufragment_layout,null,false);
        listview = (ListView) v.findViewById(R.id.listview);
        tv_main = (TextView) v.findViewById(R.id.tv_main);
        ll_head  = (LinearLayout) v.findViewById(R.id.ll_head);
        tv_login = (TextView) v.findViewById(R.id.tv_login);
        tv_save = (TextView) v.findViewById(R.id.tv_save);
        tv_download = (TextView) v.findViewById(R.id.tv_download);
        initData();
        tv_main.setOnClickListener(this);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==123) {
                    listview.setAdapter(adapter);
                    updateTheme(SaveUtils.getBoolean(ApplicationUtil.getContext(), "LIGHT"));

                }
            }
        };
        listview.setOnItemClickListener(this);
        return v;
    }


    public void updateTheme(boolean flag) {
        listview.setBackgroundColor(flag ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
        ll_head.setBackgroundColor(flag ? getResources().getColor(R.color.light_toolbar) : getResources().getColor(R.color.black));
        tv_login.setTextColor(flag ? getResources().getColor(R.color.white) : getResources().getColor(R.color.white));
        tv_save.setTextColor(flag?getResources().getColor(R.color.white):getResources().getColor(R.color.white));
        tv_download.setTextColor(flag?getResources().getColor(R.color.white):getResources().getColor(R.color.white));
        adapter.setIslight(flag);
    }

    private void initData() {
        if(HttpUtils.isNetWorkConnected()){
            HttpUtils.getRequest(Contast.BASEURL + Contast.THEMES, new HttpCallableListener() {
                @Override
                public void onScuess(String response) {
                    SaveUtils.saveString(ApplicationUtil.getContext(),Contast.THEMES,response);
                    parseJson(response);
                }

                @Override
                public void onFailure(Exception e) {
                    String result  =  SaveUtils.getString(ApplicationUtil.getContext(),Contast.THEMES);
                    parseJson(result);
                }
            });

        }else{
            ActivityUtils.shortToast(getActivity(),"当前网络有问题");
        }
    }
    /**
     *
     * @param result json数据
     *  解析json数据
     */
    private void parseJson(String result) {
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray jsonarray =jsonobject.getJSONArray("others");
            for(int i = 0;i<jsonarray.length();i++){
                JSONObject jsonobject1 = jsonarray.getJSONObject(i);
                NewTitleBean ntb = new NewTitleBean();
                ntb.setId(jsonobject1.getInt("id"));
                ntb.setTitle(jsonobject1.getString("name"));
                list.add(ntb);
            }
            adapter = new NewTitleAdapter(ApplicationUtil.getContext(),list);
            handler.sendEmptyMessage(123);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 菜单首页点击
     * @param v
     */
    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).loadLasted();
        ((MainActivity)getActivity()).closeDrawLayout();
    }

    /**
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * 点击侧滑菜单的条目。
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity)getActivity()).setId(list.get(position).getId());
        ((MainActivity)getActivity()).setTitle(list.get(position).getTitle());
        ((MainActivity) getActivity()).loadThemeLasted();
        ((MainActivity) getActivity()).setRefreshtag("item");
        ((MainActivity)getActivity()).closeDrawLayout();



    }
}
