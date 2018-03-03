package com.example.smarthealthcultivation.forum;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTalk extends AppCompatActivity {

    Button edit;
    ImageButton back;
    EditText title,articleabstract;
    String username;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(NewTalk.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_talk);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");

        back=(ImageButton) findViewById(R.id.back);
        edit=(Button) findViewById(R.id.edit);
        title=(EditText)findViewById(R.id.title);
        articleabstract=(EditText) findViewById(R.id.articleabstract);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length()==0||articleabstract.getText().length()==0)
                    Toast.makeText(NewTalk.this,"请完善信息才可以发布" , Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result[][]=DataBaseHelper.Query("select count(*) from talk",1);
                        int count=Integer.parseInt(result[0][0]);
                        boolean flag= DataBaseHelper.Execute("insert into talk values('" + String.format("%05d",count)
                                +"','"+ username +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                +"','"+ title.getText() +"','"+ articleabstract.getText() +"')");

                        boolean flag1=DataBaseHelper.Execute("insert into talkhistory values('" + String.format("%05d",count)
                                +"','"+ username +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                +"','"+ articleabstract.getText() +"','"+ 1 +"')");
                        msg=new Message();msg.what=1;
                        if(flag&&flag1) msg.obj="发布成功";
                        else msg.obj="发布失败";
                        handler.sendMessage(msg);
                        finish();
                    }
                }).start();
            }
        });
    }
}
