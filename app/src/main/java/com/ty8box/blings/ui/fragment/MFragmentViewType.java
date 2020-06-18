package com.ty8box.blings.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.neinei.cong.R;
import com.ty8box.blings.request.OKRequest;
import com.ty8box.blings.ui.adapter.MVoidAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MFragmentViewType extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_type,null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        initListener();
    }

    private TextView title;
    private TextView gengd;
    private GridView gridView;
    private MVoidAdapter adapter;
    private List<Map<String,String>> list = new ArrayList<>();
    private void init(View view) {
        title = view.findViewById(R.id.title);
        gengd = view.findViewById(R.id.gengduo);
        gridView = view.findViewById(R.id.gridview);

        title.setText(mokuainame);
        adapter = new MVoidAdapter(list,getContext());
        gengd.setTag(R.id.mid,typeid);
        gengd.setTag(R.id.mti,mokuainame);
        gridView.setAdapter(adapter);
        new OKRequest().RequestType(typeid,gridView,getContext(),"1");
    }

    private void initListener(){
        gengd.setOnClickListener(new GengDuo());
        gridView.setOnItemClickListener(new GridItemClick());
    }
    //初始化视图列表
    private String mokuainame;
    private String typeid;
    public void GridViewinit(String id,String name){
        mokuainame = name;
        typeid = id;
    }
    //更多点击
    class GengDuo implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String id = (String) v.getTag(R.id.mid);
            String ti = (String) v.getTag(R.id.mti);
            Intent intent = new Intent();
            intent.setAction("videotype");
            intent.putExtra("id",id);
            intent.putExtra("title",ti);
            startActivity(intent);
        }
    }
    class GridItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String mid = (String) view.getTag(R.id.mid);
            String mti = (String) view.getTag(R.id.mti);
            String url = (String) view.getTag(R.id.vdl);
            String tid = (String) view.getTag(R.id.mtg);
            Intent intent = new Intent();
            intent.setAction("play");
            intent.putExtra("id",mid);
            intent.putExtra("title",mti);
            intent.putExtra("url",url);
            intent.putExtra("tid",tid);

            startActivity(intent);
        }
    }

}
