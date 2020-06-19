package com.neinei.cong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.neinei.cong.R;
import com.neinei.cong.request.OKRequest;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_search);
        init();
    }

    private EditText editText;
    private Button sou;
    private GridView gridView;
    private TextView tv;

    private void init(){
        editText = findViewById(R.id.searchEdi);
        gridView = findViewById(R.id.gridview);
        sou = findViewById(R.id.search);
        tv = findViewById(R.id.tix);

        sou.setOnClickListener(new BtnClick());
        gridView.setOnItemClickListener(new GridViewItem());
        gridView.setEmptyView(tv);

    }


    class BtnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String t = editText.getText().toString();
            new OKRequest().SearchKey(t,gridView,getApplicationContext());
        }
    }

    class GridViewItem implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String ids = (String) view.getTag(R.id.mid);
            String tim = (String) view.getTag(R.id.mti);
            String url = (String) view.getTag(R.id.vdl);
            String tid = (String) view.getTag(R.id.mtg);
            Intent intent = new Intent();
            intent.setAction("play");
            intent.putExtra("id",ids);
            intent.putExtra("title",tim);
            intent.putExtra("url",url);
            intent.putExtra("tid",tid);
//            startActivity(intent);
            startActivityForResult(intent,1);
        }
    }

}
