package com.example.smarthealthcultivation.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.HomePage;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.ZhongYiTiZhi.TestView;
import com.example.smarthealthcultivation.ZhongYiTiZhi.TiZhiResult;
import com.example.smarthealthcultivation.toolsClass.StorageHepler;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class Personal extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    String item[]={"体质测试","我的收藏","浏览记录","我的推送","我的通知"};
    String username;
    ListView listView;
    CircleImageView circleImageView;
    Context context;
    Bitmap bitmap;

    public Personal() {
        // Required empty public constructor
    }

    public static Personal newInstance(String param1, String param2) {
        Personal fragment = new Personal();
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

        username= ((HomePage)getActivity()).getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");


        view=inflater.inflate(R.layout.fragment_personal, container, false);
        listView=(ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new MyAdapter());

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        username= ((HomePage)getActivity()).getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        circleImageView=(CircleImageView) view.findViewById(R.id.head);
        if (username!=""){
            String headpath=((HomePage)getActivity()).getSharedPreferences("userinfo",MODE_PRIVATE).getString("headpath","");
            if(headpath!=""){
                bitmap=StorageHepler.getDiskBitmap(headpath);
            }
            if(bitmap!=null){
                circleImageView.setImageBitmap(bitmap);
            }

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context,PersonalCenter.class));
                }
            });
        }
        else{
            circleImageView.setImageResource(R.mipmap.defaulthead);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context,login.class));
                }
            });
        }
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
            return item.length;
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
            View view=null;
            Holder holder=null;
            if(convertView==null){
                view=View.inflate(context,R.layout.listview_personal,null);
                holder=new Holder();
                holder.iv=(ImageView) view.findViewById(R.id.pic);
                holder.tv=(TextView) view.findViewById(R.id.personalitem);
                view.setTag(holder);
            }
            else {
                view=convertView;
                holder=(Holder) view.getTag();
            }
            holder.iv.setBackgroundResource(R.mipmap.unselected);
            holder.tv.setText(item[position]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(username==""){
                        Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(position==4||position==5) return;
                    Intent intent=new Intent();
                    switch (position){
                        case 0:
                            intent.setClass(context,TiZhiResult.class);
                            intent.putExtra("username",username);
                            break;
                        case 1:
                            intent.setClass(context,UserLike.class);
                            intent.putExtra("username",username);
                            break;
                        case 2:
                            intent.setClass(context,History.class);
                            intent.putExtra("username",username);
                            break;
                        case 3:
                            return;
                        case 4:
                            return;
                    }
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    class Holder{
        ImageView iv;
        TextView tv;
    }

}
