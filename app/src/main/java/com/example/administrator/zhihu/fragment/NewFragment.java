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
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.activity.MainActivity;
import com.example.administrator.zhihu.activity.NewContentActivity;
import com.example.administrator.zhihu.adapter.NewAdapter;
import com.example.administrator.zhihu.bean.NewBean;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.HttpCallableListener;
import com.example.administrator.zhihu.utils.HttpUtils;
import com.example.administrator.zhihu.utils.SaveUtils;
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
public class NewFragment extends Fragment implements AbsListView.OnScrollListener {
    private ListView listview;
    private int urlid;
    private List<NewBean> list = new ArrayList<>();
    private NewBean nb;
    private String description;
    private String background;
    private ImageView iv_title;
    private TextView tv_title;
    private Handler handler;
    private int id;
    private String title;
    private View v;
    private NewAdapter adapter;

    public NewFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.new_fragment, container, false);
        listview = (ListView) v.findViewById(R.id.listview);
        View header = inflater.inflate(R.layout.new_header_layout, null);
        iv_title = (ImageView) header.findViewById(R.id.iv_title);
        tv_title = (TextView) header.findViewById(R.id.tv_title);
        id = ((MainActivity) getActivity()).getId();
        title = ((MainActivity) getActivity()).getTitlecontent();
        urlid = id;
        final DisplayImageOptions options = new DisplayImageOptions.Builder()//这里的处理就是对于image的错位。
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();
        // 首先在这先进行属性配置，是否进行缓存，加载不出来时候默认的图片，等等属性配置
        initData();
        listview.addHeaderView(header, null, false);//设置它不可以选中。
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 123) {
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
                    adapter = new NewAdapter(list, ApplicationUtil.getContext());
                    updateTheme(SaveUtils.getBoolean( "LIGHT"));
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int[] postion = new int[2];
                            view.getLocationOnScreen(postion);
                            postion[0] = view.getWidth() / 2;
                            Intent intent = new Intent(ApplicationUtil.getContext(), NewContentActivity.class);
                            intent.putExtra("newid", list.get(position - 1).getId());//注意存放获取的位置有偏差。否则出现标题，内容不一致。
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
            }
        };
        listview.setOnScrollListener(this);
        return v;
    }

    /**
     * 跳转新闻详细页面
     */
    private void initData() {
        if (HttpUtils.isNetWorkConnected()) {
            HttpUtils.getRequest(Contast.BASEURL + Contast.THEMENEWS + urlid, new HttpCallableListener() {
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
        else{
            Toast.makeText(ApplicationUtil.getContext(),"没有网络，联网期待！",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param result 界面新闻json数据
     */
    public void parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            description = jsonObject.getString("description");
            background = jsonObject.getString("background");
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.optJSONArray("images");
                /**
                 * Returns the value mapped by name if it exists and is a JSONArray, or null otherwise.
                 */
                String images = null;
                if (jsonArray1 != null) {
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

    /**
     *主题更新
     * @param flag
     */
    public void updateTheme(boolean flag) {
        if (adapter != null) {
            adapter.setIslight(flag);
            listview.setAdapter(adapter);
        } else {

        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 解决下拉刷新和listview冲突
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (listview != null && listview.getChildCount() > 0) {
            if (listview.getFirstVisiblePosition() == 0 && listview.getChildAt(0).getTop() == 0) {
                ((MainActivity) getActivity()).setSwiprrefresh(true);
                adapter.arriveTop(true);
            } else {
                ((MainActivity) getActivity()).setSwiprrefresh(false);
            }
        }
    }
}
