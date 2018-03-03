package com.example.smarthealthcultivation.recommend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.example.smarthealthcultivation.Bean.Article;
import com.example.smarthealthcultivation.Bean.Tag;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

import static android.content.Context.MODE_PRIVATE;


@SuppressLint("NewApi")
public class Recommend extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    String  username,YinLiYear,YinLiDate,YinliYi,YinLiJi;
    TextView day, year_month,yinli_year,yinli_date,yi,ji;

    Bitmap[] bitmaps;
    View view;
    Context context;
    List<Article> articleList;
    List tagList[];
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ConvenientBanner convenientBanner;
    Integer integer[]=new Integer[]{R.mipmap.lishizheng,R.mipmap.shanggutianzhenlun,R.mipmap.door,R.mipmap.bamboo};
    int count;



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
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent=new Intent(context,RecommendView.class);
                            intent.putExtra("articleid",articleList.get(count).getArticleid());
                            intent.putExtra("username",username);
                            startActivity(intent);
                        }
                        @Override
                        public void onItemLongClick(View view, int position) { }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayout.VERTICAL));

                    break;
                case 3:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 4:
                    yinli_date.setText(YinLiDate);
                    yinli_year.setText(YinLiYear);
                    yi.setText(YinliYi);
                    ji.setText(YinLiJi);
                    break;
            }
        }
    };

    public Recommend() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Recommend newInstance(String param1, String param2) {
        Recommend fragment = new Recommend();
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

        username=getActivity().getSharedPreferences("userinfo",MODE_PRIVATE).getString("username","");
        view=inflater.inflate(R.layout.fragment_recommend, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        SharedPreferences shconfig = getActivity().getSharedPreferences("ConFigShare",MODE_PRIVATE);

        /*加载黄历模块*/
        LinearLayout huangli_layout=(LinearLayout)view.findViewById(R.id.huangli);
        huangli_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,HuangLi.class));
            }
        });
        day = (TextView) view.findViewById(R.id.day);
        year_month = (TextView) view.findViewById(R.id.year_month);
        yinli_year = (TextView) view.findViewById(R.id.yinli_year);
        yinli_date = (TextView) view.findViewById(R.id.yinli_date);
        yi = (TextView) view.findViewById(R.id.huangli_yi);
        ji = (TextView) view.findViewById(R.id.huangli_ji);

        day.setText(String.valueOf(Calendar.DAY_OF_MONTH));
        year_month.setText(new SimpleDateFormat("yyyy/MM").format(new Date()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://v.juhe.cn/laohuangli/d");

                    // 根据URL对象打开链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    // 设置请求的方式
                    urlConnection.setRequestMethod("POST");
                    // 设置请求的超时时间
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setConnectTimeout(5000);

                    // 设置请求的头
                    urlConnection.setRequestProperty("Connection", "keep-alive");
                    // 设置请求的头
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    // 设置请求的头
                    urlConnection.setRequestProperty("Content-Length","date="+ new SimpleDateFormat("yyyy-MM-dd").
                        format(new Date()) +"&key=52f889eaf7fc5343434e1d33490fdf6d");
                    // 设置请求的头
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

                    urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
                    urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
                    //setDoInput的默认值就是true
                    //获取输出流
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(("date="+ new SimpleDateFormat("yyyy-MM-dd").
                            format(new Date()) +"&key=52f889eaf7fc5343434e1d33490fdf6d").getBytes());
                    os.flush();

                    StringBuilder sb=new StringBuilder();
                    String line;
                    int responseCode=urlConnection.getResponseCode();
                    if(responseCode!=200){
                        Message msg=new Message();
                        msg.what=1;msg.obj="获取黄历失败";
                        handler.sendMessage(msg);
                        return;
                    }


                    InputStream is=urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    System.out.println(sb.toString());


                    JSONObject in=new JSONObject(sb.toString());
                    JSONObject result=in.getJSONObject("result");
                    YinliYi=result.getString("yi").split(" ")[0];
                    YinLiJi=result.getString("ji").split(" ")[0];
                    String Yinli=result.getString("yinli");
                    YinLiYear=Yinli.substring(0,6).replace("(","").replace(")","");
                    YinLiDate=Yinli.substring(6);

                    Message msg=new Message();
                    msg.what=4;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getArticle();
            }
        }).start();

        /*加载滚动条模块*/
        convenientBanner=(ConvenientBanner)view.findViewById(R.id.banner);
        convenientBanner.startTurning(4000);
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, Arrays.asList(integer))
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        /*加载下拉刷新模块*/
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getArticle();
                        handler.sendEmptyMessage(3);
                    }
                }).start();

            }
        });

        fab=(FloatingActionButton) view.findViewById(R.id.edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username==""){
                    Toast.makeText(context,"请先登录" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(context,TagSelect.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public void getArticle(){
        if(username==""){
            System.out.println("准备发送请求");
            articleList= DataBaseHelper.Query(4,Article.class,Article.params,new String[]{"Article"},null);
            if (DataBaseHelper.responseCode!=200){
                Message msg=new Message();
                msg.what=1;msg.obj="网络请求失败";
                handler.sendMessage(msg);
                return;
            }
            if(articleList==null||articleList.size()==0)
                return;
            tagList=new List[articleList.size()];
            for (int i=0;i<articleList.size();i++){
                tagList[i]=DataBaseHelper.Query(1,Tag.class,new String[]{"tagname"},new String[]{"Tag","articleTag"},
                        " articleid='" + articleList.get(i).getArticleid() + "' and articletag.tagid=tag.tagid");
            }
        }
        else {
            articleList= DataBaseHelper.Query(4,Article.class,Article.params,new String[]{"Article"},
                    "articleid in(select articleid from  article_view where userid='"+ username +"')");
            if (DataBaseHelper.responseCode!=200){
                Message msg=new Message();
                msg.what=1;msg.obj="网络请求失败";
                handler.sendMessage(msg);
                return;
            }
            if(articleList==null||articleList.size()==0)
                return;
            tagList=new List[articleList.size()];
            for (int i=0;i<articleList.size();i++){
                tagList[i]=DataBaseHelper.Query(1,Tag.class,new String[]{"tagname"},new String[]{"article_view"},
                        "userid='"+ username +"' and articleid='"+ articleList.get(i).getArticleid() +"'");
            }
        }

        bitmaps = new Bitmap[articleList.size()];
        for (int i = 0; i < articleList.size(); i++) {
            bitmaps[i]=DataBaseHelper.getBitmap(articleList.get(i).getPicturepath());
            if (bitmaps[i] == null) {
                Message msg=new Message();
                msg.what=1;msg.obj="获取图片失败";
                handler.sendMessage(msg);
                return;
            }
        }

        msg=new Message();
        msg.what=2;
        handler.sendMessage(msg);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    class MyAdapter extends RecyclerView.Adapter{

        OnItemClickListener onItemClickListener=null;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder=new Holder(LayoutInflater.from(getContext()).inflate(R.layout.listview_article_item,parent,false));
            ((Holder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,recyclerView.getChildAdapterPosition(v));
                }
            });
            ((Holder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(v,recyclerView.getChildAdapterPosition(v));
                    return false;
                }
            });
            return holder;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this.onItemClickListener=onItemClickListener;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((Holder)holder).title.setText(articleList.get(position).getTitle());
            ((Holder)holder).abst.setText(articleList.get(position).getAbstract());
            ((Holder)holder).iv.setImageBitmap(bitmaps[position]);


            String tags[] = new String[tagList[position].size()];
            for (int i = 0; i < tagList[position].size(); i++)
                tags[i] = ((Tag) tagList[position].get(i)).getTagname();
            ((Holder) holder).tagGroup.setTags(tags);
            ((Holder) holder).tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                @Override
                public void onTagClick(String tag) {
                    Intent intent=new Intent(getContext(),TagView.class);
                    intent.putExtra("tagname",tag);
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return articleList.size();
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView title,abst;
        TagGroup tagGroup;

        public Holder(View itemView) {
            super(itemView);
            iv=(ImageView) itemView.findViewById(R.id.articlepic);
            abst=(TextView)itemView.findViewById(R.id.articleabstract);
            title=(TextView)itemView.findViewById(R.id.title);
            tagGroup=(TagGroup) itemView.findViewById(R.id.tag_group);
        }
    }

    public class NetworkImageHolderView implements com.bigkoo.convenientbanner.holder.Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

}
