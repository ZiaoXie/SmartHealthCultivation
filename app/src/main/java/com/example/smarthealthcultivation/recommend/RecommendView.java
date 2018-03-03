package com.example.smarthealthcultivation.recommend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecommendView extends AppCompatActivity {

    ImageButton back;
    TextView title,content;
    CheckBox shoucang;
    String username,articleid,result[][],Title,Content;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(RecommendView.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    title.setText(Title);
                    content.setText(Content);
                    break;
                case 3:
                    shoucang.setChecked((boolean)msg.obj);
                    break;
                case 4:
                    shoucang.setChecked(!shoucang.isChecked());
                    Toast.makeText(RecommendView.this,"网络通讯失败" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_view);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        SharedPreferences shconfig = getSharedPreferences("ConFigShare",
                Context.MODE_PRIVATE);

        username=getIntent().getStringExtra("username");
        articleid=getIntent().getStringExtra("articleid");

        back=(ImageButton) findViewById(R.id.back);
        title=(TextView)findViewById(R.id.title);
        content=(TextView)findViewById(R.id.content);
        shoucang=(CheckBox)findViewById(R.id.shoucang);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag;
                if (username!=""){
                    flag=DataBaseHelper.Execute("insert into articlehistory values('"+ username +"','"+
                            articleid +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +"')");
                    if (!flag){
                        msg=new Message();msg.what=1;
                        msg.obj="网络连接失败";
                        handler.sendMessage(msg);
                        return;
                    }
                }

                result= DataBaseHelper.Query("select title,content from article where articleid='"+ articleid +"'",2);
                Title=result[0][0];
                Content=DataBaseHelper.getTxt(result[0][1]);
                msg=new Message(); msg.what=2;
                handler.sendMessage(msg);

                msg=new Message();msg.what=3;
                if(username==null){
                    msg.obj=false;
                    handler.sendMessage(msg);
                    return;
                }
                else {
                    result=DataBaseHelper.Query("select * from love where userid='"+ username +"' and articleid='"+ articleid +"'",2);
                    if (result==null) msg.obj=false;
                    else msg.obj=true;
                    handler.sendMessage(msg);
                }

            }
        }).start();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username==null){
                    Toast.makeText(RecommendView.this,"请先登录",Toast.LENGTH_SHORT).show();
                    shoucang.setChecked(false);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag;
                        if(shoucang.isChecked()){
                            flag=DataBaseHelper.Execute("insert into love values('"+ username +"','"+ articleid +"')");
                            if (flag){
                                msg=new Message();msg.what=1;
                                msg.obj="收藏成功";
                                handler.sendMessage(msg);
                            }
                        }
                        else {
                            flag=DataBaseHelper.Execute("delete from love where userid='"+ username +"' and articleid= '"+ articleid +"'");
                            if (flag){
                                msg=new Message();msg.what=1;
                                msg.obj="取消收藏";
                                handler.sendMessage(msg);
                            }
                        }
                        if (!flag){
                            msg=new Message();msg.what=4;
                            handler.sendMessage(msg);
                        }
                    }
                }).start();

            }
        });

    }
}
