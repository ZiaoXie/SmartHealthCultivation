package com.example.smarthealthcultivation.Bean;

/**
 * Created by Administrator on 2017-09-14.
 */

public class Questionaire {
    String tizhi,num,sex,flag,question;

    String params[]={"tizhi","num","sex","flag","question"};

    public String getTizhi() {
        return tizhi;
    }

    public void setTizhi(String tizhi) {
        this.tizhi = tizhi;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
