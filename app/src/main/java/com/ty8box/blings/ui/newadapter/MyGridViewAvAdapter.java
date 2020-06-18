package com.ty8box.blings.ui.newadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neinei.cong.R;
import com.neinei.cong.bean.AvVideoListBean;
import com.neinei.cong.bean.NovelTermBean;

import java.util.List;

public class MyGridViewAvAdapter extends BaseAdapter {


    private Context context;
    private List<AvVideoListBean> list;

    public MyGridViewAvAdapter(Context context, List<AvVideoListBean> list) {
        this.context = context;
        this.list = list;
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
//        String title = list.get(position).getName();
//        viewHolder.textView.setText(title);

        //空出来设置图标

        return convertView;
    }


    public class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
