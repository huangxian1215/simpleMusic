package com.example.simplemusic.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class qqMusicInfo {
    private JSONObject mMusic = null;
    public int mIndex = -1;
    public String mMusicName = "";
    public String mMusicSinger = "";
    public String mMusicAlbum = "";
    public String mMusicTime = "";
    public String mMusicUrl = "";
    public qqMusicInfo(JSONObject song){
        mMusic = song;
        mMusicName = getMusicName();
        mMusicSinger = getSingerName();
        mMusicAlbum = getAlbumName();
        mMusicTime = getMusicTime();
    }

    public qqMusicInfo(){}

    //音乐名
    public String getMusicName(){
        String name = "";
        try {
            name = mMusic.getString("title");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //音乐时长
    public String getMusicTime(){
        String name = "";
        try {
            name = mMusic.getString("interval");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //歌手
    public String getSingerName(){
        String name = "";
        try {
            JSONArray singers = mMusic.getJSONArray("singer");
            for(int i = 0; i < singers.length(); i++){
                if(i > 0){
                    name+="/";
                }
                name +=singers.getJSONObject(i).getString("name");
            }
//            JSONObject singer = singers.getJSONObject(0);
//            name = singer.getString("name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //专辑
    public String getAlbumName(){
        String name = "";
        try {
            JSONObject album = mMusic.getJSONObject("album");
            name = album.getString("title");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //图片ID
    public String getMusicPicID(){
        String name = "";
        try {
            JSONObject album = mMusic.getJSONObject("album");
            name = album.getString("mid");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }

    //歌词ID
    public String getMusicLrcID(){
        String name = "";
        try {
            name = mMusic.getString("mid");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //歌曲ID
    public String getMusicID(){
        String name = "";
        try {
            name = mMusic.getString("id");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //音乐文件ID
    public String getMusicFileID(){
        String name = "";
        try {
            JSONObject file = mMusic.getJSONObject("file");
            name = file.getString("strMediaMid");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
    //支持下载的文件类型
    public MusicFileKindSize getMusicFileSize(){
        MusicFileKindSize name = new MusicFileKindSize();
        try {
            JSONObject files = mMusic.getJSONObject("file");
            name = new MusicFileKindSize(files);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return name;
    }
}

