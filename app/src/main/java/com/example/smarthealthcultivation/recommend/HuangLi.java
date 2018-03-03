package com.example.smarthealthcultivation.recommend;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HuangLi extends AppCompatActivity {

    String  YinLi,huangliresult[];
    String tiaomu[]={"五行", "冲煞", "拜祭", "吉神宜趋", "宜", "凶神宜忌", "忌"};
    String json[]={"wuxing", "chongsha", "baiji", "jishen", "yi", "xiongshen", "ji"};
    TextView day, year_month,yinli;

    MyAdapter adapter;
    RecyclerView recyclerView;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(HuangLi.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    yinli.setText(YinLi);
                    adapter=new MyAdapter();
                    //设置布局管理器
                    recyclerView.setLayoutManager(new LinearLayoutManager(HuangLi.this));
                    //设置adapter
                    recyclerView.setAdapter(adapter);
                    //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
                    recyclerView.setHasFixedSize(true);

                    //设置Item增加、移除动画
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //添加分割线
                    recyclerView.addItemDecoration(new DividerItemDecoration(
                            HuangLi.this, LinearLayoutManager.VERTICAL));
                    break;
                case 3:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huang_li);

        ImageButton back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        huangliresult=new String[tiaomu.length];
        day = (TextView) findViewById(R.id.day);
        year_month = (TextView) findViewById(R.id.year_month);
        yinli = (TextView) findViewById(R.id.yinli);

        day.setText(String.valueOf(Calendar.DAY_OF_MONTH));
        year_month.setText(new SimpleDateFormat("yyyy/MM").format(new Date()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://v.juhe.cn/laohuangli/d");

                    // 根据URL对象打开链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    // 设置请求的方式
                    urlConnection.setRequestMethod("POST");
                    // 设置请求的超时时间
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setConnectTimeout(5000);

                    // 设置请求的头
                    urlConnection.setRequestProperty("Connection", "keep-alive");
                    // 设置请求的头
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    // 设置请求的头
                    urlConnection.setRequestProperty("Content-Length","date="+ new SimpleDateFormat("yyyy-MM-dd").
                            format(new Date()) +"&key=52f889eaf7fc5343434e1d33490fdf6d");
                    // 设置请求的头
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

                    urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
                    urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
                    //setDoInput的默认值就是true
                    //获取输出流
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(("date="+ new SimpleDateFormat("yyyy-MM-dd").
                            format(new Date()) +"&key=52f889eaf7fc5343434e1d33490fdf6d").getBytes());
                    os.flush();

                    StringBuilder sb=new StringBuilder();
                    String line;
                    int responseCode=urlConnection.getResponseCode();
                    if(responseCode!=200){
                        Message msg=new Message();
                        msg.what=1;msg.obj="获取黄历失败";
                        handler.sendMessage(msg);
                        return;
                    }


                    InputStream is=urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    System.out.println(sb.toString());


                    JSONObject in=new JSONObject(sb.toString());
                    JSONObject result=in.getJSONObject("result");

                    YinLi=result.getString("yinli");
                    for(int i=0;i<huangliresult.length;i++){
                        huangliresult[i]=result.getString(json[i]);
                    }

                    Message msg=new Message();
                    msg.what=2;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();


    }


    class MyAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder=new Holder(LayoutInflater.from(HuangLi.this).inflate(R.layout.huang_li_item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Holder)holder).tiaomu.setText(tiaomu[position]);
            ((Holder)holder).content.setText(huangliresult[position]);
        }

        @Override
        public int getItemCount() {
            return tiaomu.length;
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView tiaomu,content;
        public Holder(View itemView) {
            super(itemView);
            tiaomu=(TextView)itemView.findViewById(R.id.tiaomu);
            content=(TextView)itemView.findViewById(R.id.content);
        }
    }
}
