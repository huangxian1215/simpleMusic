package com.example.simplemusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplemusic.MusicListActivity;
import com.example.simplemusic.R;
import com.example.simplemusic.bean.dbMusicInfo;
import com.example.simplemusic.database.UserDBHelper;
import com.example.simplemusic.task.SearchLocalMusicTask;
import com.example.simplemusic.task.SearchLocalMusicTask.OnSearchLocalListener;

import java.util.ArrayList;

public class LocalMusicFragment extends Fragment implements OnSearchLocalListener{
    private static final String TAG = "LocalMusicFragment";
    protected View mView;
    protected Context mContext;
    //控件
    private TextView tv_refresh;
    private TextView localSongsNum;
    private TextView myLoveSongsNum;
    private TextView myDownSongsNum;
    private ListView mListMenu;
    private UserDBHelper mHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.fragment_local_music, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        tv_refresh =  getActivity().findViewById(R.id.refresh_local);
        tv_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        localSongsNum = getActivity().findViewById(R.id.local_songs);
        localSongsNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicListActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        myLoveSongsNum = getActivity().findViewById(R.id.mylove_songs);
        myLoveSongsNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });
        myDownSongsNum = getActivity().findViewById(R.id.down_songs);
        myDownSongsNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicListActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });
        mListMenu = (ListView) getActivity().findViewById(R.id.lv_musicMenuList);
        mHelper = UserDBHelper.getmHelper();
        onSearchLocalMusicInfo("");
    }

    public void refresh(){
        mcount = 0;
        SearchLocalMusicTask  searchLocalTask = new SearchLocalMusicTask();
        searchLocalTask.setOnSearchLocalListener(this);
        searchLocalTask.execute("searchLocalMusic");
    }

    @Override
    public void onSearchLocalMusicInfo(String info){
        //刷新显示
        String local = "本地歌曲(";
        local += String.valueOf( mHelper.queryAllNum(0)) + ")";
        localSongsNum.setText(local);
        local = "我喜欢的音乐(";
        local += String.valueOf( mHelper.queryAllNum(1)) + ")";
        myLoveSongsNum.setText(local);
        local = "下载的歌曲(";
        local += String.valueOf( mHelper.queryAllNum(2)) + ")";
        myDownSongsNum.setText(local);
        Toast.makeText(mContext, "搜索完毕", Toast.LENGTH_SHORT).show();
        tv_refresh.setText("刷新");
    }

    private int mcount = 0;
    @Override
    public void findOne(){
        String str = String.format("找到%s个",mcount);
        tv_refresh.setText(str);
    }

}
