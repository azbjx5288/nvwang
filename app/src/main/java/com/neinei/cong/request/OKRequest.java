package com.neinei.cong.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;
import com.neinei.cong.ui.GridHeight;
import com.neinei.cong.ui.adapter.MVoidAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/****
 * 域名地址变化时更改此处域名即可
 * （该APP估计经过多手开发 代码比较乱  安大侠到此一游 QQ3245752005）
 * 该类为OKHttp请求类 鉴于前人不写注释 让我费劲得一逼 再接手人员 加油！！！
 */

public class OKRequest {

    private List<Map<String,String>> list = new ArrayList<>();
    public OKRequest() {

    }
    //获取视频分类ID
    public void RequestTypeID(){
        String typeurl = AppConfig.MAIN_URL + "/api/public/?service=Home.novelTerm";
        new RequestTypeid().execute(typeurl);
    }
    class RequestTypeid extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s==null){
                return;
            }
            try {
                JSONObject obj = new JSONObject(s);
                JSONObject dataobje = obj.getJSONObject("data");
                JSONArray array = dataobje.getJSONArray("info");
                List<Map<String,String>> list = new ArrayList<>();
                for (int i=0;i<array.length();i++){
                    JSONObject aobje = array.getJSONObject(i);
                    String id = aobje.optString("term_id");
                    String name = aobje.optString("name");
                    Map<String,String> map = new HashMap<>();
                    map.put("id",id);
                    map.put("name",name);
                    list.add(map);
                }
                System.out.println("请求返回得"+list.size());
                if(list.size()<=0){
                    return;
                }
                if(listListener!=null){
                    listListener.OnRequestList(list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //请求数据完成后得回调接口
    public interface OnRequestListListener{
        void OnRequestList(List<Map<String,String>> list);
    }
    private OnRequestListListener listListener;
    public void setOnRequestListListener(OnRequestListListener listener){
        listListener = listener;
    }
    //分类推荐数据请求
    private String url;
    private MVoidAdapter typeadapter;
    public void RequestType(String id, GridView gridView,Context context,String peg){
        url = AppConfig.MAIN_URL +"/api/public/?service=Home.VideoList&id="+id+"&lx="+peg;
        System.out.println("执行分类数据请求");
        typeadapter = new MVoidAdapter(list,context);
        new RequestType(gridView,context).execute(url);
    }
    class RequestType extends AsyncTask<String,Void,String>{
        private GridView gridView;
        private Context context;
        public RequestType(GridView gridView,Context context) {
            this.gridView = gridView;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                return;
            }
            try {
                JSONObject obj = new JSONObject(s);
                JSONObject obja = obj.getJSONObject("data");
                JSONArray arr = obja.getJSONArray("info");
                for (int i=0;i<arr.length();i++){
                    JSONObject arrobj = arr.getJSONObject(i);
                    String id = arrobj.optString("id");
                    String upt = arrobj.optString("video_url");
                    String mid = arrobj.optString("term_id");
                    String title = arrobj.optString("title");
                    String img = arrobj.optString("video_img");
                    Map<String,String> map = new HashMap<>();
                    map.put("id",id);
                    map.put("video_url",upt);
                    map.put("term_id",mid);
                    map.put("title",title);
                    map.put("video_img",img);
                    list.add(map);
                }
                System.out.println("获取数据成功");
                System.out.println("获取数据成功"+list.size());

                gridView.setAdapter(typeadapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //分类列表请求数据
    public void TypeRequest(String id, String peg, MVoidAdapter adapter, List<Map<String,String>> list, GridView gridView){
        url =  AppConfig.MAIN_URL +"api/public/?service=Home.VideoList&id="+id+"&lx="+peg;

        System.out.println("执行分类数据请求");
        Log.i("UUU","开始请求"+url);
        new TypeRequest(adapter,list,gridView).execute(url);
    }
    class TypeRequest extends AsyncTask<String,Void,String>{
        private List<Map<String,String>> list;
        private MVoidAdapter adapter;
        private GridView gridView;
        private boolean aBoolean = true;
        public TypeRequest(MVoidAdapter adapter,List<Map<String,String>> list,GridView gridView) {
            this.adapter = adapter;
            this.list = list;
            this.gridView = gridView;
        }

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Log.i("UUU","空数据");
                return;
            }
            try {
                JSONObject obj = new JSONObject(s);
                JSONObject obja = obj.getJSONObject("data");
                JSONArray arr = obja.getJSONArray("info");
                for (int i=0;i<arr.length();i++){
                    JSONObject arrobj = arr.getJSONObject(i);
                    String id = arrobj.optString("id");
                    String upt = arrobj.optString("video_url");
                    String mid = arrobj.optString("term_id");
                    String title = arrobj.optString("title");
                    String img = arrobj.optString("video_img");
                    Map<String,String> map = new HashMap<>();
                    map.put("id",id);
                    map.put("video_url",upt);
                    map.put("term_id",mid);
                    map.put("title",title);
                    map.put("video_img",img);
                    list.add(map);
                }
                System.out.println("获取数据成功");
                System.out.println("获取数据成功"+list.size());

                adapter.notifyDataSetChanged();
                new GridHeight(gridView,2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //关键词搜索
    public void SearchKey(String key,GridView gridView,Context context){
        String keys =  AppConfig.MAIN_URL +"api/public/?service=Home.VideoListName&id=1&mc="+key;
        new SearchAsyn(gridView,context).execute(keys);
    }
    class SearchAsyn extends AsyncTask<String,Void,String>{
        private GridView gridView;
        private Context context;
        public SearchAsyn(GridView gridView,Context context) {
            this.gridView = gridView;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                JSONObject obj1= obj.getJSONObject("data");
                JSONArray arr = obj1.getJSONArray("info");
                for (int i=0;i<arr.length();i++){
                    JSONObject arrobj = arr.getJSONObject(i);
                    String id = arrobj.optString("id");
                    String upt = arrobj.optString("video_url");
                    String mid = arrobj.optString("term_id");
                    String title = arrobj.optString("title");
                    String img = arrobj.optString("video_img");
                    Map<String,String> map = new HashMap<>();
                    map.put("id",id);
                    map.put("video_url",upt);
                    map.put("term_id",mid);
                    map.put("title",title);
                    map.put("video_img",img);
                    list.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            MVoidAdapter adapter = new MVoidAdapter(list,context);
            gridView.setAdapter(adapter);

        }
    }

    //播放页广告获取
    public void getneiye(ImageView imageView,Context context){
        String url = AppConfig.MAIN_URL +"/api/public/?service=Home.avneiye";
        new ImageAsyn(imageView,context).execute(url);
    }
    class ImageAsyn extends AsyncTask<String,Void,String>{

        private ImageView image;
        private Context context;
        public ImageAsyn(ImageView img,Context c) {
            image = img;
            context = c;
        }

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                return;
            }
            try {
                JSONObject object = new JSONObject(s);
                JSONObject object1 = object.getJSONObject("data");
                JSONObject object2 = object1.getJSONObject("info");
                String u = object2.optString("thumb");
                String url = AppConfig.MAIN_URL+u;
                Log.i("TTT",url);
                Glide.with(context).load(url).into(image);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    //OKHttp 解析
    public void OKurlJS(String uid){
        String url = AppConfig.MAIN_URL +"/read.php?uuid="+uid;
        new UrlJs().execute(url);
    }
    class UrlJs extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return OkReq(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Log.i("AAA","法克");
                return;
            }
            try {
                JSONObject object = new JSONObject(s);
                JSONObject object1 = object.getJSONObject("data");
                String p480 = object1.optString("480p");
                String p720 = object1.optString("720p");
                if(listener!=null){
                    if(p720!=null){
                        listener.Urls(p720);
                    }else if(p480!=null){
                        listener.Urls(p480);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public interface UrlPlayListener{
        void Urls(String url);
    }
    private UrlPlayListener listener;
    public void setOnUrlPlayListener(UrlPlayListener u){
        listener = u;
    }

    //OKhttp 请求方法函数
    private String OkReq(String url){
        String body = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

}
