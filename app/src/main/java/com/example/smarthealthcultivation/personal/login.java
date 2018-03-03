package com.example.smarthealthcultivation.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthealthcultivation.HomePage;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.io.File;
import java.io.FileOutputStream;

public class login extends Activity {
	Intent intent;
	String [][]result;
	int k;
	Button register,login;
    ImageButton back;
	EditText username_input;
	EditText password_input;

	RelativeLayout relativeLayout;

	Message msg;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg){
			switch (msg.what){
				case 1:
					Toast.makeText(login.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
					break;
				case 2:
					username_input.setText(String.valueOf(k));
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        back=(ImageButton) findViewById(R.id.back); //返回键
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

		//实现点击界面其他位置使输入框失去焦点
		relativeLayout=(RelativeLayout)findViewById(R.id.activity_login);
		relativeLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				relativeLayout.setFocusable(true);
				relativeLayout.setFocusableInTouchMode(true);
				relativeLayout.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if(imm != null) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
				}
				return false;
			}
		});

		register=(Button) findViewById(R.id.register_Button); //打开注册界面
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,Register.class));
            }
        });

		username_input=(EditText)findViewById(R.id.username_input);
		password_input=(EditText)findViewById(R.id.password_input);

        login=(Button) findViewById(R.id.login_Button);
		intent=new Intent(login.this,HomePage.class);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(username_input.getText().length()!=0&&password_input.getText().length()!=0){
					new Thread(new Runnable() {
						@Override
						public void run() {
							result=DataBaseHelper.Query("select userid,password,headpath from users where userid = '" + username_input.getText().toString()+ "'",3);
							msg=new Message();
							msg.what=1;
							if(result==null)
							{
								msg.obj="用户名不存在";
								handler.sendMessage(msg);
							}
							else {
								String a=result[0][1];
								String b=password_input.getText().toString();
								if(a.equals(b))
								{
									//Toast.makeText(login.this, "密码正确", Toast.LENGTH_SHORT).show();
									//在本地保存用户信息
                                    SharedPreferences.Editor editor=getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                                    editor.putString("username",username_input.getText().toString());
                                    editor.putString("headpath",result[0][2]);
                                    editor.commit();

									/*将用户头像保存在本地*/
                                    Bitmap mBitmap=DataBaseHelper.getBitmap(result[0][2]);
                                    try {
                                        //获取内部存储状态
                                        String state = Environment.getExternalStorageState();
                                        //如果状态不是mounted，无法读写
                                        if (!state.equals(Environment.MEDIA_MOUNTED)) {
                                            throw new Exception();
                                        } else {
                                            File file=new File(Environment.getExternalStorageDirectory()+result[0][2]);
                                            FileOutputStream out = new FileOutputStream(file);
                                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                            out.flush();
                                            out.close();
                                            //保存图片后发送广播通知更新数据库
                                            Uri uri = Uri.fromFile(file);
                                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
									finish();
								}
								else {
									msg.obj="密码错误";
									handler.sendMessage(msg);
								}
							}
						}
					}).start();

				}
				else if(username_input.getText().length()==0) //用户名一栏为空
					Toast.makeText(login.this, "请输入用户名", Toast.LENGTH_SHORT).show();
				else if(password_input.getText().length()==0) //密码栏为空
					Toast.makeText(login.this, "请输入密码", Toast.LENGTH_SHORT).show();
			}

		});

	}
}