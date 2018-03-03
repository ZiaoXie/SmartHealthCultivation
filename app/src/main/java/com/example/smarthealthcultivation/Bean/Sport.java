package com.example.smarthealthcultivation.Bean;

/**
 * Created by Administrator on 2017-09-12.
 */

public class Sport {
    String sportid;
    String sportname;
    String picturepath;

    public static String[] params={"sportid", "sportname" , "picturepath"};
    public static String table="sport";

    public String getSportid() {
        return sportid;
    }

    public void setSportid(String sportid) {
        this.sportid = sportid;
    }

    public String getSportname() {
        return sportname;
    }

    public void setSportname(String sportname) {
        this.sportname = sportname;
    }

    public String getPicturepath() {
        return picturepath;
    }

    public void setPicturepath(String picturepath) {
        this.picturepath = picturepath;
    }
}
