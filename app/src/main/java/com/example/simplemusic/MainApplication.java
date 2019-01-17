package com.example.simplemusic;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.simplemusic.bean.MusicInfo;
import com.example.simplemusic.bean.qqMusicInfo;

import java.util.ArrayList;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static MainApplication mApp;
    public MediaPlayer mMediaPlayer;
    public String mSong;
    public String mFilePath;
    //下载用
    public String vkey;
    public int downtype;
    public String qqsongid;

    public ArrayList<qqMusicInfo> mqqMusicInfo;
    //当前播放
    public MusicInfo mMusic;
    public int mMode = 1;

    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mMediaPlayer = new MediaPlayer();
    }

}
