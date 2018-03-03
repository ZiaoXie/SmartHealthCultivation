package com.example.smarthealthcultivation.YangSheng;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.Bean.Sport;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SportView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;
    View view;
    RecyclerView recyclerView;
    Bitmap bitmaps[];
    List<Sport> list;
    MyAdapter adapter;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(context,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    adapter=new MyAdapter();

                    //设置布局管理器
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    //设置adapter
                    recyclerView.setAdapter(adapter);
                    //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
                    recyclerView.setHasFixedSize(true);

                    //设置Item增加、移除动画
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //添加分割线
                    recyclerView.addItemDecoration(new DividerItemDecoration(
                            getContext(), LinearLayoutManager.VERTICAL));
                    break;
            }
        }
    };

    public SportView() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SportView newInstance(String param1, String param2) {
        SportView fragment = new SportView();
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
        view=inflater.inflate(R.layout.fragment_sport_view, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
//        //设置布局管理器
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        //设置adapter
//        recyclerView.setAdapter(new MyAdapter());
        new Thread(new Runnable() {
            @Override
            public void run() {
                list=DataBaseHelper.Query(3, Sport.class, Sport.params,new String[]{Sport.table},null);
                //result= DataBaseHelper.Query("select sportid,sportname,picturepath from sport",3);
                if (DataBaseHelper.responseCode!=200){
                    Message msg=new Message();
                    msg.what=1;msg.obj="网络请求失败";
                    handler.sendMessage(msg);
                    return;
                }
                if(list.size()==0||list==null){
                    return;
                }
                System.out.println("getlist"+list.size());
                try {
                    bitmaps = new Bitmap[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        bitmaps[i]=DataBaseHelper.getBitmap(((Sport)list.get(i)).getPicturepath());
                        if (bitmaps[i] == null) {
                            Message msg=new Message();
                            msg.what=1;msg.obj="获取图片失败";
                            handler.sendMessage(msg);
                            return;
                        }
                        System.out.println("getlist"+i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                Message msg=new Message();
                msg.what=2;
                handler.sendMessage(msg);
            }
        }).start();

        return view;
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

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    class MyAdapter extends RecyclerView.Adapter {

        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder=new Holder(LayoutInflater.from(context).inflate(R.layout.listview_sport_item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Holder)holder).iv.setImageBitmap(bitmaps[position]);
            ((Holder)holder).tv.setText(((Sport)list.get(position)).getSportname());
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;

        public Holder(View v) {
            super(v);
            iv=(ImageView) v.findViewById(R.id.sportpic);
            tv=(TextView)v.findViewById(R.id.sportname);
        }
    }

}
