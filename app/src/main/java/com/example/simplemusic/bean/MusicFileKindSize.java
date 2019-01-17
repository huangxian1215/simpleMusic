package com.example.simplemusic.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class MusicFileKindSize {
    ////M500 M800 A000
    public int msize_128 = 0;
    public int msize_320 = 0;
    public int msize_flac = 0;

    public MusicFileKindSize(){
    }

    public MusicFileKindSize(JSONObject song){
        try{
            msize_128 = Integer.parseInt(song.getString("size_128"));
            msize_320 = Integer.parseInt(song.getString("size_320"));
            msize_flac = Integer.parseInt(song.getString("size_flac"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
