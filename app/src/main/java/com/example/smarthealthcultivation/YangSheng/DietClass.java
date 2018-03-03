package com.example.smarthealthcultivation.YangSheng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DietClass extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String dietclass,result[][];
    Context context;
    View view;
    ListView listView;
    Bitmap bitmaps[];

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(context,(String)msg.obj , Toast.LENGTH_SHORT).show();
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

    public DietClass() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DietClass newInstance(String param1, String param2) {
        DietClass fragment = new DietClass();
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
        view=inflater.inflate(R.layout.fragment_diet_class, container, false);

        dietclass=getArguments().getString("dietclass");
        System.out.println(dietclass);
        if (dietclass==null){
            Toast.makeText(context,"系统错误",Toast.LENGTH_SHORT);
            return view;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                result= DataBaseHelper.Query("select dietid,dietname,picturepath from diet where diettag='"+dietclass+"'",3);
                if(result==null){
                    Message msg=new Message();
                    msg.what=1;msg.obj="菜品加载失败";
                    handler.sendMessage(msg);
                    return;
                }

                try {
                    bitmaps = new Bitmap[result.length];
                    for (int i = 0; i < result.length; i++) {
                        bitmaps[i]=DataBaseHelper.getBitmap(result[i][2]);
                        if (bitmaps[i] == null) {
                            Message msg=new Message();
                            msg.what=1;msg.obj="图片加载失败";
                            handler.sendMessage(msg);
                            return;
                        }
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

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return result.length;
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
                v= View.inflate(getActivity(),R.layout.listview_diet_item,null);
                holder=new Holder();
                holder.iv=(ImageView) v.findViewById(R.id.dietpic);
                holder.tv=(TextView)v.findViewById(R.id.dietname);
                v.setTag(holder);
            }
            else{
                v=convertView;
                holder=(Holder) v.getTag();
            }
            holder.tv.setText(result[position][1]);
            holder.iv.setImageBitmap(bitmaps[position]);
            return v;
        }
    }

    class Holder{
        ImageView iv;
        TextView tv;
    }

}
