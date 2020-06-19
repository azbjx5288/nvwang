package com.neinei.cong.ui.newadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neinei.cong.R;
import com.neinei.cong.bean.NovelTermBean;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {


    private Context context;
    private List<NovelTermBean> novelTermBeanList;

    public MyGridViewAdapter(Context context, List<NovelTermBean> novelTermBeanList) {
        this.context = context;
        this.novelTermBeanList = novelTermBeanList;
    }

    @Override
    public int getCount() {
        return novelTermBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return novelTermBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.gridview_novel_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.img_no);
            viewHolder.textView = convertView.findViewById(R.id.txt_no);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String title = novelTermBeanList.get(position).getName();
        String id = novelTermBeanList.get(position).getTerm_id();
        String url = novelTermBeanList.get(position).getImgUrl();
        viewHolder.textView.setText(title);
        Glide.with(context).load(url).into(viewHolder.imageView);
        convertView.setTag(R.id.mid,id);
        convertView.setTag(R.id.mti,title);
        return convertView;
    }


    public class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
