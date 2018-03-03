package com.example.smarthealthcultivation.ZhongYiTiZhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;
import com.example.smarthealthcultivation.toolsClass.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestView extends AppCompatActivity {



    String tizhiclass[]={"阳虚质","阴虚质","气虚质","痰湿质","湿热质","血瘀质","特凛质","气郁质","平和质"};
    String result[][][],username;
    int answer[][],sex;
    boolean flag[][];
    int radioid[]={0,R.id.radio1,R.id.radio2,R.id.radio3,R.id.radio4,R.id.radio5};
    ImageButton back;
    Button commit;
    ListView listView;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(TestView.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    listView=(ListView) findViewById(R.id.listview);
                    TizhiAdapter myAdapter=new TizhiAdapter();
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
        setContentView(R.layout.activity_test_view);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        sex=getIntent().getIntExtra("sex",2);


        result=new String[tizhiclass.length][][];
        answer=new int[tizhiclass.length][];
        flag=new boolean[tizhiclass.length][];

        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commit=(Button) findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<tizhiclass.length;i++){
                    for(int j=0;j<answer[i].length;j++){
                        if(answer[i][j]==0){
                            Toast.makeText(TestView.this,"问卷未填写完整",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int score[]=new int[tizhiclass.length];
                        for (int i=0;i<tizhiclass.length;i++){
                            score[i]=0;
                            for(int j=0;j<answer[i].length;j++){
                                score[i]+=answer[i][j];
                            }
                        }
                        boolean flag=DataBaseHelper.Execute("insert into testhistory values('"+ username +"','"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                                "','" + score[0] + "','" + score[1] + "','"+ score[2] +
                                "','" + score[3] + "','" + score[4] + "','"+ score[5] +
                                "','" + score[6] + "','" + score[7] + "','"+ score[8] +"')");

                        msg=new Message();msg.what=1;
                        if(flag) msg.obj="保存成功";
                        else msg.obj="保存失败";
                        handler.sendMessage(msg);
                        finish();
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<tizhiclass.length;i++){
                    result[i]= DataBaseHelper.Query("select * from questionnaire where tizhi='"+ tizhiclass[i] +"' and sex!="+ sex ,5);
                    answer[i]=new int[result[i].length];
                    flag[i]=new boolean[result[i].length];
                    if(result[i]==null){
                        Message msg=new Message();
                        msg.what=1;msg.obj="网络请求失败";
                        handler.sendMessage(msg);
                        return;
                    }
                }
                Message msg=new Message();
                msg.what=2;
                handler.sendMessage(msg);
            }
        }).start();

    }

    class TizhiAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tizhiclass.length;
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
            final TizhiHolder holder;
            if(convertView==null){
                v= View.inflate(TestView.this,R.layout.listview_tizhi_class,null);
                holder=new TizhiHolder();
                holder.tv=(TextView) v.findViewById(R.id.tizhiclass);
                holder.listView=(ListView) v.findViewById(R.id.listview);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(TizhiHolder) v.getTag();
            }

            holder.tv.setText((position+1)+"."+tizhiclass[position]);
            holder.listView.setAdapter(new QuestionAdapter(position));
            Utility.setListViewHeightBasedOnChildren(holder.listView);
            return v;
        }
    }

    class TizhiHolder{
        TextView tv;
        ListView listView;
    }

    class QuestionAdapter extends BaseAdapter{

        int n;
        QuestionAdapter(int n){
            super();
            this.n=n;
        }
        @Override
        public int getCount() {
            System.out.println(result[n].length);
            return result[n].length;
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
            final QuestionHolder holder;
            if(convertView==null){
                v= View.inflate(TestView.this,R.layout.listview_question,null);
                holder=new QuestionHolder();
                holder.tv=(TextView) v.findViewById(R.id.question);
                holder.rg=(RadioGroup) v.findViewById(R.id.radioGroup);
                if(flag[n][position]) holder.rg.check(radioid[answer[n][position]]);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(QuestionHolder) v.getTag();
            }

            holder.tv.setText((position+1)+"."+result[n][position][4]);
            holder.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.radio1:
                            if(result[n][position][3].equals("1")) answer[n][position]=1;
                            else answer[n][position]=5;
                            break;
                        case R.id.radio2:
                            if(result[n][position][3].equals("1")) answer[n][position]=2;
                            else answer[n][position]=4;
                            break;
                        case R.id.radio3:
                            if(result[n][position][3].equals("1")) answer[n][position]=3;
                            else answer[n][position]=3;
                            break;
                        case R.id.radio4:
                            if(result[n][position][3].equals("1")) answer[n][position]=4;
                            else answer[n][position]=2;
                            break;
                        case R.id.radio5:
                            if(result[n][position][3].equals("1")) answer[n][position]=5;
                            else answer[n][position]=1;
                            break;
                    }
                    flag[n][position]=true;
                }
            });
            return v;
        }
    }

    class QuestionHolder{
        TextView tv;
        RadioGroup rg;
    }
}
