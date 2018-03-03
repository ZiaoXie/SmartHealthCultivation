package com.example.smarthealthcultivation.Bean;

/**
 * Created by Administrator on 2017-09-14.
 */

public class Article {
    String articleid;
    String title;
    String picturepath;
    String Abstract;
    String content;
    public static String params[]={"articleid","title" , "picturepath" , "Abstract" , "content"};

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicturepath() {
        return picturepath;
    }

    public void setPicturepath(String picturepath) {
        this.picturepath = picturepath;
    }

    public String getAbstract() {
        return Abstract;
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
