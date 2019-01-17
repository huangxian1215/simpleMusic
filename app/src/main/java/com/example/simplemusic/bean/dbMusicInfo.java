package com.example.simplemusic.bean;

public class dbMusicInfo {
    public int _id = 0;
    public String msongid = "";
    public String mname = "";
    public String msinger = "";
    public String malbum = "";
    public String murl = "";
    public String mtime = "";
    public String mpackname = "";
    public int love = 0;

    public dbMusicInfo(qqMusicInfo song){
        mname = song.mMusicName;
        msongid = song.getMusicID();
        msinger = song.mMusicSinger;
        malbum = song.mMusicAlbum;
        mtime = song.mMusicTime;
    }

    public dbMusicInfo(String song, String url){
        mname = song;
        murl = url;
    }

    public void setUrl(String url){
        murl = url;
    }

    public void setPackname(String pknm){
        mpackname = pknm;
    }

    public void setDBId(int id){
        _id = id;
    }
}
