package com.example.smarthealthcultivation.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.Bean.Article;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TagView extends AppCompatActivity {
    ImageButton back;
    RecyclerView recyclerView;
    String username,tagname;
    Bitmap[] bitmaps;
    List<Article> articleList;
    MyAdapter myAdapter;
    int count;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(TagView.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    myAdapter=new MyAdapter();

                    //设置布局管理器
                    recyclerView.setLayoutManager(new LinearLayoutManager(TagView.this));
                    //设置adapter
                    recyclerView.setAdapter(myAdapter);
                    //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
                    recyclerView.setHasFixedSize(true);

                    //设置Item增加、移除动画
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //添加分割线
                    recyclerView.addItemDecoration(new DividerItemDecoration(
                            TagView.this, LinearLayoutManager.VERTICAL));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_view);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        if(username==""){
            Toast.makeText(TagView.this,"通讯错误",Toast.LENGTH_SHORT);
            finish();
        }

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);

        tagname=getIntent().getStringExtra("tagname");
        TextView title=(TextView)findViewById(R.id.title_text);
        title.setText(tagname);

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
                getArticleTag();
            }
        } ).start();
    }

    public void getArticleTag() {
        articleList = DataBaseHelper.Query(4, Article.class, new String[]{"article.articleid", "title", "picturepath", "Abstract"},
                new String[]{"ArticleTag","Article","Tag"},
                " tagname='" + tagname + "' and articletag.articleid=article.articleid and articletag.tagid=tag.tagid");
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
            bitmaps[i] = DataBaseHelper.getBitmap(articleList.get(i).getPicturepath());
            if (bitmaps[i] == null) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = "网络请求失败";
                handler.sendMessage(msg);
                return;
            }
        }

        msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);
    }

    class MyAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder=new Holder(LayoutInflater.from(TagView.this).inflate(R.layout.user_like_item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Holder)holder).title.setText(articleList.get(position).getTitle());
            ((Holder)holder).abst.setText(articleList.get(position).getAbstract());
            ((Holder)holder).iv.setImageBitmap(bitmaps[position]);



            count=position;
            ((Holder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    count=recyclerView.getChildAdapterPosition(v);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            boolean flag;
                            if (username!=""){
                                flag=DataBaseHelper.Execute("insert into articlehistory values('"+ username +"','"+
                                        articleList.get(count).getArticleid() +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +"')");
                                if (!flag){
                                    msg=new Message();msg.what=1;
                                    msg.obj="网络连接失败";
                                    handler.sendMessage(msg);
                                    return;
                                }
                            }
                            Intent intent=new Intent(TagView.this,RecommendView.class);
                            intent.putExtra("articleid",articleList.get(count).getArticleid());
                            intent.putExtra("username",username);
                            startActivity(intent);
                        }
                    }).start();

                }
            });
        }

        @Override
        public int getItemCount() {
            return articleList.size();
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView title,abst;
        public Holder(View itemView) {
            super(itemView);
            iv=(ImageView) itemView.findViewById(R.id.articlepic);
            abst=(TextView)itemView.findViewById(R.id.articleabstract);
            title=(TextView)itemView.findViewById(R.id.title);
        }
    }
}
