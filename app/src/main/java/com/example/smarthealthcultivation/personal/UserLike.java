package com.example.smarthealthcultivation.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.Bean.Article;
import com.example.smarthealthcultivation.Bean.Tag;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.recommend.RecommendView;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserLike extends AppCompatActivity {
    ImageButton back;
    ListView listView;
    String username;
    Bitmap[] bitmaps;
    List<Article> articleList;
    MyAdapter myAdapter;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(UserLike.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    listView=(ListView) findViewById(R.id.listview);
                    myAdapter=new MyAdapter();
                    listView.requestLayout();
                    listView.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(myAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_like);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        if(username==""){
            Toast.makeText(UserLike.this,"通讯错误",Toast.LENGTH_SHORT);
            finish();
        }

        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getUserLike();
            }
        } ).start();
    }

    public void getUserLike(){
        articleList = DataBaseHelper.Query(4, Article.class, new String[]{"articleid","title","picturepath","Abstract"},
                new String[]{"my_like"}, " userid='"+ username +"'");
        if (DataBaseHelper.responseCode != 200) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = "网络请求失败";
            handler.sendMessage(msg);
            return;
        }
        if (articleList == null || articleList.size() == 0)
            return;


        bitmaps = new Bitmap[articleList.size()];
        for (int i = 0; i < articleList.size(); i++) {
            bitmaps[i]=DataBaseHelper.getBitmap(articleList.get(i).getPicturepath());
            if (bitmaps[i] == null) {
                Message msg=new Message();
                msg.what=1;msg.obj="网络请求失败";
                handler.sendMessage(msg);
                return;
            }
        }

        msg=new Message();
        msg.what=2;
        handler.sendMessage(msg);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return articleList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v=null;
            final Holder holder;
            if(convertView==null){
                v= View.inflate(UserLike.this,R.layout.user_like_item,null);
                holder=new Holder();
                holder.iv=(ImageView) v.findViewById(R.id.articlepic);
                holder.abst=(TextView)v.findViewById(R.id.articleabstract);
                holder.title=(TextView)v.findViewById(R.id.title);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(Holder) v.getTag();
            }
            holder.title.setText(articleList.get(position).getTitle());
            holder.abst.setText(articleList.get(position).getAbstract());
            holder.iv.setImageBitmap(bitmaps[position]);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean flag;
                            if (username!=null){
                                flag=DataBaseHelper.Execute("insert into articlehistory values('"+ username +"','"+
                                        articleList.get(position).getArticleid() +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +"')");
                                if (!flag){
                                    msg=new Message();msg.what=1;
                                    msg.obj="网络连接失败";
                                    handler.sendMessage(msg);
                                    return;
                                }
                            }
                            Intent intent=new Intent(UserLike.this,RecommendView.class);
                            intent.putExtra("articleid",articleList.get(position).getArticleid());
                            intent.putExtra("username",username);
                            startActivity(intent);
                        }
                    }).start();

                }
            });
            myAdapter.notifyDataSetChanged();
            return v;
        }
    }

    class Holder{
        ImageView iv;
        TextView title,abst;
    }
}
