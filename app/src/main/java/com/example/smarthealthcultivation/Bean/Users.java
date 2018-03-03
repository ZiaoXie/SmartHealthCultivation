package com.example.smarthealthcultivation.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-09-12.
 */

public class Users implements Serializable{
    String userid;
    String password;
    String nickname;
    String headpath;
    String sex;
    String birthday;
    public static String params[]={"userid","password","nickname","headpath","sex","birthday"};
    public static String table="users";

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpath() {
        return headpath;
    }

    public void setHeadpath(String headpath) {
        this.headpath = headpath;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
