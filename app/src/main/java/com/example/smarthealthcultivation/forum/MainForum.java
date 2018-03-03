package com.example.smarthealthcultivation.forum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.HomePage;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MainForum extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String result[][],username;
    View view;
    int count;
    ListView listView;
    FloatingActionButton fab;
    Context context;
    Bitmap bitmaps[];

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(context,"网络请求失败" , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    listView=(ListView) view.findViewById(R.id.listview);
                    MyAdapter myAdapter=new MyAdapter();
                    listView.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(myAdapter);
                    break;
            }
        }
    };

    public MainForum() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainForum newInstance(String param1, String param2) {
        MainForum fragment = new MainForum();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_main_forum, container, false);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        SharedPreferences shconfig = getActivity().getSharedPreferences("ConFigShare",
                MODE_PRIVATE);

        username=getActivity().getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        fab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username==""){
                    Toast.makeText(context,"请先登录" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(context,NewTalk.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result= DataBaseHelper.Query("select * from main_talk",6);
                if (DataBaseHelper.responseCode!=200){
                    Message msg=new Message();
                    msg.what=1;msg.obj="网络请求失败";
                    handler.sendMessage(msg);
                    return;
                }
                if(result==null)
                    return;
                else count=result.length;

                bitmaps=new Bitmap[result.length];
                for (int i = 0; i < result.length; i++) {
                    bitmaps[i]=DataBaseHelper.getBitmap(result[i][5]);
                    if (bitmaps[i] == null) {
                        Message msg=new Message();
                        msg.what=1;msg.obj="网络请求失败";
                        handler.sendMessage(msg);
                        return;
                    }
                }

                handler.sendEmptyMessage(2);
            }
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    class MyAdapter extends BaseAdapter{

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v=null;
            final Holder holder;
            if(convertView==null){
                v= View.inflate(context,R.layout.listview_item_forum,null);
                holder=new Holder();
                holder.civ=(CircleImageView) v.findViewById(R.id.head);
                holder.username=(TextView) v.findViewById(R.id.username);
                holder.time=(TextView) v.findViewById(R.id.time);
                holder.talkabstract=(TextView)v.findViewById(R.id.talkabstract);
                holder.title=(TextView)v.findViewById(R.id.title);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(Holder) v.getTag();
            }

            holder.civ.setImageBitmap(bitmaps[position]);
            holder.username.setText(result[position][1]);
            holder.time.setText(DataBaseHelper.TimeTransfer(result[position][2]));
            holder.title.setText(result[position][3]);
            holder.talkabstract.setText(result[position][4]);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,ForumView.class);
                    intent.putExtra("talkid",result[position][0]);
                    intent.putExtra("title",result[position][3]);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
            });

            return v;
        }
    }

    class Holder{
        TextView username,time,title,talkabstract;
        CircleImageView civ;
    }

}
