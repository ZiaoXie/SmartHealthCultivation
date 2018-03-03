package com.example.smarthealthcultivation.ZhongYiTiZhi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

public class TiZhiResult extends AppCompatActivity {

    String tizhiclass[]={"阳虚质","阴虚质","气虚质","痰湿质","湿热质","血瘀质","特凛质","气郁质","平和质"};
    String result[][],username;
    int score[]=new int[9],number[]={7,8,8,8,6,7,7,7,8};
    double finalresult[]=new double[9];
    Button back,test;
    TextView testresult;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(TiZhiResult.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    testresult.setText((String)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_zhi_result);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");

        testresult=(TextView) findViewById(R.id.result);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result= DataBaseHelper.Query("select * from testhistory where userid='"+ username +"' order by datetime desc",11);
                if(DataBaseHelper.responseCode!=200){
                    msg=new Message();
                    msg.what=1;msg.obj="网络连接失败";
                    handler.sendMessage(msg);
                    return;
                }
                if(result==null){
                    msg=new Message();
                    msg.what=2;msg.obj="您还没做过体质测试";
                    handler.sendMessage(msg);
                    return;
                }
                for(int i=0;i<9;i++){
                    score[i]=Integer.parseInt(result[0][i+2]);
                    finalresult[i]=((double)(score[i]-number[i])/(double) (number[i]*4))*100;
                }

                for(int i=0;i<9;i++) System.out.println(finalresult[i]+" ");
                StringBuffer yourresult=new StringBuffer("您的体质测试结果为\n");
                if(finalresult[8]>=60){
                    yourresult.append("平和质\n兼有");
                }
                for(int i=0;i<8;i++){
                    if(finalresult[i]>=40) yourresult.append(tizhiclass[i]+"\n");
                }

                boolean flag=false;
                for(int j=0;j<8;j++){
                    if(finalresult[j]>=30&&finalresult[j]<40){
                        flag=true;break;
                    }
                }
                if (flag){
                    yourresult.append("有以下体质倾向\n");
                    for(int i=0;i<8;i++){
                        if(finalresult[i]>=30&&finalresult[i]<40) yourresult.append(tizhiclass[i]+"\n");
                    }
                }

                msg=new Message();
                msg.what=2;msg.obj=yourresult.toString();
                handler.sendMessage(msg);
            }
        }).start();

        back=(Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        test=(Button) findViewById(R.id.retest);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TiZhiResult.this);
                builder.setTitle("请选择性别");
                final String[] sex = {"男", "女"};
                builder.setItems(sex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(TiZhiResult.this,TestView.class);
                        intent.putExtra("username",username);
                        intent.putExtra("sex",i+1);
                        startActivity(intent);
                    }
                });
                builder.show();

            }
        });

    }
}
