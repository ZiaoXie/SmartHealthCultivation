package com.example.smarthealthcultivation.forum;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumView extends AppCompatActivity {

    String result[][],talkid,talktitle,username;
    ListView listView;
    int count;
    TextView title;
    Button edit;
    ImageButton back;
    Bitmap bitmaps[];
    EditText et;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(ForumView.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    listView=(ListView) findViewById(R.id.listview);
                    MyAdapter myAdapter=new MyAdapter();
                    listView.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(new MyAdapter());
                    break;
                case 3:
                    et.clearFocus();
                    et.setText("");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        SharedPreferences shconfig = getSharedPreferences("ConFigShare",
                Context.MODE_PRIVATE);

        talkid=getIntent().getStringExtra("talkid");
        talktitle=getIntent().getStringExtra("title");
        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        if(talkid==null||talktitle==null){
            Toast.makeText(ForumView.this,"通讯错误",Toast.LENGTH_SHORT);
            finish();
        }

        title=(TextView) findViewById(R.id.title);
        title.setText(talktitle);

        listView=(ListView) findViewById(R.id.listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                result=DataBaseHelper.Query("select * from talk_view where talkid='"+ talkid +"' order by numberfloor",6);
                if (DataBaseHelper.responseCode!=200){
                    Message msg=new Message();
                    msg.what=1;msg.obj="网络请求失败";
                    handler.sendMessage(msg);
                    return;
                }
                if(result==null)
                    return;
                else count=result.length;

                bitmaps = new Bitmap[result.length];
                for (int i = 0; i < result.length; i++) {
                    bitmaps[i]=DataBaseHelper.getBitmap(result[i][5]);
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
        }).start();

        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit=(Button)findViewById(R.id.edit);
        et=(EditText)findViewById(R.id.input);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username==""){
                    Toast.makeText(ForumView.this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                if(et.getText().length()==0){
                    Toast.makeText(ForumView.this,"请输入文字",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag= false;

                        flag = DataBaseHelper.Execute("declare @count int select @count=max(numberfloor)+1 from talkhistory " +
                                "where talkid='" + talkid + "' insert into talkhistory values" +
                                "('" + talkid + "','" + username + "','" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "','" + et.getText().toString() + "' ,@count)");

                        msg=new Message();msg.what=1;
                        if(flag) msg.obj="发布成功";
                        else msg.obj="发布失败";
                        handler.sendMessage(msg);

                        msg=new Message();msg.what=3;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return count;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=null;
            final Holder holder;
            if(convertView==null){
                v= View.inflate(ForumView.this,R.layout.listview_floor,null);
                holder=new Holder();
                holder.civ=(CircleImageView) v.findViewById(R.id.head);
                holder.username=(TextView) v.findViewById(R.id.username);
                holder.time=(TextView) v.findViewById(R.id.time);
                holder.talkabstract=(TextView)v.findViewById(R.id.talkabstract);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(Holder) v.getTag();
            }

            holder.civ.setImageBitmap(bitmaps[position]);
            holder.username.setText(result[position][1]);
            holder.time.setText(result[position][4]+"楼 "+DataBaseHelper.TimeTransfer(result[position][2]));
            holder.talkabstract.setText(result[position][3]);
            return v;
        }
    }

    class Holder{
        TextView username,time,talkabstract;
        CircleImageView civ;
    }

}
