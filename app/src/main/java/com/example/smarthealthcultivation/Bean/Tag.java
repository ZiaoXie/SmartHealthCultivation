package com.example.smarthealthcultivation.Bean;

/**
 * Created by Administrator on 2017-09-14.
 */

public class Tag {
    String tagid;
    String tagname;

    public static String params[]={"tagid","tagname"};

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }
}
