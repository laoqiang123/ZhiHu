package com.example.administrator.zhihu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.zhihu.R;
import com.example.administrator.zhihu.activity.MainActivity;
import com.example.administrator.zhihu.bean.NewTitleBean;
import com.example.administrator.zhihu.utils.ApplicationUtil;
import com.example.administrator.zhihu.utils.SaveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/13 0013.
 * @author  laoqiang
 * 封装侧滑导航的adapter。
 */
public class NewTitleAdapter extends BaseAdapter {
    private List<NewTitleBean> list = new ArrayList<>();
    private Context context;
    private boolean islight;

    public NewTitleAdapter(Context context, List<NewTitleBean> list) {
        this.context = context;
        this.list = list;
    }

    public boolean islight() {
        return islight;
    }

    public void setIslight(boolean islight) {
        this.islight = islight;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder  = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.newtitle_item,null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        }else{
            holder  = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setTextColor(islight?ApplicationUtil.getContext().getResources().getColor(R.color.black):ApplicationUtil.getContext().getResources().getColor(R.color.white));
        holder.tv_title.setText(list.get(position).getTitle());

        return convertView;
    }



    class ViewHolder{
        TextView tv_title;
    }
}
