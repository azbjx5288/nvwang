package com.ty8box.blings.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.neinei.cong.R;

import java.util.List;
import java.util.Map;

/**************
 * Fragment 视频推荐GridView 适配器
 */
public class MVoidAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,String>> list;
    public MVoidAdapter(List<Map<String,String>> list,Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
//        if(list.size() >= 4){
//            return 4;
//        }
//        Log.i("FFF","数据量"+list.size());
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_view,null);
            Fu = new FuYong();
            Fu.image = convertView.findViewById(R.id.image);
            Fu.titile = convertView.findViewById(R.id.title);
            Fu.futitle= convertView.findViewById(R.id.futitle);
            convertView.setTag(Fu);
        }else {
            Fu = (FuYong) convertView.getTag();
        }
        Fu.titile.setText(list.get(position).get("title"));
        Fu.futitle.setText(list.get(position).get("uptime"));
        RoundedCorners roundedCorners = new RoundedCorners(15);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(convertView).load(list.get(position).get("video_img")).apply(options).into(Fu.image);
        convertView.setTag(R.id.mti,list.get(position).get("title"));
        convertView.setTag(R.id.mid,list.get(position).get("id"));
        convertView.setTag(R.id.vdl,list.get(position).get("video_url"));
        convertView.setTag(R.id.mtg,list.get(position).get("term_id"));

        return convertView;
    }

    class FuYong {
        public TextView titile;
        public TextView futitle;
        public ImageView image;
    }

}
