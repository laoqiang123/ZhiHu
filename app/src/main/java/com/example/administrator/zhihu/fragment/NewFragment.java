package com.example.administrator.zhihu.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.activity.NewContentActivity;
import com.example.administrator.zhihu.adapter.NewAdapter;
import com.example.administrator.zhihu.bean.NewBean;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/19 0019.
 *各个主题新闻fragment页面展示
 * @author  laoqiang
 */
public class NewFragment extends Fragment {
    private ListView listview;
    private int urlid;
    private List<NewBean> list = new ArrayList<>();
    private NewBean nb;
    private  String description;
    private String background;
    private ImageView iv_title;
    private TextView tv_title;
    private Handler handler;
    private int id;
    private String title;


    public NewFragment(int id, String title) {
        this.urlid = id;
        this.title = title;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment,container,false);
        listview = (ListView) v.findViewById(R.id.listview);
        View header = inflater.inflate(R.layout.new_header_layout,null);
        iv_title = (ImageView) header.findViewById(R.id.iv_title);
        tv_title = (TextView) header.findViewById(R.id.tv_title);
        final DisplayImageOptions options = new DisplayImageOptions.Builder()//这里的处理就是对于image的错位。
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();
        // 首先在这先进行属性配置，是否进行缓存，加载不出来时候默认的图片，等等属性配置
        initData();
        listview.addHeaderView(header);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==123){
                    ImageLoader.getInstance().loadImage(background, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            iv_title.setImageBitmap(loadedImage);


                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                    tv_title.setText(description);
                    NewAdapter adapter = new NewAdapter(list, ApplicationUtil.getContext());
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int[] postion = new int[2];
                            view.getLocationOnScreen(postion);
                            postion[0] = view.getWidth()/2;
                            Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                            intent.putExtra("newid", list.get(position-1).getId());//注意存放获取的位置有偏差。否则出现标题，内容不一致。
                            intent.putExtra("STARTPOSITION",postion);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0,0);//设置没有动画
                        }
                    });
                }
            }
        };

        return v;
    }

    private void initData() {
        HttpUtils.getRequest(Contast.BASEURL + Contast.THEMENEWS+ urlid, new HttpCallableListener() {
            @Override
            public void onScuess(String response) {
                parseJson(response);
                handler.sendEmptyMessage(123);


            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }
    /**
     *
     * @param result 界面新闻json数据
     */
    public void parseJson(String  result){
        try {
            JSONObject jsonObject = new JSONObject(result);
             description = jsonObject.getString("description");
             background = jsonObject.getString("background");
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.optJSONArray("images");
                /**
                 * Returns the value mapped by name if it exists and is a JSONArray, or null otherwise.
                 */
                String images = null;
                if(jsonArray1!=null){
                    images = jsonArray1.getString(0);
                }
                /**
                 * he difference is that optString returns the empty
                 * string ("") if the key you specify doesn't exist.
                 * getString on the other hand throws a JSONException.
                 * Use getString if it's an error for the data to be missing,
                 * or optString if you're not sure if it will be there.
                 */
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
