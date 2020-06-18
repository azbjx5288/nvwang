package com.ty8box.blings.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class GridHeight {
    private GridView G;
    private int lei;
    public GridHeight(GridView g,int l) {
        G = g;
        lei = l;
    }

    private void setHeight(){
        ListAdapter listAdapter = G.getAdapter();
        if(listAdapter==null){
            return;
        }
        int total = 0;
        for (int i=0;i<listAdapter.getCount();i+=lei){
            View itemview = listAdapter.getView(i,null,G);
            itemview.measure(0,0);
            total += itemview.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = G.getLayoutParams();
        params.height = total;
        G.setLayoutParams(params);
    }

}
