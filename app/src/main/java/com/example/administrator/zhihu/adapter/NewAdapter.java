package com.example.administrator.zhihu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.bean.NewBean;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.Contast;
import com.example.administrator.zhihu.utils.SaveUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/8/16 0016.
 */
public class NewAdapter extends BaseAdapter {
    private List<NewBean> list;
    private Context context;
    private  boolean islight;
    private ViewHolder holder = null;
    private List<String> time = new ArrayList<>();
    private Map<Integer,String> map = new HashMap<>();//设置一个来解决时期的复用问题，存放需要改变的时间和位置。


    public NewAdapter(List<NewBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public boolean islight() {
        return islight;
    }

    public void setIslight(boolean islight) {
        this.islight = islight;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * 加载更多数据添加到list
     * @param list
     * @param date
     */
    public  void addData(List<NewBean> list,String date){
        this.list.addAll(list);
       time.add(date);
        notifyDataSetChanged();

    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void arriveTop(boolean flag){
        if(flag) {
            holder.tv_title.setText("今日热闻");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ApplicationUtil.getContext()).inflate(R.layout.new_item_layout,null);
            holder.ll_all  = (LinearLayout) convertView.findViewById(R.id.ll_all);
            holder.ll_item  = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.tv_show = (TextView) convertView.findViewById(R.id.tv_show);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        }else{
            holder  = (ViewHolder) convertView.getTag();
        }

        if(list.get(position).getType()== Contast.TOPIC) {
            holder.tv_title.setVisibility(View.VISIBLE);
                if (time.size() != 0) {
                    if(!map.containsKey(position)) {
                        holder.tv_title.setText(time.get(time.size() - 1).toString());
                        map.put(position, time.get(time.size() - 1));
                    }
                }
                if(map.size()!=0){
                for (Object keypositon : map.keySet()) {
                    if (keypositon.equals(position)) {
                        String value = map.get(keypositon);
                        holder.tv_title.setText(value);
                    }
                }
            }
        }
        else{
            holder.tv_title.setVisibility(View.INVISIBLE);
        }
        holder.ll_all.setBackgroundColor(islight() ? ApplicationUtil.getContext().getResources().getColor(R.color.transule) : ApplicationUtil.getContext().getResources().getColor(R.color.black));
        holder.ll_item.setBackgroundColor(islight() ? ApplicationUtil.getContext().getResources().getColor(R.color.white) : ApplicationUtil.getContext().getResources().getColor(R.color.grey));
        holder.tv_show.setTextColor(islight() ? ApplicationUtil.getContext().getResources().getColor(R.color.black) : ApplicationUtil.getContext().getResources().getColor(R.color.white));
        String readinformation = SaveUtils.getString("READ");
        if(readinformation.contains(list.get(position).getId()+"")){
            holder.tv_show.setTextColor(ApplicationUtil.getContext().getResources().getColor(R.color.grey2));
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()//这里的处理就是对于image的错位。
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();
       // 首先在这先进行属性配置，是否进行缓存，加载不出来时候默认的图片，等等属性配置
    Log.d("tag12","----"+list.get(position).getTitle()+"-------");
        holder.tv_show.setText(list.get(position).getTitle());
        if(null!=list.get(position).getImages()) {
            ImageLoader.getInstance().loadImage(list.get(position).getImages(), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.iv.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }else{
            holder.iv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
    class ViewHolder{
        TextView tv_show,tv_title;
        ImageView iv;
        LinearLayout ll_all,ll_item;
    }
}
