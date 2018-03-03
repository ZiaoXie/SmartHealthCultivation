package com.example.smarthealthcultivation.personal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.HomePage;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PersonalCenter extends ActionBarActivity {
    String username;
    private String[] data={"昵称","性别","生日"},shuxing={"nickname","sex","birthday"};
    String information[]=new String[data.length],result[][];
    Intent intent;
    Holder holder;
    MyAdapter myAdapter;
    ListView listView;
    EditText editText[]=new EditText[data.length];
    Button logout;
    int cunzhu;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(PersonalCenter.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_personal_center);

        username=getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");

        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(PersonalCenter.this);
                builder.setTitle("是否退出当前账号？").setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor=getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        finish();
                    }
                }).show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                result= DataBaseHelper.Query("select nickname,sex,birthday,userid from users where userid='"+ username +"'",4);
                if (DataBaseHelper.responseCode!=200){
                    Message msg=new Message();
                    msg.what=1;msg.obj="网络请求失败";
                    handler.sendMessage(msg);
                    return;
                }
                if(result==null){
                    return;
                }

                for(int i=0;i<data.length;i++){
                    if (result[0][i]!=null&&!result[0][i].equals("null"))
                        information[i]=result[0][i];
                    else information[i]=null;
                }
                msg=new Message();
                msg.what=2;
                handler.sendMessage(msg);
            }
        }).start();

        ImageButton back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalCenter.this.finish();
            }
        });

        Button save=(Button) findViewById(R.id.edit);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenter.this);
                builder.setTitle("是否保存?").setNegativeButton(
                        "取消", null);
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        information[0]=editText[0].getText().toString();
                                        StringBuffer sb=new StringBuffer("update users set ");
                                        int i;
                                        for(i=0;i<data.length;i++){
                                            if(!TextUtils.isEmpty(editText[i].getText())){
                                                sb.append(shuxing[i]+"='"+ editText[i].getText().toString() +"'");
                                                break;
                                            }
                                        }
                                        for(i=i+1;i<data.length;i++){
                                            if(!TextUtils.isEmpty(editText[i].getText())){
                                                sb.append(" , "+shuxing[i]+"='"+ editText[i].getText().toString() +"'");
                                            }
                                        }
                                        sb.append(" where userid='"+ username +"'");
                                        boolean flag=DataBaseHelper.Execute(sb.toString());
                                        System.out.println(sb.toString());
                                        msg=new Message();msg.what=1;
                                        if(flag) msg.obj="修改成功";
                                        else msg.obj="修改失败";
                                        handler.sendMessage(msg);
                                    }
                                }).start();

                            }
                        });
                builder.show();

            }
        });

    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            View v=null;
            final Holder holder;
            if(view==null){
                v= View.inflate(PersonalCenter.this,R.layout.listview_personinfo,null);
                holder=new Holder();
                holder.tv = (TextView) v.findViewById(R.id.item_textview);
                holder.et=(TextView)v.findViewById(R.id.personalInformation);
                v.setTag(holder);
            }
            else{
                v=view;
                holder=(Holder) v.getTag();
            }
            holder.tv.setText(data[position]);
            holder.et.setText(information[position]);

            if(position!=0) holder.et.setFocusable(false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int p = position;
                    //Holder holder=(Holder) view.getTag(p);
                    switch (p) {
                        case 0:
                            EditText editText=new EditText(PersonalCenter.this);
//                            AlertDialog.Builder builder=new AlertDialog.Builder(PersonalCenter.this).setTitle("请输入昵称");
                        case 1:
                            final AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenter.this);
                            builder.setTitle("请选择性别");
                            final String[] sex = {"男", "女"};
                            builder.setItems(sex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    information[p]=sex[i];
                                    myAdapter.notifyDataSetChanged();
                                }
                            });
                            builder.show();
                            break;
                        case 2:
                            Calendar c = Calendar.getInstance();
                            DatePickerDialog dpd=new DatePickerDialog(PersonalCenter.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    information[p]= String.format( "%04d-%02d-%02d",year,month+1,dayOfMonth );
                                    myAdapter.notifyDataSetChanged();
                                }
                            },c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                            dpd.show();
                            break;
                    }
                }
            });

            return v;
        }
    }

    class Holder{
        public TextView tv;
        public TextView et;
    }

}
