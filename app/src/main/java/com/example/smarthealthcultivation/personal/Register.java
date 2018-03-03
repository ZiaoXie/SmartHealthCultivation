package com.example.smarthealthcultivation.personal;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.smarthealthcultivation.Bean.Users;
import com.example.smarthealthcultivation.R;
import com.example.smarthealthcultivation.toolsClass.DataBaseHelper;

import java.util.List;

public class Register extends AppCompatActivity {

    Button register;
    ImageButton back;
    EditText username_input;
    EditText password_input,password_input2;
    List<Users> list;

    RelativeLayout relativeLayout;

    Message msg;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 1:
                    Toast.makeText(Register.this,(String)msg.obj , Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    username_input.setText(" ");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //实现点击界面其他位置使输入框失去焦点
        relativeLayout=(RelativeLayout)findViewById(R.id.activity_register);
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

        username_input=(EditText)findViewById(R.id.username_input);
        password_input=(EditText)findViewById(R.id.password_input);
        password_input2=(EditText)findViewById(R.id.password_input2);

        register=(Button)findViewById(R.id.register_Button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username_input.getText().length()==0) //用户名一栏为空
                    Toast.makeText(Register.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                else if(password_input.getText().length()==0) //密码栏为空
                    Toast.makeText(Register.this, "请输入密码", Toast.LENGTH_SHORT).show();
                else if(password_input2.getText().length()==0) //重复输入密码栏为空
                    Toast.makeText(Register.this, "请输入核对密码", Toast.LENGTH_SHORT).show();
                else if(!password_input.getText().toString().equals(password_input2.getText().toString())) //两次密码不一致
                    Toast.makeText(Register.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                else {
                    list= DataBaseHelper.Query(2,Users.class,Users.params,new String[]{Users.table},"userid='"+ username_input.getText().toString() +"'");
                    if (DataBaseHelper.responseCode!=200){
                        Message msg=new Message();
                        msg.what=1;msg.obj="网络请求失败";
                        handler.sendMessage(msg);
                        return;
                    }
                    if(list.size()>0){
                        Message msg=new Message();
                        msg.what=1;msg.obj="这个账号已存在";
                        handler.sendMessage(msg);
                        return;
                    }
                    boolean flag=DataBaseHelper.Execute("insert into users(userid,password) values('"+
                            username_input.getText().toString() +"','"+ password_input.getText().toString()+"')");

                    msg = new Message();
                    msg.what = 1;
                    msg.obj = "注册成功";
                    handler.sendMessage(msg);
                    finish();

                }
            }
        });
    }
}
