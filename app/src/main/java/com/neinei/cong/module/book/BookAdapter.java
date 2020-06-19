package com.neinei.cong.module.book;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.neinei.cong.R;
import com.neinei.cong.bean.BookBean;

import java.util.ArrayList;
import java.util.List;

//
//                          _oo0oo_
//                         o8888888o
//                          88" . "88
//                          (| -_- |)
//                          0\  =  /0
//                      ___/`---'\___
//                      .' \\|     |// '.
//                   / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//                  |   | \\\  -  /// |   |
//                  | \_|  ''\---/''  |_/ |
//                  \  .-\__  '-'  ___/-. /
//               ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//          \  \ `_.   \_ __\ /__ _/   .-` /  /
//=====`-.____`.___ \_____/___.-`___.-'=====
//                           `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
public class BookAdapter extends BaseQuickAdapter<BookBean, BaseViewHolder> {
    public BookAdapter() {
        this(new ArrayList<>());
    }

    public BookAdapter(@Nullable List<BookBean> data) {
        super(R.layout.item_book_wallper, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookBean item) {
        TextView view = helper.getView(R.id.tv_diamond);
        if (item.is_buy == 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_title, item.post_title);
        helper.setText(R.id.tv_diamond, item.coin + "");
    }
}
