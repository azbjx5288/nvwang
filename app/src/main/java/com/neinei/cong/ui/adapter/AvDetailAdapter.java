package com.neinei.cong.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.neinei.cong.R;
import com.neinei.cong.bean.VideoDetailBean;
import com.neinei.cong.utils.GlideUtils;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/14.
 */

public class AvDetailAdapter extends BaseQuickAdapter<VideoDetailBean.ListBean,BaseViewHolder>{

    public AvDetailAdapter(int layoutResId, @Nullable List<VideoDetailBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoDetailBean.ListBean item) {
        ImageView imageView = helper.getView(R.id.img_av);
        GlideUtils.glide(mContext,item.getVideo_img(),imageView);
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_time,item.getUptime()+"");
        helper.setText(R.id.tv_watch,item.getWatch_num()+"");
    }
}
