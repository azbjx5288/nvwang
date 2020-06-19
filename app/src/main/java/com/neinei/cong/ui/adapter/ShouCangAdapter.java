package com.neinei.cong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neinei.cong.R;

import java.util.List;
import java.util.Map;

public class ShouCangAdapter extends BaseAdapter {
    private List<Map<String,String>> list;
    private Context context;
    public ShouCangAdapter(List<Map<String,String>> list,Context context) {
        this.list = list;
        this.context = context;
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

    private FuYong Fu;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shoucang_view,null);
            Fu = new FuYong();
            Fu.image = convertView.findViewById(R.id.image);
            Fu.title = convertView.findViewById(R.id.title);
            convertView.setTag(Fu);
        }else {
            Fu = (FuYong) convertView.getTag();
        }


        return convertView;
    }

    class FuYong {
        public ImageView image;
        public TextView title;
    }

}
