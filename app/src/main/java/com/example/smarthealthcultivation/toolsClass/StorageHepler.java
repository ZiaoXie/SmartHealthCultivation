package com.example.smarthealthcultivation.toolsClass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017-09-07.
 */

public class StorageHepler {
    public static boolean saveBitmap(Context context,String headpath,Bitmap mBitmap){
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        try {
            File file = new File(state + headpath);
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return  true;
    }

    public static Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try {
            File file = new File(Environment.getExternalStorageState()+pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }else {
                bitmap=DataBaseHelper.getBitmap(pathString);
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }
        return bitmap;
    }
}


