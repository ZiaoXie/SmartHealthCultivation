package com.example.smarthealthcultivation.recommend;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.Bean.Tag;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

public class TagSelect extends AppCompatActivity {

    String username,titles[]={"已选标签","未选标签"};
    int count;
    Dialog dialog;
    ImageButton back;
    List<Tag>  tagList[];//顺序为:已订阅、未订阅
    List<Tag> tagselected,tagunselected;
    String tagtemp,tags[];
    TagGroup tagGroup1,tagGroup2;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(TagSelect.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    setTagGroup();
                    break;
                case 3:
                    RadioButton r=(RadioButton)msg.obj;
                    r.setChecked(!r.isChecked());
                    Toast.makeText(TagSelect.this,"网络通讯失败" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_select);

        username=getIntent().getStringExtra("username");
        tagGroup1=(TagGroup)findViewById(R.id.tag_group1);
        tagGroup2=(TagGroup)findViewById(R.id.tag_group2);

        new Thread(new Runnable() {
            @Override
            public void run() {//读取已有订阅情况
                tagselected=DataBaseHelper.Query(2,Tag.class,new String[]{"Tag.tagid","tagname"},
                        new String[]{"TagSelect","Tag"},"userid= '"+username+"' and Tag.tagid=TagSelect.tagid");
                if(DataBaseHelper.responseCode!=200){
                    msg=new Message();
                    msg.obj="记录读取错误";msg.what=1;
                    handler.sendMessage(msg);
                    return;
                }

                tagunselected=DataBaseHelper.Query(2,Tag.class,Tag.params,new String[]{"Tag"}," not exists( select * from TagSelect "+
                        " where userid= '"+ username + "'"+" and TagSelect.tagid=Tag.tagid)");
                if(DataBaseHelper.responseCode!=200){
                    msg=new Message();
                    msg.obj="记录读取错误";msg.what=1;
                    handler.sendMessage(msg);
                    return;
                }
                msg=new Message();msg.what=2;
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



    }

    private void setTagGroup(){
        tags = new String[tagselected.size()];
        for (int i = 0; i < tagselected.size(); i++)
            tags[i] = tagselected.get(i).getTagname();
        tagGroup1.setTags(tags); //设置标签集合
        tagGroup1.setOnTagClickListener(new TagGroup.OnTagClickListener() {//添加标签点击事件,在此处为取消订阅
            @Override
            public void onTagClick(String tag) {
                tagtemp = tag;
                new AlertDialog.Builder(TagSelect.this).
                        setMessage("是否取消订阅\n" + tag).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i;
                        for (i = 0; i < tagselected.size(); i++) {
                            if (tagtemp.equals(tagselected.get(i).getTagname()))
                                break;
                        }
                        DataBaseHelper.Execute("delete from TagSelect where tagid='" + tagselected.get(i).getTagid() + "' and userid='" + username + "'");
                        if (DataBaseHelper.responseCode != 200) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = "网络请求失败";
                            handler.sendMessage(msg);
                            return;
                        }
                        tagunselected.add(tagselected.get(i));
                        tagselected.remove(i);
                        setTagGroup();
                    }
                }).show();

            }
        });

        tags = new String[tagunselected.size()];
        for (int i = 0; i < tagunselected.size(); i++)
            tags[i] = tagunselected.get(i).getTagname();
        tagGroup2.setTags(tags);
        tagGroup2.setOnTagClickListener(new TagGroup.OnTagClickListener() {//添加标签点击事件,在此处为订阅
            @Override
            public void onTagClick(String tag) {
                tagtemp = new String(tag);
                dialog = new AlertDialog.Builder(TagSelect.this).
                        setMessage("是否订阅\n" + tag).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i;
                        for (i = 0; i < tagunselected.size(); i++) {
                            if (tagtemp.equals(tagunselected.get(i).getTagname()))
                                break;
                        }
                        DataBaseHelper.Execute("insert into TagSelect values('" + username + "','" + tagunselected.get(i).getTagid() + "')");
                        if (DataBaseHelper.responseCode != 200) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = "网络请求失败";
                            handler.sendMessage(msg);
                            return;
                        }
                        tagselected.add(tagunselected.get(i));
                        tagunselected.remove(i);
                        setTagGroup();
                    }
                }).show();
            }
        });
    }
}
