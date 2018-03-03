package com.example.smarthealthcultivation.toolsClass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 谢星宇 on 2017/6/11.
 */

public class DataBaseHelper {

    private final static String path="http://10.120.51.105:8080/SmartYangSheng";//Tomcat
//    private final static String path="http://10.120.51.105:8000"; //Django
//    private final static String path="http://192.168.1.103:8080/SmartYangSheng";//滨海家庭
//    private final static String path="http://192.168.100.8:8080/SmartYangSheng";//枫桥雅筑
    public static int responseCode;

    public static String[][] Query(String Sql,int n){
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(path+"/servlet/Query");//Tomcat
//            URL url = new URL(path+"/SmartYangSheng/Django/Query");//Django
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "SQL=" + URLEncoder.encode(Sql, "UTF-8")+"&n="+n;
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            responseCode=urlConnection.getResponseCode();
            if(responseCode==200){
                InputStream is=urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }

            String in=sb.toString();
            System.out.println(in);

            if(in.startsWith("\ufeff")) {
                in = in.substring(1);
            }

            JSONArray array=new JSONArray(in);

            if(array.length()==0) return null;

            String[][] result=new String[array.length()][];

            for(int t=0; t<array.length(); t++){
                result[t]=new String[n];
                JSONObject o = (JSONObject)array.get(t);
                for(int i=0;i<n;i++){
                    if(o.getString(String.valueOf(i+1))!=null) result[t][i]=o.getString(String.valueOf(i+1));
                }
            }

            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static List Query(int n,Class targetClass,String[] params,String[] tables,String tiaojian){

        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(path+"/servlet/QueryByBean");//Tomcat
//            URL url = new URL(path+"/SmartYangSheng/Django/QueryByBean");//Django


            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "n="+n;
            for(int i=0;i<tables.length;i++){
                data+="&table="+tables[i];
            }
            for(int i=0;i<params.length;i++){
                data+="&param="+params[i];
            }
            if(tables!=null&&tiaojian!="")
                data+=("&tiaojian="+tiaojian);
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            System.out.println(data);

            responseCode=urlConnection.getResponseCode();
            if(responseCode==200){
                InputStream is=urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }

            System.out.println(sb.toString());

            List list=new ArrayList();
            Gson gson=new Gson();
            JsonParser jsonParser=new JsonParser();
            JsonElement jsonElement=jsonParser.parse(sb.toString());

            //把JsonElement对象转换成JsonArray
            JsonArray jsonArray = null;
            if(jsonElement.isJsonArray()){
                jsonArray = jsonElement.getAsJsonArray();
            }

            Iterator it = jsonArray.iterator();
            while(it.hasNext()){
                JsonElement e = (JsonElement)it.next();
                //JsonElement转换为JavaBean对象
                list.add(gson.fromJson(e.toString(),targetClass));
            }

            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public static boolean Execute(String Sql){

        try {
//            URL url=new URL(path+"/servlet/Execute?SQL="+Sql);
//            URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);
//            url=new URL(uri.toString());
//            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            URL url=new URL(path+"/servlet/Execute");
//            URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);

//            URL url = new URL(path+"/SmartYangSheng/Django/Execute");//Django
            URL url = new URL(path+"/servlet/Execute");//Tomcat
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "SQL=" + URLEncoder.encode(Sql, "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            responseCode=urlConnection.getResponseCode();
            if(responseCode==200) return true;
            else return false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static boolean Insert(String Sql){
        try {
            URL url=new URL(path+"/Execute?SQL="+Sql);
            URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);
            url=new URL(uri.toString());
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            responseCode=conn.getResponseCode();
            if(responseCode==200){
                int i=conn.getInputStream().read();
                System.out.println(i);
                if(i==0) return true;
                else return false;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap getBitmap(String path){
        try {
            URL url = new URL(DataBaseHelper.path + path);//Tomcat
//            URL url = new URL(DataBaseHelper.path+ "/static" + path);//Django
            URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);
            url=new URL(uri.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            responseCode=conn.getResponseCode();
            if(responseCode==200){
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                System.out.println("得到图片"+path);
                return bitmap;
            }
            else{
                System.out.println("获取图片"+path+"失败");
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getTxt(String path){
        int line;
        try {
            URL url = new URL(DataBaseHelper.path + path);//Tomcat
//            URL url = new URL(DataBaseHelper.path+ "/static" + path);//Django
            URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);
            url=new URL(uri.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            byte b[]=new byte[1024];
            responseCode=conn.getResponseCode();
            if(responseCode==200){
                InputStream is=conn.getInputStream();
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                while ((line = is.read(b)) != -1) {
                    bos.write(b,0,line);
                }

                String s=new String(bos.toByteArray(),"gbk");
                System.out.println(s);
                return s;
            }
            else{
                return null;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String TimeTransfer(String result){
        String time;
        try {
            Calendar calendar=Calendar.getInstance(),now=Calendar.getInstance(),temp=Calendar.getInstance(),temp1=Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.substring(0,19)));
            temp.set(Calendar.MONTH, temp.get(Calendar.MONTH)+1);
            temp.set(Calendar.DAY_OF_MONTH, 1);
            temp.setTime(new java.util.Date(temp.getTime().getTime()-24*60*60*1000));

            temp1.set(Calendar.YEAR, temp.get(Calendar.YEAR)+1);
            temp1.set(Calendar.MONTH, 0);
            temp1.set(Calendar.DAY_OF_MONTH, 1);
            temp1.setTime(new java.util.Date(temp1.getTime().getTime()-24*60*60*1000));

            long cha=now.getTimeInMillis()-calendar.getTimeInMillis();
            cha/=1000;

            if(cha<60) time=result.substring(11,16);
            else if ((cha/=60)<60) time=(cha+"分钟前");
            else if((cha/=60)<24) time=(cha+"小时前");
            else if((cha/=24)<temp.get(Calendar.DAY_OF_MONTH)) time=(cha+"天前");
            else if(cha<temp1.get(Calendar.DAY_OF_YEAR))time=(cha/temp.get(Calendar.DAY_OF_MONTH)+"月前");
            else time=(now.get(Calendar.YEAR)-calendar.get(Calendar.YEAR) +"年前");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return time;
    }

}
